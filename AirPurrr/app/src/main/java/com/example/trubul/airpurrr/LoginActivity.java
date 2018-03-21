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

public class LoginActivity extends BaseActivity implements View.OnClickListener, HttpsPostRequest.MyCallback {

    private static final String TAG = "LoginActivity";
    private HttpsPostRequest httpsPostRequest = new HttpsPostRequest(this);

    private EditText mEmailField;
    private EditText mPasswordField;
    private String email;
    private String password;
    private FirebaseAuth mAuth;


    public List<String> getEmailPassword() {
        List<String> emailPassword = new ArrayList<>(2);

        email = mEmailField.getText().toString();
        password = mPasswordField.getText().toString();
        emailPassword.add(email);
        emailPassword.add(password);

        return emailPassword;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = findViewById(R.id.input_email);
        mPasswordField = findViewById(R.id.input_password);
        findViewById(R.id.button_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        // First TLS setting - otherwise it doesn't verify even null-hostname (but don't send anything yet)
        httpsPostRequest.setRequest(this);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
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
//                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(myIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Błąd uwierzytelnienia!", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Wymagane");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Wymagane");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        email = mEmailField.getText().toString();
        password = mPasswordField.getText().toString();
        signIn(email, password);
    }

}
