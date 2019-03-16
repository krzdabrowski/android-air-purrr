package com.example.trubul.airpurrr;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

class SwitchHelper implements CompoundButton.OnCheckedChangeListener {
    private static final String WORKSTATE_URL = "http://airpurrr.ga/workstate.txt";
    private Context mContext;
    private SwitchCallback mCallback;
    private boolean stateManual = false;

    interface SwitchCallback {
        void setSwitchManual(boolean state);
    }

    SwitchHelper(Context context, SwitchCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        stateManual = isChecked;
        controlRequests(stateManual);
    }

    private void controlRequests(boolean state) {
        String workStates;
        HttpsPostRequest switchOn = new HttpsPostRequest();
        HttpsPostRequest switchOff = new HttpsPostRequest();

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            workStates = getRequest.execute(WORKSTATE_URL).get();

            switch (workStates) {
                case "WorkStates.Sleeping\n":
                    Toast.makeText(mContext, R.string.main_message_switch_processing, Toast.LENGTH_LONG).show();
                    if (state) {  // send request if it was switch -> ON
                        switchOn.execute("MANUAL=1");  // it will be POST: req = params[0]

                    } else {
                        switchOff.execute("MANUAL=0");
                    }
                    break;
                case "WorkStates.Measuring\n":
                    Toast.makeText(mContext, R.string.main_message_error_measuring, Toast.LENGTH_LONG).show();
                    keepState();
                    break;
                default:
                    Toast.makeText(mContext, R.string.main_message_error + "NoWorkStates)", Toast.LENGTH_LONG).show();
                    keepState();
                    break;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Toast.makeText(mContext, mContext.getString(R.string.main_message_error_server) + "NullPointer)", Toast.LENGTH_LONG).show();
            keepState();
        }
    }

    private void keepState() {
        stateManual = !stateManual;
        mCallback.setSwitchManual(stateManual);
    }

}