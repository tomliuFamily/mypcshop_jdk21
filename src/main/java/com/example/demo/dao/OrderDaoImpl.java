package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;

@Repository
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public OrderItem saveOrderItem(OrderItem item) {
        return orderItemRepository.save(item);
    }

    @Override
    public Optional<Order> findOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findOrdersByUserId(
            Integer userId) {

        return orderRepository
                .findByUserIdOrderByOrderDateDesc(userId);
    }

    @Override
    public List<OrderItem> findItemsByOrderId(
            Integer orderId) {

        return orderItemRepository
                .findByOrderId(orderId);
    }
}