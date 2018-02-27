package com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers;

import android.location.Location;
import android.os.AsyncTask;
import android.widget.TextView;

import com.workaround.ajeesh.ajr_27002018_workaround_async_services.ActivityDoWork;

/**
 * Package Name : com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers
 * Created by ajesh on 27-02-2018.
 * Project Name : AJR-27002018-WORKAROUND-ASYNC-SERVICES
 */

public class AsyncWorkerHelper extends AsyncTask<TextView, String, Boolean> {
    String logName = "ASYNC-HLPR-ASYNCWORK";
    TextView theTextView;

    @Override
    protected Boolean doInBackground(TextView... textViews) {
        Boolean returnValue = false;
        if (textViews.length > 0) {
            theTextView = textViews[0];
            LogHelper.LogThreadId(logName, "Asynchronous Task  - Do Work initiated with new thread.");
            WorkerHelper worker = new WorkerHelper(theTextView.getContext());
            publishProgress("Starting");

            // Get most recently available location as a latitude/longitude
            Location location = worker.getLocation();
            publishProgress("Retrieved Location");

            // Convert lat/lng to a human-readable address
            String address = worker.reverseGeocode(location);
            publishProgress("Retrieved Address");

            // Write the location and address out to a file
            worker.save(location, address, "ResponsiveUx.out");
            publishProgress("Done");
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        LogHelper.LogThreadId(logName, "Asynchronous Task  - Do Work - OnProgressUpdate");
        theTextView.setText(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        LogHelper.LogThreadId(logName, "Asynchronous Task  - Do Work - OnPostExecute");
        if (aBoolean) {
            theTextView.setText("Done");
        } else {
            theTextView.setText("Error");
        }
    }
}
