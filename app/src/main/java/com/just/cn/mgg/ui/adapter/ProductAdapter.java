package com.just.cn.mgg.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 产品列表适配器
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    
    private Context context;
    private List<Product> productList;
    private OnItemClickListener onItemClickListener;
    
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }
    
    public ProductAdapter(Context context) {
        this.context = context;
        this.productList = new ArrayList<>();
    }
    
    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        
        // 产品名称
        holder.tvProductName.setText(product.getProductName());
        
        // 产品描述
        holder.tvProductDesc.setText(product.getDescription());
        
        // 价格
        holder.tvProductPrice.setText(PriceUtils.format(product.getPrice()));
        
        // 原价
        if (product.hasDiscount()) {
            holder.tvOriginalPrice.setVisibility(View.VISIBLE);
            holder.tvOriginalPrice.setText(PriceUtils.format(product.getOriginalPrice()));
            holder.tvOriginalPrice.setPaintFlags(
                holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
        } else {
            holder.tvOriginalPrice.setVisibility(View.GONE);
        }
        
        // 销量
        holder.tvSales.setText("已售" + product.getSales());
        
        // 产品图片
        String imageUrl = product.getMainImage();
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.product_placeholder_photo)
                .error(R.drawable.product_placeholder_photo)
                .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.product_placeholder_photo);
        }
        
        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(product);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return productList.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductDesc;
        TextView tvProductPrice;
        TextView tvOriginalPrice;
        TextView tvSales;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductDesc = itemView.findViewById(R.id.tvProductDesc);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvSales = itemView.findViewById(R.id.tvSales);
        }
    }
}

