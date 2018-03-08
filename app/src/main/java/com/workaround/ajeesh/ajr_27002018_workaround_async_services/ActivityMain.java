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

import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.LogHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ActivityMain extends AppCompatActivity {
    String logName = "ASYNC-MAIN-ACT";

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
}
