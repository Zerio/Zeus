package com.elfadili.wheresmyplaces.object;

import java.util.ArrayList;

public class PlaceDetailObject {
	private String address;
	private String phone;
	private String icon;
	private String name;
	private String website;
	private float rating;
	private ArrayList<UserReviewObject> listReviewObjects;
	private ArrayList<PlacePhotoObject> listPhotoObjects;
	private String url;

	public PlaceDetailObject() {

	}

	public PlaceDetailObject(String address, String phone, String icon, String name, String website, float rating, ArrayList<UserReviewObject> listReviewObjects) {
		this.address = address;
		this.phone = phone;
		this.icon = icon;
		this.name = name;
		this.website = website;
		this.rating = rating;
		this.listReviewObjects = listReviewObjects;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public ArrayList<UserReviewObject> getListReviewObjects() {
		return listReviewObjects;
	}

	public void setListReviewObjects(ArrayList<UserReviewObject> listReviewObjects) {
		this.listReviewObjects = listReviewObjects;
	}

	public ArrayList<PlacePhotoObject> getListPhotoObjects() {
		return listPhotoObjects;
	}

	public void setListPhotoObjects(ArrayList<PlacePhotoObject> listPhotoObjects) {
		this.listPhotoObjects = listPhotoObjects;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
