package com.telecom.project4t.testactivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayEventFragment extends Fragment {

    Bundle savedState = null;
    Bundle args;
    CoordinatorLayout displayEvent;
    Toolbar displayEventToolbar;
    ImageView displayEventToolbarBackdrop;
    ImageButton exitDisplayEventImageButton;
    ToggleButton starButton;
    Button editButton;
    Button deleteButton;
    Button downloadPDFButton;
    TextView displayTimePlace;
    TextView displayEventName;
    TextView displayOrg;
    TextView displayDesc;
    Snackbar starredEventSnackbar;
    long counter;
    ChildEventListener childEventListener = null;

    CoordinatorLayout.LayoutParams starredEventSnackbarParams;
    ConstraintLayout eventCardviewLayout;
    ConstraintSet set;
    ConstraintLayout.LayoutParams starButtonParams, editButtonParams;
    ContextThemeWrapper starButtonContext;

    UserProfile userProfile;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    DatabaseReference myProfileDatabaseReference;
    DatabaseReference eventPathDatabaseReference;
    DatabaseReference starredEventDatabaseReference;
    DatabaseReference databaseReference1;
    DatabaseReference deleteStarredDatabaseReference;
    DatabaseReference xx;

    StorageReference storageReference;

    String key;
    String eventPath,userStatus,userStatus1;
    String userOrganization = " ";
    String fileURL = " ";
    String titleName = " ";
    List<Event> eventList = new ArrayList<>();
    ArrayList<String> arrayList;
    int eventDesc = 0, eventFileURL = 1, eventImageURL = 2, eventOrg = 3, eventPlace = 4, eventTime = 5, eventName = 6;
    int snackbarMargin;
    int starButtonHeight, starButtonWidth;

    public DisplayEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("lifechk disEvn","DisplayEventFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        args = getArguments();
        if (args != null) {
            eventPath = args.getString("passYeah");
            this.getArguments().remove("passYeah");
        }

        if(savedState != null) {
            eventPath = savedState.getString("eventPath");
        }
        savedState = null;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_event, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myProfileDatabaseReference = firebaseDatabase.getReference().child("Profile").child(firebaseAuth.getUid());
        eventPathDatabaseReference = firebaseDatabase.getReferenceFromUrl(eventPath);
        Log.d("PathFinder", "@DisEveFrag" + eventPath);

        snackbarMargin = ((NavigationHost) getActivity()).dptopx(8);
        starButtonWidth = ((NavigationHost) getActivity()).dptopx(24);
        starButtonHeight = ((NavigationHost) getActivity()).dptopx(24);

        displayEvent = (CoordinatorLayout) view.findViewById(R.id.displayEvent);
        eventCardviewLayout = (ConstraintLayout) view.findViewById(R.id.eventCardviewLayout);
        exitDisplayEventImageButton = (ImageButton) view.findViewById(R.id.exitDisplayEventImageButton);
        displayEventToolbarBackdrop = (ImageView) view.findViewById(R.id.displayEventToolbarBackdrop);
        displayEventToolbar = (Toolbar) view.findViewById(R.id.displayEventToolbar);
        starButton = (ToggleButton) view.findViewById(R.id.starButton);
        displayTimePlace = (TextView) view.findViewById(R.id.displayTimePlace);
        displayEventName = (TextView) view.findViewById(R.id.displayEventName);
        displayOrg = (TextView) view.findViewById(R.id.displayOrg);
        displayDesc = (TextView) view.findViewById(R.id.displayDesc);
        downloadPDFButton = (Button) view.findViewById(R.id.downloadPDFButton);
        set = new ConstraintSet();

        displayEventToolbarBackdrop.setImageResource(R.drawable.reg_profile_image);

        setEditButton();
        receiveUserProfile();

        eventPathDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                key = dataSnapshot.getKey();
                eventList.add(dataSnapshot.getValue(Event.class));
                arrayList = eventList.get(0).returnArrayList();

                if(arrayList != null) {
                    Picasso.get().load(arrayList.get(eventImageURL)).into(displayEventToolbarBackdrop);
                    displayEventName.setText(arrayList.get(eventName));
                    displayTimePlace.setText(arrayList.get(eventTime) + "\t" + arrayList.get(eventPlace));
                    displayOrg.setText(arrayList.get(eventOrg));
                    displayDesc.setText(arrayList.get(eventDesc));
                    titleName = arrayList.get(eventName);
                    fileURL = arrayList.get(eventFileURL);

                    if (arrayList.get(eventOrg).equals(userOrganization)) {
                        showEditButton();
                    }

                    pressStarbutton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        exitDisplayEventImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationHost) getActivity()).backTo();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference1=firebaseDatabase.getReference().child("Starred");
                childEventListener = databaseReference1.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
                        for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            userStatus = childSnapshot.getKey();
                            if(userStatus.equals(titleName)){
                                xx=childSnapshot.getRef();

                                deleteStarredDatabaseReference=firebaseDatabase.getReferenceFromUrl(xx.toString());
                                //Log.d("love", deleteStarredDatabaseReference.toString() );
                                //deleteStarredDatabaseReference.removeValue();
                            }

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
                ((NavigationHost) getActivity()).passStringTo(new UpdateEventFragment(), eventPath, true);
                //((NavigationHost) getActivity()).passStringTo(new UpdateEventFragment(), starredPath, true);

            }
        });

        downloadPDFButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(fileURL == null || fileURL.equals("")){
                    Toast.makeText(getContext(), "Your database have not file, please upload file first!!", Toast.LENGTH_SHORT).show();
                }else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileURL));
                    startActivity(browserIntent);
                }
            }
        });

        Log.d("lifechk disEvn","DisplayEventFragment onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk disEvn","DisplayEventFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk disEvn","DisplayEventFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk disEvn","DisplayEventFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk disEvn","DisplayEventFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk disEvn","DisplayEventFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        savedState = saveState();

        Log.d("lifechk disEvn","DisplayEventFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk disEvn","DisplayEventFragment onDestroy");
    }

    public void pressStarbutton() {
        starButton.setChecked(readStarredButtonState());

        starButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showSnackbar("Event Starred");
                    // The toggle is enabled
                    starredEventDatabaseReference = firebaseDatabase.getReference().child("Starred").child(firebaseAuth.getUid()).child(key);
                    starredEventDatabaseReference.setValue(eventPath);
                } else {
                    showSnackbar("Event Unstar");
                    // The toggle is disabled
                    starredEventDatabaseReference = firebaseDatabase.getReference().child("Starred").child(firebaseAuth.getUid()).child(key);
                    starredEventDatabaseReference.removeValue();
                }
                saveStarredButtonState(isChecked);
            }
        });
    }

    public void saveStarredButtonState(boolean isFavourite) {
        SharedPreferences aSharedPreferenes = getActivity().getSharedPreferences(
                titleName, Context.MODE_PRIVATE);
        SharedPreferences.Editor aSharedPreferenesEdit = aSharedPreferenes
                .edit();
        aSharedPreferenesEdit.putBoolean("State", isFavourite);
        aSharedPreferenesEdit.commit();
    }

    public boolean readStarredButtonState() {
        SharedPreferences aSharedPreferenes = getActivity().getSharedPreferences(
                titleName, Context.MODE_PRIVATE);
        return aSharedPreferenes.getBoolean("State", false);
    }

    public Bundle saveState() { /* called either from onDestroyView() or onSaveInstanceState() */
        Bundle state = new Bundle();
        state.putString("eventPath", eventPath);
        return state;
    }

    public void receiveUserProfile() {
        myProfileDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);
                userOrganization = userProfile.getuserOrganization();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setEditButton(){
        editButton = new Button(getActivity());
        editButtonParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        editButton.setId(View.generateViewId());
        editButton.setText(R.string.edit_event_button);
        editButton.setForegroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent, getContext().getTheme())));
        editButton.setElevation(((NavigationHost) getActivity()).dptopx(2));
        editButton.setLayoutParams(editButtonParams);
    }

    public void showEditButton(){
        eventCardviewLayout.removeView(starButton);
        eventCardviewLayout.addView(editButton, editButtonParams);

        set.clone(eventCardviewLayout);
        set.connect(editButton.getId(), ConstraintSet.TOP, eventCardviewLayout.getId(), ConstraintSet.TOP, 0);
        set.connect(editButton.getId(), ConstraintSet.END, eventCardviewLayout.getId(), ConstraintSet.END, 0);
        set.connect(displayTimePlace.getId(), ConstraintSet.END, editButton.getId(), ConstraintSet.START, 16);
        set.connect(displayEventName.getId(), ConstraintSet.END, editButton.getId(), ConstraintSet.START, 16);
        set.connect(displayOrg.getId(), ConstraintSet.END, editButton.getId(), ConstraintSet.START, 16);
        set.applyTo(eventCardviewLayout);
    }


    public void showSnackbar(String showString) {
        starredEventSnackbar = Snackbar.make(displayEvent, showString, Snackbar.LENGTH_SHORT);
        starredEventSnackbarParams = (CoordinatorLayout.LayoutParams) starredEventSnackbar.getView().getLayoutParams();
        starredEventSnackbarParams.bottomMargin = (snackbarMargin);
        starredEventSnackbarParams.leftMargin = (snackbarMargin);
        starredEventSnackbarParams.rightMargin = (snackbarMargin);
        starredEventSnackbar.getView().setLayoutParams(starredEventSnackbarParams);
        starredEventSnackbar.show();
    }

}
