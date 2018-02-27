package com.workaround.ajeesh.ajr_27002018_workaround_async_services;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.AsyncWorkerHelper;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.LogHelper;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.WorkerHelper;

public class ActivityDoWork extends AppCompatActivity {
    String logName = "ASYNC-ACT-DOWORK";

    TextView _defaultTextView;
    Thread _workerThread;
    AsyncWorkerHelper asyncWorkerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_work);

        LogHelper.LogThreadId(logName, "Activity Do Work is Created.");

        _defaultTextView = findViewById(R.id.defaultTextView);
        //StrictMode.enableDefaults();
        //doWork();
        doWorkAsynchronously();
    }

    private void doWork() {
        _workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                LogHelper.LogThreadId(logName, "Do Work initiated with new thread.");
                WorkerHelper worker = new WorkerHelper(ActivityDoWork.this);
                updateTextView("Starting");

                // Get most recently available location as a latitude/longitude
                Location location = worker.getLocation();
                updateTextView("Retrieved Location");

                // Convert lat/lng to a human-readable address
                String address = worker.reverseGeocode(location);
                updateTextView("Retrieved Address");

                // Write the location and address out to a file
                worker.save(location, address, "ResponsiveUx.out");
                LogHelper.LogThreadId(logName, "Worker File Saved Location : " + location);
                updateTextView("Done");
            }
        });
        _workerThread.start();
    }

    private void updateTextView(final String message) {
        _defaultTextView.post(new Runnable() {
            @Override
            public void run() {
                LogHelper.LogThreadId(logName, "UI interaction, i.e Text view updated with new thread.");
                _defaultTextView.setText(message);
            }
        });
    }


    private void doWorkAsynchronously() {
        asyncWorkerHelper = new AsyncWorkerHelper();
        asyncWorkerHelper.execute(_defaultTextView);
    }
}
