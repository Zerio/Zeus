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

import com.elfadili.wheresmyplaces.constants.IMarocPlaceExplorerConstants;
import com.ypyproductions.bitmap.ImageFetcher;
import com.ypyproductions.utils.StringUtils;
import com.places.nearby.R;
import com.elfadili.wheresmyplaces.dataMng.TotalDataManager;
import com.elfadili.wheresmyplaces.object.PlaceObject;

public class FavoritePlaceAdapter extends BaseAdapter implements IMarocPlaceExplorerConstants {
	public static final String TAG = FavoritePlaceAdapter.class.getSimpleName();

	private Context mContext;
	private ArrayList<PlaceObject> listPlaceObjects;

	private Typeface mTypefaceBold;
	private Typeface mTypefaceLight;
	private ImageFetcher mImageFetcher;

	private OnFavoriteAdapterListener mFavoriteAdapterListener;

	public FavoritePlaceAdapter(Context mContext, ArrayList<PlaceObject> listPlaceObjects,Typeface mTypefaceBold,
			Typeface mTypefaceLight, ImageFetcher mImageFetcher) {
		this.mContext = mContext;
		this.listPlaceObjects = listPlaceObjects;
		this.mTypefaceBold = mTypefaceBold;
		this.mTypefaceLight = mTypefaceLight;
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
			convertView = mInflater.inflate(R.layout.item_favorite_location, null);
			convertView.setTag(mHolder);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name_location);
		mHolder.mTvVicinity = (TextView) convertView.findViewById(R.id.tv_sumary);
		mHolder.mImgCategory = (ImageView) convertView.findViewById(R.id.img_cat);
		mHolder.mImgPlace = (ImageView) convertView.findViewById(R.id.img_location);
		mHolder.mRatingBar = (RatingBar) convertView.findViewById(R.id.ratingBar1);
		
		final PlaceObject mPlaceObject = listPlaceObjects.get(position);
		mHolder.mTvName.setText(mPlaceObject.getName());
		mHolder.mTvName.setTypeface(mTypefaceBold);
		
		if(!StringUtils.isStringEmpty(mPlaceObject.getVicinity())){
			mHolder.mTvVicinity.setText(mPlaceObject.getVicinity());
		}
		else{
			mHolder.mTvVicinity.setText(R.string.title_unknown_address);
		}
		mHolder.mTvVicinity.setTypeface(mTypefaceLight);
		mHolder.mImgCategory.setImageResource(TotalDataManager.getInstance().getResIconMapPin(mContext, mPlaceObject.getCategory()));
		
		mHolder.mRatingBar.setRating(mPlaceObject.getRating());
		
//		mHolder.mImgCategory.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(!mHolder.mImgCategory.isChecked()){
//					if(mFavoriteAdapterListener!=null){
//						mFavoriteAdapterListener.onDeletePlace(mPlaceObject);
//					}
//				}
//			}
//		});
		
		String urlPhoto=null;
		String photoRef = mPlaceObject.getPhotoRef();
		if(!StringUtils.isStringEmpty(photoRef)){
			urlPhoto = String.format(FORMAT_URL_PHOTO, photoRef,API_KEY);
		}
		else{
			urlPhoto =mPlaceObject.getIcon();
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
	
	public void setOnFavorieteAdapterListener(OnFavoriteAdapterListener mFavoriteAdapterListener){
		this.mFavoriteAdapterListener=mFavoriteAdapterListener;
	}
	
	public interface OnFavoriteAdapterListener{
		public void onDeletePlace(PlaceObject mPlaceObject);
	}

	private static class ViewHolder {
		public TextView mTvName;
		public TextView mTvVicinity;
		public RatingBar mRatingBar;
		public ImageView mImgCategory;
		public ImageView mImgPlace;
	}
}
