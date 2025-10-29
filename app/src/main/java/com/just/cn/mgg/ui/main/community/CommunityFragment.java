package com.just.cn.mgg.ui.main.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Review;
import com.just.cn.mgg.ui.review.ReviewAdapter;
import com.just.cn.mgg.ui.search.SearchActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 社区晒单 Fragment
 */
public class CommunityFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView galleryRecyclerView;
    private CircularProgressIndicator progressIndicator;
    private View emptyView;

    private final EnumMap<CommunityTag, TextView> tagViewMap = new EnumMap<>(CommunityTag.class);
    private CommunityTag currentTag = CommunityTag.RECOMMEND;

    private ReviewAdapter reviewAdapter;
    private CommunityGalleryAdapter galleryAdapter;
    private LocalRepository repository;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private List<Review> allReviews = new ArrayList<>();
    private List<Review> filteredReviews = new ArrayList<>();

    private static final int PAGE_SIZE = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupRefresh();

        repository = new LocalRepository(requireContext());
        loadCommunityReviews(true);
    }

    private void initViews(View root) {
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshCommunity);
        recyclerView = root.findViewById(R.id.recyclerViewCommunity);
        galleryRecyclerView = root.findViewById(R.id.rvCommunityGallery);
        progressIndicator = root.findViewById(R.id.progressCommunity);
        emptyView = root.findViewById(R.id.emptyCommunity);

        EditText searchInput = root.findViewById(R.id.etExploreSearch);
        View searchBar = root.findViewById(R.id.layoutExploreSearchBar);
        View.OnClickListener openSearch = v -> SearchActivity.startForCommunity(requireContext());
        if (searchInput != null) {
            searchInput.setFocusable(false);
            searchInput.setFocusableInTouchMode(false);
            searchInput.setOnClickListener(openSearch);
        }
        if (searchBar != null) {
            searchBar.setOnClickListener(openSearch);
        }

        tagViewMap.put(CommunityTag.RECOMMEND, root.findViewById(R.id.tvTagRecommend));
        tagViewMap.put(CommunityTag.HANDMADE, root.findViewById(R.id.tvTagHandmade));
        tagViewMap.put(CommunityTag.FESTIVAL, root.findViewById(R.id.tvTagFestival));
        tagViewMap.put(CommunityTag.INHERITOR, root.findViewById(R.id.tvTagInheritor));

        View.OnClickListener tagClickListener = v -> {
            CommunityTag tag = mapViewIdToTag(v.getId());
            if (tag != null) {
                onTagSelected(tag);
            }
        };
        for (Map.Entry<CommunityTag, TextView> entry : tagViewMap.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().setOnClickListener(tagClickListener);
            }
        }
        applyTagSelection(currentTag);

        reviewAdapter = new ReviewAdapter(requireContext());
        reviewAdapter.setOnReviewClickListener(review ->
                startActivity(ReviewDetailActivity.createIntent(requireContext(), review.getReviewId())));
        setupGallery();
    }

    private void setupGallery() {
        if (galleryRecyclerView == null) {
            return;
        }
        galleryAdapter = new CommunityGalleryAdapter(getGalleryImages());
        LinearLayoutManager galleryLayoutManager = new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false);
        galleryRecyclerView.setLayoutManager(galleryLayoutManager);
        galleryRecyclerView.setAdapter(galleryAdapter);
        galleryRecyclerView.setHasFixedSize(true);
    }

    private List<Integer> getGalleryImages() {
        return Arrays.asList(
                R.drawable.community_banner_photo,
                R.drawable.community_gallery_photo,
                R.drawable.category_gallery_photo,
                R.drawable.article_header_photo,
                R.drawable.profile_story_photo
        );
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
                emptyView.setVisibility(View.VISIBLE);
            } else {
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

    private void onTagSelected(@NonNull CommunityTag tag) {
        if (tag == currentTag) {
            return;
        }
        currentTag = tag;
        applyTagSelection(tag);
        loadCommunityReviews(true);
    }

    private void applyTagSelection(@NonNull CommunityTag selectedTag) {
        for (Map.Entry<CommunityTag, TextView> entry : tagViewMap.entrySet()) {
            TextView view = entry.getValue();
            if (view == null) {
                continue;
            }
            boolean isSelected = entry.getKey() == selectedTag;
            view.setBackgroundResource(isSelected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
            int colorRes = isSelected ? R.color.color_surface_main : R.color.text_secondary;
            view.setTextColor(ContextCompat.getColor(requireContext(), colorRes));
        }
    }

    @Nullable
    private CommunityTag mapViewIdToTag(int viewId) {
        if (viewId == R.id.tvTagRecommend) {
            return CommunityTag.RECOMMEND;
        } else if (viewId == R.id.tvTagHandmade) {
            return CommunityTag.HANDMADE;
        } else if (viewId == R.id.tvTagFestival) {
            return CommunityTag.FESTIVAL;
        } else if (viewId == R.id.tvTagInheritor) {
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

    private enum CommunityTag {
        RECOMMEND,
        HANDMADE,
        FESTIVAL,
        INHERITOR
    }
}
