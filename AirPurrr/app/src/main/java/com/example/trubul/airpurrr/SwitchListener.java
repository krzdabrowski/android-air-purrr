package com.example.trubul.airpurrr;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Toast;
import org.apache.http.client.methods.HttpGet;
import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

public class SwitchListener implements CompoundButton.OnCheckedChangeListener {

//    private static final String TAG = "SwitchListener";
//    private static final String WORKSTATE_URL_GLOBAL = "http://xxx.xxx.xxx.xxx:xxx/workstate.txt";
    private static final String WORKSTATE_URL = "http://192.168.0.248/workstate.txt";
    private boolean flagToggle1 = true;
    private boolean flagToggle2 = true;
    private HttpGet mRequestOn = new HttpGet();
    private HttpGet mRequestOff = new HttpGet();

    private Context mContext;
    private WorkingMode mode;
    private MyCallback mCallback;

    public enum WorkingMode {
        AUTO,
        MANUAL
    }


    public interface MyCallback {
        void setSwitchAuto(boolean keepState);
        void setSwitchManual(boolean keepState);
        PMDataResults getPMDataDetectorResults();
    }


    public SwitchListener(Context context, WorkingMode mode, MyCallback callback) {
        mContext = context;
        this.mode = mode;
        this.mCallback = callback;
    }

    private void controlRequests(boolean keepState) {
        String res;
        AbortableRequest switchOn = new AbortableRequest(mRequestOn);
        AbortableRequest switchOff = new AbortableRequest(mRequestOff);

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            res = getRequest.execute(WORKSTATE_URL).get();

            if (res.equals("WorkStates.Sleeping\n")) {
                Toast.makeText(mContext, "Przetwarzam żądanie...", Toast.LENGTH_LONG).show();
                if (!keepState) {  // send request if it was switch -> ON
                    switchOn.execute(mode + "=1");
                }
                else {
                    switchOff.execute(mode + "=0");
                }
            }
            else if (res.equals("WorkStates.Measuring\n")) {
                Toast.makeText(mContext, "Nie mogę przetworzyć żądania - czujnik w trybie pomiarowym" , Toast.LENGTH_LONG).show();
                if (mode.equals(WorkingMode.AUTO)) {
                    mCallback.setSwitchAuto(keepState);
                }
                else {
                    mCallback.setSwitchManual(keepState);
                }
            }
            else {
                Toast.makeText(mContext, "Coś się popsuło i nie było mnie słychać", Toast.LENGTH_LONG).show();
                if (mode.equals(WorkingMode.AUTO)) {
                    mCallback.setSwitchAuto(keepState);
                }
                else {
                    mCallback.setSwitchManual(keepState);
                }
            }

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {  // no-internet?
            Toast.makeText(mContext, "Nie mogę się połączyć z domową siecią Wi-Fi!" , Toast.LENGTH_LONG).show();
            if (mode.equals(WorkingMode.AUTO)) {
                mCallback.setSwitchAuto(keepState);
            }
            else {
                mCallback.setSwitchManual(keepState);
            }
        }
    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        // Automatic mode - turn on the fan if any of PM2.5/10 will be higher than threshold (default: 100%)
        if (mode.equals(WorkingMode.AUTO)) {
            // Initial flag configuration - multiple switching possible
            if (!flagToggle1) {
                mRequestOn = new HttpGet();
                mRequestOff = new HttpGet();
                flagToggle1 = true;
            }

            if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 2)  // if true
                if (isChecked) {
                    controlRequests(false);
                }
                else {
                    mRequestOn.abort();  // break while loop of working air purifier
                    controlRequests(true);
                    flagToggle1 = false;
                }
            else if (mCallback.getPMDataDetectorResults().flagTriStateAuto == 1) {}  // if false
            else {  // if null
                Toast.makeText(mContext, "Nie mogę się połączyć z domową siecią Wi-Fi!" , Toast.LENGTH_LONG).show();
                mCallback.setSwitchAuto(false);
            }
        }

        // Manual mode - control anytime!
        else {
            if (!flagToggle2) {
                mRequestOn = new HttpGet();
                mRequestOff = new HttpGet();
                flagToggle2 = true;
            }

            if (isChecked) {
                controlRequests(false);
            }
            else {
                mRequestOn.abort();  // break while loop of working air purifier
                controlRequests(true);
                flagToggle2 = false;

            }
        }

    }
}
