package com.shuiyes.video.bean;

public class ListVideo {

    private String text;
    private String title;
    private String url;

    public ListVideo(int id, String title, String url) {
        this(id + "", title, url);
    }

    public ListVideo(String text, String title, String url) {
        this.text = text;
        this.title = title;
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ListVideo [text=" + text + ", title='" + title + "', url='" + url + "']";
    }

}
