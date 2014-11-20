package com.elfadili.wheresmyplaces.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elfadili.wheresmyplaces.constanst.IMarocPlaceExplorerConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ypyproductions.location.DBLastLocationFinder;
import com.ypyproductions.location.utils.LocationUtils;
import com.ypyproductions.net.task.DBTask;
import com.ypyproductions.net.task.IDBConstantURL;
import com.ypyproductions.net.task.IDBTaskListener;
import com.ypyproductions.slidinguppanel.SlidingUpPanelLayout;
import com.ypyproductions.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.ResolutionUtils;
import com.ypyproductions.utils.StringUtils;
import com.places.nearby.R;
import com.elfadili.wheresmyplaces.DetailLocationActivity;
import com.elfadili.wheresmyplaces.adapter.MapInfoAdapter;
import com.elfadili.wheresmyplaces.dataMng.TotalDataManager;
import com.elfadili.wheresmyplaces.dataMng.YPYNetUtils;
import com.elfadili.wheresmyplaces.object.PlaceDetailObject;
import com.elfadili.wheresmyplaces.object.PlaceObject;
import com.elfadili.wheresmyplaces.object.RouteObject;
import com.elfadili.wheresmyplaces.object.StepObject;
import com.elfadili.wheresmyplaces.settings.SettingManager;

public class FragmentLocationDetailMap extends SupportMapFragment implements IMarocPlaceExplorerConstants, IDBConstantURL {

	public static final String TAG = FragmentLocationDetailMap.class.getSimpleName();
	public static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1111;
	public static final float ZOOM_LEVEL = 13;

	private RelativeLayout mRootView;
	private DetailLocationActivity mContext;

	private boolean isFindView;
	private GoogleMap mMap;

	private View mSupportMapView;

	private SlidingUpPanelLayout mSlideLayout;
	private MarkerOptions mCurrentLocationMarker;
	private PlaceObject mCurrentPlaceObject;
	private MarkerOptions mDestiLocationMarker;
	private MapInfoAdapter mMapAdapter;
	private PlaceDetailObject mPlaceDetailObject;
	private boolean isSetUpMap;
	private RouteObject mRoutObject;
	
	private TextView mTvDistanceDuration;
	private TextView mTvSummary;
	private LinearLayout mLayoutListDirections;
	private ImageView mImgIndicator;
	
	private int mCurrentMapType=GoogleMap.MAP_TYPE_NORMAL; 
	private DBLastLocationFinder mDBLocationFinder;
	private String mLocationName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mSupportMapView = super.onCreateView(inflater, container, savedInstanceState);
		this.mRootView = (RelativeLayout) inflater.inflate(R.layout.fragment_location_detail_map, container, false);
		this.mSlideLayout = (SlidingUpPanelLayout) mRootView.findViewById(R.id.layout_slidingup);
		this.mSlideLayout.addView(mSupportMapView, 0);
		return mRootView;
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
		this.mContext = (DetailLocationActivity) getActivity();
		this.mTvDistanceDuration =(TextView) mRootView.findViewById(R.id.tv_duration_distance);
		this.mTvSummary =(TextView) mRootView.findViewById(R.id.tv_sumary);
		this.mLayoutListDirections = (LinearLayout) mRootView.findViewById(R.id.list_directions);
		this.mImgIndicator =(ImageView) mRootView.findViewById(R.id.img_indicator);
		
		this.mDBLocationFinder = new DBLastLocationFinder(mContext);
		
		this.mTvDistanceDuration.setText("");
		this.mTvDistanceDuration.setTypeface(mContext.mTypeFaceRobotoLight);
		
		this.mTvSummary.setText("");
		this.mTvSummary.setTypeface(mContext.mTypeFaceRobotoLight);
		
		this.mSlideLayout = (SlidingUpPanelLayout) mRootView.findViewById(R.id.layout_slidingup);
		this.mSlideLayout.setAnchorPoint(1f);
		this.mSlideLayout.setEnableDragViewTouchEvents(true);
		this.mSlideLayout.setDragView(mRootView.findViewById(R.id.layout_info));
		this.mSlideLayout.setPanelSlideListener(new PanelSlideListener() {
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				DBLog.d(TAG, "onPanelSlide, offset " + slideOffset);
			}

			@Override
			public void onPanelExpanded(View panel) {
				DBLog.d(TAG, "onPanelExpanded");
				mImgIndicator.setImageResource(R.drawable.ic_action_expand);

			}

			@Override
			public void onPanelCollapsed(View panel) {
				DBLog.d(TAG, "onPanelCollapsed");
				mImgIndicator.setImageResource(R.drawable.ic_action_collapse);

			}

			@Override
			public void onPanelAnchored(View panel) {
				DBLog.d(TAG, "onPanelAnchored");

			}
		});
		this.mCurrentPlaceObject = mContext.mCurrentPlaceObject;
		this.mPlaceDetailObject = mContext.mPlaceDetailObject;
		this.mRoutObject = mContext.mRouteObject;
		this.setUpMapIfNeeded();

	}
	
	private void setUpInfoForSlidingPanel(){
		String distance="";
		if(mRoutObject==null){
			String metric = SettingManager.getMetric(mContext);
			if(metric.equals(UNIT_KILOMETTER)){
				distance="~ "+String.valueOf(mCurrentPlaceObject.getDistance())+" km";
			}
			else if(metric.equals(UNIT_MILE)){
				float convertDistance =  Math.round((float)mCurrentPlaceObject.getDistance()/ONE_MILE);
				distance="~ "+String.valueOf(convertDistance)+" mi";
			}
		}
		else{
			distance= mRoutObject.getDistance();
		}
		String duration = mRoutObject.getDuration();
		String mInfoDistanceDuration= String.format(getString(R.string.format_duration_distance), duration,distance);
		mTvDistanceDuration.setText(Html.fromHtml(mInfoDistanceDuration));
		
		if(!StringUtils.isStringEmpty(mRoutObject.getSummary())){
			String mInfoSummary = String.format(getString(R.string.format_sumary),mRoutObject.getSummary());
			mTvSummary.setText(Html.fromHtml(mInfoSummary));
		}
		else{
			mTvSummary.setVisibility(View.GONE);
		}
		
		ArrayList<StepObject> mListStepObject = mRoutObject.getListStepObjects();
		if(mListStepObject!=null && mListStepObject.size()>0){
			int size = mListStepObject.size();
			addItemDirections(getString(R.string.title_my_location), "", R.drawable.start);
			for(int i=0;i<size;i++){
				StepObject mStepObject = mListStepObject.get(i);
				String mDescription = mStepObject.getDescription();
				String mDistance = mStepObject.getDistance();
				addItemDirections(mDescription, mDistance, 0);
			}
			addItemDirections(mPlaceDetailObject.getAddress(), "", TotalDataManager.getInstance().getResIconMapPin(mContext, mCurrentPlaceObject.getCategory()));
		}
	}

	private void setUpMapIfNeeded() {
		int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, mContext, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
		}
		else {
			if (mMap == null) {
				mMap = getMap();
			}
		}
	}

	public void startRoute() {
		if (mRoutObject == null) {
			if (!ApplicationUtils.isOnline(mContext)) {
				mContext.showDialogFragment(DIALOG_LOSE_CONNECTION);
				return;
			}
			DBTask mDbTask = new DBTask(new IDBTaskListener() {

				@Override
				public void onPreExcute() {
					mContext.showProgressDialog(String.format(getString(R.string.info_format_process_detail_location), mCurrentPlaceObject.getName()));
				}

				@Override
				public void onDoInBackground() {
					mRoutObject = YPYNetUtils.getRouteObject(mContext, mContext.mCurrentLocation, mCurrentPlaceObject.getLocation());
				}

				@Override
				public void onPostExcute() {
					mContext.dismissProgressDialog();
					if (mRoutObject == null) {
						Toast.makeText(mContext, R.string.info_server_error, Toast.LENGTH_LONG).show();
						return;
					}
					mContext.mRouteObject=mRoutObject;
					mCurrentPlaceObject.setRouteObject(mRoutObject);
					drawMyLocation();
				}

			});
			mDbTask.execute();
		}
		else {
			drawMyLocation();
		}
	}

	public void setUpMap() {
		if(mRoutObject==null || mContext.mCurrentLocation==null || mMap==null || isSetUpMap){
			return;
		}
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMapAdapter =new MapInfoAdapter(mContext);
		mMap.setInfoWindowAdapter(mMapAdapter);
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
				return false;
			}
		});
		
		final LatLng mCurrentLatLng = new LatLng(mContext.mCurrentLocation.getLatitude(), mContext.mCurrentLocation.getLongitude());
		mCurrentLocationMarker = new MarkerOptions().position(mCurrentLatLng).title(mContext.getString(R.string.title_my_location)).snippet(mLocationName)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_my_location));
		mMap.addMarker(mCurrentLocationMarker);
		
		int resId = TotalDataManager.getInstance().getResIconMapPin(mContext, mCurrentPlaceObject.getCategory());
		final LatLng mDestinationLatLng = new LatLng(mCurrentPlaceObject.getLocation().getLatitude(), mCurrentPlaceObject.getLocation().getLongitude());
		mDestiLocationMarker = new MarkerOptions().position(mDestinationLatLng).title(mPlaceDetailObject.getName()).snippet(mPlaceDetailObject.getAddress())
				.icon(BitmapDescriptorFactory.fromResource(resId));
		mMap.addMarker(mDestiLocationMarker);
		
		this.isSetUpMap=true;
		
		final Builder mMapBuilder = new LatLngBounds.Builder();
		mMapBuilder.include(mCurrentLatLng);
		mMapBuilder.include(mDestinationLatLng);
		
		ArrayList<LatLng> mListLatLng = LocationUtils.decodePoly(mRoutObject.getOverViewPolyline());
		if(mListLatLng!=null && mListLatLng.size()>0){
			PolylineOptions mPolylineOptions = new PolylineOptions();
			mPolylineOptions.width(ResolutionUtils.convertDpToPixel(mContext, 4));
			mPolylineOptions.color(Color.RED);
			for(LatLng mLatLng:mListLatLng){
				mPolylineOptions.add(mLatLng);
			}
			mMap.addPolyline(mPolylineOptions);
		}
		
		this.setUpInfoForSlidingPanel();
		
		final View mapView = mSupportMapView;
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				@SuppressLint("NewApi")
				@Override
				public void onGlobalLayout() {
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
					else {
						mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					LatLngBounds bounds = mMapBuilder.build();  
					CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
					mMap.moveCamera(cu);
					mMap.animateCamera(cu);
					
					LatLng ne = bounds.northeast;
					LatLng sw = bounds.southwest;
					LatLng center = new LatLng((ne.latitude + sw.latitude) / 2, (ne.longitude + sw.longitude) / 2);
					
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center,ZOOM_LEVEL));
				}
			});
		}

	}
	
	private void addItemDirections(String description,String distance,int resIconId){
		RelativeLayout mRelativeLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.item_directions, null);
		TextView mTvDes = (TextView) mRelativeLayout.findViewById(R.id.tv_descriptions);
		TextView mTvDistance = (TextView) mRelativeLayout.findViewById(R.id.tv_distance);
		ImageView mImgIcon = (ImageView) mRelativeLayout.findViewById(R.id.img_icon);
		
		description = description.replaceAll("<b>", "");
		description = description.replaceAll("</b>", "");
		description = description.replaceAll("<[^>]*>", "|");
		String [] datas = description.split("\\|+");
		
		if(datas!=null && datas.length>=2){
			description="";
			for(int i=0;i<datas.length;i++){
				if(!StringUtils.isStringEmpty(datas[i])){
					if(i!=datas.length-1){
						description=description+datas[i]+"<br>";
					}
					else{
						description=description+datas[i];
					}
				}
			}
		}
		DBLog.d(TAG, "===========>description="+description);
		mTvDes.setText(Html.fromHtml(description));
		mTvDes.setTypeface(mContext.mTypeFaceRobotoLight);
		
		if(!StringUtils.isStringEmpty(distance)){
			mTvDistance.setText(distance);
			mTvDistance.setTypeface(mContext.mTypeFaceRobotoLight);
		}
		else{
			mTvDistance.setVisibility(View.GONE);
		}
		
		if(resIconId!=0){
			mImgIcon.setImageResource(resIconId);
		}
		else{
			mImgIcon.setVisibility(View.INVISIBLE);
		}
		
		LayoutParams mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		if(mLayoutListDirections.getChildCount()!=0){
			mLayoutParams.topMargin=(int) ResolutionUtils.convertDpToPixel(mContext, 2);
		}
		mLayoutListDirections.addView(mRelativeLayout, mLayoutParams);
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
	
	private void drawMyLocation() {
		if (mMap != null) {
			final Location mLocation = TotalDataManager.getInstance().getCurrentLocation();
			if(mLocation!=null){
				if(StringUtils.isStringEmpty(mLocationName)){
					IDBTaskListener mDBTaskListener = new IDBTaskListener() {
						protected Address mLocationAddress;
						
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
	}
}
