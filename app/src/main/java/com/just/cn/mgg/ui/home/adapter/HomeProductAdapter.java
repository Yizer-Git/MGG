package com.just.cn.mgg.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.databinding.ItemProductBinding;
import com.just.cn.mgg.utils.PriceUtils;

public class HomeProductAdapter extends ListAdapter<Product, HomeProductAdapter.ProductViewHolder> {

    public interface OnItemClickListener {
        void onProductClick(Product product);
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getProductId() == newItem.getProductId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final OnItemClickListener listener;

    public HomeProductAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemProductBinding binding = ItemProductBinding.inflate(inflater, parent, false);
        return new ProductViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ItemProductBinding binding;
        private final OnItemClickListener listener;

        ProductViewHolder(ItemProductBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        void bind(Product product) {
            binding.tvProductName.setText(product.getProductName());
            binding.tvProductDesc.setText(product.getDescription());
            binding.tvProductPrice.setText(PriceUtils.format(product.getPrice()));

            if (product.hasDiscount()) {
                binding.tvOriginalPrice.setText(PriceUtils.format(product.getOriginalPrice()));
                binding.tvOriginalPrice.getPaint().setStrikeThruText(true);
                binding.tvOriginalPrice.setAlpha(0.7f);
                binding.tvOriginalPrice.setVisibility(View.VISIBLE);
            } else {
                binding.tvOriginalPrice.setVisibility(View.GONE);
            }

            binding.tvSales.setText(binding.getRoot().getContext().getString(
                    R.string.home_item_sales_template,
                    product.getSales()
            ));

            Glide.with(binding.ivProductImage)
                    .load(product.getMainImage())
                    .placeholder(R.drawable.product_placeholder_photo)
                    .error(R.drawable.product_placeholder_photo)
                    .into(binding.ivProductImage);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }
}
