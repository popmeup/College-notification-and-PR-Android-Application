package com.telecom.project4t.testactivity;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
public class AccountFragment extends Fragment {

    EventLayoutAdapter eventLayoutAdapter;
    RecyclerView recyclerView;

    ConstraintLayout profilePreviewBar;
    ConstraintSet set;
    ConstraintLayout.LayoutParams idPreviewParams,
            fullnamePreviewParams,
            emailPreviewParams,
            userStatusPreviewParams;

    Button editProfileButton;
    TextView idPreview;
    TextView fullnamePreview;
    TextView emailPreview;
    TextView userStatusPreview;

    UserProfile userProfile;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    DatabaseReference myProfileDatabaseReference;
    DatabaseReference myEventDatabaseReference;
    StorageReference storageReference;
    ValueEventListener myProfileValueEventListener;
    ChildEventListener myEventChildEventListener;

    List<Event> eventList = new ArrayList<>();
    List<DatabaseReference> eventListPath = new ArrayList<>();
    String organization = "";
    int eventGridSpacing;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Log.d("lifechk acc","AccountFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        eventGridSpacing = ((NavigationHost) getActivity()).dptopx(8);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        myProfileDatabaseReference = firebaseDatabase.getReference().child("Profile").child(firebaseAuth.getUid());
        storageReference = firebaseStorage.getReference();

        editProfileButton = (Button) view.findViewById(R.id.editProfileButton);
        profilePreviewBar = (ConstraintLayout) view.findViewById(R.id.profilePreviewBar);
        set = new ConstraintSet();

        eventLayoutAdapter = new EventLayoutAdapter(eventList, eventListPath, getContext(), getActivity());
        recyclerView = view.findViewById(R.id.accountLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        //recyclerView.setAdapter(eventLayoutAdapter);
        recyclerView.addItemDecoration(new GridListDecoration(eventGridSpacing, eventGridSpacing));

        setProfilePreviewLayout();
        receiveUserProfile();

        myEventDatabaseReference = firebaseDatabase.getReference().child("AdminUpload").child(organization);
        myEventChildEventListener = myEventDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                eventList.add(dataSnapshot.getValue(Event.class));
                eventListPath.add(dataSnapshot.getRef());

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

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).passStringTo(new RegisterFragment(), "editProfile", true);
            }
        });

        Log.d("lifechk acc","AccountFragment onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk acc","AccountFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk acc","AccountFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk acc","AccountFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk acc","AccountFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk acc","AccountFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (myEventChildEventListener != null) {
            eventList.clear();
            eventListPath.clear();
            myEventDatabaseReference.removeEventListener(myEventChildEventListener);
        }

        if (myProfileValueEventListener != null) {
            myProfileDatabaseReference.removeEventListener(myProfileValueEventListener);
        }

        Log.d("lifechk acc","AccountFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk acc","AccountFragment onDestroy");
    }

    public void setProfilePreviewLayout() {
        idPreview = new TextView(getActivity());
        fullnamePreview = new TextView(getActivity());
        emailPreview = new TextView(getActivity());
        userStatusPreview = new TextView(getActivity());

        idPreviewParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        fullnamePreviewParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        emailPreviewParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        userStatusPreviewParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        idPreview.setId(View.generateViewId());
        fullnamePreview.setId(View.generateViewId());
        emailPreview.setId(View.generateViewId());
        userStatusPreview.setId(View.generateViewId());

        idPreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        fullnamePreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emailPreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        userStatusPreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        idPreview.setText(getString(R.string.id_message));
        fullnamePreview.setText(getString(R.string.fullname_message));
        emailPreview.setText(getString(R.string.email_message));
        userStatusPreview.setText(getString(R.string.status_message));

        profilePreviewBar.addView(idPreview, idPreviewParams);
        profilePreviewBar.addView(fullnamePreview, fullnamePreviewParams);
        profilePreviewBar.addView(emailPreview, emailPreviewParams);
        profilePreviewBar.addView(userStatusPreview, userStatusPreviewParams);

        set.clone(profilePreviewBar);
        set.connect(idPreview.getId(), ConstraintSet.START, profilePreviewBar.getId(), ConstraintSet.START, ((NavigationHost) getActivity()).dptopx(16));
        set.connect(idPreview.getId(), ConstraintSet.TOP, profilePreviewBar.getId(), ConstraintSet.TOP, ((NavigationHost) getActivity()).dptopx(64));
        set.connect(fullnamePreview.getId(), ConstraintSet.START, profilePreviewBar.getId(), ConstraintSet.START, ((NavigationHost) getActivity()).dptopx(16));
        set.connect(fullnamePreview.getId(), ConstraintSet.TOP, idPreview.getId(), ConstraintSet.BOTTOM, ((NavigationHost) getActivity()).dptopx(16));
        set.connect(emailPreview.getId(), ConstraintSet.START, profilePreviewBar.getId(), ConstraintSet.START, ((NavigationHost) getActivity()).dptopx(16));
        set.connect(emailPreview.getId(), ConstraintSet.TOP, fullnamePreview.getId(), ConstraintSet.BOTTOM, ((NavigationHost) getActivity()).dptopx(16));
        set.connect(userStatusPreview.getId(), ConstraintSet.START, profilePreviewBar.getId(), ConstraintSet.START, ((NavigationHost) getActivity()).dptopx(16));
        set.connect(userStatusPreview.getId(), ConstraintSet.TOP, emailPreview.getId(), ConstraintSet.BOTTOM, ((NavigationHost) getActivity()).dptopx(16));
        set.connect(userStatusPreview.getId(), ConstraintSet.BOTTOM, profilePreviewBar.getId(), ConstraintSet.BOTTOM, ((NavigationHost) getActivity()).dptopx(16));
        set.applyTo(profilePreviewBar);
    }

    public void receiveUserProfile() {
        myProfileValueEventListener = myProfileDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);
                organization = userProfile.getuserOrganization();
                idPreview.setText(userProfile.getuserId());
                fullnamePreview.setText(userProfile.getuserName() + " " + userProfile.getuserSurname());
                emailPreview.setText(userProfile.getuserEmail());
                userStatusPreview.setText(userProfile.getuserStatus());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
