package com.example.trubul.airpurrr;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

public class SwitchListener implements CompoundButton.OnCheckedChangeListener {

//    private static final String TAG = "SwitchListener";
    private static final String WORKSTATE_URL = "http://89.70.85.249:2138/workstate.txt";
//    private static final String WORKSTATE_URL = "http://192.168.0.248/workstate.txt";
    private boolean flagToggleAuto = true;

    public boolean isFlagToggleAuto() {
        return flagToggleAuto;
    }

    public boolean isFlagToggleManual() {
        return flagToggleManual;
    }

    private boolean flagToggleManual = false;
//    private HttpPost mRequestOn = new HttpPost();
//    private HttpPost mRequestOff = new HttpPost();
    private OkHttpClient mRequestOn = new OkHttpClient();
    private OkHttpClient mRequestOff = new OkHttpClient();

    private static final String TAG = "SwitchListener";

    private static boolean state;

    public static boolean isState() {
        return state;
    }

    private Context mContext;

    private WorkingMode mode;
    private MyCallback mCallback;

    public enum WorkingMode {
        AUTO,
        MANUAL
    }


    public interface MyCallback {
        void setSwitchAuto(boolean state);
        void setSwitchManual(boolean state);
        PMDataResults getPMDataDetectorResults();
    }


    public SwitchListener(Context context, WorkingMode mode, MyCallback callback) {
        mContext = context;
        this.mode = mode;
        this.mCallback = callback;
    }

    private void controlRequests(boolean state) {

        String res;

        AbortableRequest switchOn = new AbortableRequest(mContext);
        AbortableRequest switchOff = new AbortableRequest(mContext);

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            res = getRequest.execute(WORKSTATE_URL).get();

            if (res.equals("WorkStates.Sleeping\n")) {
                Toast.makeText(mContext, "Przetwarzam żądanie...", Toast.LENGTH_LONG).show();
                if (state) {  // send request if it was switch -> ON
                    switchOn.execute(mode + "=1"); // to moze tak byc, to pojdzie w req = params[0]

                } else {
                    switchOff.execute(mode + "=0");
                }
            } else if (res.equals("WorkStates.Measuring\n")) {
                Toast.makeText(mContext, "Nie mogę przetworzyć żądania - czujnik w trybie pomiarowym", Toast.LENGTH_LONG).show();
                keepState();
            } else {
                Toast.makeText(mContext, "Coś się popsuło i nie było mnie słychać", Toast.LENGTH_LONG).show();
                keepState();
            }


//            else {
//                Toast.makeText(mContext, "ZA DŁUGO", Toast.LENGTH_LONG).show();
//                if (mode.equals(WorkingMode.AUTO)) {
//                    mCallback.setSwitchAuto(keepState);
//                } else {
//                    mCallback.setSwitchManual(keepState);
//                }


        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            Toast.makeText(mContext, "Serwer nie odpowiada, spróbuj ponownie później" , Toast.LENGTH_LONG).show();
            keepState();
        }
    }

    public void keepState() {
        if (mode.equals(WorkingMode.AUTO)) {
            mCallback.setSwitchAuto(!flagToggleAuto);
        }
        else {
            mCallback.setSwitchManual(!flagToggleManual);
        }
    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        Log.d(TAG, "mode is: " + mode);

        // Automatic mode - turn on the fan if any of PM2.5/10 will be higher than threshold (default: 100%)
        if (mode.equals(WorkingMode.AUTO)) {
            // Initial flag configuration - multiple switching possible
            if (!flagToggleAuto) {
//                mRequestOn = new HttpPost();
//                mRequestOff = new HttpPost();
//                mRequestOn = new OkHttpClient();
//                mRequestOff = new OkHttpClient();
                flagToggleAuto = true;
            }

            if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 2)  // if true
                if (isChecked) {
                    controlRequests(true);
                }
                else {
//                    mRequestOn.abort();  // break while loop of working air purifier
//                    mRequestOn.cancel("req");
                    controlRequests(false);
                    flagToggleAuto = false;
                }
            else if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 1) {}  // if false
            else {  // if null
                Toast.makeText(mContext, "Serwer nie odpowiada, spróbuj ponownie później (flagTriState = 0)" , Toast.LENGTH_LONG).show();
                mCallback.setSwitchAuto(false);
            }
        }

        // Manual mode - control anytime!
        else {
            Log.d(TAG, "NACISNIECIE TUTAJ");
//            if (!flagToggle2) {
////                mRequestOn = new HttpPost();
////                mRequestOff = new HttpPost();
////                mRequestOn = new OkHttpClient();
////                mRequestOff = new OkHttpClient();
//                flagToggle2 = true;
//            }

            if (isChecked) {
                flagToggleManual = true;
                controlRequests(true);

            }
            else {
//                mRequestOn.abort();  // break while loop of working air purifier
//                mRequestOn.cancel(TAG);
                flagToggleManual = false;
                controlRequests(false);


            }
        }

    }
}
