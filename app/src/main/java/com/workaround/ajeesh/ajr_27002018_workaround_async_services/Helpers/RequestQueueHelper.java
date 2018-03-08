package com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Package Name : com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers
 * Created by ajesh on 08-03-2018.
 * Project Name : AJR-27002018-WORKAROUND-ASYNC-SERVICES
 */

public class RequestQueueHelper {
    private Context theContext;
    public RequestQueue mRequestQueue;
    //    TextView theTextView;
    private String theUrl;
    private final String logName = "ASYNC-VOLLEY-CACHE";
    private Cache theCache;
    private Network theNetwork;

    public RequestQueueHelper(Context theContext, String theUrl) {
        this.theContext = theContext;
        this.theUrl = theUrl;
        // Instantiate the cache
        theCache = new DiskBasedCache(theContext.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        theNetwork = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(theCache, theNetwork);
    }

    public void processRequest() {
        LogHelper.LogThreadId(logName, "Activity Home Location - Cache. ");


       /* //TextView
        theTextView = ((Activity) theContext).findViewById(R.id.textShowAddress);*/


        // Start the queue
        mRequestQueue.start();

        //Volley Tag
       /* VolleyLog.setTag("ASYNC-VOLLEY-LOGS");
        Log.isLoggable("ASYNC-VOLLEY-LOGS", Log.VERBOSE);*/
        VolleyLog.DEBUG = true;


        // Formulate the request and handle the response.
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, theUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogHelper.LogThreadId(logName, "Activity Home Location - Response Received.");
                        try {
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            XmlPullParser xpp = factory.newPullParser();
                            xpp.setInput(new StringReader(response));
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
                                    LogHelper.LogThreadId(logName, "Activity Home Location - Response Text - " + xpp.getText());

                                  /*  theTextView.setText(xpp.getText());*/
                                    Toast.makeText(theContext, xpp.getText(), Toast.LENGTH_LONG).show();
                                    break;

                                }
                                eventType = xpp.next();
                            }

                        } catch (XmlPullParserException e) {
                            LogHelper.LogThreadId(logName, "Activity Home Location - XmlPullParserException Exception thrown - " + e.getMessage());
                            e.printStackTrace();
                        } catch (IOException e) {
                            LogHelper.LogThreadId(logName, "Activity Home Location - IOException Exception thrown - " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });


        // Add the request to the RequestQueue.
        mRequestQueue.add(stringRequest);

// ...
    }

}
