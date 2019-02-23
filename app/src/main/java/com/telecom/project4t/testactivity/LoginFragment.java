package com.telecom.project4t.testactivity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    TextView loginPageTitleText;
    TextInputEditText Email;
    TextInputEditText Password;
    TextInputLayout passwordErrorIndicator;
    Button loginButton;
    Button registerButton;
    TextView forgotPasswordTextButton;
    ConstraintLayout loginLayout;

    UserProfile userProfile;
    InputMethodManager inputManager;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    boolean pageState;
    int loginAttemptCount = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("lifechk log","LoginFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        loginPageTitleText = (TextView) view.findViewById(R.id.loginPageTitleText);
        Email = (TextInputEditText) view.findViewById(R.id.emailEditText);
        Password = (TextInputEditText) view.findViewById(R.id.passwordEditText);
        passwordErrorIndicator = (TextInputLayout) view.findViewById(R.id.passwordLayoutText);
        loginButton = (Button) view.findViewById(R.id.btnLogin);
        registerButton = (Button) view.findViewById(R.id.btnRegister);
        forgotPasswordTextButton = (TextView) view.findViewById(R.id.tvForgotPassword);
        loginLayout = (ConstraintLayout) view.findViewById(R.id.loginLayout);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).hideKeyboard(getActivity());

                if (pageState == false) {
                    loginButton.setEnabled(false);
                    String loginEmailCheck = Email.getText().toString().trim();
                    String loginPasswordCheck = Password.getText().toString().trim();

                    if (loginEmailCheck.equals("") || loginPasswordCheck.equals("")) {
                        loginButton.setEnabled(true);
                        Toast.makeText(getActivity(), "Please enter your Email and Password", Toast.LENGTH_SHORT).show();
                    }
                    else {
//                        readStarredButtonStateEmail();
//                        readStarredButtonStatePassword();
                          validate(Email.getText().toString(), Password.getText().toString(),view.toString());
                    }
                }
                else {
                    loginButton.setEnabled(false);
                    String resetEmailCheck = Email.getText().toString().trim();

                    if (resetEmailCheck.equals("")) {
                        loginButton.setEnabled(true);
                        Toast.makeText(getActivity(), "Please enter your Email", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        firebaseAuth.sendPasswordResetEmail(resetEmailCheck).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loginButton.setEnabled(true);
                                    Toast.makeText(getActivity(), "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                    backToLogin();
                                }
                                else {
                                    loginButton.setEnabled(true);
                                    Toast.makeText(getActivity(), "Error in sending password reset email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).hideKeyboard(getActivity());

                if (pageState == false) {
                    ((NavigationHost) getActivity()).navigateTo(new RegisterFragment(), true);
                }
                else {
                    backToLogin();
                }
            }
        });

        forgotPasswordTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageState = true;

                loginLayout.removeView(passwordErrorIndicator);
                loginLayout.removeView(forgotPasswordTextButton);

                loginPageTitleText.setText(R.string.reset_password_title);
                loginButton.setText(R.string.reset_password_button);
                registerButton.setText(R.string.cancel_reset_password_button);
            }
        });

        Log.d("lifechk log","LoginFragment onCreateView");

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk log","LoginFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk log","LoginFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk log","LoginFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk log","LoginFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk log","LoginFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("lifechk log","LoginFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk log","LoginFragment onDestroy");
    }

    private void backToLogin() {
        pageState = false;

        loginLayout.addView(passwordErrorIndicator);
        loginLayout.addView(forgotPasswordTextButton);

        loginPageTitleText.setText(R.string.app_name);
        loginButton.setText(R.string.login_button);
        registerButton.setText(R.string.sign_up_button);
    }

    private void validate(String Email, String Password, final String view){
        firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkEmailVerification(view);
                }
                else {
                    loginButton.setEnabled(true);
                    loginAttemptCount = loginAttemptCount - 1;
                    passwordErrorIndicator.setError(getString(R.string.error_password) + " " + loginAttemptCount);

                    if (loginAttemptCount == 0) {
                        loginButton.setEnabled(false); //set false for 1 min which will done later -froyo
                    }
                }
            }
        });
    }

    private void checkEmailVerification(String view) {
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();

        if (firebaseAuth != null) {
            if (firebaseUser.isEmailVerified()) {
                loginButton.setEnabled(true);
                Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                saveLoginState(true);
                ((NavigationHost) getActivity()).navigateTo(new DisplayFeedEventFragment(), false); // Navigate to the next Fragment
            } else {
                loginButton.setEnabled(true);
                Toast.makeText(getActivity(), "Verify your email", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }
        }
        else {
            Toast.makeText(getActivity(), "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveLoginState(Boolean isChecked) {
        SharedPreferences aSharedPreferenes = getActivity().getSharedPreferences(
                "LoginState",Context.MODE_PRIVATE);
        SharedPreferences.Editor aSharedPreferenesEdit = aSharedPreferenes
                .edit();
        aSharedPreferenesEdit.putBoolean("State" ,isChecked);
        aSharedPreferenesEdit.apply();
    }


}
