package com.just.cn.mgg;

import android.content.Context;

import com.just.cn.mgg.core.coroutines.AppDispatchers;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.repository.HomeRepositoryImpl;
import com.just.cn.mgg.domain.repository.HomeRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public final class AppModule {

    private AppModule() {
    }

    @Provides
    @Singleton
    static AppDispatchers provideAppDispatchers() {
        return AppDispatchers.defaultDispatchers();
    }

    @Provides
    @Singleton
    static LocalRepository provideLocalRepository(@ApplicationContext Context context) {
        return new LocalRepository(context);
    }

    @Provides
    @Singleton
    static HomeRepository provideHomeRepository(LocalRepository localRepository) {
        return new HomeRepositoryImpl(localRepository);
    }
}
