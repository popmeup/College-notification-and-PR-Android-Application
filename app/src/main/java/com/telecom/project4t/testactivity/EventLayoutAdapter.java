package com.telecom.project4t.testactivity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.telecom.project4t.testactivity.Event;
import com.telecom.project4t.testactivity.EventLayoutViewHolder;
import com.telecom.project4t.testactivity.R;

import java.util.List;

public class EventLayoutAdapter extends RecyclerView.Adapter<EventLayoutViewHolder> {

    View eventLayoutView;

    Context context;
    Activity activity;

    List<Event> eventList;
    List<DatabaseReference> eventListPath;

    public EventLayoutAdapter(List<Event> eventList, List<DatabaseReference> eventListPath, Context context, Activity activity) {
        this.eventList = eventList;
        this.eventListPath = eventListPath;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public EventLayoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        eventLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);

        return new EventLayoutViewHolder(eventLayoutView);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public void onBindViewHolder(EventLayoutViewHolder viewHolder, int position) {
        viewHolder.setData(eventList, eventListPath, position, context, activity);
    }

}
