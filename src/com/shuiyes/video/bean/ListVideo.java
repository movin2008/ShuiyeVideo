package com.shuiyes.video.bean;

public class ListVideo {

	private int id;
	private String title;
	private String url;
	
	public ListVideo(int id, String title, String url) {
		this.id = id;
		this.title = title;
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		return "ListVideo [id=" + id + ", title='" + title + "', url='" + url + "']";
	}

}
