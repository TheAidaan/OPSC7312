package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth _mAuth;

    EditText _username, _email, _confirmEmail, _password, _confirmPassword;

    SharedPreferences.Editor _sharedPrefsEdit;
    SharedPreferences _appSettingPrefs;

    FirebaseFirestore _database = FirebaseFirestore.getInstance();

    Boolean _isNightModeOn;
    Dialog _dialog;
    private GoogleSignInClient googleSignInClient;

    private final static int RC_SIGN_IN = 123;
    @Override
    protected void onStart() {
        super.onStart();

        //checks to see if a user is already logged in

        FirebaseUser user = _mAuth.getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _mAuth = FirebaseAuth.getInstance(); // Initializes FirebaseAuthenticator

        _appSettingPrefs = getSharedPreferences("AppSettingPrefs",0); // get the storage reference
        _sharedPrefsEdit = _appSettingPrefs.edit(); //set the storage editor reference

        _dialog = new Dialog(this);




        //region night mode for activity

        _isNightModeOn = _appSettingPrefs.getBoolean("NightMode",false); //check the variable status

        if(_isNightModeOn) // set the mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //night
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //day

        //endregion

        ImageButton btnLogo  = findViewById(R.id.btnlogo_welcome);
        btnLogo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChangeTheme();
            }
        });

        Button btnLogin  = findViewById(R.id.btnLogin_welcome);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowLoginPopUp();
            }
        });

        Button btnSignUp  = findViewById(R.id.btnSignup_welcome);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowSignUpPopUp();
            }
        });

        createRequest();
    }

    void ChangeTheme() {

        if(_isNightModeOn) // variable set in OnCreate
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            _sharedPrefsEdit.putBoolean("NightMode",false);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            _sharedPrefsEdit.putBoolean("NightMode",true);
        }
        _sharedPrefsEdit.apply();
    } // called by btnLogo

    void ShowLoginPopUp(){
        _dialog.setContentView(R.layout.login_popup);

        _email = (EditText) _dialog.findViewById(R.id.editTextEmail_LoginPopup);
        _password = (EditText) _dialog.findViewById(R.id.editTextPassword_LoginPopup);

        //region Cancel
        Button btnCancel = _dialog.findViewById(R.id.btnCancel_LoginPopup);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _dialog.dismiss();
            }
        });
        //endregion

        //region Login
        Button btnDone = _dialog.findViewById(R.id.btnDone_LoginPopup);
        btnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();

            }
        });
        //endregion

        //region google auth
        Button btnGoogle = _dialog.findViewById(R.id.btnLoginGoogle_LoginPopup);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signIn();
            }
        });
        //endregion

        _dialog.show();
    }//called by btnLogin

    private void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        //displays the google log in
    }

    void ShowSignUpPopUp(){
        _dialog.setContentView(R.layout.signup_popup);

        _username = (EditText) _dialog.findViewById(R.id.editTextUsername_signUpPopup);
        _email = (EditText) _dialog.findViewById(R.id.editTextEmail_signUpPopup);
        _confirmEmail = (EditText) _dialog.findViewById(R.id.editTextConfirmEmail_signUpPopup);
        _password = (EditText) _dialog.findViewById(R.id.editTextPassword_signUpPopup);
        _confirmPassword = (EditText) _dialog.findViewById(R.id.editTextConfirmPassword_signUpPopup);

        //region Cancel
        Button btnCancel = _dialog.findViewById(R.id.btnCancel_signUpPopup);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _dialog.dismiss();
            }
        });
        //endregion

        //region Login
        Button btnDone = _dialog.findViewById(R.id.btnDone_signUpPopup);
        btnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class); //replace with check login
                startActivity(intent);
            }
        });
        //endregion

        //region google auth
        //Button btnGoogle = _dialog.findViewById(R.id.btnSignUpGoogle_SignUpPopup);
       // btnGoogle.setOnClickListener(new View.OnClickListener() {
      //      public void onClick(View v) {
     //           Intent intent = new Intent(MainActivity.this, HomeActivity.class); //replace with check login
     //           startActivity(intent);
    //        }
   //     });
        //endregion

        _dialog.show();

    }//called by btnSignUp

    void createRequest(){
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Initializes google sign in client
    }

    private void login(){

        //Login for email and password

        String email = _email.getText().toString().trim();
        String password = _password.getText().toString().trim();

        if (email.isEmpty()){

            _email.setError("Email is required");
            _email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _email.setError("Please provide valid email");
            _email.requestFocus();

        }

        if (password.length() < 6){

            _password.setError("Min password length should be 6 characters!");
            _password.requestFocus();
            return;
        }


        _mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class); //replace with check login
                    startActivity(intent);

                }else{
                    Toast.makeText(MainActivity.this, "Failed to login, Please check your credentials", Toast.LENGTH_LONG).show();


                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.d("tag", "test" + task  );
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("tag", "account: " + account);
                addtoDatabase(account);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("tag", "failed");
            }
        }
    }

    private void addtoDatabase(GoogleSignInAccount acct){
        //      stores user information in database - Note: Google accounts do not have usernames.
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        _mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //      adds user to the database
                String username = null;
                String email = _mAuth.getCurrentUser().getEmail();

                User user = new User(username, email, _mAuth.getInstance().getUid() );
                _database.collection("Users").document(FirebaseAuth.getInstance().getUid()).set(user);
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Authorises with firebase, using google credentials.
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Log.d("tag", "" + credential);
        _mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = _mAuth.getCurrentUser();
                            Log.d("tag", "User: " + _mAuth.getCurrentUser());
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);


                        } else {
                            Toast.makeText(MainActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();


                        }


                        // ...
                    }
                });
    }
    private void registerUser(){
        // takes the edit text boxes and inserts their contents into strings
        Log.d("tag", "" + _email.getId());
        String email = _email.getText().toString().trim();
        String password = _password.getText().toString().trim();
        String confirmPassword = _confirmPassword.getText().toString().trim();
        String username = _username.getText().toString().trim();
        String confirmEmail = _confirmEmail.getText().toString().trim();


        // checks for potential user errors
        if (username.isEmpty()){
            _username.setError("Username is required");
            _username.requestFocus();
            return;
        }

        if (email.isEmpty()){
            _email.setError("email is required");
            _email.requestFocus();
            return;
        }
        if (confirmEmail.isEmpty()){
            _confirmEmail.setError("email is required");
            _confirmEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _email.setError("Please Provide a valid email!");
            _email.requestFocus();
            return;
        }
        if (!(email.equals(confirmEmail))){
            _email.setError("Emails do no match");
            _email.requestFocus();

            return;
        }
        if (password.isEmpty()){
            _password.setError("Password is required");
            _password.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()){
            _confirmPassword.setError("Password is required");
            _confirmPassword.requestFocus();
            return;
        }
        if (!(password.equals(confirmPassword))){
            _password.setError("Passwords do no match");
            _password.requestFocus();

            return;
        }
        Toast.makeText(MainActivity.this, "Registering User", Toast.LENGTH_LONG).show();
        _mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //adds users to the database
                    User user = new User(username, email, FirebaseAuth.getInstance().getUid() );
                    _database.collection("Users").document(FirebaseAuth.getInstance().getUid()).set(user);

                    Toast.makeText(MainActivity.this, "User has been registered succesfully!", Toast.LENGTH_LONG).show();

                }
            }
        });




    }


}