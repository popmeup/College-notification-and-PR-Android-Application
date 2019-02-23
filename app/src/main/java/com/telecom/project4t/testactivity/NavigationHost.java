package com.telecom.project4t.testactivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public interface NavigationHost {
    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */
    void navigateTo(Fragment fragment, boolean addToBackstack);
    void passStringArrayTo(Fragment fragment, ArrayList<String> arrayList, boolean addToBackstack);
    void passStringTo(Fragment fragment, String stringData, boolean addToBackstack);
    void backTo();
    void hideKeyboard(Activity activity);
    String receivedBeaconInfo (String tag);
    int dptopx(float dip);
    Bitmap decodeSampledBitmapFromResource(byte[] data, int reqWidth, int reqHeight);
}
