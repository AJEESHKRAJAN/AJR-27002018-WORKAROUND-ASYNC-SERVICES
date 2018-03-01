package com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.HandlerThread;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Package Name : com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers
 * Created by ajesh on 27-02-2018.
 * Project Name : AJR-27002018-WORKAROUND-ASYNC-SERVICES
 */

public class WorkerHelper {
    // Controls whether to use the device/emulator GPS provider to get a location value
    // Set to false if you're using an emulator to run this code and you haven't setup DDMS
    // to create fake location data.
    private final boolean _useGpsToGetLocation = false;
    String logName = "ASYNC-HLPR-WORKER";

    HandlerThread _gpsHandlerThread;
    LocationManager _locationManager;
    LocationListener _locationListener;

    Context _context;

    public WorkerHelper(Context context) {
        _context = context;
    }


    @SuppressLint("MissingPermission")
    public void MonitorGpsInBackground() {
        if (_useGpsToGetLocation) {
            // Create a thread to handle the GPS events
            _gpsHandlerThread = new HandlerThread("GPSThread");
            _gpsHandlerThread.start();

            // Create a listener for the updates
            _locationListener = new NoOpLocationListener();

            // Get a reference to the location manager
            _locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);

            // Start GPS monitoring
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _locationListener, _gpsHandlerThread.getLooper());
        }
    }

    public void stopGpsMonitoring() {
        if (_locationManager != null)
            _locationManager.removeUpdates(_locationListener);

        if (_gpsHandlerThread != null)
            _gpsHandlerThread.quit();
    }


    // Retrieve the most recent location available
    @SuppressLint("MissingPermission")
    public Location getLocation() {
        Location lastLocation = null;
        LogHelper.LogThreadId(logName, "Get Location method triggered.");
        if (_useGpsToGetLocation) {
            LocationManager locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        // If _useGpsToGetLocation is false, or if attempt to retrieve GPS location failed,
        //  use a manually created location
        if (lastLocation == null)
            lastLocation = createLocationManually();
        LogHelper.LogThreadId(logName, "Simulate Delay method triggered.");
        simulateDelay();
        return lastLocation;
    }

    // Convert the location's lat/lng into a human-readable address
    public String reverseGeocode(Location location) {
        LogHelper.LogThreadId(logName, "Reverse Geocode method triggered.");
        String addressDescription = null;
        try {
            Geocoder geocoder = new Geocoder(_context);
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            //addressDescription = reverseGeoCodeWithWebService(location);
            LogHelper.LogThreadId(logName, "Address List found count. " + addressList.size());
            if (!addressList.isEmpty()) {
                Address firstAddress = addressList.get(0);
                // Retrieve each line of the formatted address description using
                //  getAddressLine
                LogHelper.LogThreadId(logName, "First Address: " + firstAddress);
                StringBuilder addressBuilder = new StringBuilder();
                for (int idx = 0; idx <= firstAddress.getMaxAddressLineIndex(); idx++) {
                    if (idx != 0)
                        addressBuilder.append(", ");
                    addressBuilder.append(firstAddress.getAddressLine(idx));
                }
                addressDescription = addressBuilder.toString();
                LogHelper.LogThreadId(logName, "Overall address description: " + addressDescription);
            }

        } catch (IOException ex) {
            // Some Emulators throw an IOException indicating that the Geocoder service isn't available
            //  If this happens, then just call the Google Maps web service to reverse geocode the coordinates
            addressDescription = reverseGeoCodeWithWebService(location);
        } catch (Exception ex) {
            LogHelper.LogThreadId(logName, "Worker.reverseGeocode : " + ex.getMessage());
        }
        LogHelper.LogThreadId(logName, "Simulate Delay method triggered.");
        simulateDelay();
        return addressDescription;
    }

    // Append the location timestamp, lat/lng, and address to the specified file
    // NOTE: ** If running on an emulator, the emulator must be created to include an SD card
    public void save(Location location, String address, String fileName) {
        try {
            // Retrieve the appropriate location on the SD card to great general-use,
            // publicly accessible files
            File targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            LogHelper.LogThreadId(logName, "Target Directory : " + targetDir);

            // Open or create the file so that new content is appended to the existing and
            // wrap in a buffered writer (as opposed to doing direct physical writes on
            // each write operation)
            File outFile = new File(targetDir, fileName);
            FileWriter fileWriter = new FileWriter(outFile, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            // Format the content and write it to the file
            String outLine = String.format(Locale.US, "%s:%f/%f", DateFormat.getDateTimeInstance().format(location.getTime()), location.getLatitude(), location.getLongitude());

            LogHelper.LogThreadId(logName, "File outline : " + outLine);
            writer.write(outLine);
            writer.write(address);

            // Flush the buffered writer's buffers and close the file
            writer.flush();
            writer.close();
            fileWriter.close();
        } catch (Exception ex) {
            LogHelper.LogThreadId(logName, "Worker.save ex: " + ex.getMessage());
        }
        LogHelper.LogThreadId(logName, "Simulate Delay method triggered.");
        simulateDelay();

    }

    // Create a location point to use if we can't access the GPS
    private Location createLocationManually() {
        Location lastLocation = new Location("Ajeesh_Home");
        Date now = new Date();
        lastLocation.setTime(now.getTime());
        lastLocation.setLatitude(13.111702);
        lastLocation.setLongitude(80.291387);

        return lastLocation;
    }

    // Reverse geocode that lat/lng using the Google Web Service API
    private String reverseGeoCodeWithWebService(Location location) {
        LogHelper.LogThreadId(logName, "REVERSE GEO CODE WITH WEB SERVICE method triggered.");
        StringBuilder stringBuilder = new StringBuilder();
        String addressDescription = null;
        try {
            // Create the Google Web Service URL to use to retrieve the address for the lat/lng
            String serviceUrl =
                    String.format(Locale.ENGLISH, "http://maps.google.com/maps/api/geocode/xml?sensor=false&latlng=%f,%f",
                            location.getLatitude(), location.getLongitude());
            LogHelper.LogThreadId(logName, "Service URL : " + serviceUrl);

            // Create the HTTP Client and make the call
            HttpGet httpGet = new HttpGet(serviceUrl);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpGet);
            LogHelper.LogThreadId(logName, "Service Response : " + response);

            // Retrieve the Web Service response and wrap in a Reader
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader reader = new InputStreamReader(stream);

            LogHelper.LogThreadId(logName, "content : " + stream);

            // Use the XML Parse to locate the formatted address in the response
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(reader);
            boolean isAddressNode = false;
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();
                    if (tagName.equalsIgnoreCase("formatted_address")) {
                        // This is the formatted address element so set the flag indicating
                        //  that we need to read the address from the next text element
                        isAddressNode = true;
                    }
                } else if (isAddressNode && eventType == XmlPullParser.TEXT) {
                    // This is the text element w/in the formatted_address so read it
                    //  then exit because we have what we came for
                    addressDescription = xpp.getText();
                    break;

                }
                eventType = xpp.next();
            }
        } catch (Exception ex) {
            LogHelper.LogThreadId(logName, "Worker.reverseGeoCodeWithWebService : Exception : " + ex.getMessage());
        }
        LogHelper.LogThreadId(logName, "Service Address Description : " + addressDescription);
        return addressDescription;
    }

    // Emulators don't always have the standard folder created, so create if necessary
    private void assureThatDirectoryExist(File directory) {
        if (!directory.exists())
            directory.mkdirs();
    }

    private void simulateDelay() {
        try {
            // Simulate network or other delays
            Thread.sleep(3000);
        } catch (Exception ex) {
            // Only likely exception is an InterruptedException which we can ignore
        }
    }

    class NoOpLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
        }

        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        public void onProviderEnabled(String s) {
        }

        public void onProviderDisabled(String s) {
        }
    }
}
