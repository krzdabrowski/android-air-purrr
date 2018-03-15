package com.example.trubul.airpurrr;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import java.util.List;

/**
 * Created by krzysiek
 * On 3/5/18.
 */

public class SwipeListener implements SwipeRefreshLayout.OnRefreshListener {

//    private static final String TAG = "SwipeListener";
    private MyCallback mCallback;
    Double[] pmValuesDetector;

    List<Object> pmValuesAndDatesAPI;
    Double[] pmValuesAPI;
    String[] pmDatesAPI;

    public interface MyCallback{
        void setSwipeRefreshing(boolean state);

        PMDataDetector getPMDataDetector();
        PMDataAPI getPMDataAPI();

        PMDataResults getPMDataDetectorResults();
        PMDataResults getPMDataAPIResults();

        void setPM25Mode(String mode);
        void setPM10Mode(String mode);
    }

    public SwipeListener(MyCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onRefresh() {
        if (!MainActivity.flagDetectorAPI) {  // if detector
            onRefreshAPI();
            onRefreshDetector();
        }
        else {  // if API
            onRefreshDetector();
            onRefreshAPI();
        }
    }

    public void onRefreshDetector() {
        pmValuesDetector = mCallback.getPMDataDetector().downloadPMDataDetector();
        mCallback.getPMDataDetectorResults().showResults(pmValuesDetector, null );
        mCallback.setPM25Mode("W mieszkaniu");
        mCallback.setPM10Mode("W mieszkaniu");
        mCallback.setSwipeRefreshing(false);
    }

    public void onRefreshAPI() {
        pmValuesAndDatesAPI = mCallback.getPMDataAPI().downloadPMDataAPI();
        pmValuesAPI = (Double[]) pmValuesAndDatesAPI.get(0);
        pmDatesAPI = (String[]) pmValuesAndDatesAPI.get(1);
        mCallback.getPMDataAPIResults().showResults(pmValuesAPI, pmDatesAPI);
        mCallback.setSwipeRefreshing(false);
    }

}