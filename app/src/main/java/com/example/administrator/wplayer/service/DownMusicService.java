package com.example.administrator.wplayer.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.administrator.wplayer.models.MediaItem;

import java.io.File;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2017/4/30 0030.
 * com.example.administrator.wplayer.service
 * 功能、作用：
 */

public class DownMusicService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaItem music = (MediaItem) intent.getParcelableExtra("music");

        down(music);
        return super.onStartCommand(intent, flags, startId);
    }

    private void down(final MediaItem music) {
        new Thread() {
            @Override
            public void run() {
                String downloadUrl = music.getData();

                // 创建下载请求
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        /*
        * 设置在通知栏是否显示下载通知(下载进度), 有 3 个值可选:
        *    VISIBILITY_VISIBLE:                   下载过程中可见, 下载完后自动消失 (默认)
        *    VISIBILITY_VISIBLE_NOTIFY_COMPLETED:  下载过程中和下载完成后均可见
        *    VISIBILITY_HIDDEN:                    始终不显示通知
        */
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 设置通知的标题和描述
                request.setTitle("下载音乐");
                request.setDescription(music.getName());

                    /*
             * 设置允许使用的网络类型, 可选值:
             *     NETWORK_MOBILE:      移动网络
             *     NETWORK_WIFI:        WIFI网络
             *     NETWORK_BLUETOOTH:   蓝牙网络
             * 默认为所有网络都允许
             */
            // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

            // 添加请求头
            // request.addRequestHeader("User-Agent", "Chrome Mozilla/5.0");

            // 设置下载文件的保存位置
                File saveFile = new File(Environment.getExternalStorageDirectory(), music.getName() + ".mp3");
                request.setDestinationUri(Uri.fromFile(saveFile));

            /*
                     * 2. 获取下载管理器服务的实例, 添加下载任务
             */
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                    // 将下载请求加入下载队列, 返回一个下载ID
                long downloadId = manager.enqueue(request);

                // 如果中途想取消下载, 可以调用remove方法, 根据返回的下载ID取消下载, 取消下载后下载保存的文件将被删除
                // manager.remove(downloadId);
            }
        }.start();
    }
}
