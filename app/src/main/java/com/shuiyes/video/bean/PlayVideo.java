package com.shuiyes.video.bean;

public class PlayVideo {

    protected String text;
    protected String url;

    public PlayVideo(String text, String url) {
        this.text = text;
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toStr() {
        return "PlayVideo{" + text
//                + ", url='" + url + '\''
                + '}';
    }
}
