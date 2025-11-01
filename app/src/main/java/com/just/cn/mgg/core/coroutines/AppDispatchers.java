package com.just.cn.mgg.core.coroutines;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple holder for application-wide executors so we can inject thread pools where needed.
 */
public final class AppDispatchers {

    private final ExecutorService io;
    private final ExecutorService computation;
    private final Executor main;

    public AppDispatchers(ExecutorService io, ExecutorService computation, Executor main) {
        this.io = io;
        this.computation = computation;
        this.main = main;
    }

    public ExecutorService getIo() {
        return io;
    }

    public ExecutorService getComputation() {
        return computation;
    }

    public Executor getMain() {
        return main;
    }

    public static AppDispatchers defaultDispatchers() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ExecutorService IO_EXECUTOR = Executors.newFixedThreadPool(3);
        private static final ExecutorService COMPUTATION_EXECUTOR = Executors.newCachedThreadPool();
        private static final Executor MAIN_EXECUTOR = new MainThreadExecutor();
        private static final AppDispatchers INSTANCE =
                new AppDispatchers(IO_EXECUTOR, COMPUTATION_EXECUTOR, MAIN_EXECUTOR);
    }

    private static final class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }
}
