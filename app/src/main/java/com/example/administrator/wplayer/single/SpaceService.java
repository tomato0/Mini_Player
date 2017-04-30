/**
 * Copyright Statement:
 *
 * Company: Gionee Communication Equipment Limited
 *
 * Author: Houjie
 *
 * Date: 2016-11-27
 */
package com.example.administrator.wplayer.single;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import android.util.Log;

import com.example.administrator.wplayer.interfaces.StorageStateChangeCallback;

import java.io.File;

public class SpaceService {
    private static final String TAG = "SpaceService";
    private static SpaceService sInstance;

    private Context mContext;
    private boolean mFirstTime = true;
     private StorageService mStorageService;

    public static synchronized SpaceService getInstance() {
        if (null == sInstance) {
            sInstance = new SpaceService();
        }
        return sInstance;
    }

    private SpaceService() {
    }

    public synchronized void init(Context context) {
        mContext = context.getApplicationContext();
        mStorageService = StorageService.getInstance();
        mStorageService.init(mContext);
    }

    public synchronized boolean externalMemoryAvailable() {
        return mStorageService.checkSDCardMount(mStorageService.getStorage2Path());
    }

    public synchronized long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public synchronized long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public synchronized long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable() && mStorageService != null) {
            Log.d(TAG, "getAvailableExternalMemorySize path:" + mStorageService.getStorage2Path());
            StatFs stat = new StatFs(mStorageService.getStorage2Path());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            Log.d(TAG, "getTotalExternalMemorySize blockSize:" + blockSize + ", availableBlocks:" + availableBlocks);
            return availableBlocks * blockSize;
        }
        return 0L;
    }

    public synchronized long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            Log.d(TAG, "getTotalExternalMemorySize path:" + mStorageService.getStorage2Path());
            StatFs stat = new StatFs(mStorageService.getStorage2Path());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            Log.d(TAG, "getTotalExternalMemorySize blockSize:" + blockSize + ", totalBlocks:" + totalBlocks);
            return totalBlocks * blockSize;
        }
        return 0L;
    }

    public void setStorageStateChangeCallBack(String key,
                                              StorageStateChangeCallback callback) {
        mStorageService.setStorageStateChangeCallBack(key, callback);
    }

    public void unsetStorageStateChangeCallBack(String key) {
        mStorageService.unsetStorageStateChangeCallBack(key);
    }
}