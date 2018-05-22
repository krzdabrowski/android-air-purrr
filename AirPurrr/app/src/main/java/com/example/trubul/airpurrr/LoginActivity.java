package com.example.trubul.airpurrr;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trubul.airpurrr.BaseActivity;
import com.example.trubul.airpurrr.FingerprintHelper;
import com.example.trubul.airpurrr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity implements FingerprintHelper.FingerprintCallback {

    private static final String TAG = "LoginActivity";
    static final String SAVED_EMAIL_KEY = "login_email";
    static final String SAVED_PASSWORD_KEY = "login_password";
    private String mSavedEmail;
    private String mSavedPassword;
    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth;
    private FingerprintHelper mFingerprintHelper;
    private InputMethodManager mInputMethodManager;
    private SharedPreferences mSharedPreferences;


    String getEmail() {
        return mEmailField.getText().toString().trim();
    }
    String getPassword() {
        return mPasswordField.getText().toString().trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = findViewById(R.id.input_email);
        mPasswordField = findViewById(R.id.input_password);
        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(getEmail(), getPassword());
            }
        });
        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
            FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);
            mFingerprintHelper = new FingerprintHelper(fingerprintManager, this);
            mInputMethodManager = getSystemService(InputMethodManager.class);

            if (mFingerprintHelper.isFingerprintAuthAvailable()) {
                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                mSavedEmail = mSharedPreferences.getString(SAVED_EMAIL_KEY, null);
                mSavedPassword = mSharedPreferences.getString(SAVED_PASSWORD_KEY, null);

                if (!keyguardManager.isKeyguardSecure()) {  // show a message that the user hasn't set up a fingerprint or lock screen
                    Toast.makeText(this, R.string.login_no_secure_screen, Toast.LENGTH_LONG).show();
                }
                if (!fingerprintManager.hasEnrolledFingerprints()) {  // this happens when no fingerprints are registered
                    Toast.makeText(this, R.string.login_no_saved_fingerprints, Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFingerprintHelper.isFingerprintAuthAvailable() && mSavedEmail != null) {
            mFingerprintHelper.startListening();
        } else {
            activateKeyboard();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFingerprintHelper.isFingerprintAuthAvailable()) {
            mFingerprintHelper.stopListening();
        }
    }

    private boolean isFormFilled(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            valid = false;
            mEmailField.setError(getString(R.string.login_required));
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            valid = false;
            mPasswordField.setError(getString(R.string.login_required));
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private boolean isInternetConnection() {
        boolean valid = true;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            valid = false;
            Toast.makeText(this, R.string.login_internet_error, Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    private void signIn(final String email, final String password) {
        if (!isInternetConnection() || !isFormFilled(email, password)) {
            return;
        }

        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(SAVED_EMAIL_KEY, email);
                    editor.putString(SAVED_PASSWORD_KEY, password);
                    editor.apply();

                    startActivity(intent);
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, R.string.login_auth_error, Toast.LENGTH_SHORT).show();
                }

                hideProgressDialog();
            }});
    }

    private void activateKeyboard() {
        mPasswordField.requestFocus();
        mPasswordField.postDelayed(mShowKeyboardRunnable, 500);  // show the keyboard
        mFingerprintHelper.stopListening();
    }

    private final Runnable mShowKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            mInputMethodManager.showSoftInput(mPasswordField, 0);
        }
    };

    @Override
    public void onError() {
        Log.d(TAG, "onError: ");
        activateKeyboard();
    }

    @Override
    public void onHelp(CharSequence helpString) {
        Log.d(TAG, "onHelp: ");
        Toast.makeText(this, helpString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed() {
        Log.d(TAG, "onFailed: ");
        Toast.makeText(this, R.string.login_fingerprint_not_recognized, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticated() {
        Log.d(TAG, "onAuthenticated: ");
        signIn(mSavedEmail, mSavedPassword);
    }
}