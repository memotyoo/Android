package name.m.m.android.game;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class Main extends Activity {

    /** タグ。 */
    private static final String TAG = Main.class.getSimpleName();

    /** Wake Lock。 */
    private WakeLock mWakeLock = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // wake lock
        // XXX: android.permission.WAKE_LOCK
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        if (!mWakeLock.isHeld()) {
            Log.v(TAG, "WakeLock acquire");
            mWakeLock.acquire();
        }

        setContentView(new MainSurfaceView(getApplicationContext()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWakeLock.isHeld()) {
            Log.v(TAG, "WakeLock release");
            mWakeLock.release();
        }
    }

}
