package com.elfadili.wheresmyplaces.dataMng;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;

import android.content.Context;
import android.location.Location;

import com.elfadili.wheresmyplaces.constants.IMarocPlaceExplorerConstants;
import com.ypyproductions.net.task.IDBCallback;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.DBListExcuteAction;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.IOUtils;
import com.ypyproductions.utils.StringUtils;
import com.places.nearby.R;
import com.elfadili.wheresmyplaces.object.HomeSearchObject;
import com.elfadili.wheresmyplaces.object.KeywordObject;
import com.elfadili.wheresmyplaces.object.PlaceObject;

public class TotalDataManager implements IMarocPlaceExplorerConstants {

	public static final String TAG = TotalDataManager.class.getSimpleName();

	private static TotalDataManager totalDataManager;
	private ArrayList<HomeSearchObject> listHomeSearchObjects;
	private Location currentLocation;
	private ArrayList<PlaceObject> listFavoriteObjects;
	private ArrayList<KeywordObject> listKeywordObjects;

	public static TotalDataManager getInstance() {
		if (totalDataManager == null) {
			totalDataManager = new TotalDataManager();
		}
		return totalDataManager;
	}

	private TotalDataManager() {
		listFavoriteObjects = new ArrayList<PlaceObject>();
	}

	public void onDestroy() {
		if (listFavoriteObjects != null) {
			listFavoriteObjects.clear();
			listFavoriteObjects = null;
		}
		if (listHomeSearchObjects != null) {
			listHomeSearchObjects.clear();
			listHomeSearchObjects = null;
		}
		totalDataManager = null;
	}

	public ArrayList<KeywordObject> getListKeywordObjects() {
		return listKeywordObjects;
	}

	public void setListKeywordObjects(ArrayList<KeywordObject> listKeywordObjects) {
		this.listKeywordObjects = listKeywordObjects;
	}

	public ArrayList<HomeSearchObject> getListHomeSearchObjects() {
		return listHomeSearchObjects;
	}

	public void setListHomeSearchObjects(ArrayList<HomeSearchObject> listHomeSearchObjects) {
		this.listHomeSearchObjects = listHomeSearchObjects;
	}

	public HomeSearchObject getHomeSearchSelected() {
		if (listHomeSearchObjects != null && listHomeSearchObjects.size() > 0) {
			for (HomeSearchObject mHomeSearchObject1 : listHomeSearchObjects) {
				if (mHomeSearchObject1.isSelected()) {
					DBLog.d(TAG, "============>home search selected=" + mHomeSearchObject1.getName());
					return mHomeSearchObject1;
				}
			}
			listHomeSearchObjects.get(1).setSelected(true);
			return listHomeSearchObjects.get(1);
		}
		return null;
	}
	
	public void setSelectedObject(int pos){
		if (listHomeSearchObjects != null && listHomeSearchObjects.size() > 0) {
			for (HomeSearchObject mHomeSearchObject : listHomeSearchObjects) {
				if(listHomeSearchObjects.indexOf(mHomeSearchObject)==pos){
					mHomeSearchObject.setSelected(true);
				}
				else{
					mHomeSearchObject.setSelected(false);
				}
			}
		}
	}
	public void setSelectedObject(HomeSearchObject pos){
		if (listHomeSearchObjects != null && listHomeSearchObjects.size() > 0) {
			for (HomeSearchObject mHomeSearchObject : listHomeSearchObjects) {
				if(mHomeSearchObject.equals(pos)){
					mHomeSearchObject.setSelected(true);
				}
				else{
					mHomeSearchObject.setSelected(false);
				}
			}
		}
	}
	
	public HomeSearchObject findHomeSearchObject(String query){
		if(StringUtils.isStringEmpty(query)){
			return null;
		}
		if (listHomeSearchObjects != null && listHomeSearchObjects.size() > 0) {
			for (HomeSearchObject mHomeSearchObject : listHomeSearchObjects) {
				String keyword =mHomeSearchObject.getKeyword();
				if(!StringUtils.isStringEmpty(keyword) && keyword.equalsIgnoreCase(query)){
					return mHomeSearchObject;
				}
			}
		}
		return null;
	}

	public void onResetResultSearch(boolean isResetAll) {
		if (listHomeSearchObjects != null && listHomeSearchObjects.size() > 0) {
			for (HomeSearchObject mHomeSearchObject1 : listHomeSearchObjects) {
				mHomeSearchObject1.setResponcePlaceResult(null);
			}
			boolean isSelected = listHomeSearchObjects.get(0).isSelected();
			if(isSelected && isResetAll){
				listHomeSearchObjects.get(0).setSelected(false);
				listHomeSearchObjects.get(0).setType(TYPE_SEARCH_BY_TYPES);
				listHomeSearchObjects.get(0).setKeyword(null);
				listHomeSearchObjects.get(1).setSelected(true);
			}
		}
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public int getResIconMapPin(Context mContext, String keyword) {
		if (StringUtils.isStringEmpty(keyword)) {
			return R.drawable.icon_custom_search;
		}
		if (keyword.equals("ATM")) {
			return R.drawable.icon_pin_atm;
		}
		else if (keyword.equals("BANQUE")) {
			return R.drawable.icon_pin_bank;
		}
		else if (keyword.equals("POLICE")) {
			return R.drawable.icon_pin_police;
		}
		else if (keyword.equals("UNIVERSITÉ")) {
			return R.drawable.icon_pin_university;
		}
		else if (keyword.equals("SERVICES")) {
			return R.drawable.icon_pin_gas;
		}
		else if (keyword.equals("TAXI")) {
			return R.drawable.icon_pin_taxi;
		}
		else if (keyword.equals("BUS")) {
			return R.drawable.icon_pin_bus;
		}
		else if (keyword.equals("AÉROPORT")) {
			return R.drawable.icon_pin_airport;
		}
		else if (keyword.equals("HÔPITAL")) {
			return R.drawable.icon_pin_hospital;
		}
		else if (keyword.equals("HÔTEL")) {
			return R.drawable.icon_pin_hotel;
		}
		else if (keyword.equals("PARK")) {
			return R.drawable.icon_pin_park;
		}
		else if (keyword.equals("ZOO")) {
			return R.drawable.icon_pin_zoo;
		}
		else if (keyword.equals("CINEMA")) {
			return R.drawable.icon_pin_cinema;
		}
		else if (keyword.equals("SHOPPING")) {
			return R.drawable.icon_pin_shop;
		}
		else if (keyword.equals("CAFÉ")) {
			return R.drawable.icon_pin_cafe;
		}
		else if (keyword.equals("BAR")) {
			return R.drawable.icon_pin_bar;
		}
		return R.drawable.icon_custom_search;
	}

	public int getResIconHome(Context mContext, String keyword) {
		if (StringUtils.isStringEmpty(keyword)) {
			return -1;
		}
		if (keyword.equals("HÔTEL")) {
			return R.drawable.hotel;
		}
		else if (keyword.equals("BANQUE")) {
			return R.drawable.bank;
		}
		else if (keyword.equals("POLICE")) {
			return R.drawable.police;
		}
		else if (keyword.equals("UNIVERSITÉ")) {
			return R.drawable.university;
		}
		else if (keyword.equals("SERVICES")) {
			return R.drawable.gas_station;
		}
		else if (keyword.equals("TAXI")) {
			return R.drawable.taxi_stand;
		}
		else if (keyword.equals("BUS")) {
			return R.drawable.bus_station;
		}
		else if (keyword.equals("AÉROPORT")) {
			return R.drawable.airport;
		}
		else if (keyword.equals("HÔPITAL")) {
			return R.drawable.hospital;
		}
		else if (keyword.equals("ATM")) {
			return R.drawable.atm;
		}
		else if (keyword.equals("PARK")) {
			return R.drawable.park;
		}
		else if (keyword.equals("ZOO")) {
			return R.drawable.zoo;
		}
		else if (keyword.equals("CINEMA")) {
			return R.drawable.cinema;
		}
		else if (keyword.equals("SHOPPING")) {
			return R.drawable.shop;
		}
		else if (keyword.equals("CAFÉ")) {
			return R.drawable.cafe;
		}
		else if (keyword.equals("BAR")) {
			return R.drawable.bar;
		}
		return -1;
	}

	public int getResMiniIconHome(Context mContext, String name) {
		if (StringUtils.isStringEmpty(name)) {
			return -1;
		}
		if (name.equals("ATM")) {
			return R.drawable.mini_atm;
		}
		else if (name.equals("BANQUE")) {
			return R.drawable.mini_bank;
		}
		else if (name.equals("POLICE")) {
			return R.drawable.mini_police;
		}
		else if (name.equals("UNIVERSITÉ")) {
			return R.drawable.mini_university;
		}
		else if (name.equals("SERVICES")) {
			return R.drawable.mini_gas_station;
		}
		else if (name.equals("TAXI")) {
			return R.drawable.mini_taxi_stand;
		}
		else if (name.equals("BUS")) {
			return R.drawable.mini_bus_station;
		}
		else if (name.equals("AÉROPORT")) {
			return R.drawable.mini_airport;
		}
		else if (name.equals("HÔPITAL")) {
			return R.drawable.mini_hospital;
		}
		else if (name.equals("HÔTEL")) {
			return R.drawable.mini_hotel;
		}
		else if (name.equals("PARK")) {
			return R.drawable.mini_park;
		}
		else if (name.equals("ZOO")) {
			return R.drawable.mini_zoo;
		}
		else if (name.equals("CINEMA")) {
			return R.drawable.mini_cinema;
		}
		else if (name.equals("SHOPPING")) {
			return R.drawable.mini_shop;
		}
		else if (name.equals("CAFÉ")) {
			return R.drawable.mini_cafe;
		}
		else if (name.equals("BAR")) {
			return R.drawable.mini_bar;
		}
		return -1;
	}

	public ArrayList<PlaceObject> getListFavoriteObjects() {
		return listFavoriteObjects;
	}

	public void setListFavoriteObjects(ArrayList<PlaceObject> listFavoriteObjects) {
		if (listFavoriteObjects != null) {
			this.listFavoriteObjects.clear();
			this.listFavoriteObjects = null;
			this.listFavoriteObjects = listFavoriteObjects;
		}
	}

	public boolean isFavoriteLocation(String id) {
		if (listFavoriteObjects != null && listFavoriteObjects.size() > 0 && !StringUtils.isStringEmpty(id)) {
			for (PlaceObject mPlaceObject : listFavoriteObjects) {
				String idNew = mPlaceObject.getId();
				if (!StringUtils.isStringEmpty(idNew) && idNew.equals(id)) {
					return true;
				}
			}
		}
		return false;
	}

	public void addFavoritePlace(final Context mContext, PlaceObject mPlaceObject) {
		if (mPlaceObject != null && listFavoriteObjects != null) {
			boolean isAdd = isFavoriteLocation(mPlaceObject.getId());
			if (!isAdd) {
				listFavoriteObjects.add(mPlaceObject);
				DBListExcuteAction.getInstance().queueAction(new IDBCallback() {
					@Override
					public void onAction() {
						saveFavoritePlaces(mContext);
					}
				});
			}
		}
	}

	public void removeFavoritePlace(final Context mContext, PlaceObject mPlaceObject) {
		if (mPlaceObject != null && listFavoriteObjects != null) {
			Iterator<PlaceObject> mListIterator = listFavoriteObjects.iterator();
			String id = mPlaceObject.getId();
			boolean isSyncAgain = false;
			while (mListIterator.hasNext()) {
				PlaceObject placeObject = (PlaceObject) mListIterator.next();
				String idNew = placeObject.getId();
				if (!StringUtils.isStringEmpty(idNew) && !StringUtils.isStringEmpty(id) && id.equals(idNew)) {
					mListIterator.remove();
					isSyncAgain = true;
					break;
				}
			}
			DBLog.d(TAG, "============>isSyncAgain=" + isSyncAgain);
			if (isSyncAgain) {
				DBListExcuteAction.getInstance().queueAction(new IDBCallback() {
					@Override
					public void onAction() {
						saveFavoritePlaces(mContext);
					}
				});
			}
		}
	}

	public synchronized void saveFavoritePlaces(Context mContext) {
		if (!ApplicationUtils.hasSDcard()) {
			return;
		}
		File mFile = IOUtils.getDiskCacheDir(mContext, DIR_DATA);
		if (!mFile.exists()) {
			mFile.mkdirs();
		}
		if (listFavoriteObjects != null && listFavoriteObjects.size() > 0) {
			JSONArray mJsArray = new JSONArray();
			for (PlaceObject mSongObject : listFavoriteObjects) {
				mJsArray.put(mSongObject.toJson());
			}
			DBLog.d(TAG, "=============>favoriteDatas=" + mJsArray.toString());
			IOUtils.writeString(mFile.getAbsolutePath(), FILE_FAVORITE_PLACES, mJsArray.toString());
			return;
		}
		IOUtils.writeString(mFile.getAbsolutePath(), FILE_FAVORITE_PLACES, "");
	}

}
