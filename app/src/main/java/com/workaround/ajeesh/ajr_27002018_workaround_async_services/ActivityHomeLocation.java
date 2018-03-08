package com.workaround.ajeesh.ajr_27002018_workaround_async_services;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.LogHelper;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

public class ActivityHomeLocation extends AppCompatActivity {
    private static final String logName = "ASYNC-ACT-HME-LOC-SVC";
    private TextView theTextView;
    private String url = "http://maps.google.com/maps/api/geocode/xml?sensor=false&latlng=13.111702,80.291387";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogHelper.LogThreadId(logName, "Activity Home Location has been initiated.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_location);
        theTextView = findViewById(R.id.textShowAddress);

        getMyLocation();
    }

    private void getMyLocation() {
        final String[] output = {""};
        StringRequest theStringRequest = new StringRequest(Request.Method.GET, url,
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
                                    output[0] = xpp.getText();
                                    LogHelper.LogThreadId(logName, "Activity Home Location - Response Text - " + output[0]);

                                    theTextView.setText(output[0]);
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
                        LogHelper.LogThreadId(logName, "Activity Home Location - Error thrown - " + error.getMessage());
                    }
                });

        RequestQueue theRequestQueue = Volley.newRequestQueue(this);
        theRequestQueue.add(theStringRequest);

    }
}
