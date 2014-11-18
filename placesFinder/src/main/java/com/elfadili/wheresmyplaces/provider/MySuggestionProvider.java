package com.elfadili.wheresmyplaces.provider;

import java.util.HashMap;
import java.util.Locale;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import com.ypyproductions.utils.DBLog;
import com.elfadili.wheresmyplaces.constanst.IWhereMyLocationConstants;


public class MySuggestionProvider extends ContentProvider implements IWhereMyLocationConstants {

	public static final String TAG = MySuggestionProvider.class.getSimpleName();

	private SQLiteDatabase mDB;
	private DatabaseHelper mDbHelper;
	public  UriMatcher uriMatcher = buildUriMatcher();

	@Override
	public boolean onCreate() {
		Context context = getContext();
		mDbHelper = new DatabaseHelper(context);
		mDB = mDbHelper.getWritableDatabase();
		return (mDB == null) ? false : true;

	}
	
   private UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	 
        // Suggestion items of Search Dialog is provided by this uri
        uriMatcher.addURI(CONTENT_URI.getAuthority(), SearchManager.SUGGEST_URI_PATH_QUERY,SUGGESTION_KEYWORD);
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SUGGESTION_KEYWORD);
	 
        // This URI is invoked, when user presses "Go" in the Keyboard of Search Dialog
        // Listview items of SearchableActivity is provided by this uri
        // See android:searchSuggestIntentData="content://in.wptrafficanalyzer.searchdialogdemo.provider/countries" of searchable.xml
        uriMatcher.addURI(AUTHORITY, "records", SEARCH_KEYWORD);
	 
        // This URI is invoked, when user selects a suggestion from search dialog or an item from the listview
        // Country details for CountryActivity is provided by this uri
        // See, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID in CountryDB.java
        uriMatcher.addURI(AUTHORITY, "records/#", GET_KEYWORD);
        uriMatcher.addURI(AUTHORITY, "records/@", SAVE_KEYWORD);
	 
        return uriMatcher;
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if(mDB!=null){
			long rowID=mDB.insert(DATABASE_TABLE, null, values);
			if (rowID > 0) {
				Uri mUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(mUri, null);
				return mUri;

			}
		}
		throw new UnsupportedOperationException();
	}


	private Cursor getSuggestions(String query) {
		query = query.toLowerCase(Locale.US);
		String[] columns = new String[] { KEY_ID, KEY_NAME, KEY_KEYWORD};
		return mDbHelper.getRecordMatches(query, columns);
	}
	
	private Cursor getExactlyRecord(String query) {
		query = query.toLowerCase(Locale.US);
		String[] columns = new String[] { KEY_ID, KEY_NAME, KEY_KEYWORD};
		return mDbHelper.getExactlyRecords(query, columns);
	}

	private Cursor getRecord(Uri uri) {
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] { KEY_NAME ,KEY_KEYWORD};
		return mDbHelper.getRecord(rowId, columns);
	}
	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor c=null;
		switch (uriMatcher.match(uri)) {
        case SUGGESTION_KEYWORD:
        	DBLog.d(TAG, "================>aaaaa="+selectionArgs[0]);
        	c=getSuggestions(selectionArgs[0]);
            break;
        case SEARCH_KEYWORD:
        	DBLog.d(TAG, "================>bbb="+selectionArgs[0]);
        	c=getSuggestions(selectionArgs[0]);
        	break;
        case GET_KEYWORD:
             c = getRecord(uri);
        	break;
        case SAVE_KEYWORD:
        	c = getExactlyRecord(selectionArgs[0]);
        	break;
		}
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
	
	private static HashMap<String, String> buildColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(BaseColumns._ID, KEY_ID+" as " + KEY_ID);
		map.put(SearchManager.SUGGEST_COLUMN_TEXT_1,KEY_NAME+" AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA, KEY_KEYWORD+" AS " +SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, KEY_ID + " as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID );
		return map;
	}
	

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}

		public Cursor getRecordMatches(String query, String[] columns) {
			String selection = KEY_NAME + " like ?";
			String[] selectionArgs = new String[] {"%"+query + "%" };
			return query(selection, selectionArgs, columns);
		}
		public Cursor getExactlyRecords(String query, String[] columns) {
			String selection = KEY_NAME + " = ?";
			String[] selectionArgs = new String[] {query};
			return query(selection, selectionArgs, columns);
		}
		
		public Cursor getRecord(String rowId, String[] columns) {
			String selection = KEY_ID+" = ?";
			String[] selectionArgs = new String[] { rowId };
			return query(selection, selectionArgs, columns);
		}

		private Cursor query(String selection, String[] selectionArgs, String[] columns) {
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			builder.setTables(DATABASE_TABLE);
			builder.setProjectionMap(buildColumnMap());
			
			Cursor cursor = builder.query(getReadableDatabase(), new String[] { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,
					SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID}, 
					selection, selectionArgs, null, null, 
					KEY_NAME + " asc ", "20");
			return cursor;
		}
	}

}
