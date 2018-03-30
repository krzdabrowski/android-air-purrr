package com.example.trubul.airpurrr;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

public class SwitchListener implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "SwitchListener";
    private static final String WORKSTATE_URL_LOCAL = "http://192.168.0.248/workstate.txt";
    private static final String WORKSTATE_URL_REMOTE = "http://89.70.85.249:2138/workstate.txt";
    private boolean flagLastUseAuto = false;
    private boolean flagLastUseManual = false;
    public boolean flagStateAuto = false;
    private boolean flagStateManual = false;
    private boolean isWorkingOnAuto = false;
    private Context mContext;
    private WorkingMode mode;
    private MyCallback mCallback;
    private final Activity mActivity;
    private String workstateURL;

    public enum WorkingMode {
        AUTO,
        MANUAL
    }


    public interface MyCallback {
        void setSwitchAuto(boolean state);

        void setSwitchManual(boolean state);

        PMDataResults getPMDataDetectorResults();

        Double[] getCurrentPMDetector();
    }

    public boolean isFlagLastUseAuto() {
        Log.d(TAG, "isFlagLastUseAuto W SWITCH_IS: " + flagLastUseAuto);
        return flagLastUseAuto;

    }

    public boolean isFlagLastUseManual() {
        Log.d(TAG, "isFlagLastUseManual W SWITCH_IS: " + flagLastUseManual);

        return flagLastUseManual;

    }


    public SwitchListener(Context context, WorkingMode mode, MyCallback callback, Activity activity) {
        mContext = context;
        this.mode = mode;
        this.mCallback = callback;
        mActivity = activity;

        if (!MainActivity.flagLocalRemote) {  // if local
            workstateURL = WORKSTATE_URL_LOCAL;
        } else {  // if remote
            workstateURL = WORKSTATE_URL_REMOTE;
        }
    }

    private void controlRequests(boolean state, String workstateURL) {
        String res;

        AbortableRequest switchOn = new AbortableRequest(mContext);
        AbortableRequest switchOff = new AbortableRequest(mContext);

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            res = getRequest.execute(workstateURL).get();

            if (res.equals("WorkStates.Sleeping\n")) {
                Toast.makeText(mContext, "Przetwarzam żądanie...", Toast.LENGTH_LONG).show();
                if (state) {  // send request if it was switch -> ON
                    switchOn.execute(mode + "=1");  // it will be POST: req = params[0]

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

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Toast.makeText(mContext, "Serwer nie odpowiada, spróbuj ponownie później", Toast.LENGTH_LONG).show();
            keepState();
        }
    }

    public void keepState() {
        if (flagLastUseAuto) {
            mCallback.setSwitchAuto(!flagStateAuto);
            isWorkingOnAuto = !isWorkingOnAuto;
            flagStateAuto = !flagStateAuto;
        } else {
            mCallback.setSwitchManual(!flagStateManual);
            flagStateManual = !flagStateManual;
        }
    }




    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        // Automatic mode - turn on the fan if any of PM2.5/10 will be higher than threshold (default: 100%)
        if (mode.equals(WorkingMode.AUTO)) {
            autoMode(isChecked);
        }

        // Manual mode - control anytime!
        else {
            flagLastUseAuto = false;
            flagLastUseManual = true;

            if (isChecked) {
                flagStateManual = true;
                controlRequests(flagStateManual, workstateURL);
            } else {
                flagStateManual = false;
                controlRequests(flagStateManual, workstateURL);
            }
        }

    }

    public void autoMode(boolean isChecked) {

        if (isChecked) {  // rusza maszyna
            flagLastUseAuto = true;
            flagLastUseManual = false;

            flagStateAuto = true;

            if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 2 && isWorkingOnAuto) {
                // do nothing
            }
            else if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 2 && !isWorkingOnAuto) {
                isWorkingOnAuto = true;
                controlRequests(true, workstateURL);
            } else if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 1 && isWorkingOnAuto) {
                isWorkingOnAuto = false;
                controlRequests(false, workstateURL);
            } else if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 1 && !isWorkingOnAuto) {
                // it does not exceed the threshold
            } else if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 0) {  // if null
                Toast.makeText(mContext, "Serwer nie odpowiada, spróbuj ponownie później (flagTriState = 0)", Toast.LENGTH_LONG).show();
                mCallback.setSwitchAuto(false);
            }
        }

        else {
            flagStateAuto = false;
            if (isWorkingOnAuto) {
                isWorkingOnAuto = false;
                controlRequests(false, workstateURL);
            }
        }
    }


}
