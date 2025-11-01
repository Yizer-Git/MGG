package com.just.cn.mgg.domain.repository;

import com.just.cn.mgg.data.model.HomeRecommendation;
import com.just.cn.mgg.data.model.Product;

import java.util.List;

/**
 * Contract for fetching home screen data synchronously.
 */
public interface HomeRepository {

    List<Product> getHotProducts(int limit);

    List<HomeRecommendation> getRecommendations();
}
