package com.just.cn.mgg.ui.main.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Review;
import com.just.cn.mgg.data.model.ReviewComment;
import com.just.cn.mgg.ui.review.ReviewCommentAdapter;
import com.just.cn.mgg.ui.review.ReviewImageAdapter;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.List;

/**
 * 社区评价详情页，支持点赞与评论
 */
public class ReviewDetailActivity extends AppCompatActivity {

    private static final String EXTRA_REVIEW_ID = "extra_review_id";

    public static Intent createIntent(Context context, long reviewId) {
        Intent intent = new Intent(context, ReviewDetailActivity.class);
        intent.putExtra(EXTRA_REVIEW_ID, reviewId);
        return intent;
    }

    private long reviewId;
    private LocalRepository repository;

    private ShapeableImageView imgAvatar;
    private TextView tvUserName;
    private TextView tvCreatedAt;
    private RatingBar ratingBar;
    private TextView tvContent;
    private RecyclerView rvImages;
    private TextView tvLikeCount;
    private TextView tvCommentCount;
    private TextView tvCommentEmpty;
    private RecyclerView rvComments;
    private ImageButton btnLike;
    private EditText etCommentInput;
    private TextView btnSend;
    private CircularProgressIndicator progressIndicator;

    private ReviewImageAdapter imageAdapter;
    private ReviewCommentAdapter commentAdapter;
    private Review currentReview;
    private boolean likedByUser = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        reviewId = getIntent().getLongExtra(EXTRA_REVIEW_ID, 0L);
        if (reviewId == 0L) {
            finish();
            return;
        }
        repository = new LocalRepository(this);

        initViews();
        loadReviewDetail();
        loadComments();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarReviewDetail);
        toolbar.setNavigationOnClickListener(v -> finish());

        imgAvatar = findViewById(R.id.imgReviewAvatar);
        tvUserName = findViewById(R.id.tvReviewUserName);
        tvCreatedAt = findViewById(R.id.tvReviewCreatedAt);
        ratingBar = findViewById(R.id.ratingReview);
        tvContent = findViewById(R.id.tvReviewContent);
        rvImages = findViewById(R.id.rvReviewImages);
        tvLikeCount = findViewById(R.id.tvReviewLikeCount);
        tvCommentCount = findViewById(R.id.tvReviewCommentCount);
        tvCommentEmpty = findViewById(R.id.tvCommentEmpty);
        rvComments = findViewById(R.id.rvReviewComments);
        btnLike = findViewById(R.id.btnLike);
        etCommentInput = findViewById(R.id.etCommentInput);
        btnSend = findViewById(R.id.btnSendComment);
        progressIndicator = findViewById(R.id.progressReviewDetail);

        imageAdapter = new ReviewImageAdapter(this);
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        rvImages.setAdapter(imageAdapter);
        rvImages.setNestedScrollingEnabled(false);

        commentAdapter = new ReviewCommentAdapter(this);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);
        rvComments.setNestedScrollingEnabled(false);

        btnLike.setOnClickListener(v -> toggleLike());
        btnSend.setOnClickListener(v -> submitComment());
        etCommentInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                submitComment();
                return true;
            }
            return false;
        });
    }

    private void loadReviewDetail() {
        progressIndicator.setVisibility(android.view.View.VISIBLE);
        currentReview = repository.getReviewDetail(reviewId);
        if (currentReview == null) {
            ToastUtils.show(this, getString(R.string.review_not_found));
            finish();
            return;
        }

        Review.ReviewUser user = currentReview.getUser();
        if (user != null) {
            tvUserName.setText(!TextUtils.isEmpty(user.getNickname()) ? user.getNickname() : getString(R.string.review_user_default));
            Glide.with(this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.profile_header_photo)
                    .error(R.drawable.profile_header_photo)
                    .into(imgAvatar);
        } else {
            tvUserName.setText(getString(R.string.review_user_default));
            imgAvatar.setImageResource(R.drawable.profile_header_photo);
        }

        tvCreatedAt.setText(currentReview.getCreatedAt());
        ratingBar.setRating(currentReview.getRating());
        tvContent.setText(currentReview.getContent());

        List<String> images = currentReview.getImages();
        if (images == null || images.isEmpty()) {
            rvImages.setVisibility(android.view.View.GONE);
        } else {
            rvImages.setVisibility(android.view.View.VISIBLE);
            imageAdapter.setImages(images);
        }

        updateStats();
        updateLikeState();
        progressIndicator.setVisibility(android.view.View.GONE);
    }

    private void loadComments() {
        List<ReviewComment> comments = repository.getReviewComments(reviewId);
        commentAdapter.setItems(comments);
        tvCommentEmpty.setVisibility(comments == null || comments.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void updateStats() {
        if (currentReview == null) return;
        tvLikeCount.setText(getString(R.string.review_like_format, currentReview.getLikeCount()));
        tvCommentCount.setText(getString(R.string.review_comment_format, currentReview.getCommentCount()));
    }

    private void updateLikeState() {
        int userId = SPUtils.getInt(this, Constants.KEY_USER_ID);
        likedByUser = repository.hasUserLikedReview(reviewId, userId);
        btnLike.setColorFilter(ContextCompat.getColor(this, likedByUser ? R.color.color_accent : R.color.text_secondary));
    }

    private void toggleLike() {
        if (!SPUtils.isLogin(this)) {
            ToastUtils.show(this, getString(R.string.review_like_require_login));
            return;
        }
        int userId = SPUtils.getInt(this, Constants.KEY_USER_ID);
        boolean nowLiked = repository.toggleReviewLike(reviewId, userId);
        likedByUser = nowLiked;
        int likeCount = currentReview.getLikeCount() + (nowLiked ? 1 : -1);
        if (likeCount < 0) likeCount = 0;
        currentReview.setLikeCount(likeCount);
        updateStats();
        btnLike.setColorFilter(ContextCompat.getColor(this, nowLiked ? R.color.color_accent : R.color.text_secondary));
    }

    private void submitComment() {
        if (!SPUtils.isLogin(this)) {
            ToastUtils.show(this, getString(R.string.review_comment_require_login));
            return;
        }
        String content = etCommentInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.show(this, getString(R.string.review_comment_empty_error));
            return;
        }

        int userId = SPUtils.getInt(this, Constants.KEY_USER_ID);
        try {
            ReviewComment comment = repository.addReviewComment(reviewId, userId, content);
            if (comment != null) {
                commentAdapter.addItem(comment);
                etCommentInput.setText("");
                hideKeyboard();
                currentReview.setCommentCount(currentReview.getCommentCount() + 1);
                updateStats();
                tvCommentEmpty.setVisibility(android.view.View.GONE);
                ToastUtils.show(this, getString(R.string.review_comment_success));
            }
        } catch (Exception e) {
            ToastUtils.show(this, e.getMessage());
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etCommentInput.getWindowToken(), 0);
        }
    }
}
