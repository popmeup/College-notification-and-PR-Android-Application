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


/**
 * A simple {@link Fragment} subclass.
 */
public class StarredFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "com.telecom.project4t.testActivity.MESSAGE";

    EventLayoutAdapter eventLayoutAdapter;
    RecyclerView recyclerView;

    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    DatabaseReference myStarredDatabaseReference;
    DatabaseReference eventPathDatabaseReference;
    ChildEventListener myStarredChildEventListener;
    ValueEventListener valueEventListener;
    ValueEventListener eventPathValueEventListener;

    List<Event> eventList = new ArrayList<>();
    List<DatabaseReference> eventListPath = new ArrayList<>();

    int eventGridSpacing;

    public StarredFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Log.d("lifechk sta","StarredFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        eventGridSpacing = ((NavigationHost) getActivity()).dptopx(8);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_starred, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myStarredDatabaseReference = firebaseDatabase.getReference().child("Starred").child(firebaseAuth.getUid());
        Log.d("PathFinder", "@StarFrag : Enter");

        eventLayoutAdapter = new EventLayoutAdapter(eventList, eventListPath, getContext(), getActivity());
        recyclerView = view.findViewById(R.id.starredLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        //recyclerView.setAdapter(eventLayoutAdapter);
        recyclerView.addItemDecoration(new GridListDecoration(eventGridSpacing, eventGridSpacing));

        myStarredChildEventListener = myStarredDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
                receiveEventList(dataSnapshot.getValue(String.class));
                Log.d("PathFinder", "@StarFrag : 1 : " + dataSnapshot.getValue(String.class));
                //categoryLayoutAdapter.notifyDataSetChanged();
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

        Log.d("lifechk sta","StarredFragment onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk sta","StarredFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk sta","StarredFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk sta","StarredFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk sta","StarredFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk sta","StarredFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (eventPathValueEventListener != null) {
            eventList.clear();
            eventListPath.clear();
            myStarredDatabaseReference.removeEventListener(eventPathValueEventListener);
        }

        if (myStarredChildEventListener != null) {
            myStarredDatabaseReference.removeEventListener(myStarredChildEventListener);
        }

        Log.d("lifechk sta","StarredFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk sta","StarredFragment onDestroy");
    }

    public void receiveEventList(final String eventPath) {
        Log.d("PathFinder", "@StarFrag : 2 : " + eventPath);
        eventPathDatabaseReference = firebaseDatabase.getReferenceFromUrl(eventPath);

        eventPathValueEventListener = eventPathDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.add(dataSnapshot.getValue(Event.class));
//                Log.d("PathFinder", "@StarFrag : 3 : " + eventList.get(eventList.size() - 1).returnArrayList());
                eventListPath.add(dataSnapshot.getRef());
                recyclerView.setAdapter(eventLayoutAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
