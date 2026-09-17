package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.OrderDao;
import com.example.demo.dao.PaymentDao;
import com.example.demo.dao.ProductDao;
import com.example.demo.dto.OrderCreateRequest;
import com.example.demo.dto.OrderItemRequest;
import com.example.demo.dto.OrderItemResponse;
import com.example.demo.dto.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Payment;
import com.example.demo.entity.Product;


@Service
public class OrderServiceImpl
        implements OrderService {


    @Autowired
    private OrderDao orderDao;


    @Autowired
    private ProductDao productDao;


    @Autowired
    private PaymentDao paymentDao;


    // =====================================================
    // 建立訂單
    // =====================================================

    @Override
    @Transactional
    public OrderResponse createOrder(
            OrderCreateRequest request) {


        // =================================================
        // 1. 基本檢查
        // =================================================

        if (request == null) {

            throw new RuntimeException(
                    "訂單資料不可為空"
            );
        }


        if (request.getUserId() == null) {

            throw new RuntimeException(
                    "userId 不可為空"
            );
        }


        if (request.getItems() == null
                || request.getItems().isEmpty()) {

            throw new RuntimeException(
                    "購物車沒有商品"
            );
        }


        double totalAmount = 0;


        List<OrderItem> preparedItems =
                new ArrayList<>();


        List<Product> preparedProducts =
                new ArrayList<>();


        // =================================================
        // 2. 檢查每一項商品
        // =================================================

        for (
            OrderItemRequest requestItem
                : request.getItems()
        ) {


            if (requestItem == null) {

                throw new RuntimeException(
                        "訂單商品資料不可為空"
                );
            }


            if (requestItem.getProductId()
                    == null) {

                throw new RuntimeException(
                        "productId 不可為空"
                );
            }


            if (requestItem.getQuantity()
                    == null
                    || requestItem.getQuantity()
                    <= 0) {

                throw new RuntimeException(
                        "商品數量必須大於 0"
                );
            }


            // =============================================
            // 查商品
            // =============================================

            Product product =
                    productDao
                        .findById(
                            requestItem
                                .getProductId()
                        )
                        .orElseThrow(
                            () ->
                                new RuntimeException(
                                    "找不到商品 ID："
                                    + requestItem
                                        .getProductId()
                                )
                        );


            // =============================================
            // Price
            // =============================================

            if (product.getPrice() == null) {

                throw new RuntimeException(
                        "商品價格不可為空："
                        + product.getName()
                );
            }


            // =============================================
            // Stock
            // =============================================

            if (product.getStock() == null) {

                throw new RuntimeException(
                        "商品庫存資料異常："
                        + product.getName()
                );
            }


            if (product.getStock() <= 0) {

                throw new RuntimeException(
                        "商品「"
                        + product.getName()
                        + "」目前已售完"
                );
            }


            if (
                requestItem.getQuantity()
                > product.getStock()
            ) {

                throw new RuntimeException(
                        "商品「"
                        + product.getName()
                        + "」庫存不足，目前只剩 "
                        + product.getStock()
                        + " 件"
                );
            }


            // =============================================
            // Subtotal
            // =============================================

            double subtotal =
                    product.getPrice()
                    * requestItem.getQuantity();


            totalAmount += subtotal;


            // =============================================
            // Order Item
            // =============================================

            OrderItem orderItem =
                    new OrderItem();


            orderItem.setProductId(
                    product.getId()
            );


            // =============================================
            // Product Snapshot
            // =============================================

            orderItem.setProductName(
                    product.getName()
            );


            orderItem.setProductImage(
                    product.getImage()
            );


            orderItem.setProductCategory(
                    product.getCategory()
            );


            orderItem.setPrice(
                    product.getPrice()
            );


            orderItem.setQuantity(
                    requestItem.getQuantity()
            );


            preparedItems.add(
                    orderItem
            );


            // =============================================
            // 扣庫存
            // =============================================

            int newStock =
                    product.getStock()
                    - requestItem.getQuantity();


            product.setStock(
                    newStock
            );


            // =============================================
            // ★ 累計銷售量
            // =============================================

            int currentSales =
                    product.getSalesCount()
                    == null

                        ? 1000

                        : product.getSalesCount();


            int newSalesCount =
                    currentSales
                    + requestItem.getQuantity();


            product.setSalesCount(
                    newSalesCount
            );


            preparedProducts.add(
                    product
            );
        }


        // =================================================
        // 3. 建立 Order
        // =================================================

        Order order =
                new Order();


        order.setUserId(
                request.getUserId()
        );


        order.setOrderDate(
                LocalDateTime.now()
        );


        order.setTotalAmount(
                totalAmount
        );


        order.setStatus(
                "NEW"
        );


        Order savedOrder =
                orderDao.saveOrder(
                        order
                );


        // =================================================
        // 4. 儲存明細
        // =================================================

        for (
            OrderItem item
                : preparedItems
        ) {

            item.setOrderId(
                    savedOrder.getId()
            );


            orderDao.saveOrderItem(
                    item
            );
        }


        // =================================================
        // 5. 更新庫存 + 銷量
        // =================================================

        for (
            Product product
                : preparedProducts
        ) {

            productDao.save(
                    product
            );
        }


        return convertToResponse(
                savedOrder
        );
    }


    // =====================================================
    // User Orders
    // =====================================================

    @Override
    public List<OrderResponse>
            getOrdersByUserId(
                    Integer userId) {


        if (userId == null) {

            throw new RuntimeException(
                    "userId 不可為空"
            );
        }


        List<Order> orders =
                orderDao
                    .findOrdersByUserId(
                        userId
                    );


        List<OrderResponse> responses =
                new ArrayList<>();


        for (Order order : orders) {

            responses.add(
                convertToResponse(
                    order
                )
            );
        }


        return responses;
    }


    // =====================================================
    // Order
    // =====================================================

    @Override
    public OrderResponse getOrderById(
            Integer orderId) {


        if (orderId == null) {

            throw new RuntimeException(
                    "orderId 不可為空"
            );
        }


        Order order =
                orderDao
                    .findOrderById(
                        orderId
                    )
                    .orElseThrow(
                        () ->
                            new RuntimeException(
                                "找不到訂單 ID："
                                + orderId
                            )
                    );


        return convertToResponse(
                order
        );
    }


    // =====================================================
    // Entity → Response DTO
    // =====================================================

    private OrderResponse convertToResponse(
            Order order) {


        OrderResponse response =
                new OrderResponse();


        response.setId(
                order.getId()
        );


        response.setUserId(
                order.getUserId()
        );


        response.setOrderDate(
                order.getOrderDate()
        );


        response.setTotalAmount(
                order.getTotalAmount()
        );


        response.setStatus(
                order.getStatus()
        );


        // =================================================
        // Payment
        // =================================================

        Payment payment =
                paymentDao
                    .findByOrderId(
                        order.getId()
                    )
                    .orElse(null);


        if (payment != null) {

            response.setPaymentMethod(
                    payment.getPaymentMethod()
            );


            response.setPaymentStatus(
                    payment.getPaymentStatus()
            );

        } else {

            response.setPaymentMethod(
                    null
            );


            response.setPaymentStatus(
                    "UNPAID"
            );
        }


        // =================================================
        // Order Items
        // =================================================

        List<OrderItem> orderItems =
                orderDao
                    .findItemsByOrderId(
                        order.getId()
                    );


        List<OrderItemResponse>
            itemResponses =
                new ArrayList<>();


        for (
            OrderItem item
                : orderItems
        ) {


            OrderItemResponse itemResponse =
                    new OrderItemResponse();


            itemResponse.setProductId(
                    item.getProductId()
            );


            itemResponse.setProductName(
                    item.getProductName()
            );


            itemResponse.setProductImage(
                    item.getProductImage()
            );


            itemResponse.setProductCategory(
                    item.getProductCategory()
            );


            itemResponse.setQuantity(
                    item.getQuantity()
            );


            itemResponse.setPrice(
                    item.getPrice()
            );


            double subtotal = 0;


            if (
                item.getPrice() != null
                &&
                item.getQuantity() != null
            ) {

                subtotal =
                        item.getPrice()
                        * item.getQuantity();
            }


            itemResponse.setSubtotal(
                    subtotal
            );


            itemResponses.add(
                    itemResponse
            );
        }


        response.setItems(
                itemResponses
        );


        return response;
    }
}