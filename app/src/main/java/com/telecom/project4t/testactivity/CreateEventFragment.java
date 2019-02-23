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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment extends Fragment {

    Button createEventButton;
    ImageButton exitCreateEventImageButton;
    EditText titleEditText,contentEditText,timeEditText,placeEditText,orgEditText;
    TextView imageName,fileName;
    Button uploadImage, uploadFile;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference ;
    DatabaseReference databaseReference1 ;
    DatabaseReference databaseReference2 ;
    StorageReference storageReference;
    StorageReference storageReference1;
    UploadTask uploadTaskFile;
    UploadTask uploadTaskImage;
    Event event;
    UserProfile userProfile;

    StorageReference fileReference;
    StorageReference imageReference;
    ChildEventListener childEventListener;

    Uri pdfUri;
    Uri imageUri;
    String title, content, time, place, organize, fileURL, imageURL;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("lifechk creEvn","CreateEventFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        titleEditText = (EditText) view.findViewById(R.id.idTitle);
        contentEditText = (EditText) view.findViewById(R.id.idContent);
        timeEditText = (EditText) view.findViewById(R.id.idTime);
        placeEditText = (EditText) view.findViewById(R.id.idPlace);
        orgEditText = (EditText) view.findViewById(R.id.idOrganize);
        imageName = (TextView) view.findViewById(R.id.TextImageName);
        fileName = (TextView) view.findViewById(R.id.TextFileName);
        uploadImage = (Button) view.findViewById(R.id.buttonImage);
        uploadFile = (Button) view.findViewById(R.id.buttonFile);
        createEventButton = (Button) view.findViewById(R.id.createEventButton);
        exitCreateEventImageButton = (ImageButton) view.findViewById(R.id.exitCreateEventImageButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = firebaseStorage.getReference();
        databaseReference1 = firebaseDatabase.getReference().child("AdminUpload");
        storageReference1 = firebaseStorage.getReference().child(firebaseAuth.getUid()).child("FileAdmin");

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    SelectImage();
                }
                else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},7);
                }

            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    SelectPDF();
                }
                else{
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);

                }

            }
        });

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pdfUri != null) {
                    uploadFile(pdfUri);
                    ((NavigationHost) getActivity()).backTo();
                }else{
                    uploadImage(imageUri);
                    ((NavigationHost) getActivity()).backTo();
                }
            }
        });

        exitCreateEventImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).backTo();
            }
        });

        databaseReference2 = firebaseDatabase.getReference().child("Profile").child(firebaseAuth.getUid());
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);
                orgEditText.setText(userProfile.getuserOrganization());
                orgEditText.setEnabled(false);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("lifechk creEvn","CreateEventFragment onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk creEvn","CreateEventFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk creEvn","CreateEventFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk creEvn","CreateEventFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk creEvn","CreateEventFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk creEvn","CreateEventFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("lifechk creEvn","CreateEventFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk creEvn","CreateEventFragment onDestroy");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SelectPDF();
        }
        else if(requestCode == 7 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SelectImage();
        }
        else {
            Toast.makeText(getContext(), "please provide permisiion", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadFile(Uri pdfUri) {

        title = titleEditText.getText().toString();
        content = contentEditText.getText().toString();
        time = timeEditText.getText().toString();
        place = placeEditText.getText().toString();
        organize = orgEditText.getText().toString();
        //databaseReference = firebaseDatabase.getReference().child("AdminUpload").child(firebaseAuth.getUid()).child(new SimpleDateFormat("ddMMyy-HHmmss").format(Calendar.getInstance().getTime()));
        databaseReference = firebaseDatabase.getReference().child("AdminUpload").child(organize).child(new SimpleDateFormat("ddMMyy-HHmmss").format(Calendar.getInstance().getTime()));
        fileReference = firebaseStorage.getReference().child(firebaseAuth.getUid()).child("FileAdmin").child(title).child("file");

        if(pdfUri == null){
            fileURL = fileName.getText().toString();
            event = new Event(content, fileURL, imageURL, organize, place, time, title);
            databaseReference.setValue(event);
            //uploadImage(imageUri);
        }else {
            uploadTaskFile = fileReference.putFile(pdfUri);
            Task<Uri> urlTask = uploadTaskFile.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        fileURL = task.getResult().toString();
                        event = new Event(content, fileURL, imageURL, organize, place, time, title);
                        databaseReference.setValue(event);
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
            uploadImage(imageUri);
        }

    }

    public void uploadImage(Uri imageUri) {

        title = titleEditText.getText().toString();
        content = contentEditText.getText().toString();
        time = timeEditText.getText().toString();
        place = placeEditText.getText().toString();
        organize = orgEditText.getText().toString();

        databaseReference = firebaseDatabase.getReference().child("AdminUpload").child(organize).child(new SimpleDateFormat("ddMMyy-HHmmss").format(Calendar.getInstance().getTime()));
        imageReference = firebaseStorage.getReference().child(firebaseAuth.getUid()).child("AdminImage").child(title).child("image");


        if(imageUri == null){
            imageURL = imageName.getText().toString();
            event = new Event(content, fileURL, imageURL, organize, place, time, title);
            databaseReference.setValue(event);

        }else {
            uploadTaskImage = imageReference.putFile(imageUri);
            Task<Uri> urlTask = uploadTaskImage.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        imageURL = task.getResult().toString();
                        event = new Event(content, fileURL, imageURL, organize, place, time, title);
                        databaseReference.setValue(event);
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

    public void SelectPDF() {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    public void SelectImage() {
        Intent chooseProfileImage = new Intent(Intent.ACTION_GET_CONTENT);
        chooseProfileImage.setType("image/*"); //applicatiob/* audio/*
        chooseProfileImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(chooseProfileImage, "Select Image"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri=data.getData();
            imageName.setText("A Image is selected : "+ data.getData().getPath());
        }
        else if(requestCode == 86 && resultCode == RESULT_OK && data!=null) {
            pdfUri=data.getData();
            fileName.setText("A File is selected : "+ data.getData().getPath());
        }
        else {
            Toast.makeText(getContext(),"Please Select a file",Toast.LENGTH_SHORT).show();
        }
    }

}
