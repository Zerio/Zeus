package com.elfadili.wheresmyplaces.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.ypyproductions.utils.StringUtils;
import com.places.nearby.R;

public class MapInfoAdapter implements InfoWindowAdapter {

	public static final String TAG = MapInfoAdapter.class.getSimpleName();
	private View mWindow;

	public MapInfoAdapter(FragmentActivity mContext) {
		this.mWindow = mContext.getLayoutInflater().inflate(R.layout.custom_info_window, null, false);
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		render(marker);
		return mWindow;
	}

	private void render(final Marker mMarker) {
		String nameStore = mMarker.getTitle();
		if(nameStore==null){
			nameStore="";
		}
		TextView mTvStore = ((TextView) mWindow.findViewById(R.id.tv_name_location));
		mTvStore.setText(nameStore);
		
		String snippet = mMarker.getSnippet();
		if(!StringUtils.isStringEmpty(snippet)){
			TextView mTvAddress = ((TextView) mWindow.findViewById(R.id.tv_sumary));
			mTvAddress.setText(snippet);
		}
	}
	

}
