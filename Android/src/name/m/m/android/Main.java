// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/01/11]
// Last updated: [2012/01/11]
package name.m.m.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * メイン。
 */
public class Main extends Activity {

    // アプリケーション情報
    private static final int POSITION_APPINFO = 0;

    // リスト
    private static final String[] LIST_ITEMS = new String[] {
        "Application Info"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ArrayList<String> list = new ArrayList<String>();
        list.add(LIST_ITEMS[POSITION_APPINFO]);

        final LinearLayout ll = new LinearLayout(getApplicationContext());
        ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        ll.setOrientation(LinearLayout.VERTICAL);

        final ListView lv = new ListView(getApplicationContext());
        lv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, list));
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
                switch (position) {
                // アプリケーション情報
                case POSITION_APPINFO:
                    startActivity(new Intent(getApplicationContext(),
                            name.m.m.android.appinfo.Main.class));
                    break;

                default:
                    break;
                }
            }
        });

        ll.addView(lv);
        setContentView(ll);
    }

}
