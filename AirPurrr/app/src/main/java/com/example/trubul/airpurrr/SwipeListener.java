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
//        Detector getDetector();
        void onNewDetectorData();
        void onNewAPIData();
//        API getAPI();
        String[] getPMDatesAPI();

//        TextViewResults getTextViewDetector();
//        TextViewResults getTextViewAPI();

        void setSwipeRefreshing(boolean state);
//
//        void setPM25Mode(String mode);
//        void setPM10Mode(String mode);
    }

    public SwipeListener(MyCallback callback) {
        this.mCallback = callback;
    }


    @Override
    public void onRefresh() {
        String pmDataDetectorURL;
        if (!MainActivity.flagLocalRemote) {  // if local
            pmDataDetectorURL = MainActivity.DETECTOR_URL_LOCAL;
        }
        else {  // if remote
            pmDataDetectorURL = MainActivity.DETECTOR_URL_REMOTE;
        }

        if (!MainActivity.flagDetectorAPI) {  // if detector
            onRefreshAPI();
            onRefreshDetector(pmDataDetectorURL);
        }
        else {  // if API
            onRefreshDetector(pmDataDetectorURL);
            onRefreshAPI();
        }
    }

    public void onRefreshDetector(String pmDataDetectorURL) {
//        Double[] pmValuesDetector = mCallback.getDetector().download(pmDataDetectorURL);
        mCallback.onNewDetectorData();
//        MainActivity.showResults(pmValuesDetector, null );
//        mCallback.setPM25Mode("W mieszkaniu");
//        mCallback.setPM10Mode("W mieszkaniu");
//        mCallback.setSwipeRefreshing(false);
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