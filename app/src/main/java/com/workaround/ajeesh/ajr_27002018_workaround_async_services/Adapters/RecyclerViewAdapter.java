package com.workaround.ajeesh.ajr_27002018_workaround_async_services.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Helpers.LogHelper;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.Model.ModelListItem;
import com.workaround.ajeesh.ajr_27002018_workaround_async_services.R;

import java.util.List;

/**
 * Package Name : com.workaround.ajeesh.ajr_27002018_workaround_async_services.Adapters
 * Created by ajesh on 07-03-2018.
 * Project Name : AJR-27002018-WORKAROUND-ASYNC-SERVICES
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private static final String logName = "ASYNC-ADPTR-RECYCLER";
    List<ModelListItem> listItems;
    Context theContext;

    public RecyclerViewAdapter(List<ModelListItem> listItems, Context theContext) {
        this.listItems = listItems;
        this.theContext = theContext;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogHelper.LogThreadId(logName, "RecyclerViewAdapter : onCreateViewHolder ");
        View theView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_source, parent, false);
        return new RecyclerViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        LogHelper.LogThreadId(logName, "RecyclerViewAdapter : onBindViewHolder ");
        ModelListItem theListItem = listItems.get(position);
        holder.textViewHeading.setText(theListItem.getHeading());
        holder.textViewDesc.setText(theListItem.getDescription());

        Picasso.with(theContext).load(theListItem.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewHeading;
        public TextView textViewDesc;
        public ImageView imageView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            textViewHeading = itemView.findViewById(R.id.textViewSimpleVolley);
            textViewDesc = itemView.findViewById(R.id.textViewSimpleVolleyDescription);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
