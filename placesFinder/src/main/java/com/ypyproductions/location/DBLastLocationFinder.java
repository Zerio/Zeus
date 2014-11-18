package com.ypyproductions.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.ypyproductions.location.utils.LocationUtils;
import com.ypyproductions.location.utils.SystemUtils;
import com.ypyproductions.utils.DBLog;

/**
 * DBLastLocationFinder
 * @author       
 * 
 * @Company      
 * @Date         Mar 8, 2013
 * @Packagename  com.apex.fishandbrag.location
 */
public class DBLastLocationFinder implements com.google.android.gms.location.LocationListener {

	protected static String TAG = "LastLocationFinder";
	protected static String SINGLE_LOCATION_UPDATE_ACTION = "com.db.places.SINGLE_LOCATION_UPDATE_ACTION";
	public static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final long ONE_SECOND = 1000;

	protected LocationManager locationManager;
	protected Context context;
	
	private Handler mHandler;
	private MyTracksLocationManager myTracksLocationManager;
	
	private WakeLock wakeLock;
	private Location lastLocation;
	private AdaptiveLocationListenerPolicy locationListenerPolicy;
	private int recordingGpsAccuracy;
	private ExecutorService executorService;
	private ILastLocationFinder mLastLocationFinder;
	
	public DBLastLocationFinder(Context context) {
		this.context = context;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mHandler =new Handler();
		myTracksLocationManager = new MyTracksLocationManager(context, mHandler.getLooper(), true);
		
	    locationListenerPolicy = new AdaptiveLocationListenerPolicy(ONE_SECOND, 30 * ONE_SECOND, 0);
        recordingGpsAccuracy=200;
        executorService = Executors.newSingleThreadExecutor();
	}

	
	public boolean isBetterLocation(Location mLocation, int minTime, int minDistance) {
		float accuracy = mLocation.getAccuracy();
		long time = mLocation.getTime();
		Log.d(TAG,"================>new accuracy="+accuracy);
		if ((time < minTime || accuracy > minDistance)) {
			return false;
		}
		return true;
	}
	
	public Location getLastConfigLocation(){
		return lastLocation;
	}
	
	public String getNameLocation(Location mLocation) {
		Address mAddress = getAddressLocation(mLocation);
		return getNameLocation(mAddress);
	}
	
	public String getNameLocation(Address mAddress) {
		if(mAddress!=null){
			String localy = mAddress.getLocality();
			if (localy == null) {
				localy = "";
			}
			String name = mAddress.getCountryName();
			if (name == null) {
				name = "";
			}
			String addressText = String.format("%s", mAddress.getMaxAddressLineIndex() > 0 ? mAddress.getAddressLine(0) : "");
			if (addressText != null && !addressText.equals("")) {
				if (!localy.equals("")) {
					addressText = addressText + "," + localy;
				}
				if (!name.equals("")) {
					addressText = addressText + "," + name;
				}
				return addressText;
			}
		}
		return null;
	}
	
	public Address getAddressLocation(Location mLocation) {
		if (mLocation == null) {
			new Exception(TAG + " getNameLocation:location can not null").printStackTrace();
			return null;
		}
		Geocoder geocoder = new Geocoder(context, Locale.US);
		if (Geocoder.isPresent()) {
			try {
				List<Address> listAddresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
				if (null != listAddresses && listAddresses.size() > 0) {
					Address address = listAddresses.get(0);
					return address;

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	

	
	public void startGps() {
		wakeLock = SystemUtils.acquireWakeLock(context, wakeLock);
		registerLocationListener();
	}

	public void stopGps() {
		try {
			mHandler.removeCallbacksAndMessages(null);
			unregisterLocationListener();
			releaseWakeLock();
			if(myTracksLocationManager!=null){
				myTracksLocationManager.close();
				myTracksLocationManager = null;
			}
			if(executorService!=null){
				executorService.shutdown();
				executorService=null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void onLocationChangedAsync(Location location) {
		try {
			if (!LocationUtils.isValidLocation(location)) {
				DBLog.d(TAG, "Ignore onLocationChangedAsync. location is invalid.");
				if(mLastLocationFinder!=null){
					mLastLocationFinder.onError();
				}
				return;
			}
			Log.d(TAG, "===========>onLocationChangedAsync Accuracy="+location.getAccuracy());
			if (!location.hasAccuracy() || location.getAccuracy() >= recordingGpsAccuracy) {
				Log.d(TAG, "Ignore onLocationChangedAsync. Poor accuracy.");
				if(mLastLocationFinder!=null){
					mLastLocationFinder.onLocationError(location);
				}
				return;
			}
			if (location.getTime() == 0L) {
				location.setTime(System.currentTimeMillis());
			}
			Location lastValidTrackPoint = lastLocation;
			long idleTime = 0L;
			if (lastValidTrackPoint != null && location.getTime() > lastValidTrackPoint.getTime()) {
				idleTime = location.getTime() - lastValidTrackPoint.getTime();
			}
			locationListenerPolicy.updateIdleTime(idleTime);
			
			lastLocation = location;
			double lastLat = location.getLatitude();
			double lastLong = location.getLongitude();
			Log.d(TAG, "==============>update lastLong="+lastLong +"===========>lastLat="+lastLat);
			if(mLastLocationFinder!=null){
				mLastLocationFinder.onLocationSuccess(location);
			}
		}
		catch (Error e) {
			DBLog.e(TAG, "Error in onLocationChangedAsync"+e.getMessage());
			e.printStackTrace();
		}
		catch (RuntimeException e) {
			DBLog.e(TAG, "RuntimeException in onLocationChangedAsync"+e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * Registers the location listener.
	 */
	private void registerLocationListener() {
		if (myTracksLocationManager == null) {
			DBLog.e(TAG, "locationManager is null.");
			return;
		}
		try {
			long interval = locationListenerPolicy.getDesiredPollingInterval();
			myTracksLocationManager.requestLocationUpdates(interval, locationListenerPolicy.getMinDistance(), this);
		}
		catch (RuntimeException e) {
			DBLog.e(TAG, "Could not register location listener."+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Unregisters the location manager.
	 */
	public void unregisterLocationListener() {
		if (myTracksLocationManager == null) {
			DBLog.e(TAG, "locationManager is null.");
			return;
		}
		myTracksLocationManager.removeLocationUpdates(this);
	}

	/**
	 * Releases the wake lock.
	 */
	private void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}

	@Override
	public void onLocationChanged(final Location location) {
		if (myTracksLocationManager == null || executorService == null || executorService.isShutdown()|| executorService.isTerminated()) {
			return;
		}
		executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				onLocationChangedAsync(location);
			}
		});
	}
	
	public void setOnLocationFinderListener(ILastLocationFinder mILastLocationFinder){
		this.mLastLocationFinder=mILastLocationFinder;
	}

	public double[] getLocationFromAddress(String address) {
		if (address == null) {
			new Exception(TAG + " getLocationFromAddress address can not null").printStackTrace();
			return null;
		}
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		List<Address> locationAddress;
		if (Geocoder.isPresent()) {
			try {
				locationAddress = geocoder.getFromLocationName(address, 5);
				if(locationAddress!=null){
					if(locationAddress.size()>0){
						Address mCurrentLocation = locationAddress.get(0);
						double[] mLocationLatLong = new double[2];
						mLocationLatLong[0] = mCurrentLocation.getLatitude();
						mLocationLatLong[1] = mCurrentLocation.getLongitude();
						DBLog.d(TAG, "--------------->lat="+mLocationLatLong[0]);
						DBLog.d(TAG, "--------------->long="+mLocationLatLong[1]);
						return mLocationLatLong;
					}
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public interface ILastLocationFinder {
		  public void onLocationError(Location mLocation);
		  public void onLocationSuccess(Location mLocation);
		  public void onError();
		  
	}
}