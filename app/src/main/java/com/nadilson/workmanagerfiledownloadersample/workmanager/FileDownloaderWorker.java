package com.nadilson.workmanagerfiledownloadersample.workmanager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.nadilson.workmanagerfiledownloadersample.R;
import com.nadilson.workmanagerfiledownloadersample.constants.MyConstants;
import com.nadilson.workmanagerfiledownloadersample.utils.MyUtils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloaderWorker extends Worker {

    Context context;

    public static MutableLiveData<Integer> progressLiveData = new MutableLiveData<>();
    public static MutableLiveData<String> progressTextLiveData = new MutableLiveData<>();

    public FileDownloaderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String fileUrl = getInputData().getString(MyConstants.FILE_URL_DATA_KEY);
        String filePath = getInputData().getString(MyConstants.FILE_PATH_DATA_KEY);

        if (fileUrl == null) {
            Log.e(MyConstants.APP_TAG, "O Worker não recebeu o URL!");
            return Result.failure();
        }

        if (filePath == null) {
            Log.e(MyConstants.APP_TAG, "O Worker não recebeu o caminho do arquivo!");
            return Result.failure();
        }

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //   httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(MyConstants.APP_TAG, "Erro! Código " +
                        httpURLConnection.getResponseCode() + ": " + httpURLConnection.getResponseMessage());
                return Result.failure();
            }

            long fileSize = httpURLConnection.getContentLengthLong();


            InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            byte[] data = new byte[1024];
            int count = 0;
            long totalWritten = 0L;

            while ((count = inputStream.read(data)) > 0) {
                fileOutputStream.write(data, 0, count);
                totalWritten += count;

                int progress = (int) ((totalWritten * 100L) / fileSize);

                progressLiveData.postValue(progress);
                progressTextLiveData.postValue(String.format(context.getResources().getString(R.string.file_download_percentage),
                        MyUtils.getFormattedFileSize(context, (int) totalWritten),
                        MyUtils.getFormattedFileSize(context, fileSize),
                        String.valueOf(progress)));
            }

            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
            httpURLConnection.disconnect();

            Log.d(MyConstants.WORKER_TAG, "Download finalizado: " + filePath);

            return Result.success();

        } catch (Exception ex) {
            Log.e(MyConstants.WORKER_TAG, "Erro ao realizar o download do arquivo! Descrição do erro: ", ex);
            return Result.failure();
        }
    }
}
