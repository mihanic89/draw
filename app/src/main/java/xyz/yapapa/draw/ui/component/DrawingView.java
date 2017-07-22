package xyz.yapapa.draw.ui.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
//import static com.google.android.gms.internal.zzt.TAG;

public class DrawingView extends View
{
	private Path mDrawPath;
	private Paint mBackgroundPaint;
	private Paint mDrawPaint;
	private Canvas mDrawCanvas;
	private Bitmap mCanvasBitmap;
	private Bitmap mCanvasBitmapBackground=null;

	private ArrayList<Path> mPaths = new ArrayList<>();
	private ArrayList<Paint> mPaints = new ArrayList<>();
	private ArrayList<Path> mUndonePaths = new ArrayList<>();
	private ArrayList<Paint> mUndonePaints = new ArrayList<>();

	// Set default values
	private int mBackgroundColor = 0xFFFFFFFF;
	private int mPaintColor = 0xFF660000;
	private int mStrokeWidth = 10;

	public DrawingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
		mDrawPath = new Path();
		mBackgroundPaint = new Paint();
		initPaint();
	}



	private void initPaint()
	{
		mDrawPaint = new Paint();
		mDrawPaint.setColor(mPaintColor);
		mDrawPaint.setAntiAlias(true);
		mDrawPaint.setStrokeWidth(mStrokeWidth);
		mDrawPaint.setStyle(Paint.Style.STROKE);
		mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
		mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
	}



	private void drawBackground(Canvas canvas)
	{
		mBackgroundPaint.setColor(mBackgroundColor);
		mBackgroundPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), mBackgroundPaint);
	}

	private void drawBackgroundBitmap(Canvas canvas)
	{
		int width1 = mCanvasBitmapBackground.getWidth();
		int height1 = mCanvasBitmapBackground.getHeight();

		int x0=(mDrawCanvas.getWidth()/2- width1/2)-1;
		int y0=(mDrawCanvas.getHeight()/2 -height1/2)-1;


		if (x0<0) x0=0;
		if (y0<0) y0=0;

		canvas.drawBitmap(mCanvasBitmapBackground,x0, y0, null);
	}
	private void drawPaths(Canvas canvas)
	{
		int i = 0;
		for (Path p : mPaths)
		{
			canvas.drawPath(p, mPaints.get(i));
			i++;
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{

		drawBackground(canvas);
		if (mCanvasBitmapBackground!=null)
			drawBackgroundBitmap(canvas);
		drawPaths(canvas);

		canvas.drawPath(mDrawPath, mDrawPaint);
	}



	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		mDrawCanvas = new Canvas(mCanvasBitmap);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float touchX = event.getX();
		float touchY = event.getY();

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				mDrawPath.moveTo(touchX, touchY);
				//mDrawPath.addCircle(touchX, touchY, mStrokeWidth/10, Path.Direction.CW);
				break;
			case MotionEvent.ACTION_MOVE:
				mDrawPath.lineTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_UP:
				mDrawPath.lineTo(touchX, touchY);
				mPaths.add(mDrawPath);
				mPaints.add(mDrawPaint);
				mDrawPath = new Path();
				initPaint();
				break;
			default:
				return false;
		}

		invalidate();
		return true;
	}

	public void clearCanvas()
	{
		mPaths.clear();
		mPaints.clear();
		mUndonePaths.clear();
		mUndonePaints.clear();
		mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		if (mCanvasBitmapBackground!=null){
		mCanvasBitmapBackground.recycle();
		mCanvasBitmapBackground=null;}
		invalidate();
	}

	public void setPaintColor(int color)
	{
		mPaintColor = color;
		mDrawPaint.setColor(mPaintColor);
	}



	public void setPaintStrokeWidth(int strokeWidth)
	{
		mStrokeWidth = strokeWidth;
		mDrawPaint.setStrokeWidth(mStrokeWidth);
	}

	public void setBackgroundColor(int color)
	{
		mBackgroundColor = color;
		mBackgroundPaint.setColor(mBackgroundColor);
		invalidate();
	}

	public void DrawCustom(Bitmap b)
	{

        //Bitmap b1 = Bitmap.createScaledBitmap(b,mDrawCanvas.getWidth(),mDrawCanvas.getHeight(),false);
        //b = Bitmap.createBitmap(mDrawCanvas.getWidth(),mDrawCanvas.getHeight(), Bitmap.Config.ARGB_8888);
		//mDrawCanvas = new Canvas(b);
		//mDrawCanvas.drawBitmap(b,mDrawCanvas.getWidth(),mDrawCanvas.getHeight(),null);

		//mDrawCanvas.drawBitmap(b,0, 0,mDrawPaint);
        //b.recycle();
        //mDrawCanvas.drawCircle(300,300,200,mDrawPaint);
		mDrawCanvas.drawBitmap(b,0, 0,null);
		invalidate();
        //mDrawCanvas.drawBitmap(b,mDrawCanvas.getWidth(), mDrawCanvas.getHeight(),null);

	}



	public Bitmap getBitmap()
	{
		drawBackground(mDrawCanvas);
		if (mCanvasBitmapBackground!=null)
			drawBackgroundBitmap(mDrawCanvas);
		drawPaths(mDrawCanvas);
		return mCanvasBitmap;
	}

	public void undo()
	{
		if (mPaths.size() > 0)
		{
			mUndonePaths.add(mPaths.remove(mPaths.size() - 1));
			mUndonePaints.add(mPaints.remove(mPaints.size() - 1));
			invalidate();
		}
	}

	public void redo()
	{
		if (mUndonePaths.size() > 0)
		{
			mPaths.add(mUndonePaths.remove(mUndonePaths.size() - 1));
			mPaints.add(mUndonePaints.remove(mUndonePaints.size() - 1));
			invalidate();
		}
	}

	public void SetCustomBitmap(Bitmap b) {
		int width = b.getWidth();
		int height = b.getHeight();
		float scaleWidth = ((float) mDrawCanvas.getWidth()) / width;
		float scaleHeight = ((float) mDrawCanvas.getHeight()) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		mCanvasBitmapBackground= Bitmap.createBitmap(b, 0, 0,
				width, height, matrix, true);

		invalidate();
	}



	public void SetCustomBitmap2(Bitmap b) {

		Boolean landscape;


		if (b.getWidth()>b.getHeight()) landscape=true;
		else landscape=false;

		int width = b.getWidth();
		int height = b.getHeight();



		float scaleWidth = ((float) mDrawCanvas.getWidth()) / width;
		float scaleHeight = ((float) mDrawCanvas.getHeight()) / height;

		if (landscape)
			{
					scaleWidth=scaleHeight;

			}
			else
			{


					scaleWidth = scaleHeight;

					if (width*scaleWidth<=mDrawCanvas.getWidth()){
						scaleWidth=((float) mDrawCanvas.getWidth()) / width;
						scaleHeight=scaleWidth;

					}

			}



		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap b1= Bitmap.createBitmap(b, 0, 0,
				width, height, matrix, true);

		int width1 = b1.getWidth();
		int height1 = b1.getHeight();

		int x0=(width1/2-mDrawCanvas.getWidth()/2)-1;
		int y0=(height1/2-mDrawCanvas.getHeight()/2)-1;

		if (x0<0) x0=0;
		if (y0<0) y0=0;


		mCanvasBitmapBackground= Bitmap.createBitmap(b1, x0, y0,
				mDrawCanvas.getWidth(), mDrawCanvas.getHeight());
		b1.recycle();
		Log.d(TAG, "SetCustomBwidth: " + b1.getWidth());
		Log.d(TAG, "SetCustomBheight: "+ b1.getHeight());
		Log.d(TAG, "SetCustomBcanwidth: " + mDrawCanvas.getWidth());
		Log.d(TAG, "SetCustomBcanheight: "+ mDrawCanvas.getHeight());



		invalidate();
	}

	public void SetCustomBitmap1(Bitmap b) {
		int width = b.getWidth();
		int height = b.getHeight();



		float scaleWidth = ((float) this.getWidth()) / width;
		float scaleHeight = ((float) this.getHeight()) / height;

		if (scaleWidth>scaleHeight)
		{scaleWidth=scaleHeight;}
		else {scaleHeight=scaleWidth;}

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		this.mCanvasBitmapBackground= Bitmap.createBitmap(b, 0, 0,
				width, height, matrix, true);
		b.recycle();

		invalidate();
	}
}
