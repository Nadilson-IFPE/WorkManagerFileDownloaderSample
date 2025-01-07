package com.nadilson.workmanagerfiledownloadersample.utils;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtils {

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
}
