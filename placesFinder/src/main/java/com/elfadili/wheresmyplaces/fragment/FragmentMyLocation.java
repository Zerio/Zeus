package com.elfadili.wheresmyplaces.fragment;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.elfadili.wheresmyplaces.constanst.IMarocPlaceExplorerConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ypyproductions.dialog.utils.AlertDialogUtils;
import com.ypyproductions.location.DBLastLocationFinder;
import com.ypyproductions.location.DBLastLocationFinder.ILastLocationFinder;
import com.ypyproductions.net.task.DBTask;
import com.ypyproductions.net.task.IDBTaskListener;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.StringUtils;
import com.places.nearby.R;
import com.elfadili.wheresmyplaces.DBFragmentActivity;
import com.elfadili.wheresmyplaces.adapter.MapInfoAdapter;
import com.elfadili.wheresmyplaces.dataMng.TotalDataManager;

public class FragmentMyLocation extends SupportMapFragment implements IMarocPlaceExplorerConstants, OnMarkerClickListener, ILastLocationFinder {

	public static final String TAG = FragmentMyLocation.class.getSimpleName();
	public static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1111;
	
	private RelativeLayout mRootlayout;
	private boolean isFindView;

	private DBFragmentActivity mContext;
	private GoogleMap mMap;

	private MapInfoAdapter mMapAdapter;
	private DBLastLocationFinder mDBDbLastLocationFinder;
	private int currentTimeOut;
	private MarkerOptions mCurrentLocationMarker;
	private CircleOptions mCircleLocationMarker;
	private LatLng mCurrentLatLng;
	private View mSupportMapView;
	private boolean isStartFindingLocation;
	private int mCurrentMapType=GoogleMap.MAP_TYPE_NORMAL; 
	
	private Address mLocationAddress;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mSupportMapView = super.onCreateView(inflater, container, savedInstanceState);
		this.mRootlayout = (RelativeLayout) inflater.inflate(R.layout.fragment_my_location, container, false);
		this.mRootlayout.addView(mSupportMapView, 0);
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
		this.mContext = (DBFragmentActivity) getActivity();
		Button mBtMyLocation = (Button) mRootlayout.findViewById(R.id.bt_my_location);
		mBtMyLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startProcessFinder();
			}
		});
		this.setUpMapIfNeeded();
		this.startProcessFinder();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
	}
	
	private void startProcessFinder(){
		if(mMap!=null){
			if(!isStartFindingLocation){
				isStartFindingLocation=true;
				try {
					if(mDBDbLastLocationFinder!=null){
						mDBDbLastLocationFinder.stopGps();
						mDBDbLastLocationFinder =null;
					}
					mDBDbLastLocationFinder = new DBLastLocationFinder(mContext);
					mDBDbLastLocationFinder.setOnLocationFinderListener(FragmentMyLocation.this);
					mDBDbLastLocationFinder.startGps();
					Toast.makeText(mContext, R.string.info_process_find_location, Toast.LENGTH_SHORT).show();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{
				Toast.makeText(mContext, R.string.info_process_find_location, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	

	private void setUpMapIfNeeded() {
		int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, mContext, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
		}
		else{
			if (mMap == null) {
				mMap = getMap();
				DBLog.d(TAG, "==============>mMap="+mMap);
			}
		}
	}
	

	private void setUpMap() {
		if(mCurrentLocationMarker==null || mCurrentLatLng==null){
			isStartFindingLocation=false;
			return;
		}
		if(mMapAdapter!=null){
			mMap.setInfoWindowAdapter(null);
			mMapAdapter=null;
		}
		try {
			mMap.clear();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMapAdapter =new MapInfoAdapter(mContext);
		mMap.setInfoWindowAdapter(mMapAdapter);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker mMarker) {
				showDialogLocationDetails();
			}
		});
		mMap.addCircle(mCircleLocationMarker);
		mMap.addMarker(mCurrentLocationMarker);
		
		final View mapView = mSupportMapView;
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				@SuppressLint("NewApi")
				@Override
				public void onGlobalLayout() {
					Builder mMapBuilder = new LatLngBounds.Builder();
					mMapBuilder.include(mCurrentLatLng);
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
					else {
						mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBuilder.build(), 50));
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, DEFAULT_ZOOM_LEVEL));
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
		try {
			if(mDBDbLastLocationFinder!=null){
				mDBDbLastLocationFinder.stopGps();
				mDBDbLastLocationFinder=null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		mCurrentLatLng=null;
		mCurrentLocationMarker=null;
		super.onDestroy();
	}

	@Override
	public void onLocationError(Location mLocation) {
		if(currentTimeOut<TIME_OUT){
			startGetLocation();
		}
		else{
			currentTimeOut=0;
			drawMyLocation(mLocation);
		}
	}

	@Override
	public void onLocationSuccess(Location mLocation) {
		currentTimeOut=0;
		drawMyLocation(mLocation);
	}

	@Override
	public void onError() {
		if(currentTimeOut<TIME_OUT){
			startGetLocation();
		}
		else{
			currentTimeOut=0;
			Location mLocation = mDBDbLastLocationFinder.getLastConfigLocation();
			if(mLocation!=null){
				drawMyLocation(mLocation);
			}
			else{
				mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mContext.dismissProgressDialog();
						Toast.makeText(mContext, R.string.info_get_location_error, Toast.LENGTH_LONG).show();
						if(mDBDbLastLocationFinder!=null){
							mDBDbLastLocationFinder.stopGps();
						}
					}
				});
			}
		}
	}
	
	public void startGetLocation() {
		if (currentTimeOut < TIME_OUT) {
			currentTimeOut = currentTimeOut + 1;
			mDBDbLastLocationFinder.startGps();
		}
	}
	
	private synchronized void drawMyLocation(final Location mLocation) {
		if(mMap!=null && mDBDbLastLocationFinder!=null){
			TotalDataManager.getInstance().setCurrentLocation(mLocation);
			mContext.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					IDBTaskListener mDBTaskListener = new IDBTaskListener() {
						private String mLocationName;
						
						@Override
						public void onPreExcute() {
							mContext.showProgressDialog();
						}
						
						@Override
						public void onDoInBackground() {
							mLocationAddress = mDBDbLastLocationFinder.getAddressLocation(mLocation);
							mLocationName = mDBDbLastLocationFinder.getNameLocation(mLocationAddress);
							if(StringUtils.isStringEmpty(mLocationName)){
								mLocationName=getString(R.string.title_unknown_location);
							}
						}
						@Override
						public void onPostExcute() {
							mContext.dismissProgressDialog();
							try {
								mCurrentLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
								mCurrentLocationMarker = new MarkerOptions().position(mCurrentLatLng).title(mContext.getString(R.string.title_my_location)).snippet(mLocationName)
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_my_location));
								
								mCircleLocationMarker = new CircleOptions().center(mCurrentLatLng).radius(DEFAULT_RADIUS_MARKER)
										.fillColor(FILL_COLOR).strokeColor(STROKE_COLOR).strokeWidth(DEFAULT_STROKE_MARKER_WIDTH);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
							if(mDBDbLastLocationFinder!=null){
								mDBDbLastLocationFinder.stopGps();
							}
							setUpMap();
							isStartFindingLocation=false;
						}
					};
					DBTask mDBTask = new DBTask(mDBTaskListener);
					mDBTask.execute();
				}
			});
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
	public void showDialogLocationDetails(){
		if(mMap!=null){
			if(mLocationAddress==null){
				if(mCurrentLatLng==null){
					Toast.makeText(mContext, R.string.title_unknown_location, Toast.LENGTH_SHORT).show();
				}
				else{
					StringBuilder mStringBuilder = new StringBuilder();
					String lat = String.valueOf(mCurrentLatLng.latitude);
					mStringBuilder.append(String.format(getString(R.string.format_address), getString(R.string.title_unknown_location))+"\n");
					if(!StringUtils.isStringEmpty(lat)){
						mStringBuilder.append(String.format(getString(R.string.format_lat), lat)+"\n");
					}
					String lon = String.valueOf(mCurrentLatLng.longitude);
					if(!StringUtils.isStringEmpty(lon)){
						mStringBuilder.append(String.format(getString(R.string.format_long), lon)+"\n");
					}
					showInfoDialogWith(mStringBuilder.toString(), R.string.menu_location_details);
				}
			}
			else{
				StringBuilder mStringBuilder = new StringBuilder();
				String addressText = String.format("%s", mLocationAddress.getMaxAddressLineIndex() > 0 ? mLocationAddress.getAddressLine(0) : "");
				if (!StringUtils.isStringEmpty(addressText)) {
					mStringBuilder.append(String.format(getString(R.string.format_address), addressText)+"\n");
				}
				String area = mLocationAddress.getLocality();
				if(!StringUtils.isStringEmpty(area)){
					mStringBuilder.append(String.format(getString(R.string.format_area), area)+"\n");
				}
				String countryName = mLocationAddress.getCountryName();
				if(!StringUtils.isStringEmpty(countryName)){
					mStringBuilder.append(String.format(getString(R.string.format_country), countryName)+"\n\n");
				}
				String countryCode = mLocationAddress.getCountryCode();
				if(!StringUtils.isStringEmpty(countryCode)){
					mStringBuilder.append(String.format(getString(R.string.format_country_code), countryCode)+"\n");
				}
				String lat = String.valueOf(mCurrentLatLng.latitude);
				if(!StringUtils.isStringEmpty(lat)){
					mStringBuilder.append(String.format(getString(R.string.format_lat), lat)+"\n");
				}
				String lon = String.valueOf(mCurrentLatLng.longitude);
				if(!StringUtils.isStringEmpty(lon)){
					mStringBuilder.append(String.format(getString(R.string.format_long), lon));
				}
				showInfoDialogWith(mStringBuilder.toString(), R.string.menu_location_details);
			}
		}
		
	}
	
	private void showInfoDialogWith(String message,int titleId){
		if(message==null || message.equals("")){
			new Exception(TAG+" createEmptyDialog:name can not null").printStackTrace();
			return;
		}
		AlertDialogUtils.createInfoDialog(mContext, android.R.drawable.ic_dialog_info, titleId, android.R.string.ok, message, null).show();
	}
}
