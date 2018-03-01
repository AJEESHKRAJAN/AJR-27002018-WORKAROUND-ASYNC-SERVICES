package com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

public class ExtendedLifeCycleService extends Service {
    String logName = "ASYNC-SVC-EXTN";
    WorkerHelper worker;

    public ExtendedLifeCycleService() {
        LogHelper.LogThreadId(logName, "ExtendedLifeCycleService Service - initiated.");
    }


    @Override
    public void onCreate() {
        worker = new WorkerHelper(this);
        worker.MonitorGpsInBackground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogHelper.LogThreadId(logName, "ExtendedLifeCycleService Service - onStartCommand : Started");
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
        LogHelper.LogThreadId(logName, "ExtendedLifeCycleService Service - onStartCommand : Completed");

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
       if(worker!= null){
           worker.stopGpsMonitoring();
       }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
