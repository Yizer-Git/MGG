package com.just.cn.mgg.ui.main.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Review;
import com.just.cn.mgg.ui.review.ReviewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 社区晒单 Fragment
 */
public class CommunityFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView galleryRecyclerView;
    private CircularProgressIndicator progressIndicator;
    private View emptyView;

    private ReviewAdapter reviewAdapter;
    private CommunityGalleryAdapter galleryAdapter;
    private LocalRepository repository;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private List<Review> cachedReviews = new ArrayList<>();

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

        reviewAdapter = new ReviewAdapter(requireContext());
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
            cachedReviews = repository.getCommunityReviews();
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

            if (pageReviews.isEmpty() || (currentPage * PAGE_SIZE) >= cachedReviews.size()) {
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
        if (cachedReviews == null || cachedReviews.isEmpty()) {
            return new ArrayList<>();
        }
        int fromIndex = Math.max(0, (page - 1) * PAGE_SIZE);
        if (fromIndex >= cachedReviews.size()) {
            return new ArrayList<>();
        }
        int toIndex = Math.min(fromIndex + PAGE_SIZE, cachedReviews.size());
        return new ArrayList<>(cachedReviews.subList(fromIndex, toIndex));
    }
}
