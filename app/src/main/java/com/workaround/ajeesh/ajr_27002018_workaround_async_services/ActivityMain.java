package com.workaround.ajeesh.ajr_27002018_workaround_async_services;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.LogHelper;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.RequestQueueHelper;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Patterns.VolleySingleton;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ActivityMain extends AppCompatActivity {
    String logName = "ASYNC-MAIN-ACT";
    private String url = "http://maps.google.com/maps/api/geocode/xml?sensor=false&latlng=13.111702,80.291387";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LogHelper.LogThreadId(logName, "Main Activity is created.");
        String createdTime =
                (new SimpleDateFormat("HH:mm:ss", Locale.US)).format(System.currentTimeMillis());

        TextView masterPageTextView = findViewById(R.id.asyncProgMainTextView);
        masterPageTextView.append("Activity Created time: " + createdTime);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean handled = true;

        switch (id) {
            case R.id.menuDoWork: {
                generateActivityOnClassType(ActivityDoWork.class);
                break;
            }
            case R.id.menuVolley: {
                generateActivityOnClassType(ActivityVolley.class);
                break;
            }
            case R.id.menuAccessLocationGoogle: {
                generateActivityOnClassType(ActivityHomeLocation.class);
                break;
            }
            case R.id.menuAccessLocationGoogleCache: {
                getCachedVolley();
                break;
            }
            case R.id.menuAccessLocationGoogleSingletonCache: {
                getSingletonVolley();
                break;
            }
            case R.id.menuExit: {
                onClickExit(item);
                break;
            }
            default: {
                handled = super.onOptionsItemSelected(item);
            }
        }
        return handled;
    }


    private void generateActivityOnClassType(Class<?> activityClass) {
        LogHelper.LogThreadId(logName, "Activity generated for :" + activityClass);
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    private void onClickExit(MenuItem item) {
        finish();
    }

    private void getCachedVolley() {

        RequestQueueHelper queueHelper = new RequestQueueHelper(this, url);
        queueHelper.mRequestQueue.getCache().clear();
        queueHelper.mRequestQueue.getCache().invalidate(url, true);
        queueHelper.processRequest();

    }

    private void getSingletonVolley() {
        // Formulate the request and handle the response.
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
                                    Toast.makeText(ActivityMain.this, xpp.getText(), Toast.LENGTH_LONG).show();
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

        // Get a RequestQueue
        RequestQueue queue = VolleySingleton.getInstance(this).
                getRequestQueue();
        VolleySingleton.getInstance(this).
                addToRequestQueue(stringRequest);
    }
}
