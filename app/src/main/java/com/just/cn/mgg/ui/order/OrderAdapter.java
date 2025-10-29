package com.just.cn.mgg.ui.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.Order;
import com.just.cn.mgg.data.model.OrderItem;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onOrderItemClick(Order order, int position);
        void onAction1Click(Order order, int position); // e.g., Cancel
        void onAction2Click(Order order, int position); // e.g., Pay, Confirm Receipt
    }

    public OrderAdapter(Context context) {
        this.context = context;
        this.orderList = new ArrayList<>();
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = (orderList == null) ? new ArrayList<>() : orderList;
        notifyDataSetChanged();
    }

    public void setListener(OnOrderActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int currentPosition = holder.getAdapterPosition();
        if (currentPosition == RecyclerView.NO_POSITION) return;
        Order order = orderList.get(currentPosition);
        if (order == null) return;

        holder.tvOrderId.setText(order.getOrderId());
        holder.tvOrderTotalAmount.setText(PriceUtils.format(order.getTotalAmount()));
        holder.tvOrderStatus.setText(order.getStatusText()); // 使用模型中的方法

        // 显示第一个商品信息
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            OrderItem firstItem = order.getOrderItems().get(0);
            if (firstItem != null) {
                holder.tvProductName.setText(firstItem.getProductName());
                String imageUrl = firstItem.getProductImage();
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.color.color_surface_elevated)
                        .error(R.color.color_surface_elevated)
                        .into(holder.ivProductImage);
            }

            if (order.getOrderItems().size() > 1) {
                holder.tvProductCount.setText("等" + order.getOrderItems().size() + "件商品");
                holder.tvProductCount.setVisibility(View.VISIBLE);
            } else {
                holder.tvProductCount.setVisibility(View.GONE);
            }
        } else {
            // 没有商品信息（理论上不应该）
            holder.tvProductName.setText("订单无商品");
            holder.tvProductCount.setVisibility(View.GONE);
            holder.ivProductImage.setImageResource(R.color.color_surface_elevated);
        }


        // 根据订单状态显示不同的操作按钮
        setupActionButtons(holder, order, currentPosition);

        // 列表项点击
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderItemClick(order, currentPosition);
            }
        });
    }

    private void setupActionButtons(ViewHolder holder, Order order, int position) {
        // 先隐藏所有按钮
        holder.btnOrderAction1.setVisibility(View.GONE);
        holder.btnOrderAction2.setVisibility(View.GONE);

        switch (order.getStatus()) {
            case Constants.ORDER_STATUS_PENDING: // 待付款
                holder.btnOrderAction1.setText("取消订单");
                holder.btnOrderAction1.setVisibility(View.VISIBLE);
                holder.btnOrderAction2.setText("去支付");
                holder.btnOrderAction2.setVisibility(View.VISIBLE);

                holder.btnOrderAction1.setOnClickListener(v -> {
                    if (listener != null) listener.onAction1Click(order, position);
                });
                holder.btnOrderAction2.setOnClickListener(v -> {
                    if (listener != null) listener.onAction2Click(order, position);
                });
                break;
            case Constants.ORDER_STATUS_PAID: // 待发货
                holder.btnOrderAction1.setVisibility(View.GONE);
                break;
            case Constants.ORDER_STATUS_SHIPPED: // 待收货
                holder.btnOrderAction2.setText("确认收货");
                holder.btnOrderAction2.setVisibility(View.VISIBLE);
                holder.btnOrderAction2.setOnClickListener(v -> {
                    if (listener != null) listener.onAction2Click(order, position);
                });
                break;
            case Constants.ORDER_STATUS_COMPLETED: // 已完成
                holder.btnOrderAction2.setText("去评价");
                holder.btnOrderAction2.setVisibility(View.GONE); // 评价功能待实现
                break;
            case Constants.ORDER_STATUS_CANCELLED: // 已取消
                // 无操作
                break;
        }
    }


    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvProductName, tvProductCount, tvOrderTotalAmount;
        ImageView ivProductImage;
        Button btnOrderAction1, btnOrderAction2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCount = itemView.findViewById(R.id.tvProductCount);
            tvOrderTotalAmount = itemView.findViewById(R.id.tvOrderTotalAmount);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            btnOrderAction1 = itemView.findViewById(R.id.btnOrderAction1);
            btnOrderAction2 = itemView.findViewById(R.id.btnOrderAction2);
        }
    }
}
