package com.example.trubul.airpurrr;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
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
import com.mklimek.sslutilsandroid.SslUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends BaseActivity implements
        View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static EditText mEmailField;
    private static EditText mPasswordField;
    private FirebaseAuth mAuth;


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
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        doMagic(this);
    }


    public static HttpsURLConnection doMagic(Context context) {
        try {

            URL url = new URL("https://89.70.85.249:2137");

            String email = mEmailField.getText().toString();
            String password = mPasswordField.getText().toString();

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDefaultHostnameVerifier(new NullHostNameVerifier());

            // Create the SSL connection
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
            sc = SslUtils.getSslContextForCertificateFile(context, "mycert1024.cer");
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Use this if you need SSL authentication
            String userpass = email + ":" + password;
            String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
            conn.setRequestProperty("Authorization", basicAuth);

            // set Timeout and method
            conn.setReadTimeout(3000); // czas na cala reszte, responsy itd
            conn.setConnectTimeout(7000); // czas na polaczenie sie z IP
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            return conn;

        }
        catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "sdds " + e.getMessage());
        }
        catch (KeyManagementException e) {
            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
        }
        catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        }
        catch(SecurityException e) {
            Log.e(TAG, "doInBackground: Security Exception. Needs permission? " + e.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, "doInBackground: IO Exception reading data: " + e.getMessage());
        }

        return null;
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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(myIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();                        }
                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {

//            finish();


        } else {
            findViewById(R.id.button_login).setVisibility(View.VISIBLE);
            findViewById(R.id.input_email).setVisibility(View.VISIBLE);
            findViewById(R.id.input_password).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

}
