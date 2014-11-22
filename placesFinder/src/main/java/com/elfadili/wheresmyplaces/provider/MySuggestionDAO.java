package com.elfadili.wheresmyplaces.provider;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.elfadili.wheresmyplaces.constants.IMarocPlaceExplorerConstants;
import com.ypyproductions.utils.DBLog;
import com.elfadili.wheresmyplaces.object.KeywordObject;


/**
 * Ruby data access object
 * @author DOBao
 * 3/11/2011 
 */
public class MySuggestionDAO implements IMarocPlaceExplorerConstants {
	
	
	private static final String TAG = MySuggestionDAO.class.getSimpleName();
	
	public static void insertData(Context mContext,KeywordObject mKeywordObject) {
		if(mKeywordObject==null){
			DBLog.d(TAG, "----------->PrivateData can not null");
			return;
		}
    	try {
    		DBLog.d(TAG, "=================>start insert name="+mKeywordObject.getName());
    		ContentValues values = new ContentValues();
    		values.put(KEY_NAME, mKeywordObject.getName());
    		values.put(KEY_KEYWORD,mKeywordObject.getKeyword());
			Uri uriInsert = mContext.getContentResolver().insert(CONTENT_URI, values);
			if(uriInsert == null){
				new Exception("DoBao :cannot insert to database").printStackTrace();
				return;
			}
		} 
    	catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * get all infoOutOfDate from database
	 * @param mContext
	 * @return
	 */
	public static KeywordObject getPrivateData(Context mContext,String query) {
		Cursor c = mContext.getContentResolver().query(CONTENT_CHECK, null, null, new String[]{query},null);
		if (c.moveToFirst()) {
			KeywordObject mPrivateData=null;
			do {
				String mKeyword= c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
				String mData= c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA));
				try {
					mPrivateData = new KeywordObject(mKeyword, mData);
					break;
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			} 
			while (c.moveToNext());
			c.close();
			return mPrivateData;
		}
		return null;
	}
	
}
