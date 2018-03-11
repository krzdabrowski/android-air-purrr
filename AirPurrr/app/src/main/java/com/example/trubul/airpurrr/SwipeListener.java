package com.example.trubul.airpurrr;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

/**
 * Created by krzysiek
 * On 3/5/18.
 */

public class SwipeListener implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "SwipeListener";
//    private PMData pmData = MainActivity.getPmData();
    private MyCallback mCallback;

    public interface MyCallback{
        void setSwipeRefreshing(boolean state);
        PMData getPMDatalala();
    }

    public SwipeListener(MyCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh: ");

        Double[] pmValues = mCallback.getPMDatalala().downloadPMData();
        mCallback.getPMDatalala().showResults(pmValues);
        mCallback.setSwipeRefreshing(false);
    }

}