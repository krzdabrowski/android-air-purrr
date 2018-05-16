package com.example.trubul.airpurrr;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by krzysiek
 * On 5/16/18.
 */

public class DetectorLoader extends AsyncTaskLoader<Double[]> {
    DetectorLoader(Context context) {
        super(context);
    }

    @Override
    public Double[] loadInBackground() {
        return Detector.download();
    }
}
