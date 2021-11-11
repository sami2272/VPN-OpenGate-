package com.example.openvpn.utils;

import android.net.Uri;

import com.example.openvpn.R;

public class Utils {

    // Convert drawable image to String resource
    public static String getImgURL(int resourceId) {

        // Use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }
}
