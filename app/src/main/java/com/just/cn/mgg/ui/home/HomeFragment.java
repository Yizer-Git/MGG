package com.just.cn.mgg.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.just.cn.mgg.MainActivity;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.databinding.FragmentHomeBinding;
import com.just.cn.mgg.ui.home.adapter.HomeProductAdapter;
import com.just.cn.mgg.ui.home.model.HomeUiState;
import com.just.cn.mgg.ui.main.cart.CartActivity;
import com.just.cn.mgg.ui.product.ProductDetailActivity;
import com.just.cn.mgg.utils.ToastUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private HomeProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        setupRecycler();
        setupClicks();
        observeState();
    }

    private void setupRecycler() {
        productAdapter = new HomeProductAdapter(new HomeProductAdapter.OnItemClickListener() {
            @Override
            public void onProductClick(Product product) {
                if (getContext() == null) {
                    return;
                }
                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getProductId());
                startActivity(intent);
            }
        });
        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvProducts.setAdapter(productAdapter);
    }

    private void setupClicks() {
        View.OnClickListener openCulture = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openCultureSection();
                }
            }
        };
        binding.cultureCard.setOnClickListener(openCulture);
        binding.btnCultureExplore.setOnClickListener(openCulture);

        binding.btnCart.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CartActivity.class)));
    }

    private void observeState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), this::renderState);
    }

    private void renderState(HomeUiState state) {
        if (binding == null) {
            return;
        }
        binding.progressHome.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
        binding.rvProducts.setVisibility(state.getProducts().isEmpty() ? View.GONE : View.VISIBLE);
        boolean showEmpty = !state.isLoading() && state.getProducts().isEmpty();
        binding.tvEmptyState.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
        productAdapter.submitList(state.getProducts());

        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            if (getContext() != null) {
                ToastUtils.show(requireContext(), state.getErrorMessage());
            }
        }
    }

    @Override
    public void onDestroyView() {
        productAdapter = null;
        binding = null;
        super.onDestroyView();
    }
}
