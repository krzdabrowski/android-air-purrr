package com.example.trubul.airpurrr;

import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.core.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.trubul.airpurrr.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity implements LoginHelper.FingerprintCallback {

    private static final String TAG = "LoginActivity";
    static final String SAVED_HASH_EMAIL_KEY = "login_email";
    static final String SAVED_HASH_PASSWORD_KEY = "login_password";
    private String mHashedEmail;

//    private TextInputEditText mEmailField;
//    private TextInputEditText mPasswordField;
//    private Button mButton;
//    private CircleImageView mFingerprintIcon;
//    private TextView mFingerprintMessage;

    private FirebaseAuth mAuth;
    private LoginHelper mLoginHelper;
    private InputMethodManager mInputMethodManager;
    private SharedPreferences mSharedPreferences;

    static LocationService mLocationService;
    static boolean isLocation = false;
    static Intent mLocationIntent;

    private ActivityLoginBinding activityLoginBinding;


    String getEmail() {
        return activityLoginBinding.inputEmail.getText().toString().trim();
    }
    String getPassword() {
        return activityLoginBinding.inputPassword.getText().toString().trim();
    }

    static ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isLocation = true;
            LocationService.MyBinder binder = (LocationService.MyBinder) service;
            mLocationService = binder.getServiceSystem();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
//        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        // Location service init
        mLocationIntent = new Intent(this, LocationService.class);
        bindService(mLocationIntent, connection, Context.BIND_AUTO_CREATE);

//        mEmailField = findViewById(R.id.input_email);
//        mPasswordField = findViewById(R.id.input_password);
//        mButton = findViewById(R.id.button_login);
        activityLoginBinding.buttonLogin.setOnClickListener((view) -> manualLogin(getEmail(), getPassword()));

//        mFingerprintIcon = findViewById(R.id.fingerprint_icon);
//        mFingerprintMessage = findViewById(R.id.fingerprint_message);
        activityLoginBinding.fingerprintIcon.setVisibility(View.GONE);
        activityLoginBinding.fingerprintMessage.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        // If Android is at least Marshmallow (6.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
            FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);
            mLoginHelper = new LoginHelper(fingerprintManager, this);
            mInputMethodManager = getSystemService(InputMethodManager.class);

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            mHashedEmail = mSharedPreferences.getString(SAVED_HASH_EMAIL_KEY, null);

            // If phone has fingerprint reader and user has granted permission for an app
            if (mLoginHelper.isFingerprintAuthAvailable() && isFingerprintPermissionGranted()) {

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
        if (mLoginHelper.isFingerprintAuthAvailable() && isFingerprintPermissionGranted() && mHashedEmail != null) {
            activityLoginBinding.inputEmail.setVisibility(View.GONE);
            activityLoginBinding.inputPassword.setVisibility(View.GONE);
            activityLoginBinding.buttonLogin.setVisibility(View.GONE);
            activityLoginBinding.fingerprintIcon.setVisibility(View.VISIBLE);
            activityLoginBinding.fingerprintMessage.setVisibility(View.VISIBLE);

            mLoginHelper.startListening();
        } else {
            activateKeyboard();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLoginHelper.isFingerprintAuthAvailable() && isFingerprintPermissionGranted()) {
            mLoginHelper.stopListening();
        }
    }

    private boolean isFingerprintPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isFormFilled(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            valid = false;
            activityLoginBinding.inputEmail.setError(getString(R.string.login_required));
        } else {
            activityLoginBinding.inputEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            valid = false;
            activityLoginBinding.inputPassword.setError(getString(R.string.login_required));
        } else {
            activityLoginBinding.inputPassword.setError(null);
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

    private void manualLogin(final String email, final String password) {
        if (!isInternetConnection() || !isFormFilled(email, password)) {
            return;
        }

        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                SharedPreferences.Editor editor = mSharedPreferences.edit();

                mHashedEmail = LoginHelper.sha512Hash(email);
                String hashedPassword = LoginHelper.sha512Hash(password);
                editor.putString(SAVED_HASH_EMAIL_KEY, mHashedEmail);
                editor.putString(SAVED_HASH_PASSWORD_KEY, hashedPassword);
                editor.apply();

                startActivity(intent);
            } else {
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                Toast.makeText(LoginActivity.this, R.string.login_auth_error, Toast.LENGTH_SHORT).show();
            }

            hideProgressDialog();
        });
    }

    private void activateKeyboard() {
        activityLoginBinding.inputPassword.requestFocus();
        activityLoginBinding.inputPassword.postDelayed(mShowKeyboardRunnable, 500);  // show the keyboard
        mLoginHelper.stopListening();
    }

    private final Runnable mShowKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            mInputMethodManager.showSoftInput(activityLoginBinding.inputEmail, 0);
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
        showProgressDialog();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        hideProgressDialog();
    }
}