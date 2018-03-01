package com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExtendedLifeCycleThreadService extends Service {
    String logName = "ASYNC-SVC-EXTN-THRD";
    WorkerHelper worker;
    ExecutorService executorService;

    public ExtendedLifeCycleThreadService() {
        LogHelper.LogThreadId(logName, "ExtendedLifeCycleThreadService Service - initiated.");
    }


    @Override
    public void onCreate() {
        worker = new WorkerHelper(this);
        executorService = Executors.newSingleThreadExecutor();
        worker.MonitorGpsInBackground();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                LogHelper.LogThreadId(logName, "ExtendedLifeCycleThreadService Service - onStartCommand : Started");
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
                LogHelper.LogThreadId(logName, "ExtendedLifeCycleThreadService Service - onStartCommand : Completed");
            }
        });
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
