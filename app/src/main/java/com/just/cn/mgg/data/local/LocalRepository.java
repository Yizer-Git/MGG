package com.just.cn.mgg.data.local;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.just.cn.mgg.data.local.entity.AddressEntity;
import com.just.cn.mgg.data.local.entity.ArticleEntity;
import com.just.cn.mgg.data.local.entity.CartItemEntity;
import com.just.cn.mgg.data.local.entity.CategoryEntity;
import com.just.cn.mgg.data.local.entity.OrderEntity;
import com.just.cn.mgg.data.local.entity.OrderItemEntity;
import com.just.cn.mgg.data.local.entity.ProductEntity;
import com.just.cn.mgg.data.local.entity.ReviewCommentEntity;
import com.just.cn.mgg.data.local.entity.ReviewEntity;
import com.just.cn.mgg.data.local.entity.ReviewLikeEntity;
import com.just.cn.mgg.data.local.entity.UserEntity;
import com.just.cn.mgg.data.model.Address;
import com.just.cn.mgg.data.model.Article;
import com.just.cn.mgg.data.model.CartItem;
import com.just.cn.mgg.data.model.Order;
import com.just.cn.mgg.data.model.OrderItem;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.data.model.Review;
import com.just.cn.mgg.data.model.ReviewComment;
import com.just.cn.mgg.data.remote.response.LoginResponse;
import com.just.cn.mgg.utils.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.text.SimpleDateFormat;

/**
 * 基于 Room 的本地数据仓库
 */
public class LocalRepository {

    private final AppDatabase database;
    private final Gson gson = new Gson();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.CHINA);
    private final SimpleDateFormat orderIdFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    public LocalRepository(Context context) {
        this.database = AppDatabase.getInstance(context);
    }

    private String now() {
        return dateFormat.format(new Date());
    }

    private String generateOrderId() {
        int suffix = (int) ((Math.random() * 9 + 1) * 100);
        return "MGG" + orderIdFormat.format(new Date()) + suffix;
    }

    // region 登录 & 用户

    public LoginResponse login(String phone, String password) {
        UserEntity entity = database.userDao().findByPhone(phone);
        if (entity == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!entity.getPassword().equals(password)) {
            throw new IllegalArgumentException("密码错误");
        }
        LoginResponse response = new LoginResponse();
        response.setToken("local_" + UUID.randomUUID());
        response.setUser(mapUser(entity));
        return response;
    }

    public com.just.cn.mgg.data.model.User getUser(int userId) {
        UserEntity entity = database.userDao().findById(userId);
        return entity == null ? null : mapUser(entity);
    }

    private com.just.cn.mgg.data.model.User mapUser(UserEntity entity) {
        com.just.cn.mgg.data.model.User user = new com.just.cn.mgg.data.model.User();
        user.setUserId(entity.getUserId());
        user.setPhone(entity.getPhone());
        user.setNickname(entity.getNickname());
        user.setAvatar(entity.getAvatar());
        user.setRegisterTime(entity.getRegisterTime());
        return user;
    }

    public com.just.cn.mgg.data.model.User registerUser(String phone, String password, @Nullable String nickname) {
        UserEntity existing = database.userDao().findByPhone(phone);
        if (existing != null) {
            throw new IllegalArgumentException("该手机号已注册");
        }
        Integer maxId = database.userDao().getMaxUserId();
        int newId = (maxId == null ? 6000 : maxId) + 1;

        UserEntity entity = new UserEntity();
        entity.setUserId(newId);
        entity.setPhone(phone);
        entity.setPassword(password);
        entity.setNickname(nickname == null || nickname.isEmpty() ? "米友" + (newId % 1000) : nickname);
        entity.setAvatar("https://cdn.mgg.com/avatars/default_user.svg");
        entity.setGender(0);
        entity.setStatus(1);
        String timestamp = now();
        entity.setRegisterTime(timestamp);
        entity.setLastLoginTime(timestamp);

        database.userDao().insert(entity);
        return mapUser(entity);
    }

    // endregion

    // region 产品

    public List<Product> getProducts(@Nullable Integer categoryId) {
        List<ProductEntity> entities = categoryId == null
                ? database.productDao().getAll()
                : database.productDao().getByCategory(categoryId);
        List<Product> result = new ArrayList<>();
        for (ProductEntity entity : entities) {
            result.add(mapProduct(entity));
        }
        return result;
    }

    public Product getProductDetail(int productId) {
        ProductEntity entity = database.productDao().getProduct(productId);
        return entity == null ? null : mapProduct(entity);
    }

    public List<Product> searchProducts(String keyword) {
        List<ProductEntity> entities = database.productDao().search(keyword);
        List<Product> result = new ArrayList<>();
        for (ProductEntity entity : entities) {
            result.add(mapProduct(entity));
        }
        return result;
    }

    public List<Product> getHotProducts(int limit) {
        List<ProductEntity> entities = database.productDao().getHotProducts(limit);
        List<Product> result = new ArrayList<>();
        for (ProductEntity entity : entities) {
            result.add(mapProduct(entity));
        }
        return result;
    }

    private Product mapProduct(ProductEntity entity) {
        Product product = new Product();
        product.setProductId(entity.getProductId());
        product.setProductName(entity.getProductName());
        product.setCategoryId(entity.getCategoryId());
        product.setPrice(entity.getPrice());
        product.setOriginalPrice(entity.getOriginalPrice() != null ? entity.getOriginalPrice() : 0);
        product.setStock(entity.getStock());
        product.setSales(entity.getSales());
        product.setDescription(entity.getDescription());
        product.setCreateTime(entity.getCreateTime());
        product.setOnSale(entity.isOnSale());
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            product.setImages(Arrays.asList(entity.getImages().split(",")));
        }
        product.setQuantity(1);
        product.setSelected(true);
        return product;
    }

    // endregion

    // region 文章

    public List<Article> getArticles(@Nullable String category) {
        List<ArticleEntity> entities = database.articleDao().getByCategory(category);
        List<Article> result = new ArrayList<>();
        for (ArticleEntity entity : entities) {
            result.add(mapArticle(entity));
        }
        return result;
    }

    public Article getArticleDetail(long articleId) {
        ArticleEntity entity = database.articleDao().findById(articleId);
        return entity == null ? null : mapArticle(entity);
    }

    private Article mapArticle(ArticleEntity entity) {
        Article article = new Article();
        article.setArticleId(entity.getArticleId());
        article.setTitle(entity.getTitle());
        article.setSummary(entity.getSummary());
        article.setContent(entity.getContent());
        article.setCoverImage(entity.getCoverImage());
        article.setCategory(entity.getCategory());
        article.setAuthor(entity.getAuthor());
        article.setViewCount(entity.getViewCount());
        article.setPublished(entity.isPublished());
        article.setCreatedAt(entity.getCreatedAt());
        article.setUpdatedAt(entity.getUpdatedAt());
        return article;
    }

    // endregion

    // region 地址

    public List<Address> getAddresses(int userId) {
        List<AddressEntity> entities = database.addressDao().getAddressesForUser(userId);
        List<Address> result = new ArrayList<>();
        for (AddressEntity entity : entities) {
            result.add(mapAddress(entity));
        }
        return result;
    }

    public Address getDefaultAddress(int userId) {
        AddressEntity entity = database.addressDao().getDefaultAddress(userId);
        return entity == null ? null : mapAddress(entity);
    }

    public Address insertAddress(Address address) {
        AddressEntity entity = mapAddressEntity(address);
        if (entity.isDefault()) {
            database.addressDao().clearDefault(entity.getUserId());
        }
        long id = database.addressDao().insert(entity);
        if (entity.getAddressId() == 0) {
            entity.setAddressId((int) id);
        }
        return mapAddress(entity);
    }

    public Address updateAddress(Address address) {
        AddressEntity entity = mapAddressEntity(address);
        if (entity.isDefault()) {
            database.addressDao().clearDefault(entity.getUserId());
        }
        database.addressDao().update(entity);
        return mapAddress(entity);
    }

    public void deleteAddress(Address address) {
        if (address == null) {
            return;
        }
        AddressEntity entity = database.addressDao().findById(address.getAddressId());
        if (entity != null) {
            database.addressDao().delete(entity);
        }
    }

    public void setDefaultAddress(int userId, int addressId) {
        database.addressDao().clearDefault(userId);
        List<AddressEntity> addresses = database.addressDao().getAddressesForUser(userId);
        for (AddressEntity entity : addresses) {
            entity.setDefault(entity.getAddressId() == addressId);
            database.addressDao().update(entity);
        }
    }

    private Address mapAddress(AddressEntity entity) {
        Address address = new Address();
        address.setAddressId(entity.getAddressId());
        address.setUserId(entity.getUserId());
        address.setReceiverName(entity.getReceiverName());
        address.setReceiverPhone(entity.getReceiverPhone());
        address.setProvince(entity.getProvince());
        address.setCity(entity.getCity());
        address.setDistrict(entity.getDistrict());
        address.setDetailAddress(entity.getDetailAddress());
        address.setDefault(entity.isDefault());
        return address;
    }

    private AddressEntity mapAddressEntity(Address address) {
        AddressEntity entity = null;
        if (address.getAddressId() > 0) {
            entity = database.addressDao().findById(address.getAddressId());
        }
        if (entity == null) {
            entity = new AddressEntity();
            entity.setCreateTime(now());
        }

        entity.setAddressId(address.getAddressId());
        entity.setUserId(address.getUserId());
        entity.setReceiverName(address.getReceiverName());
        entity.setReceiverPhone(address.getReceiverPhone());
        entity.setProvince(address.getProvince());
        entity.setCity(address.getCity());
        entity.setDistrict(address.getDistrict());

        if ((entity.getProvince() == null || entity.getProvince().isEmpty())
                && (entity.getCity() == null || entity.getCity().isEmpty())
                && (entity.getDistrict() == null || entity.getDistrict().isEmpty())) {
            String region = address.getRegion();
            if (region != null && !region.trim().isEmpty()) {
                String[] parts = region.trim().split("\\s+");
                if (parts.length > 0) {
                    entity.setProvince(parts[0]);
                }
                if (parts.length > 1) {
                    entity.setCity(parts[1]);
                }
                if (parts.length > 2) {
                    entity.setDistrict(parts[2]);
                }
            }
        }

        entity.setDetailAddress(address.getDetailAddress());
        entity.setDefault(address.isDefault());
        if (entity.getCreateTime() == null || entity.getCreateTime().isEmpty()) {
            entity.setCreateTime(now());
        }
        return entity;
    }

    // endregion

    // region 购物车

    public List<CartItem> getCartItems(int userId) {
        List<CartItemEntity> entities = database.cartItemDao().getCartItems(userId);
        List<CartItem> result = new ArrayList<>();
        for (CartItemEntity entity : entities) {
            result.add(mapCartItem(entity));
        }
        return result;
    }

    public CartItem addToCart(int userId, int productId, int quantity) {
        CartItemEntity entity = database.cartItemDao().findCartItem(userId, productId);
        if (entity == null) {
            entity = new CartItemEntity();
            entity.setUserId(userId);
            entity.setProductId(productId);
            entity.setQuantity(Math.max(1, quantity));
            entity.setSelected(true);
            String ts = now();
            entity.setCreateTime(ts);
            entity.setUpdateTime(ts);
        } else {
            entity.setQuantity(entity.getQuantity() + Math.max(1, quantity));
            entity.setSelected(true);
            entity.setUpdateTime(now());
        }
        long id = database.cartItemDao().insert(entity);
        if (entity.getCartId() == 0) {
            entity.setCartId((int) id);
        }
        return mapCartItem(entity);
    }

    public void updateCartItem(CartItem item) {
        database.cartItemDao().update(mapCartItemEntity(item));
    }

    public void deleteCartItem(CartItem item) {
        database.cartItemDao().delete(mapCartItemEntity(item));
    }

    public void deleteCartItems(int userId, List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        List<Integer> cartIds = new ArrayList<>();
        for (CartItem item : items) {
            if (item != null && item.getCartId() != null && item.getCartId() > 0) {
                cartIds.add(item.getCartId());
            }
        }
        if (!cartIds.isEmpty()) {
            database.cartItemDao().deleteByIds(userId, cartIds);
        }
    }

    private CartItem mapCartItem(CartItemEntity entity) {
        CartItem item = new CartItem();
        item.setCartId(entity.getCartId());
        item.setUserId(entity.getUserId());
        item.setProductId(entity.getProductId());
        item.setQuantity(entity.getQuantity());
        item.setSelected(entity.isSelected());

        Product product = getProductDetail(entity.getProductId());
        if (product != null) {
            item.setProduct(product);
        }
        return item;
    }

    private CartItemEntity mapCartItemEntity(CartItem item) {
        CartItemEntity entity = null;
        if (item.getCartId() != null && item.getCartId() > 0) {
            entity = database.cartItemDao().findById(item.getCartId());
        }
        if (entity == null) {
            entity = new CartItemEntity();
            entity.setCreateTime(now());
        }
        if (item.getCartId() != null && item.getCartId() > 0) {
            entity.setCartId(item.getCartId());
        }
        if (item.getUserId() != null && item.getUserId() > 0) {
            entity.setUserId(item.getUserId());
        }
        if (item.getProductId() != null && item.getProductId() > 0) {
            entity.setProductId(item.getProductId());
        }
        entity.setQuantity(item.getQuantity());
        entity.setSelected(item.getSelected() != null && item.getSelected());
        entity.setUpdateTime(now());
        return entity;
    }

    // endregion

    // region 订单

    public List<Order> getOrders(int userId) {
        return getOrders(userId, null);
    }

    public List<Order> getOrders(int userId, @Nullable Integer status) {
        List<OrderEntity> entities = status == null
                ? database.orderDao().getOrdersForUser(userId)
                : database.orderDao().getOrdersForUser(userId, status);
        List<Order> result = new ArrayList<>();
        for (OrderEntity entity : entities) {
            result.add(mapOrder(entity));
        }
        return result;
    }

    private Order mapOrder(OrderEntity entity) {
        Order order = new Order();
        order.setOrderId(entity.getOrderId());
        order.setUserId(entity.getUserId());
        order.setTotalAmount(entity.getTotalAmount());
        order.setAddressId(entity.getAddressId() != null ? entity.getAddressId() : 0);
        order.setStatus(entity.getStatus());
        order.setRemark(entity.getRemark());
        order.setPayTime(entity.getPayTime());
        order.setShipTime(entity.getShipTime());
        order.setCreateTime(entity.getCreateTime());
        order.setShippingFee(0);
        order.setHasReviewed(entity.getStatus() >= Constants.ORDER_STATUS_COMPLETED);

        Address address = new Address();
        address.setAddressId(order.getAddressId());
        address.setUserId(entity.getUserId());
        address.setReceiverName(entity.getReceiverName());
        address.setReceiverPhone(entity.getReceiverPhone());
        address.setDetailAddress(entity.getReceiverAddress());
        order.setAddress(address);

        List<OrderItemEntity> items = database.orderItemDao().getItemsForOrder(entity.getOrderId());
        List<OrderItem> mapped = new ArrayList<>();
        for (OrderItemEntity itemEntity : items) {
            mapped.add(mapOrderItem(itemEntity));
        }
        order.setOrderItems(mapped);
        return order;
    }

    private OrderItem mapOrderItem(OrderItemEntity entity) {
        OrderItem item = new OrderItem();
        item.setItemId(entity.getItemId());
        item.setOrderId(entity.getOrderId());
        item.setProductId(entity.getProductId());
        item.setProductName(entity.getProductName());
        item.setPrice(entity.getPrice());
        item.setQuantity(entity.getQuantity());
        item.setProductImage(entity.getProductImage());
        return item;
    }

    public Order getOrderDetail(String orderId) {
        OrderEntity entity = database.orderDao().findById(orderId);
        return entity == null ? null : mapOrder(entity);
    }

    public Order createOrder(int userId, Address address, List<CartItem> items, String remark, double shippingFee) {
        if (address == null) {
            throw new IllegalArgumentException("请选择收货地址");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("请选择商品");
        }

        String orderId = generateOrderId();
        double totalAmount = 0;
        List<OrderItemEntity> itemEntities = new ArrayList<>();

        for (CartItem cartItem : items) {
            if (cartItem == null) {
                continue;
            }
            Product product = cartItem.getProduct();
            if (product == null) {
                product = getProductDetail(cartItem.getProductId());
            }
            if (product == null) {
                continue;
            }
            double itemTotal = product.getPrice() * cartItem.getQuantity();
            totalAmount += itemTotal;

            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrderId(orderId);
            itemEntity.setProductId(product.getProductId());
            itemEntity.setProductName(product.getProductName());
            itemEntity.setProductImage(product.getMainImage());
            itemEntity.setPrice(product.getPrice());
            itemEntity.setQuantity(cartItem.getQuantity());
            itemEntities.add(itemEntity);
        }

        if (itemEntities.isEmpty()) {
            throw new IllegalStateException("无法创建订单");
        }

        OrderEntity entity = new OrderEntity();
        entity.setOrderId(orderId);
        entity.setUserId(userId);
        entity.setTotalAmount(totalAmount + shippingFee);
        entity.setAddressId(address.getAddressId());
        entity.setReceiverName(address.getReceiverName());
        entity.setReceiverPhone(address.getReceiverPhone());
        entity.setReceiverAddress(address.getFullAddress());
        entity.setStatus(Constants.ORDER_STATUS_PENDING);
        entity.setRemark(remark);
        entity.setCreateTime(now());
        entity.setPayTime(null);
        entity.setShipTime(null);
        entity.setFinishTime(null);

        database.orderDao().insert(entity);
        database.orderItemDao().insertAll(itemEntities);
        deleteCartItems(userId, items);

        return mapOrder(entity);
    }

    public void cancelOrder(String orderId) {
        OrderEntity entity = database.orderDao().findById(orderId);
        if (entity == null) {
            return;
        }
        entity.setStatus(Constants.ORDER_STATUS_CANCELLED);
        entity.setFinishTime(null);
        database.orderDao().update(entity);
    }

    public void payOrder(String orderId) {
        OrderEntity entity = database.orderDao().findById(orderId);
        if (entity == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (entity.getStatus() != Constants.ORDER_STATUS_PENDING) {
            throw new IllegalStateException("当前状态不可支付");
        }
        entity.setStatus(Constants.ORDER_STATUS_PAID);
        entity.setPayTime(now());
        database.orderDao().update(entity);
    }

    public void confirmOrder(String orderId) {
        OrderEntity entity = database.orderDao().findById(orderId);
        if (entity == null) {
            return;
        }
        entity.setStatus(Constants.ORDER_STATUS_COMPLETED);
        entity.setFinishTime(now());
        database.orderDao().update(entity);
    }

    // endregion

    // region 社区

    public List<Review> getCommunityReviews() {
        return mapReviews(database.reviewDao().getAll());
    }

    public List<Review> getProductReviews(int productId) {
        return mapReviews(database.reviewDao().getByProduct(productId));
    }

    public List<Review> searchCommunityReviews(String keyword) {
        if (keyword == null) {
            keyword = "";
        }
        return mapReviews(database.reviewDao().search(keyword));
    }

    public Review getReviewDetail(long reviewId) {
        ReviewEntity entity = database.reviewDao().findById(reviewId);
        return entity == null ? null : mapReview(entity);
    }

    private List<Review> mapReviews(List<ReviewEntity> entities) {
        List<Review> reviews = new ArrayList<>();
        for (ReviewEntity entity : entities) {
            reviews.add(mapReview(entity));
        }
        return reviews;
    }

    private Review mapReview(ReviewEntity entity) {
        Review review = new Review();
        review.setReviewId(entity.getReviewId());
        review.setUserId(entity.getUserId());
        review.setProductId(entity.getProductId());
        review.setOrderId(entity.getOrderId());
        review.setRating(entity.getRating());
        review.setContent(entity.getContent());
        review.setLikeCount(entity.getLikeCount());
        review.setCommentCount(entity.getCommentCount());
        review.setCreatedAt(entity.getCreatedAt());
        review.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            Type type = new TypeToken<List<String>>() {}.getType();
            review.setImages(gson.fromJson(entity.getImages(), type));
        }

        UserEntity userEntity = database.userDao().findById(entity.getUserId());
        if (userEntity != null) {
            Review.ReviewUser reviewUser = new Review.ReviewUser();
            reviewUser.setUserId(userEntity.getUserId());
            reviewUser.setNickname(userEntity.getNickname());
            reviewUser.setAvatar(userEntity.getAvatar());
            review.setUser(reviewUser);
        }

        return review;
    }

    public List<ReviewComment> getReviewComments(long reviewId) {
        List<ReviewCommentEntity> entities = database.reviewCommentDao().getCommentsForReview(reviewId);
        List<ReviewComment> comments = new ArrayList<>();
        for (ReviewCommentEntity entity : entities) {
            comments.add(mapReviewComment(entity));
        }
        return comments;
    }

    public ReviewComment addReviewComment(long reviewId, int userId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("评论内容不能为空");
        }
        ReviewEntity review = database.reviewDao().findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("评价不存在");
        }
        UserEntity userEntity = database.userDao().findById(userId);
        if (userEntity == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        ReviewCommentEntity entity = new ReviewCommentEntity();
        entity.setReviewId(reviewId);
        entity.setUserId(userId);
        entity.setContent(content.trim());
        entity.setCreatedAt(now());
        long newId = database.reviewCommentDao().insert(entity);
        entity.setCommentId(newId);
        database.reviewDao().adjustCommentCount(reviewId, 1);
        return mapReviewComment(entity);
    }

    public boolean toggleReviewLike(long reviewId, int userId) {
        ReviewEntity review = database.reviewDao().findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("评价不存在");
        }
        ReviewLikeEntity existing = database.reviewLikeDao().findLike(reviewId, userId);
        if (existing != null) {
            database.reviewLikeDao().delete(reviewId, userId);
            database.reviewDao().adjustLikeCount(reviewId, -1);
            return false;
        }
        ReviewLikeEntity likeEntity = new ReviewLikeEntity();
        likeEntity.setReviewId(reviewId);
        likeEntity.setUserId(userId);
        likeEntity.setCreatedAt(now());
        database.reviewLikeDao().insert(likeEntity);
        database.reviewDao().adjustLikeCount(reviewId, 1);
        return true;
    }

    public boolean hasUserLikedReview(long reviewId, int userId) {
        if (userId == 0) {
            return false;
        }
        return database.reviewLikeDao().findLike(reviewId, userId) != null;
    }


    private ReviewComment mapReviewComment(ReviewCommentEntity entity) {
        ReviewComment comment = new ReviewComment();
        comment.setCommentId(entity.getCommentId());
        comment.setReviewId(entity.getReviewId());
        comment.setUserId(entity.getUserId());
        comment.setContent(entity.getContent());
        comment.setCreatedAt(entity.getCreatedAt());

        UserEntity userEntity = database.userDao().findById(entity.getUserId());
        if (userEntity != null) {
            Review.ReviewUser reviewUser = new Review.ReviewUser();
            reviewUser.setUserId(userEntity.getUserId());
            reviewUser.setNickname(userEntity.getNickname());
            reviewUser.setAvatar(userEntity.getAvatar());
            comment.setUser(reviewUser);
        }
        return comment;
    }

    // endregion
}
