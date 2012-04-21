// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/04/22]
// Last updated: [2012/04/22]
package name.m.m.android.view.imageswitchingview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

import java.util.ArrayList;

/**
 * 画像切替ビューア画面。
 */
public class Main extends Activity {

    private static final String TAG = Main.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String sdPath = Environment.getExternalStorageDirectory().getPath();
        final ArrayList<String> list = new ArrayList<String>();
        list.add(sdPath + "/external_sd/myroot/image/[20100812][001]いっちゃん.jpg");
        list.add(sdPath + "/external_sd/myroot/image/[20100814][004]西原.jpg");
        list.add(sdPath + "/external_sd/myroot/image/[20100921]ミカ.jpg");

        final ImageSwitchingView view = new ImageSwitchingView(getApplicationContext());
        view.setImageList(list, 1);

        setContentView(view);

    }

}
