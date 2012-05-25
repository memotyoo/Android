// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/05/21]
// Last updated: [2012/05/21]
package name.m.m.android.game;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * 描画クラス。
 */
public class MainDrawer {

    private static MainDrawer instance = new MainDrawer();

    private MainDrawer() {
    }

    public static MainDrawer getInstance() {
        return instance;
    }

    public void draw(Context context, Canvas c) {
        getLayer01().draw(context, c);
        getLayer02().draw(context, c);
        getLayer03().draw(context, c);
    }

    public void onChanged() {
        getLayer01().onChanged();
        getLayer02().onChanged();
        getLayer03().onChanged();
    }

    public void onDestroyed() {
        getLayer01().onDestroyed();
        getLayer02().onDestroyed();
        getLayer03().onDestroyed();
    }

    public boolean onTouchEvent(MotionEvent event) {

        return getLayer02().onTouchEvent(event);
    }

    private BaseDrawer getLayer01() {
        return BackgroundDrawer.getInstance();
    }

    private BaseDrawer getLayer02() {
        return ControllerDrawer.getInstance();
    }

    private BaseDrawer getLayer03() {
        // return TestDrawer.getInstance();
        return ActionDrawer.getInstance();
    }

}
