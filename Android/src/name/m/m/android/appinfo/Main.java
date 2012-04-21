// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/01/11]
// Last updated: [2012/01/11]
package name.m.m.android.appinfo;

import name.m.m.android.R;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * アプリケーション情報。
 */
public class Main extends Activity {

    private static final String TAG = Main.class.getSimpleName();

    private static final double SIZE_UNIT = 1024.0;
    private static final String FORMAT_SIZE = "%.3f KB";
    private static final String ZERO_SIZE = "0 KB";

    private static final String SLASH = "/";
    private static final String BRACE_START = "[";
    private static final String BRACE_END = "]";
    private static final String NEW_LINE = "\n";
    private static final String EXT_HTML = ".html";
    private static final String EXT_TEXT = ".txt";

    private static final String HTML01 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
    private static final String HTML02 = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n";
    private static final String HTML03 = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"%s\" lang=\"%s\">\n";
    private static final String HTML04 = "<head>\n";
    private static final String HTML05 = "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />\n";
    private static final String HTML06 = "<meta http-equiv=\"content-style-type\" content=\"text/css\" />\n";
    private static final String HTML07 = "<title>%s</title>\n";
    private static final String HTML08 = "<style type=\"text/css\"><!--\n";
    private static final String HTML09 = "ol > li { margin-top: 1em; }\n";
    private static final String HTML10 = "--></style>\n";
    private static final String HTML11 = "</head>\n";
    private static final String HTML12 = "<body>\n";
    private static final String HTML13 = "</body>\n";
    private static final String HTML14 = "</html>\n";

    private static final String HTML_BR = "<br />\n";
    private static final String HTML_A_START = "<a href=\"%s\">";
    private static final String HTML_A_END = "</a>";
    private static final String HTML_OL_START = "<ol>\n";
    private static final String HTML_OL_END = "</ol>\n";
    private static final String HTML_UL_START = "<ul>\n";
    private static final String HTML_UL_END = "</ul>\n";
    private static final String HTML_LI_START = "<li>";
    private static final String HTML_LI_END = "</li>\n";
    private static final String HTML_DL_START = "<dl>\n";
    private static final String HTML_DL_END = "</dl>\n";
    private static final String HTML_DT_START = "<dt>";
    private static final String HTML_DT_END = "</dt>\n";
    private static final String HTML_DD_START = "<dd>";
    private static final String HTML_DD_END = "</dd>\n";

    // プリファレンスキー 表示モード
    private static final String SP_MODE = "appinfo_mode";
    // プリファレンスキー ソートオーダ
    private static final String SP_SORT = "appinfo_sort";
    // プリファレンスキー 検索文字列
    private static final String SP_SEARCH = "appinfo_search";
    // プリファレンスキー 出力フラグ
    private static final String SP_FORMAT = "appinfo_format";
    private static final String SP_DISPLAY = "appinfo_display";
    private static final String SP_PACKAGE_NAME = "appinfo_package_name";
    private static final String SP_APK_PATH = "appinfo_apk_path";
    private static final String SP_APK_SIZE = "appinfo_apk_size";
    private static final String SP_DATA_PATH = "appinfo_data_path";
    private static final String SP_DATA_SIZE = "appinfo_data_size";
    private static final String SP_DESCRIPTION = "appinfo_description";
    private static final String SP_MARKET_URL = "appinfo_market_url";
    private static final String SP_PERMISSION = "appinfo_permission";

    // 出力フラグ
    private int mOutputFormat = R.id.rb_html;
    private boolean mIsOutputDisplay = true;
    private boolean mIsOutputPackageName = false;
    private boolean mIsOutputApkPath = false;
    private boolean mIsOutputApkSize = false;
    private boolean mIsOutputDataPath = false;
    private boolean mIsOutputDataSize = false;
    private boolean mIsOutputDescription = false;
    private boolean mIsOutputMarketUrl = true;
    private boolean mIsOutputPermission = false;

    // 出力ファイル名
    private static final String FILE_NAME = "appinfo";

    // メニュー ID 表示
    private static final int MENU_DISPLAY = Menu.FIRST + 1;
    // メニュー ID ソート
    private static final int MENU_SORT = Menu.FIRST + 2;
    // メニュー ID 検索
    private static final int MENU_SEARCH = Menu.FIRST + 3;
    // メニュー ID 出力
    private static final int MENU_OUTPUT = Menu.FIRST + 4;

    // 表示モード ランチャー
    private static final int VIEW_MODE_LAUNCHER = 0;
    // 表示モード アンインストール可能
    private static final int VIEW_MODE_UNINSTALL = 1;
    // 表示モード プリインストール
    private static final int VIEW_MODE_SYSTEM = 2;
    // 表示モード プリインストール以外
    private static final int VIEW_MODE_WITHOUT_SYSTEM = 3;
    // 表示モード 全て
    private static final int VIEW_MODE_ALL = 4;

    // 表示モード
    private int mMode = 0;
    // 表示モード ラベル
    private String[] mModeLabel;

    // ソートオーダー 名称
    private static final int SORT_ORDER_NAME = 0;
    // ソートオーダー ソースサイズ
    private static final int SORT_ORDER_SOURCE_SIZE = 1;
    // ソートオーダー データサイズ
    private static final int SORT_ORDER_DATA_SIZE = 2;

    // ソートオーダー
    private int mSort = 0;
    // ソートオーダーコンパレータ
    private ArrayList<Comparator<AppInfo>> mSortComparator;
    // ソートオーダー ラベル
    private String[] mSortLabel;

    // 状態保存キー リストビュー選択位置
    private static final String STATE_LISTVIEW_TOP_POSITION = "LISTVIEW_TOP_POSITION";
    // 状態保存キー リストビュースクロール位置
    private static final String STATE_LISTVIEW_TOP_POSITION_Y = "LISTVIEW_TOP_POSITION_Y";

    // リストビュー選択位置
    private int mTopPosition = 0;
    // リストビュースクロール位置
    private int mTopPositionY = 0;

    // ビュールート
    private LinearLayout mRoot;
    // リストビュー
    private ListView mListView;
    // アダプタ
    private AppInfoAdapter mAdapter;

    // パッケージマネージャ
    private PackageManager mPm;
    // 権限情報
    private PermissionInfo mPi;

    // アプリ情報取得タスク
    private AsyncTask<Void, Void, Void> mAppInfoTask;
    // スキャン中フラグ
    private boolean mIsScanning = false;
    // 検索文字列
    private String mSearch = "";

    // ダイアログ
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPm = getPackageManager();

        mRoot = new LinearLayout(getApplicationContext());
        mRoot.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.setGravity(Gravity.CENTER);

        mModeLabel = new String[5];
        mModeLabel[VIEW_MODE_LAUNCHER] = getString(R.string.appinfo_menu_display_launcher);
        mModeLabel[VIEW_MODE_UNINSTALL] = getString(R.string.appinfo_menu_display_uninstall);
        mModeLabel[VIEW_MODE_SYSTEM] = getString(R.string.appinfo_menu_display_preinstall);
        mModeLabel[VIEW_MODE_WITHOUT_SYSTEM] = getString(R.string.appinfo_menu_display_except_preinstall);
        mModeLabel[VIEW_MODE_ALL] = getString(R.string.appinfo_menu_display_all);

        mSortComparator = new ArrayList<Comparator<AppInfo>>();
        mSortComparator.add(new NameComparator());
        mSortComparator.add(new SourceSizeComparator());
        mSortComparator.add(new DataSizeComparator());

        mSortLabel = new String[3];
        mSortLabel[SORT_ORDER_NAME] = ((NameComparator) mSortComparator.get(SORT_ORDER_NAME))
                .getName();
        mSortLabel[SORT_ORDER_SOURCE_SIZE] = ((SourceSizeComparator) mSortComparator
                .get(SORT_ORDER_SOURCE_SIZE)).getName();
        mSortLabel[SORT_ORDER_DATA_SIZE] = ((DataSizeComparator) mSortComparator
                .get(SORT_ORDER_DATA_SIZE)).getName();

        final SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        // プリファレンスキー 表示モード
        mMode = sp.getInt(SP_MODE, 0);
        // プリファレンスキー ソートオーダ
        mSort = sp.getInt(SP_SORT, 0);
        // プリファレンスキー 検索文字列
        mSearch = sp.getString(SP_SEARCH, "");
        // プリファレンスキー 出力フラグ
        mOutputFormat = sp.getInt(SP_FORMAT, R.id.rb_html);
        mIsOutputDisplay = sp.getBoolean(SP_DISPLAY, true);
        mIsOutputPackageName = sp.getBoolean(SP_PACKAGE_NAME, false);
        mIsOutputApkPath = sp.getBoolean(SP_APK_PATH, false);
        mIsOutputApkSize = sp.getBoolean(SP_APK_SIZE, false);
        mIsOutputDataPath = sp.getBoolean(SP_DATA_PATH, false);
        mIsOutputDataSize = sp.getBoolean(SP_DATA_SIZE, false);
        mIsOutputDescription = sp.getBoolean(SP_DESCRIPTION, false);
        mIsOutputMarketUrl = sp.getBoolean(SP_MARKET_URL, true);
        mIsOutputPermission = sp.getBoolean(SP_PERMISSION, false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mAppInfoTask = new AppInfoTask();
        mAppInfoTask.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListView != null) {
            mTopPosition = mListView.getFirstVisiblePosition();
            mTopPositionY = mListView.getChildAt(0).getTop();
            outState.putInt(STATE_LISTVIEW_TOP_POSITION, mTopPosition);
            outState.putInt(STATE_LISTVIEW_TOP_POSITION_Y, mTopPositionY);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTopPosition = savedInstanceState.getInt(STATE_LISTVIEW_TOP_POSITION, 0);
        mTopPositionY = savedInstanceState.getInt(STATE_LISTVIEW_TOP_POSITION_Y, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mAppInfoTask.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, MENU_DISPLAY, Menu.NONE, getString(R.string.appinfo_menu_display));
        menu.add(Menu.NONE, MENU_SORT, Menu.NONE, getString(R.string.appinfo_menu_sort));
        menu.add(Menu.NONE, MENU_SEARCH, Menu.NONE, getString(R.string.appinfo_menu_search));
        menu.add(Menu.NONE, MENU_OUTPUT, Menu.NONE, getString(R.string.appinfo_menu_output));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (mIsScanning) {
            return false;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean ret = true;

        switch (item.getItemId()) {
        // 表示
        case MENU_DISPLAY:
            mDialog = new AlertDialog.Builder(Main.this).setTitle(R.string.appinfo_menu_display)
                    .setItems(mModeLabel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            // 検索条件クリア
                            mSearch = "";
                            // スクロール位置クリア
                            mTopPosition = 0;
                            mTopPositionY = 0;

                            mMode = which;
                            final SharedPreferences.Editor ed = PreferenceManager
                                    .getDefaultSharedPreferences(getApplicationContext()).edit();
                            ed.putInt(SP_MODE, mMode);
                            ed.commit();

                            mAppInfoTask = new AppInfoTask();
                            mAppInfoTask.execute();
                        }
                    }).show();
            break;

        // ソート
        case MENU_SORT:
            mDialog = new AlertDialog.Builder(Main.this).setTitle(R.string.appinfo_menu_sort)
                    .setItems(mSortLabel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            // スクロール位置クリア
                            mTopPosition = 0;
                            mTopPositionY = 0;

                            mSort = which;
                            final SharedPreferences.Editor ed = PreferenceManager
                                    .getDefaultSharedPreferences(getApplicationContext()).edit();
                            ed.putInt(SP_SORT, mSort);
                            ed.commit();

                            mAppInfoTask = new AppInfoTask();
                            mAppInfoTask.execute();
                        }
                    }).show();
            break;

        // 検索
        case MENU_SEARCH:
            final EditText et = new EditText(getApplicationContext());
            et.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            et.setText(mSearch);
            et.setSelection(mSearch.length());

            mDialog = new AlertDialog.Builder(Main.this)
                    .setTitle(R.string.appinfo_menu_search)
                    .setView(et)
                    .setPositiveButton(getString(R.string.appinfo_button_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    final String search = et.getText().toString().trim();
                                    if (!mSearch.equals(search)) {

                                        // スクロール位置クリア
                                        mTopPosition = 0;
                                        mTopPositionY = 0;

                                        mSearch = search;
                                        final SharedPreferences.Editor ed = PreferenceManager
                                                .getDefaultSharedPreferences(
                                                        getApplicationContext()).edit();
                                        ed.putString(SP_SEARCH, mSearch);
                                        ed.commit();

                                        mAppInfoTask = new AppInfoTask();
                                        mAppInfoTask.execute();
                                    }
                                }
                            })
                    .setNegativeButton(getString(R.string.appinfo_button_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
            break;

        // 出力
        case MENU_OUTPUT:
            showOutputDialog();
            break;

        default:
            ret = super.onOptionsItemSelected(item);
            break;
        }

        return ret;
    }

    /**
     * 使用権限ダイアログを表示する。
     * 
     * @param ai アプリケーション情報。
     */
    private void showUsesPermissionDialog(AppInfo ai) {

        PackageInfo pi = null;
        try {
            pi = mPm.getPackageInfo(ai.mPackageName, PackageManager.GET_PERMISSIONS);
        } catch (final NameNotFoundException e) {
        }
        if (pi != null) {
            final String[] perms = pi.requestedPermissions;

            final LinearLayout llRoot = new LinearLayout(getApplicationContext());
            llRoot.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            llRoot.setOrientation(LinearLayout.VERTICAL);
            llRoot.setPadding(5, 5, 5, 5);
            llRoot.setBackgroundColor(Color.BLACK);

            if (perms == null) {
                final TextView tv = new TextView(getApplicationContext());
                tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT));
                tv.setPadding(5, 5, 5, 5);
                tv.setTextColor(Color.WHITE);
                tv.setText(getString(R.string.appinfo_title_nothing));

                llRoot.addView(tv);
            } else {
                Arrays.sort(perms);
                final int len = perms.length;

                if (mPi == null) {
                    mPi = new PermissionInfo(getApplicationContext());
                }
                for (int i = 0; i < len; i++) {
                    final String perm = perms[i];
                    final String permlab = mPi.mPermlabValue.get(perm);
                    final StringBuffer sb = new StringBuffer();
                    sb.append(perm);
                    if (permlab != null && !"".equals(permlab)) {
                        sb.append(NEW_LINE);
                        sb.append(BRACE_START);
                        sb.append(permlab);
                        final String permdesc = mPi.mPermdescValue.get(perm);
                        if (permdesc != null && !"".equals(mPi.mPermdescValue.get(perm))) {
                            sb.append(BRACE_END);
                            sb.append(NEW_LINE);
                            sb.append(permdesc);
                        }
                    }

                    final TextView tv = new TextView(getApplicationContext());
                    tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                            LayoutParams.WRAP_CONTENT));
                    tv.setPadding(0, 5, 0, 5);
                    tv.setTextColor(Color.WHITE);
                    tv.setText(sb.toString());

                    llRoot.addView(tv);

                    if (i != len - 1) {
                        final View border = new View(getApplicationContext());
                        border.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
                        border.setBackgroundColor(Color.WHITE);
                        llRoot.addView(border);
                    }
                }
            }

            final ScrollView sv = new ScrollView(getApplicationContext());
            sv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            sv.addView(llRoot);

            mDialog = new AlertDialog.Builder(Main.this)
                    .setTitle(getString(R.string.appinfo_menu_detail_uses_permission)).setView(sv)
                    .show();
        }
    }

    /**
     * 出力ダイアログを表示する。
     */
    private void showOutputDialog() {

        final View v = getLayoutInflater().inflate(R.layout.appinfo_output_dialog, null);

        // HTML にチェック
        final RadioGroup rg = (RadioGroup) v.findViewById(R.id.rg_format);
        rg.check(mOutputFormat);
        // 出力後に表示するにチェック
        final CheckBox cbDisplay = (CheckBox) v.findViewById(R.id.cb_display);
        cbDisplay.setChecked(mIsOutputDisplay);

        // アプリ名にチェック
        final CheckBox cbAppName = (CheckBox) v.findViewById(R.id.cb_app_name);
        cbAppName.setChecked(true);
        // 強制
        cbAppName.setEnabled(false);

        // パッケージ名
        final CheckBox cbPackageName = (CheckBox) v.findViewById(R.id.cb_package_name);
        cbPackageName.setChecked(mIsOutputPackageName);
        // APK Path
        final CheckBox cbApkPath = (CheckBox) v.findViewById(R.id.cb_source_dir);
        cbApkPath.setChecked(mIsOutputApkPath);
        // APK Size
        final CheckBox cbApkSize = (CheckBox) v.findViewById(R.id.cb_source_size);
        cbApkSize.setChecked(mIsOutputApkSize);
        // Data Path
        final CheckBox cbDataPath = (CheckBox) v.findViewById(R.id.cb_data_dir);
        cbDataPath.setChecked(mIsOutputDataPath);
        // Data Size
        final CheckBox cbDataSize = (CheckBox) v.findViewById(R.id.cb_data_size);
        cbDataSize.setChecked(mIsOutputDataSize);
        // Description
        final CheckBox cbDescription = (CheckBox) v.findViewById(R.id.cb_description);
        cbDescription.setChecked(mIsOutputDescription);
        // マーケット検索URLにチェック
        final CheckBox cbMarket = (CheckBox) v.findViewById(R.id.cb_market);
        cbMarket.setChecked(mIsOutputMarketUrl);
        // 使用権限
        final CheckBox cbPermission = (CheckBox) v.findViewById(R.id.cb_permission);
        cbPermission.setChecked(mIsOutputPermission);

        mDialog = new AlertDialog.Builder(Main.this)
                .setTitle(R.string.appinfo_menu_output)
                .setView(v)
                .setPositiveButton(getString(R.string.appinfo_button_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (!Environment.MEDIA_MOUNTED.equals(Environment
                                        .getExternalStorageState())) {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.appinfo_output_message_unavailable),
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // 形式
                                mOutputFormat = rg.getCheckedRadioButtonId();
                                // 出力後
                                mIsOutputDisplay = cbDisplay.isChecked();

                                // パッケージ名
                                mIsOutputPackageName = cbPackageName.isChecked();
                                // APK Path
                                mIsOutputApkPath = cbApkPath.isChecked();
                                // APK Size
                                mIsOutputApkSize = cbApkSize.isChecked();
                                // Data Path
                                mIsOutputDataPath = cbDataPath.isChecked();
                                // Data Size
                                mIsOutputDataSize = cbDataSize.isChecked();
                                // Description
                                mIsOutputDescription = cbDescription.isChecked();
                                // マーケット検索URL
                                mIsOutputMarketUrl = cbMarket.isChecked();
                                // 使用権限
                                mIsOutputPermission = cbPermission.isChecked();

                                final SharedPreferences.Editor ed = PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext())
                                        .edit();
                                ed.putInt(SP_FORMAT, mOutputFormat);
                                ed.putBoolean(SP_DISPLAY, mIsOutputDisplay);
                                ed.putBoolean(SP_PACKAGE_NAME, mIsOutputPackageName);
                                ed.putBoolean(SP_APK_PATH, mIsOutputApkPath);
                                ed.putBoolean(SP_APK_SIZE, mIsOutputApkSize);
                                ed.putBoolean(SP_DATA_PATH, mIsOutputDataPath);
                                ed.putBoolean(SP_DATA_SIZE, mIsOutputDataSize);
                                ed.putBoolean(SP_DESCRIPTION, mIsOutputDescription);
                                ed.putBoolean(SP_MARKET_URL, mIsOutputMarketUrl);
                                ed.putBoolean(SP_PERMISSION, mIsOutputPermission);
                                ed.commit();

                                File f = null;
                                switch (rg.getCheckedRadioButtonId()) {
                                case R.id.rb_html:
                                    f = outputHtml();
                                    if (f == null) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                getString(R.string.appinfo_output_message_unavailable),
                                                Toast.LENGTH_SHORT).show();
                                    } else if (cbDisplay.isChecked()) {
                                        try {
                                            final Uri uri = Uri.parse("file://" + f.getPath());
                                            final Intent it = new Intent(Intent.ACTION_VIEW);
                                            it.setDataAndType(uri, "text/html");
                                            startActivity(it);
                                        } catch (final ActivityNotFoundException e) {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    getString(R.string.appinfo_output_app_notfound),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    break;
                                case R.id.rb_text:
                                    f = outputText();
                                    if (f == null) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                getString(R.string.appinfo_output_message_unavailable),
                                                Toast.LENGTH_SHORT).show();
                                    } else if (cbDisplay.isChecked()) {
                                        try {
                                            final Uri uri = Uri.parse("file://" + f.getPath());
                                            final Intent it = new Intent(Intent.ACTION_VIEW);
                                            it.setDataAndType(uri, "text/*");
                                            startActivity(it);
                                        } catch (final ActivityNotFoundException e) {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    getString(R.string.appinfo_output_app_notfound),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    break;

                                default:
                                    break;
                                }

                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.appinfo_button_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    /**
     * アプリ一覧情報を HTML ファイルに出力する。
     * 
     * @return HTML ファイル。
     */
    private File outputHtml() {

        final StringBuffer sb = new StringBuffer();
        sb.append(HTML01);
        sb.append(HTML02);
        final String locale = Locale.getDefault().toString();

        sb.append(String.format(HTML03, locale, locale));
        sb.append(HTML04);
        sb.append(HTML05);
        sb.append(HTML06);
        sb.append(String.format(HTML07, getTitleString()));
        sb.append(HTML08);
        sb.append(HTML09);
        sb.append(HTML10);
        sb.append(HTML11);

        sb.append(HTML12);
        sb.append(HTML_OL_START);

        for (final AppInfo o : mAdapter.mAiList) {
            sb.append(HTML_LI_START);
            sb.append(o.mAppName);
            sb.append(NEW_LINE);

            sb.append(HTML_DL_START);
            if (mIsOutputPackageName) {
                sb.append(HTML_DT_START);
                sb.append(getString(R.string.appinfo_detail_package_name));
                sb.append(HTML_DT_END);
                sb.append(HTML_DD_START);
                sb.append(o.mPackageName);
                sb.append(HTML_DD_END);
            }
            if (mIsOutputApkPath) {
                sb.append(HTML_DT_START);
                sb.append(getString(R.string.appinfo_detail_source_dir));
                sb.append(HTML_DT_END);
                sb.append(HTML_DD_START);
                sb.append(o.mSourceDir);
                sb.append(HTML_DD_END);
            }
            if (mIsOutputApkSize) {
                sb.append(HTML_DT_START);
                sb.append(getString(R.string.appinfo_detail_source_size));
                sb.append(HTML_DT_END);
                sb.append(HTML_DD_START);
                if (o.mSourceSize == 0) {
                    sb.append(ZERO_SIZE);
                } else {
                    sb.append(String.format(FORMAT_SIZE, o.mSourceSize / SIZE_UNIT));
                }
                sb.append(HTML_DD_END);
            }
            if (mIsOutputDataPath) {
                sb.append(HTML_DT_START);
                sb.append(getString(R.string.appinfo_detail_data_dir));
                sb.append(HTML_DT_END);
                sb.append(HTML_DD_START);
                sb.append(o.mDataDir);
                sb.append(HTML_DD_END);
            }
            if (mIsOutputDataSize) {
                sb.append(HTML_DT_START);
                sb.append(getString(R.string.appinfo_detail_data_size));
                sb.append(HTML_DT_END);
                sb.append(HTML_DD_START);
                if (o.mDataSize == 0) {
                    sb.append(ZERO_SIZE);
                } else {
                    sb.append(String.format(FORMAT_SIZE, o.mDataSize / SIZE_UNIT));
                }
                sb.append(HTML_DD_END);
            }
            if (mIsOutputDescription) {
                final String desc = o.mDescription;
                if (!"".equals(desc)) {
                    sb.append(HTML_DT_START);
                    sb.append(getString(R.string.appinfo_detail_description));
                    sb.append(HTML_DT_END);
                    sb.append(HTML_DD_START);
                    sb.append(desc);
                    sb.append(HTML_DD_END);
                }
            }
            if (mIsOutputMarketUrl) {
                sb.append(HTML_DT_START);
                sb.append(getString(R.string.appinfo_output_market_search_url));
                sb.append(HTML_DT_END);
                sb.append(HTML_DD_START);
                sb.append(String.format(HTML_A_START, o.mMarketUrl));
                sb.append(o.mMarketUrl);
                sb.append(HTML_A_END);
                sb.append(HTML_DD_END);
            }
            if (mIsOutputPermission) {
                PackageInfo pi = null;
                try {
                    pi = mPm.getPackageInfo(o.mPackageName, PackageManager.GET_PERMISSIONS);
                } catch (final NameNotFoundException e) {
                }
                if (pi != null) {
                    final String[] perms = pi.requestedPermissions;
                    if (perms != null) {
                        sb.append(HTML_DT_START);
                        sb.append(getString(R.string.appinfo_menu_detail_uses_permission));
                        sb.append(HTML_DT_END);
                        sb.append(HTML_DD_START);

                        Arrays.sort(perms);

                        final int len = perms.length;
                        if (mPi == null) {
                            mPi = new PermissionInfo(getApplicationContext());
                        }

                        final StringBuffer sbPerm = new StringBuffer();
                        sbPerm.append(HTML_UL_START);
                        for (int i = 0; i < len; i++) {
                            final String perm = perms[i];
                            final String permlab = mPi.mPermlabValue.get(perm);

                            sbPerm.append(HTML_LI_START);
                            sbPerm.append(perm);

                            if (permlab != null && !"".equals(permlab)) {
                                sbPerm.append(HTML_BR);
                                sbPerm.append(BRACE_START);
                                sbPerm.append(permlab);
                                sbPerm.append(BRACE_END);
                                final String permdesc = mPi.mPermdescValue.get(perm);
                                if (permdesc != null && !"".equals(mPi.mPermdescValue.get(perm))) {
                                    sbPerm.append(HTML_BR);
                                    sbPerm.append(permdesc);
                                }
                                sbPerm.append(HTML_BR);
                            }

                            sbPerm.append(HTML_LI_END);
                        }
                        sbPerm.append(HTML_UL_END);

                        sb.append(HTML_DD_END);
                        if (sbPerm.length() > 0) {
                            sb.append(sbPerm.toString());
                        }
                    }
                }
            }

            sb.append(HTML_DL_END);
            sb.append(HTML_LI_END);
        }

        sb.append(HTML_OL_END);
        sb.append(HTML13);
        sb.append(HTML14);

        final StringBuffer sbPath = new StringBuffer();
        sbPath.append(Environment.getExternalStorageDirectory().getPath());
        sbPath.append(SLASH);
        sbPath.append(FILE_NAME);
        sbPath.append(EXT_HTML);

        final File f = new File(sbPath.toString());
        if (f.exists()) {
            if (!f.isFile() || !f.canWrite()) {
                return null;
            }
        }

        FileWriter fw = null;
        try {
            // XXX: android.permission.WRITE_EXTERNAL_STORAGE
            fw = new FileWriter(f);
        } catch (final IOException e) {
            Log.w(TAG, e);
            return null;
        }

        BufferedWriter bw = null;
        PrintWriter pw = null;
        try {
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            pw.println(sb.toString());
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (final IOException e) {
                }
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (final IOException e) {
                }
            }
        }

        return f;
    }

    /**
     * アプリ一覧情報をテキストファイルに出力する。
     * 
     * @return テキストファイル。
     */
    private File outputText() {

        final StringBuffer sb = new StringBuffer();
        sb.append(getTitleString());
        sb.append(NEW_LINE);

        for (final AppInfo o : mAdapter.mAiList) {
            sb.append(getString(R.string.appinfo_output_punc));
            sb.append(o.mAppName);
            sb.append(NEW_LINE);

            if (mIsOutputPackageName) {
                sb.append(getString(R.string.appinfo_output_indent));
                sb.append(getString(R.string.appinfo_detail_package_name));
                sb.append(getString(R.string.appinfo_separate_colon));
                sb.append(o.mPackageName);
                sb.append(NEW_LINE);
            }
            if (mIsOutputApkPath) {
                sb.append(getString(R.string.appinfo_output_indent));
                sb.append(getString(R.string.appinfo_detail_source_dir));
                sb.append(getString(R.string.appinfo_separate_colon));
                sb.append(o.mSourceDir);
                sb.append(NEW_LINE);
            }
            if (mIsOutputApkSize) {
                sb.append(getString(R.string.appinfo_output_indent));
                sb.append(getString(R.string.appinfo_detail_source_size));
                sb.append(getString(R.string.appinfo_separate_colon));
                if (o.mSourceSize == 0) {
                    sb.append(ZERO_SIZE);
                } else {
                    sb.append(String.format(FORMAT_SIZE, o.mSourceSize / SIZE_UNIT));
                }
                sb.append(NEW_LINE);
            }
            if (mIsOutputDataPath) {
                sb.append(getString(R.string.appinfo_output_indent));
                sb.append(getString(R.string.appinfo_detail_data_dir));
                sb.append(getString(R.string.appinfo_separate_colon));
                sb.append(o.mDataDir);
                sb.append(NEW_LINE);
            }
            if (mIsOutputDataSize) {
                sb.append(getString(R.string.appinfo_output_indent));
                sb.append(getString(R.string.appinfo_detail_data_size));
                sb.append(getString(R.string.appinfo_separate_colon));
                if (o.mDataSize == 0) {
                    sb.append(ZERO_SIZE);
                } else {
                    sb.append(String.format(FORMAT_SIZE, o.mDataSize / SIZE_UNIT));
                }
                sb.append(NEW_LINE);
            }
            if (mIsOutputDescription) {
                final String desc = o.mDescription;
                if (!"".equals(desc)) {
                    sb.append(getString(R.string.appinfo_output_indent));
                    sb.append(getString(R.string.appinfo_detail_description));
                    sb.append(getString(R.string.appinfo_separate_colon));
                    sb.append(desc);
                    sb.append(NEW_LINE);
                }
            }
            if (mIsOutputMarketUrl) {
                sb.append(getString(R.string.appinfo_output_indent));
                sb.append(getString(R.string.appinfo_output_market_search_url));
                sb.append(getString(R.string.appinfo_separate_colon));
                sb.append(o.mMarketUrl);
                sb.append(NEW_LINE);
            }
            if (mIsOutputPermission) {
                PackageInfo pi = null;
                try {
                    pi = mPm.getPackageInfo(o.mPackageName, PackageManager.GET_PERMISSIONS);
                } catch (final NameNotFoundException e) {
                }
                if (pi != null) {
                    final String[] perms = pi.requestedPermissions;
                    if (perms != null) {
                        sb.append(getString(R.string.appinfo_output_indent));
                        sb.append(getString(R.string.appinfo_menu_detail_uses_permission));
                        sb.append(getString(R.string.appinfo_separate_colon));

                        Arrays.sort(perms);

                        final int len = perms.length;
                        if (mPi == null) {
                            mPi = new PermissionInfo(getApplicationContext());
                        }

                        final StringBuffer sbPerm = new StringBuffer();
                        for (int i = 0; i < len; i++) {
                            final String perm = perms[i];
                            final String permlab = mPi.mPermlabValue.get(perm);

                            sbPerm.append(NEW_LINE);
                            sbPerm.append(getString(R.string.appinfo_output_indent));
                            sbPerm.append(getString(R.string.appinfo_output_indent));
                            sbPerm.append(perm);

                            if (permlab != null && !"".equals(permlab)) {
                                sbPerm.append(NEW_LINE);
                                sbPerm.append(getString(R.string.appinfo_output_indent));
                                sbPerm.append(getString(R.string.appinfo_output_indent));
                                sbPerm.append("[");
                                sbPerm.append(permlab);
                                final String permdesc = mPi.mPermdescValue.get(perm);
                                if (permdesc != null && !"".equals(mPi.mPermdescValue.get(perm))) {
                                    sbPerm.append("]\n");
                                    sbPerm.append(getString(R.string.appinfo_output_indent));
                                    sbPerm.append(getString(R.string.appinfo_output_indent));
                                    sbPerm.append(permdesc);
                                }
                            }

                            sbPerm.append(NEW_LINE);
                        }

                        if (sbPerm.length() > 0) {
                            sb.append(sbPerm.toString());
                        }
                    }
                }
            }

            sb.append(NEW_LINE);
        }

        final StringBuffer sbPath = new StringBuffer();
        sbPath.append(Environment.getExternalStorageDirectory().getPath());
        sbPath.append(SLASH);
        sbPath.append(FILE_NAME);
        sbPath.append(EXT_TEXT);

        final File f = new File(sbPath.toString());
        if (f.exists()) {
            if (!f.isFile() || !f.canWrite()) {
                return null;
            }
        }

        FileWriter fw = null;
        try {
            // XXX: android.permission.WRITE_EXTERNAL_STORAGE
            fw = new FileWriter(f);
        } catch (final IOException e) {
            Log.w(TAG, e);
            return null;
        }

        BufferedWriter bw = null;
        PrintWriter pw = null;
        try {
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            pw.println(sb.toString());
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (final IOException e) {
                }
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (final IOException e) {
                }
            }
        }

        return f;
    }

    /**
     * タイトル文字列を取得する。
     * 
     * @return タイトル文字列。
     */
    private String getTitleString() {

        final StringBuffer sb = new StringBuffer();

        // モード
        switch (mMode) {
        case VIEW_MODE_LAUNCHER:
            sb.append(mModeLabel[VIEW_MODE_LAUNCHER]);
            break;
        case VIEW_MODE_UNINSTALL:
            sb.append(mModeLabel[VIEW_MODE_UNINSTALL]);
            break;
        case VIEW_MODE_SYSTEM:
            sb.append(mModeLabel[VIEW_MODE_SYSTEM]);
            break;
        case VIEW_MODE_WITHOUT_SYSTEM:
            sb.append(mModeLabel[VIEW_MODE_WITHOUT_SYSTEM]);
            break;
        case VIEW_MODE_ALL:
            sb.append(mModeLabel[VIEW_MODE_ALL]);
            break;

        default:
            break;
        }

        // 件数
        sb.append(getString(R.string.appinfo_separate_colon));
        sb.append(getString(R.string.appinfo_title_count, mListView.getCount()));
        sb.append(getString(R.string.appinfo_separate_space));

        // ソート
        switch (mSort) {
        case SORT_ORDER_NAME:
            sb.append(getString(R.string.appinfo_title_sort,
                    ((NameComparator) mSortComparator.get(SORT_ORDER_NAME)).getName()));
            break;
        case SORT_ORDER_SOURCE_SIZE:
            sb.append(getString(R.string.appinfo_title_sort,
                    ((SourceSizeComparator) mSortComparator.get(SORT_ORDER_SOURCE_SIZE)).getName()));
            break;
        case SORT_ORDER_DATA_SIZE:
            sb.append(getString(R.string.appinfo_title_sort,
                    ((DataSizeComparator) mSortComparator.get(SORT_ORDER_DATA_SIZE)).getName()));
            break;

        default:
            break;
        }

        // 検索
        if (mSearch != null && !"".equals(mSearch)) {
            sb.append(getString(R.string.appinfo_separate_space));
            sb.append(getString(R.string.appinfo_menu_search));
            sb.append(getString(R.string.appinfo_separate_colon));
            sb.append(mSearch);
        }

        return sb.toString();
    }

    /**
     * アプリケーション情報リストビューアダプタ。
     */
    private class AppInfoAdapter extends ArrayAdapter<AppInfo> {

        private final List<AppInfo> mAiList;
        private final LayoutInflater mInflater;

        /**
         * コンストラクタ。
         * 
         * @param context コンテキスト。
         * @param textViewResourceId (未使用)。
         * @param objects 表示リスト。
         */
        public AppInfoAdapter(Context context, int textViewResourceId, List<AppInfo> objects) {
            super(context, textViewResourceId, objects);

            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mAiList = objects;
        }

        /**
         * コンストラクタ。
         * 
         * @param context コンテキスト。
         * @param objects 表示リスト。
         */
        public AppInfoAdapter(Context context, List<AppInfo> objects) {
            this(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // ビューを受け取る
            View v = convertView;

            if (v == null) {
                // 受け取ったビューが NULL なら新しくビューを生成
                v = mInflater.inflate(R.layout.appinfo_list_item, null);
            }

            // 表示すべきデータの取得
            final AppInfo ai = mAiList.get(position);

            if (ai != null) {
                // アイコン
                final ImageView icon = (ImageView) v.findViewById(R.id.icon);
                icon.setImageDrawable(ai.mIcon);

                // アプリ名
                final TextView tvAppName = (TextView) v.findViewById(R.id.app_name);
                tvAppName.setText(ai.mAppName);

                // Package Name
                final TextView tvPackageName = (TextView) v.findViewById(R.id.package_name);
                tvPackageName.setText(ai.mPackageName);

                // Source Dir
                final TextView tvSourceDir = (TextView) v.findViewById(R.id.source_dir);
                tvSourceDir.setText(ai.mSourceDir);

                // Source Size
                final TextView tvSourceSize = (TextView) v.findViewById(R.id.source_size);
                if (ai.mSourceSize == 0) {
                    tvSourceSize.setText(ZERO_SIZE);
                } else {
                    tvSourceSize.setText(String.format(FORMAT_SIZE, ai.mSourceSize / SIZE_UNIT));
                }

                // Data Dir
                final TextView tvDataDir = (TextView) v.findViewById(R.id.data_dir);
                tvDataDir.setText(ai.mDataDir);

                // Data Size
                final TextView tvDataSize = (TextView) v.findViewById(R.id.data_size);
                if (ai.mDataSize == 0) {
                    tvDataSize.setText(ZERO_SIZE);
                } else {
                    tvDataSize.setText(String.format(FORMAT_SIZE, ai.mDataSize / SIZE_UNIT));
                }

                // Description
                final TextView tvDescription = (TextView) v.findViewById(R.id.description);
                tvDescription.setText(ai.mDescription);

                // 詳細メニュー
                final ArrayList<String> items = new ArrayList<String>();
                items.add(getString(R.string.appinfo_menu_detail_uses_permission));
                items.add(getString(R.string.appinfo_menu_detail_search_market));
                items.add(getString(R.string.appinfo_menu_detail_share));

                switch (mMode) {
                case VIEW_MODE_LAUNCHER:
                    items.add(getString(R.string.appinfo_menu_detail_launch));
                    break;
                case VIEW_MODE_UNINSTALL:
                    items.add(getString(R.string.appinfo_menu_detail_uninstall));
                    break;

                default:
                    break;
                }

                final LinearLayout detail = (LinearLayout) v.findViewById(R.id.detail);
                detail.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog = new AlertDialog.Builder(Main.this)
                                .setTitle(ai.mAppName)
                                .setItems(items.toArray(new String[items.size()]),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                switch (which) {

                                                // 使用権限
                                                case 0:
                                                    showUsesPermissionDialog(ai);
                                                    break;

                                                // マーケット検索
                                                case 1:
                                                    final Intent itSearch = new Intent(
                                                            Intent.ACTION_VIEW, Uri
                                                                    .parse(ai.mMarketUrl));
                                                    startActivity(itSearch);
                                                    break;

                                                // 共有
                                                case 2:
                                                    final Intent itShare = new Intent(
                                                            Intent.ACTION_SEND);
                                                    itShare.setType("text/plain");
                                                    itShare.putExtra(Intent.EXTRA_SUBJECT,
                                                            ai.mAppName);
                                                    itShare.putExtra(Intent.EXTRA_TEXT,
                                                            ai.mMarketUrl);
                                                    try {
                                                        startActivity(itShare);
                                                    } catch (final ActivityNotFoundException e) {
                                                    }
                                                    break;

                                                default:
                                                    switch (mMode) {
                                                    case VIEW_MODE_LAUNCHER:
                                                        // 起動
                                                        final Intent itLaunch = new Intent(
                                                                Intent.ACTION_MAIN);
                                                        itLaunch.addCategory(Intent.CATEGORY_LAUNCHER);
                                                        itLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        itLaunch.setClassName(ai.mPackageName,
                                                                ai.mName);
                                                        startActivity(itLaunch);
                                                        break;

                                                    case VIEW_MODE_UNINSTALL:
                                                        // アンインストール
                                                        final Uri uriUninstall = Uri.fromParts(
                                                                "package", ai.mPackageName, null);
                                                        final Intent itUninstall = new Intent(
                                                                Intent.ACTION_DELETE, uriUninstall);
                                                        startActivity(itUninstall);
                                                        break;

                                                    default:
                                                        break;
                                                    }
                                                    break;
                                                }
                                            }
                                        }).show();
                    }
                });

                final LinearLayout llTitle = (LinearLayout) v.findViewById(R.id.title);
                llTitle.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (detail != null) {
                            if (detail.getVisibility() == View.VISIBLE) {
                                detail.setVisibility(View.GONE);
                                ai.mIsShowDetail = false;
                            } else {
                                detail.setVisibility(View.VISIBLE);
                                ai.mIsShowDetail = true;
                            }
                        }
                    }
                });

                if (ai.mIsShowDetail) {
                    detail.setVisibility(View.VISIBLE);
                } else {
                    detail.setVisibility(View.GONE);
                }
            }

            return v;
        }

        @Override
        public int getCount() {
            if (mAiList == null) {
                return 0;
            }
            return mAiList.size();
        }
    }

    /**
     * アプリケーション情報。
     */
    private class AppInfo {

        private static final String MARKET_URL = "http://market.android.com/search?q=";

        /** android:name。 */
        private String mName = "";
        /** アイコン。 */
        private final Drawable mIcon;
        /** アプリ名。 */
        private String mAppName = "";
        /** パッケージ名。 */
        private String mPackageName = "";
        /** APK パス。 */
        private String mSourceDir = "";
        /** APK サイズ。 */
        private long mSourceSize = 0;
        /** データディレクトリパス */
        private String mDataDir = "";
        /** データサイズ */
        private long mDataSize = 0;
        /** 説明。 */
        private String mDescription = "";
        /** マーケット検索 URL。 */
        private String mMarketUrl = "";

        /** 詳細表示するなら TRUE。 */
        private boolean mIsShowDetail = false;

        /**
         * コンストラクタ。
         * 
         * @param rInfo リゾルブインフォ。
         */
        public AppInfo(ResolveInfo rInfo) {

            ApplicationInfo info;
            if (rInfo.activityInfo == null) {
                info = rInfo.serviceInfo.applicationInfo;
            } else {
                info = rInfo.activityInfo.applicationInfo;
            }

            // android:name
            if (rInfo.activityInfo == null) {
                mName = rInfo.serviceInfo.name;
            } else {
                mName = rInfo.activityInfo.name;
            }

            // アイコン
            mIcon = rInfo.loadIcon(mPm);

            // アプリ名
            final CharSequence appName = rInfo.loadLabel(mPm);
            if (appName != null) {
                mAppName = appName.toString();
            }

            // パッケージ名
            mPackageName = info.packageName;

            // XXX: test
            if (rInfo.activityInfo == null) {
                if (mPackageName == null) {
                    if (rInfo.serviceInfo.packageName != null) {
                        Log.e(TAG, "null != " + rInfo.serviceInfo.packageName);
                    }
                } else if (!mPackageName.equals(rInfo.serviceInfo.packageName)) {
                    Log.e(TAG, mPackageName + " != " + rInfo.serviceInfo.packageName);
                }
            } else {
                if (mPackageName == null) {
                    if (rInfo.activityInfo.packageName != null) {
                        Log.e(TAG, "null != " + rInfo.activityInfo.packageName);
                    }
                } else if (!mPackageName.equals(rInfo.activityInfo.packageName)) {
                    Log.e(TAG, mPackageName + " != " + rInfo.activityInfo.packageName);
                }
            }

            // Source Dir
            mSourceDir = info.sourceDir;

            // Source Dir Size
            final File source = new File(mSourceDir);
            if (source.exists()) {
                mSourceSize = source.length();
            }

            // Data Dir
            mDataDir = info.dataDir;

            // Data Dir Size
            final File data = new File(mDataDir);
            if (data.exists()) {
                mDataSize = calcDirectorySize(data);
            }

            // Description
            final CharSequence description = info.loadDescription(mPm);
            if (description != null) {
                mDescription = description.toString();
            }

            // Market URL
            final StringBuffer sb = new StringBuffer();
            sb.append(MARKET_URL);
            sb.append(mPackageName);
            mMarketUrl = sb.toString();
        }

        /**
         * コンストラクタ。
         * 
         * @param info アプリケーションインフォ。
         */
        public AppInfo(ApplicationInfo info) {

            // android:name
            mName = info.name;

            // アイコン
            mIcon = info.loadIcon(mPm);

            // アプリ名
            final CharSequence appName = info.loadLabel(mPm);
            if (appName != null) {
                mAppName = appName.toString();
            }

            // パッケージ名
            mPackageName = info.packageName;

            // Source Dir
            mSourceDir = info.sourceDir;

            // Source Dir Size
            if (mSourceDir != null) {
                final File source = new File(mSourceDir);
                if (source.exists()) {
                    mSourceSize = source.length();
                }
            }

            // Data Dir
            mDataDir = info.dataDir;

            // Data Dir Size
            if (mDataDir != null) {
                final File data = new File(mDataDir);
                if (data.exists()) {
                    mDataSize = calcDirectorySize(data);
                }
            }

            // Description
            final CharSequence description = info.loadDescription(mPm);
            if (description != null) {
                mDescription = description.toString();
            }

            // Market URL
            final StringBuffer sb = new StringBuffer();
            sb.append(MARKET_URL);
            sb.append(mPackageName);
            mMarketUrl = sb.toString();

        }

        /**
         * 指定文字列と関係があれば TRUE を返す。
         * 
         * @param search 文字列。
         * @return 指定文字列と関係があれば TRUE。
         */
        public boolean isContain(String search) {
            return mAppName.contains(search) || mPackageName.contains(search)
                    || mSourceDir.contains(search) || mDataDir.contains(search)
                    || mDescription.contains(search);
        }

        /**
         * 指定ディレクトリ以下のサイズをバイト単位で返す。
         * 
         * @param f ディレクトリ。
         * @return サイズ（バイト）。
         */
        private long calcDirectorySize(File f) {
            long ret = 0;
            if (f != null && f.exists() && f.isDirectory()) {
                final File[] list = f.listFiles();
                if (list != null) {
                    for (final File o : list) {
                        if (o.isDirectory()) {
                            ret += calcDirectorySize(o);
                        } else {
                            ret += o.length();
                        }
                    }
                }
            }
            return ret;
        }

    }

    /**
     * 権限情報。
     */
    private static class PermissionInfo {

        private static final String PREFIX_PERMLAB = "permlab_";
        private static final String PREFIX_PERMDESC = "permdesc_";

        private final HashMap<String, String> mField = new HashMap<String, String>();
        private final HashMap<String, String> mFieldValue = new HashMap<String, String>();
        private final HashMap<String, String> mPermlab = new HashMap<String, String>();
        private final HashMap<String, String> mPermlabValue = new HashMap<String, String>();
        private final HashMap<String, String> mPermdesc = new HashMap<String, String>();
        private final HashMap<String, String> mPermdescValue = new HashMap<String, String>();

        /**
         * コンストラクタ。
         */
        public PermissionInfo(Context c) {

            for (final Field o : Manifest.permission.class.getFields()) {

                try {
                    final String key = o.get(new Manifest.permission()).toString();
                    mFieldValue.put(key, key);

                    final String field = o.getName();
                    mField.put(key, field);

                    final StringBuffer sb = new StringBuffer();

                    // TODO: fix
                    if ("android.permission.INTERNET".equals(key)) {
                        sb.append("createNetworkSockets");
                    } else if ("android.permission.WRITE_EXTERNAL_STORAGE".equals(key)) {
                        sb.append("sdcardWrite");
                    } else if ("com.android.email.permission.READ_ATTACHMENT".equals(key)) {
                        sb.append("readAttachment");
                    } else {
                        final String[] parts = field.split("_");
                        for (int i = 0; i < parts.length; i++) {
                            if (i == 0) {
                                sb.append(parts[i].toLowerCase());
                            } else {
                                sb.append(parts[i].charAt(0));
                                sb.append(parts[i].substring(1).toLowerCase());
                            }
                        }
                    }

                    final String permlab = PREFIX_PERMLAB + sb.toString();
                    mPermlab.put(key, permlab);
                    String permlabValue = "";
                    try {
                        permlabValue = c.getString(R.string.class.getField(permlab).getInt(
                                new R.string()));
                    } catch (final NullPointerException e) {
                    } catch (final IllegalArgumentException e) {
                    } catch (final SecurityException e) {
                    } catch (final IllegalAccessException e) {
                    } catch (final NoSuchFieldException e) {
                        Log.w(TAG, permlab + " not found: " + key);
                    }
                    mPermlabValue.put(key, permlabValue);

                    final String permdesc = PREFIX_PERMDESC + sb.toString();
                    mPermdesc.put(key, permdesc);
                    String permdescValue = "";
                    try {
                        permdescValue = c.getString(R.string.class.getField(permdesc).getInt(
                                new R.string()));
                    } catch (final NullPointerException e) {
                    } catch (final IllegalArgumentException e) {
                    } catch (final SecurityException e) {
                    } catch (final IllegalAccessException e) {
                    } catch (final NoSuchFieldException e) {
                        Log.w(TAG, permdesc + " not found: " + key);
                    }
                    mPermdescValue.put(key, permdescValue);

                } catch (final NullPointerException e) {
                } catch (final IllegalArgumentException e) {
                } catch (final IllegalAccessException e) {
                }
            }
        }
    }

    /**
     * アプリケーション情報取得タスク。
     */
    private class AppInfoTask extends AsyncTask<Void, Void, Void> {

        // アプリケーション情報リスト
        private ArrayList<AppInfo> mAiList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mIsScanning = true;
            setTitle(R.string.appinfo_title_scanning);

            final ProgressBar pb = new ProgressBar(getApplicationContext());
            pb.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));

            mRoot.removeAllViews();
            mRoot.addView(pb);
            setContentView(mRoot);

            mAiList = new ArrayList<AppInfo>();
        }

        @Override
        protected Void doInBackground(Void... params) {

            AppInfo info;
            switch (mMode) {

            // ランチャー
            case VIEW_MODE_LAUNCHER:
                final Intent it = new Intent(Intent.ACTION_MAIN);
                it.addCategory(Intent.CATEGORY_LAUNCHER);
                final List<ResolveInfo> list = mPm.queryIntentActivities(it, 0);
                for (final ResolveInfo o : list) {
                    info = new AppInfo(o);
                    if (mSearch != null && !"".equals(mSearch)) {
                        if (info.isContain(mSearch)) {
                            mAiList.add(info);
                        }
                    } else {
                        mAiList.add(info);
                    }
                }
                break;

            // UNINSTALL
            case VIEW_MODE_UNINSTALL:
                final List<ApplicationInfo> uninstallList = mPm
                        .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
                for (final ApplicationInfo o : uninstallList) {
                    if ((o.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                        info = new AppInfo(o);
                        if (mSearch != null && !"".equals(mSearch)) {
                            if (info.isContain(mSearch)) {
                                mAiList.add(info);
                            }
                        } else {
                            mAiList.add(info);
                        }
                    }
                }
                break;

            // プリインストール
            case VIEW_MODE_SYSTEM:
                final List<ApplicationInfo> systemList = mPm.getInstalledApplications(0);
                for (final ApplicationInfo o : systemList) {
                    if ((o.flags & ApplicationInfo.FLAG_SYSTEM) == 1
                            || (o.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
                        info = new AppInfo(o);
                        if (mSearch != null && !"".equals(mSearch)) {
                            if (info.isContain(mSearch)) {
                                mAiList.add(info);
                            }
                        } else {
                            mAiList.add(info);
                        }
                    }
                }
                break;

            // プリインストール以外
            case VIEW_MODE_WITHOUT_SYSTEM:
                final List<ApplicationInfo> aiList = mPm.getInstalledApplications(0);
                for (final ApplicationInfo o : aiList) {
                    if ((o.flags & ApplicationInfo.FLAG_SYSTEM) != 1
                            && (o.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 1) {
                        info = new AppInfo(o);
                        if (mSearch != null && !"".equals(mSearch)) {
                            if (info.isContain(mSearch)) {
                                mAiList.add(info);
                            }
                        } else {
                            mAiList.add(info);
                        }
                    }
                }
                break;

            // 全て
            case VIEW_MODE_ALL:
                final List<ApplicationInfo> allList = mPm.getInstalledApplications(0);
                for (final ApplicationInfo o : allList) {
                    info = new AppInfo(o);
                    if (mSearch != null && !"".equals(mSearch)) {
                        if (info.isContain(mSearch)) {
                            mAiList.add(info);
                        }
                    } else {
                        mAiList.add(info);
                    }
                }
                break;

            default:
                break;
            }

            // ソート
            switch (mSort) {
            // 名称
            case SORT_ORDER_NAME:
                Collections.sort(mAiList, mSortComparator.get(SORT_ORDER_NAME));
                break;

            // ソースサイズ
            case SORT_ORDER_SOURCE_SIZE:
                Collections.sort(mAiList, mSortComparator.get(SORT_ORDER_SOURCE_SIZE));
                break;

            // データサイズ
            case SORT_ORDER_DATA_SIZE:
                Collections.sort(mAiList, mSortComparator.get(SORT_ORDER_DATA_SIZE));
                break;

            default:
                break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (mListView == null) {
                mListView = new ListView(getApplicationContext());
                mListView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT));
            }
            mAdapter = new AppInfoAdapter(Main.this, mAiList);
            mListView.setAdapter(mAdapter);

            mRoot.removeAllViews();
            mRoot.addView(mListView);
            setContentView(mRoot);
            mListView.setSelectionFromTop(mTopPosition, mTopPositionY);

            setTitle(getTitleString());
            mIsScanning = false;
        }
    }

    /**
     * 名称ソート。
     */
    private class NameComparator implements Comparator<AppInfo> {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            // 0：arg1 = arg2
            // 1 : arg1 > arg2
            // -1: arg1 < arg2

            // アプリ名
            int ret = lhs.mAppName.compareToIgnoreCase(rhs.mAppName);
            if (ret == 0) {
                // パッケージ名
                ret = lhs.mPackageName.compareToIgnoreCase(rhs.mPackageName);
            }
            if (ret == 0) {
                // APK パス
                ret = lhs.mSourceDir.compareToIgnoreCase(rhs.mSourceDir);
            }

            return ret;
        }

        /**
         * ソート名を返す。
         * 
         * @return ソート名。
         */
        public String getName() {
            return getString(R.string.appinfo_menu_sort_name);
        }
    }

    /**
     * ソースサイズソート。
     */
    private class SourceSizeComparator implements Comparator<AppInfo> {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            // 0：arg1 = arg2
            // 1 : arg1 > arg2
            // -1: arg1 < arg2

            int ret = 0;
            if (lhs.mSourceSize > rhs.mSourceSize) {
                ret = -1;
            } else if (lhs.mSourceSize < rhs.mSourceSize) {
                ret = 1;
            }

            if (ret == 0) {
                // アプリ名
                ret = lhs.mAppName.compareToIgnoreCase(rhs.mAppName);
                if (ret == 0) {
                    // パッケージ名
                    ret = lhs.mPackageName.compareToIgnoreCase(rhs.mPackageName);
                }
                if (ret == 0) {
                    // APK パス
                    ret = lhs.mSourceDir.compareToIgnoreCase(rhs.mSourceDir);
                }
            }

            return ret;
        }

        /**
         * ソート名を返す。
         * 
         * @return ソート名。
         */
        public String getName() {
            return getString(R.string.appinfo_menu_sort_source_size);
        }
    }

    /**
     * データサイズソート。
     */
    private class DataSizeComparator implements Comparator<AppInfo> {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            // 0：arg1 = arg2
            // 1 : arg1 > arg2
            // -1: arg1 < arg2

            int ret = 0;
            if (lhs.mDataSize > rhs.mDataSize) {
                ret = -1;
            } else if (lhs.mDataSize < rhs.mDataSize) {
                ret = 1;
            }

            if (ret == 0) {
                // アプリ名
                ret = lhs.mAppName.compareToIgnoreCase(rhs.mAppName);
                if (ret == 0) {
                    // パッケージ名
                    ret = lhs.mPackageName.compareToIgnoreCase(rhs.mPackageName);
                }
                if (ret == 0) {
                    // APK パス
                    ret = lhs.mSourceDir.compareToIgnoreCase(rhs.mSourceDir);
                }
            }

            return ret;
        }

        /**
         * ソート名を返す。
         * 
         * @return ソート名。
         */
        public String getName() {
            return getString(R.string.appinfo_menu_sort_data_size);
        }
    }

}
