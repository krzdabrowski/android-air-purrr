package com.example.trubul.airpurrr;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by krzysiek
 * On 3/5/18.
 */

class SwipeListener implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "SwipeListener";
    private SwipeCallback mCallback;


    interface SwipeCallback {
        void onNewDetectorData();
        void onNewAPIData();
        void setSwipeRefreshing();
    }

    SwipeListener(SwipeCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onRefresh() {
        if (!MainActivity.flagDetectorAPI) {  // if detector
            mCallback.onNewAPIData();
            mCallback.onNewDetectorData();
        } else {  // if API
            mCallback.onNewDetectorData();
            mCallback.onNewAPIData();
        }

        mCallback.setSwipeRefreshing();
    }

}