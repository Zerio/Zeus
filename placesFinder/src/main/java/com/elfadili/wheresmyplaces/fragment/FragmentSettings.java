package com.elfadili.wheresmyplaces.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.places.nearby.R;
import com.elfadili.wheresmyplaces.MainActivity;
import com.elfadili.wheresmyplaces.constanst.IWhereMyLocationConstants;
import com.elfadili.wheresmyplaces.settings.SettingManager;
import com.elfadili.wheresmyplaces.view.DBSeekBarView;
import com.elfadili.wheresmyplaces.view.DBSeekBarView.OnSeekBarChangeListener;

public class FragmentSettings extends Fragment implements IWhereMyLocationConstants {
	
	public static final String TAG = FragmentSettings.class.getSimpleName();

	private View mRootView;

	private MainActivity mContext;

	private boolean isFindView;
	private TextView mTvInfoRadius;
	private TextView mTvMinRadius;
	private TextView mTvMaxRadius;

	private DBSeekBarView mDBSeekbar;

	private TextView mTvTitlePiority;

	private TextView mTvTitleMetric;

	private TextView mTvMetric;
	private TextView mTvPiority;

	private TextView mTvTitleTravelMode;

	private TextView mTvTravelMode;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_settings, container, false);
		return mRootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(!isFindView){
			isFindView=true;
			this.findView();
		}
	}
	
	private void findView(){
		this.mContext = (MainActivity) getActivity();
		this.mTvInfoRadius = (TextView) mRootView.findViewById(R.id.tv_radius);
		this.mTvMinRadius = (TextView) mRootView.findViewById(R.id.tv_min_radius);
		this.mTvMaxRadius = (TextView) mRootView.findViewById(R.id.tv_max_radius);
		
		this.mTvTitleMetric = (TextView) mRootView.findViewById(R.id.tv_title_metric);
		this.mTvTitlePiority = (TextView) mRootView.findViewById(R.id.tv_title_piority);
		this.mTvTitleTravelMode = (TextView) mRootView.findViewById(R.id.tv_title_travel_mode);
		
		this.mTvMetric = (TextView) mRootView.findViewById(R.id.tv_metric);
		this.mTvPiority = (TextView) mRootView.findViewById(R.id.tv_piority);
		this.mTvTravelMode = (TextView) mRootView.findViewById(R.id.tv_travel_mode);
		
		this.mTvMinRadius.setTypeface(mContext.mTypeFaceRobotoLight);
		this.mTvMaxRadius.setTypeface(mContext.mTypeFaceRobotoLight);
		this.mTvMetric.setTypeface(mContext.mTypeFaceRobotoLight);
		this.mTvPiority.setTypeface(mContext.mTypeFaceRobotoLight);
		this.mTvTravelMode.setTypeface(mContext.mTypeFaceRobotoLight);
		
		this.mTvInfoRadius.setTypeface(mContext.mTypeFaceRobotoBold);
		this.mTvTitleMetric.setTypeface(mContext.mTypeFaceRobotoBold);
		this.mTvTitlePiority.setTypeface(mContext.mTypeFaceRobotoBold);
		this.mTvTitleTravelMode.setTypeface(mContext.mTypeFaceRobotoBold);
		
		updateInfoRadius(SettingManager.getRadius(mContext),true);
		
		this.mDBSeekbar =(DBSeekBarView)mRootView.findViewById(R.id.dBSeekBarView1);
		this.mDBSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onUpdateProcess(int process) {
				SettingManager.setRadius(mContext, process);
				updateInfoRadius(process,false);
			}
			
			@Override
			public void onSeekBarChangeListener(int process) {
				SettingManager.setRadius(mContext, process);
				updateInfoRadius(process,false);
			}
		});
		mDBSeekbar.setProgress(SettingManager.getRadius(mContext), true);
		RelativeLayout mLayoutMetric = (RelativeLayout) mContext.findViewById(R.id.layout_metric);
		RelativeLayout mLayoutPiority = (RelativeLayout) mContext.findViewById(R.id.layout_piority);
		RelativeLayout mLayoutTravelModes = (RelativeLayout) mContext.findViewById(R.id.layout_travel_mode);
		
		mLayoutMetric.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPopupMetric();
			}
		});
		mLayoutPiority.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPopupPiority();
			}
		});
		mLayoutTravelModes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showTravelMode();
			}
		});
		
		String piority = SettingManager.getPiority(mContext);
		if(piority.equals(PIORITY_DISTANCE)){
			mTvPiority.setText(getString(R.string.title_distance));
		}
		else if(piority.equals(PIORITY_RATING)){
			mTvPiority.setText(getString(R.string.title_rating));
		}
		
		String unit = SettingManager.getMetric(mContext);
		if(unit.equals(UNIT_KILOMETTER)){
			mTvMetric.setText(UNIT_KILOMETTER);
		}
		else if(unit.equals(UNIT_MILE)){
			mTvMetric.setText(UNIT_MILE);
		}
		
		String travelMode = SettingManager.getTravelMode(mContext);
		if(travelMode.equals(TRAVEL_MODE_DRIVING)){
			mTvTravelMode.setText(DRIVING);
		}
		else if(travelMode.equals(TRAVEL_MODE_WALKING)){
			mTvTravelMode.setText(WALKING);
		}
		
	}
	
	private void updateInfoRadius(int process, boolean isInit){
		if(SettingManager.getMetric(mContext).equals(UNIT_KILOMETTER)){
			mTvInfoRadius.setText(String.format(mContext.getString(R.string.format_radius), process,"km"));
			if(isInit){
				mTvMaxRadius.setText(String.valueOf(MAX_RADIUS));
				mTvMinRadius.setText(String.valueOf(MIN_RADIUS));
			}
		}
		else{
			int miles =  (int) ((float)process/ONE_MILE);
			mTvInfoRadius.setText(String.format(mContext.getString(R.string.format_radius), miles,"mi"));
			if(isInit){
				mTvMaxRadius.setText(String.valueOf((int) (MAX_RADIUS/ONE_MILE)));
				mTvMinRadius.setText(String.valueOf((int) (MIN_RADIUS/ONE_MILE)));
			}
		}
	}
	
	private void showPopupPiority(){
		String piority = SettingManager.getPiority(mContext);
		int selected=0;
		if(piority.equals(PIORITY_DISTANCE)){
			selected=0;
		}
		else if(piority.equals(PIORITY_RATING)){
			selected=1;
		}
		AlertDialog.Builder  mBuilder = new AlertDialog.Builder(mContext);
		mBuilder.setTitle(R.string.title_piority);
		mBuilder.setSingleChoiceItems(R.array.sorting_array, selected, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0){
					SettingManager.setPiority(mContext, PIORITY_DISTANCE);
					mTvPiority.setText(getString(R.string.title_distance));
				}
				else if(which==1){
					SettingManager.setPiority(mContext, PIORITY_RATING);
					mTvPiority.setText(getString(R.string.title_rating));
				}
				dialog.dismiss();
			}
		});
		AlertDialog mDialog=mBuilder.create();
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
	}
	private void showPopupMetric(){
		String piority = SettingManager.getMetric(mContext);
		int selected=0;
		if(piority.equals(UNIT_KILOMETTER)){
			selected=0;
		}
		else if(piority.equals(UNIT_MILE)){
			selected=1;
		}
		AlertDialog.Builder  mBuilder = new AlertDialog.Builder(mContext);
		mBuilder.setTitle(R.string.title_units);
		mBuilder.setSingleChoiceItems(R.array.metric_array, selected, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0){
					SettingManager.setMetric(mContext, UNIT_KILOMETTER);
					mTvMaxRadius.setText(String.valueOf(MAX_RADIUS));
					mTvMinRadius.setText(String.valueOf(MIN_RADIUS));
					
					mTvMetric.setText(UNIT_KILOMETTER);
				}
				else if(which==1){
					SettingManager.setMetric(mContext, UNIT_MILE);
					mTvMaxRadius.setText(String.valueOf((int) (MAX_RADIUS/ONE_MILE)));
					mTvMinRadius.setText(String.valueOf((int) (MIN_RADIUS/ONE_MILE)));
					
					mTvMetric.setText(UNIT_MILE);
				}
				updateInfoRadius(SettingManager.getRadius(mContext),false);
				dialog.dismiss();
			}
		});
		AlertDialog mDialog=mBuilder.create();
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
	}
	private void showTravelMode(){
		String travelMode = SettingManager.getTravelMode(mContext);
		int selected=0;
		if(travelMode.equals(TRAVEL_MODE_DRIVING)){
			selected=0;
		}
		else if(travelMode.equals(TRAVEL_MODE_WALKING)){
			selected=1;
		}
		AlertDialog.Builder  mBuilder = new AlertDialog.Builder(mContext);
		mBuilder.setTitle(R.string.title_travel_modes);
		mBuilder.setSingleChoiceItems(R.array.travel_mode_array, selected, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0){
					SettingManager.setTravelMode(mContext, TRAVEL_MODE_DRIVING);
					mTvTravelMode.setText(DRIVING);
				}
				else if(which==1){
					SettingManager.setTravelMode(mContext, TRAVEL_MODE_WALKING);
					mTvTravelMode.setText(WALKING);
				}
				dialog.dismiss();
			}
		});
		AlertDialog mDialog=mBuilder.create();
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
	}
	
}
