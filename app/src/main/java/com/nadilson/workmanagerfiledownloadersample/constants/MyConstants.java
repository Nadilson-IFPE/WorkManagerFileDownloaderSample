package com.nadilson.workmanagerfiledownloadersample.constants;

import android.os.Environment;

public class MyConstants {
    public static final String APP_TAG = "WorkManager_File_Downloader";
    public static final String WORKER_TAG = "FileDownloaderWorker";
    public static final String NOTIFICATION_CHANNEL_ID = "my_worker_notification_channel";
    public static final long NOTIFICATION_ID = System.currentTimeMillis();
    public static final String FILE_URL_DATA_KEY = "FILE_URL_DATA";
    public static final String FILE_PATH_DATA_KEY = "FILE_PATH_DATA";
    public static final String PROGRESS = "PROGRESS";
    public static final String APP_DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
}
