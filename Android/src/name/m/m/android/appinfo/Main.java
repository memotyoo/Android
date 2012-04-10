// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/01/11]
// Last updated: [2012/01/11]
package name.m.m.android.appinfo;

import name.m.m.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * アプリケーション情報。
 */
public class Main extends Activity {

    // ビュールート。
    private LinearLayout mRoot;
    // パッケージマネージャ
    private PackageManager mPm;
    // アプリ情報取得タスク
    private AsyncTask<Void, Void, Void> mAppInfoTask;

    /*
     * (非 Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPm = getPackageManager();

        mRoot = new LinearLayout(getApplicationContext());
        mRoot.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.setGravity(Gravity.CENTER);

    }

    /*
     * (非 Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        mAppInfoTask = new AsyncTask<Void, Void, Void>() {

            private ArrayList<AppInfo> mAiList;

            /*
             * (非 Javadoc)
             * @see android.os.AsyncTask#onPreExecute()
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                final LayoutParams lpWW = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);

                final ProgressBar pb = new ProgressBar(getApplicationContext());
                pb.setLayoutParams(lpWW);

                mRoot.removeAllViews();
                mRoot.addView(pb);
                setContentView(mRoot);

                mAiList = new ArrayList<Main.AppInfo>();
            }

            /*
             * (非 Javadoc)
             * @see android.os.AsyncTask#doInBackground(Params[])
             */
            @Override
            protected Void doInBackground(Void... params) {

                final List<ApplicationInfo> aiList = mPm.getInstalledApplications(0);
                for (ApplicationInfo o : aiList) {
                    if ((o.flags & ApplicationInfo.FLAG_SYSTEM) == 1
                            || (o.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
                        mAiList.add(new AppInfo(o));
                    }
                }

                Collections.sort(mAiList, new Comparator<AppInfo>() {
                    public int compare(AppInfo lhs, AppInfo rhs) {
                        // 0：arg1 = arg2
                        // 1 : arg1 > arg2
                        // -1: arg1 < arg2

                        int ret = lhs.mAppName.compareToIgnoreCase(rhs.mAppName);
                        if (ret == 0) {
                            ret = lhs.mPackageName.compareToIgnoreCase(rhs.mPackageName);
                        }
                        if (ret == 0) {
                            ret = lhs.mSourceDir.compareToIgnoreCase(rhs.mSourceDir);
                        }

                        return ret;
                    }
                });
                return null;
            }

            /*
             * (非 Javadoc)
             * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
             */
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                final ListView lv = new ListView(getApplicationContext());
                lv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT));
                lv.setAdapter(new AppInfoAdapter(Main.this, 0, mAiList));

                mRoot.removeAllViews();
                mRoot.addView(lv);
                setContentView(mRoot);

                setTitle("Count: " + lv.getCount());
            }

        };
        mAppInfoTask.execute();

    }

    /*
     * (非 Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();

        mAppInfoTask.cancel(true);
    }

    private static final int MODE_ALL = 0;
    private static final int MODE_UNINSTALL = 1;
    private static final int MODE_LAUNCHER = 2;

    private static final int MENU_ID_MENU1 = (Menu.FIRST + 1);

    /*
     * (非 Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_MENU1, Menu.NONE, "表示");
        menu.add(Menu.NONE, MENU_ID_MENU1, Menu.NONE, "検索");
        menu.add(Menu.NONE, MENU_ID_MENU1, Menu.NONE, "出力");
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * (非 Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;

        switch (item.getItemId()) {
        case MENU_ID_MENU1:
            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            break;

        default:
            ret = super.onOptionsItemSelected(item);
            break;
        }

        return ret;
    }

    /**
     * アプリケーション情報リストビューアダプタ。
     */
    private class AppInfoAdapter extends ArrayAdapter<AppInfo> {

        private List<AppInfo> mAiList;
        private LayoutInflater mInflater;

        /**
         * コンストラクタ。
         * 
         * @param context コンテキスト。
         * @param textViewResourceId
         * @param objects
         */
        public AppInfoAdapter(Context context, int textViewResourceId, List<AppInfo> objects) {
            super(context, textViewResourceId, objects);

            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mAiList = objects;
        }

        /*
         * (非 Javadoc)
         * @see android.widget.ArrayAdapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // ビューを受け取る
            View v = convertView;

            if (v == null) {
                // 受け取ったビューが NULL なら新しくビューを生成
                v = mInflater.inflate(R.layout.item_appinfo, null);
            }

            final View root = v;

            // 表示すべきデータの取得
            final AppInfo ai = (AppInfo) mAiList.get(position);

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
                tvSourceSize.setText(ai.mSourceSize);

                // Data Dir
                final TextView tvDataDir = (TextView) v.findViewById(R.id.data_dir);
                tvDataDir.setText(ai.mDataDir);

                // Data Size
                final TextView tvDataSize = (TextView) v.findViewById(R.id.data_size);
                tvDataSize.setText(ai.mDataSize);

                // Description
                final TextView tvDescription = (TextView) v.findViewById(R.id.description);
                tvDescription.setText(ai.mDescription);

                // Market URL
                final TextView tvMarketUrl = (TextView) v.findViewById(R.id.market_url);
                tvMarketUrl.setText(ai.mMarketUrl);

                final TableLayout detail = (TableLayout) root.findViewById(R.id.detail);
                final LinearLayout llTitle = (LinearLayout) v.findViewById(R.id.title);
                llTitle.setOnClickListener(new OnClickListener() {
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

        /*
         * (非 Javadoc)
         * @see android.widget.ArrayAdapter#getCount()
         */
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

        private static final String FORMAT_SIZE = "%.3f KB";
        private static final String ZERO_SIZE = "0 KB";
        private static final String MARKET_URL = "http://market.android.com/search?q=";

        private Drawable mIcon;
        private String mAppName = "";
        private String mPackageName = "";
        private String mSourceDir = "";
        private String mSourceSize = "";
        private String mDataDir = "";
        private String mDataSize = "";
        private String mDescription = "";
        private String mMarketUrl = "";
        private int mFlags = 0;

        private boolean mIsShowDetail = false;

        /**
         * コンストラクタ。
         * 
         * @param info アプリケーションインフォ。
         */
        public AppInfo(ApplicationInfo info) {

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
            final File source = new File(mSourceDir);
            if (source.exists()) {
                final double size = source.length() / 1024.0;
                if (size == 0) {
                    mSourceSize = ZERO_SIZE;
                } else {
                    mSourceSize = String.format(FORMAT_SIZE, size);
                }
            }

            // Data Dir
            mDataDir = info.dataDir;

            // Data Dir Size
            final File data = new File(mDataDir);
            if (data.exists()) {
                final double size = calcDirectorySize(data) / 1024.0;
                if (size == 0) {
                    mDataSize = ZERO_SIZE;
                } else {
                    mDataSize = String.format(FORMAT_SIZE, size);
                }
            }

            // Description
            final CharSequence description = info.loadDescription(mPm);
            if (description != null) {
                mDescription = description.toString();
            }

            // Market Dir
            final StringBuffer sb = new StringBuffer();
            sb.append(MARKET_URL);
            sb.append(mPackageName);
            mMarketUrl = sb.toString();

            // Flags
            mFlags = info.flags;

        }

        private long calcDirectorySize(File f) {
            long ret = 0;
            if (f != null && f.exists() && f.isDirectory()) {
                final File[] list = f.listFiles();
                if (list != null) {
                    for (File o : list) {
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

}
