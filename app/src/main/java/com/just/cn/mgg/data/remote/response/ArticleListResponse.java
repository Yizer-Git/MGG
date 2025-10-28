package com.just.cn.mgg.data.remote.response;

import com.google.gson.annotations.SerializedName;
import com.just.cn.mgg.data.model.Article;

import java.util.List;

/**
 * 文章列表接口的分页响应
 */
public class ArticleListResponse {
    private List<Article> list;
    private long total;
    private int page;

    @SerializedName("page_size")
    private int pageSize;

    @SerializedName("total_pages")
    private int totalPages;

    public List<Article> getList() {
        return list;
    }

    public void setList(List<Article> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
