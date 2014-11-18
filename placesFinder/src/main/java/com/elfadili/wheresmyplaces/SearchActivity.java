package com.elfadili.wheresmyplaces;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.ypyproductions.utils.DBLog;

public class SearchActivity extends Activity {
	
	public static final String TAG =SearchActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		DBLog.d(TAG, "===============>search");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			DBLog.d(TAG, "===============>search =" + query);
		}
	}
}
