package com.elfadili.wheresmyplaces.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ypyproductions.bitmap.ImageFetcher;
import com.ypyproductions.utils.StringUtils;
import com.places.nearby.R;
import com.elfadili.wheresmyplaces.constanst.IWhereMyLocationConstants;
import com.elfadili.wheresmyplaces.object.PlaceObject;
import com.elfadili.wheresmyplaces.object.PlacePhotoObject;
import com.elfadili.wheresmyplaces.object.RouteObject;
import com.elfadili.wheresmyplaces.settings.SettingManager;

public class PlaceAdapter extends BaseAdapter implements IWhereMyLocationConstants {
	public static final String TAG = PlaceAdapter.class.getSimpleName();

	private Context mContext;
	private ArrayList<PlaceObject> listPlaceObjects;

	private Typeface mTypefaceBold;
	private Typeface mTypefaceLight;
	private ImageFetcher mImageFetcher;

	public PlaceAdapter(Context mContext, ArrayList<PlaceObject> listPlaceObjects,Typeface mTypefaceBold,
			Typeface mTypefaceLight, ImageFetcher mImageFetcher) {
		this.mContext = mContext;
		this.listPlaceObjects = listPlaceObjects;
		this.mTypefaceBold=mTypefaceBold;
		this.mTypefaceLight=mTypefaceLight;
		this.mImageFetcher = mImageFetcher;
	}

	@Override
	public int getCount() {
		if (listPlaceObjects != null) {
			return listPlaceObjects.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		if (listPlaceObjects != null) {
			return listPlaceObjects.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder mHolder;
		LayoutInflater mInflater;
		if (convertView == null) {
			mHolder = new ViewHolder();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_location, null);
			convertView.setTag(mHolder);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name_location);
		mHolder.mTvVicinity = (TextView) convertView.findViewById(R.id.tv_sumary);
		mHolder.mTvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
		mHolder.mImgPlace = (ImageView) convertView.findViewById(R.id.img_location);
		mHolder.mRatingBar = (RatingBar) convertView.findViewById(R.id.ratingBar1);
		
		PlaceObject mPlaceObject = listPlaceObjects.get(position);
		mHolder.mTvName.setText(mPlaceObject.getName());
		mHolder.mTvName.setTypeface(mTypefaceBold);
		
		if(!StringUtils.isStringEmpty(mPlaceObject.getVicinity())){
			mHolder.mTvVicinity.setText(mPlaceObject.getVicinity());
		}
		else{
			mHolder.mTvVicinity.setText(R.string.title_unknown_address);
		}
		mHolder.mTvVicinity.setTypeface(mTypefaceLight);
		
		String distance="";
		RouteObject mRouteObject = mPlaceObject.getRouteObject();
		if(mRouteObject!=null){
			distance=mRouteObject.getDistance();
		}
		if(StringUtils.isStringEmpty(distance)){
			String metric = SettingManager.getMetric(mContext);
			if(metric.equals(UNIT_KILOMETTER)){
				distance=String.format(mContext.getString(R.string.info_distance_km_format), String.valueOf(mPlaceObject.getDistance()));
			}
			else if(metric.equals(UNIT_MILE)){
				float convertDistance =  Math.round((float)mPlaceObject.getDistance()/ONE_MILE);
				distance=String.format(mContext.getString(R.string.info_distance_miles_format), String.valueOf(convertDistance));
			}
		}
		mHolder.mTvDistance.setText(distance);
		mHolder.mTvDistance.setTypeface(mTypefaceLight);
		addContentWhichHasItalic(mHolder.mTvDistance, distance);
		
		mHolder.mRatingBar.setRating(mPlaceObject.getRating());
		String urlPhoto =mPlaceObject.getIcon();
		ArrayList<PlacePhotoObject> mListPhotoObjects = mPlaceObject.getListPhotoObjects();
		if(mListPhotoObjects!=null && mListPhotoObjects.size()>0){
			PlacePhotoObject mPlacePhotoObject = mListPhotoObjects.get(0);
			String photoRef = mPlacePhotoObject.getPhotoReference();
			if(!StringUtils.isStringEmpty(photoRef)){
				urlPhoto = String.format(FORMAT_URL_PHOTO, photoRef,API_KEY);
			}
		}
		if(!StringUtils.isStringEmpty(urlPhoto)){
			mImageFetcher.loadImage(urlPhoto, mHolder.mImgPlace);
		}
		
		return convertView;
	}
	
	public ArrayList<PlaceObject> getListPlaceObjects() {
		return listPlaceObjects;
	}

	public void setListPlaceObjects(ArrayList<PlaceObject> listPlaceObjects) {
		if(listPlaceObjects!=null){
			if(this.listPlaceObjects!=null && this.listPlaceObjects.size()==0){
				this.listPlaceObjects=null;
			}
			this.listPlaceObjects = listPlaceObjects;
			notifyDataSetChanged();
		}
	}

	public void addContentWhichHasItalic(TextView mTextView, String mData){
		if(mData!=null && !mData.equals("")){
			 SpannableString spanString = new SpannableString(mData);
			 spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
			 mTextView.setText(spanString);
		}
	}

	private static class ViewHolder {
		public TextView mTvName;
		public TextView mTvVicinity;
		public RatingBar mRatingBar;
		public TextView mTvDistance;
		public ImageView mImgPlace;
	}
}
