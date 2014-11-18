package com.ypyproductions.location;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * My Tracks Location Manager. Applies Google location settings before allowing
 * access to {@link LocationManager}.
 * 
 * @author DoBao
 */
public class MyTracksLocationManager {

	public static final String TAG = MyTracksLocationManager.class.getSimpleName();
	
	private final Handler handler;
	private final LocationClient locationClient;

	private LocationListener requestLastLocation;
	private LocationListener requestLocationUpdates;
	private float requestLocationUpdatesDistance;
	private long requestLocationUpdatesTime;
	
	
	public MyTracksLocationManager(Context context, Looper looper, boolean enableLocaitonClient) {
		this.handler = new Handler(looper);
		if (enableLocaitonClient) {
			locationClient = new LocationClient(context, connectionCallbacks, onConnectionFailedListener);
			locationClient.connect();
		}
		else {
			locationClient = null;
		}
	}
	
	private final ConnectionCallbacks connectionCallbacks = new ConnectionCallbacks() {
		@Override
		public void onDisconnected() {
		}

		@Override
		public void onConnected(Bundle bunlde) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (requestLastLocation != null && locationClient.isConnected()) {
						requestLastLocation.onLocationChanged(locationClient.getLastLocation());
						requestLastLocation = null;
					}
					if (requestLocationUpdates != null && locationClient.isConnected()) {
						LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(requestLocationUpdatesTime)
								.setFastestInterval(requestLocationUpdatesTime).setSmallestDisplacement(requestLocationUpdatesDistance);
						locationClient.requestLocationUpdates(locationRequest, requestLocationUpdates, handler.getLooper());
					}
				}
			});
		}
	};

	private final OnConnectionFailedListener onConnectionFailedListener = new OnConnectionFailedListener() {
		@Override
		public void onConnectionFailed(ConnectionResult connectionResult) {
			
		}
	};
	

	/**
	 * Closes the {@link MyTracksLocationManager}.
	 */
	public void close() {
		if (locationClient != null) {
			locationClient.disconnect();
		}
	}

	/**
	 * Request last location.
	 * 
	 * @param locationListener
	 *            location listener
	 */
	public void requestLastLocation(final LocationListener locationListener) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				requestLastLocation = locationListener;
				connectionCallbacks.onConnected(null);
			}
		});
	}

	/**
	 * Requests location updates. This is an ongoing request, thus the caller
	 * needs to check the status of {@link #isAllowed}.
	 * 
	 * @param minTime
	 *            the minimal time
	 * @param minDistance
	 *            the minimal distance
	 * @param locationListener
	 *            the location listener
	 */
	public void requestLocationUpdates(final long minTime, final float minDistance, final LocationListener locationListener) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				requestLocationUpdatesTime = minTime;
				requestLocationUpdatesDistance = minDistance;
				requestLocationUpdates = locationListener;
				connectionCallbacks.onConnected(null);
			}
		});
	}

	/**
	 * Removes location updates.
	 * 
	 * @param locationListener
	 *            the location listener
	 */
	public void removeLocationUpdates(final LocationListener locationListener) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				requestLocationUpdates = null;
				if (locationClient != null && locationClient.isConnected()) {
					locationClient.removeLocationUpdates(locationListener);
				}
			}
		});
	}
}
