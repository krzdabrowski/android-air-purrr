package com.example.trubul.airpurrr;

import android.util.Log;
import javax.net.ssl.HostnameVerifier ;
import javax.net.ssl.SSLSession;

/**
 * Created by krzysiek
 * On 3/18/18.
 */

public class NullHostNameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        Log.i("RestUtilImpl", "Approving certificate for " + hostname);
        return true;
    }

}
