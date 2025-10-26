package com.just.cn.mgg.backend.service;

import com.just.cn.mgg.backend.entity.*;
import com.just.cn.mgg.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    /**
     * 创建订单
     */
    @Transactional
    public Order createOrder(Integer userId, Integer addressId, List<OrderItem> items, String remark) {
        // 验证地址
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不属于当前用户");
        }
        
        // 计算总金额并验证库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("产品不存在：" + item.getProductId()));
            
            // 验证库存
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("产品库存不足：" + product.getProductName());
            }
            
            // 使用当前价格
            item.setPrice(product.getPrice());
            item.setProductName(product.getProductName());
            
            // 设置第一张图片
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                String[] images = product.getImages().split(",");
                item.setProductImage(images[0]);
            }
            
            // 计算小计
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            
            // 减少库存，增加销量
            product.setStock(product.getStock() - item.getQuantity());
            product.setSales(product.getSales() + item.getQuantity());
            productRepository.save(product);
        }
        
        // 生成订单ID
        String orderId = generateOrderId();
        
        // 创建订单
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setAddressId(addressId);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getFullAddress());
        order.setStatus(1); // 待付款
        order.setRemark(remark);
        
        Order savedOrder = orderRepository.save(order);
        
        // 保存订单详情
        for (OrderItem item : items) {
            item.setOrderId(orderId);
            orderItemRepository.save(item);
        }
        
        // 从购物车删除已下单商品
        for (OrderItem item : items) {
            cartItemRepository.deleteByUserIdAndProductId(userId, item.getProductId());
        }
        
        savedOrder.setOrderItems(items);
        return savedOrder;
    }
    
    /**
     * 获取订单列表
     */
    public Page<Order> getOrderList(Integer userId, Integer status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        Page<Order> orderPage;
        if (status != null) {
            orderPage = orderRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, status, pageable);
        } else {
            orderPage = orderRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
        }
        
        // 填充订单详情
        for (Order order : orderPage.getContent()) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getOrderId());
            order.setOrderItems(items);
        }
        
        return orderPage;
    }
    
    /**
     * 获取订单详情
     */
    public Order getOrderDetail(String orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        // 验证订单所有权
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此订单");
        }
        
        // 填充订单详情
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        order.setOrderItems(items);
        
        return order;
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(String orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        // 验证订单所有权
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        // 只有待付款状态可以取消
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不允许取消");
        }
        
        // 恢复库存
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(Math.max(0, product.getSales() - item.getQuantity()));
                productRepository.save(product);
            }
        }
        
        // 更新订单状态
        order.setStatus(5); // 已取消
        orderRepository.save(order);
    }
    
    /**
     * 确认收货
     */
    @Transactional
    public void confirmOrder(String orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        // 验证订单所有权
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        // 只有待收货状态可以确认
        if (order.getStatus() != 3) {
            throw new RuntimeException("订单状态不允许确认收货");
        }
        
        // 更新订单状态
        order.setStatus(4); // 已完成
        order.setFinishTime(LocalDateTime.now());
        orderRepository.save(order);
    }
    
    /**
     * 生成订单ID
     */
    private String generateOrderId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        Random random = new Random();
        int randomNum = 10000 + random.nextInt(90000);
        return timestamp + randomNum;
    }
    
    /**
     * 统计订单数量
     */
    public long countOrders(Integer userId, Integer status) {
        if (status != null) {
            return orderRepository.countByUserIdAndStatus(userId, status);
        }
        return orderRepository.countByUserId(userId);
    }
}

