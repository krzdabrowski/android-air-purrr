package com.example.trubul.airpurrr;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

class DataReader {
    private static final String TAG = "DataReader";

    String getData(InputStreamReader streamReader) {
        BufferedReader in = null;
        String inputLine;

        try {
            //Create a new buffered reader and String Builder
            in = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine).append("\n");
            }

            return stringBuilder.toString();
        }
        catch (IOException e) {
            Log.e(TAG, "DataReader: IO Exception getting data " + e.getMessage());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "DataReader: Error closing stream " + e.getMessage());
                }
            }
        }
        return null;
    }
}
