package com.example.trubul.airpurrr;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by krzysiek on 3/3/18.
 */

public class DataReader {

    private static final String TAG = "DataReader";
    private String mResult;

    public String getResult(InputStreamReader streamReader) {
        String inputLine;

        try {
            //Create a new buffered reader and String Builder
            BufferedReader in = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine).append("\n");
            }

            //Close our InputStream and Buffered reader
            in.close();
            streamReader.close();

            mResult = stringBuilder.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "DataReader result is: " + mResult);
        return mResult;
    }

}
