package com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;

import com.workaround.ajeesh.ajr_27002018_workaround_async_services.ActivityDoWork;


public class SimpleService extends IntentService {
    String logName = "ASYNC-SVC-SMPLE";

    public SimpleService() {
        super("ASYNC-SVC-SMPLE");
        LogHelper.LogThreadId(logName, "Simple Service is initiated.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            LogHelper.LogThreadId(logName, "Simple Service - onHandleIntent : Started");
            WorkerHelper worker = new WorkerHelper(this);

            // Get most recently available location as a latitude/longitude
            Location location = worker.getLocation();

            // Convert lat/lng to a human-readable address
            String address = worker.reverseGeocode(location);

            // Write the location and address out to a file
            String fileName = intent.getStringExtra("fileName");
            if (fileName == null) {
                fileName = "ResponsiveUx.out";
            }
            worker.save(location, address, fileName);
            LogHelper.LogThreadId(logName, "Simple Service - onHandleIntent : Completed");
        }
    }
}
