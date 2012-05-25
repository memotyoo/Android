// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/05/21]
// Last updated: [2012/05/21]
package name.m.m.android.game;

import name.m.m.android.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;
import android.view.MotionEvent;

/**
 *
 */
public class BackgroundDrawer extends BaseDrawer {

    /** タグ。 */
    private static final String TAG = BackgroundDrawer.class.getSimpleName();

    private static final int BGM_LOOP_COUNT = 8;

    private static boolean sIsInit = false;

    private static MediaPlayer sBgm = null;
    private static int sBgmLoopCount = 0;

    private static BackgroundDrawer instance = new BackgroundDrawer();

    private BackgroundDrawer() {
    }

    public static BackgroundDrawer getInstance() {
        return instance;
    }

    protected void draw(Context context, Canvas c) {
        if (!sIsInit) {
            init(context);
            return;
        }

        c.drawColor(Color.BLACK);

        if (ActionDrawer.sIsInit) {

        }
    }

    protected void onChanged() {
        Log.v(TAG, "onChanged");
    }

    protected void onDestroyed() {
        Log.v(TAG, "onDestroyed");

        if (sBgm != null) {
            sBgm.release();
            sBgm = null;
        }

        sIsInit = false;
    }

    protected boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "onTouchEvent");

        return false;
    }

    private void init(final Context context) {
        Log.v(TAG, "init");

        if (sBgm == null) {
            sBgm = MediaPlayer.create(context, R.raw.bgm);
        }

        Log.v(TAG, "MediaPlayer bgm: " + sBgm.getAudioSessionId());
        sBgm.start();
        sBgm.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                sBgmLoopCount++;
                if (sBgmLoopCount < BGM_LOOP_COUNT) {
                    mp.start();

                } else {
                    final MediaPlayer timelimit = MediaPlayer.create(context, R.raw.timelimit);
                    Log.v(TAG, "MediaPlayer timelimit: " + timelimit.getAudioSessionId());
                    timelimit.start();
                    timelimit.setOnErrorListener(new OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            Log.w(TAG, "onError id: " + mp.getAudioSessionId() + " what: " + what
                                    + " extra: " + extra);
                            mp.release();
                            mp = null;
                            playEnd();
                            return true;
                        }
                    });
                    timelimit.setOnCompletionListener(new OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                            mp = null;
                            playEnd();
                        }
                    });
                }
            }

            private void playEnd() {
                final MediaPlayer end = MediaPlayer.create(context, R.raw.gameover);
                Log.v(TAG, "MediaPlayer gameover: " + end.getAudioSessionId());
                end.start();
                end.setOnErrorListener(new OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.w(TAG, "onError id: " + mp.getAudioSessionId() + " what: " + what
                                + " extra: " + extra);

                        mp.release();
                        mp = null;
                        MainSurfaceView.sTimeline.forceFinish();
                        return true;
                    }
                });
                end.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        mp = null;
                        MainSurfaceView.sTimeline.forceFinish();
                    }
                });
            }
        });

        sBgmLoopCount = 0;

        sIsInit = true;
    }
}
