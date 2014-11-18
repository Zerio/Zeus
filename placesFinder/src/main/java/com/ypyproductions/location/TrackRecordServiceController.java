package com.ypyproductions.location;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.ypyproductions.net.task.IDBCallback;
import com.ypyproductions.utils.DBLog;

public class TrackRecordServiceController {
	
	public static final String TAG = TrackRecordServiceController.class.getSimpleName();
	
	private static TrackRecordServiceController mTrackRecordingServiceController=null;
	
	private static boolean mIsBound;
	private ServiceConnection mTrackServiceConnection;
	protected ITrackRecordingService mTrackServiceBinder;
	public static boolean isStartedService=false;
	
	private TrackRecordServiceController() {
		
	}
	
	public static TrackRecordServiceController getInstance(){
		if(mTrackRecordingServiceController==null){
			mTrackRecordingServiceController = new TrackRecordServiceController();
		}
		return mTrackRecordingServiceController;
	}
	
	/**
     * Bind to audio service if it is running
     */
    public void bindTrackingService(Context mContext,final IDBCallback mDBCallback) {
        if (!mIsBound) {
            Intent service = new Intent(mContext, TrackRecordingService.class);
            if(!isStartedService){
            	isStartedService=true;
            	mContext.startService(service);
            }
            mTrackServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    DBLog.d(TAG, "===========>Service Disconnected");
                    mTrackServiceBinder = null;
                    mIsBound = false;
                }
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (!mIsBound) {
                    	return;
                    }
                    DBLog.d(TAG, "==============>Service Connected");
                    mTrackServiceBinder = ITrackRecordingService.Stub.asInterface(service);
                    if(mDBCallback!=null){
                    	mDBCallback.onAction();
                    }
                }
            };
            mIsBound = mContext.bindService(service, mTrackServiceConnection, Context.BIND_AUTO_CREATE);
        } 
    }

    public void unbindTrackingServiceAndStop(Context context) {
        if (mIsBound) {
            mIsBound = false;
            context.stopService(new Intent(context, TrackRecordingService.class));
            context.unbindService(mTrackServiceConnection);
            mTrackServiceBinder = null;
            mTrackServiceConnection = null;
        }
    }
    public void unbindTrackingService(Context context) {
    	if (mIsBound) {
    		mIsBound = false;
    		context.unbindService(mTrackServiceConnection);
    		mTrackServiceBinder = null;
    	}
    }
    
    public void stopTracking(){
    	if(mTrackServiceBinder!=null){
    		try {
				mTrackServiceBinder.stopTracking();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    }
    public void startTracking(){
    	if(mTrackServiceBinder!=null){
    		try {
    			mTrackServiceBinder.startTracking();
    		}
    		catch (RemoteException e) {
    			e.printStackTrace();
    		}
    	}
    }
    public void onDestroy(){
    	isStartedService=false;
    	mTrackRecordingServiceController=null;
    }
}
