package com.just.cn.mgg.ui.main.category;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Article;
import com.just.cn.mgg.ui.adapter.ArticleAdapter;
import com.just.cn.mgg.ui.article.ArticleDetailActivity;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 专题页 Fragment，使用 TabLayout + ViewPager2 呈现不同分类。
 */
public class CategoryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private CategoryPagerAdapter pagerAdapter;
    private final List<CategoryTab> tabs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(view);
        setupTabs(view);
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbarCategory);
        if (toolbar == null) {
            return;
        }
        toolbar.setNavigationIcon(R.drawable.ic_nav_back_culture);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        toolbar.inflateMenu(R.menu.menu_category_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_category_share) {
                ToastUtils.show(requireContext(), getString(R.string.article_detail_share));
                return true;
            }
            return false;
        });
    }

    private void setupTabs(View view) {
        tabLayout = view.findViewById(R.id.tabCategory);
        viewPager = view.findViewById(R.id.viewPagerCategory);
        if (tabLayout == null || viewPager == null) {
            return;
        }

        tabs.clear();
        tabs.add(new CategoryTab(R.string.article_filter_all, null));
        tabs.add(new CategoryTab(R.string.article_filter_brand_story, "品牌故事"));
        tabs.add(new CategoryTab(R.string.article_filter_craft, "非遗技艺"));
        tabs.add(new CategoryTab(R.string.article_filter_trend, "国潮资讯"));

        pagerAdapter = new CategoryPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(tabs.size());

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(getString(tabs.get(position).titleRes))).attach();
    }

    private class CategoryPagerAdapter extends FragmentStateAdapter {

        CategoryPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            CategoryTab tab = tabs.get(position);
            return CategoryPageFragment.newInstance(tab.categoryTag);
        }

        @Override
        public int getItemCount() {
            return tabs.size();
        }
    }

    private static class CategoryTab {
        final int titleRes;
        final String categoryTag;

        CategoryTab(int titleRes, @Nullable String categoryTag) {
            this.titleRes = titleRes;
            this.categoryTag = categoryTag;
        }
    }

    /**
     * 单个分类页面。
     */
    public static class CategoryPageFragment extends Fragment {

        private static final String ARG_CATEGORY = "arg_category";
        private static final int PAGE_SIZE = 10;

        private String categoryTag;
        private LocalRepository repository;
        private ArticleAdapter articleAdapter;
        private NestedScrollView nestedScrollView;
        private RecyclerView recyclerView;
        private TextView emptyView;
        private CircularProgressIndicator progressIndicator;
        private ImageView bannerImage;
        private ImageView highlightIcon;
        private TextView highlightTitle;
        private TextView highlightDescription;
        private TextView newsDescription;

        private int currentPage = 1;
        private boolean isLoading = false;
        private boolean isLastPage = false;
        private List<Article> cachedArticles = new ArrayList<>();

        public static CategoryPageFragment newInstance(@Nullable String categoryTag) {
            CategoryPageFragment fragment = new CategoryPageFragment();
            Bundle args = new Bundle();
            args.putString(ARG_CATEGORY, categoryTag);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                categoryTag = getArguments().getString(ARG_CATEGORY);
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_category_page, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            repository = new LocalRepository(requireContext());
            initViews(view);
            initRecyclerView(view);
            loadArticles(true);
        }

        private void initViews(View root) {
            nestedScrollView = root.findViewById(R.id.nestedScrollCategory);
            recyclerView = root.findViewById(R.id.recyclerViewArticles);
            emptyView = root.findViewById(R.id.tvEmptyArticles);
            progressIndicator = root.findViewById(R.id.progressBarArticles);
            bannerImage = root.findViewById(R.id.ivTopicBanner);
            highlightIcon = root.findViewById(R.id.ivHighlightIcon);
            highlightTitle = root.findViewById(R.id.tvHighlightTitle);
            highlightDescription = root.findViewById(R.id.tvHighlightDescription);
            newsDescription = root.findViewById(R.id.tvNewsDescription);
            MaterialButton highlightMore = root.findViewById(R.id.btnHighlightMore);
            MaterialButton newsMore = root.findViewById(R.id.btnNewsMore);

            if (highlightMore != null) {
                highlightMore.setOnClickListener(v ->
                        ToastUtils.show(requireContext(), getString(R.string.feature_in_development)));
            }
            if (newsMore != null) {
                newsMore.setOnClickListener(v ->
                        ToastUtils.show(requireContext(), getString(R.string.feature_in_development)));
            }

            if (nestedScrollView != null) {
                nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                        (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                            if (!v.canScrollVertically(1) && !isLoading && !isLastPage) {
                                loadArticles(false);
                            }
                        });
            }
        }

        private void initRecyclerView(View root) {
            articleAdapter = new ArticleAdapter(requireContext());
            articleAdapter.setOnArticleClickListener(article ->
                    ArticleDetailActivity.start(requireContext(), article));

            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(articleAdapter);
        }

        private void loadArticles(boolean reset) {
            if (isLoading) {
                return;
            }
            isLoading = true;

            if (reset) {
                currentPage = 1;
                isLastPage = false;
                cachedArticles = repository.getArticles(categoryTag);
                articleAdapter.replaceItems(new ArrayList<>());
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
                if (hasData) {
                    updateHighlightAndBanner();
                } else {
                    resetHighlightFallback();
                }

                if (pageItems.isEmpty() || (currentPage * PAGE_SIZE) >= cachedArticles.size()) {
                    isLastPage = true;
                } else {
                    currentPage += 1;
                }
            } catch (Exception e) {
                ToastUtils.show(requireContext(), getString(R.string.article_load_failed));
                if (articleAdapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    resetHighlightFallback();
                }
            } finally {
                isLoading = false;
                progressIndicator.setVisibility(View.GONE);
            }
        }

        private List<Article> getPageItems(int page) {
            if (cachedArticles == null || cachedArticles.isEmpty()) {
                return new ArrayList<>();
            }
            int fromIndex = Math.max(0, (page - 1) * PAGE_SIZE);
            if (fromIndex >= cachedArticles.size()) {
                return new ArrayList<>();
            }
            int toIndex = Math.min(fromIndex + PAGE_SIZE, cachedArticles.size());
            return new ArrayList<>(cachedArticles.subList(fromIndex, toIndex));
        }

        private void updateHighlightAndBanner() {
            Article highlight = articleAdapter.getItem(0);
            if (highlight == null) {
                resetHighlightFallback();
                return;
            }

            highlightTitle.setText(highlight.getTitle());
            String summary = TextUtils.isEmpty(highlight.getSummary())
                    ? getString(R.string.topic_highlight_description)
                    : highlight.getSummary();
            highlightDescription.setText(summary);

            String author = TextUtils.isEmpty(highlight.getAuthor())
                    ? getString(R.string.brand_name)
                    : highlight.getAuthor();
            String date = highlight.getDisplayDate();
            String meta = getString(R.string.article_meta_format, author, date, highlight.getViewCount());
            newsDescription.setText(meta);

            String cover = highlight.getCoverImage();
            Glide.with(this)
                    .load(cover)
                    .placeholder(R.drawable.category_banner_photo)
                    .error(R.drawable.category_banner_photo)
                    .into(bannerImage);
            Glide.with(this)
                    .load(cover)
                    .placeholder(R.drawable.ic_heritage_inheritor)
                    .error(R.drawable.ic_heritage_inheritor)
                    .into(highlightIcon);
        }

        private void resetHighlightFallback() {
            highlightTitle.setText(getString(R.string.topic_highlight_title));
            highlightDescription.setText(getString(R.string.topic_highlight_description));
            newsDescription.setText(getString(R.string.topic_fast_news_placeholder));
            bannerImage.setImageResource(R.drawable.category_banner_photo);
            highlightIcon.setImageResource(R.drawable.ic_heritage_inheritor);
        }

        @Override
        public void onDestroyView() {
            recyclerView.setAdapter(null);
            super.onDestroyView();
        }
    }
}
