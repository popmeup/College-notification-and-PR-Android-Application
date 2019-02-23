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
import android.widget.ImageButton;
import android.widget.TextView;

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
public class DisplayCategoryFragment extends Fragment {

    Bundle savedState = null;
    Bundle args;
    EventLayoutAdapter eventLayoutAdapter;
    RecyclerView recyclerView;
    TextView displayCategoryTitleText;
    ImageButton exitDisplayCategoryImageButton;

    UserProfile userProfile;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    DatabaseReference eventDatabaseReference;
    DatabaseReference categoryDatabaseReference;
    DatabaseReference profileDatabaseReference;
    ChildEventListener eventChildEventListener;
    ChildEventListener profileChildEventListener = null;

    String categoryOrgUID;
    String categoryPath =" ";
    String userStatus = "user",  userOrganization = " ";
    List<Event> eventList = new ArrayList<>();
    List<DatabaseReference> eventListPath = new ArrayList<>();
    List<UserProfile> categoryList = new ArrayList<>();
    List<String> categoryListPath = new ArrayList<>();
    List<String> tempCategoryListPath = new ArrayList<>();
    ArrayList<String> arrayList;
    int catUserEmail = 0, catID = 1, catImageURL = 2, catUserName = 3, catOrg = 4, catOrgEmail = 5, catOrgPhone = 6, catOrgParent = 7, catUserStatus = 8, catUserSurname = 9;
    int eventGridSpacing;

    public DisplayCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        /*if (savedInstanceState != null) {
            arrayList = savedInstanceState.getStringArrayList("catArrayList");
        }
        Log.d("savestateinC", arrayList + " inC");*/

        Log.d("lifechk disCat", "DisplayCategoryFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        args = getArguments();
        if (args != null) {
            categoryOrgUID = args.getString("passYeah");
            this.getArguments().remove("passYeah");
        }

        if (categoryOrgUID != null) {
            if (categoryOrgUID.equals("0x00112233445566778899")) {
                userOrganization = "Telecom";
            }
            else if (categoryOrgUID.equals("0x00112233445566778888")) {
                userOrganization = "Electrical";
            }
        }

        if(savedState != null) {
            categoryOrgUID = savedState.getString("categoryOrgUID");
        }
        savedState = null;

        eventGridSpacing = ((NavigationHost) getActivity()).dptopx(8);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_category, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        eventDatabaseReference = firebaseDatabase.getReference("AdminUpload");
        profileDatabaseReference = firebaseDatabase.getReference().child("Profile");

        displayCategoryTitleText = (TextView) view.findViewById(R.id.displayCategoryTitleText);
        exitDisplayCategoryImageButton = (ImageButton) view.findViewById(R.id.exitDisplayCategoryImageButton);

        eventLayoutAdapter = new EventLayoutAdapter(eventList, eventListPath, getContext(), getActivity());
        recyclerView = view.findViewById(R.id.categoryEventLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        //recyclerView.setAdapter(eventLayoutAdapter);
        recyclerView.addItemDecoration(new GridListDecoration(eventGridSpacing, eventGridSpacing));

        displayCategoryTitleText.setText(getString(R.string.nearby_event_message));

        eventChildEventListener = eventDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
                if (categoryOrgUID.equals(dataSnapshot.getKey())|| userOrganization.equals(dataSnapshot.getKey())) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        eventList.add(childSnapshot.getValue(Event.class));
                        eventListPath.add(childSnapshot.getRef());
                        //categoryLayoutAdapter.notifyDataSetChanged();
                    }
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

        exitDisplayCategoryImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationHost) getActivity()).backTo();
            }
        });

        Log.d("lifechk disCat", "DisplayCategoryFragment onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*if (savedInstanceState != null) {
            arrayList = savedInstanceState.getStringArrayList("catArrayList");
        }
        Log.d("savestateinAC", arrayList + " inAC");*/

        Log.d("lifechk disCat", "DisplayCategoryFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk disCat","DisplayCategoryFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk disCat","DisplayCategoryFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk disCat","DisplayCategoryFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk disCat","DisplayCategoryFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (eventChildEventListener != null) {
            eventList.clear();
            eventListPath.clear();
            eventDatabaseReference.removeEventListener(eventChildEventListener);
        }

        savedState = saveState();

        Log.d("lifechk disCat", "DisplayCategoryFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk disCat", "DisplayCategoryFragment onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /*outState.putStringArrayList("catArrayList", arrayList);
        Log.d("savestate", "save!!!");*/
    }

    public Bundle saveState() { /* called either from onDestroyView() or onSaveInstanceState() */
        Bundle state = new Bundle();
        state.putString("categoryOrgUID", categoryOrgUID);
        return state;
    }

}
