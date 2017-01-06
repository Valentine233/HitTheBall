package com.phpec.sokoban;
/**
 * 自定义推箱子游戏的舞台VIEW
 */


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.GestureDetector;


public class SokobanView extends View{
	private int w,h;			//屏宽及高
	private static int mSize;		//每个格的大小
	private static int mXCount;		//x轴格数
	private static int mYCount;
	private static int mXOffset;    //水平起点
	private static int mYOffset;	//垂直起点

	private static final int hh = 30;	//头部高度
	private static final int fh = 0;	//脚部高度

	private long mDelay = 500;  //更新显示
	private long mLastUpdate ;

	public static int es_time = 0; //所用时间

	private static int level = 1; //关卡
	public SokobanArtists sokobanArtists = new SokobanArtists();

	Context context;

	private Bitmap[] bitmap = new Bitmap[7];

	ArrayList<TextView> tv = new ArrayList<TextView>();

	private RefreshHandler mRedrawHandler = new RefreshHandler();
	class RefreshHandler extends Handler {
		public void handleMessage(Message msg) {
			SokobanView.this.updateTips();
			SokobanView.this.invalidate(0,0,30,w);
		}
		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	public SokobanView(Context context, AttributeSet arrts) {
		super(context,arrts);  //This is called when a view is being constructed from an XML file, supplying attributes that were specified in the XML file.
		this.context = context;
		initSokobanView();
	}

	public SokobanView(Context context,AttributeSet arrts,int defStyle) {
		super(context,arrts,defStyle);   //This constructor of View allows subclasses to use their own base style when they are inflating.
		this.context = context;
		initSokobanView();
	}

	private void initSokobanView(){
		Log.d("ss", "initSokobanView");
		setFocusable(true); //允许获得焦点，如果没有这句，按键不起作用
		setFocusableInTouchMode(true); //获取焦点时允许触控
		//准备绘图用的Bitmap对象
		bitmap[SokobanArtists.W] = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.wall));
		bitmap[SokobanArtists.B] = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.box));
		bitmap[SokobanArtists.T] = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.target));
		bitmap[SokobanArtists.P] = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.player));
		bitmap[SokobanArtists.M] = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.mixed));
		bitmap[SokobanArtists.S] = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.player));
	}
	public void stopHandler(){
		mRedrawHandler.removeMessages(0);
	}
	/**
	 * 设置显示的文本域
	 * @param tv
	 */
	public void setTextView(ArrayList<TextView> tv){
		Log.d("ss","setTextView");
		this.tv = tv;
	}
	/**
	 * 载入地图，并计算画图的一些参数
	 */
	public void loadMap(int level){
		Log.d("ss","loadMap");
		sokobanArtists.setLevel(level);
		es_time = 0;

		int[] rect = sokobanArtists.getRectSize();
		mXCount = rect[0];					//X的格数
		mYCount = rect[1];					//Y的格数

		int xSize = (int) Math.floor(this.w / mXCount); 	//X方向的每格大小
		int ySize = (int) Math.floor(this.h / mYCount); 	//Y方向的每格大小
		mSize = xSize > ySize ? ySize:xSize;				//使用小的尺寸来画
		mXOffset = ((this.w - (mSize * mXCount)) / 2);  	//X轴起点
		mYOffset = hh + ((this.h - (mSize * mYCount)) / 2);	//Y轴起点
		updateTips();
		this.invalidate();
	}

	public Bundle saveState(){
		//直接用hashmap，可以保存对象
		Bundle mp=  new Bundle();
		//mp.putInt("level", level);
		//mp.putInt("es_time",es_time);
		//保存对象?
		return mp;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.w = w;
		this.h = h - hh - fh; //去掉头和脚
		loadMap(level);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("ss", String.valueOf(keyCode));
		boolean cleared = false;
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			cleared = sokobanArtists.move(SokobanArtists.DIRECT_LEFT);
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			cleared = sokobanArtists.move(SokobanArtists.DIRECT_DOWN);
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			cleared = sokobanArtists.move(SokobanArtists.DIRECT_UP);
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			cleared = sokobanArtists.move(SokobanArtists.DIRECT_RIGHT);
		}
		else if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME){ //返回键
			Log.d("ss","back or home:"+String.valueOf(keyCode));
			//捕捉返回键及HOME键
			makeSure();
			return (true);
		}

		if(cleared){
			declareWin();
		}
		this.invalidate();
		return super.onKeyDown(keyCode, event);
	}



	@Override
	protected void onDraw(Canvas canvas) {
		updateTips();
		for(int i = 0;i < mYCount;i++){
			for(int j= 0; j < mXCount;j++){
				int npcId = sokobanArtists.getArtistsByXY(i,j);

				if (npcId == SokobanArtists.N) continue;  //空白位置
				Rect rt = new Rect();
				int x = mXOffset + mSize * j; //
				int y = mYOffset + mSize * i;



				rt.set(x,y,x+mSize,y+mSize);
				canvas.drawBitmap(bitmap[npcId], null, rt, null);
			}
		}
		super.onDraw(canvas);
	}


	public boolean declareWin(){

		//实时创建对话框并显示
		new AlertDialog.Builder(context)
				//.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.dia_tips)
				.setMessage(R.string.dia_youwin)
				.setPositiveButton(R.string.menu_ok,	new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,int whichButton)
							{
								if(level < sokobanArtists.getTotalLevels()){
									Log.d("ee",String.format("level=%1$d,total=%2$d", level,sokobanArtists.getTotalLevels()));
									level++; //next
									loadMap(level);
								}
							}
						}
				)
				.show();

		return true;
	}

	private boolean makeSure(){

		//实时创建对话框并显示
		new AlertDialog.Builder(context)
				//.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.dia_tips)
				.setMessage(R.string.sure_tips)
				.setPositiveButton(R.string.menu_ok,	new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,int whichButton)
							{
								System.exit(0);
							}
						}
				)
				.setNegativeButton(R.string.menu_cancle, new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,int whichButton)
							{
								//取消
							}
						}
				)
				.show();

		return true;
	}

	/**
	 * 更新界面上的信息提示
	 */
	private void updateTips(){
		long now = System.currentTimeMillis();
		if (now - mLastUpdate > mDelay) {
			if(sokobanArtists.s_time != 0){
				es_time = (int) Math.floor((System.currentTimeMillis() - sokobanArtists.s_time) / 1000 );
			}
			tv.get(Sokoban.TVID_STEP).setText(String.valueOf(sokobanArtists.getPSteps()));
			tv.get(Sokoban.TVID_TIME).setText(String.valueOf(es_time));
			tv.get(Sokoban.TVID_LEVEL).setText(String.format("%1$d/%2$d", sokobanArtists.getLevel(),sokobanArtists.getTotalLevels()));

			//int pos[] = sokobanArtists.getPPosition();
			//tv.get(Sokoban.TVID_XY).setText(String.format("%1$d/%2$d",pos[0],pos[1]));
			tv.get(Sokoban.TVID_TARGET).setText(String.format("%1$d/%2$d",sokobanArtists.getTCleared(),sokobanArtists.getTTotal()));
			mLastUpdate = now;
		}
		mRedrawHandler.sleep(mDelay);
	}
}