package com.elfadili.wheresmyplaces.object;

import com.elfadili.wheresmyplaces.constants.IMarocPlaceExplorerConstants;
import com.ypyproductions.utils.StringUtils;

public class PlacePhotoObject implements IMarocPlaceExplorerConstants {
	private String photoReference;
	private int width;
	private int height;
	
	public PlacePhotoObject(String photoReference, int width, int height) {
		super();
		this.photoReference = photoReference;
		this.width = width;
		this.height = height;
	}

	public String getPhotoReference() {
		return photoReference;
	}

	public void setPhotoReference(String photoReference) {
		this.photoReference = photoReference;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public String toLink() {
		if(!StringUtils.isStringEmpty(photoReference)){
			if(width>0 && height>0){
				return String.format(FORMAT_URL_PHOTO_REF, photoReference,width,height,MAP_KEY);
			}
		}
		return null;
	}
	
}
