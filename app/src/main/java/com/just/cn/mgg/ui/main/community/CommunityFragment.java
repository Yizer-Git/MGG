package com.just.cn.mgg.ui.main.community;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.HomeRecommendation;
import com.just.cn.mgg.data.model.Review;
import com.just.cn.mgg.ui.review.ReviewAdapter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * 社区晒单 Fragment
 */
public class CommunityFragment extends Fragment {

    private static final int PAGE_SIZE = 10;
    private static final long BANNER_AUTO_SCROLL_DELAY_MS = 4000L;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private CircularProgressIndicator progressIndicator;
    private View emptyView;
    private ChipGroup chipGroup;
    private ViewPager2 bannerPager;
    private LinearLayout bannerIndicator;

    private final EnumMap<CommunityTag, Integer> tagIdMap = new EnumMap<>(CommunityTag.class);
    private CommunityTag currentTag = CommunityTag.RECOMMEND;

    private ReviewAdapter reviewAdapter;
    private CommunityBannerAdapter bannerAdapter;
    private LocalRepository repository;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private List<Review> allReviews = new ArrayList<>();
    private List<Review> filteredReviews = new ArrayList<>();

    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private final Runnable bannerAutoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerPager == null || bannerAdapter == null || bannerAdapter.getItemCount() <= 1) {
                return;
            }
            int next = (bannerPager.getCurrentItem() + 1) % bannerAdapter.getItemCount();
            bannerPager.setCurrentItem(next, true);
            bannerHandler.postDelayed(this, BANNER_AUTO_SCROLL_DELAY_MS);
        }
    };
    private ViewPager2.OnPageChangeCallback bannerPageChangeCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new LocalRepository(requireContext());
        initViews(view);
        setupRecyclerView();
        setupRefresh();
        loadCommunityReviews(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        startBannerAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBannerAutoScroll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopBannerAutoScroll();
        if (bannerPager != null && bannerPageChangeCallback != null) {
            bannerPager.unregisterOnPageChangeCallback(bannerPageChangeCallback);
        }
        bannerPager = null;
        bannerIndicator = null;
        chipGroup = null;
        recyclerView = null;
        swipeRefreshLayout = null;
        progressIndicator = null;
        emptyView = null;
    }

    private void initViews(View root) {
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshCommunity);
        recyclerView = root.findViewById(R.id.recyclerViewCommunity);
        progressIndicator = root.findViewById(R.id.progressCommunity);
        emptyView = root.findViewById(R.id.emptyCommunity);
        chipGroup = root.findViewById(R.id.chipGroupExploreFilters);
        bannerPager = root.findViewById(R.id.vpCommunityBanner);
        bannerIndicator = root.findViewById(R.id.layoutCommunityBannerIndicator);

        tagIdMap.put(CommunityTag.RECOMMEND, R.id.chipRecommend);
        tagIdMap.put(CommunityTag.HANDMADE, R.id.chipHandmade);
        tagIdMap.put(CommunityTag.FESTIVAL, R.id.chipFestival);
        tagIdMap.put(CommunityTag.INHERITOR, R.id.chipInheritor);

        if (chipGroup != null) {
            chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds == null || checkedIds.isEmpty()) {
                    return;
                }
                CommunityTag tag = mapChipIdToTag(checkedIds.get(0));
                if (tag != null && tag != currentTag) {
                    currentTag = tag;
                    loadCommunityReviews(true);
                }
            });
            applyTagSelection(currentTag);
        }

        reviewAdapter = new ReviewAdapter(requireContext());
        reviewAdapter.setOnReviewClickListener(review ->
                startActivity(ReviewDetailActivity.createIntent(requireContext(), review.getReviewId())));

        setupBanner();
    }

    private void setupBanner() {
        if (bannerPager == null) {
            return;
        }
        bannerAdapter = new CommunityBannerAdapter(requireContext());
        bannerAdapter.setOnBannerClickListener(item ->
                Toast.makeText(requireContext(),
                        getString(R.string.home_recommendation_open_placeholder, item.getTitle()),
                        Toast.LENGTH_SHORT).show());
        bannerPager.setAdapter(bannerAdapter);
        bannerPager.setOffscreenPageLimit(3);
        bannerPager.setClipToPadding(false);
        bannerPager.setClipChildren(false);
        if (bannerPager.getChildCount() > 0) {
            bannerPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(
                getResources().getDimensionPixelOffset(R.dimen.spacing_medium)));
        transformer.addTransformer((page, position) -> {
            float scale = 0.92f + (1 - Math.abs(position)) * 0.08f;
            page.setScaleY(scale);
        });
        bannerPager.setPageTransformer(transformer);

        bannerPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateBannerIndicator(position);
                startBannerAutoScroll();
            }
        };
        bannerPager.registerOnPageChangeCallback(bannerPageChangeCallback);

        List<HomeRecommendation> banners = repository.getHomeRecommendations();
        if (banners == null || banners.isEmpty()) {
            banners = buildFallbackBanners();
        }
        bannerAdapter.setItems(banners);
        renderBannerIndicators(bannerAdapter.getItemCount());
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(reviewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0 || isLoading || isLastPage) {
                    return;
                }

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    loadCommunityReviews(false);
                }
            }
        });
    }

    private void setupRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.color_accent, R.color.color_frame);
        swipeRefreshLayout.setOnRefreshListener(() -> loadCommunityReviews(true));
    }

    private void loadCommunityReviews(boolean reset) {
        if (isLoading) {
            return;
        }
        isLoading = true;

        if (reset) {
            currentPage = 1;
            isLastPage = false;
            reviewAdapter.replaceItems(new ArrayList<>());
            allReviews = repository.getCommunityReviews();
            filteredReviews = applyFilter(allReviews, currentTag);
        }

        if (!swipeRefreshLayout.isRefreshing()) {
            progressIndicator.setVisibility(View.VISIBLE);
        }

        try {
            List<Review> pageReviews = getPageReviews(currentPage);
            if (reset) {
                reviewAdapter.replaceItems(pageReviews);
            } else {
                reviewAdapter.addItems(pageReviews);
            }

            if (reviewAdapter.getItemCount() == 0) {
                if (emptyView != null) {
                    emptyView.setVisibility(View.VISIBLE);
                }
            } else if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }

            if (pageReviews.isEmpty() || (currentPage * PAGE_SIZE) >= filteredReviews.size()) {
                isLastPage = true;
            } else {
                currentPage += 1;
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "社区内容加载失败", Toast.LENGTH_SHORT).show();
        } finally {
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            progressIndicator.setVisibility(View.GONE);
        }
    }

    private List<Review> getPageReviews(int page) {
        if (filteredReviews == null || filteredReviews.isEmpty()) {
            return new ArrayList<>();
        }
        int fromIndex = Math.max(0, (page - 1) * PAGE_SIZE);
        if (fromIndex >= filteredReviews.size()) {
            return new ArrayList<>();
        }
        int toIndex = Math.min(fromIndex + PAGE_SIZE, filteredReviews.size());
        return new ArrayList<>(filteredReviews.subList(fromIndex, toIndex));
    }

    private void applyTagSelection(@NonNull CommunityTag selectedTag) {
        if (chipGroup == null) {
            return;
        }
        Integer chipId = tagIdMap.get(selectedTag);
        if (chipId != null && chipGroup.getCheckedChipId() != chipId) {
            chipGroup.check(chipId);
        }
    }

    @Nullable
    private CommunityTag mapChipIdToTag(int chipId) {
        if (chipId == R.id.chipRecommend) {
            return CommunityTag.RECOMMEND;
        } else if (chipId == R.id.chipHandmade) {
            return CommunityTag.HANDMADE;
        } else if (chipId == R.id.chipFestival) {
            return CommunityTag.FESTIVAL;
        } else if (chipId == R.id.chipInheritor) {
            return CommunityTag.INHERITOR;
        }
        return null;
    }

    private List<Review> applyFilter(List<Review> source, CommunityTag tag) {
        if (source == null || source.isEmpty() || tag == CommunityTag.RECOMMEND) {
            return source == null ? new ArrayList<>() : new ArrayList<>(source);
        }
        List<Review> filtered = new ArrayList<>();
        for (Review review : source) {
            if (review != null && resolveCategory(review) == tag) {
                filtered.add(review);
            }
        }
        return filtered;
    }

    private CommunityTag resolveCategory(@NonNull Review review) {
        int productId = review.getProductId();
        switch (productId) {
            case 1001:
            case 1002:
            case 1007:
                return CommunityTag.HANDMADE;
            case 1003:
            case 1005:
            case 1008:
                return CommunityTag.FESTIVAL;
            case 1004:
            case 1006:
            case 1009:
                return CommunityTag.INHERITOR;
            default:
                return CommunityTag.RECOMMEND;
        }
    }

    private void renderBannerIndicators(int count) {
        if (bannerIndicator == null) {
            return;
        }
        bannerIndicator.removeAllViews();
        if (count <= 1) {
            bannerIndicator.setVisibility(View.GONE);
            return;
        }
        bannerIndicator.setVisibility(View.VISIBLE);
        int size = getResources().getDimensionPixelSize(R.dimen.banner_indicator_size);
        int spacing = getResources().getDimensionPixelSize(R.dimen.banner_indicator_spacing);
        for (int i = 0; i < count; i++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(spacing, 0, spacing, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.bg_banner_indicator_inactive);
            bannerIndicator.addView(dot);
        }
        updateBannerIndicator(0);
    }

    private void updateBannerIndicator(int position) {
        if (bannerIndicator == null || bannerIndicator.getChildCount() == 0) {
            return;
        }
        for (int i = 0; i < bannerIndicator.getChildCount(); i++) {
            View child = bannerIndicator.getChildAt(i);
            child.setBackgroundResource(i == position
                    ? R.drawable.bg_banner_indicator_active
                    : R.drawable.bg_banner_indicator_inactive);
        }
    }

    private void startBannerAutoScroll() {
        if (bannerAdapter == null || bannerAdapter.getItemCount() <= 1) {
            stopBannerAutoScroll();
            return;
        }
        bannerHandler.removeCallbacks(bannerAutoScrollRunnable);
        bannerHandler.postDelayed(bannerAutoScrollRunnable, BANNER_AUTO_SCROLL_DELAY_MS);
    }

    private void stopBannerAutoScroll() {
        bannerHandler.removeCallbacks(bannerAutoScrollRunnable);
    }

    private List<HomeRecommendation> buildFallbackBanners() {
        List<HomeRecommendation> fallback = new ArrayList<>();
        fallback.add(new HomeRecommendation(
                getString(R.string.explore_tab_recommend),
                getString(R.string.explore_tab_recommend),
                "3.12 - 4.02 · 乐山市非遗馆",
                R.drawable.home_banner_photo_1));
        fallback.add(new HomeRecommendation(
                getString(R.string.explore_tab_festival),
                getString(R.string.explore_tab_festival),
                "4.18 - 4.21 · 中区老街",
                R.drawable.home_banner_photo_2));
        fallback.add(new HomeRecommendation(
                getString(R.string.explore_tab_handmade),
                getString(R.string.explore_tab_handmade),
                "每周六 · 米嘎嘎文化站",
                R.drawable.home_banner_photo_3));
        return fallback;
    }

    private enum CommunityTag {
        RECOMMEND,
        HANDMADE,
        FESTIVAL,
        INHERITOR
    }
}
