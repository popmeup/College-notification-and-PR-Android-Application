package com.telecom.project4t.testactivity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DisplayEventLayoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView displayEventImage;
    TextView displayEventName;
    TextView displayEventDesc;
    TextView displayEventTime;
    Event displayEventListSet;

    Context context;
    Activity activity;

    List<Event> displayEventList;
    ArrayList<String> arrayList;

    public DisplayEventLayoutViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        displayEventImage = (ImageView) itemView.findViewById(R.id.eventImage);
        displayEventName = (TextView) itemView.findViewById(R.id.eventName);
        displayEventDesc = (TextView) itemView.findViewById(R.id.eventDesc);
        displayEventTime = (TextView) itemView.findViewById(R.id.timePlace);
    }

    public void setData(List<Event> eventList, int position, Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.displayEventList = eventList;
        this.displayEventListSet = eventList.get(position);

        displayEventImage.setImageResource(R.drawable.reg_profile_image);
        Picasso.get().load(displayEventListSet.imageURL).into(displayEventImage);
        displayEventName.setText(displayEventListSet.getTitle());
        displayEventDesc.setText(displayEventListSet.getContent());
        displayEventTime.setText(displayEventListSet.getTime());
    }

    @Override
    public void onClick(View view) {
        arrayList = displayEventList.get(getAdapterPosition()).returnArrayList();
        ((NavigationHost) activity).passStringArrayTo(new DisplayEventFragment(), arrayList, true);
    }

}

