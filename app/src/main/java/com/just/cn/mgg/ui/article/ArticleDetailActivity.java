package com.just.cn.mgg.ui.article;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Article;

public class ArticleDetailActivity extends AppCompatActivity {

    private static final String EXTRA_ARTICLE_ID = "extra_article_id";
    private static final String EXTRA_ARTICLE = "extra_article";

    private MaterialToolbar toolbar;
    private WebView webView;
    private CircularProgressIndicator progressIndicator;

    private LocalRepository repository;
    private Article article;
    private long articleId = -1L;

    public static void start(Context context, Article article) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        if (!(context instanceof android.app.Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (article != null) {
            intent.putExtra(EXTRA_ARTICLE_ID, article.getArticleId());
            intent.putExtra(EXTRA_ARTICLE, article);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        toolbar = findViewById(R.id.toolbarArticleDetail);
        webView = findViewById(R.id.webViewArticle);
        progressIndicator = findViewById(R.id.progressArticleDetail);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.inflateMenu(R.menu.menu_article_detail);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_share) {
                shareArticle();
                return true;
            }
            return false;
        });

        setupWebView();

        Article cachedArticle = (Article) getIntent().getSerializableExtra(EXTRA_ARTICLE);
        if (cachedArticle != null) {
            article = cachedArticle;
            articleId = cachedArticle.getArticleId();
            renderArticle(cachedArticle);
        } else {
            articleId = getIntent().getLongExtra(EXTRA_ARTICLE_ID, -1L);
        }

        repository = new LocalRepository(this);

        if (articleId > 0) {
            loadArticleDetail(articleId);
        } else if (article == null) {
            Toast.makeText(this, R.string.article_load_failed, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupWebView() {
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setVerticalScrollBarEnabled(false);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
    }

    private void loadArticleDetail(long id) {
        if (article == null) {
            progressIndicator.setVisibility(android.view.View.VISIBLE);
        }
        Article detail = repository.getArticleDetail(id);
        progressIndicator.setVisibility(android.view.View.GONE);
        if (detail != null) {
            article = detail;
            renderArticle(detail);
        } else if (article != null) {
            renderArticle(article);
        } else {
            showLoadError();
        }
    }

    private void renderArticle(@NonNull Article article) {
        toolbar.setTitle(article.getTitle());
        String html = buildHtmlTemplate(article);
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    private String buildHtmlTemplate(Article article) {
        String title = safeText(article.getTitle());
        String author = safeText(TextUtils.isEmpty(article.getAuthor()) ? getString(R.string.brand_name) : article.getAuthor());
        String date = safeText(article.getDisplayDate());
        String views = String.valueOf(article.getViewCount());
        String cover = TextUtils.isEmpty(article.getCoverImage()) ? "" :
                "<img class=\"cover\" src=\"" + article.getCoverImage() + "\" alt=\"cover\" />";
        String content = article.getContent();
        if (TextUtils.isEmpty(content)) {
            String summary = safeText(article.getSummary());
            content = TextUtils.isEmpty(summary) ? "" : "<p>" + summary + "</p>";
        }

        return "<html><head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />"
                + "<style>"
                + "body{font-family:'Noto Sans SC',sans-serif;background-color:#FDFDFD;color:#333;margin:0;padding:0;}"
                + ".container{padding:16px;margin:0 auto;max-width:820px;}"
                + ".cover{width:100%;border-radius:20px;object-fit:cover;margin-bottom:18px;}"
                + ".title{font-size:26px;font-weight:700;line-height:1.35;margin:0 0 12px 0;color:#A4393C;}"
                + ".meta{font-size:14px;color:#888;margin-bottom:18px;display:flex;gap:8px;flex-wrap:wrap;}"
                + ".meta span{background:#F7F2EC;padding:6px 12px;border-radius:999px;color:#A4393C;}"
                + ".content{font-size:16px;line-height:1.75;color:#444;}"
                + ".content img{max-width:100%;border-radius:16px;margin:12px 0;}"
                + ".content h2,.content h3{color:#A4393C;}"
                + ".content strong{color:#9E2F33;}"
                + "</style></head><body>"
                + "<div class=\"container\">"
                + cover
                + "<h1 class=\"title\">" + title + "</h1>"
                + "<div class=\"meta\">"
                + "<span>" + author + "</span>"
                + "<span>" + date + "</span>"
                + "<span>浏览 " + views + "</span>"
                + "</div>"
                + "<div class=\"content\">" + content + "</div>"
                + "</div></body></html>";
    }

    private String safeText(String input) {
        return input == null ? "" : input.replace("'", "&#39;");
    }

    private void shareArticle() {
        if (article == null) {
            Toast.makeText(this, R.string.article_load_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        String shareText = getString(R.string.article_detail_share_text, article.getTitle());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, getString(R.string.article_detail_share)));
    }

    private void showLoadError() {
        Toast.makeText(this, R.string.article_load_failed, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
        }
        super.onDestroy();
    }
}
