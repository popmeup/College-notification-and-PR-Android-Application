package com.telecom.project4t.testactivity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventLayoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView eventImage;
    TextView eventName;
    TextView eventDesc;
    TextView eventTime;
    Event eventListSet;

    Context context;
    Activity activity;

    String eventPath;
    List<Event> eventList;
    List<DatabaseReference> eventListPath;

    public EventLayoutViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        eventImage = (ImageView) itemView.findViewById(R.id.eventImage);
        eventName = (TextView) itemView.findViewById(R.id.eventName);
        eventDesc = (TextView) itemView.findViewById(R.id.eventDesc);
        eventTime = (TextView) itemView.findViewById(R.id.timePlace);
    }

    public void setData(List<Event> eventList, List<DatabaseReference> eventListPath, int position, Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.eventList = eventList;
        this.eventListPath = eventListPath;
        this.eventListSet = eventList.get(position);

        eventImage.setImageResource(R.drawable.reg_profile_image);
        Picasso.get().load(eventListSet.imageURL).into(eventImage);
        eventName.setText(eventListSet.getTitle());
        eventDesc.setText(eventListSet.getContent());
        eventTime.setText(eventListSet.getTime());
    }

    @Override
    public void onClick(View view) {
        eventPath = eventListPath.get(getAdapterPosition()).toString();
        Log.d("FBD", eventPath);
        ((NavigationHost) activity).passStringTo(new DisplayEventFragment(), eventPath, true);
    }

}
