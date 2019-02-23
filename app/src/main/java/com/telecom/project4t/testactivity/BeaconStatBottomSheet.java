package com.telecom.project4t.testactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.altbeacon.beacon.BeaconManager;

import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public class BeaconStatBottomSheet extends BottomSheetDialogFragment {

    NearbyFeed nearbyFeed;
    BeaconManager beaconManager;
    SharedPreferences beaconInfoPref;

    View view;

    EditText beaconLog;
    TextView beaconName;
    TextView beaconNamespaceId;
    TextView beaconInstanceId;
    TextView beaconDistance;
    TextView beaconRSSI;

    String namespaceId;
    String instanceId;
    String rssi;
    String distance;
    long telemetryVersion;
    long batteryMilliVolts;
    long pduCount;
    long uptime;
    String time, lastTime;

    List<Long> data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("lifechk stat","BeaconStatBottomSheet onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        beaconInfoPref = getActivity().getSharedPreferences("beaconInfo", Context.MODE_PRIVATE);

        view = inflater.inflate(R.layout.bottom_sheet_beacon_stat, container, false);

        beaconName = (TextView) view.findViewById(R.id.beaconName);
        beaconNamespaceId = (TextView) view.findViewById(R.id.beaconNamespaceId);
        beaconInstanceId = (TextView) view.findViewById(R.id.beaconInstanceId);
        beaconDistance = (TextView) view.findViewById(R.id.beaconDistance);
        beaconRSSI = (TextView) view.findViewById(R.id.beaconRSSI);

        beaconInfoPref.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                time = ((NavigationHost) getActivity()).receivedBeaconInfo("time");
                namespaceId = ((NavigationHost) getActivity()).receivedBeaconInfo("namespaceID");
                instanceId = ((NavigationHost) getActivity()).receivedBeaconInfo("instanceID");
                distance = ((NavigationHost) getActivity()).receivedBeaconInfo("distance");
                rssi = ((NavigationHost) getActivity()).receivedBeaconInfo("rssi");

                beaconName.setText("Latest " + getString(R.string.app_name) + " : " + time);
                beaconNamespaceId.setText("NamespaceID : " + namespaceId);
                beaconInstanceId.setText("InstanceID : " + instanceId);
                beaconDistance.setText("Distance : " + distance + " m");
                beaconRSSI.setText("RSSI : " + rssi + " dBm");

                logToDisplay(instanceId + " : " + String.valueOf(rssi) + " dBm / " + distance + " m");
            }
        });

        Log.d("lifechk stat","BeaconStatBottomSheet onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk stat","BeaconStatBottomSheet onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk stat","BeaconStatBottomSheet onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk stat","BeaconStatBottomSheet onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk stat","BeaconStatBottomSheet onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk stat","BeaconStatBottomSheet onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("lifechk stat","BeaconStatBottomSheet onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk stat","BeaconStatBottomSheet onDestroy");
    }

    public void logToDisplay(final String beaconData) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                beaconLog = (EditText) view.findViewById(R.id.beaconLog);
                beaconLog.append(beaconData + "\n");
            }
        });
    }

}
