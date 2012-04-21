// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/01/27]
// Last updated: [2012/01/27]
package name.m.m.android.libs;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * コマンド実行関連ライブラリ。
 */
public class Exec {

    private static final String TAG = Exec.class.getSimpleName();

    /**
     * 標準入力、標準出力、標準エラー 取得キー。
     */
    public static enum KeyExecResult {
        /** リターンコード 取得キー。 */
        RETCODE,
        /** 標準入力 取得キー。 */
        STDIN,
        /** 標準出力 取得キー。 */
        STDOUT,
        /** 標準エラー 取得キー。 */
        STDERR,
    }

    /**
     * 指定コマンドの実行結果を返す。
     * 
     * @param command 実行コマンド。
     * @return 実行結果の標準出力と標準エラーの内容。
     * @see #KeyExecResult
     */
    public static HashMap<KeyExecResult, ArrayList<String>> getExecOutput(String[] command) {
        return getExecOutput(command, ProcessDestroyer.DELAY);
    }

    /**
     * 指定コマンドの実行結果を返す。
     * 
     * @param command 実行コマンド。
     * @param delay 強制終了させるまでの待機時間 (ms)。
     * @return 実行結果のリターンコードと標準出力、標準エラーの内容。
     * @see #KeyExecResult
     */
    public static HashMap<KeyExecResult, ArrayList<String>> getExecOutput(String[] command,
            long delay) {

        // ログ出力用 実行コマンド格納
        final StringBuffer sbPrintCmd = new StringBuffer();
        for (int i = 0; i < command.length; i++) {
            sbPrintCmd.append(command[i]);
            sbPrintCmd.append(" ");
        }
        sbPrintCmd.deleteCharAt(sbPrintCmd.length() - 1);

        Log.i(TAG, sbPrintCmd.toString() + ": start");

        // プロセスオブジェクト生成
        Process process = null;
        // ProcessBuilder 生成
        final ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            // コマンド実行
            process = processBuilder.start();
        } catch (final IOException e) {
            Log.w(TAG, e);
            return null;
        }

        // プロセス強制終了タスク
        final ProcessDestroyer processDestroyer = new ProcessDestroyer(process);
        // プロセス強制終了時間登録
        processDestroyer.schedule(delay);

        // 標準エラー取得スレッド
        final InputStreamThread errorStreamThread = new InputStreamThread(process.getErrorStream());
        errorStreamThread.start();

        // 標準出力取得スレッド
        final InputStreamThread inputStreamThread = new InputStreamThread(process.getInputStream());
        inputStreamThread.start();

        // コマンドリターンコード
        int retCode = -1;
        while (true) {
            try {
                // 結果を待つ
                retCode = process.waitFor();
                break;
            } catch (final InterruptedException e) {
                Log.w(TAG, e);
            }
        }

        // プロセス強制終了タスクキャンセル
        processDestroyer.cancel();

        // 返却値格納オブジェクト
        final HashMap<KeyExecResult, ArrayList<String>> hmResult = new HashMap<KeyExecResult, ArrayList<String>>();
        // リターンコード格納
        hmResult.put(KeyExecResult.RETCODE,
                (ArrayList<String>) Arrays.asList(String.valueOf(retCode)));

        try {
            // 標準エラー InputStream のスレッド終了待ち
            errorStreamThread.join();
            // 標準エラー格納
            hmResult.put(KeyExecResult.STDERR, errorStreamThread.getResultList());
        } catch (final InterruptedException e) {
            Log.w(TAG, e);
        }

        try {
            // 標準出力 InputStream のスレッド終了待ち
            inputStreamThread.join();
            // 標準出力格納
            hmResult.put(KeyExecResult.STDOUT, inputStreamThread.getResultList());
        } catch (final InterruptedException e) {
            Log.w(TAG, e);
        }

        Log.i(TAG, sbPrintCmd.toString() + ": end(" + String.valueOf(retCode) + ")");

        process = null;

        return hmResult;
    }
}

/**
 * 入力ストリーム読み込みスレッド。
 */
class InputStreamThread extends Thread {

    private static final String TAG = InputStreamThread.class.getSimpleName();

    private static final String ENCODING = "UTF-8";

    // 入力ストリーム格納バッファリーダ。
    private BufferedReader mBufferedReader;

    // 入力ストリーム読み込み結果格納リスト。
    private final ArrayList<String> mResultList = new ArrayList<String>();

    /**
     * コンストラクタ。
     * 
     * @param is 読み込む入力ストリーム。
     */
    public InputStreamThread(InputStream is) {
        this(is, ENCODING);
    }

    /**
     * コンストラクタ。
     * 
     * @param is 読み込む入力ストリーム。
     * @param charset キャラセット。
     */
    public InputStreamThread(InputStream is, String charset) {
        try {
            mBufferedReader = new BufferedReader(new InputStreamReader(is, charset));
        } catch (final UnsupportedEncodingException e) {
            Log.w(TAG, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                final String line = mBufferedReader.readLine();
                if (line == null) {
                    break;
                }
                mResultList.add(line);
            } catch (final IOException e) {
                Log.w(TAG, e);
            }
        }

        try {
            mBufferedReader.close();
        } catch (final IOException e) {
            Log.w(TAG, e);
        }
    }

    /**
     * 入力ストリーム読み込み結果を返す。
     * 
     * @return 入力ストリーム読み込み結果。
     */
    public ArrayList<String> getResultList() {
        return mResultList;
    }

}

/**
 * プロセス強制終了タスク。
 */
class ProcessDestroyer extends TimerTask {

    private static final String TAG = ProcessDestroyer.class.getSimpleName();

    /** 強制終了させるまでのデフォルト待機時間 (ms)。 */
    public static final long DELAY = 10 * 1000;

    // 強制終了させるプロセスオブジェクト。
    private Process mProcess;

    // タイマーオブジェクト。
    private Timer mTimer;

    /**
     * @param process 強制終了させるプロセスオブジェクト。
     */
    public ProcessDestroyer(Process process) {
        this(process, new Timer(TAG));
    }

    /**
     * @param process 強制終了させるプロセスオブジェクト。
     * @param timer タイマー。
     */
    public ProcessDestroyer(Process process, Timer timer) {
        mProcess = process;
        mTimer = timer;
    }

    /**
     * 強制終了時間を登録。
     */
    public void schedule() {
        schedule(DELAY);
    }

    /**
     * 強制終了時間を登録。
     * 
     * @param delay 強制終了させるまでの待機時間 (ms)。
     */
    public void schedule(long delay) {
        mTimer.schedule(this, delay);
    }

    @Override
    public boolean cancel() {
        mTimer.cancel();
        mProcess = null;
        mTimer = null;
        return super.cancel();
    }

    @Override
    public void run() {
        // プロセス強制終了
        mProcess.destroy();
    }
}
