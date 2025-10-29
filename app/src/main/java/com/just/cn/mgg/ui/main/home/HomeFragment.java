package com.just.cn.mgg.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.ui.adapter.ProductAdapter;
import com.just.cn.mgg.ui.main.cart.CartActivity;
import com.just.cn.mgg.MainActivity;
import com.just.cn.mgg.utils.ToastUtils;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 首页Fragment
 */
public class HomeFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private LocalRepository repository;
    private View cultureCard;
    private View cultureExploreButton;
    private View cartShortcut;
    private ViewPager2 bannerViewPager;
    private TabLayout bannerTabLayout;
    private ViewPager2.OnPageChangeCallback bannerPageChangeCallback;

    private final AtomicBoolean isBannerAutoScrollRunning = new AtomicBoolean(false);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new LocalRepository(requireContext());
        initViews(view);
        loadProducts();
    }

    private void initViews(View view) {
        rvProducts = view.findViewById(R.id.rvProducts);
        cultureCard = view.findViewById(R.id.cultureCard);
        cultureExploreButton = view.findViewById(R.id.btnCultureExplore);
        cartShortcut = view.findViewById(R.id.btnCart);
        bannerViewPager = view.findViewById(R.id.vpHomeBanner);
        bannerTabLayout = view.findViewById(R.id.tabHomeBanner);

        View.OnClickListener openCultureListener = v -> navigateToCultureSection();
        if (cultureCard != null) {
            cultureCard.setOnClickListener(openCultureListener);
        }
        if (cultureExploreButton != null) {
            cultureExploreButton.setOnClickListener(openCultureListener);
        }
        if (cartShortcut != null) {
            cartShortcut.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), com.just.cn.mgg.ui.main.cart.CartActivity.class);
                startActivity(intent);
            });
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rvProducts.setLayoutManager(layoutManager);

        productAdapter = new ProductAdapter(getContext());
        productAdapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(getContext(), com.just.cn.mgg.ui.product.ProductDetailActivity.class);
            intent.putExtra(com.just.cn.mgg.ui.product.ProductDetailActivity.EXTRA_PRODUCT_ID,
                    product.getProductId());
            startActivity(intent);
        });
        rvProducts.setAdapter(productAdapter);

        setupBanner();
    }

    private void navigateToCultureSection() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openCultureSection();
        }
    }

    /**
     * 加载产品列表
     */
    private void loadProducts() {
        List<Product> products = repository.getHotProducts(20);
        if (products == null || products.isEmpty()) {
            ToastUtils.show(requireContext(), "暂无商品数据");
        } else {
            productAdapter.setProductList(products);
        }
    }

    private void setupBanner() {
        if (bannerViewPager == null || bannerTabLayout == null) {
            return;
        }

        List<Integer> banners = getHomeBannerImages();
        HomeBannerAdapter bannerAdapter = new HomeBannerAdapter(banners);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerViewPager.setOffscreenPageLimit(3);
        View pagerChild = bannerViewPager.getChildAt(0);
        if (pagerChild instanceof RecyclerView) {
            pagerChild.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        new TabLayoutMediator(bannerTabLayout, bannerViewPager, (tab, position) -> {
            if (tab.getCustomView() == null) {
                tab.setCustomView(R.layout.view_tab_indicator);
            }
        }).attach();

        if (bannerPageChangeCallback != null) {
            bannerViewPager.unregisterOnPageChangeCallback(bannerPageChangeCallback);
        }
        bannerPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                restartAutoScroll();
            }
        };
        bannerViewPager.registerOnPageChangeCallback(bannerPageChangeCallback);

        restartAutoScroll();
    }

    private List<Integer> getHomeBannerImages() {
        return Arrays.asList(
                R.drawable.home_banner_photo_1,
                R.drawable.home_banner_photo_2,
                R.drawable.home_banner_photo_3,
                R.drawable.home_banner_photo_4
        );
    }

    private final Runnable bannerAutoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerViewPager == null || bannerViewPager.getAdapter() == null
                    || bannerViewPager.getAdapter().getItemCount() <= 1) {
                isBannerAutoScrollRunning.set(false);
                return;
            }
            int nextItem = (bannerViewPager.getCurrentItem() + 1) % bannerViewPager.getAdapter().getItemCount();
            bannerViewPager.post(() -> bannerViewPager.setCurrentItem(nextItem, true));
            bannerViewPager.postDelayed(this, 4000);
        }
    };

    private void restartAutoScroll() {
        if (bannerViewPager == null) {
            return;
        }
        bannerViewPager.removeCallbacks(bannerAutoScrollRunnable);
        bannerViewPager.postDelayed(bannerAutoScrollRunnable, 4000);
        isBannerAutoScrollRunning.set(true);
    }

    private void stopAutoScroll() {
        if (bannerViewPager == null) {
            return;
        }
        bannerViewPager.removeCallbacks(bannerAutoScrollRunnable);
        isBannerAutoScrollRunning.set(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isBannerAutoScrollRunning.get()) {
            restartAutoScroll();
        }
    }

    @Override
    public void onPause() {
        stopAutoScroll();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        stopAutoScroll();
        if (bannerViewPager != null && bannerPageChangeCallback != null) {
            bannerViewPager.unregisterOnPageChangeCallback(bannerPageChangeCallback);
            bannerPageChangeCallback = null;
            bannerViewPager = null;
        }
        bannerTabLayout = null;
        super.onDestroyView();
    }
}
