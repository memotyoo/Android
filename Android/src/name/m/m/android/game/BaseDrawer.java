// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/05/21]
// Last updated: [2012/05/21]
package name.m.m.android.game;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * 描画ベースクラス。
 */
abstract class BaseDrawer {

    protected abstract void draw(Context context, Canvas c);

    protected abstract void onChanged();

    protected abstract void onDestroyed();

    protected abstract boolean onTouchEvent(MotionEvent event);

}
