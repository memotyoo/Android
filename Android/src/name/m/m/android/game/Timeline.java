// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/05/21]
// Last updated: [2012/05/21]
package name.m.m.android.game;

/**
 * タイムライン。
 */
public class Timeline {

    /** 長さ (ミリ秒)。 */
    private final long mLength;

    /** 開始時間 (ミリ秒)。 */
    private final long mStart;

    /** 強制終了フラグ。 */
    private boolean mIsForceFinish = false;

    /**
     * コンストラクタ。
     *
     * @param length 全体時間 (ミリ秒)。
     */
    public Timeline(long length) {
        mLength = length;
        mStart = System.currentTimeMillis();
    }

    /**
     * 経過時間を取得する。
     *
     * @return 経過時間 (ミリ秒)。
     */
    public long getTime() {
        return System.currentTimeMillis() - mStart;
    }

    /**
     * 強制終了する。
     */
    public void forceFinish() {
        mIsForceFinish = true;
    }

    /**
     * 終了したかを返す。
     *
     * @return 全体時間を経過していれば TRUE。
     */
    public boolean isFinished() {
        return mIsForceFinish || mLength > 0 ? getTime() > mLength : false;
    }
}
