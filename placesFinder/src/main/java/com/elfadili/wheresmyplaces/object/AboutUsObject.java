package com.elfadili.wheresmyplaces.object;

public class AboutUsObject {
	private int iconRes;
	private String link;
	private String title;
	private String content;
	
	public AboutUsObject(int iconRes, String link, String title, String content) {
		super();
		this.iconRes = iconRes;
		this.link = link;
		this.title = title;
		this.content = content;
	}

	public int getIconRes() {
		return iconRes;
	}

	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
