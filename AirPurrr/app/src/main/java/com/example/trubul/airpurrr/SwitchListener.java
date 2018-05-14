package com.example.trubul.airpurrr;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

class SwitchListener implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SwitchListener";
    private static final String WORKSTATE_URL = "http://89.70.85.249:2138/workstate.txt";
    private Context mContext;
    private SwitchCallback mCallback;
    private WorkingMode mMode;

    private boolean isLastUseAuto = false;
    private boolean isLastUseManual = false;
    boolean stateAuto = false;
    private boolean stateManual = false;
    private boolean isWorking = false;


    enum WorkingMode {
        AUTO,
        MANUAL
    }

    interface SwitchCallback {
        void setSwitchAuto(boolean state);
        void setSwitchManual(boolean state);
    }

    SwitchListener(Context context, SwitchCallback callback, WorkingMode mode) {
        mContext = context;
        mCallback = callback;
        mMode = mode;
    }

    private void controlRequests(boolean state) {
        String res;

        HttpsPostRequest switchOn = new HttpsPostRequest(mContext);
        HttpsPostRequest switchOff = new HttpsPostRequest(mContext);

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            res = getRequest.execute(WORKSTATE_URL).get();

            if (res.equals("WorkStates.Sleeping\n")) {
                Toast.makeText(mContext, R.string.switch_processing_the_request, Toast.LENGTH_LONG).show();
                if (state) {  // send request if it was switch -> ON
                    switchOn.execute(mMode + "=1");  // it will be POST: req = params[0]

                } else {
                    switchOff.execute(mMode + "=0");
                }
            } else if (res.equals("WorkStates.Measuring\n")) {
                Toast.makeText(mContext, R.string.switch_cannot_process, Toast.LENGTH_LONG).show();
                keepState();
            } else {
                Toast.makeText(mContext, R.string.switch_error_detector + "NoWorkStates)", Toast.LENGTH_LONG).show();
                keepState();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Toast.makeText(mContext, mContext.getString(R.string.switch_error_server) + "NullPointer)", Toast.LENGTH_LONG).show();
            keepState();
        }
    }

    private void keepState() {
        if (isLastUseAuto) {
            stateAuto = !stateAuto;
            mCallback.setSwitchAuto(stateAuto);
        } else {
            stateManual = !stateManual;
            mCallback.setSwitchManual(stateManual);
        }
        isWorking = !isWorking;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Automatic mode - turn on the fan if any of PM2.5/10 will be higher than threshold (default: 100%)
        if (mMode.equals(WorkingMode.AUTO)) {
            autoMode(isChecked);
        }

        // Manual mode - control anytime!
        else {
            isLastUseAuto = false;
            isLastUseManual = true;

            if (isChecked) {
                stateManual = true;
                isWorking = true;
                controlRequests(stateManual);
            } else {
                stateManual = false;
                isWorking = false;
                controlRequests(stateManual);
            }
        }
    }

    void autoMode(boolean isChecked) {
        if (isChecked) {
            stateAuto = true;
            isLastUseAuto = true;
            isLastUseManual = false;

            if (MainActivity.flagTriStateAuto == 2 && isWorking) {
                // Continue work
            } else if (MainActivity.flagTriStateAuto == 2 && !isWorking) {
                isWorking = true;
                controlRequests(true);
            } else if (MainActivity.flagTriStateAuto == 1 && isWorking) {
                isWorking = false;
                controlRequests(false);
            } else if (MainActivity.flagTriStateAuto == 1 && !isWorking) {
                // It does not exceed the threshold
            } else if (MainActivity.flagTriStateAuto == 0) {  // if null
                Toast.makeText(mContext, R.string.switch_error_server, Toast.LENGTH_LONG).show();
                mCallback.setSwitchAuto(false);
            }
        } else {
            stateAuto = false;
            if (isWorking) {
                isWorking = false;
                controlRequests(false);
            }
        }
    }

}
