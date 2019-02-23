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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    Bundle args;
    CategoryLayoutAdapter categoryLayoutAdapter;
    RecyclerView recyclerView;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference profileDatabaseReference;
    ChildEventListener profileChildEventListener = null;

    String userStatus = "user", orgName = " ";
    List<UserProfile> categoryList = new ArrayList<>();
    List<String> categoryListPath = new ArrayList<>();


    int categoryGridSpacing;
    long count = 0;
    //app crash for no reason -froyo
    //int largePadding = ((NavigationHost) getActivity()).dptopx(getResources().getDimension(R.dimen.category_grid_spacing));
    //int smallPadding = ((NavigationHost) getActivity()).dptopx(getResources().getDimension(R.dimen.category_grid_spacing_small));

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Log.d("lifechk cat","CategoryFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        args = getArguments();
        if (args != null) {
            orgName = args.getString("passYeah");
            this.getArguments().remove("passYeah");
        }

        categoryGridSpacing = ((NavigationHost) getActivity()).dptopx(8);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        profileDatabaseReference = firebaseDatabase.getReference().child("Profile");

        categoryLayoutAdapter = new CategoryLayoutAdapter(categoryList, categoryListPath, getContext(), getActivity());
        recyclerView = view.findViewById(R.id.categoryLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        //recyclerView.setAdapter(categoryLayoutAdapter);
        recyclerView.addItemDecoration(new GridListDecoration(categoryGridSpacing, categoryGridSpacing));

        profileChildEventListener = profileDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    userStatus = childSnapshot.getValue().toString();

                    if (userStatus.equals("admin")) {
                        categoryList.add(dataSnapshot.getValue(UserProfile.class));
                        categoryListPath.add(dataSnapshot.getKey());
                        //categoryLayoutAdapter.notifyDataSetChanged();
                    }
                    recyclerView.setAdapter(categoryLayoutAdapter);
                }
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

        Log.d("lifechk cat","CategoryFragment onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk cat","CategoryFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk cat","CategoryFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk cat","CategoryFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk cat","CategoryFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk cat","CategoryFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (profileChildEventListener != null) {
            categoryList.clear();
            profileDatabaseReference.removeEventListener(profileChildEventListener);
        }

        Log.d("lifechk cat","CategoryFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk cat","CategoryFragment onDestroy");
    }

}
