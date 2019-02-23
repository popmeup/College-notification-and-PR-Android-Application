package com.telecom.project4t.testactivity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telecom.project4t.testactivity.DisplayEventLayoutViewHolder;
import com.telecom.project4t.testactivity.Event;
import com.telecom.project4t.testactivity.R;

import java.util.List;

public class DisplayEventLayoutAdapter extends RecyclerView.Adapter<DisplayEventLayoutViewHolder> {

    View displayEventLayoutView;

    Context context;
    Activity activity;

    List<Event> displayEventList;

    public DisplayEventLayoutAdapter(List<Event> displayEventList, Context context, Activity activity) {
        this.displayEventList = displayEventList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public DisplayEventLayoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        displayEventLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);

        return new DisplayEventLayoutViewHolder(displayEventLayoutView);
    }

    @Override
    public int getItemCount() {
        return displayEventList.size();
    }

    @Override
    public void onBindViewHolder(DisplayEventLayoutViewHolder viewHolder, int position) {
        viewHolder.setData(displayEventList, position, context, activity);
    }

}
