package com.just.cn.mgg.backend.controller;

import com.just.cn.mgg.backend.dto.ApiResponse;
import com.just.cn.mgg.backend.entity.Order;
import com.just.cn.mgg.backend.entity.OrderItem;
import com.just.cn.mgg.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            Integer addressId = (Integer) request.get("address_id");
            String remark = (String) request.get("remark");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
            
            List<OrderItem> items = itemsData.stream().map(item -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId((Integer) item.get("product_id"));
                orderItem.setQuantity((Integer) item.get("quantity"));
                return orderItem;
            }).toList();
            
            Order order = orderService.createOrder(userId, addressId, items, remark);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int page_size,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            Page<Order> orderPage = orderService.getOrderList(userId, status, page, page_size);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", orderPage.getContent());
            result.put("total", orderPage.getTotalElements());
            result.put("page", page);
            result.put("page_size", page_size);
            result.put("total_pages", orderPage.getTotalPages());
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrderDetail(
            @PathVariable String orderId,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            Order order = orderService.getOrderDetail(orderId, userId);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @RequestParam String order_id,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            orderService.cancelOrder(order_id, userId);
            return ResponseEntity.ok(ApiResponse.success("订单已取消"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 确认收货
     */
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> confirmOrder(
            @RequestParam String order_id,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            orderService.confirmOrder(order_id, userId);
            return ResponseEntity.ok(ApiResponse.success("确认收货成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 统计订单数量
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countOrders(
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            
            Map<String, Long> counts = new HashMap<>();
            counts.put("all", orderService.countOrders(userId, null));
            counts.put("pending_payment", orderService.countOrders(userId, 1));
            counts.put("pending_ship", orderService.countOrders(userId, 2));
            counts.put("pending_receive", orderService.countOrders(userId, 3));
            counts.put("completed", orderService.countOrders(userId, 4));
            
            return ResponseEntity.ok(ApiResponse.success(counts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 从认证信息获取用户ID
     */
    private Integer getUserIdFromAuth(Authentication authentication) {
        // TODO: 从JWT Token中提取用户ID
        // 暂时返回测试用户ID
        return 1;
    }
}

