package com.just.cn.mgg.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton; // <-- Import ImageButton
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton; // <-- Import CompoundButton

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.CartItem;
import com.just.cn.mgg.utils.PriceUtils;
import com.just.cn.mgg.utils.ToastUtils; // <-- Import ToastUtils

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
        void onSelectChange(CartItem item, boolean selected, int position); // 添加 position
        void onQuantityChange(CartItem item, int newQuantity, int position); // 添加 position
        void onDelete(CartItem item, int position); // 添加 position
    }

    public CartAdapter(Context context) {
        this.context = context;
        this.cartItems = new ArrayList<>();
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = (cartItems == null) ? new ArrayList<>() : cartItems; // 防御空指针
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
        // 使用 holder.getAdapterPosition() 获取当前准确位置
        int currentPosition = holder.getAdapterPosition();
        if (currentPosition == RecyclerView.NO_POSITION || cartItems == null || currentPosition >= cartItems.size()) {
            // 防止越界或数据不一致
            return;
        }
        CartItem item = cartItems.get(currentPosition);


        if (item == null || item.getProduct() == null) {
            // 处理无效数据，例如隐藏视图或显示错误提示
            holder.itemView.setVisibility(View.GONE);
            return;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        // --- 数据绑定 ---
        // 产品信息
        holder.tvProductName.setText(item.getProduct().getProductName());
        holder.tvProductPrice.setText(PriceUtils.format(item.getProduct().getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // 产品图片
        String imageUrl = item.getProduct().getMainImage();
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.color.color_surface_elevated)
                    .error(R.color.color_surface_elevated) // 添加错误占位符
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.color.color_surface_elevated);
        }

        // --- 事件监听 ---
        // 选择状态 (先移除旧监听器避免重复触发)
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(item.getSelected() != null && item.getSelected());
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                // 再次检查位置，防止异步问题
                int latestPosition = holder.getAdapterPosition();
                if (latestPosition != RecyclerView.NO_POSITION) {
                    listener.onSelectChange(cartItems.get(latestPosition), isChecked, latestPosition);
                }
            }
        });


        // 减少数量
        holder.btnDecrease.setOnClickListener(v -> {
            int latestPosition = holder.getAdapterPosition();
            if (latestPosition == RecyclerView.NO_POSITION) return;
            CartItem currentItem = cartItems.get(latestPosition);
            int currentQuantity = currentItem.getQuantity();
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                // holder.tvQuantity.setText(String.valueOf(newQuantity)); // UI更新交给Fragment处理
                if (listener != null) {
                    listener.onQuantityChange(currentItem, newQuantity, latestPosition);
                }
            } else {
                ToastUtils.showShort(context, "不能再减少了");
            }
        });

        // 增加数量
        holder.btnIncrease.setOnClickListener(v -> {
            int latestPosition = holder.getAdapterPosition();
            if (latestPosition == RecyclerView.NO_POSITION) return;
            CartItem currentItem = cartItems.get(latestPosition);
            int currentQuantity = currentItem.getQuantity();
            // 检查库存
            if (currentItem.getProduct() != null && currentQuantity < currentItem.getProduct().getStock()) {
                int newQuantity = currentQuantity + 1;
                // holder.tvQuantity.setText(String.valueOf(newQuantity)); // UI更新交给Fragment处理
                if (listener != null) {
                    listener.onQuantityChange(currentItem, newQuantity, latestPosition);
                }
            } else {
                ToastUtils.showShort(context, "库存不足");
            }
        });

        // 删除按钮
        holder.btnDelete.setOnClickListener(v -> {
            int latestPosition = holder.getAdapterPosition();
            if (listener != null && latestPosition != RecyclerView.NO_POSITION) {
                listener.onDelete(cartItems.get(latestPosition), latestPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems == null ? 0 : cartItems.size(); // 防御空指针
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvQuantity;
        Button btnDecrease;
        Button btnIncrease;
        ImageButton btnDelete; // <-- 添加删除按钮引用

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDelete = itemView.findViewById(R.id.btnDelete); // <-- 初始化删除按钮
        }
    }
}
