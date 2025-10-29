package com.just.cn.mgg.ui.main.category;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Article;
import com.just.cn.mgg.ui.adapter.ArticleAdapter;
import com.just.cn.mgg.ui.article.ArticleDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 非遗文化文章列表
 */
public class CategoryFragment extends Fragment {

    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private CircularProgressIndicator progressIndicator;
    private ChipGroup chipGroup;

    private ArticleAdapter articleAdapter;
    private LinearLayoutManager layoutManager;
    private LocalRepository repository;

    private String currentCategory;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private List<Article> cachedArticles = new ArrayList<>();

    private static final int PAGE_SIZE = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initRecyclerView();
        initChips();

        repository = new LocalRepository(requireContext());
        loadArticles(true);
    }

    private void initViews(View root) {
        nestedScrollView = root.findViewById(R.id.nestedScrollCategory);
        recyclerView = root.findViewById(R.id.recyclerViewArticles);
        emptyView = root.findViewById(R.id.tvEmptyArticles);
        progressIndicator = root.findViewById(R.id.progressBarArticles);
        chipGroup = root.findViewById(R.id.chipGroupCategories);

        View backButton = root.findViewById(R.id.btnTopicBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                    (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        if (!v.canScrollVertically(1) && !isLoading && !isLastPage) {
                            loadArticles(false);
                        }
                    });
        }

        articleAdapter = new ArticleAdapter(requireContext());
        articleAdapter.setOnArticleClickListener(this::navigateToArticleDetail);
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(articleAdapter);
    }

    private void initChips() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            String newCategory = null;
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null) {
                    Object tag = chip.getTag();
                    if (tag != null) {
                        newCategory = TextUtils.isEmpty(tag.toString()) ? null : tag.toString();
                    }
                }
            }

            if (!Objects.equals(currentCategory, newCategory)) {
                currentCategory = newCategory;
                loadArticles(true);
            }
        });
    }

    private void navigateToArticleDetail(Article article) {
        if (article == null) {
            return;
        }
        ArticleDetailActivity.start(requireContext(), article);
    }

    private void loadArticles(boolean reset) {
        if (isLoading) {
            return;
        }
        isLoading = true;

        if (reset) {
            currentPage = 1;
            isLastPage = false;
            cachedArticles = repository.getArticles(currentCategory);
            articleAdapter.replaceItems(Collections.emptyList());
        }

        progressIndicator.setVisibility(View.VISIBLE);

        try {
            List<Article> pageItems = getPageItems(currentPage);
            if (reset) {
                articleAdapter.replaceItems(pageItems);
            } else {
                articleAdapter.appendItems(pageItems);
            }

            boolean hasData = articleAdapter.getItemCount() > 0;
            emptyView.setVisibility(hasData ? View.GONE : View.VISIBLE);

            if (pageItems.isEmpty() || (currentPage * PAGE_SIZE) >= cachedArticles.size()) {
                isLastPage = true;
            } else {
                currentPage += 1;
            }
        } catch (Exception e) {
            showLoadError();
        } finally {
            isLoading = false;
            progressIndicator.setVisibility(View.GONE);
        }
    }

    private List<Article> getPageItems(int page) {
        if (cachedArticles == null || cachedArticles.isEmpty()) {
            return Collections.emptyList();
        }
        int fromIndex = Math.max(0, (page - 1) * PAGE_SIZE);
        if (fromIndex >= cachedArticles.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + PAGE_SIZE, cachedArticles.size());
        return new ArrayList<>(cachedArticles.subList(fromIndex, toIndex));
    }

    private void showLoadError() {
        if (getContext() != null) {
            Toast.makeText(getContext(), R.string.article_load_failed, Toast.LENGTH_SHORT).show();
        }
        if (articleAdapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        recyclerView.setAdapter(null);
        super.onDestroyView();
    }
}

