// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/05/21]
// Last updated: [2012/05/21]
package name.m.m.android.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 
 */
public class BackgroundDrawer extends BaseDrawer {

    /** タグ。 */
    private static final String TAG = BackgroundDrawer.class.getSimpleName();

    private static BackgroundDrawer instance = new BackgroundDrawer();

    private BackgroundDrawer() {
    }

    public static BackgroundDrawer getInstance() {
        return instance;
    }

    protected void draw(Context context, Canvas c) {
        c.drawColor(Color.BLACK);
    }

    protected void onChanged() {
    }

    protected void onDestroyed() {
    }

    protected boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "onTouchEvent");

        return false;
    }

}
