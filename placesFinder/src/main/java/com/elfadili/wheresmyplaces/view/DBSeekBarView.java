package com.elfadili.wheresmyplaces.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.places.nearby.R;

public class DBSeekBarView extends View {

	private static final String TAG = DBSeekBarView.class.getSimpleName();
	
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL=1;
	
	private Bitmap mSlideBar;
	private Bitmap mThumb;

	private int mWidth=0;
	private int mHeight=0;
	
	private float mThumbX=0;
	private float mThumbY=0;
	private float slideBgX=0;
	private float slideBgY=0;
	
	private Paint mPaint;

	private int progress;

	private int mWidthToCalculateProcess=0;
	private int mHeightToCalculateProcess=0;

	private OnSeekBarChangeListener mOnSeekBarChangeListener;

	private float mThumbMinX;
	private float mThumbMaxX;
	private boolean isFocus;

	private Context mContext;
	
	private int orientation=HORIZONTAL;

	private Rect mRectThumb;

	private Rect mRectSlide;

	private float mThumbMinY;

	private float mThumbMaxY;

	private int maxProgress=100;
	
	private boolean isEnable=true;
	private boolean isAllowAction=false;
	
	public DBSeekBarView(Context context) {
		super(context);
		this.mContext = context;
	}

	public DBSeekBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		loadView(attrs);
	}

	public DBSeekBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		loadView(attrs);
	}
	
	private void loadView(AttributeSet attrs){
		 TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.DBSeekBarView);
		 try {
			 Drawable mSlideDrawable = a.getDrawable(R.styleable.DBSeekBarView_slidebar);
			 Drawable mThumbDrawable = a.getDrawable(R.styleable.DBSeekBarView_thumb);
			 this.orientation = a.getInteger(R.styleable.DBSeekBarView_orientation, 0);
			 this.maxProgress = a.getInteger(R.styleable.DBSeekBarView_max, 100);
			 this.progress = a.getInteger(R.styleable.DBSeekBarView_progress, 0);
			 initView(((BitmapDrawable)mSlideDrawable).getBitmap(), 
					 ((BitmapDrawable)mThumbDrawable).getBitmap());
		} 
		 catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			a.recycle();
		}
		 
	}
	public void initView(Bitmap mSlideBar, Bitmap mThumb){
		if(mSlideBar==null || mThumb==null){
			new Exception(TAG +" :Bitmap can not null").printStackTrace();
			return;
		}
		this.mSlideBar = mSlideBar;
		this.mThumb = mThumb;
		
		if(orientation==HORIZONTAL){
			this.mWidth = mSlideBar.getWidth();
			if(mSlideBar.getHeight()>mThumb.getHeight()){
				this.mHeight= mSlideBar.getHeight();
			}
			else{
				this.mHeight= mThumb.getHeight();
			}
			this.slideBgX = (mWidth - mSlideBar.getWidth())/2;
			this.slideBgY = (mHeight-mSlideBar.getHeight())/2;
			
			this.mWidthToCalculateProcess = mWidth-mThumb.getWidth();
			this.mThumbMinX = slideBgX;
			this.mThumbMaxX = slideBgX+mSlideBar.getWidth()-mThumb.getWidth();
			this.mThumbX = mThumbMinX;
			this.mThumbY = (mHeight-mThumb.getHeight())/2;
		}
		else if(orientation==VERTICAL){
			this.mHeight = mSlideBar.getHeight();
			if(mSlideBar.getWidth()>mThumb.getWidth()){
				this.mWidth= mSlideBar.getWidth();
			}
			else{
				this.mWidth= mThumb.getWidth();
			}
			this.slideBgX = (mWidth - mSlideBar.getWidth())/2;
			this.slideBgY = (mHeight-mSlideBar.getHeight())/2;
			
			this.mHeightToCalculateProcess = mHeight-mThumb.getHeight();
			
			this.mThumbMinY = slideBgY+mSlideBar.getHeight()-mThumb.getHeight();
			this.mThumbMaxY = slideBgY;
			
			this.mThumbX = (mWidth-mThumb.getWidth())/2;
			this.mThumbY = mThumbMinY;
		}
		this.mRectThumb = new Rect();
		this.mRectSlide = new Rect();
		this.mPaint = new Paint();
		this.mPaint.setAntiAlias(true);
		this.mPaint.setFilterBitmap(true);
		
		this.setProgress(progress,false);
		
		this.invalidate();
	}
	
	public void onDestroy() {
		if(mSlideBar!=null){
			mSlideBar.recycle();
			mSlideBar =null;
		}
		if(mThumb!=null){
			mThumb.recycle();
			mThumb =null;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if(action==MotionEvent.ACTION_DOWN){
			onTouchDown(event);
			return true;
		}
		else if(action==MotionEvent.ACTION_MOVE){
			onTouchMove(event);
			return true;
		}
		else if(action==MotionEvent.ACTION_UP|| action==MotionEvent.ACTION_CANCEL){
			onTouchUp(event);
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	private void onTouchDown(MotionEvent event) {
		if(!isEnable){
			return;
		}
		float x = event.getX();
		float y = event.getY();
		boolean isContains = this.isContains(x, y);
		int newProgress=progress;
		if (isContains){
			if(orientation==HORIZONTAL){
				this.mThumbX = x-mThumb.getWidth()/2;
				if(mThumbX<=mThumbMinX){
					mThumbX=mThumbMinX;
				}
				if (mThumbX>=mThumbMaxX){
					mThumbX=mThumbMaxX;
				}
				this.isFocus = true;
				newProgress = Math.abs((int) (maxProgress*(mThumbX-mThumbMinX)/mWidthToCalculateProcess)); 
			}
			else if(orientation==VERTICAL){
				this.mThumbY = y-mThumb.getHeight()/2;
				if(mThumbY>=mThumbMinY){
					mThumbY=mThumbMinY;
				}
				if (mThumbY<=mThumbMaxY){
					mThumbY=mThumbMaxY;
				}
				this.isFocus = true;
				newProgress = Math.abs((int) (maxProgress*(mThumbMinY-mThumbY)/mHeightToCalculateProcess));
			}
			this.invalidate();
			if(newProgress!=progress){
				progress=newProgress;
				isAllowAction=true;
				if(mOnSeekBarChangeListener!=null){
					mOnSeekBarChangeListener.onUpdateProcess(progress);
				}
			}
		}
		
	}

	private void onTouchMove(MotionEvent event) {
		if(!isEnable){
			return;
		}
		float x = event.getX();
		float y = event.getY();
		int newProgress=progress;
		if(isFocus){
			if(orientation==HORIZONTAL){
				this.mThumbX = x-mThumb.getWidth()/2;
				if(mThumbX<=mThumbMinX){
					mThumbX=mThumbMinX;
				}
				if (mThumbX>=mThumbMaxX){
					mThumbX=mThumbMaxX;
				}
				newProgress = (int) (maxProgress*(mThumbX-mThumbMinX)/mWidthToCalculateProcess);
			}
			else if(orientation==VERTICAL){
				this.mThumbY = y-mThumb.getHeight()/2;
				if(mThumbY>=mThumbMinY){
					mThumbY=mThumbMinY;
				}
				if (mThumbY<=mThumbMaxY){
					mThumbY=mThumbMaxY;
				}
				this.isFocus = true;
				newProgress = Math.abs((int) (maxProgress*(mThumbMinY-mThumbY)/mHeightToCalculateProcess));
			}
			this.invalidate();
			if(newProgress!=progress){
				progress=newProgress;
				isAllowAction=true;
				if(mOnSeekBarChangeListener!=null){
					mOnSeekBarChangeListener.onUpdateProcess(progress);
				}
			}
		}
	}

	private void onTouchUp(MotionEvent event) {
		if(!isEnable){
			this.isFocus=false;
			this.isAllowAction=false;
			return;
		}
		this.isFocus=false;
		if(mOnSeekBarChangeListener!=null && isAllowAction){
			mOnSeekBarChangeListener.onSeekBarChangeListener(progress);
		}
		this.isAllowAction=false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mSlideBar!=null){
			mRectSlide.left = (int) slideBgX;
			mRectSlide.right = (int) (mRectSlide.left+mSlideBar.getWidth());
			mRectSlide.top = (int) slideBgY;
			mRectSlide.bottom = (int) (mRectSlide.top+mSlideBar.getHeight());
			canvas.drawBitmap(mSlideBar, null, mRectSlide, mPaint);
		}
		if(mThumb!=null){
			mRectThumb.left = (int) mThumbX;
			mRectThumb.right = (int) (mThumbX+mThumb.getWidth());
			mRectThumb.top = (int) mThumbY;
			mRectThumb.bottom = (int) (mThumbY+mThumb.getHeight());
			canvas.drawBitmap(mThumb, null, mRectThumb, mPaint);
		}
	}
	
	private boolean isContains(float x,float y){
		float xMin = 0;
		float xMax = mWidth;
		float yMin =0;
		float yMax =mHeight; 
		if(xMin<=x && x<=xMax && yMin<=y && y<=yMax){
			return true;
		}
		return false;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mWidth, mHeight);
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress, boolean isAffected) {
		this.progress = progress;
		if(orientation==HORIZONTAL){
			float x = (float)((float)progress/maxProgress)*(float)mWidthToCalculateProcess;
			this.mThumbX = (int)(mThumbMinX+x);
		}
		else if(orientation==VERTICAL){
			float y = (float)((float)progress/maxProgress)*(float)mHeightToCalculateProcess;
			this.mThumbY = (int)(mThumbMinY-y);
		}
		if(mOnSeekBarChangeListener!=null && isAffected){
			mOnSeekBarChangeListener.onSeekBarChangeListener(progress);
		}
		this.invalidate();
	}
	
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener mBarChangeListener){
		this.mOnSeekBarChangeListener= mBarChangeListener;
	}
	
	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}


	public interface OnSeekBarChangeListener {
		public void onSeekBarChangeListener(int process);
		public void onUpdateProcess(int process);
	}
	
}
