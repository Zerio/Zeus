package com.ypyproductions.location;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.util.Log;

import com.elfadili.wheresmyplaces.constants.IMarocPlaceExplorerConstants;
import com.google.android.gms.location.LocationListener;
import com.ypyproductions.location.utils.LocationUtils;
import com.ypyproductions.location.utils.SystemUtils;
import com.ypyproductions.utils.DBLog;

public class TrackRecordingService extends Service implements IMarocPlaceExplorerConstants {

	private static final String TAG = TrackRecordingService.class.getSimpleName();
	
	public static final String ACTION_UPDATE_LOCATION = "com.ypyproductions.location.UPDATE_MY_LOCATION";
	public static final String KEY_LNG = "KEY_LNG";
	public static final String KEY_LAT = "KEY_LAT";

	private static final long ONE_SECOND = 1000; 
	private static final long ONE_MINUTE = 60000;

	private ExecutorService executorService;
	
	private Handler mHandlerBroadcastLocation;
	private Handler mHandlerUpdateLocation;
	
	private boolean isFirstTimeTracking;
	
	private MyTracksLocationManager myTracksLocationManager;
	private LocationListenerPolicy locationListenerPolicy;
	private int recordingGpsAccuracy;

	private WakeLock wakeLock;
	private Location lastLocation;
	
	private double lastLng=INVALID_VALUE;
	private double lastLat=INVALID_VALUE;
	private int count=0;
	
	private boolean isStartGetLocation=false;

	private ServiceBinder binder = new ServiceBinder(this);

	private LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			if (myTracksLocationManager == null || executorService == null || executorService.isShutdown()|| executorService.isTerminated()) {
				return;
			}
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						onLocationChangedAsync(location);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	};

	private final Runnable updateLocationRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				registerLocationListener();
				mHandlerUpdateLocation.postDelayed(this, ONE_MINUTE);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private final Runnable broadcastLocationRunnable = new Runnable() {
		@Override
		public void run() {
			Log.d(TAG, "==============>startPost lastLat="+lastLat+"========>lastLong="+lastLng);
			if(lastLat!=INVALID_VALUE & lastLng!=INVALID_VALUE){
				Intent mIntent = new Intent(ACTION_UPDATE_LOCATION);
				mIntent.putExtra(KEY_LAT, lastLat);
				mIntent.putExtra(KEY_LNG, lastLng);
				sendBroadcast(mIntent);
			}
			mHandlerBroadcastLocation.postDelayed(broadcastLocationRunnable, (int)(0.25f*ONE_MINUTE));
		}
	};

	
	@Override
	public void onCreate() {
		super.onCreate();
		executorService = Executors.newSingleThreadExecutor();
		mHandlerUpdateLocation = new Handler();
		mHandlerBroadcastLocation = new Handler();
		
		myTracksLocationManager = new MyTracksLocationManager(this, mHandlerBroadcastLocation.getLooper(), true);
		mHandlerUpdateLocation.post(updateLocationRunnable);
		
        locationListenerPolicy = new AdaptiveLocationListenerPolicy(ONE_SECOND, 30 * ONE_SECOND, 0);
		
//		locationListenerPolicy = new AdaptiveLocationListenerPolicy(30*ONE_SECOND, 5 * ONE_MINUTE, 5);
        recordingGpsAccuracy=200;
        
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		// Reverse order from onCreate
		mHandlerBroadcastLocation.removeCallbacks(broadcastLocationRunnable);
		mHandlerUpdateLocation.removeCallbacks(updateLocationRunnable);
		
		unregisterLocationListener();
		myTracksLocationManager.close();
		myTracksLocationManager = null;
		binder.detachFromService();
		binder = null;
		releaseWakeLock();
		executorService.shutdown();
		super.onDestroy();
	}

	/**
	 * Starts gps.
	 */
	private void startGps() {
		if(!isStartGetLocation){
			isStartGetLocation=true;
			lastLocation = null;
			wakeLock = SystemUtils.acquireWakeLock(this, wakeLock);
			registerLocationListener();
		}
	}

	/**
	 * Stops gps.
	 * 
	 * @param stop
	 *            true to stop self
	 */
	private void stopGps(boolean stop) {
		isStartGetLocation=false;
		unregisterLocationListener();
		releaseWakeLock();
		if (stop) {
			stopSelf();
		}
	}


	/**
	 * Called when location changed.
	 * 
	 * @param location
	 *            the location
	 */
	private void onLocationChangedAsync(Location location) {
		try {
			if (!LocationUtils.isValidLocation(location)) {
				DBLog.d(TAG, "Ignore onLocationChangedAsync. location is invalid.");
				return;
			}
			Log.d(TAG, "===========>onLocationChangedAsync Accuracy="+location.getAccuracy());
			if (!location.hasAccuracy() || location.getAccuracy() >= recordingGpsAccuracy && count<TIME_OUT) {
				count++;
				Log.d(TAG, "Ignore onLocationChangedAsync. Poor accuracy time out="+count);
				return;
			}
			count=0;
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
			lastLat=location.getLatitude();
			lastLng = location.getLongitude();
			
			if(!isFirstTimeTracking){
				isFirstTimeTracking=true;
				mHandlerBroadcastLocation.post(broadcastLocationRunnable);
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
			myTracksLocationManager.requestLocationUpdates(interval, locationListenerPolicy.getMinDistance(), locationListener);
		}
		catch (RuntimeException e) {
			DBLog.e(TAG, "Could not register location listener."+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Unregisters the location manager.
	 */
	private void unregisterLocationListener() {
		if (myTracksLocationManager == null) {
			DBLog.e(TAG, "locationManager is null.");
			return;
		}
		myTracksLocationManager.removeLocationUpdates(locationListener);
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

	private static class ServiceBinder extends ITrackRecordingService.Stub {
		private TrackRecordingService trackRecordingService;
		private DeathRecipient deathRecipient;

		public ServiceBinder(TrackRecordingService trackRecordingService) {
			this.trackRecordingService = trackRecordingService;
		}

		@Override
		public boolean isBinderAlive() {
			return trackRecordingService != null;
		}

		@Override
		public boolean pingBinder() {
			return isBinderAlive();
		}

		@Override
		public void linkToDeath(DeathRecipient recipient, int flags) {
			deathRecipient = recipient;
		}

		@Override
		public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
			if (!isBinderAlive()) {
				return false;
			}
			deathRecipient = null;
			return true;
		}

		/**
		 * Detaches from the track recording service. Clears the reference to
		 * the outer class to minimize the leak.
		 */
		private void detachFromService() {
			trackRecordingService = null;
			attachInterface(null, null);

			if (deathRecipient != null) {
				deathRecipient.binderDied();
			}
		}

		@Override
		public void startTracking() throws RemoteException {
			if(trackRecordingService!=null){
				trackRecordingService.startGps();
			}
		}

		@Override
		public void stopTracking() throws RemoteException {
			if(trackRecordingService!=null){
				trackRecordingService.stopGps(true);
			}
		}
	}
}
