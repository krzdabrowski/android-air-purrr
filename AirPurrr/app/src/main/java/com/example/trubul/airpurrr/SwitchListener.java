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

    private void controlRequests(boolean keepState) {
        String res;
        AbortableRequest switchOn = new AbortableRequest(mRequestOn);
        AbortableRequest switchOff = new AbortableRequest(mRequestOff);

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            res = getRequest.execute(WORKSTATE_URL).get();

            switch(res) {
                case "WorkStates.Sleeping\n":
                    Toast.makeText(mContext, "Przetwarzam żądanie...", Toast.LENGTH_LONG).show();
                    if (!keepState) {  // wyslij zadanie, jesli to byl switch wlaczajacy
                        switchOn.execute(mode + "=1");
                    }
                    else {
                        switchOff.execute(mode + "=0");
                    }

                case "WorkStates.Measuring\n":
                    Toast.makeText(mContext, "Nie mogę przetworzyć żądania - czujnik w trybie pomiarowym" , Toast.LENGTH_LONG).show();
                    if (mode.equals(WorkingMode.AUTO)) {
                        MainActivity.getSwitchAuto().setChecked(keepState);
                    }
                    else {
                        MainActivity.getSwitchManual().setChecked(keepState);
                    }

                default:
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
        catch (NullPointerException e) { // gdy np nie ma internetu
            Toast.makeText(mContext, "Nie mogę się połączyć z domową siecią Wi-Fi!" , Toast.LENGTH_LONG).show();
            if (mode.equals(WorkingMode.AUTO)) {
                MainActivity.getSwitchAuto().setChecked(keepState);
            }
            else {
                MainActivity.getSwitchManual().setChecked(keepState);
            }
        }
    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        // Tryb automatyczny - wlacz wentylator gdy dowolna wartosc PM2.5/10 przekroczy 100% normy
        if (mode.equals(WorkingMode.AUTO)) {
            // Poczatkowa konfiguracja flag, umozliwiajaca wielokrotne zmiany switchy
            if (!flagToggle1) {
                mRequestOn = new HttpGet();
                mRequestOff = new HttpGet();
                flagToggle1 = true;
            }

            if (MainActivity.getPmData().flagTriStateAuto == 2) // if true
                if (isChecked) {
                    controlRequests(false);
                }
                else {
                    mRequestOn.abort();  // zerwij petle while w pracy wentylatora
                    controlRequests(true);
                    flagToggle1 = false;
                }
            else if (MainActivity.getPmData().flagTriStateAuto == 1) {} // if false
            else { // if null
                Toast.makeText(mContext, "Nie mogę się połączyć z domową siecią Wi-Fi!" , Toast.LENGTH_LONG).show();
                MainActivity.getSwitchAuto().setChecked(false);
            }
        }

        // Tryb manualny - wlaczaj kiedy chcesz
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
                mRequestOn.abort();  // zerwij petle while w pracy wentylatora
                controlRequests(true);
                flagToggle2 = false;

            }
        }

    }
}
