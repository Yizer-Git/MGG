package com.just.cn.mgg.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Database;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.just.cn.mgg.data.local.dao.AddressDao;
import com.just.cn.mgg.data.local.dao.ArticleDao;
import com.just.cn.mgg.data.local.dao.CartItemDao;
import com.just.cn.mgg.data.local.dao.CategoryDao;
import com.just.cn.mgg.data.local.dao.OrderDao;
import com.just.cn.mgg.data.local.dao.OrderItemDao;
import com.just.cn.mgg.data.local.dao.ProductDao;
import com.just.cn.mgg.data.local.dao.ReviewCommentDao;
import com.just.cn.mgg.data.local.dao.ReviewDao;
import com.just.cn.mgg.data.local.dao.ReviewLikeDao;
import com.just.cn.mgg.data.local.dao.UserDao;
import com.just.cn.mgg.data.local.entity.AddressEntity;
import com.just.cn.mgg.data.local.entity.ArticleEntity;
import com.just.cn.mgg.data.local.entity.CartItemEntity;
import com.just.cn.mgg.data.local.entity.CategoryEntity;
import com.just.cn.mgg.data.local.entity.OrderEntity;
import com.just.cn.mgg.data.local.entity.OrderItemEntity;
import com.just.cn.mgg.data.local.entity.ProductEntity;
import com.just.cn.mgg.data.local.entity.ReviewCommentEntity;
import com.just.cn.mgg.data.local.entity.ReviewEntity;
import com.just.cn.mgg.data.local.entity.ReviewLikeEntity;
import com.just.cn.mgg.data.local.entity.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                UserEntity.class,
                AddressEntity.class,
                CategoryEntity.class,
                ProductEntity.class,
                ArticleEntity.class,
                ReviewEntity.class,
                ReviewCommentEntity.class,
                ReviewLikeEntity.class,
                CartItemEntity.class,
                OrderEntity.class,
                OrderItemEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "migaga_local.db";
    private static final ExecutorService SEED_EXECUTOR = Executors.newSingleThreadExecutor();
    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public abstract AddressDao addressDao();

    public abstract CategoryDao categoryDao();

    public abstract ProductDao productDao();

    public abstract ArticleDao articleDao();

    public abstract ReviewDao reviewDao();

    public abstract ReviewCommentDao reviewCommentDao();

    public abstract ReviewLikeDao reviewLikeDao();

    public abstract CartItemDao cartItemDao();

    public abstract OrderDao orderDao();

    public abstract OrderItemDao orderItemDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME
                            )
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    SEED_EXECUTOR.execute(() -> DatabaseSeeder.seed(getInstance(context)));
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
