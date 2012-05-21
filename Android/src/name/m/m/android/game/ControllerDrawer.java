// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/05/21]
// Last updated: [2012/05/21]
package name.m.m.android.game;

import name.m.m.android.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 
 */
public class ControllerDrawer extends BaseDrawer {

    /** タグ。 */
    private static final String TAG = ControllerDrawer.class.getSimpleName();

    private static final int HEIGHT = 250;
    private static final int BAR_WIDTH = 90;
    private static final int BAR_LENGTH = 80;
    private static final int BUTTON_LENGTH = 200;

    private float mHeight = 0;
    private float mTop = 0;
    private float mBarWidth = 0;
    private float mBarLength = 0;
    private float mButtonLength = 0;

    private RectF mRectBarLeft = null;
    private RectF mRectBarRight = null;
    private RectF mRectBarTop = null;
    private RectF mRectBarBottom = null;

    private Paint mBarLeftPaint = null;
    private Paint mBarTopPaint = null;
    private Paint mBarRightPaint = null;
    private Paint mBarBottomPaint = null;

    private RectF mRectButtonB = null;
    private RectF mRectButtonA = null;

    private Paint mButtonBPaint = null;
    private Paint mButtonAPaint = null;

    private MediaPlayer mSeJump = null;

    private static ControllerDrawer instance = new ControllerDrawer();

    private ControllerDrawer() {
    }

    public static ControllerDrawer getInstance() {
        return instance;
    }

    @Override
    protected void draw(Context context, Canvas c) {

        final Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        c.drawRect(0, mTop, MainSurfaceView.sWidth, MainSurfaceView.sHeight, paint);

        if (mBarLeftPaint == null) {
            mBarLeftPaint = new Paint();
            mBarLeftPaint.setColor(Color.DKGRAY);
        }
        if (mBarTopPaint == null) {
            mBarTopPaint = new Paint();
            mBarTopPaint.setColor(Color.DKGRAY);
        }
        if (mBarRightPaint == null) {
            mBarRightPaint = new Paint();
            mBarRightPaint.setColor(Color.DKGRAY);
        }
        if (mBarBottomPaint == null) {
            mBarBottomPaint = new Paint();
            mBarBottomPaint.setColor(Color.DKGRAY);
        }

        c.drawRect(mRectBarLeft, mBarLeftPaint);
        c.drawRect(mRectBarTop, mBarTopPaint);
        c.drawRect(mRectBarRight, mBarRightPaint);
        c.drawRect(mRectBarBottom, mBarBottomPaint);

        if (mButtonBPaint == null) {
            mButtonBPaint = new Paint();
            mButtonBPaint.setColor(Color.DKGRAY);
        }
        if (mButtonAPaint == null) {
            mButtonAPaint = new Paint();
            mButtonAPaint.setColor(Color.DKGRAY);
        }

        c.drawRect(mRectButtonB, mButtonBPaint);
        c.drawRect(mRectButtonA, mButtonAPaint);

        if (mSeJump == null) {
            mSeJump = MediaPlayer.create(context, R.raw.jump);
        }
    }

    @Override
    protected void onChanged() {
        Log.v(TAG, "onChanged");

        mHeight = HEIGHT * MainSurfaceView.sScale;
        mTop = MainSurfaceView.sHeight - mHeight;

        mBarWidth = BAR_WIDTH * MainSurfaceView.sScale;
        mBarLength = BAR_LENGTH * MainSurfaceView.sScale;

        mRectBarLeft = new RectF(0, mTop + mHeight / 2 - mBarWidth / 2, mBarLength, mTop + mHeight
                / 2 + mBarWidth / 2);
        mRectBarTop = new RectF(mBarLength, mTop, mBarLength + mBarWidth, mTop + mBarLength);
        mRectBarRight = new RectF(mBarLength + mBarWidth, mTop + mHeight / 2 - mBarWidth / 2,
                mBarLength + mBarWidth + mBarLength, mTop + mHeight / 2 + mBarWidth / 2);
        mRectBarBottom = new RectF(mBarLength, mTop + mHeight / 2 + mBarWidth / 2, mBarLength
                + mBarWidth, MainSurfaceView.sHeight);

        mButtonLength = BUTTON_LENGTH * MainSurfaceView.sScale;

        mRectButtonB = new RectF(MainSurfaceView.sWidth - 2 * mButtonLength - mButtonLength / 4,
                mTop + (mHeight - mButtonLength) / 2, MainSurfaceView.sWidth - mButtonLength
                        - mButtonLength / 4, MainSurfaceView.sHeight - (mHeight - mButtonLength)
                        / 2);
        mRectButtonA = new RectF(MainSurfaceView.sWidth - mButtonLength, mTop
                + (mHeight - mButtonLength) / 2, MainSurfaceView.sWidth, MainSurfaceView.sHeight
                - (mHeight - mButtonLength) / 2);
    }

    @Override
    protected void onDestroyed() {
        Log.v(TAG, "onDestroyed");
    }

    @Override
    protected boolean onTouchEvent(MotionEvent event) {

        final int action = event.getAction();
        final int count = event.getPointerCount();
        final int mask = action & MotionEvent.ACTION_MASK;

        boolean isLeft = false;
        boolean isTop = false;
        boolean isRight = false;
        boolean isBottom = false;

        boolean isB = false;
        boolean isA = false;

        Log.v("MM", "count: " + count);
        for (int i = 0; i < count; i++) {
            if (mRectBarLeft.contains(event.getX(i), event.getY(i))) {
                if (mask == MotionEvent.ACTION_UP || mask == MotionEvent.ACTION_OUTSIDE) {
                    isLeft = false;
                } else {
                    isLeft = true;
                }
            }
            if (mRectBarTop.contains(event.getX(i), event.getY(i))) {
                if (mask == MotionEvent.ACTION_UP || mask == MotionEvent.ACTION_OUTSIDE) {
                    isTop = false;
                } else {
                    isTop = true;
                }
            }
            if (mRectBarRight.contains(event.getX(i), event.getY(i))) {
                if (mask == MotionEvent.ACTION_UP || mask == MotionEvent.ACTION_OUTSIDE) {
                    isRight = false;
                } else {
                    isRight = true;
                }
            }
            if (mRectBarBottom.contains(event.getX(i), event.getY(i))) {
                if (mask == MotionEvent.ACTION_UP || mask == MotionEvent.ACTION_OUTSIDE) {
                    isBottom = false;
                } else {
                    isBottom = true;
                }
            }

            if (mRectButtonB.contains(event.getX(i), event.getY(i))) {
                if (mask == MotionEvent.ACTION_UP || mask == MotionEvent.ACTION_OUTSIDE) {
                    isB = false;
                } else {
                    isB = true;
                }
            }
            if (mRectButtonA.contains(event.getX(i), event.getY(i))) {
                if (mask == MotionEvent.ACTION_UP || mask == MotionEvent.ACTION_OUTSIDE) {
                    isA = false;
                } else if (mask == MotionEvent.ACTION_DOWN) {
                    if (!mSeJump.isPlaying()) {
                        mSeJump.start();
                    }
                    isA = true;
                } else {
                    isA = true;
                }
            }
        }

        mBarLeftPaint.setColor(isLeft ? Color.LTGRAY : Color.DKGRAY);
        mBarTopPaint.setColor(isTop ? Color.LTGRAY : Color.DKGRAY);
        mBarRightPaint.setColor(isRight ? Color.LTGRAY : Color.DKGRAY);
        mBarBottomPaint.setColor(isBottom ? Color.LTGRAY : Color.DKGRAY);

        mButtonBPaint.setColor(isB ? Color.LTGRAY : Color.DKGRAY);
        mButtonAPaint.setColor(isA ? Color.LTGRAY : Color.DKGRAY);

        return true;
    }

}
