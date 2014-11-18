package com.elfadili.wheresmyplaces.object;

/**
 * 
 * UserReviewObject.java
 * 
 * @author :DOBAO
 * @Email :dotrungbao@gmail.com
 * @Skype :baopfiev_k50
 * @Phone :+84983028786
 * @Date :Nov 21, 2013
 * @project :WhereMyLocation
 * @Package :com.ypyproductions.wheremylocation.object
 */
public class UserReviewObject {

	private String authorName;
	private String authorUrl;
	private float rating;
	private String text;
	private long time;

	public UserReviewObject() {
		super();
	}

	public UserReviewObject(String authorName, String authorUrl, float rating, String text, long time) {
		super();
		this.authorName = authorName;
		this.authorUrl = authorUrl;
		this.rating = rating;
		this.text = text;
		this.time = time;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getAuthorUrl() {
		return authorUrl;
	}

	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}

}
