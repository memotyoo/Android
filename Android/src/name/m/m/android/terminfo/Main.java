// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/01/26]
// Last updated: [2012/01/26]
package name.m.m.android.terminfo;

import name.m.m.android.R;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 端末情報。
 */
public class Main extends Activity {

    private static final String TAG = Main.class.getSimpleName();

    // private static final int BUILD_VERSION_CODES_HONEYCOMB = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.terminfo_title);
        setContentView(R.layout.terminfo_main);

        final DisplayMetrics dm = new DisplayMetrics();
        final Display d = getWindowManager().getDefaultDisplay();
        d.getMetrics(dm);
        final int dpi = dm.densityDpi;

        final int w = dm.widthPixels;
        int h = dm.heightPixels;

        final Configuration conf = getResources().getConfiguration();
        Log.v(TAG, "conf.fontScale: " + conf.fontScale);
        Log.v(TAG, conf.locale.getCountry());
        // conf.locale.getCountry()
        // conf.locale.getDisplayName(null);

        // Size
        final int sl = conf.screenLayout;
        String size = getString(R.string.terminfo_unknown);
        if ((sl & Configuration.SCREENLAYOUT_SIZE_SMALL) != 0) {
            size = getString(R.string.terminfo_small);
        } else if ((sl & Configuration.SCREENLAYOUT_SIZE_NORMAL) != 0) {
            size = getString(R.string.terminfo_normal);
        } else if ((sl & Configuration.SCREENLAYOUT_SIZE_LARGE) != 0) {
            size = getString(R.string.terminfo_large);
        } else {
            Log.w(TAG, "unknown display size: " + sl);
        }
        ((TextView) findViewById(R.id.display_size)).setText(size);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.i(TAG, "for SDK >= 11");
            try {
                final Method getRawWidth = Display.class.getMethod("getRawWidth");
                final Method getRawHeight = Display.class.getMethod("getRawHeight");
                final int rawWidth = (Integer) getRawWidth.invoke(d);
                final int rawHeight = (Integer) getRawHeight.invoke(d);

                // Status Bar
                ((TextView) findViewById(R.id.display_statusbar_height)).setText(getString(
                        R.string.terminfo_unit_pixel, rawHeight - h));

                if (w == rawWidth) {
                    h = rawHeight;
                }
            } catch (final Throwable e) {
                Log.w(TAG, e);
            }
        } else {
            Log.i(TAG, "for SDK < 11");
            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    final Rect r = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

                    // Status Bar
                    ((TextView) findViewById(R.id.display_statusbar_height)).setText(getString(
                            R.string.terminfo_unit_pixel, dm.heightPixels - r.height()));

                }
            });
        }

        ((TextView) findViewById(R.id.display_width)).setText(getString(
                R.string.terminfo_unit_pixel, w));
        ((TextView) findViewById(R.id.display_height)).setText(getString(
                R.string.terminfo_unit_pixel, h));
        ((TextView) findViewById(R.id.display_density_dpi)).setText(String.valueOf(dpi));

        switch (dpi) {
        case DisplayMetrics.DENSITY_LOW:
            ((TextView) findViewById(R.id.display_dpi)).setText(R.string.terminfo_ldpi);
            break;

        case DisplayMetrics.DENSITY_MEDIUM:
            ((TextView) findViewById(R.id.display_dpi)).setText(R.string.terminfo_mdpi);
            break;

        case DisplayMetrics.DENSITY_HIGH:
            ((TextView) findViewById(R.id.display_dpi)).setText(R.string.terminfo_hdpi);
            break;

        default:
            final StringBuffer sb = new StringBuffer();
            sb.append(getString(R.string.terminfo_unknown));
            sb.append(getString(R.string.terminfo_separate_colon));
            sb.append(dpi);
            ((TextView) findViewById(R.id.display_density_dpi)).setText(sb.toString());
            break;
        }

        // Refresh Rate
        ((TextView) findViewById(R.id.display_refresh_rate)).setText(getString(
                R.string.terminfo_unit_hertz, d.getRefreshRate()));

        final StringBuffer sbBuild = new StringBuffer();
        for (final Field o : Build.class.getFields()) {
            try {
                sbBuild.append(o.getName());
                sbBuild.append(getString(R.string.terminfo_separate_colon));
                sbBuild.append(o.get(Build.class));
                sbBuild.append("\n");
            } catch (final IllegalArgumentException e) {
            } catch (final IllegalAccessException e) {
            }
        }
        if (sbBuild.length() > 0) {
            ((TextView) findViewById(R.id.build)).setText(sbBuild.toString());
        }

        final StringBuffer sbVersion = new StringBuffer();
        BufferedReader raderVersion = null;
        try {
            raderVersion = new BufferedReader(new FileReader("/proc/version"), 256);
            while (true) {
                final String line = raderVersion.readLine();
                if (line == null) {
                    break;
                }
                sbVersion.append(line);
                sbVersion.append("\n");
            }
        } catch (final FileNotFoundException e) {
        } catch (final IOException e) {
        } finally {
            if (raderVersion != null) {
                try {
                    raderVersion.close();
                } catch (final IOException e) {
                }
            }
        }
        if (sbVersion.length() > 0) {
            ((TextView) findViewById(R.id.proc_version)).setText(sbVersion.toString());
        }

        final StringBuffer sbCpuInfo = new StringBuffer();
        BufferedReader readerCpuInfo = null;
        try {
            readerCpuInfo = new BufferedReader(new FileReader("/proc/cpuinfo"), 256);
            while (true) {
                final String line = readerCpuInfo.readLine();
                if (line == null) {
                    break;
                }
                sbCpuInfo.append(line);
                sbCpuInfo.append("\n");
            }
        } catch (final FileNotFoundException e) {
        } catch (final IOException e) {
        } finally {
            if (readerCpuInfo != null) {
                try {
                    readerCpuInfo.close();
                } catch (final IOException e) {
                }
            }
        }
        if (sbCpuInfo.length() > 0) {
            ((TextView) findViewById(R.id.proc_cpuinfo)).setText(sbCpuInfo.toString());
        }

        final StringBuffer sbMemInfo = new StringBuffer();
        BufferedReader readerMemInfo = null;
        try {
            readerMemInfo = new BufferedReader(new FileReader("/proc/meminfo"), 256);
            while (true) {
                final String line = readerMemInfo.readLine();
                if (line == null) {
                    break;
                }
                sbMemInfo.append(line);
                sbMemInfo.append("\n");
            }
        } catch (final FileNotFoundException e) {
        } catch (final IOException e) {
        } finally {
            if (readerMemInfo != null) {
                try {
                    readerMemInfo.close();
                } catch (final IOException e) {
                }
            }
        }
        if (sbMemInfo.length() > 0) {
            ((TextView) findViewById(R.id.proc_meminfo)).setText(sbMemInfo.toString());
        }

    }
}
