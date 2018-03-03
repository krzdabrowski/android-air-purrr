package com.example.trubul.airpurrr;

import android.content.Context;
import android.support.annotation.StringDef;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.apache.http.client.methods.HttpGet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek on 3/3/18.
 */


public class SwitchListeners implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "SwitchListeners";
    private static final String WORKSTATE_URL = "http://192.168.0.248/workstate.txt";
    private static final String PM_DATA_URL = "http://192.168.0.248/pm_data.txt";
    private boolean flagToggle1 = true;  // zawsze musi byc true
    private boolean flagToggle2 = true;

    private HttpGet requestOn = new HttpGet();
    private HttpGet requestOff = new HttpGet();
    private Context mContext;

    public enum WorkingMode {
        AUTO,
        MANUAL
    }

    private WorkingMode mode;


    public SwitchListeners(Context context, WorkingMode mode) {
        mContext = context;
        this.mode = mode;
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String res;

        if (mode.equals(WorkingMode.AUTO)) {
            if (!flagToggle1) {
                requestOn = new HttpGet();
                requestOff = new HttpGet();
                flagToggle1 = true;
            }
        }
        else {
            if (!flagToggle2) {
                requestOn = new HttpGet();
                requestOff = new HttpGet();
                flagToggle2 = true;
            }
        }


        AbortableRequest switchOn = new AbortableRequest(requestOn);
        AbortableRequest switchOff = new AbortableRequest(requestOff);

        if (isChecked) {
            try {
                HttpGetRequest getRequest = new HttpGetRequest();
                res = getRequest.execute(WORKSTATE_URL).get();

                if (res.equals("WorkStates.Sleeping\n")) {
                    Toast.makeText(mContext, "Przetwarzam żądanie...", Toast.LENGTH_LONG).show();
                    switchOn.execute(mode + "=1");
                }
                else if (res.equals("WorkStates.Measuring\n")) {
                    Toast.makeText(mContext, "Nie mogę przetworzyć żądania - czujnik w trybie pomiarowym" , Toast.LENGTH_LONG).show();
                    if (mode.equals(WorkingMode.AUTO)) {
                        MainActivity.getSwitchAuto().setChecked(false);
                    }
                    else {
                        MainActivity.getSwitchManual().setChecked(false);
                    }
                }
                else {
                    Toast.makeText(mContext, "Coś się popsuło i nie było mnie słychać", Toast.LENGTH_LONG).show();
                }

            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        else {
            requestOn.abort();  // zerwij petle while w pracy wentylatora
            switchOff.execute(mode + "=0"); // to (czy to?) nie powinno dac sie odpalic gdy jest measuring

            if (mode.equals(WorkingMode.AUTO)) {
                flagToggle1 = false;
            }
            else {
                flagToggle2 = false;
            }

            try {
                HttpGetRequest getRequest = new HttpGetRequest();
                res = getRequest.execute(WORKSTATE_URL).get();
//
                if (res.equals("WorkStates.Sleeping\n")) {
                    Toast.makeText(mContext, "Próbuję przetworzyć żądanie, proszę czekać...", Toast.LENGTH_SHORT).show();
                }
                else if (res.equals("WorkStates.Measuring\n")) {
                    Toast.makeText(mContext, "Nie mogę przetworzyć żądania - czujnik w trybie pomiarowym" , Toast.LENGTH_LONG).show();
                    if (mode.equals(WorkingMode.AUTO)) {
                        MainActivity.getSwitchAuto().setChecked(true);
                    }
                    else {
                        MainActivity.getSwitchManual().setChecked(true);
                    }
                }
                else {
                    Toast.makeText(mContext, "Coś się popsuło i nie było mnie słychać", Toast.LENGTH_LONG).show();
                    if (mode.equals(WorkingMode.AUTO)) {
                        MainActivity.getSwitchAuto().setChecked(true);
                    }
                    else {
                        MainActivity.getSwitchManual().setChecked(true);
                    }
                }
            }

            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

    }
}

//}
