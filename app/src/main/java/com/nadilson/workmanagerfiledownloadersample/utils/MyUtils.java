package com.nadilson.workmanagerfiledownloadersample.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.nadilson.workmanagerfiledownloadersample.constants.MyConstants;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtils {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String getFormattedFileSize(Context context, long fileSize) {
        return android.text.format.Formatter.formatFileSize(context, fileSize);
    }

    public static String extractFilename(String url) {
        Pattern pattern = Pattern.compile("/([^/]+\\.\\w+)(?:\\?|$)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void openDownloadedFile(Context context, String filePath) {
        File downloadedFile = new File(filePath);
        Uri uri =  FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", downloadedFile);
        Intent fileIntent = new Intent(Intent.ACTION_VIEW);
        String mime = context.getContentResolver().getType(uri);
        fileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        fileIntent.setDataAndType(uri, mime);
        try {
            context.startActivity(fileIntent);
        } catch (Exception e) {
            Log.e(MyConstants.APP_TAG, "Falha ao abrir arquivo: ", e);
        }
    }
}
