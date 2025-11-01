package com.just.cn.mgg.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.just.cn.mgg.core.coroutines.AppDispatchers;
import com.just.cn.mgg.data.model.HomeRecommendation;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.domain.repository.HomeRepository;
import com.just.cn.mgg.ui.home.model.HomeUiState;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * Home screen ViewModel that fetches data on background executors and exposes LiveData for the UI.
 */
@HiltViewModel
public class HomeViewModel extends ViewModel {

    private static final int HOT_PRODUCT_LIMIT = 20;

    private final HomeRepository repository;
    private final AppDispatchers dispatchers;
    private final MutableLiveData<HomeUiState> uiState = new MutableLiveData<>(HomeUiState.loading());

    @Inject
    public HomeViewModel(HomeRepository repository, AppDispatchers dispatchers) {
        this.repository = repository;
        this.dispatchers = dispatchers;
        observeData();
    }

    public LiveData<HomeUiState> getUiState() {
        return uiState;
    }

    private void observeData() {
        dispatchers.getIo().execute(() -> {
            uiState.postValue(HomeUiState.loading());
            try {
                List<Product> products = repository.getHotProducts(HOT_PRODUCT_LIMIT);
                List<HomeRecommendation> recommendations = repository.getRecommendations();
                uiState.postValue(HomeUiState.success(products, recommendations));
            } catch (Exception exception) {
                uiState.postValue(HomeUiState.error(
                        exception.getMessage() != null ? exception.getMessage() : "加载首页数据失败"
                ));
            }
        });
    }
}
