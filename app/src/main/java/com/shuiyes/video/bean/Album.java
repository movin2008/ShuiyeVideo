package com.shuiyes.video.bean;

import java.util.List;

public class Album {

	private int order;
	private String title;
	private String summary;
	private String imgurl;
	private String playurl;
	private List<ListVideo> listVideos;
	
	public Album(int order, String title, String summary, String imgurl, String playurl, List<ListVideo> listVideos) {
		this.order = order;
		this.title = title;
		this.summary = summary;
		this.imgurl = imgurl;
		this.playurl = playurl;
		this.listVideos = listVideos;
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

	public void setSummary(String summary) {
		this.summary = summary;
	}

	

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getPlayurl() {
		return playurl;
	}

	public void setPlayurl(String playurl) {
		this.playurl = playurl;
	}

	public List<ListVideo> getListVideos() {
		return listVideos;
	}

	public void setListVideos(List<ListVideo> listVideos) {
		this.listVideos = listVideos;
	}
	
}