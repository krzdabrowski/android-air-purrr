package com.example.trubul.airpurrr;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

/**
 * Created by krzysiek
 * On 3/5/18.
 */

public class SwipeListener implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "SwipeListener";
    private MyCallback mCallback;

    public interface MyCallback{
        void setSwipeRefreshing(boolean state);
        PMDataDetector getPMDataDetector();
    }

    public SwipeListener(MyCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh: ");

        Double[] pmValuesDetector = mCallback.getPMDataDetector().downloadPMDataDetector();
        mCallback.getPMDataDetector().showResults(pmValuesDetector);
        mCallback.setSwipeRefreshing(false);
    }

}