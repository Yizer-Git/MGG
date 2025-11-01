package com.just.cn.mgg.data.model;

import java.io.Serializable;

/**
 * 首页推荐展览数据模型。
 */
public class HomeRecommendation implements Serializable {
    private String title;
    private String tag;
    private String meta;
    private String cover;
    private int coverRes;

    public HomeRecommendation(String title, String tag, String meta, String cover) {
        this.title = title;
        this.tag = tag;
        this.meta = meta;
        this.cover = cover;
        this.coverRes = 0;
    }

    public HomeRecommendation(String title, String tag, String meta, int coverRes) {
        this.title = title;
        this.tag = tag;
        this.meta = meta;
        this.coverRes = coverRes;
        this.cover = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getCoverRes() {
        return coverRes;
    }

    public void setCoverRes(int coverRes) {
        this.coverRes = coverRes;
    }
}
