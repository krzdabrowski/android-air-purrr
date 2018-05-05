package com.example.trubul.airpurrr;

import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
//    private HttpsPostRequest httpsPostRequest = new HttpsPostRequest(this);
    private EditText mEmailField;
    private EditText mPasswordField;
//    private String email;
//    private String password;
    private FirebaseAuth mAuth;


//    public String[] getEmailPassword() {
//        String[] emailPassword = new String[2];
//
//        email = mEmailField.getText().toString();
//        password = mPasswordField.getText().toString();
////        emailPassword.add(email);
////        emailPassword.add(password);
//        emailPassword[0] = email;
//        emailPassword[1] = password;
//
//        return emailPassword;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = findViewById(R.id.input_email);
        mPasswordField = findViewById(R.id.input_password);
        findViewById(R.id.button_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        String email = getEmail();
        String password = getPassword();
        signIn(email, password);
    }

    public String getEmail() {
        return mEmailField.getText().toString().trim();
    }

    public String getPassword() {
        return mPasswordField.getText().toString().trim();
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Wymagane");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Wymagane");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void signIn(String email, String password) {
        if (!validateForm(email, password)) {
            return;
        }
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, might implement update UI accordingly with FirebaseUser
                            Log.d(TAG, "signInWithEmail:success");

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("email", getEmail());
                            intent.putExtra("password", getPassword());
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Błąd uwierzytelnienia!", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }
}