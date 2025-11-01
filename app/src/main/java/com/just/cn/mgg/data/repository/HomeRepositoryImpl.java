package com.just.cn.mgg.data.repository;

import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.HomeRecommendation;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.domain.repository.HomeRepository;

import java.util.List;

/**
 * Returns home data backed by the {@link LocalRepository}.
 */
public class HomeRepositoryImpl implements HomeRepository {

    private final LocalRepository localRepository;

    public HomeRepositoryImpl(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }

    @Override
    public List<Product> getHotProducts(int limit) {
        return localRepository.getHotProducts(limit);
    }

    @Override
    public List<HomeRecommendation> getRecommendations() {
        return localRepository.getHomeRecommendations();
    }
}
