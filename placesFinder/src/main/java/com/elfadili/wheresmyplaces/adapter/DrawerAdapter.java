package com.elfadili.wheresmyplaces.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.places.nearby.R;
import com.elfadili.wheresmyplaces.constanst.IWhereMyLocationConstants;
import com.elfadili.wheresmyplaces.object.ItemDrawerObject;

public class DrawerAdapter extends BaseAdapter implements IWhereMyLocationConstants {
	public static final String TAG = DrawerAdapter.class.getSimpleName();

	private Context mContext;
	private ArrayList<ItemDrawerObject> listDrawerObjects;

	private Typeface mTypefaceBold;
	private Typeface mTypefaceLight;

	public DrawerAdapter(Context mContext, ArrayList<ItemDrawerObject> listDrawerObjects,Typeface mTypefaceBold, Typeface mTypefaceLight) {
		this.mContext = mContext;
		this.listDrawerObjects = listDrawerObjects;
		this.mTypefaceBold=mTypefaceBold;
		this.mTypefaceLight=mTypefaceLight;
	}

	@Override
	public int getCount() {
		if (listDrawerObjects != null) {
			return listDrawerObjects.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		if (listDrawerObjects != null) {
			return listDrawerObjects.get(arg0);
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
			convertView = mInflater.inflate(R.layout.item_drawer, null);
			convertView.setTag(mHolder);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		mHolder.mTvNameDrawer = (TextView) convertView.findViewById(R.id.tv_name_setting);
		mHolder.mDevider = convertView.findViewById(R.id.devider);

		ItemDrawerObject mDrawerObject = listDrawerObjects.get(position);
		mHolder.mTvNameDrawer.setText(mDrawerObject.getName());
		if(mDrawerObject.isSelected()){
			mHolder.mTvNameDrawer.setTypeface(mTypefaceBold);
		}
		else{
			mHolder.mTvNameDrawer.setTypeface(mTypefaceLight);
		}
		mHolder.mDevider.setVisibility(position==listDrawerObjects.size()-1 ? View.GONE:View.VISIBLE);
		return convertView;
	}
	
	public void setSelectedDrawer(int pos){
		if(pos<0 || pos>=listDrawerObjects.size()){
			return;
		}
		for(ItemDrawerObject mDrawerObject:listDrawerObjects){
			mDrawerObject.setSelected(false);
		}
		listDrawerObjects.get(pos).setSelected(true);
		notifyDataSetChanged();
	}
	
	
	public ArrayList<ItemDrawerObject> getListDrawerObjects() {
		return listDrawerObjects;
	}

	private static class ViewHolder {
		public TextView mTvNameDrawer;
		public View mDevider;
	}
}
