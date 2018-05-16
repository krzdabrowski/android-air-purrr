package com.example.trubul.airpurrr;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by krzysiek
 * On 5/16/18.
 */

public class APILoader extends AsyncTaskLoader<List<Object>> {
    APILoader(Context context) {
        super(context);
    }

    @Override
    public List<Object> loadInBackground() {
        return API.download();
    }
}