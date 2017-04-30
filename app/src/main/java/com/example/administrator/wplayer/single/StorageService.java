/**
 *
 * Author: wangshaoqiang
 *
 * Date: 2016-11-27
 */
package com.example.administrator.wplayer.single;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.storage.StorageManager;

import android.util.Log;

import com.example.administrator.wplayer.interfaces.StorageStateChangeCallback;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class StorageService {
    private static final String TAG = "StorageService";
    private static StorageService sInstance;

    private Context mContext;
    private StorageManager mStorageManager;
    private StorageReceiver mStorageReceiver = new StorageReceiver();

    private String mStoragePath;
    private String mStorage2Path;

    private Map<String, WeakReference<StorageStateChangeCallback>> mStorageStateChangeCallbacks =
            new HashMap<String, WeakReference<StorageStateChangeCallback>>();

    public static synchronized StorageService getInstance() {
        if (null == sInstance) {
            sInstance = new StorageService();
        }
        return sInstance;
    }

    private StorageService() {
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        initStoragePaths(mStorageManager);
        registerStorageInfoRefreshReceiver();
    }

    private void initStoragePaths(StorageManager storageManager) {
        Log.d(TAG,"initStoragePaths");
        List<File>  sdCardMountPointPathList = null;
        mStoragePath = null;
        mStorage2Path = null;
        sdCardMountPointPathList = listAllStorage(mContext);
        updateStoragePaths(sdCardMountPointPathList);
    }

//    private File[] updateMountedPointList() {
//        StorageVolume[] storageVolume = StorageManager.getVolumeList();
//        Log.d(TAG, "updateMountedPointList storageVolume.length:" + storageVolume.length);
//        File[] systemSDCardMountPointPathList = new File[storageVolume.length];
//        for (int i = 0; i < storageVolume.length; ++i) {
//            systemSDCardMountPointPathList[i] = new File(storageVolume[i].getPath());
//        }
//
//        int mountCount = 0;
//        for (int i = 0; i < systemSDCardMountPointPathList.length; i++) {
//            if (checkSDCardMount(systemSDCardMountPointPathList[i].getAbsolutePath())) {
//                mountCount++;
//            }
//        }
//
//        Log.d(TAG, "updateMountedPointList mountCount:" + mountCount);
//
//        File[] sdCardMountPointPathList = new File[mountCount];
//        if (twoSDCardSwap() && mountCount >= 2) {
//            for (int i = mountCount - 1, j = 0; i >= 0; i--) {
//                if (checkSDCardMount(systemSDCardMountPointPathList[i].getAbsolutePath())) {
//                    sdCardMountPointPathList[j++] = systemSDCardMountPointPathList[i];
//                }
//            }
//        } else {
//            for (int i = 0, j = 0; i < systemSDCardMountPointPathList.length; i++) {
//                if (checkSDCardMount(systemSDCardMountPointPathList[i].getAbsolutePath())) {
//                    sdCardMountPointPathList[j++] = systemSDCardMountPointPathList[i];
//                }
//            }
//        }
//        return sdCardMountPointPathList;
//    }

    public List<File> listAllStorage(Context context) {
        List<File> storages = new ArrayList<File>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);

            if (invokes != null) {
                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    storages.add(new File(path));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return storages;
    }

    public boolean checkSDCardMount(String mountPoint) {
        if (mountPoint == null) {
            return false;
        }
        String invoke = null;
        try {
            Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
            invoke = (String) getVolumeState.invoke(sInstance, mountPoint);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return Environment.MEDIA_MOUNTED.equals(invoke);
    }

//    @SuppressWarnings("rawtypes")
//    private boolean twoSDCardSwap() {
//        String sSDSwapEnabled = SystemProperties.get("ro.gn.gn2sdcardswap", "no");
//        return sSDSwapEnabled.equals("yes");
//    }

    private void updateStoragePaths(List<File> sdCardMountPointPathList) {
        if (sdCardMountPointPathList.size() >= 2) {
            mStoragePath = sdCardMountPointPathList.get(0).getAbsolutePath();
            mStorage2Path = sdCardMountPointPathList.get(1).getAbsolutePath();
        } else if (sdCardMountPointPathList.size() == 1 && sdCardMountPointPathList.get(0) != null) {
            mStoragePath = sdCardMountPointPathList.get(0).getAbsolutePath();
        }
    }

    private void registerStorageInfoRefreshReceiver() {
        Log.d(TAG, "registerStorageInfoRefreshReceiver");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addDataScheme("file");
        mContext.registerReceiver(mStorageReceiver, intentFilter);
    }

    private class StorageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "StorageReceiver onReceive action:" + action);
            if (!action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)
                    && !action.equals(Intent.ACTION_MEDIA_MOUNTED)
                    && !action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                Log.d(TAG,"StorageReceiver return");
                return;
            }
            Log.d(TAG,"StorageReceiver no return");
            initStoragePaths(mStorageManager);
            notifyStorageStateChange();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mContext.unregisterReceiver(mStorageReceiver);
    }

    public String getStoragePath() {
        return mStoragePath;
    }

    public String getStorage2Path() {
        return mStorage2Path;
    }

    public void setStorageStateChangeCallBack(String key,
                                              StorageStateChangeCallback callback) {
        WeakReference<StorageStateChangeCallback> cb =
                new WeakReference<StorageStateChangeCallback>(callback);
        Log.d(TAG,"key="+key+"==callback=="+cb);
        mStorageStateChangeCallbacks.put(key, cb);
    }

    public void unsetStorageStateChangeCallBack(String key) {
        mStorageStateChangeCallbacks.remove(key);
    }

    private void notifyStorageStateChange() {
        if (null == mStorageStateChangeCallbacks) {
            Log.d(TAG,"null mStorageStateChangeCallbacks");
            return;
        }
        Log.d(TAG,"notifyStorageStateChange()");
        for (WeakReference<StorageStateChangeCallback> cb : mStorageStateChangeCallbacks.values()) {
            Log.d(TAG,"---->weak callback----->"+cb);
            StorageStateChangeCallback callback = cb.get();
            Log.d(TAG,"---->callback----->"+callback);
            if (callback != null) {
                Log.d(TAG,"onStorageStateChange()");
                callback.onStorageStateChange();
            }
        }
    }
}
