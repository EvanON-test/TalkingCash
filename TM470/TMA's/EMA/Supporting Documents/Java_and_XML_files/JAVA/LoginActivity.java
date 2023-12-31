package com.example.tm470talkingcash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * Provides the function to sign into the application using their registered email and password, and
 * also navigate to the registration activity if required
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private Button registerBtn;

    private Button loginBtn;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //sets the view to the login activity layout
        setContentView(R.layout.activity_login);
        //Gets the firebase instance for the firebase authentication service
        fAuth = FirebaseAuth.getInstance();
        //Finds the views based on their ID's
        emailText = findViewById(R.id.log_email_input);
        passwordText = findViewById(R.id.log_pass_input);
        loginBtn = findViewById(R.id.log_button);
        registerBtn = findViewById(R.id.reg_on_click);



        //Sets a click listener for the login button, calls the userAccountLogin method when clicked
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userAccountLogin();
            }
        });

        //Sets a click listener for the register button and directs to registration activity when clicked
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }





    /**
     * This method logs the user into the application if they have a valid email and password
     * that is stored in the authentication database
     *
     * FYI *changed from private to protected for testing*
     */
    protected void userAccountLogin(){
        String email;
        String password;

        //initializes the variables based oin their inputs
        email = Objects.requireNonNull(emailText.getText()).toString();
        password = Objects.requireNonNull(passwordText.getText()).toString();






        //Validates the inputs using methods imported from the UserValidation class
        if (!UserValidation.isValidEmail(email)){
            Toast.makeText(getApplicationContext(), "Please enter Valid Email", Toast.LENGTH_LONG).show();
            return;
        } else if (!UserValidation.isValidPassword(password)){
            Toast.makeText(getApplicationContext(), "Please conform to password requirements", Toast.LENGTH_LONG).show();
            return;
        }

        /**
         * Attempts to sign in to a user account based on the email and password input. If successful
         * the main activity launches with a affirmative message. If unsuccessful an informative
         * error message will be displayed.
         *
         * */
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                    FirebaseUser user = fAuth.getCurrentUser();
                    Intent intent = new Intent(LoginActivity.this, MainThreadActivity.class);//TODO: just a note that you've set this to the main thread after login works
                    intent.putExtra("userEmail", user.getEmail());
                    intent.putExtra("userUid", user.getUid());
                    if (user.getDisplayName()!=null){
                        intent.putExtra("userName", user.getDisplayName());
                    }
                    startActivity(intent);
                } else {
                    String errorMessage;
                    errorMessage = "Login Failed: ";
                    if (task.getException() != null){
                        errorMessage = errorMessage + task.getException().getMessage();
                    }
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //For AndroidTest Testing. However not utilised yet
    public void setFirebaseAuth(FirebaseAuth mockFirebaseAuth) {
        this.fAuth = mockFirebaseAuth;
    }
}
