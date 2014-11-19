package com.elfadili.wheresmyplaces.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.elfadili.wheresmyplaces.constanst.IMarocPlaceExplorerConstants;

public class SettingManager implements ISettingConstants, IMarocPlaceExplorerConstants {
	
	public static final String TAG = SettingManager.class.getSimpleName();
	
	public static final String YPY_SHARPREFS = "ypy_prefs";

	public static void saveSetting(Context mContext,String mKey,String mValue){
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(YPY_SHARPREFS, Context.MODE_PRIVATE);
		Editor editor = mSharedPreferences.edit();
		editor.putString(mKey, mValue);
		editor.commit();
	}
	
	public static String getSetting(Context mContext,String mKey,String mDefValue){
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(YPY_SHARPREFS, Context.MODE_PRIVATE);
		return mSharedPreferences.getString(mKey, mDefValue);
	}
	
	public static void setRadius(Context mContext, int mValue){
		saveSetting(mContext, KEY_RADIUS, String.valueOf(mValue));
	}
	
	public static int getRadius(Context mContext){
		return Integer.parseInt(getSetting(mContext, KEY_RADIUS, DEFAULT_RADIUS_VALUE));
	}
	
	public static void setMetric(Context mContext, String mValue){
		saveSetting(mContext, KEY_METRIC, mValue);
	}
	
	public static String getMetric(Context mContext){
		return getSetting(mContext, KEY_METRIC, UNIT_KILOMETTER);
	}
	
	public static void setPiority(Context mContext, String mValue){
		saveSetting(mContext, KEY_PIORITY, mValue);
	}
	
	public static String getPiority(Context mContext){
		return getSetting(mContext, KEY_PIORITY, PIORITY_DISTANCE);
	}
	
	public static void setTravelMode(Context mContext, String mValue){
		saveSetting(mContext, KEY_TRAVEL_MODE, mValue);
	}
	
	public static String getTravelMode(Context mContext){
		return getSetting(mContext, KEY_TRAVEL_MODE, TRAVEL_MODE_DRIVING);
	}
	public static void setDontShow(Context mContext, boolean mValue){
		saveSetting(mContext, KEY_DONT_SHOW, String.valueOf(mValue));
	}
	
	public static boolean getDontShow(Context mContext){
		return Boolean.parseBoolean(getSetting(mContext, KEY_DONT_SHOW, "false"));
	}
	
	public static void setInitProvider(Context mContext, boolean mValue){
		saveSetting(mContext, KEY_INIT_PROVIDER, String.valueOf(mValue));
	}
	
	public static boolean isInitProvider(Context mContext){
		return Boolean.parseBoolean(getSetting(mContext, KEY_INIT_PROVIDER, "false"));
	}
}
