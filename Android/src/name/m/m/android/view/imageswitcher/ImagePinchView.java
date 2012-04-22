// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/04/22]
// Last updated: [2012/04/22]
package name.m.m.android.view.imageswitcher;

import name.m.m.android.libs.ImageCacher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * 画像ピンチ表示ビュー。
 */
public class ImagePinchView extends View {

    private static final String TAG = ImagePinchView.class.getSimpleName();

    private String mImagePath = null;

    private RectF mRect = null;

    private Bitmap mBitmap = null;

    private boolean mIsLoading = false;

    private float mScale = 1.0f;

    private ScaleGestureDetector mScaleGestureDetector = null;

    private ScaleGestureDetector.SimpleOnScaleGestureListener mSimpleOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale += detector.getScaleFactor() > 1 ? 0.05f : -0.05f;
            if (mScale > 3) {
                mScale = 3.0f;
            } else if (mScale < 0.5f) {
                mScale = 0.5f;
            }
            invalidate();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    };

    public ImagePinchView(Context context) {
        this(context, null, 0);
    }

    public ImagePinchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImagePinchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleGestureDetector = new ScaleGestureDetector(context, mSimpleOnScaleGestureListener);
    }

    /**
     * 表示画像を設定する。
     * 
     * @param list 画像パス。
     */
    public void setImagePath(String path) {
        mImagePath = path;
    }

    /**
     * 表示倍率を設定する。
     * 
     * @param scale 倍率。
     */
    public void setScale(float scale) {
        mScale = scale;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            return mScaleGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mRect = new RectF(left, top, right, bottom);
        }
        Log.v(TAG, "onLayout " + changed + " " + mRect.toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(TAG, "onDraw: " + mScale);

        mBitmap = ImageCacher.getBitmap(mImagePath);
        if (mBitmap == null || mBitmap.isRecycled()) {
            if (!mIsLoading) {
                new LoadTask().execute();
                mIsLoading = true;
            }
            final Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            canvas.drawColor(Color.BLUE);
            return;
        }

        Log.v(TAG, "bitmap: " + mBitmap.getWidth() + "x" + mBitmap.getHeight());
        final float cX = mRect.centerX() - mScale * mBitmap.getWidth() / 2;
        final float cY = mRect.centerY() - mScale * mBitmap.getHeight() / 2;
        final Matrix matrix = canvas.getMatrix();

        canvas.save();
        matrix.postScale(mScale, mScale, cX, cY);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(mBitmap, cX, cY, null);
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.v(TAG, "onDetachedFromWindow");

        if (mBitmap != null) {
            Log.v(TAG, "bitmap recycled");
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    private class LoadTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(mImagePath, options);

            final int w = (int) (mRect.right - mRect.left - 50);
            final int h = (int) (mRect.bottom - mRect.top - 50);
            final int scaleW = options.outWidth / w;
            final int scaleH = options.outHeight / h;

            int scale = Math.max(scaleW, scaleH);

            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;

            return BitmapFactory.decodeFile(mImagePath, options);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            Log.v(TAG, "onPostExecute: " + (result != null));
            mIsLoading = false;
            if (result != null) {
                ImageCacher.putBitmap(mImagePath, result);
                invalidate();
            }
        }
    }
}
