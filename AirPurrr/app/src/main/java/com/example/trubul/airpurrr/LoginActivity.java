package com.example.trubul.airpurrr;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mklimek.sslutilsandroid.SslUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;


public class LoginActivity extends BaseActivity implements
        View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private static EditText mEmailField;
    private static EditText mPasswordField;

    private FirebaseAuth mAuth;
//    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mEmailField = findViewById(R.id.input_email);
        mPasswordField = findViewById(R.id.input_password);

        // Buttons
        findViewById(R.id.button_login).setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
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
//            URI url = new URI("http://192.168.0.248/?" + params[0]);
//            URI url = new URI("https://89.70.85.249:2137/?" + params[0]);
//            URL url = new URL("https://89.70.85.249:2137/?" + params[0]);

            URL url = new URL("https://89.70.85.249:2137");
//            HttpPost request = LoginActivity.sendPOST(url, getRequest());

            List<String> data = LoginActivity.sendPOST();


//            URL url = new URL(urlStr);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDefaultHostnameVerifier(new NullHostNameVerifier());

            // Create the SSL connection
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
//            sc.init(null, null, new java.security.SecureRandom());

            sc.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());

            sc = SslUtils.getSslContextForCertificateFile(context, "mycert1024.cer");
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setDefaultSSLSocketFactory(sc.getSocketFactory());

            Log.d(TAG, "PO SSL SOCKECIE");
//
//            Response response = getHttpClient().newCall(request).execute();


            // Use this if you need SSL authentication
            String userpass = data.get(0) + ":" + data.get(1);
            String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
            conn.setRequestProperty("Authorization", basicAuth);
//            conn.setRequestProperty("req", params[0]);

            Log.d(TAG, "PO BASIC AUTH");

            // set Timeout and method
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            Log.d(TAG, "PO USTAWIENIU TIMEOUTOW");


            return conn;

        }
//        catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

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
            Toast.makeText(context, "Serwer nie odp, spróbuj ponownie później", Toast.LENGTH_LONG).show();
//            throw new CustomException("bum");
        }

        return null;
    }



    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
//                            sendLoginPassword();

                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(myIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    public static List<String> sendPOST() {
//        request.setURI(url);

//        try {

        List<String> data = new ArrayList<>(2);

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        data.add(email);
        data.add(password);

        return data;


//            List nameValuePairs = new ArrayList(1);
//            nameValuePairs.add(new BasicNameValuePair("login", mEmailField.getText().toString()));
//            nameValuePairs.add(new BasicNameValuePair("password", mPasswordField.getText().toString()));
//            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));



//            return req;
//        }
//
//        catch (IOException e) {
//            Log.e(TAG, "Error sending ID token to backend.", e);
//        }
//
//        return null;
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
