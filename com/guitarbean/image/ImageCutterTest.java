package com.guitarbean.image;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nie163.tx3.R;

public class ImageCutterTest extends Activity {
	private int _xDelta;
	private int _yDelta;
	private int _wDelta;
	private int _hDelta;
	private int _widthDelta;
	private int _heightDelta;
	private int _topDelta;
	private int _leftDelta;
	
	
	final static boolean isFixWidthAndHeight = true;
	final static boolean isBothSizeFixedInZoom = true;
	final static boolean isFixedInZoom = true;
	final static float sizeRatio = 1f;
	final static int minWidth  = 150;
	final static int minHeight = 150;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.image_cutter);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_titlebar);
		findViewById(R.id.titleImg).setVisibility(View.GONE);
		setTitle("裁切");
		((TextView) findViewById(R.id.title)).setText("裁切");
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.setting).setVisibility(View.GONE);

		final View cutter = findViewById(R.id.cutter);
		final View handlerTop = findViewById(R.id.cutterHandlerTop);
		final View handlerRight = findViewById(R.id.cutterHandlerRight);
		final View handlerBottom = findViewById(R.id.cutterHandlerBottom);
		final View handlerLeft = findViewById(R.id.cutterHandlerLeft);
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cutter
				.getLayoutParams();
		setHandlerAndWraperFixed(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.width, layoutParams.height);
		
		
		OnTouchListener handlerListener = new OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (isFixedInZoom) {
					return ImageCutterTest.this.onHandlerDragFixedInZoom(view, event,cutter);
				}else {
					return ImageCutterTest.this.onHandlerDragFree(view, event,cutter);
				}
				
			}
		};
		handlerTop.setOnTouchListener(handlerListener);
		handlerRight.setOnTouchListener(handlerListener);
		handlerBottom.setOnTouchListener(handlerListener);
		handlerLeft.setOnTouchListener(handlerListener);
		
		
		
		cutter.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {

				final int X = (int) event.getRawX();
				final int Y = (int) event.getRawY();
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view
							.getLayoutParams();
					//Log.i("topMargin", ""+lParams.topMargin+","+Y);
					_xDelta = X - lParams.leftMargin;
					_yDelta = Y - lParams.topMargin;
					return true;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					break;
				case MotionEvent.ACTION_POINTER_UP:
					break;
				case MotionEvent.ACTION_MOVE:
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
							.getLayoutParams();
					int x = X - _xDelta;
					int y = Y - _yDelta;
					int w = view.getWidth();
					int h = view.getHeight();
					View parentView = (View) view.getParent();
					int parentWidth = parentView.getWidth();
					int parentHeight = parentView.getHeight();

					
					y = y < 0 ? 0 : ((y + h) > parentHeight ? (parentHeight - h)
							: y);
					x = x < 0 ? 0 : ((x + w) > parentWidth ? (parentWidth - w) : x);
					
					layoutParams.leftMargin = x;
					layoutParams.topMargin = y;
					ImageCutterTest.this.setHandlerAndWraperFixed(x,y,w,h);
					/*Log.i("xy????", "" + layoutParams.leftMargin + ","
							+ layoutParams.topMargin + ";" + X + "," + Y + ";"
							+ _xDelta + "," + _yDelta+",parent:"+parentWidth+","+parentHeight);*/
					view.setLayoutParams(layoutParams);
					return true;
				}
				return false;
			}
		});

	}
	boolean onHandlerDragFree(View view, MotionEvent event,View cutter){
		final int X = (int) event.getRawX();
		final int Y = (int) event.getRawY();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) cutter
					.getLayoutParams();
			_xDelta = X - lParams.leftMargin;
			_yDelta = Y - lParams.topMargin;
			_wDelta = X - lParams.width;
			_hDelta = Y - lParams.height;
			_widthDelta = lParams.width;
			_heightDelta = lParams.height;
			_topDelta = lParams.topMargin;
			_leftDelta = lParams.leftMargin;
			lParams = null;
			return true;
/*		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;*/
		case MotionEvent.ACTION_MOVE:
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cutter
					.getLayoutParams();
			int x , y ;
			int w , h;
			
			View parentView = (View) cutter.getParent();
			int parentWidth = parentView.getWidth();
			int parentHeight = parentView.getHeight();

			switch (view.getId()) {
			case R.id.cutterHandlerBottom:
				h = Y - _hDelta;
				h = h < minHeight ? minHeight : h;
				if (isBothSizeFixedInZoom) {
					w = h;
					int leftMargin = layoutParams.leftMargin;
					w = (leftMargin + w) >= parentWidth ? (parentWidth - leftMargin) : w;
					h = w;
					layoutParams.width = w;
				}
				layoutParams.height = h;
				break;
			case R.id.cutterHandlerRight:
				w = X - _wDelta;
				w = w < minWidth ? minWidth : w;
				if (isBothSizeFixedInZoom) {
					h = w;
					int topMargin = layoutParams.topMargin;
					h = (topMargin + h) >= parentHeight ? (parentHeight - topMargin) : h;
					w = h;
					layoutParams.height = h;
				}
				layoutParams.width = h;
				break;
			case R.id.cutterHandlerTop:
				y = Y - _yDelta;
				y = y < 0 ? 0 :y; 

				h = -y + _topDelta + _heightDelta;
				h = h < minHeight ? minHeight : h;

				y = y + h > parentHeight ? parentHeight-h : y;

				if (isBothSizeFixedInZoom) {
					w = h;
					x =  _leftDelta + _widthDelta - w;
					if (x<=0) {
						w += x;
						h = w;
						x = 0;
						y = layoutParams.topMargin;
					}
					if (x+w>parentWidth) {
						w = parentWidth - x;
						h = w;
					}
					layoutParams.width =  w;
					
				}else{
					x = _leftDelta;
					w = cutter.getWidth();
				}
				layoutParams.leftMargin = x;
				layoutParams.topMargin = y;
				layoutParams.height = h;
				ImageCutterTest.this.setHandlerAndWraperFixed(x, y, w, h);
				break;
			case R.id.cutterHandlerLeft:
				x = X - _xDelta;
				x = x<0 ? 0 :x;

				w = -x + _leftDelta + _widthDelta;
				w = w < minWidth ? minWidth : w;
				
				x = x + w > parentWidth ? parentWidth-w : x;

				if (isBothSizeFixedInZoom) {
					h = w;
					y =  _topDelta + _heightDelta - h;
					if (y<=0) {
						h += y;
						w = h;
						y = 0;
						x = layoutParams.leftMargin;
					}
					if (h+y>parentHeight) {
						h = parentHeight - y;
						w = h;
					}
					
					layoutParams.height =  h;
				}else{
					y = _topDelta;
					h = cutter.getHeight();
				}
				layoutParams.leftMargin = x;
				layoutParams.topMargin = y;
				layoutParams.width = w;
				ImageCutterTest.this.setHandlerAndWraperFixed(x, y, w, h);
				break;
			default:
				break;
			}
			
			cutter.setLayoutParams(layoutParams);
			return true;
		}
		return false;
	}	
	boolean onHandlerDragFixedInZoom(View view,MotionEvent event,View cutter){
		final int X = (int) event.getRawX();
		final int Y = (int) event.getRawY();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) cutter
					.getLayoutParams();
			_xDelta = X - lParams.leftMargin;
			_yDelta = Y - lParams.topMargin;
			_wDelta = X - lParams.width;
			_hDelta = Y - lParams.height;
			_widthDelta = lParams.width;
			_heightDelta = lParams.height;
			_topDelta = lParams.topMargin;
			_leftDelta = lParams.leftMargin;
			lParams = null;
			return true;
/*		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;*/
		case MotionEvent.ACTION_MOVE:
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cutter
					.getLayoutParams();
			int x,y;
			int w,h;
			
			View parentView = (View) cutter.getParent();
			int parentWidth = parentView.getWidth();
			int parentHeight = parentView.getHeight();
			switch (view.getId()) {
			case R.id.cutterHandlerTop:
				y = Y - _yDelta;
				y = y < 0 ? 0 :y; 

				h = (-y + _topDelta)*2 + _heightDelta;
				h = h < minHeight ? minHeight : ( h > parentHeight ? parentHeight : h );
				
				if (h==minHeight) {
					y = layoutParams.topMargin;
				}else {
					y = y + h >= parentHeight ? parentHeight - h : y;
				}
				
				if (isBothSizeFixedInZoom) {
					w = (int)(((float)h)*sizeRatio);
					x =  (_widthDelta - w ) /2 + _leftDelta;
					if (x<=0) {
						x = 0;
					}
					if (x+w>parentWidth && x>0) {
						x = parentWidth - w;
					}
					if (w>=parentWidth) {
						x = 0;
						w = parentWidth;
						h = (int)(((float)w)/sizeRatio);

						y = layoutParams.topMargin;
						y = y + h >= parentHeight ? parentHeight - h : y;

					}
					layoutParams.width =  w;
					
				}else{
					x = _leftDelta;
					w = view.getWidth();
				}
				layoutParams.leftMargin = x;
				layoutParams.topMargin = y;
				layoutParams.height = h;
				
				ImageCutterTest.this.setHandlerAndWraperFixed(x, y, w, h);
				break;
			case R.id.cutterHandlerLeft:
				x = X - _xDelta;
				x = x < 0 ? 0 :x; 

				w = (-x + _leftDelta)*2 + _widthDelta;
				w = w < minWidth ? minWidth : ( w > parentWidth ? parentWidth : w );
				
				
				if (w==minWidth) {
					x = layoutParams.leftMargin;
				}else {
					x = x + w > parentWidth ? parentWidth - w : x;
				}
				
				if (isBothSizeFixedInZoom) {
					h = (int)(((float)w)/sizeRatio);
					y =  (_heightDelta - h ) /2 + _topDelta;
					
					if (y<=0) {
						y = 0;
					}
					if (y+h>parentHeight && y>0) {
						y = parentHeight - h;
					}
					if (h>=parentHeight) {
						y = 0;
						h = parentHeight;
						w = (int)(((float)h)*sizeRatio);
						x = layoutParams.leftMargin;
						x = x + w > parentWidth ? parentWidth - w : x;
					}
					layoutParams.height =  h;
					
				}else{
					y = _topDelta;
					h = view.getHeight();
				}
				layoutParams.leftMargin = x;
				layoutParams.topMargin = y;
				layoutParams.width = w;
				
				ImageCutterTest.this.setHandlerAndWraperFixed(x, y, w, h);
				break;
			case R.id.cutterHandlerBottom:
				h = Y - _hDelta;
				int heightDelta = (h - _heightDelta);
				h += heightDelta;
				h = h < minHeight ? minHeight : h;
				h = h > parentHeight ? parentHeight : h;
				
				
				if (h==minHeight) {
					y = layoutParams.topMargin;
				}else {
					y = Y - _yDelta - heightDelta*2;
					y = y < 0 ? 0 :( (y+h)>parentHeight ? parentHeight - h : y );
				}
				
				
				if (isBothSizeFixedInZoom) {
					w = (int)(((float)h)*sizeRatio);
					x =  (_widthDelta - w ) /2 + _leftDelta;
					if (x<=0) {
						x = 0;
					}
					if (x+w>parentWidth && x>0) {
						x = parentWidth - w;
					}
					if (w>=parentWidth) {
						x = 0;
						w = parentWidth;
						h = (int)(((float)w)/sizeRatio);
						y = layoutParams.topMargin;
					}
					layoutParams.width =  w;
				}else{
					x = _leftDelta;
					w = view.getWidth();
				}
				layoutParams.leftMargin = x;
				layoutParams.topMargin = y;
				layoutParams.height = h;
				ImageCutterTest.this.setHandlerAndWraperFixed(layoutParams.leftMargin, y, w, h);
				break;
			case R.id.cutterHandlerRight:
				w = X - _wDelta;
				int widthDelta = (w - _widthDelta);
				w += widthDelta;
				w = w < minWidth ? minWidth : w;
				w = w > parentWidth ? parentWidth : w;
				
				if (w==minWidth) {
					x = layoutParams.leftMargin;
				}else {
					x = X - _xDelta - widthDelta*2;
					x = x < 0 ? 0 :( (x+w)>parentWidth ? parentWidth - w : x );
				}

				if (isBothSizeFixedInZoom) {
					h = (int)(((float)w)/sizeRatio);
					y =  (_heightDelta - h ) /2 + _topDelta;
					if (y<=0) {
						y = 0;
					}
					if (y+h>parentHeight && y>0) {
						y = parentHeight - h;
					}
					if (h>=parentHeight) {
						y = 0;
						h = parentHeight;
						w = (int)(((float)h)*sizeRatio);
						x = layoutParams.leftMargin;
					}
					layoutParams.height =  h;
					
				}else{
					y = _topDelta;
					h = view.getHeight();
				}
				layoutParams.leftMargin = x;
				layoutParams.topMargin = y;
				layoutParams.width = w;
				ImageCutterTest.this.setHandlerAndWraperFixed(layoutParams.leftMargin, y, w, h);
				break;
			}
			cutter.setLayoutParams(layoutParams);
		}
		
		return false;
	}
	
	void setHandlerAndWraperFixed(int x, int y, int w,int h){
		View handlerTop = findViewById(R.id.cutterHandlerTop);
		View handlerLeft = findViewById(R.id.cutterHandlerLeft);
		View maskTop = findViewById(R.id.maskTop);
		View maskLeft = findViewById(R.id.maskLeft);
		RelativeLayout.LayoutParams handlerToplayoutParams = (RelativeLayout.LayoutParams) handlerTop
				.getLayoutParams();
		RelativeLayout.LayoutParams handlerLeftlayoutParams = (RelativeLayout.LayoutParams) handlerLeft
				.getLayoutParams();
		RelativeLayout.LayoutParams maskToplayoutParams = (RelativeLayout.LayoutParams) maskTop
				.getLayoutParams();
		RelativeLayout.LayoutParams maskLeftlayoutParams = (RelativeLayout.LayoutParams) maskLeft
				.getLayoutParams();
		
		handlerToplayoutParams.topMargin = y - (handlerToplayoutParams.height/2);
		handlerLeftlayoutParams.leftMargin = x - handlerLeftlayoutParams.width/2;
		maskToplayoutParams.height = y;
		maskLeftlayoutParams.width = x;
		
		handlerTop.setLayoutParams(handlerToplayoutParams);
		handlerLeft.setLayoutParams(handlerLeftlayoutParams);
		maskTop.setLayoutParams(maskToplayoutParams);
		maskLeft.setLayoutParams(maskLeftlayoutParams);
		
	
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/*
		View view = this.findViewById(R.id.cutter);
		final int X = (int) event.getRawX();
		final int Y = (int) event.getRawY();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view
					.getLayoutParams();
			_xDelta = X - lParams.leftMargin;
			_yDelta = Y - lParams.topMargin;
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
					.getLayoutParams();
			int x = X - _xDelta;
			int y = Y - _yDelta;
			int w = view.getWidth();
			int h = view.getHeight();
			View parentView = (View) view.getParent();
			int parentWidth = parentView.getWidth();
			int parentHeight = parentView.getHeight();

			
			y = y < 0 ? 0 : ((y + h) > parentHeight ? (parentHeight - h)
					: y);
			x = x < 0 ? 0 : ((x + w) > parentWidth ? (parentWidth - w) : x);
			
			layoutParams.leftMargin = x;
			layoutParams.topMargin = y;
			Log.i("xy????", "" + layoutParams.leftMargin + ","
					+ layoutParams.topMargin + ";" + X + "," + Y + ";"
					+ _xDelta + "," + _yDelta+",parent:"+parentWidth+","+parentHeight);
			view.setLayoutParams(layoutParams);
			break;
		}*/
		return super.onTouchEvent(event);
	}

	public class GestureDragListener implements OnGestureListener {
		WeakReference<Context> cutterReference;

		/**
		 * @param cutterReference
		 */
		public GestureDragListener(Context context) {
			super();
			this.cutterReference = new WeakReference<Context>(context);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
