package com.workaround.ajeesh.ajr_27002018_workaround_async_services;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Adapters.RecyclerViewAdapter;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.LogHelper;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Model.ModelListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityVolley extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ModelListItem> listItems;
    private String url = "https://simplifiedcoding.net/demos/marvel/";
    private static final String logName = "ASYNC-ACT-SMPL-VOLLEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogHelper.LogThreadId(logName, "OnCreate: Simple Volley");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        LoadRecyclerViewDataUsingVolley();
    }

    private void LoadRecyclerViewDataUsingVolley() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray theJsonArray = new JSONArray(response);
                    LogHelper.LogThreadId(logName, "Total list count : " + theJsonArray.length());
                    for (int i = 0; i < theJsonArray.length(); i++) {

                        JSONObject theJsonObject = theJsonArray.getJSONObject(i);

                        ModelListItem theListItem = new ModelListItem(
                                theJsonObject.getString("name"),
                                theJsonObject.getString("bio"),
                                theJsonObject.getString("imageurl")
                        );
                        listItems.add(theListItem);
                    }

                    adapter = new RecyclerViewAdapter(listItems, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    LogHelper.LogThreadId(logName, "LoadRecyclerViewDataUsingVolley Exception: " + e.getMessage());

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "URL returned an error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
