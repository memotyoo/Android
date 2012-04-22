// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/04/22]
// Last updated: [2012/04/22]
package name.m.m.android.view.imageswitcher;

import name.m.m.android.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import java.util.ArrayList;

/**
 * 画像切替表示画面。
 */
public class Main extends Activity {

    private static final String TAG = Main.class.getSimpleName();

    private ViewFlipper mViewFlipper = null;

    private Animation mInFromLeft = null;
    private Animation mOutToRight = null;
    private Animation mInFromRight = null;
    private Animation mOutToLeft = null;

    private GestureDetector mGestureDetector = null;

    private OnGestureListener mOnGestureListener = new OnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float dx = Math.abs(velocityX);
            float dy = Math.abs(velocityY);
            if (dx > dy && dx > 150) {
                if (e1.getX() < e2.getX()) {
                    mViewFlipper.setInAnimation(mInFromLeft);
                    mViewFlipper.setOutAnimation(mOutToRight);
                    mViewFlipper.showPrevious();

                } else {
                    mViewFlipper.setInAnimation(mInFromRight);
                    mViewFlipper.setOutAnimation(mOutToLeft);
                    mViewFlipper.showNext();
                }

                ((ImagePinchView) mViewFlipper.getCurrentView()).setScale(1.0f);
                return true;
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    };

    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mGestureDetector.onTouchEvent(event)) {
                return true;
            }
            return false;
        }
    };

    private ArrayList<String> mImageList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.imageswicher_main);

        mViewFlipper = (ViewFlipper) findViewById(R.id.flipper);

        mGestureDetector = new GestureDetector(getApplicationContext(), mOnGestureListener);

        mInFromLeft = AnimationUtils.loadAnimation(this, R.anim.left_in);
        mOutToRight = AnimationUtils.loadAnimation(this, R.anim.right_out);
        mInFromRight = AnimationUtils.loadAnimation(this, R.anim.right_in);
        mOutToLeft = AnimationUtils.loadAnimation(this, R.anim.left_out);

        final Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                mImageList = new ArrayList<String>();
                do {
                    final String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                    if (path != null && !"".equals(path)) {
                        mImageList.add(path);
                    }
                } while (cursor.moveToNext());

                Log.v(TAG, "image count: " + mImageList.size());
                for (int i = 0; i < mImageList.size(); i++) {
                    final ImagePinchView child = new ImagePinchView(getApplicationContext());
                    child.setImagePath(mImageList.get(i));
                    child.setOnTouchListener(mOnTouchListener);
                    mViewFlipper.addView(child, i);
                }
            }
            cursor.close();
        }
    }
}
