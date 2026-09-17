package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.MessageAttachmentDao;
import com.example.demo.dao.MessageDao;
import com.example.demo.dao.MessageReplyDao;

import com.example.demo.dto.MessageAttachmentResponse;
import com.example.demo.dto.MessageReplyResponse;
import com.example.demo.dto.MessageResponse;

import com.example.demo.entity.Message;
import com.example.demo.entity.MessageAttachment;
import com.example.demo.entity.MessageReply;
import com.example.demo.entity.User;

import com.example.demo.repository.UserRepository;


@Service
public class MessageServiceImpl implements MessageService {


    // =====================================================
    // 管理員資料
    // =====================================================

    private static final Integer ADMIN_ID = 1;

    private static final String ADMIN_EMAIL =
            "admin@example.com";

    private static final String ADMIN_NAME =
            "管理員";


    // =====================================================
    // 留言後端分頁
    // =====================================================

    private static final int MESSAGE_PAGE_SIZE =
            5;


    // =====================================================
    // 單張圖片最大 10 MB
    // =====================================================

    private static final long MAX_FILE_SIZE =
            10L * 1024 * 1024;


    // =====================================================
    // 一則留言最多 5 張圖片
    // =====================================================

    private static final int MAX_FILE_COUNT =
            5;


    // =====================================================
    // DAO / Repository
    // =====================================================

    private final MessageDao messageDao;

    private final MessageAttachmentDao
            messageAttachmentDao;

    private final MessageReplyDao
            messageReplyDao;

    private final UserRepository
            userRepository;


    // =====================================================
    // Constructor Injection
    // =====================================================

    public MessageServiceImpl(

            MessageDao messageDao,

            MessageAttachmentDao messageAttachmentDao,

            MessageReplyDao messageReplyDao,

            UserRepository userRepository

    ) {

        this.messageDao =
                messageDao;

        this.messageAttachmentDao =
                messageAttachmentDao;

        this.messageReplyDao =
                messageReplyDao;

        this.userRepository =
                userRepository;
    }


    // =====================================================
    // 查詢全部留言
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getAllMessages() {


        return messageDao
                .findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =====================================================
    // 後端分頁
    //
    // page = 0 → 第一頁
    // page = 1 → 第二頁
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessagePage(
            int page
    ) {


        if (page < 0) {

            throw new IllegalArgumentException(
                    "page 不可小於 0"
            );
        }


        return messageDao
                .findPage(
                        page,
                        MESSAGE_PAGE_SIZE
                )
                .map(this::convertToResponse);
    }


    // =====================================================
    // 新增主留言 + 圖片
    //
    // loginEmail：
    // 由 Controller 從 JWT Principal 取得
    // =====================================================

    @Override
    @Transactional(
            rollbackFor = Exception.class
    )
    public MessageResponse saveMessageWithFiles(

            String loginEmail,

            String content,

            List<MultipartFile> files

    ) throws IOException {


        // =================================================
        // 1. 根據 JWT Email 找真正 User
        // =================================================

        User currentUser =
                findLoginUser(
                        loginEmail
                );


        // =================================================
        // 2. 驗證留言內容
        // =================================================

        validateContent(
                content
        );


        // =================================================
        // 3. 整理有效附件
        // =================================================

        List<MultipartFile> validFiles =
                new ArrayList<>();


        if (files != null) {


            for (MultipartFile file : files) {


                if (
                        file != null
                        &&
                        !file.isEmpty()
                ) {

                    validFiles.add(
                            file
                    );
                }
            }
        }


        // =================================================
        // 4. 最多 5 張圖片
        // =================================================

        if (
                validFiles.size()
                >
                MAX_FILE_COUNT
        ) {

            throw new IllegalArgumentException(
                    "每則留言最多上傳 5 張圖片"
            );
        }


        // =================================================
        // 5. 驗證每張圖片
        // =================================================

        for (MultipartFile file : validFiles) {

            validateFile(
                    file
            );
        }


        // =================================================
        // 6. 建立 Message
        // =================================================

        Message message =
                new Message();


        message.setUserId(
                currentUser.getId()
        );


        message.setUserName(
                getDisplayName(
                        currentUser
                )
        );


        message.setContent(
                content.trim()
        );


        message.setCreatedAt(
                LocalDateTime.now()
        );


        // =================================================
        // 7. 儲存主留言
        // =================================================

        Message savedMessage =
                messageDao.save(
                        message
                );


        // =================================================
        // 8. 建立圖片附件
        // =================================================

        List<MessageAttachment> attachmentList =
                new ArrayList<>();


        for (MultipartFile file : validFiles) {


            MessageAttachment attachment =
                    new MessageAttachment();


            attachment.setMessageId(
                    savedMessage.getId()
            );


            // =============================================
            // 原始檔名
            // =============================================

            String originalName =
                    file.getOriginalFilename();


            if (
                    originalName == null
                    ||
                    originalName.isBlank()
            ) {

                originalName =
                        "image";
            }


            // =============================================
            // 避免路徑資訊
            // =============================================

            originalName =
                    Paths
                            .get(originalName)
                            .getFileName()
                            .toString();


            attachment.setOriginalFileName(
                    originalName
            );


            // =============================================
            // MIME Type
            // =============================================

            attachment.setContentType(
                    file.getContentType()
            );


            // =============================================
            // 檔案大小
            // =============================================

            attachment.setFileSize(
                    file.getSize()
            );


            // =============================================
            // MultipartFile
            // ↓
            // byte[]
            // ↓
            // MySQL LONGBLOB
            // =============================================

            attachment.setFileData(
                    file.getBytes()
            );


            attachment.setUploadTime(
                    LocalDateTime.now()
            );


            attachmentList.add(
                    attachment
            );
        }


        // =================================================
        // 9. 儲存所有附件
        // =================================================

        if (
                !attachmentList.isEmpty()
        ) {

            messageAttachmentDao
                    .saveAll(
                            attachmentList
                    );
        }


        // =================================================
        // 10. 回傳完整 DTO
        // =================================================

        return convertToResponse(
                savedMessage
        );
    }


    // =====================================================
    // 取得留言圖片
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public MessageAttachment getAttachment(
            Integer attachmentId
    ) {


        if (attachmentId == null) {

            throw new IllegalArgumentException(
                    "圖片 id 不可為空"
            );
        }


        return messageAttachmentDao
                .findById(
                        attachmentId
                )
                .orElseThrow(

                        () ->
                                new IllegalArgumentException(
                                        "找不到圖片 id="
                                        +
                                        attachmentId
                                )
                );
    }


    // =====================================================
    // ★ 雙向留言回覆
    //
    // ADMIN：
    // 可以回覆所有主留言
    //
    // USER：
    // 只能回覆自己的主留言
    // =====================================================

    @Override
    @Transactional
    public MessageReplyResponse saveReply(

            Integer messageId,

            String loginEmail,

            String content

    ) {


        // =================================================
        // 1. messageId 驗證
        // =================================================

        if (messageId == null) {

            throw new IllegalArgumentException(
                    "留言 id 不可為空"
            );
        }


        // =================================================
        // 2. 根據 JWT Email 找登入 User
        // =================================================

        User currentUser =
                findLoginUser(
                        loginEmail
                );


        // =================================================
        // 3. 找原始主留言
        // =================================================

        Message message =
                messageDao
                        .findById(
                                messageId
                        )
                        .orElseThrow(

                                () ->
                                        new IllegalArgumentException(
                                                "找不到留言 id="
                                                +
                                                messageId
                                        )
                        );


        // =================================================
        // 4. 判斷是否為管理員
        // =================================================

        boolean admin =
                isAdmin(
                        currentUser
                );


        // =================================================
        // ★ Console 除錯
        // =================================================

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "留言回覆權限檢查"
        );

        System.out.println(
                "JWT loginEmail = "
                +
                loginEmail
        );

        System.out.println(
                "DB user id = "
                +
                currentUser.getId()
        );

        System.out.println(
                "DB user email = "
                +
                currentUser.getEmail()
        );

        System.out.println(
                "DB user name = "
                +
                currentUser.getName()
        );

        System.out.println(
                "FINAL isAdmin = "
                +
                admin
        );

        System.out.println(
                "message id = "
                +
                message.getId()
        );

        System.out.println(
                "message owner userId = "
                +
                message.getUserId()
        );

        System.out.println(
                "current userId = "
                +
                currentUser.getId()
        );

        System.out.println(
                "=========================================="
        );


        // =================================================
        // 5. 權限判斷
        //
        // 如果是 ADMIN：
        // 不檢查留言 owner。
        //
        // 如果是 USER：
        // message.userId 必須等於 currentUser.id
        // =================================================

        if (
                !admin
                &&
                !Objects.equals(
                        message.getUserId(),
                        currentUser.getId()
                )
        ) {

            throw new SecurityException(
                    "你沒有權限回覆這則留言"
            );
        }


        // =================================================
        // 6. 驗證回覆文字
        // =================================================

        validateContent(
                content
        );


        // =================================================
        // 7. 建立 MessageReply
        // =================================================

        MessageReply reply =
                new MessageReply();


        reply.setMessageId(
                message.getId()
        );


        reply.setSenderUserId(
                currentUser.getId()
        );


        reply.setSenderName(
                getDisplayName(
                        currentUser
                )
        );


        // =================================================
        // ADMIN / USER
        // =================================================

        if (admin) {

            reply.setSenderRole(
                    "ADMIN"
            );

        } else {

            reply.setSenderRole(
                    "USER"
            );
        }


        reply.setContent(
                content.trim()
        );


        reply.setCreatedAt(
                LocalDateTime.now()
        );


        // =================================================
        // 8. DAO 儲存
        // =================================================

        MessageReply savedReply =
                messageReplyDao.save(
                        reply
                );


        System.out.println(
                "留言回覆成功"
        );

        System.out.println(
                "senderRole = "
                +
                savedReply.getSenderRole()
        );


        // =================================================
        // 9. Entity → DTO
        // =================================================

        return convertReplyToResponse(
                savedReply
        );
    }


    // =====================================================
    // ★ 管理員判斷
    //
    // 目前 MyPC：
    //
    // id = 1
    // OR
    // email = admin@example.com
    // OR
    // name = 管理員
    //
    // 任一符合，就視為管理員
    // =====================================================

    private boolean isAdmin(
            User user
    ) {


        if (user == null) {

            return false;
        }


        // =================================================
        // ID 判斷
        // =================================================

        boolean adminById =
                Objects.equals(
                        user.getId(),
                        ADMIN_ID
                );


        // =================================================
        // Email 判斷
        // =================================================

        boolean adminByEmail =
                user.getEmail() != null
                &&
                ADMIN_EMAIL.equalsIgnoreCase(
                        user.getEmail().trim()
                );


        // =================================================
        // Name 判斷
        // =================================================

        boolean adminByName =
                user.getName() != null
                &&
                ADMIN_NAME.equals(
                        user.getName().trim()
                );


        // =================================================
        // Console 除錯
        // =================================================

        System.out.println(
                "========== ADMIN CHECK =========="
        );

        System.out.println(
                "user.id = "
                +
                user.getId()
        );

        System.out.println(
                "user.email = "
                +
                user.getEmail()
        );

        System.out.println(
                "user.name = "
                +
                user.getName()
        );

        System.out.println(
                "adminById = "
                +
                adminById
        );

        System.out.println(
                "adminByEmail = "
                +
                adminByEmail
        );

        System.out.println(
                "adminByName = "
                +
                adminByName
        );


        boolean result =
                adminById
                ||
                adminByEmail
                ||
                adminByName;


        System.out.println(
                "FINAL isAdmin = "
                +
                result
        );

        System.out.println(
                "================================="
        );


        return result;
    }


    // =====================================================
    // JWT Email
    // ↓
    // UserRepository
    // ↓
    // User
    // =====================================================

    private User findLoginUser(
            String loginEmail
    ) {


        if (
                loginEmail == null
                ||
                loginEmail.isBlank()
        ) {

            throw new SecurityException(
                    "登入資訊不存在"
            );
        }


        String email =
                loginEmail.trim();


        return userRepository
                .findByEmail(
                        email
                )
                .orElseThrow(

                        () ->
                                new SecurityException(
                                        "找不到登入會員："
                                        +
                                        email
                                )
                );
    }


    // =====================================================
    // 取得顯示名稱
    // =====================================================

    private String getDisplayName(
            User user
    ) {


        if (
                user.getName() != null
                &&
                !user.getName().isBlank()
        ) {

            return user
                    .getName()
                    .trim();
        }


        return user.getEmail();
    }


    // =====================================================
    // 驗證留言 / 回覆內容
    // =====================================================

    private void validateContent(
            String content
    ) {


        if (
                content == null
                ||
                content.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "留言內容不可空白"
            );
        }


        if (
                content.trim().length()
                >
                1000
        ) {

            throw new IllegalArgumentException(
                    "留言最多 1000 個字"
            );
        }
    }


    // =====================================================
    // 驗證圖片
    // =====================================================

    private void validateFile(
            MultipartFile file
    ) {


        if (
                file == null
                ||
                file.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "圖片內容不可為空"
            );
        }


        // =================================================
        // Size
        // =================================================

        if (
                file.getSize()
                >
                MAX_FILE_SIZE
        ) {

            throw new IllegalArgumentException(
                    "單張圖片不可超過 10MB"
            );
        }


        // =================================================
        // MIME Type
        // =================================================

        String contentType =
                file.getContentType();


        if (contentType == null) {

            throw new IllegalArgumentException(
                    "無法判斷圖片格式"
            );
        }


        boolean validType =
                contentType.equalsIgnoreCase(
                        "image/jpeg"
                )
                ||
                contentType.equalsIgnoreCase(
                        "image/png"
                )
                ||
                contentType.equalsIgnoreCase(
                        "image/webp"
                );


        if (!validType) {

            throw new IllegalArgumentException(
                    "圖片只允許 JPG、PNG、WEBP"
            );
        }
    }


    // =====================================================
    // Message Entity
    // ↓
    // MessageResponse DTO
    // =====================================================

    private MessageResponse convertToResponse(
            Message message
    ) {


        MessageResponse response =
                new MessageResponse();


        response.setId(
                message.getId()
        );


        response.setUserId(
                message.getUserId()
        );


        response.setUserName(
                message.getUserName()
        );


        response.setContent(
                message.getContent()
        );


        response.setCreatedAt(
                message.getCreatedAt()
        );


        // =================================================
        // 附件
        // =================================================

        List<MessageAttachmentResponse> attachments =

                messageAttachmentDao
                        .findByMessageId(
                                message.getId()
                        )
                        .stream()
                        .map(
                                this::
                                convertAttachmentToResponse
                        )
                        .toList();


        response.setAttachments(
                attachments
        );


        // =================================================
        // 雙向回覆
        // =================================================

        List<MessageReplyResponse> replies =

                messageReplyDao
                        .findByMessageId(
                                message.getId()
                        )
                        .stream()
                        .map(
                                this::
                                convertReplyToResponse
                        )
                        .toList();


        response.setReplies(
                replies
        );


        return response;
    }


    // =====================================================
    // MessageAttachment
    // ↓
    // MessageAttachmentResponse
    // =====================================================

    private MessageAttachmentResponse
            convertAttachmentToResponse(

                    MessageAttachment attachment

            ) {


        MessageAttachmentResponse response =
                new MessageAttachmentResponse();


        response.setId(
                attachment.getId()
        );


        response.setOriginalFileName(
                attachment.getOriginalFileName()
        );


        response.setContentType(
                attachment.getContentType()
        );


        response.setFileSize(
                attachment.getFileSize()
        );


        return response;
    }


    // =====================================================
    // MessageReply
    // ↓
    // MessageReplyResponse
    // =====================================================

    private MessageReplyResponse
            convertReplyToResponse(

                    MessageReply reply

            ) {


        MessageReplyResponse response =
                new MessageReplyResponse();


        response.setId(
                reply.getId()
        );


        response.setMessageId(
                reply.getMessageId()
        );


        response.setSenderUserId(
                reply.getSenderUserId()
        );


        response.setSenderName(
                reply.getSenderName()
        );


        response.setSenderRole(
                reply.getSenderRole()
        );


        response.setContent(
                reply.getContent()
        );


        response.setCreatedAt(
                reply.getCreatedAt()
        );


        return response;
    }
}