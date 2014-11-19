package com.elfadili.wheresmyplaces;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdView;
import com.ypyproductions.utils.DBLog;
import com.places.nearby.R;
import com.elfadili.wheresmyplaces.constanst.IWhereMyLocationConstants;

public class ShowUrlActivity extends DBFragmentActivity implements IWhereMyLocationConstants {

	public static final String TAG = ShowUrlActivity.class.getSimpleName();

	private ProgressBar mProgressBar;
	private WebView mWebViewShowPage;

	private String mUrl;

	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		this.setContentView(R.layout.activity_show_url);

		Intent args = getIntent();
		if (args != null) {
			mUrl = args.getStringExtra(KEY_URL);
			DBLog.d(TAG, "===========>url=" + mUrl);
		}

		this.mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		this.mProgressBar.setVisibility(View.VISIBLE);
		this.mWebViewShowPage = (WebView) findViewById(R.id.webview);
		this.mWebViewShowPage.getSettings().setJavaScriptEnabled(true);
		this.mWebViewShowPage.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mProgressBar.setVisibility(View.GONE);
			}
		});
		this.mWebViewShowPage.loadUrl(mUrl);
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mWebViewShowPage != null) {
			mWebViewShowPage.destroy();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (adView != null) {
			adView.destroy();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebViewShowPage.canGoBack()) {
				mWebViewShowPage.goBack();
			}
			else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
