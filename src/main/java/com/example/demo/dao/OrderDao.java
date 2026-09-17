package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;

public interface OrderDao {

    Order saveOrder(Order order);

    OrderItem saveOrderItem(OrderItem item);

    Optional<Order> findOrderById(Integer id);

    List<Order> findOrdersByUserId(Integer userId);

    List<OrderItem> findItemsByOrderId(Integer orderId);
}