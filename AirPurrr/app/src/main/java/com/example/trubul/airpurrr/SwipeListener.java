package com.example.trubul.airpurrr;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

/**
 * Created by krzysiek on 3/5/18.
 */

public class SwipeListener implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "SwipeListener";
    private PMData pmData = MainActivity.getPmData();
    private SwipeRefreshLayout mySwipeRefreshLayout = MainActivity.getMySwipeRefreshLayout();

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh: ");

        Double[] pmValues = pmData.downloadPMData();
        pmData.showResults(pmValues);
        mySwipeRefreshLayout.setRefreshing(false);
    }

}