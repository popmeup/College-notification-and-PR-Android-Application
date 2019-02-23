package com.telecom.project4t.testactivity;


import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.telecom.project4t.testactivity.NavigationHost;
import com.telecom.project4t.testactivity.R;
import com.telecom.project4t.testactivity.UserProfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    Bundle args;
    ImageButton exitRegisterImageButton;
    Button signUpRegisterButton;
    ImageView registerProfileImage;
    RadioButton userRadioButton;
    RadioButton adminRadioButton;
    TextInputLayout idRegLayoutText;
    TextInputEditText idRegEditText;
    TextInputLayout firstnameRegLayoutText;
    TextInputEditText firstnameRegEditText;
    TextInputLayout surnameRegLayoutText;
    TextInputEditText surnameRegEditText;
    TextInputLayout emailRegLayoutText;
    TextInputEditText emailRegEditText;
    TextInputLayout passwordRegLayoutText;
    TextInputEditText passwordRegEditText;
    TextInputLayout orgRegLayoutText;
    TextInputEditText orgRegEditText;
    TextInputLayout orgemailRegLayoutText;
    TextInputEditText orgemailRegEditText;
    TextInputLayout orgphoneRegLayoutText;
    TextInputEditText orgphoneRegEditText;
    TextInputLayout orgparentRegLayoutText;
    TextInputEditText orgparentRegEditText;
    ConstraintLayout registerLayout;
    ConstraintSet set;
    ConstraintLayout.LayoutParams orgRegEditTextParams,
            orgemailRegEditTextParams,
            orgphoneRegEditTextParams,
            orgparentRegEditTextParams,
            orgRegLayoutTextParams,
            orgemailRegLayoutTextParams,
            orgphoneRegLayoutTextParams,
            orgparentRegLayoutTextParams;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myProfileDatabaseReference;
    StorageReference imageReference;

    RoundedBitmapDrawable roundDrawable;
    UploadTask uploadTask;
    UserProfile userProfile;
    Bitmap bitmap, largeBitmap;
    Uri imagePath;
    ByteArrayOutputStream imageBytesSteam, largeImageBytesSteam;
    String id, name, surname, password, email, orgName, orgEmail, orgPhone, orgParent, profileImage, status = "user";
    boolean editProfile = false;
    String stringData;
    int verticalMargin, registerProfileImageWidth, registerProfileImageHeight;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("lifechk reg","RegisterFragment onCreate");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        args = getArguments();
        if (args != null) {
            stringData = args.getString("passYeah");
            if (stringData.equals("editProfile")) {
                editProfile = true;
            }
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        verticalMargin = ((NavigationHost) getActivity()).dptopx(48);
        registerProfileImageWidth = ((NavigationHost) getActivity()).dptopx(120);
        registerProfileImageHeight = ((NavigationHost) getActivity()).dptopx(120);

        exitRegisterImageButton = (ImageButton) view.findViewById(R.id.exitRegisterImageButton);
        signUpRegisterButton = (Button) view.findViewById(R.id.signUpRegisterButton);
        registerProfileImage = (ImageView) view.findViewById(R.id.registerProfileImage);
        userRadioButton = (RadioButton) view.findViewById(R.id.userRadioButton);
        adminRadioButton = (RadioButton) view.findViewById(R.id.adminRadioButton);
        idRegEditText = (TextInputEditText) view.findViewById(R.id.idRegEditText);
        firstnameRegEditText = (TextInputEditText) view.findViewById(R.id.firstnameRegEditText);
        surnameRegEditText = (TextInputEditText) view.findViewById(R.id.surnameRegEditText);
        emailRegEditText = (TextInputEditText) view.findViewById(R.id.emailRegEditText);
        passwordRegEditText = (TextInputEditText) view.findViewById(R.id.passwordRegEditText);
        passwordRegLayoutText = (TextInputLayout) view.findViewById(R.id.passwordRegLayoutText);
        registerLayout = (ConstraintLayout) view.findViewById(R.id.registerLayout);
        set = new ConstraintSet();
        orgTextField();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        setEditProfileData();

        registerProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseProfileImage = new Intent(Intent.ACTION_GET_CONTENT);
                chooseProfileImage.setType("image/*"); //applicatiob/* audio/*
                chooseProfileImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(chooseProfileImage, "Select Image"), 1);
            }
        });

        userRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = "user";

                orgTextFieldRemove();
            }
        });

        adminRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = "admin";

                orgTextFieldAdd();
            }
        });

        signUpRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).hideKeyboard(getActivity());

                if (validate() && editProfile == false) {
                    //Upload data to the database
                    String regEmailCheck = emailRegEditText.getText().toString().trim();
                    String regPasswordCheck = passwordRegEditText.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(regEmailCheck, regPasswordCheck).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendEmailVerification();
                                Toast.makeText(getActivity(), "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
                                ((NavigationHost) getActivity()).backTo();
                            } else {
                                Toast.makeText(getActivity(), "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (validate() && editProfile == true) {
                    sendUserData();
                    editProfile = false;
                    ((NavigationHost) getActivity()).backTo();
                }
            }
        });

        exitRegisterImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).hideKeyboard(getActivity());

                if (editProfile == false) {
                    ((NavigationHost) getActivity()).backTo();
                }
                if (editProfile == true) {
                    editProfile = false;
                    ((NavigationHost) getActivity()).backTo();
                }
            }
        });

        Log.d("lifechk reg","RegisterFragment onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk reg","RegisterFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk reg","RegisterFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk reg","RegisterFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk reg","RegisterFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk reg","RegisterFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("lifechk reg","RegisterFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk reg","RegisterFragment onDestroy");
    }

    public void setEditProfileData() {
        if (editProfile == true) {
            signUpRegisterButton.setText(getString(R.string.apply_button));
            myProfileDatabaseReference = firebaseDatabase.getReference().child("Profile").child(firebaseAuth.getUid());

            myProfileDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    idRegEditText.setText(userProfile.getuserId());
                    firstnameRegEditText.setText(userProfile.getuserName());
                    surnameRegEditText.setText(userProfile.getuserSurname());
                    emailRegEditText.setText(userProfile.getuserEmail());
                    passwordRegEditText.setText("xxxxxxxx");
                    status = userProfile.getuserStatus();
                    orgRegEditText.setText(userProfile.getuserOrganization());
                    orgemailRegEditText.setText(userProfile.getuserOrganizationEmailAddress());
                    orgphoneRegEditText.setText(userProfile.getuserOrganizationPhoneNumber());
                    orgparentRegEditText.setText(userProfile.getuserParentOrganization());

                    if (status.equals("user")) {
                        userRadioButton.setChecked(true);

                    }
                    if (status.equals("admin")) {
                        adminRadioButton.setChecked(true);
                        orgTextFieldRemove();
                        orgTextFieldAdd();
                    }
                    userRadioButton.setEnabled(false);
                    adminRadioButton.setEnabled(false);
                    emailRegEditText.setEnabled(false);
                    passwordRegEditText.setEnabled(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });

            try {
                final File profileImage = File.createTempFile("Images", "bmp");
                storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getFile(profileImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        largeImageBytesSteam = new ByteArrayOutputStream();
                        imageBytesSteam = new ByteArrayOutputStream();
                        largeBitmap = BitmapFactory.decodeFile(profileImage.getAbsolutePath());
                        largeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, largeImageBytesSteam);
                        bitmap = ((NavigationHost) getActivity()).decodeSampledBitmapFromResource(largeImageBytesSteam.toByteArray(), registerProfileImageWidth, registerProfileImageHeight);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, imageBytesSteam);
                        roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                        roundDrawable.setCircular(true);
                        registerProfileImage.setImageDrawable(roundDrawable);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void orgTextField() {
        orgRegLayoutText = new TextInputLayout(getActivity());
        orgRegLayoutTextParams = new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        orgRegEditText = new TextInputEditText(getActivity());
        orgRegEditTextParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        orgemailRegLayoutText = new TextInputLayout(getActivity());
        orgemailRegLayoutTextParams = new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        orgemailRegEditText = new TextInputEditText(getActivity());
        orgemailRegEditTextParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        orgphoneRegLayoutText = new TextInputLayout(getActivity());
        orgphoneRegLayoutTextParams = new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        orgphoneRegEditText = new TextInputEditText(getActivity());
        orgphoneRegEditTextParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        orgparentRegLayoutText = new TextInputLayout(getActivity());
        orgparentRegLayoutTextParams = new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        orgparentRegEditText = new TextInputEditText(getActivity());
        orgparentRegEditTextParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        orgRegEditText.setId(View.generateViewId());
        orgemailRegEditText.setId(View.generateViewId());
        orgphoneRegEditText.setId(View.generateViewId());
        orgparentRegEditText.setId(View.generateViewId());

        orgRegEditText.setMaxLines(1);
        orgemailRegEditText.setMaxLines(1);
        orgphoneRegEditText.setMaxLines(1);
        orgparentRegEditText.setMaxLines(1);

        orgRegLayoutText.addView(orgRegEditText, 0);
        orgemailRegLayoutText.addView(orgemailRegEditText, 0);
        orgphoneRegLayoutText.addView(orgphoneRegEditText, 0);
        orgparentRegLayoutText.addView(orgparentRegEditText, 0);

        orgRegLayoutText.setId(View.generateViewId());
        orgemailRegLayoutText.setId(View.generateViewId());
        orgphoneRegLayoutText.setId(View.generateViewId());
        orgparentRegLayoutText.setId(View.generateViewId());

        orgRegLayoutText.setHint(getString(R.string.org_message));
        orgemailRegLayoutText.setHint(getString(R.string.org_email_message));
        orgphoneRegLayoutText.setHint(getString(R.string.org_phone_message));
        orgparentRegLayoutText.setHint(getString(R.string.org_parent_message));
    }

    public void orgTextFieldAdd(){
        registerLayout.addView(orgRegLayoutText, orgRegLayoutTextParams);
        registerLayout.addView(orgemailRegLayoutText, orgemailRegLayoutTextParams);
        registerLayout.addView(orgphoneRegLayoutText, orgphoneRegLayoutTextParams);
        registerLayout.addView(orgparentRegLayoutText, orgparentRegLayoutTextParams);

        set.clone(registerLayout);
        set.connect(orgRegLayoutText.getId(), ConstraintSet.TOP, passwordRegLayoutText.getId(), ConstraintSet.BOTTOM, 8);
        set.connect(orgRegLayoutText.getId(), ConstraintSet.START, registerLayout.getId(), ConstraintSet.START, verticalMargin);
        set.connect(orgRegLayoutText.getId(), ConstraintSet.END, registerLayout.getId(), ConstraintSet.END, verticalMargin);
        set.connect(orgemailRegLayoutText.getId(), ConstraintSet.TOP, orgRegLayoutText.getId(), ConstraintSet.BOTTOM, 8);
        set.connect(orgemailRegLayoutText.getId(), ConstraintSet.START, registerLayout.getId(), ConstraintSet.START, verticalMargin);
        set.connect(orgemailRegLayoutText.getId(), ConstraintSet.END, registerLayout.getId(), ConstraintSet.END, verticalMargin);
        set.connect(orgphoneRegLayoutText.getId(), ConstraintSet.TOP, orgemailRegLayoutText.getId(), ConstraintSet.BOTTOM, 8);
        set.connect(orgphoneRegLayoutText.getId(), ConstraintSet.START, registerLayout.getId(), ConstraintSet.START, verticalMargin);
        set.connect(orgphoneRegLayoutText.getId(), ConstraintSet.END, registerLayout.getId(), ConstraintSet.END, verticalMargin);
        set.connect(orgparentRegLayoutText.getId(), ConstraintSet.TOP, orgphoneRegLayoutText.getId(), ConstraintSet.BOTTOM, 8);
        set.connect(orgparentRegLayoutText.getId(), ConstraintSet.START, registerLayout.getId(), ConstraintSet.START, verticalMargin);
        set.connect(orgparentRegLayoutText.getId(), ConstraintSet.END, registerLayout.getId(), ConstraintSet.END, verticalMargin);
        set.applyTo(registerLayout);
    }

    public void orgTextFieldRemove(){
        registerLayout.removeView(orgRegLayoutText);
        registerLayout.removeView(orgemailRegLayoutText);
        registerLayout.removeView(orgphoneRegLayoutText);
        registerLayout.removeView(orgparentRegLayoutText);

        set.clone(registerLayout);
        set.applyTo(registerLayout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();

            try {
                largeBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagePath);
                largeImageBytesSteam = new ByteArrayOutputStream();
                largeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, largeImageBytesSteam);
                bitmap = ((NavigationHost) getActivity()).decodeSampledBitmapFromResource(largeImageBytesSteam.toByteArray(), 480,480);
                imageBytesSteam = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, imageBytesSteam);
                //registerProfileImage.setImageBitmap(bitmap); //set bitmap image instantly -froyo
                roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundDrawable.setCircular(true);
                registerProfileImage.setImageDrawable(roundDrawable);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Boolean validate(){
        id = idRegEditText.getText().toString();
        name = firstnameRegEditText.getText().toString();
        surname = surnameRegEditText.getText().toString();
        email = emailRegEditText.getText().toString();
        orgName = orgRegEditText.getText().toString();
        orgEmail = orgemailRegEditText.getText().toString();
        orgPhone = orgphoneRegEditText.getText().toString();
        orgParent = orgparentRegEditText.getText().toString();

        if (status.equals("user")) {
            if (id.isEmpty() || name.isEmpty() || surname.isEmpty() || email.isEmpty() || imagePath == null
                    || (!(userRadioButton.isChecked()) && !(adminRadioButton.isChecked()))) {
                Toast.makeText(getActivity(), "Please enter all the details", Toast.LENGTH_SHORT).show();
                return false;
            }
            else {
                return true;
            }
        }
        if (status.equals("admin")) {
            if (id.isEmpty() || name.isEmpty() || surname.isEmpty() || email.isEmpty() || imagePath == null
                    || (!(userRadioButton.isChecked()) && !(adminRadioButton.isChecked())) || orgName.isEmpty()
                    || orgEmail.isEmpty() || orgPhone.isEmpty() || orgParent.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter all the details", Toast.LENGTH_SHORT).show();
                return false;
            }
            else {
                return true;
            }
        }

        return false;
    }

    private void sendEmailVerification() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserData();
                        firebaseAuth.signOut();
                    }
                    else {
                        Toast.makeText(getActivity(), "Verification mail hasn't been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData() {
        myProfileDatabaseReference = firebaseDatabase.getReference().child("Profile").child(firebaseAuth.getUid());
        imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic");  //User id/Images/Profile Pic.jpg
        uploadTask = imageReference.putBytes(imageBytesSteam.toByteArray());

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                    profileImage = task.getResult().toString();

                    userProfile = new UserProfile(id, name, surname, email, status, orgName, orgEmail, orgPhone, orgParent, profileImage);
                    myProfileDatabaseReference.setValue(userProfile);
                }
                else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
}
