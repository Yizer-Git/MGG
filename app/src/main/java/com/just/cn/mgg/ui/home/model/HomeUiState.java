package com.just.cn.mgg.ui.home.model;

import com.just.cn.mgg.data.model.HomeRecommendation;
import com.just.cn.mgg.data.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable snapshot of the UI state for the home screen.
 */
public final class HomeUiState {

    private final boolean loading;
    private final List<Product> products;
    private final List<HomeRecommendation> recommendations;
    private final String errorMessage;

    private HomeUiState(boolean loading,
                        List<Product> products,
                        List<HomeRecommendation> recommendations,
                        String errorMessage) {
        this.loading = loading;
        this.products = products;
        this.recommendations = recommendations;
        this.errorMessage = errorMessage;
    }

    public static HomeUiState loading() {
        return new HomeUiState(true, Collections.emptyList(), Collections.emptyList(), null);
    }

    public static HomeUiState success(List<Product> products, List<HomeRecommendation> recommendations) {
        List<Product> productSnapshot = products == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(products));
        List<HomeRecommendation> recommendationSnapshot = recommendations == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(recommendations));
        return new HomeUiState(false, productSnapshot, recommendationSnapshot, null);
    }

    public static HomeUiState error(String message) {
        return new HomeUiState(false, Collections.emptyList(), Collections.emptyList(), message);
    }

    public boolean isLoading() {
        return loading;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<HomeRecommendation> getRecommendations() {
        return recommendations;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
