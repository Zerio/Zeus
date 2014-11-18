package com.elfadili.wheresmyplaces.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.places.nearby.R;
import com.elfadili.wheresmyplaces.constanst.IWhereMyLocationConstants;
import com.elfadili.wheresmyplaces.object.AboutUsObject;

public class AboutUsAdapter extends BaseAdapter implements IWhereMyLocationConstants {
	public static final String TAG = AboutUsAdapter.class.getSimpleName();

	private Context mContext;
	private ArrayList<AboutUsObject> listAboutObjects;

	private Typeface mTypefaceBold;
	private Typeface mTypefaceLight;

	public AboutUsAdapter(Context mContext, ArrayList<AboutUsObject> listDrawerObjects,Typeface mTypefaceBold, Typeface mTypefaceLight) {
		this.mContext = mContext;
		this.listAboutObjects = listDrawerObjects;
		this.mTypefaceBold=mTypefaceBold;
		this.mTypefaceLight=mTypefaceLight;
	}

	@Override
	public int getCount() {
		if (listAboutObjects != null) {
			return listAboutObjects.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		if (listAboutObjects != null) {
			return listAboutObjects.get(arg0);
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
			convertView = mInflater.inflate(R.layout.item_about_us, null);
			convertView.setTag(mHolder);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
		mHolder.mTvContent = (TextView) convertView.findViewById(R.id.tv_content);
		mHolder.mImgIcon = (ImageView) convertView.findViewById(R.id.img_icon);
		mHolder.mDevider = convertView.findViewById(R.id.devider);
		
		AboutUsObject mAboutUsObject = listAboutObjects.get(position);
		mHolder.mTvName.setText(mAboutUsObject.getTitle());
		mHolder.mTvName.setTypeface(mTypefaceBold);
		
		mHolder.mTvContent.setText(mAboutUsObject.getContent());
		mHolder.mTvContent.setTypeface(mTypefaceLight);
		
		mHolder.mImgIcon.setImageResource(mAboutUsObject.getIconRes());
		mHolder.mDevider.setVisibility(position==listAboutObjects.size()-1 ? View.GONE:View.VISIBLE);
		
		return convertView;
	}
	

	private static class ViewHolder {
		public TextView mTvName;
		public TextView mTvContent;
		public ImageView mImgIcon;
		public View mDevider;
	}
}
