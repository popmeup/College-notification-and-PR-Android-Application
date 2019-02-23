package com.telecom.project4t.testactivity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateEventFragment extends Fragment {

    Bundle args;
    EditText titleUpdate, contentUpdate, timeUpdate, placeUpdate, organizeUpdate;
    TextView imageNameUpdate, fileNameUpdate;
    Button uploadImageButton, uploadFileButton, saveButton, deleteButton;
    ImageButton exitUpdateEventImageButton;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    DatabaseReference myProfileDatabaseReference;
    DatabaseReference eventPathDatabaseReference;
    DatabaseReference deleteEventDatabaseReference;
    DatabaseReference deleteStarredDatabaseReference;
    DatabaseReference databaseReference1;
    DatabaseReference xx;
    DatabaseReference myTitleDatabaseReference;
    ChildEventListener childEventListener;
    StorageReference storageReference;

    UploadTask uploadTaskFile;
    UploadTask uploadTaskImage;
    Event event;
    Event userProfile;
    StorageReference fileReference;
    StorageReference imageReference;
    ValueEventListener myProfileValueEventListener;
    ValueEventListener eventPathValueEventListener;
    ValueEventListener deleteeventPathValueEventListener;


    Uri pdfUri;
    Uri imageUri;
    long counter;
    long number;
    String title, content, time, place, organize, fileURL, imageURL, admintitle;
    String titlestarred =" ";
    String eventPath,userStatus,starredPath;

    public UpdateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("lifechk upEvn","UpdateEventFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        args = getArguments();
        if (args != null) {
            eventPath = args.getString("passYeah");
            this.getArguments().remove("passYeah");
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_event, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        myProfileDatabaseReference = firebaseDatabase.getReference().child("Profile").child(firebaseAuth.getUid());
        eventPathDatabaseReference = firebaseDatabase.getReferenceFromUrl(eventPath);
        deleteEventDatabaseReference= firebaseDatabase.getReferenceFromUrl(eventPath);
        myTitleDatabaseReference = firebaseDatabase.getReferenceFromUrl(eventPath);

        //Log.d("mark", deleteStarredDatabaseReference.toString() );



        titleUpdate = (EditText) view.findViewById(R.id.idTitleUpdate);
        contentUpdate = (EditText) view.findViewById(R.id.idContentUpdate);
        timeUpdate = (EditText) view.findViewById(R.id.idTimeUpdate);
        placeUpdate = (EditText) view.findViewById(R.id.idPlaceUpdate);
        organizeUpdate = (EditText) view.findViewById(R.id.idOrganizeUpdate);
        imageNameUpdate = (TextView) view.findViewById(R.id.TextImageNameUpdate);
        fileNameUpdate = (TextView) view.findViewById(R.id.TextFileNameUpdate);
        uploadImageButton = (Button) view.findViewById(R.id.buttonImageUpdate);
        uploadFileButton = (Button) view.findViewById(R.id.buttonFileUpdate);
        saveButton = (Button) view.findViewById(R.id.saveActivityButton);
        deleteButton = (Button)view.findViewById(R.id.deleteActivityButton);
        exitUpdateEventImageButton = (ImageButton) view.findViewById(R.id.exitUpdateEventImageButton);
        receiveStarredPath();

        eventPathValueEventListener = eventPathDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                titleUpdate.setText(event.getTitle());
                contentUpdate.setText(event.getContent());
                timeUpdate.setText(event.getTime());
                placeUpdate.setText(event.getPlace());
                organizeUpdate.setText(event.getOrganize());
                fileNameUpdate.setText(event.getFileURL());
                imageNameUpdate.setText(event.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    imageNameUpdate.setText(null);
                    selectImage();
                }
                else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 7);
                }
            }
        });

        uploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    fileNameUpdate.setText(null);
                    selectPdf();
                }
                else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        myProfileValueEventListener = myProfileDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                //organizeUpdate.setText(event.getOrganize());
                organizeUpdate.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pdfUri != null || fileNameUpdate.toString() != null){
                    uploadFileButton(pdfUri);
                    ((NavigationHost) getActivity()).backTo();
                }




//                if (imageUri != null || imageNameUpdate.toString() != null) {
//                    uploadImageButton(imageUri);
//                    ((NavigationHost) getActivity()).backTo();
//
//               }else if(pdfUri != null || fileNameUpdate.toString() != null){
//                    uploadFileButton(pdfUri);
//                    ((NavigationHost) getActivity()).backTo();
//                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                databaseReference1=firebaseDatabase.getReference().child("Starred");
//                childEventListener = databaseReference1.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
//                        for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                            userStatus = childSnapshot.getKey();
//                            if(userStatus.equals(titlestarred)){
//                                xx=childSnapshot.getRef();
//                                Log.d("love", xx.toString());
//                                if(xx != null){
//                                    xx.removeValue();
//                                }
//                                deleteStarredDatabaseReference=firebaseDatabase.getReferenceFromUrl(xx.toString());
//                                Log.d("lemon", deleteStarredDatabaseReference.toString());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

                if(xx != null){
                    xx.removeValue();
                }
                deleteEventDatabaseReference.removeValue();

                ((NavigationHost) getActivity()).passStringTo(new DisplayFeedEventFragment(), eventPath, true);
            }
        });


        exitUpdateEventImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationHost) getActivity()).backTo();
            }
        });

        Log.d("lifechk upEvn","UpdateEventFragment onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk upEvn","UpdateEventFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk upEvn","UpdateEventFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk upEvn","UpdateEventFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk upEvn","UpdateEventFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk upEvn","UpdateEventFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (myProfileValueEventListener != null) {
            myProfileDatabaseReference.removeEventListener(myProfileValueEventListener);
        }

        if (eventPathValueEventListener != null) {
            eventPathDatabaseReference.removeEventListener(eventPathValueEventListener);
        }

        Log.d("lifechk upEvn","UpdateEventFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk upEvn","UpdateEventFragment onDestroy");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPdf();
        }
        else if(requestCode == 7 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        }
        else {
            Toast.makeText(getContext(), "please provide permisiion", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    public void selectImage() {
        Intent chooseProfileImage = new Intent(Intent.ACTION_GET_CONTENT);
        chooseProfileImage.setType("image/*"); //applicatiob/* audio/*
        chooseProfileImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(chooseProfileImage, "Select Image"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Log.d("sunny", imageUri.toString());
            Log.d("sunny1", imageNameUpdate.toString());
            Toast.makeText(getContext(), "Successful upload image", Toast.LENGTH_SHORT).show();
            //imageNameUpdate.setText("A Image is selected : "+ data.getData().getPath());
        }
        else if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            Log.d("kmitl",  pdfUri.toString());
            Log.d("kmitl1", fileNameUpdate.toString());
            Toast.makeText(getContext(), "Successful upload file", Toast.LENGTH_SHORT).show();
            //fileNameUpdate.setText("A File is selected : "+ data.getData().getPath());
        }
        else {
            Toast.makeText(getContext(), "Please Select a file", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadFileButton(Uri pdfUri) {
        //final String workcount = "work" + number;
        title = titleUpdate.getText().toString();
        content = contentUpdate.getText().toString();
        time = timeUpdate.getText().toString();
        place = placeUpdate.getText().toString();
        organize = organizeUpdate.getText().toString();

        fileReference = firebaseStorage.getReference().child(firebaseAuth.getUid()).child("FileAdmin").child(titlestarred).child("file");

        if(pdfUri == null){
            imageURL=imageNameUpdate.getText().toString();
            fileURL = fileNameUpdate.getText().toString();
            event = new Event(content, fileURL, imageURL, organize, place, time, title);
            eventPathDatabaseReference.setValue(event);
            uploadImageButton(imageUri);

        }else if(pdfUri != null) {
            uploadTaskFile = fileReference.putFile(pdfUri);
            Task<Uri> urlTask = uploadTaskFile.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        fileURL = task.getResult().toString();
                        event = new Event(content, fileURL, imageURL, organize, place, time, title);
                        eventPathDatabaseReference.setValue(event);
                    }
                    else {
                        // Handle failures
                        // ...
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "File not Succesfully upload", Toast.LENGTH_SHORT).show();
                }
            });
            uploadImageButton(imageUri);
        }

    }

    public void uploadImageButton(Uri imageUri) {
        //final String workcount = "work" + number;
        //imageReference = firebaseStorage.getReference().child(firebaseAuth.getUid()).child("AdminImage").child(workcount).child("image");
        title = titleUpdate.getText().toString();
        content = contentUpdate.getText().toString();
        time = timeUpdate.getText().toString();
        place = placeUpdate.getText().toString();
        organize = organizeUpdate.getText().toString();


        imageReference = firebaseStorage.getReference().child(firebaseAuth.getUid()).child("AdminImage").child(titlestarred).child("image");

        if(imageUri == null) {
            imageURL=imageNameUpdate.getText().toString();
            //Log.d("kimmy", imageURL);
            event = new Event(content, fileURL, imageURL, organize, place, time, title);
            eventPathDatabaseReference.setValue(event);
            //uploadFileButton(pdfUri);

        }else if(imageUri != null){
            uploadTaskImage = imageReference.putFile(imageUri);
            Task<Uri> urlTask = uploadTaskImage.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        imageURL = task.getResult().toString();
                        event = new Event(content, fileURL, imageURL, organize, place, time, title);
                        eventPathDatabaseReference.setValue(event);
                    }
                    else {
                        // Handle failures
                        // ...
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "File not Succesfully upload", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void receiveStarredPath() {

        deleteeventPathValueEventListener = myTitleDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                titlestarred=dataSnapshot.getKey();
                receiveStarredPath1();
                Log.d("semen", titlestarred);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void receiveStarredPath1() {

        databaseReference1=firebaseDatabase.getReference().child("Starred");
        childEventListener = databaseReference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    userStatus = childSnapshot.getKey();
                    if(userStatus.equals(titlestarred)){
                        xx=childSnapshot.getRef();
                        Log.d("kitty", xx.toString());
                        deleteStarredDatabaseReference=firebaseDatabase.getReferenceFromUrl(xx.toString());
                        Log.d("lemon", deleteStarredDatabaseReference.toString());
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

    }

}
