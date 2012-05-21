// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/05/21]
// Last updated: [2012/05/21]
package name.m.m.android.game;

import name.m.m.android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 
 */
public class TestDrawer extends BaseDrawer {

    /** タグ。 */
    private static final String TAG = TestDrawer.class.getSimpleName();

    private static final int START_TIME_TEST = 0;
    private static Bitmap sIcon = null;
    private static long sReset = 0;

    private static TestDrawer instance = new TestDrawer();

    private TestDrawer() {
    }

    public static TestDrawer getInstance() {
        return instance;
    }

    protected void draw(Context context, Canvas c) {
        final long currentTime = MainSurfaceView.sTimeline.getTime();
        if (currentTime < START_TIME_TEST) {
            return;
        }
        final long t = currentTime - START_TIME_TEST;

        if (sIcon == null || sIcon.isRecycled()) {
            sIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        }
        int top = MainSurfaceView.sHeight - sIcon.getHeight();
        top -= (int) (t - sReset) / 100;
        if (top < 0) {
            sReset = t;
            top = MainSurfaceView.sHeight - sIcon.getHeight();
        }
        final int left = (MainSurfaceView.sWidth - sIcon.getWidth()) / 2;

        final Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        c.drawRect(left, top + sIcon.getHeight(), left + sIcon.getWidth(), MainSurfaceView.sHeight,
                paint);
        c.drawBitmap(sIcon, left, top, null);

        paint.setColor(Color.YELLOW);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(24);
        paint.setAntiAlias(true);

        c.drawText((t / 1000) + "m", 10, 100, paint);
    }

    protected void onChanged() {
    }

    protected void onDestroyed() {
        if (sIcon != null && !sIcon.isRecycled()) {
            sIcon.recycle();
        }
    }

    protected boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "onTouchEvent");

        return false;
    }

}
