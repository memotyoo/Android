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

import java.util.HashMap;

/**
 *
 */
public class ControllerDrawer extends BaseDrawer {

    public static float sHeight = 0;

    public static boolean sIsPressedLeft = false;
    public static boolean sIsPressedTop = false;
    public static boolean sIsPressedRight = false;
    public static boolean sIsPressedBottom = false;

    public static boolean sIsPressedA = false;
    public static boolean sIsPressedB = false;

    /** タグ。 */
    private static final String TAG = ControllerDrawer.class.getSimpleName();

    private static final int HEIGHT = 250;
    private static final int BAR_WIDTH = 90;
    private static final int BAR_LENGTH = 80;
    private static final int BUTTON_LENGTH = 200;

    private static final String KEY_BAR_LEFT = "BAR_LEFT";
    private static final String KEY_BAR_TOP = "BAR_TOP";
    private static final String KEY_BAR_RIGHT = "BAR_RIGHT";
    private static final String KEY_BAR_BOTTOM = "BAR_BOTTOM";

    private static final String KEY_BUTTON_A = "BUTTON_A";
    private static final String KEY_BUTTON_B = "BUTTON_B";

    private static final HashMap<String, Integer> sKeyMap = new HashMap<String, Integer>();

    private static boolean sIsInit = false;

    private static float sTop = 0;
    private static float sBarWidth = 0;
    private static float sBarLength = 0;
    private static float sButtonLength = 0;

    private static RectF sRectBarLeft = null;
    private static RectF sRectBarRight = null;
    private static RectF sRectBarTop = null;
    private static RectF sRectBarBottom = null;

    private static Paint sBarLeftPaint = null;
    private static Paint sBarTopPaint = null;
    private static Paint sBarRightPaint = null;
    private static Paint sBarBottomPaint = null;

    private static RectF sRectButtonB = null;
    private static RectF sRectButtonA = null;

    private static Paint sButtonBPaint = null;
    private static Paint sButtonAPaint = null;

    private static MediaPlayer sSeJump = null;

    private static ControllerDrawer instance = new ControllerDrawer();

    private ControllerDrawer() {
    }

    public static ControllerDrawer getInstance() {
        return instance;
    }

    @Override
    protected void draw(Context context, Canvas c) {
        if (!sIsInit) {
            init(context);
            return;
        }

        final Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        c.drawRect(0, sTop, MainSurfaceView.sWidth, MainSurfaceView.sHeight, paint);

        sBarLeftPaint.setColor(sIsPressedLeft ? Color.LTGRAY : Color.DKGRAY);
        sBarTopPaint.setColor(sIsPressedTop ? Color.LTGRAY : Color.DKGRAY);
        sBarRightPaint.setColor(sIsPressedRight ? Color.LTGRAY : Color.DKGRAY);
        sBarBottomPaint.setColor(sIsPressedBottom ? Color.LTGRAY : Color.DKGRAY);

        c.drawRect(sRectBarLeft, sBarLeftPaint);
        c.drawRect(sRectBarTop, sBarTopPaint);
        c.drawRect(sRectBarRight, sBarRightPaint);
        c.drawRect(sRectBarBottom, sBarBottomPaint);

        sButtonBPaint.setColor(sIsPressedB ? Color.LTGRAY : Color.DKGRAY);
        sButtonAPaint.setColor(sIsPressedA ? Color.LTGRAY : Color.DKGRAY);

        c.drawRect(sRectButtonB, sButtonBPaint);
        c.drawRect(sRectButtonA, sButtonAPaint);

    }

    @Override
    protected void onChanged() {
        Log.v(TAG, "onChanged");
    }

    @Override
    protected void onDestroyed() {
        Log.v(TAG, "onDestroyed");

        sKeyMap.clear();

        sTop = 0;
        sBarWidth = 0;
        sBarLength = 0;
        sButtonLength = 0;

        sRectBarLeft = null;
        sRectBarRight = null;
        sRectBarTop = null;
        sRectBarBottom = null;

        sBarLeftPaint = null;
        sBarTopPaint = null;
        sBarRightPaint = null;
        sBarBottomPaint = null;

        sRectButtonB = null;
        sRectButtonA = null;

        sButtonBPaint = null;
        sButtonAPaint = null;

        if (sSeJump != null) {
            sSeJump.release();
        }

        sIsInit = false;
    }

    @Override
    protected boolean onTouchEvent(MotionEvent event) {

        final int action = event.getAction();
        final int count = event.getPointerCount();
        final int mask = action & MotionEvent.ACTION_MASK;
        final int idx = (action & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        final int id = event.getPointerId(idx);

        switch (mask) {
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:

            if (sRectBarLeft.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BAR_LEFT) != null && sKeyMap.get(KEY_BAR_LEFT) == id) {
                    sIsPressedLeft = false;
                    sKeyMap.remove(KEY_BAR_LEFT);
                }
            }
            if (sRectBarTop.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BAR_TOP) != null && sKeyMap.get(KEY_BAR_TOP) == id) {
                    sIsPressedTop = false;
                    sKeyMap.remove(KEY_BAR_TOP);
                }
            }
            if (sRectBarRight.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BAR_RIGHT) != null && sKeyMap.get(KEY_BAR_RIGHT) == id) {
                    sIsPressedRight = false;
                    sKeyMap.remove(KEY_BAR_RIGHT);
                }
            }
            if (sRectBarBottom.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BAR_BOTTOM) != null && sKeyMap.get(KEY_BAR_BOTTOM) == id) {
                    sIsPressedBottom = false;
                    sKeyMap.remove(KEY_BAR_BOTTOM);
                }
            }

            if (sRectButtonB.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BUTTON_B) != null && sKeyMap.get(KEY_BUTTON_B) == id) {
                    sIsPressedB = false;
                    sKeyMap.remove(KEY_BUTTON_B);
                }
            }
            if (sRectButtonA.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BUTTON_A) != null && sKeyMap.get(KEY_BUTTON_A) == id) {
                    sIsPressedA = false;
                    sKeyMap.remove(KEY_BUTTON_A);
                }
            }
            break;

        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:

            if (sRectBarLeft.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BAR_LEFT) == null) {
                    sIsPressedLeft = true;
                    sKeyMap.put(KEY_BAR_LEFT, id);
                }
            }
            if (sRectBarTop.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BAR_TOP) == null) {
                    sIsPressedTop = true;
                    sKeyMap.put(KEY_BAR_TOP, id);
                }
            }
            if (sRectBarRight.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BAR_RIGHT) == null) {
                    sIsPressedRight = true;
                    sKeyMap.put(KEY_BAR_RIGHT, id);
                }
            }
            if (sRectBarBottom.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BAR_BOTTOM) == null) {
                    sIsPressedBottom = true;
                    sKeyMap.put(KEY_BAR_BOTTOM, id);
                }
            }

            if (sRectButtonB.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BUTTON_B) == null) {
                    sIsPressedB = true;
                    sKeyMap.put(KEY_BUTTON_B, id);
                }
            }
            if (sRectButtonA.contains(event.getX(idx), event.getY(idx))) {
                if (sKeyMap.get(KEY_BUTTON_A) == null) {
                    if (!sSeJump.isPlaying()) {
                        sSeJump.start();
                    }
                    sIsPressedA = true;
                    sKeyMap.put(KEY_BUTTON_A, id);
                }
            }
            break;

        case MotionEvent.ACTION_MOVE:

            for (int i = 0; i < count; i++) {
                final int pid = event.getPointerId(i);

                if (sKeyMap.get(KEY_BAR_LEFT) == null
                        && sRectBarLeft.contains(event.getX(i), event.getY(i))) {
                    sIsPressedLeft = true;
                    sKeyMap.put(KEY_BAR_LEFT, pid);
                } else if (sKeyMap.get(KEY_BAR_LEFT) != null && sKeyMap.get(KEY_BAR_LEFT) == pid
                        && !sRectBarLeft.contains(event.getX(i), event.getY(i))) {
                    sIsPressedLeft = false;
                    sKeyMap.remove(KEY_BAR_LEFT);
                }

                if (sKeyMap.get(KEY_BAR_TOP) == null
                        && sRectBarTop.contains(event.getX(i), event.getY(i))) {
                    sIsPressedTop = true;
                    sKeyMap.put(KEY_BAR_TOP, pid);
                } else if (sKeyMap.get(KEY_BAR_TOP) != null && sKeyMap.get(KEY_BAR_TOP) == pid
                        && !sRectBarTop.contains(event.getX(i), event.getY(i))) {
                    sIsPressedTop = false;
                    sKeyMap.remove(KEY_BAR_TOP);
                }

                if (sKeyMap.get(KEY_BAR_RIGHT) == null
                        && sRectBarRight.contains(event.getX(i), event.getY(i))) {
                    sIsPressedRight = true;
                    sKeyMap.put(KEY_BAR_RIGHT, pid);
                } else if (sKeyMap.get(KEY_BAR_RIGHT) != null && sKeyMap.get(KEY_BAR_RIGHT) == pid
                        && !sRectBarRight.contains(event.getX(i), event.getY(i))) {
                    sIsPressedRight = false;
                    sKeyMap.remove(KEY_BAR_RIGHT);
                }

                if (sKeyMap.get(KEY_BAR_BOTTOM) == null
                        && sRectBarBottom.contains(event.getX(i), event.getY(i))) {
                    sIsPressedBottom = true;
                    sKeyMap.put(KEY_BAR_BOTTOM, pid);
                } else if (sKeyMap.get(KEY_BAR_BOTTOM) != null
                        && sKeyMap.get(KEY_BAR_BOTTOM) == pid
                        && !sRectBarBottom.contains(event.getX(i), event.getY(i))) {
                    sIsPressedBottom = false;
                    sKeyMap.remove(KEY_BAR_BOTTOM);
                }

                if (sKeyMap.get(KEY_BUTTON_B) == null
                        && sRectButtonB.contains(event.getX(i), event.getY(i))) {
                    sIsPressedB = true;
                    sKeyMap.put(KEY_BUTTON_B, pid);
                } else if (sKeyMap.get(KEY_BUTTON_B) != null && sKeyMap.get(KEY_BUTTON_B) == pid
                        && !sRectButtonB.contains(event.getX(i), event.getY(i))) {
                    sIsPressedB = false;
                    sKeyMap.remove(KEY_BUTTON_B);
                }

                if (sKeyMap.get(KEY_BUTTON_A) == null
                        && sRectButtonA.contains(event.getX(i), event.getY(i))) {
                    if (!sSeJump.isPlaying()) {
                        sSeJump.start();
                    }
                    sIsPressedA = true;
                    sKeyMap.put(KEY_BUTTON_A, pid);
                } else if (sKeyMap.get(KEY_BUTTON_A) != null && sKeyMap.get(KEY_BUTTON_A) == pid
                        && !sRectButtonA.contains(event.getX(i), event.getY(i))) {
                    sIsPressedA = false;
                    sKeyMap.remove(KEY_BUTTON_A);
                }
            }

            break;

        default:
            break;
        }

        return true;
    }

    private void init(Context context) {
        Log.v(TAG, "init");

        if (sBarLeftPaint == null) {
            sBarLeftPaint = new Paint();
            sBarLeftPaint.setColor(Color.DKGRAY);
        }
        if (sBarTopPaint == null) {
            sBarTopPaint = new Paint();
            sBarTopPaint.setColor(Color.DKGRAY);
        }
        if (sBarRightPaint == null) {
            sBarRightPaint = new Paint();
            sBarRightPaint.setColor(Color.DKGRAY);
        }
        if (sBarBottomPaint == null) {
            sBarBottomPaint = new Paint();
            sBarBottomPaint.setColor(Color.DKGRAY);
        }

        if (sButtonBPaint == null) {
            sButtonBPaint = new Paint();
            sButtonBPaint.setColor(Color.DKGRAY);
        }
        if (sButtonAPaint == null) {
            sButtonAPaint = new Paint();
            sButtonAPaint.setColor(Color.DKGRAY);
        }

        if (sSeJump == null) {
            sSeJump = MediaPlayer.create(context, R.raw.jump);
        }

        sHeight = HEIGHT * MainSurfaceView.sScale;
        sTop = MainSurfaceView.sHeight - sHeight;

        sBarWidth = BAR_WIDTH * MainSurfaceView.sScale;
        sBarLength = BAR_LENGTH * MainSurfaceView.sScale;

        sRectBarLeft = new RectF(0, sTop + sHeight / 2 - sBarWidth / 2, sBarLength, sTop + sHeight
                / 2 + sBarWidth / 2);
        sRectBarTop = new RectF(sBarLength, sTop, sBarLength + sBarWidth, sTop + sBarLength);
        sRectBarRight = new RectF(sBarLength + sBarWidth, sTop + sHeight / 2 - sBarWidth / 2,
                sBarLength + sBarWidth + sBarLength, sTop + sHeight / 2 + sBarWidth / 2);
        sRectBarBottom = new RectF(sBarLength, sTop + sHeight / 2 + sBarWidth / 2, sBarLength
                + sBarWidth, MainSurfaceView.sHeight);

        sButtonLength = BUTTON_LENGTH * MainSurfaceView.sScale;

        sRectButtonB = new RectF(MainSurfaceView.sWidth - 2 * sButtonLength - sButtonLength / 4,
                sTop + (sHeight - sButtonLength) / 2, MainSurfaceView.sWidth - sButtonLength
                        - sButtonLength / 4, MainSurfaceView.sHeight - (sHeight - sButtonLength)
                        / 2);
        sRectButtonA = new RectF(MainSurfaceView.sWidth - sButtonLength, sTop
                + (sHeight - sButtonLength) / 2, MainSurfaceView.sWidth, MainSurfaceView.sHeight
                - (sHeight - sButtonLength) / 2);

        sIsInit = true;
    }

}
