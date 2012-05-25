// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/05/24]
// Last updated: [2012/05/24]
package name.m.m.android.game;

import name.m.m.android.R;
import name.m.m.android.game.ControllerDrawer.KeyListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;

/**
 * アクション描画クラス。
 */
public class ActionDrawer extends BaseDrawer {

    public static boolean sIsInit = false;

    public static float sMinLeft = 0;
    public static float sMaxLeft = 0;
    public static float sMinTop = 0;
    public static float sMaxTop = 0;

    /** タグ。 */
    private static final String TAG = TestDrawer.class.getSimpleName();

    private static final float SPEED = 3.0f;
    private static final float DASH = 3.0f;

    private static final float JUMP = 6.0f;
    private static final int JUMP_TIME = 600;

    private static float sSpeed = 0;
    private static float sJump = 0;

    private static Bitmap sChara = null;

    private static float sLeft = 0;
    private static float sTop = 0;

    private static float sCurrentSpeed = SPEED;

    private static boolean sIsJump = false;
    private static Timeline sJumpStartTime = null;
    private static float sJumpStartTop = 0;

    private static SoundPool sSe = null;

    private static ActionDrawer instance = new ActionDrawer();

    private ActionDrawer() {
    }

    public static ActionDrawer getInstance() {
        return instance;
    }

    @Override
    protected void draw(Context context, Canvas c) {
        if (!sIsInit) {
            init(context);
            return;
        }

        if (ControllerDrawer.sIsPressedB) {
            if (sCurrentSpeed == sSpeed) {
                sCurrentSpeed = DASH * sSpeed;
            }
        } else {
            sCurrentSpeed = sSpeed;
        }

        if (sIsJump || ControllerDrawer.sIsPressedA) {
            if (sJumpStartTime == null) {
                sJumpStartTime = new Timeline(JUMP_TIME);
                sIsJump = true;
                sJumpStartTop = sTop;
            }

            if (sJumpStartTime.isFinished()) {
                sJumpStartTime = null;
                sIsJump = false;
                sTop = sJumpStartTop;
            } else {

                final long now = sJumpStartTime.getTime();

                if (now > (JUMP_TIME / 2)) {
                    sTop += sJump;
                } else {
                    sTop -= sJump;
                }
            }
        }

        if (ControllerDrawer.sIsPressedLeft) {
            sLeft -= sCurrentSpeed;
            if (sLeft < sMinLeft) {
                sLeft = sMinLeft;
            }
        }

        if (ControllerDrawer.sIsPressedRight) {
            sLeft += sCurrentSpeed;
            if (sLeft > sMaxLeft) {
                sLeft = sMaxLeft;
            }
        }

        // if (ControllerDrawer.sIsPressedTop) {
        // mTop -= mSpeed;
        // if (mTop < mMinTop) {
        // mTop = mMinLeft;
        // }
        // }

        // if (ControllerDrawer.sIsPressedBottom) {
        // mTop += mSpeed;
        // if (mTop > mMaxTop) {
        // mTop = mMaxTop;
        // }
        // }

        c.drawBitmap(sChara, sLeft, sTop, null);
    }

    @Override
    protected void onChanged() {
        Log.v(TAG, "onChanged");
        sIsInit = false;
    }

    @Override
    protected void onDestroyed() {
        Log.v(TAG, "onDestroyed");

        sMinLeft = 0;
        sMaxLeft = 0;
        sMinTop = 0;
        sMaxTop = 0;

        sSpeed = 0;
        sJump = 0;

        if (sSe != null) {
            sSe.release();
            sSe = null;
        }

        if (sChara != null) {
            sChara.recycle();
            sChara = null;
        }

        sLeft = 0;
        sTop = 0;

        sCurrentSpeed = SPEED;

        sIsJump = false;
        sJumpStartTime = null;
        sJumpStartTop = 0;

        sIsInit = false;
    }

    @Override
    protected boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    private void init(Context context) {
        Log.v(TAG, "init");

        sSe = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        final int idJump = sSe.load(context, R.raw.jump, 1);

        if (sChara == null || sChara.isRecycled()) {
            sChara = BitmapFactory.decodeResource(context.getResources(), R.drawable.chara);
        }

        ControllerDrawer.setKeyListener(new KeyListener() {
            @Override
            public void onDown(String key) {
                if (key.equals(ControllerDrawer.KEY_BUTTON_A)) {
                    sSe.play(idJump, 1, 1, 0, 0, 1);
                }
            }
        });

        sSpeed = MainSurfaceView.sScale * SPEED;
        sJump = MainSurfaceView.sScale * JUMP;

        sLeft = (MainSurfaceView.sWidth - sChara.getWidth()) / 2;
        sTop = MainSurfaceView.sHeight - ControllerDrawer.sHeight - sChara.getHeight();

        sMinLeft = -sChara.getWidth();
        sMaxLeft = MainSurfaceView.sWidth;

        sMinTop = 0;
        sMaxTop = sTop;

        sIsInit = true;
    }
}
