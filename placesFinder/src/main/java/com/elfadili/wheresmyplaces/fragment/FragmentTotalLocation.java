package com.elfadili.wheresmyplaces.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.elfadili.wheresmyplaces.constants.IMarocPlaceExplorerConstants;
import com.elfadili.wheresmyplaces.object.ResponsePlaceResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ypyproductions.location.DBLastLocationFinder;
import com.ypyproductions.net.task.DBTask;
import com.ypyproductions.net.task.IDBTaskListener;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.StringUtils;
import com.places.nearby.R;
import com.elfadili.wheresmyplaces.TotalLocationInMapActivity;
import com.elfadili.wheresmyplaces.adapter.DetailMapInfoAdapter;
import com.elfadili.wheresmyplaces.dataMng.TotalDataManager;
import com.elfadili.wheresmyplaces.object.HomeSearchObject;
import com.elfadili.wheresmyplaces.object.PlaceObject;
import com.elfadili.wheresmyplaces.object.PlacePhotoObject;

public class FragmentTotalLocation extends Fragment implements IMarocPlaceExplorerConstants, OnMarkerClickListener {

	public static final String TAG = FragmentTotalLocation.class.getSimpleName();
	public static final int TIME_OUT = 20;

	private View mRootlayout;
	private boolean isFindView;

	private TotalLocationInMapActivity mContext;
	private GoogleMap mMap;

	private ArrayList<LatLng> lisLatLngs = new ArrayList<LatLng>();
	private ArrayList<MarkerOptions> listMarkerOptions = new ArrayList<MarkerOptions>();
	private DetailMapInfoAdapter mMapAdapter;

	private Address mLocationAddress;
	private LatLng mCurrentLatLng;
	private MarkerOptions mCurrentLocationMarker;
	private DBLastLocationFinder mDBLocationFinder;
	private int mCurrentMapType=GoogleMap.MAP_TYPE_NORMAL; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mRootlayout = inflater.inflate(R.layout.fragment_all_location, container, false);
		return mRootlayout;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (!isFindView) {
			isFindView = true;
			this.findView();
		}
	}

	private void findView() {
		this.mContext = (TotalLocationInMapActivity) getActivity();
		this.mDBLocationFinder = new DBLastLocationFinder(mContext);
		this.setUpMapIfNeeded();
		drawMyLocation();
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
            //Toast.makeText(getActivity(), ((SupportMapFragment) mContext.getSupportFragmentManager().findFragmentById(R.id.detail_map)).toString(), Toast.LENGTH_LONG).show();
			//mMap = ((SupportMapFragment) mContext.getSupportFragmentManager().findFragmentById(R.id.detail_map)).getMap();
		}
	}

	private void setUpMap() {
		if (listMarkerOptions != null && listMarkerOptions.size() > 0) {
			listMarkerOptions.clear();
			listMarkerOptions = null;
			listMarkerOptions = new ArrayList<MarkerOptions>();
		}

		if (lisLatLngs != null && lisLatLngs.size() > 0) {
			lisLatLngs.clear();
			lisLatLngs = null;
			lisLatLngs = new ArrayList<LatLng>();
		}
		if (mMapAdapter != null) {
			mMap.setInfoWindowAdapter(null);
			mMapAdapter = null;
		}
		try {
			mMap.clear();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		mMap.getUiSettings().setZoomControlsEnabled(false);

		final HomeSearchObject mHomeSearchObject = TotalDataManager.getInstance().getHomeSearchSelected();
		if (mHomeSearchObject != null) {
			ResponsePlaceResult mResponcePlaceResult = mHomeSearchObject.getResponcePlaceResult();
			if (mResponcePlaceResult != null) {
				ArrayList<PlaceObject> mListPlaceObjects = mResponcePlaceResult.getListPlaceObjects();

				if (mListPlaceObjects != null && mListPlaceObjects.size() > 0) {
					int size = mListPlaceObjects.size();
					for (int i = 0; i < size; i++) {
						PlaceObject mPlaceObject = mListPlaceObjects.get(i);
						LatLng mLatLng = new LatLng(mPlaceObject.getLocation().getLatitude(), mPlaceObject.getLocation().getLongitude());
						lisLatLngs.add(mLatLng);

						String urlPhoto = mPlaceObject.getIcon();
						ArrayList<PlacePhotoObject> mListPhotoObjects = mPlaceObject.getListPhotoObjects();
						if (mListPhotoObjects != null && mListPhotoObjects.size() > 0) {
							PlacePhotoObject mPlacePhotoObject = mListPhotoObjects.get(0);
							String photoRef = mPlacePhotoObject.getPhotoReference();
							if (!StringUtils.isStringEmpty(photoRef)) {
								urlPhoto = String.format(FORMAT_URL_PHOTO, photoRef, API_KEY);
							}
						}
						String address = getString(R.string.title_unknown_address);
						if (!StringUtils.isStringEmpty(mPlaceObject.getVicinity())) {
							address = mPlaceObject.getVicinity();
						}
						String snippet = address + "|" + urlPhoto+"|"+String.valueOf(i);
						DBLog.d(TAG, "==============>snippet=" + snippet);
						MarkerOptions mMarkerOptions = new MarkerOptions().position(mLatLng).title(mPlaceObject.getName()).snippet(snippet)
								.icon(BitmapDescriptorFactory.fromResource(mContext.mResIcon));
						listMarkerOptions.add(mMarkerOptions);
						mMap.addMarker(mMarkerOptions);
					}
				}
			}
		}


		mMapAdapter = new DetailMapInfoAdapter(mContext, mContext.mImgFetcher,mContext.mTypeFaceRobotoBold,mContext.mTypeFaceRobotoLight);
		mMap.setInfoWindowAdapter(mMapAdapter);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker mMarker) {
				String snippet = mMarker.getSnippet();
				if(!StringUtils.isStringEmpty(snippet)){
					try {
						String[] datas = snippet.split("\\|+");
						if(datas!=null && datas.length>=3){
							int index = Integer.parseInt(datas[2]);
							if(index>=0){
								DBLog.d(TAG, "===============>indexClick="+index);
								mContext.goToDetail(index);
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		});
		final View mapView = mContext.getSupportFragmentManager().findFragmentById(R.id.detail_map).getView();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				// We use the new method when supported
				@SuppressLint("NewApi")
				// We check which build version we are using.
				@Override
				public void onGlobalLayout() {
					Builder mMapBuilder = new LatLngBounds.Builder();
					for (LatLng mLatLng : lisLatLngs) {
						mMapBuilder.include(mLatLng);
					}
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
					else {
						mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					if(mCurrentLatLng!=null){
						if(mCurrentLocationMarker!=null){
							mMap.addMarker(mCurrentLocationMarker);
						}
					}
					mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBuilder.build(), 50));
				}
			});
		}

	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		return false;
	}

	@Override
	public void onDestroy() {
		lisLatLngs.clear();
		lisLatLngs = null;
		listMarkerOptions.clear();
		listMarkerOptions = null;
		super.onDestroy();
	}

	private void drawMyLocation() {
		if (mMap != null) {
			final Location mLocation = TotalDataManager.getInstance().getCurrentLocation();
			if(mLocation!=null){
				IDBTaskListener mDBTaskListener = new IDBTaskListener() {
					private String mLocationName;
					
					@Override
					public void onPreExcute() {
						mContext.showProgressDialog();
					}
					
					@Override
					public void onDoInBackground() {
						mLocationAddress = mDBLocationFinder.getAddressLocation(mLocation);
						mLocationName = mDBLocationFinder.getNameLocation(mLocationAddress);
						if (StringUtils.isStringEmpty(mLocationName)) {
							mLocationName = getString(R.string.title_unknown_location);
						}
					}
					
					@Override
					public void onPostExcute() {
						mContext.dismissProgressDialog();
						try {
							mCurrentLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
							mCurrentLocationMarker = new MarkerOptions().position(mCurrentLatLng).title(mContext.getString(R.string.title_my_location))
									.snippet(mLocationName + "|" + "1"+"|"+"-1").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_my_location));
					
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						setUpMap();
					}
				};
				DBTask mDBTask = new DBTask(mDBTaskListener);
				mDBTask.execute();
			}
			else{
				setUpMap();
			}
		}
	}
	public void setMapType(int mapType,String mStrName){
		if(mMap!=null){
			if(mCurrentMapType!=mapType){
				this.mCurrentMapType = mapType;
				Toast.makeText(mContext, mStrName+ " On", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(mContext, mStrName+ " Off", Toast.LENGTH_SHORT).show();
				this.mCurrentMapType= GoogleMap.MAP_TYPE_NORMAL;
			}
			mMap.setMapType(mCurrentMapType);
		}
		
	}

}
