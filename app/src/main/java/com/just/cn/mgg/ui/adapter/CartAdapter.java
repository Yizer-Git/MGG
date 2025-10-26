package com.just.cn.mgg.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.CartItem;
import com.just.cn.mgg.utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车适配器
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    
    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemChangeListener listener;
    
    public interface OnCartItemChangeListener {
        void onSelectChange(CartItem item, boolean selected);
        void onQuantityChange(CartItem item, int newQuantity);
        void onDelete(CartItem item);
    }
    
    public CartAdapter(Context context) {
        this.context = context;
        this.cartItems = new ArrayList<>();
    }
    
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }
    
    public void setListener(OnCartItemChangeListener listener) {
        this.listener = listener;
    }
    
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        
        if (item.getProduct() == null) {
            return;
        }
        
        // 选择状态
        holder.cbSelect.setChecked(item.getSelected() != null && item.getSelected());
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onSelectChange(item, isChecked);
            }
        });
        
        // 产品信息
        holder.tvProductName.setText(item.getProduct().getProductName());
        holder.tvProductPrice.setText(PriceUtils.format(item.getProduct().getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        
        // 产品图片
        String imageUrl = item.getProduct().getMainImage();
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.color.secondary)
                .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.color.secondary);
        }
        
        // 减少数量
        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                item.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                if (listener != null) {
                    listener.onQuantityChange(item, newQuantity);
                }
            }
        });
        
        // 增加数量
        holder.btnIncrease.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            // 检查库存
            if (currentQuantity < item.getProduct().getStock()) {
                int newQuantity = currentQuantity + 1;
                item.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                if (listener != null) {
                    listener.onQuantityChange(item, newQuantity);
                }
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return cartItems.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvQuantity;
        Button btnDecrease;
        Button btnIncrease;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
        }
    }
}

