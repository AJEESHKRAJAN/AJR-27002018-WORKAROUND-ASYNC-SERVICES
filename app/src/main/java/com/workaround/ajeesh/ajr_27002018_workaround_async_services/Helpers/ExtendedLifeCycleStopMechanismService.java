package com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExtendedLifeCycleStopMechanismService extends Service {
    String logName = "ASYNC-SVC-EXTN-THRD-STP-MCHNSM";
    WorkerHelper worker;
    ExecutorService executorService;
    ScheduledExecutorService scheduledExecutorService;

    String statusMessage;

    public ExtendedLifeCycleStopMechanismService() {
        LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismService Service - initiated.");
    }


    @Override
    public void onCreate() {
        worker = new WorkerHelper(this);
        executorService = Executors.newSingleThreadExecutor();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        worker.MonitorGpsInBackground();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismService Service - onStartCommand - initiated.");
        LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismService Service - onStartCommand - StartId - " + startId);
        Runnable triggerWorkerRunThread = new ServiceRunnable(this, startId);
        executorService.execute(triggerWorkerRunThread);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (worker != null) {
            LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismService Service - Destroyed.");
            worker.stopGpsMonitoring();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ServiceRunnable implements Runnable {
        ExtendedLifeCycleStopMechanismService extendedLifeCycleStopMechanismService;
        int _startId;
        Handler handler;

        public ServiceRunnable(ExtendedLifeCycleStopMechanismService theService, int startId) {
            extendedLifeCycleStopMechanismService = theService;
            _startId = startId;
        }

        @Override
        public void run() {
            setUpHandler();
            LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismService Service - ServiceRunnable Class : Started");
            // Get most recently available location as a latitude/longitude
            Location location = worker.getLocation();
            updateStatus("Starting");

            // Convert lat/lng to a human-readable address
            String address = worker.reverseGeocode(location);
            updateStatus("Reverse Geocode");

            // Write the location and address out to a file
            String fileName = "ResponsiveUx.out";
            worker.save(location, address, fileName);
            updateStatus("Done");
            LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismService Service - ServiceRunnable Class : Completed");

            // Stop the service if there are no more tasks to process after this one
            // If we wanted to keep the service for some period of time after the last request,
            // // we could use ScheduledThreadPoolExecutor to call stopSelf after some delay
            DelayedStopService delayedStopService = new DelayedStopService(extendedLifeCycleStopMechanismService, _startId);
            extendedLifeCycleStopMechanismService.scheduledExecutorService.schedule(delayedStopService, 10, TimeUnit.SECONDS);
        }

        private void setUpHandler() {
            Looper looper = extendedLifeCycleStopMechanismService.getMainLooper();
            LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismService Service - Looper : " + looper);
            handler = new Handler(looper);
        }

        private void updateStatus(String message) {
            statusMessage = message;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismService Service - handler : " + handler);
                    Toast.makeText(extendedLifeCycleStopMechanismService, statusMessage, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    class DelayedStopService implements Runnable {
        ExtendedLifeCycleStopMechanismService extendedLifeCycleStopMechanismService;
        int _startId;

        public DelayedStopService(ExtendedLifeCycleStopMechanismService theService, int startId) {
            extendedLifeCycleStopMechanismService = theService;
            _startId = startId;
        }

        @Override
        public void run() {
            boolean stopResult = extendedLifeCycleStopMechanismService.stopSelfResult(_startId);
            LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismService Service - DelayedStopService - StopResult : " + stopResult + " For startId " + _startId);
        }
    }
}
