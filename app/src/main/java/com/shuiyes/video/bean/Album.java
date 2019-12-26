package com.shuiyes.video.bean;

import java.util.List;

public class Album {

    private int order;
    private String title;
    private String summary;
    private String imgurl;
    private String playurl;
    private String albumUrl;
    private List<ListVideo> listVideos;

    public Album(int order, String title, String summary, String imgurl, String albumUrl, List<ListVideo> listVideos) {
        this.order = order;
        this.title = title;
        this.summary = summary;
        this.imgurl = imgurl;
        this.albumUrl = albumUrl;
        this.listVideos = listVideos;
        this.playurl = getListPlayurl();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getImgurl() {
        return imgurl;
    }

    public String getPlayurl() {
        return playurl;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public int getSize() {
        return listVideos != null ? listVideos.size() : 0;
    }

    public List<ListVideo> getListVideos() {
        return listVideos;
    }

    public void setListVideos(List<ListVideo> listVideos) {
        this.listVideos = listVideos;
        this.playurl = getListPlayurl();
    }

    private String getListPlayurl() {
        if (listVideos != null && listVideos.size() > 0) {
            ListVideo video = listVideos.get(0);
            // 一个视频不需要展示列表
            if (listVideos.size() == 1) {
                this.title = video.getText();
                listVideos.clear();
            }
            return video.getUrl();
        } else {
            return albumUrl;
        }
    }

    @Override
    public String toString() {
        return "Album [id=" + order + ", title='" + title + "', size=" + ((listVideos != null) ? listVideos.size() : 0) + ", url='" + playurl + "']";
    }
}