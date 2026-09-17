package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.StockNotificationRequest;
import com.example.demo.dto.StockNotificationResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.StockNotification;
import com.example.demo.entity.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockNotificationRepository;
import com.example.demo.repository.UserRepository;

@Service
public class StockNotificationServiceImpl
        implements StockNotificationService {

    private final StockNotificationRepository
        notificationRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;


    public StockNotificationServiceImpl(
        StockNotificationRepository
            notificationRepository,
        UserRepository userRepository,
        ProductRepository productRepository
    ) {

        this.notificationRepository =
            notificationRepository;

        this.userRepository =
            userRepository;

        this.productRepository =
            productRepository;
    }


    // ==========================================
    // 新增到貨通知
    // ==========================================

    @Override
    @Transactional
    public StockNotificationResponse create(
        StockNotificationRequest request
    ) {

        if (
            request.getUserId() == null
            ||
            request.getProductId() == null
        ) {

            throw new IllegalArgumentException(
                "會員 ID 與商品 ID 不可為空"
            );
        }


        // 不允許重複設定
        notificationRepository
            .findByUserIdAndProductId(
                request.getUserId(),
                request.getProductId()
            )
            .ifPresent(
                item -> {
                    throw new IllegalArgumentException(
                        "這個商品已經設定到貨通知"
                    );
                }
            );


        User user =
            userRepository
                .findById(
                    request.getUserId()
                )
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "找不到會員"
                        )
                );


        Product product =
            productRepository
                .findById(
                    request.getProductId()
                )
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "找不到商品"
                        )
                );


        StockNotification notification =
            new StockNotification();

        notification.setUser(user);

        notification.setProduct(product);

        notification.setTargetPrice(
            request.getTargetPrice()
        );

        notification.setStatus(
            product.getStock() != null
            &&
            product.getStock() > 0
                ? "AVAILABLE"
                : "WAITING"
        );


        StockNotification saved =
            notificationRepository.save(
                notification
            );


        return convert(saved);
    }


    // ==========================================
    // 查詢會員通知
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<StockNotificationResponse>
        getByUserId(
            Integer userId
        ) {

        return notificationRepository
            .findByUserIdOrderByCreatedAtDesc(
                userId
            )
            .stream()
            .map(this::convert)
            .toList();
    }


    // ==========================================
    // 取消通知
    // ==========================================

    @Override
    @Transactional
    public void delete(
        Integer notificationId,
        Integer userId
    ) {

        StockNotification notification =
            notificationRepository
                .findById(notificationId)
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "找不到通知資料"
                        )
                );


        if (
            !notification
                .getUser()
                .getId()
                .equals(userId)
        ) {

            throw new IllegalArgumentException(
                "無權刪除此通知"
            );
        }


        notificationRepository.delete(
            notification
        );
    }


    // ==========================================
    // Entity → DTO
    // ==========================================

    private StockNotificationResponse convert(
        StockNotification notification
    ) {

        Product product =
            notification.getProduct();


        StockNotificationResponse response =
            new StockNotificationResponse();


        response.setId(
            notification.getId()
        );

        response.setProductId(
            product.getId()
        );

        response.setProductName(
            product.getName()
        );

        response.setCategory(
            product.getCategory()
        );

        response.setPrice(
            product.getPrice()
        );

        response.setStock(
            product.getStock()
        );

        response.setImage(
            product.getImage()
        );

        response.setTargetPrice(
            notification.getTargetPrice()
        );

        response.setStatus(
            product.getStock() != null
            &&
            product.getStock() > 0
                ? "AVAILABLE"
                : "WAITING"
        );

        response.setCreatedAt(
            notification.getCreatedAt()
        );


        return response;
    }
}