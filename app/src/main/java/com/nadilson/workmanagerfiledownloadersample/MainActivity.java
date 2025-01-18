package com.nadilson.workmanagerfiledownloadersample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.Observer;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.nadilson.workmanagerfiledownloadersample.constants.MyConstants;
import com.nadilson.workmanagerfiledownloadersample.databinding.ActivityMainBinding;
import com.nadilson.workmanagerfiledownloadersample.utils.MyUtils;
import com.nadilson.workmanagerfiledownloadersample.workmanager.FileDownloaderWorker;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private ProgressBar progressBar;
    private TextView progressText;
    private TextInputEditText textInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyUtils.verifyStoragePermissions(MainActivity.this);

        setSupportActionBar(binding.toolbar);

        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressBarText);
        progressText.setText(String.format(getString(R.string.file_download_percentage),
                MyUtils.getFormattedFileSize(this, 0),
                MyUtils.getFormattedFileSize(this, 0),
                "0"));
        Button button = findViewById(R.id.download_button);
        textInputEditText = findViewById(R.id.textInputEditText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(textInputEditText.getText())) {
                    Data inputData = new Data.Builder()
                            .putString(MyConstants.FILE_URL_DATA_KEY, String.valueOf(textInputEditText.getText()))
                            .putString(MyConstants.FILE_PATH_DATA_KEY, MyConstants.APP_DOWNLOAD_PATH +
                                    MyUtils.extractFilename(String.valueOf(textInputEditText.getText())))
                            .build();

                    OneTimeWorkRequest fileDownloadOneTimeWorkRequest = new OneTimeWorkRequest.Builder(FileDownloaderWorker.class)
                            .setInputData(inputData)
                            .setConstraints(new Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .setRequiresStorageNotLow(true)
                                    .build())
                            .build();

                    WorkManager.getInstance(MainActivity.this).enqueue(fileDownloadOneTimeWorkRequest);

                    WorkManager.getInstance(MainActivity.this)
                            .getWorkInfoByIdLiveData(fileDownloadOneTimeWorkRequest.getId())
                            .observe(MainActivity.this, new Observer<WorkInfo>() {
                                @Override
                                public void onChanged(WorkInfo workInfo) {
                                    if (workInfo != null) {
                                        try {
                                            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                                Snackbar.make(view, "Download finalizado com sucesso!", Snackbar.LENGTH_LONG)
                                                        .setAnchorView(R.id.progressBarText)
                                                        .setAction("Action", null).show();
                                            } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                                                Snackbar.make(view, "Falha ao realizar o download do arquivo!", Snackbar.LENGTH_LONG)
                                                        .setAnchorView(R.id.progressBarText)
                                                        .setAction("Action", null).show();
                                            }
                                        } catch (Exception exception) {
                                            Snackbar.make(view, "Erro: " + exception.getLocalizedMessage(), Snackbar.LENGTH_SHORT)
                                                    .setAnchorView(R.id.progressBarText)
                                                    .setAction("Action", null).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });

        FileDownloaderWorker.progressLiveData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer progress) {
                progressBar.setProgress(progress);
            }
        });

        FileDownloaderWorker.progressTextLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String progressInfo) {
                progressText.setText(progressInfo);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}