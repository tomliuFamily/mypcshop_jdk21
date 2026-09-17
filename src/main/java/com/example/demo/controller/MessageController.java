package com.example.demo.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.MessageReplyRequest;
import com.example.demo.dto.MessageReplyResponse;
import com.example.demo.dto.MessageResponse;

import com.example.demo.entity.MessageAttachment;

import com.example.demo.service.MessageService;


@RestController
@RequestMapping("/api/messages")
@CrossOrigin(
    origins = "http://localhost:5173"
)
public class MessageController {


    private final MessageService
        messageService;


    private final SimpMessagingTemplate
        messagingTemplate;


    public MessageController(

        MessageService messageService,

        SimpMessagingTemplate messagingTemplate

    ) {

        this.messageService =
            messageService;

        this.messagingTemplate =
            messagingTemplate;
    }


    // ==========================================
    // 查詢全部
    // ==========================================

    @GetMapping(
        produces =
            MediaType.APPLICATION_JSON_VALUE
    )
    public List<MessageResponse>
        getAllMessages() {


        return messageService
            .getAllMessages();
    }


    // ==========================================
    // 後端分頁
    // ==========================================

    @GetMapping(
        value = "/page",
        produces =
            MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?>
        getMessagePage(

            @RequestParam(
                defaultValue = "0"
            )
            int page

        ) {


        try {


            Page<MessageResponse> result =
                messageService
                    .getMessagePage(
                        page
                    );


            return ResponseEntity.ok(
                result
            );


        } catch (
            IllegalArgumentException e
        ) {


            return ResponseEntity
                .badRequest()
                .body(
                    e.getMessage()
                );
        }
    }


    // ==========================================
    // ★ 新增主留言 + 圖片
    //
    // 身分直接從 JWT Principal 取得
    // ==========================================

    @PostMapping(
        value = "/upload",
        consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?>
        createMessage(

            @RequestParam("content")
            String content,

            @RequestParam(
                value = "files",
                required = false
            )
            List<MultipartFile> files,

            Principal principal

        ) {


        if (
            principal == null
        ) {

            return ResponseEntity
                .status(401)
                .body(
                    "請先登入"
                );
        }


        try {


            String loginEmail =
                principal.getName();


            MessageResponse result =
                messageService
                    .saveMessageWithFiles(

                        loginEmail,

                        content,

                        files
                    );


            // ==================================
            // WebSocket：
            // 通知其他瀏覽器有資料更新
            // ==================================

            messagingTemplate
                .convertAndSend(

                    "/topic/messages",

                    result
                );


            return ResponseEntity
                .status(201)
                .body(
                    result
                );


        } catch (
            SecurityException e
        ) {


            return ResponseEntity
                .status(403)
                .body(
                    e.getMessage()
                );


        } catch (
            IllegalArgumentException e
        ) {


            return ResponseEntity
                .badRequest()
                .body(
                    e.getMessage()
                );


        } catch (
            IOException e
        ) {


            return ResponseEntity
                .internalServerError()
                .body(
                    "圖片處理失敗"
                );
        }
    }


    // ==========================================
    // 顯示圖片
    // ==========================================

    @GetMapping(
        "/attachments/{id}/view"
    )
    public ResponseEntity<byte[]>
        viewAttachment(

            @PathVariable
            Integer id

        ) {


        MessageAttachment file =
            messageService
                .getAttachment(
                    id
                );


        MediaType mediaType =
            MediaType.APPLICATION_OCTET_STREAM;


        if (
            file.getContentType()
            != null
        ) {

            mediaType =
                MediaType.parseMediaType(
                    file.getContentType()
                );
        }


        return ResponseEntity
            .ok()
            .contentType(
                mediaType
            )
            .body(
                file.getFileData()
            );
    }


    // ==========================================
    // ★ 雙向回覆
    //
    // 管理員：
    // 可回所有留言
    //
    // 一般會員：
    // 只能回自己的留言
    // ==========================================

    @PostMapping(
        value = "/{messageId}/reply",
        consumes =
            MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?>
        replyMessage(

            @PathVariable
            Integer messageId,

            @RequestBody
            MessageReplyRequest request,

            Principal principal

        ) {


        if (
            principal == null
        ) {

            return ResponseEntity
                .status(401)
                .body(
                    "請先登入"
                );
        }


        try {


            MessageReplyResponse result =
                messageService
                    .saveReply(

                        messageId,

                        principal.getName(),

                        request.getContent()
                    );


            // ==================================
            // WebSocket 通知大家刷新
            // ==================================

            messagingTemplate
                .convertAndSend(

                    "/topic/messages",

                    result
                );


            return ResponseEntity
                .status(201)
                .body(
                    result
                );


        } catch (
            SecurityException e
        ) {


            return ResponseEntity
                .status(403)
                .body(
                    e.getMessage()
                );


        } catch (
            IllegalArgumentException e
        ) {


            return ResponseEntity
                .badRequest()
                .body(
                    e.getMessage()
                );
        }
    }
}