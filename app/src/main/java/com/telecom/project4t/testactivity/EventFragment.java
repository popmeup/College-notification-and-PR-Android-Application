package com.telecom.project4t.testactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class EventFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "com.telecom.project4t.testActivity.MESSAGE";

    EventLayoutAdapter eventLayoutAdapter;
    RecyclerView recyclerView;

    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    DatabaseReference eventDatabaseReference;
    ChildEventListener eventChildEventListener;
    ValueEventListener valueEventListener;

    List<Event> eventList = new ArrayList<>();
    List<DatabaseReference> eventListPath = new ArrayList<>();

    int eventGridSpacing;
    //app crash for no reason -froyo
    //int largePadding = ((NavigationHost) getActivity()).dptopx(getResources().getDimension(R.dimen.category_grid_spacing));
    //int smallPadding = ((NavigationHost) getActivity()).dptopx(getResources().getDimension(R.dimen.category_grid_spacing_small));

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Log.d("lifechk evn","EventFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        eventGridSpacing = ((NavigationHost) getActivity()).dptopx(8);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        eventDatabaseReference = firebaseDatabase.getReference().child("AdminUpload");

        eventLayoutAdapter = new EventLayoutAdapter(eventList, eventListPath, getContext(), getActivity());
        recyclerView = view.findViewById(R.id.eventLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        //recyclerView.setAdapter(eventLayoutAdapter);
        recyclerView.addItemDecoration(new GridListDecoration(eventGridSpacing, eventGridSpacing));

        eventChildEventListener = eventDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    eventList.add(childSnapshot.getValue(Event.class));
                    eventListPath.add(childSnapshot.getRef());
                    //categoryLayoutAdapter.notifyDataSetChanged();
                }

                recyclerView.setAdapter(eventLayoutAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d("lifechk evn","EventFragment onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk evn","EventFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk evn","EventFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk evn","EventFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk evn","EventFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk evn","EventFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (eventChildEventListener != null) {
            eventList.clear();
            eventListPath.clear();
            eventDatabaseReference.removeEventListener(eventChildEventListener);
        }

        Log.d("lifechk evn","EventFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk evn","EventFragment onDestroy");
    }

}
