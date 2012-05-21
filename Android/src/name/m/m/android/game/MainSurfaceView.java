// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/05/17]
// Last updated: [2012/05/17]
package name.m.m.android.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    /** 基準幅。 */
    public static final float BASE_WIDTH = 1280.0f;

    /** 描画タイムライン */
    public static Timeline sTimeline = null;

    /** ビュー 幅。 */
    public static int sWidth = 0;
    /** ビュー 高さ。 */
    public static int sHeight = 0;
    /** 表示倍率 */
    public static float sScale = 1f;

    /** タグ。 */
    private static final String TAG = MainSurfaceView.class.getSimpleName();

    /** ビュー表示フラグ。 */
    private boolean mIsAttached = false;

    /** 描画スレッド。 */
    private Thread mThread = null;
    /** 描画フラグ */
    private boolean mIsDraw = false;

    /** 描画終了リスナ */
    private OnFinishedListener mOnFinishedListener = null;

    /**
     * 描画終了リスナ
     */
    public interface OnFinishedListener {
        /**
         * 描画終了時コール
         */
        public void onFinished();
    }

    public MainSurfaceView(Context context) {
        super(context);

        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surfaceCreated");
        mIsAttached = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(TAG, "surfaceChanged: " + width + "x" + height);

        sWidth = width;
        sHeight = height;
        sScale = sWidth / BASE_WIDTH;

        MainDrawer.getInstance().onChanged();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed");

        mIsDraw = false;
        mIsAttached = false;

        // 確実にスレッド終了させる
        while (mThread != null && mThread.isAlive()) {
        }

        MainDrawer.getInstance().onDestroyed();

        sTimeline = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return MainDrawer.getInstance().onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public void run() {
        while (mIsAttached) {
            if (!getHolder().isCreating()) {
                if (sTimeline == null) {
                    sTimeline = new Timeline(-1);
                }

                if (mIsDraw && sTimeline.isFinished()) {
                    if (mOnFinishedListener == null) {
                        Log.w(TAG, "listener nof found");
                    } else {
                        Log.w(TAG, "bar chart finished");
                        mOnFinishedListener.onFinished();
                    }

                    mIsAttached = false;
                    break;
                }

                doDraw(getHolder());
            }
        }
    }

    /**
     * 描画処理
     * 
     * @param h サーフェースホルダ
     */
    private void doDraw(SurfaceHolder h) {
        final Canvas c = h.lockCanvas();
        if (c == null) {
            Log.w(TAG, "canvas is null");
            return;
        }
        mIsDraw = true;

        // 描画
        MainDrawer.getInstance().draw(getContext(), c);

        h.unlockCanvasAndPost(c);
    }
}
