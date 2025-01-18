package com.nadilson.workmanagerfiledownloadersample.constants;

import android.os.Environment;

public class MyConstants {
    public static final String APP_TAG = "WorkManager_Downloader";
    public static final String WORKER_TAG = "FileDownloaderWorker";
    public static final String FILE_URL_DATA_KEY = "FILE_URL_DATA";
    public static final String FILE_PATH_DATA_KEY = "FILE_PATH_DATA";
    public static final String APP_DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
}
