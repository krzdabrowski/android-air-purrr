package com.example.trubul.airpurrr;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by krzysiek
 * On 3/5/18.
 */

public class SwipeListener implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "SwipeListener";
    private MyCallback mCallback;


    public interface MyCallback{
        void onNewDetectorData();
        void onNewAPIData();

        void setSwipeRefreshing();
    }

    public SwipeListener(MyCallback callback) {
        this.mCallback = callback;
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


    public void onRefreshAPI() {
//        List<Object> pmValuesAndDatesAPI = mCallback.getAPI().download();
//        Double[] pmValuesAPI = (Double[]) pmValuesAndDatesAPI.get(0);
//        String[] pmDatesAPI = (String[]) pmValuesAndDatesAPI.get(1);
//        mCallback.getTextViewAPI().showResults(pmValuesAPI, pmDatesAPI);
//
//        if (!pmDatesAPI[0].isEmpty() && !pmDatesAPI[1].isEmpty()) {
//            mCallback.setPM25Mode("API z " + pmDatesAPI[0]);
//            mCallback.setPM10Mode("API z " + pmDatesAPI[1]);
//        }
//        else {
//            mCallback.setPM25Mode("API z " + mCallback.getPMDatesAPI()[0]);
//            mCallback.setPM10Mode("API z " + mCallback.getPMDatesAPI()[1]);
//        }
//
//        mCallback.setSwipeRefreshing(false);
    }

}