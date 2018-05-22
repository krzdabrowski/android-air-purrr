package com.example.trubul.airpurrr;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by krzysiek
 * On 5/22/18.
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHelper extends FingerprintManager.AuthenticationCallback {

    private static final String TAG = "MyFingerprintHelper";
    private final FingerprintManager mFingerprintManager;
    private final FingerprintCallback mCallback;
    private CancellationSignal mCancellationSignal;
    private boolean mSelfCancelled;

    interface FingerprintCallback {
        void onError();
        void onHelp(CharSequence helpString);
        void onFailed();
        void onAuthenticated();
    }


    FingerprintHelper(FingerprintManager fingerprintManager, FingerprintCallback callback) {
        mFingerprintManager = fingerprintManager;
        mCallback = callback;
    }

    boolean isFingerprintAuthAvailable() {
        return mFingerprintManager.isHardwareDetected() && mFingerprintManager.hasEnrolledFingerprints();
    }

    void startListening() {
        if (!isFingerprintAuthAvailable()) {
            return;
        }
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        mFingerprintManager.authenticate(null, mCancellationSignal, 0 /* flags */, this, null);
    }

    void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    // FingerprintManager.AuthenticationCallback
    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Log.d(TAG, "onAuthenticationError: ");
        if (!mSelfCancelled) {
            mCallback.onError();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Log.d(TAG, "onAuthenticationHelp: ");
        mCallback.onHelp(helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        Log.d(TAG, "onAuthenticationFailed: ");
        mCallback.onFailed();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Log.d(TAG, "onAuthenticationSucceeded: ");
        mCallback.onAuthenticated();
    }
}
