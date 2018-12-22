package com.example.trubul.airpurrr;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
                stringBuilder.append(inputLine).append('\n');
            }

            return stringBuilder.toString();
        }
        catch (IOException e) {
            Log.e(TAG, "getData: IO Exception getting data " + e.getMessage());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "getData: Error closing stream " + e.getMessage());
                }
            }
        }
        return null;
    }
}
