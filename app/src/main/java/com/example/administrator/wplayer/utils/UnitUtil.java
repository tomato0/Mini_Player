package com.example.administrator.wplayer.utils;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class UnitUtil {

    public static final String APK_DELETE_ACTION = "com.gionee.antivirus.package_delete";
    public static final String APK_DELETE_PACKAGE_PATH_KEY = "pckPath";
    public static final String APK_DELETE_PACKAGE_NAME_KEY = "pckName";


    private static String TAG = "UnitUtil";

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

    public static void updateMediaVolume(Context context, 
            AsyncQueryHandler asyncQuery, String fileName) {
        Log.d("UnitUtil", "updateMediaVolume fileName:" + fileName);
        Uri data = Uri.parse("file://" + fileName);     
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }

    public static String sqliteEscape(String keyWord){
        //keyWord = keyWord.replace("/", "//");
        keyWord = keyWord.replace("'", "''");
        keyWord = keyWord.replace("[", "/[");
        keyWord = keyWord.replace("]", "/]");
        keyWord = keyWord.replace("%", "/%");
        keyWord = keyWord.replace("&","/&");
        keyWord = keyWord.replace("_", "/_");
        keyWord = keyWord.replace("(", "/(");
        keyWord = keyWord.replace(")", "/)");
        Log.d("UnitUtil", "sqliteEscape after keyWord:" + keyWord);
        return keyWord;
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {

        boolean returnValue = defValue;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(getUri("boolean"), null, key,
                    new String[] {"" + defValue}, null);
            if (cursor != null && cursor.moveToFirst()) {
                String value = cursor.getString(0);
                returnValue = "true".equals(value) ? true : false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        } finally {
            closeCursor(cursor);
        }
        return returnValue;
    }

    public static Uri getUri(String str) {
        return Uri.parse("content://" + "com.gionee.systemmanager.sp" + "/" + str);
    }

    public static void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

}
