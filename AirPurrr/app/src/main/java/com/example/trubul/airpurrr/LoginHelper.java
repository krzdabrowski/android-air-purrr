package com.example.trubul.airpurrr;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@TargetApi(23)
class LoginHelper extends FingerprintManager.AuthenticationCallback {

    private static final String TAG = "LoginHelper";
    private FingerprintManager mFingerprintManager;
    private FingerprintCallback mCallback;
    private CancellationSignal mCancellationSignal;
    private boolean mSelfCancelled;

    interface FingerprintCallback {
        void onError();
        void onHelp(CharSequence helpString);
        void onFailed();
        void onAuthenticated();
    }

    LoginHelper(FingerprintManager fingerprintManager, FingerprintCallback callback) {
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

    static String sha512Hash(String toHash) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            hash = bytesToHex(bytes);  // this is ~55x faster than looping and String.formating()
        } catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
