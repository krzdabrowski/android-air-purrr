package com.example.trubul.airpurrr;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Toast;
import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

public class SwitchListener implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "SwitchListener";
    private static final String WORKSTATE_URL = "http://89.70.85.249:2138/workstate.txt";
//    private static final String WORKSTATE_URL = "http://192.168.0.248/workstate.txt";
    private boolean flagLastUseAuto = false;
    private boolean flagLastUseManual = false;
    private boolean flagStateAuto = false;
    private boolean flagStateManual = false;
    private Context mContext;
    private WorkingMode mode;
    private MyCallback mCallback;

    public enum WorkingMode {
        AUTO,
        MANUAL
    }

    public boolean isFlagLastUseAuto() {
        return flagLastUseAuto;
    }

    public boolean isFlagLastUseManual() {
        return flagLastUseManual;
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
            mCallback.setSwitchAuto(!flagStateAuto);
        }
        else {
            mCallback.setSwitchManual(!flagStateManual);
        }
    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Automatic mode - turn on the fan if any of PM2.5/10 will be higher than threshold (default: 100%)
        if (mode.equals(WorkingMode.AUTO)) {

            if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 2) { // if true
                flagLastUseAuto = true;
                flagLastUseManual = false;

                if (isChecked) {
                    flagStateAuto = true;
                    controlRequests(true);
                } else {
                    flagStateAuto = false;
                    controlRequests(false);
                }
            }
            else if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 1) {}  // if false
            else {  // if null
                Toast.makeText(mContext, "Serwer nie odpowiada, spróbuj ponownie później (flagTriState = 0)" , Toast.LENGTH_LONG).show();
                mCallback.setSwitchAuto(false);
            }
        }

        // Manual mode - control anytime!
        else {
            flagLastUseAuto = false;
            flagLastUseManual = true;

            if (isChecked) {
                flagStateManual = true;
                controlRequests(true);
            }
            else {
                flagStateManual = false;
                controlRequests(false);
            }
        }

    }
}
