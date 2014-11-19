package com.elfadili.wheresmyplaces.object;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.StringUtils;

public class PlaceObject {

	private String id;
	private String icon;
	private Location location;
	private String name;
	private String vicinity;
	private float distance;
	private float rating;
	private String referenceToken;
	private PlaceDetailObject placeDetailObject;
	private ArrayList<PlacePhotoObject> listPhotoObjects;
	private RouteObject routeObject;
	private String photoRef;
	private String category;

	public PlaceObject() {
		super();
	}

	public PlaceObject(String id, String icon, Location location, String name, String vicinity, float distance, float rating, String referenceToken) {
		this.id = id;
		this.icon = icon;
		this.location = location;
		this.name = name;
		this.vicinity = vicinity;
		this.distance = distance;
		this.rating = rating;
		this.referenceToken = referenceToken;
	}

	public PlaceObject(String id, Location location, String name, String vicinity, float rating, String token, String cat) {
		super();
		this.id = id;
		this.location = location;
		this.name = name;
		this.vicinity = vicinity;
		this.rating = rating;
		this.referenceToken = token;
		this.category = cat;
	}

	public PlaceObject(String id, String icon, Location location, String name, String vicinity, float rating, String referenceToken) {
		super();
		this.id = id;
		this.icon = icon;
		this.location = location;
		this.name = name;
		this.vicinity = vicinity;
		this.rating = rating;
		this.referenceToken = referenceToken;
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

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getReferenceToken() {
		return referenceToken;
	}

	public void setReferenceToken(String referenceToken) {
		this.referenceToken = referenceToken;
	}

	public PlaceDetailObject getPlaceDetailObject() {
		return placeDetailObject;
	}

	public void setPlaceDetailObject(PlaceDetailObject placeDetailObject) {
		this.placeDetailObject = placeDetailObject;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<PlacePhotoObject> getListPhotoObjects() {
		return listPhotoObjects;
	}

	public void setListPhotoObjects(ArrayList<PlacePhotoObject> listPhotoObjects) {
		this.listPhotoObjects = listPhotoObjects;
	}

	public RouteObject getRouteObject() {
		return routeObject;
	}

	public void setRouteObject(RouteObject routeObject) {
		this.routeObject = routeObject;
	}

	public void onDestroy() {
		if (listPhotoObjects != null) {
			listPhotoObjects.clear();
			listPhotoObjects = null;
		}
		routeObject = null;
		placeDetailObject = null;
	}

	public String getPhotoRef() {
		return photoRef;
	}

	public void setPhotoRef(String photoRef) {
		this.photoRef = photoRef;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public JSONObject toJson() {
		try {
			JSONObject mJsonObject = new JSONObject();
			mJsonObject.put("id", id);
			mJsonObject.put("name", name);
			mJsonObject.put("loc", String.valueOf(location.getLatitude()) + "|" + String.valueOf(location.getLongitude()));
			if (!StringUtils.isStringEmpty(vicinity)) {
				mJsonObject.put("vicinity", vicinity);
			}
			if (rating > 0) {
				mJsonObject.put("rating", rating);
			}
			mJsonObject.put("token", referenceToken);
			mJsonObject.put("cat", category);

			String url = icon;
			if (listPhotoObjects != null && listPhotoObjects.size() > 0) {
				PlacePhotoObject mPlacePhotoObject = listPhotoObjects.get(0);
				photoRef = mPlacePhotoObject.getPhotoReference();
				if (!StringUtils.isStringEmpty(photoRef)) {
					url = photoRef;
				}
			}
			else{
				if (!StringUtils.isStringEmpty(photoRef)) {
					url = photoRef;
				}
			}
			DBLog.d("PlaceObject", "==================>url=" + url);
			mJsonObject.put("icon", url);
			return mJsonObject;
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
