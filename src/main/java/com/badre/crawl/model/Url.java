package com.badre.crawl.model;

public class Url {

	private String url;
	private int level;

	public Url(String url, int level) {
		super();
		this.url = url;
		this.level = level;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "Url [url=" + url + ", level=" + level + "]";
	}

}
