package com.just.cn.mgg.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.data.model.Review;
import com.just.cn.mgg.ui.adapter.ProductAdapter;
import com.just.cn.mgg.ui.main.community.ReviewDetailActivity;
import com.just.cn.mgg.ui.product.ProductDetailActivity;
import com.just.cn.mgg.ui.review.ReviewAdapter;
import com.just.cn.mgg.utils.ToastUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;

/**
 * 通用搜索页，根据入口展示商品或社区内容
 */
public class SearchActivity extends AppCompatActivity {

    private static final String EXTRA_SCOPE = "extra_scope";
    private static final String EXTRA_INITIAL_QUERY = "extra_initial_query";

    public static final int SCOPE_PRODUCT = 0;
    public static final int SCOPE_COMMUNITY = 1;

    @IntDef({SCOPE_PRODUCT, SCOPE_COMMUNITY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SearchScope {}

    private EditText etSearchInput;
    private ImageButton btnClear;
    private TextView btnSearchAction;
    private TextView tvHint;
    private TextView tvEmpty;
    private RecyclerView recyclerView;
    private View progressIndicator;

    private LocalRepository repository;
    private ProductAdapter productAdapter;
    private ReviewAdapter reviewAdapter;
    private @SearchScope int scope = SCOPE_PRODUCT;

    public static void startForProducts(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_SCOPE, SCOPE_PRODUCT);
        context.startActivity(intent);
    }

    public static void startForCommunity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_SCOPE, SCOPE_COMMUNITY);
        context.startActivity(intent);
    }

    public static void startForCommunity(Context context, String keyword) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_SCOPE, SCOPE_COMMUNITY);
        intent.putExtra(EXTRA_INITIAL_QUERY, keyword);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        scope = getIntent().getIntExtra(EXTRA_SCOPE, SCOPE_PRODUCT);
        repository = new LocalRepository(this);

        initViews();
        setupRecyclerView();
        setupListeners();

        String initialQuery = getIntent().getStringExtra(EXTRA_INITIAL_QUERY);
        if (!TextUtils.isEmpty(initialQuery)) {
            etSearchInput.setText(initialQuery);
            etSearchInput.setSelection(initialQuery.length());
            performSearch(initialQuery);
        } else {
            showKeyboard();
        }
    }

    private void initViews() {
        etSearchInput = findViewById(R.id.etSearchInput);
        btnClear = findViewById(R.id.btnClearInput);
        btnSearchAction = findViewById(R.id.btnSearchAction);
        tvHint = findViewById(R.id.tvSearchHint);
        tvEmpty = findViewById(R.id.tvSearchEmpty);
        recyclerView = findViewById(R.id.rvSearchResult);
        progressIndicator = findViewById(R.id.progressSearch);
        ImageButton btnBack = findViewById(R.id.btnSearchBack);
        btnBack.setOnClickListener(v -> finish());

        if (scope == SCOPE_PRODUCT) {
            etSearchInput.setHint(R.string.search_hint_product);
            tvHint.setText(R.string.search_hint_product);
        } else {
            etSearchInput.setHint(R.string.search_hint_community);
            tvHint.setText(R.string.search_hint_community);
        }
    }

    private void setupRecyclerView() {
        if (scope == SCOPE_PRODUCT) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            productAdapter = new ProductAdapter(this);
            productAdapter.setOnItemClickListener(this::openProductDetail);
            recyclerView.setAdapter(productAdapter);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            reviewAdapter = new ReviewAdapter(this);
            reviewAdapter.setOnReviewClickListener(this::openReviewDetail);
            recyclerView.setAdapter(reviewAdapter);
        }
    }

    private void setupListeners() {
        btnSearchAction.setOnClickListener(v -> triggerSearch());
        btnClear.setOnClickListener(v -> {
            etSearchInput.setText("");
            tvEmpty.setVisibility(View.GONE);
             tvHint.setVisibility(View.VISIBLE);
            if (scope == SCOPE_PRODUCT && productAdapter != null) {
                productAdapter.setProductList(Collections.emptyList());
            } else if (reviewAdapter != null) {
                reviewAdapter.replaceItems(Collections.emptyList());
            }
        });

        etSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                triggerSearch();
                return true;
            }
            return false;
        });

        etSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void triggerSearch() {
        String keyword = etSearchInput.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            ToastUtils.show(this, getString(R.string.search_input_empty));
            return;
        }
        performSearch(keyword);
        hideKeyboard();
    }

    private void performSearch(String keyword) {
        progressIndicator.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        tvHint.setVisibility(View.GONE);

        if (scope == SCOPE_PRODUCT) {
            List<Product> products = repository.searchProducts(keyword);
            if (productAdapter != null) {
                productAdapter.setProductList(products);
            }
            tvEmpty.setVisibility(products == null || products.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            List<Review> reviews = repository.searchCommunityReviews(keyword);
            if (reviewAdapter != null) {
                reviewAdapter.replaceItems(reviews);
            }
            tvEmpty.setVisibility(reviews == null || reviews.isEmpty() ? View.VISIBLE : View.GONE);
        }

        progressIndicator.setVisibility(View.GONE);
    }

    private void openProductDetail(Product product) {
        if (product == null) {
            return;
        }
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getProductId());
        startActivity(intent);
    }

    private void openReviewDetail(Review review) {
        if (review == null) {
            return;
        }
        Intent intent = ReviewDetailActivity.createIntent(this, review.getReviewId());
        startActivity(intent);
    }

    private void showKeyboard() {
        etSearchInput.requestFocus();
        etSearchInput.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSearchInput, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 150);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearchInput.getWindowToken(), 0);
        }
    }
}
