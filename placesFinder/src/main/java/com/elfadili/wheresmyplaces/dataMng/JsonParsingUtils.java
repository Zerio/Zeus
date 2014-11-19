package com.elfadili.wheresmyplaces.dataMng;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationManager;
import android.util.JsonReader;

import com.elfadili.wheresmyplaces.constanst.IMarocPlaceExplorerConstants;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.StringUtils;
import com.elfadili.wheresmyplaces.object.HomeSearchObject;
import com.elfadili.wheresmyplaces.object.KeywordObject;
import com.elfadili.wheresmyplaces.object.PlaceDetailObject;
import com.elfadili.wheresmyplaces.object.PlaceObject;
import com.elfadili.wheresmyplaces.object.PlacePhotoObject;
import com.elfadili.wheresmyplaces.object.ResponcePlaceResult;
import com.elfadili.wheresmyplaces.object.RouteObject;
import com.elfadili.wheresmyplaces.object.StepObject;
import com.elfadili.wheresmyplaces.object.UserReviewObject;

public class JsonParsingUtils implements IMarocPlaceExplorerConstants {

	public static final String TAG = JsonParsingUtils.class.getSimpleName();

	public static final String TYPE = "type";
	public static final String NAME = "name";
	public static final String REAL_NAME = "realname";
	public static final String KEY_WORD = "keyword";
	public static final String IMG = "img";

	public static final String NEXT_PAGE_TOKEN = "next_page_token";
	public static final String STATUS = "status";
	public static final String RESULTS = "results";
	public static final String RESULT = "result";
	public static final String ROUTES = "routes";
	public static final String DISTANCE = "distance";
	public static final String VALUE = "value";
	public static final String DURATION = "duration";
	public static final String END_ADDRESS = "end_address";
	public static final String START_ADDRESS = "start_address";
	public static final String START_LOCATION = "start_location";
	public static final String END_LOCATION = "end_location";
	public static final String TRAVEL_MODE = "travel_mode";
	public static final String HTML_INSTRUCTIONS = "html_instructions";
	public static final String OVERVIEW_POLYLINE = "overview_polyline";
	public static final String SUMMARY = "summary";

	public static final String GEOMETRY = "geometry";
	public static final String LOCATION = "location";
	public static final String LEGS = "legs";
	public static final String STEPS = "steps";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String ICON = "icon";
	public static final String ID = "id";
	public static final String PHOTOS = "photos";
	public static final String PHOTO_REFERENCE = "photo_reference";
	public static final String FORMAT_ADDRESS = "formatted_address";
	public static final String FORMAT_PHONE_NUMBER = "international_phone_number";
	public static final String REFERENCE = "reference";
	public static final String VINICITY = "vicinity";
	public static final String RATING = "rating";
	public static final String WEBSITE = "website";
	public static final String URL = "url";

	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";

	private static final String REVIEWS = "reviews";
	private static final String AUTHOR_NAME = "author_name";
	private static final String AUTHOR_URL = "author_url";
	private static final String TEXT = "text";
	private static final String TIME = "time";

	public static ArrayList<HomeSearchObject> parsingListHomeObjects(String data) {
		if (!StringUtils.isStringEmpty(data)) {
			try {
				JSONArray mJsonArray = new JSONArray(data);
				int size = mJsonArray.length();
				if (size > 0) {
					ArrayList<HomeSearchObject> mList = new ArrayList<HomeSearchObject>();
					for (int i = 0; i < size; i++) {
						JSONObject mJsonObject = mJsonArray.getJSONObject(i);
						int type = mJsonObject.getInt(TYPE);
						String name = mJsonObject.getString(NAME);
						String realName = mJsonObject.getString(REAL_NAME);
						String keyword = mJsonObject.getString(KEY_WORD);
						String img = mJsonObject.getString(IMG);

						HomeSearchObject mHomeSearchObject = new HomeSearchObject(type, name, keyword, img);
						mHomeSearchObject.setRealName(realName);
						mList.add(mHomeSearchObject);
					}
					DBLog.d(TAG, "====================>parsingListHomeObjects Size=" + mList.size());
					return mList;
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static ArrayList<KeywordObject> parsingListKeywordObjects(String data) {
		if (!StringUtils.isStringEmpty(data)) {
			try {
				JSONArray mJsonArray = new JSONArray(data);
				int size = mJsonArray.length();
				if (size > 0) {
					ArrayList<KeywordObject> mList = new ArrayList<KeywordObject>();
					for (int i = 0; i < size; i++) {
						JSONObject mJsonObject = mJsonArray.getJSONObject(i);
						String name = mJsonObject.getString(NAME);
						String keyword = mJsonObject.getString(KEY_WORD);

						KeywordObject mKeywordObject = new KeywordObject(name, keyword);
						mList.add(mKeywordObject);
					}
					DBLog.d(TAG, "====================>parsingListKeywordObjects Size=" + mList.size());
					return mList;
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static ResponcePlaceResult parsingListPlaceObjects(InputStream mInputStream) {
		if (mInputStream == null) {
			new Exception(TAG + " data can not null").printStackTrace();
			return null;
		}
		try {
			JsonReader reader = new JsonReader(new InputStreamReader(mInputStream, "UTF-8"));
			ResponcePlaceResult mResponcePlaceResult = new ResponcePlaceResult();
			reader.beginObject();
			while (reader.hasNext()) {
				String tag = reader.nextName();
				if (tag.equals(NEXT_PAGE_TOKEN)) {
					mResponcePlaceResult.setPageToken(reader.nextString());
				}
				else if (tag.equals(RESULTS)) {
					ArrayList<PlaceObject> mListPlaceObjects = new ArrayList<PlaceObject>();
					reader.beginArray();
					Location mLocation = null;
					;
					String icon = "";
					String id = "";
					String name = "";
					String reference = "";
					String vicinity = "";
					float rating = 0;
					PlaceObject mPlaceObject = null;
					boolean isStartAddress=false;
					while (reader.hasNext()) {
						reader.beginObject();
						while (reader.hasNext()) {
							String nameArray = reader.nextName();
							if (nameArray.equals(FORMAT_ADDRESS)) {
								mPlaceObject = new PlaceObject();
								mListPlaceObjects.add(mPlaceObject);
								vicinity = reader.nextString();
								mPlaceObject.setVicinity(vicinity);
								isStartAddress=true;
							}
							else if (nameArray.equals(GEOMETRY)) {
								if(!isStartAddress){
									mPlaceObject = new PlaceObject();
									mListPlaceObjects.add(mPlaceObject);
								}
								reader.beginObject();
								while (reader.hasNext()) {
									String tagLocation = reader.nextName();
									if (tagLocation.equals(LOCATION)) {
										mLocation = new Location(LocationManager.GPS_PROVIDER);
										reader.beginObject();
										while (reader.hasNext()) {
											double lat, lng;
											String tagInLocation = reader.nextName();
											if (tagInLocation.equals(LAT)) {
												lat = reader.nextDouble();
												mLocation.setLatitude(lat);
											}
											else if (tagInLocation.equals(LNG)) {
												lng = reader.nextDouble();
												mLocation.setLongitude(lng);
											}
											else{
												reader.skipValue();
											}
										}
										reader.endObject();
										if (mLocation != null) {
											mPlaceObject.setLocation(mLocation);
										}
									}
									else{
										reader.skipValue();
									}
								}
								reader.endObject();
							}
							else if (nameArray.equals(ICON)) {
								icon = reader.nextString();
								mPlaceObject.setIcon(icon);
							}
							else if (nameArray.equals(ID)) {
								id = reader.nextString();
								mPlaceObject.setId(id);
							}
							else if (nameArray.equals(NAME)) {
								name = reader.nextString();
								mPlaceObject.setName(name);
							}
							else if (nameArray.equals(PHOTOS)) {
								ArrayList<PlacePhotoObject> mListPhotoObjects = new ArrayList<PlacePhotoObject>();
								reader.beginArray();
								while (reader.hasNext()) {
									reader.beginObject();
									int width = 0;
									int height = 0;
									String photoReference = null;
									while (reader.hasNext()) {
										String nameTagPhoto = reader.nextName();
										if (nameTagPhoto.equals(HEIGHT)) {
											height = reader.nextInt();
										}
										else if (nameTagPhoto.equals(PHOTO_REFERENCE)) {
											photoReference = reader.nextString();
										}
										else if (nameTagPhoto.equals(WIDTH)) {
											width = reader.nextInt();
										}
										else {
											reader.skipValue();
										}
									}
									PlacePhotoObject mPhotoObject = new PlacePhotoObject(photoReference, width, height);
									mListPhotoObjects.add(mPhotoObject);
									reader.endObject();
								}
								reader.endArray();
								mPlaceObject.setListPhotoObjects(mListPhotoObjects);
							}
							else if (nameArray.equals(REFERENCE)) {
								reference = reader.nextString();
								mPlaceObject.setReferenceToken(reference);
							}
							else if (nameArray.equals(VINICITY)) {
								vicinity = reader.nextString();
								mPlaceObject.setVicinity(vicinity);
							}
							else if (nameArray.equals(RATING)) {
								rating = (float) reader.nextDouble();
								mPlaceObject.setRating(rating);
							}
							else {
								reader.skipValue();
							}
						}
						reader.endObject();
						isStartAddress=false;
					}
					reader.endArray();
					mResponcePlaceResult.setListPlaceObjects(mListPlaceObjects);
					DBLog.d(TAG, "=================>listLocation=" + mListPlaceObjects.size());
				}
				else if (tag.equals(STATUS)) {
					mResponcePlaceResult.setStatus(reader.nextString());
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
			return mResponcePlaceResult;
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PlaceDetailObject parsingPlaceDetailObject(InputStream mInputStream) {
		if (mInputStream == null) {
			new Exception(TAG + " data can not null").printStackTrace();
			return null;
		}
		PlaceDetailObject mPlaceDetailObject = null;
		try {
			JsonReader reader = new JsonReader(new InputStreamReader(mInputStream, "UTF-8"));
			reader.beginObject();
			while (reader.hasNext()) {
				String tag = reader.nextName();
				if (tag.equals(RESULT)) {
					mPlaceDetailObject = new PlaceDetailObject();
					reader.beginObject();
					while (reader.hasNext()) {
						String nameTagResult = reader.nextName();
						if (nameTagResult.equals(FORMAT_ADDRESS)) {
							mPlaceDetailObject.setAddress(reader.nextString());
						}
						else if (nameTagResult.equals(ICON)) {
							mPlaceDetailObject.setIcon(reader.nextString());
						}
						else if (nameTagResult.equals(ID)) {
							mPlaceDetailObject.setIcon(reader.nextString());
						}
						else if (nameTagResult.equals(FORMAT_PHONE_NUMBER)) {
							mPlaceDetailObject.setPhone(reader.nextString());
						}
						else if (nameTagResult.equals(NAME)) {
							mPlaceDetailObject.setName(reader.nextString());
						}
						else if (nameTagResult.equals(PHOTOS)) {
							ArrayList<PlacePhotoObject> mListPhotoObjects = new ArrayList<PlacePhotoObject>();
							reader.beginArray();
							while (reader.hasNext()) {
								reader.beginObject();
								int width = 0;
								int height = 0;
								String photoReference = null;
								while (reader.hasNext()) {
									String nameTagPhoto = reader.nextName();
									if (nameTagPhoto.equals(HEIGHT)) {
										height = reader.nextInt();
									}
									else if (nameTagPhoto.equals(PHOTO_REFERENCE)) {
										photoReference = reader.nextString();
									}
									else if (nameTagPhoto.equals(WIDTH)) {
										width = reader.nextInt();
									}
									else {
										reader.skipValue();
									}
								}
								PlacePhotoObject mPhotoObject = new PlacePhotoObject(photoReference, width, height);
								mListPhotoObjects.add(mPhotoObject);
								reader.endObject();
							}
							reader.endArray();
							if (mPlaceDetailObject != null) {
								mPlaceDetailObject.setListPhotoObjects(mListPhotoObjects);
							}
						}
						else if (nameTagResult.equals(REVIEWS)) {
							ArrayList<UserReviewObject> mListReviewObjects = new ArrayList<UserReviewObject>();
							reader.beginArray();
							while (reader.hasNext()) {
								reader.beginObject();
								String authorName = null;
								String authorUrl = null;
								float rating = 0;
								String text = null;
								long time = 0;
								while (reader.hasNext()) {
									String nameTagReview = reader.nextName();
									if (nameTagReview.equals(AUTHOR_NAME)) {
										authorName = reader.nextString();
									}
									else if (nameTagReview.equals(AUTHOR_URL)) {
										authorUrl = reader.nextString();
									}
									else if (nameTagReview.equals(RATING)) {
										rating = (float) reader.nextDouble();
									}
									else if (nameTagReview.equals(TEXT)) {
										text = reader.nextString();
									}
									else if (nameTagReview.equals(TIME)) {
										time = reader.nextLong();
									}
									else {
										reader.skipValue();
									}
								}
								UserReviewObject mUserReviewObject = new UserReviewObject(authorName, authorUrl, rating, text, time);
								mListReviewObjects.add(mUserReviewObject);
								reader.endObject();
							}
							reader.endArray();
							if (mPlaceDetailObject != null) {
								mPlaceDetailObject.setListReviewObjects(mListReviewObjects);
							}
						}
						else if (nameTagResult.equals(URL)) {
							mPlaceDetailObject.setUrl(reader.nextString());
						}
						else if (nameTagResult.equals(WEBSITE)) {
							mPlaceDetailObject.setWebsite(reader.nextString());
						}
						else if (nameTagResult.equals(RATING)) {
							float rating = (float) reader.nextDouble();
							mPlaceDetailObject.setRating(rating);
						}
						else {
							reader.skipValue();
						}
					}
					reader.endObject();
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
			return mPlaceDetailObject;
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static RouteObject parsingRouteObject(InputStream mInputStream) {
		if (mInputStream == null) {
			new Exception(TAG + " data can not null").printStackTrace();
			return null;
		}
		RouteObject mRouteObject = null;
		try {
			JsonReader reader = new JsonReader(new InputStreamReader(mInputStream, "UTF-8"));
			reader.beginObject();
			while (reader.hasNext()) {
				String tag = reader.nextName();
				if (tag.equals(ROUTES)) {
					mRouteObject = new RouteObject();
					reader.beginArray();
					while (reader.hasNext()) {
						reader.beginObject();
						while (reader.hasNext()) {
							String nameTagResult = reader.nextName();
							if (nameTagResult.equals(LEGS)) {
								reader.beginArray();
								while (reader.hasNext()) {
									reader.beginObject();
									while (reader.hasNext()) {
										String nameTagLegs = reader.nextName();
										if (nameTagLegs.equals(DISTANCE)) {
											String distance = parsingTextInObject(reader);
											mRouteObject.setDistance(distance);
										}
										else if (nameTagLegs.equals(DURATION)) {
											String duration = parsingTextInObject(reader);
											mRouteObject.setDuration(duration);
										}
										else if (nameTagLegs.equals(END_ADDRESS)) {
											mRouteObject.setEndAddress(reader.nextString());
										}
										else if (nameTagLegs.equals(START_ADDRESS)) {
											mRouteObject.setStartAddress(reader.nextString());
										}
										else if (nameTagLegs.equals(STEPS)) {
											ArrayList<StepObject> mListStepObjects = new ArrayList<StepObject>();
											reader.beginArray();
											while (reader.hasNext()) {
												mListStepObjects.add(parsingStepObject(reader));
											}
											reader.endArray();
											DBLog.d(TAG, "================>mListStepObjects=" + mListStepObjects.size());
											mRouteObject.setListStepObjects(mListStepObjects);
										}
										else {
											reader.skipValue();
										}

									}
									reader.endObject();
								}
								reader.endArray();
							}
							else if (nameTagResult.equals(OVERVIEW_POLYLINE)) {
								mRouteObject.setOverViewPolyline(parsingPolyLine(reader));
							}
							else if (nameTagResult.equals(SUMMARY)) {
								mRouteObject.setSummary(reader.nextString());
							}
							else {
								reader.skipValue();
							}
						}
						reader.endObject();

					}
					reader.endArray();
				}
				else if (tag.equals(STATUS)) {
					mRouteObject.setStatus(reader.nextString());
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
			return mRouteObject;
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String parsingTextInObject(JsonReader reader) {
		try {
			String result = "";
			reader.beginObject();
			while (reader.hasNext()) {
				String tag = reader.nextName();
				if (tag.equals(TEXT)) {
					result = reader.nextString();
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
			return result;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String parsingPolyLine(JsonReader reader) {
		try {
			String result = "";
			reader.beginObject();
			while (reader.hasNext()) {
				String tag = reader.nextName();
				if (tag.equals("points")) {
					result = reader.nextString();
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
			return result;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Location parsingLocation(JsonReader reader) {
		try {
			Location mLocation = new Location("");
			reader.beginObject();
			while (reader.hasNext()) {
				String tag = reader.nextName();
				if (tag.equals(LAT)) {
					mLocation.setLatitude(reader.nextDouble());
				}
				else if (tag.equals(LNG)) {
					mLocation.setLongitude(reader.nextDouble());
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
			return mLocation;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static StepObject parsingStepObject(JsonReader reader) {
		try {
			StepObject mStepObject = new StepObject();
			reader.beginObject();
			while (reader.hasNext()) {
				String tag = reader.nextName();
				if (tag.equals(DISTANCE)) {
					mStepObject.setDistance(parsingTextInObject(reader));
				}
				else if (tag.equals(DURATION)) {
					mStepObject.setDuration(parsingTextInObject(reader));
				}
				// else if(tag.equals(END_LOCATION)){
				// mStepObject.setEndLocation(parsingLocation(reader));
				// }
				else if (tag.equals(HTML_INSTRUCTIONS)) {
					mStepObject.setDescription(reader.nextString());
				}
				// else if(tag.equals(START_LOCATION)){
				// mStepObject.setStartLocation(parsingLocation(reader));
				// }
				else if (tag.equals(TRAVEL_MODE)) {
					mStepObject.setTravelMode(reader.nextString());
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
			return mStepObject;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<PlaceObject> parsingListFavoriteObjects(String data) {
		if (StringUtils.isStringEmpty(data)) {
			return null;
		}
		try {
			JSONArray mJsArray = new JSONArray(data);
			int size = mJsArray.length();
			if (size > 0) {
				ArrayList<PlaceObject> listFavorites = new ArrayList<PlaceObject>();
				for (int i = 0; i < size; i++) {
					JSONObject mJsObject = mJsArray.getJSONObject(i);
					String id = mJsObject.getString(ID);
					String name = mJsObject.getString(NAME);
					String loc = mJsObject.getString("loc");
					Location mLocation = null;
					if (!StringUtils.isStringEmpty(loc)) {
						String[] datas = loc.split("\\|+");
						if (datas != null && datas.length >= 2) {
							mLocation = new Location("");
							mLocation.setLatitude(Double.parseDouble(datas[0]));
							mLocation.setLongitude(Double.parseDouble(datas[1]));
						}
					}
					String vinicity = "";
					if (mJsObject.opt(VINICITY) != null) {
						vinicity = mJsObject.getString(VINICITY);
					}
					float rating = 0;
					if (mJsObject.opt(RATING) != null) {
						rating = (float) mJsObject.getDouble(RATING);
					}
					String token = mJsObject.getString("token");
					String cat = mJsObject.getString("cat");

					PlaceObject mPlaceObject = new PlaceObject(id, mLocation, name, vinicity, rating, token, cat);
					listFavorites.add(mPlaceObject);

					String urlIcon = mJsObject.getString("icon");
					if (!StringUtils.isStringEmpty(urlIcon)) {
						if (urlIcon.startsWith("http")) {
							mPlaceObject.setIcon(urlIcon);
						}
						else {
							mPlaceObject.setPhotoRef(urlIcon);
						}
					}
				}
				DBLog.d(TAG, "================>listFavorite=" + listFavorites.size());
				return listFavorites;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
