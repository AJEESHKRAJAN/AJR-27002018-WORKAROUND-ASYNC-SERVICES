package com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.workaround.ajeesh.ajr_27002018_workaround_async_services.ActivityDoWork;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExtendedLifeCycleStopMechanismForeGroundService extends Service {
    String logName = "ASYNC-SVC-EXTN-THRD-STP-MCHNSM-FORE-GRD";
    WorkerHelper worker;
    ExecutorService executorService;
    ScheduledExecutorService scheduledExecutorService;

    String statusMessage;

    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    private final int notificationId = 234;

    public ExtendedLifeCycleStopMechanismForeGroundService() {
        LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismForeGroundService Service - initiated.");
    }

    @Override
    public void onCreate() {
        worker = new WorkerHelper(this);
        executorService = Executors.newSingleThreadExecutor();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        worker.MonitorGpsInBackground();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setUpForegroundService();
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismForeGroundService Service - onStartCommand - initiated.");
        LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismForeGroundService Service - onStartCommand - StartId - " + startId);
        Runnable triggerWorkerRunThread = new ExtendedLifeCycleStopMechanismForeGroundService.ServiceRunnable(this, startId);
        executorService.execute(triggerWorkerRunThread);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (worker != null) {
            LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismForeGroundService Service - Destroyed.");
            worker.stopGpsMonitoring();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ServiceRunnable implements Runnable {
        ExtendedLifeCycleStopMechanismForeGroundService extendedLifeCycleStopMechanismForeGroundService;
        int _startId;
        Handler handler;

        public ServiceRunnable(ExtendedLifeCycleStopMechanismForeGroundService theService, int startId) {
            extendedLifeCycleStopMechanismForeGroundService = theService;
            _startId = startId;
        }

        @Override
        public void run() {
            setUpHandler();
            extendedLifeCycleStopMechanismForeGroundService.setStatusIcon(R.drawable.ic_stat_forwardname);
            LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismForeGroundService Service - ServiceRunnable Class : Started");
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
            LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismForeGroundService Service - ServiceRunnable Class : Completed");

            extendedLifeCycleStopMechanismForeGroundService.setStatusIcon(R.drawable.ic_stat_name);

            // Stop the service if there are no more tasks to process after this one
            // If we wanted to keep the service for some period of time after the last request,
            // // we could use ScheduledThreadPoolExecutor to call stopSelf after some delay
            ExtendedLifeCycleStopMechanismForeGroundService.DelayedStopService delayedStopService = new ExtendedLifeCycleStopMechanismForeGroundService.DelayedStopService(extendedLifeCycleStopMechanismForeGroundService, _startId);
            extendedLifeCycleStopMechanismForeGroundService.scheduledExecutorService.schedule(delayedStopService, 10, TimeUnit.SECONDS);
        }

        private void setUpHandler() {
            Looper looper = extendedLifeCycleStopMechanismForeGroundService.getMainLooper();
            LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismForeGroundService Service - Looper : " + looper);
            handler = new Handler(looper);
        }

        private void updateStatus(String message) {
            statusMessage = message;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismForeGroundService Service - handler : " + handler);
                    Toast.makeText(extendedLifeCycleStopMechanismForeGroundService, statusMessage, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    class DelayedStopService implements Runnable {
        ExtendedLifeCycleStopMechanismForeGroundService extendedLifeCycleStopMechanismForeGroundService;
        int _startId;

        public DelayedStopService(ExtendedLifeCycleStopMechanismForeGroundService theService, int startId) {
            extendedLifeCycleStopMechanismForeGroundService = theService;
            _startId = startId;
        }

        @Override
        public void run() {
            boolean stopResult = extendedLifeCycleStopMechanismForeGroundService.stopSelfResult(_startId);
            LogHelper.LogThreadId(logName, "ExtendedLifeCycleStopMechanismForeGroundService Service - DelayedStopService - StopResult : " + stopResult + " For startId " + _startId);
        }
    }

    private void setUpForegroundService() {
        //Basic notification set up
        int notificationIcon = R.drawable.ic_stat_name;

        // Describe what to do if the user clicks the notification in the status bar
        String notificationTitleText = "Master Extended LC SM FR GRD service";
        String notificationBodyText = "Does non-UI processing";


        createNotification(notificationIcon, notificationTitleText, notificationBodyText, notificationId);
    }

    public void createNotification(int notificationIcon, String notificationTitleText,
                                   String notificationBodyText, int notificationId) {


        LogHelper.LogThreadId(logName, "The Notification builder received ");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel_01";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        long notificationTimeStamp = System.currentTimeMillis();
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(notificationIcon)
                .setWhen(notificationTimeStamp)
                .setContentTitle(notificationTitleText)
                .setContentText(notificationBodyText);


        Intent resultIntent = new Intent(this, ActivityDoWork.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ActivityDoWork.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        LogHelper.LogThreadId(logName, "The Notification builder: " + builder.toString());
        if (notificationManager != null) {
            //notificationManager.notify(notificationId, notification);
            notificationManager.notify(notificationId, builder.build());
        }
    }

    void setStatusIcon(int iconId) {
        builder.setSmallIcon(iconId);
        notificationManager.notify(notificationId, builder.build());

    }
}
