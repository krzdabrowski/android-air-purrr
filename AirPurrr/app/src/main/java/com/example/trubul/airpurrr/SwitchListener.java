package com.example.trubul.airpurrr;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Toast;
import org.apache.http.client.methods.HttpGet;
import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek on 3/3/18.
 */

public class SwitchListener implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "SwitchListener";
    private static final String WORKSTATE_URL = "http://192.168.0.248/workstate.txt";
    private static final String PM_DATA_URL = "http://192.168.0.248/pm_data.txt";
    private boolean flagToggle1 = true;  // zawsze musi byc true
    private boolean flagToggle2 = true;
    private HttpGet mRequestOn = new HttpGet();
    private HttpGet mRequestOff = new HttpGet();

    private Context mContext;

    public enum WorkingMode {
        AUTO,
        MANUAL
    }

    private WorkingMode mode;


    public SwitchListener(Context context, WorkingMode mode) {
        mContext = context;
        this.mode = mode;
    }

    public void controlRequests(boolean keepState) {
        String res;
        AbortableRequest switchOn = new AbortableRequest(mRequestOn);
        AbortableRequest switchOff = new AbortableRequest(mRequestOff);

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            res = getRequest.execute(WORKSTATE_URL).get();

            if (res.equals("WorkStates.Sleeping\n")) {
                Toast.makeText(mContext, "Przetwarzam żądanie...", Toast.LENGTH_LONG).show();
                if (!keepState) {  // wyslij zadanie, jesli to byl switch wlaczajacy
                    switchOn.execute(mode + "=1");
                }
                else {
                    switchOff.execute(mode + "=0");
                }
            }
            else if (res.equals("WorkStates.Measuring\n")) {
                Toast.makeText(mContext, "Nie mogę przetworzyć żądania - czujnik w trybie pomiarowym" , Toast.LENGTH_LONG).show();
                if (mode.equals(WorkingMode.AUTO)) {
                    MainActivity.getSwitchAuto().setChecked(keepState);
                }
                else {
                    MainActivity.getSwitchManual().setChecked(keepState);
                }
            }
            else {
                Toast.makeText(mContext, "Coś się popsuło i nie było mnie słychać", Toast.LENGTH_LONG).show();
                if (mode.equals(WorkingMode.AUTO)) {
                    MainActivity.getSwitchAuto().setChecked(keepState);
                }
                else {
                    MainActivity.getSwitchManual().setChecked(keepState);
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


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        // Poczatkowa konfiguracja flag, umozliwiajaca wielokrotne zmiany switchy
        if (mode.equals(WorkingMode.AUTO)) {
            if (!flagToggle1) {
                mRequestOn = new HttpGet();
                mRequestOff = new HttpGet();
                flagToggle1 = true;
            }
        }
        else {
            if (!flagToggle2) {
                mRequestOn = new HttpGet();
                mRequestOff = new HttpGet();
                flagToggle2 = true;
            }
        }

        // Obsluga requestow
        if (isChecked) {
            controlRequests(false);
        }
        else {
            mRequestOn.abort();  // zerwij petle while w pracy wentylatora
            controlRequests(true);

            if (mode.equals(WorkingMode.AUTO)) {
                flagToggle1 = false;
            }
            else {
                flagToggle2 = false;
            }

        }

    }
}

//}
