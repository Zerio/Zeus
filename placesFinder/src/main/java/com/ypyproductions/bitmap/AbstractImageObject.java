package com.ypyproductions.bitmap;

import android.graphics.drawable.BitmapDrawable;

import com.ypyproductions.net.task.IDBCallback;

public class AbstractImageObject {
	private String link;
	private IDBCallback finishCallback;
	private BitmapDrawable bitmap;
	
	public AbstractImageObject() {
		super();
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public IDBCallback getFinishCallback() {
		return finishCallback;
	}

	public void setFinishCallback(IDBCallback finishCallback) {
		this.finishCallback = finishCallback;
	}

	public BitmapDrawable getBitmap() {
		return bitmap;
	}

	public void setBitmap(BitmapDrawable bitmap) {
		this.bitmap = bitmap;
	}
	
	

}
