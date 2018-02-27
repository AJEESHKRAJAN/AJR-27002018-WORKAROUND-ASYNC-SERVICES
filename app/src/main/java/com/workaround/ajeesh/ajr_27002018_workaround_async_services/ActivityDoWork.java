package com.workaround.ajeesh.ajr_27002018_workaround_async_services;

import android.location.Location;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.LogHelper;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.WorkerHelper;

public class ActivityDoWork extends AppCompatActivity {

    TextView _defaultTextView;
    String logName = "ASYNC-ACT-DOWORK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_work);

        LogHelper.LogThreadId(logName, "Activity Do Work is Created.");

        _defaultTextView = findViewById(R.id.defaultTextView);
        StrictMode.enableDefaults();
        doWork();
    }

    private void doWork() {
        WorkerHelper worker = new WorkerHelper(this);
        _defaultTextView.setText("Starting");

        // Get most recently available location as a latitude/longitude
        Location location = worker.getLocation();
        _defaultTextView.setText("Retrieved Location");

        // Convert lat/lng to a human-readable address
        String address = worker.reverseGeocode(location);
        _defaultTextView.setText("Retrieved Address");

        // Write the location and address out to a file
        worker.save(location, address, "ResponsiveUx.out");
        LogHelper.LogThreadId(logName, "Worker File Saved Location : " + location);
        _defaultTextView.setText("Done");
    }
}
