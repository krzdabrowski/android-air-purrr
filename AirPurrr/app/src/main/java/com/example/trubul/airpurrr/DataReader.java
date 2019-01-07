package com.example.trubul.airpurrr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class DataReader {

    String getData(InputStreamReader streamReader) {
        BufferedReader in = null;
        String inputLine;

        try {
            in = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine).append('\n');
            }

            return stringBuilder.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
