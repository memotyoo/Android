// -*- Mode: Java; Encoding: utf8n -*-
// ①ⅱ㈱℡髙﨑塚德彅
// Created: [2012/04/22]
// Last updated: [2012/04/22]
package name.m.m.android.libs;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 画像キャッシュクラス。
 */
public class ImageCacher {

    @SuppressWarnings("unused")
    private static final String TAG = ImageCacher.class.getSimpleName();

    private static final HashMap<String, SoftReference<Bitmap>> mCache = new HashMap<String, SoftReference<Bitmap>>();

    /**
     * 画像を取得する。
     * 
     * @param path パス。
     * @return 画像。
     */
    public static Bitmap getBitmap(String path) {
        final SoftReference<Bitmap> image = mCache.get(path);
        return image == null ? null : image.get();
    }

    /**
     * 画像をキャッシュする。
     * 
     * @param path 画像パス。
     * @param bmp 画像。
     */
    public static void putBitmap(String path, Bitmap bmp) {
        mCache.put(path, new SoftReference<Bitmap>(bmp));
    }

}
