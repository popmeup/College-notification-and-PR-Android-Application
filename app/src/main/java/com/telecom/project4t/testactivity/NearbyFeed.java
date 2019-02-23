package com.telecom.project4t.testactivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class NearbyFeed extends AppCompatActivity implements NavigationHost, BeaconConsumer, RangeNotifier {

    Bundle args;
    Fragment fragment;
    FragmentTransaction transaction, transactionWithString;
    InputMethodManager inputManager;
    TaskStackBuilder stackBuilder;
    SharedPreferences beaconInfoPref;
    SharedPreferences.Editor prefEditor;
    NotificationCompat.Builder builder;

    BeaconManager beaconManager;
    BackgroundPowerSaver backgroundPowerSaver;
    RegionBootstrap regionBootstrap;
    BluetoothAdapter bluetoothAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ChildEventListener eventChildEventListener;

    String found = " ";
    String uuid;
    String menuFragment1;
    String menuFragment2;
    String menuFragment3;

    Identifier namespaceId;
    Identifier instanceId;
    int rssi;
    double distance;
    long telemetryVersion;
    long batteryMilliVolts;
    long pduCount;
    long uptime;
    String time;
    Boolean notificationState;
    List<Long> data;
    List<Event> eventList = new ArrayList<>();

    public static final String EXTRA_MESSAGE = "com.telecom.project4t.testActivity.MESSAGE";
    private static final String TAG = "BeaconReferenceApp";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_feed);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        verifyBluetooth();
        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);
        /*beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);*/
        beaconManager.bind(this);

        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        // wake up the app when a beacon is seen
        //Region region = new Region("backgroundRegion", null, null, null);

        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);

        // If you wish to test beacon detection in the Android Emulator, you can use code like this:
        //BeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
        //((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        public boolean readLoginState() {
//        SharedPreferences aSharedPreferenes = getSharedPreferences(
//                "LoginState", Context.MODE_PRIVATE);
//        return aSharedPreferenes.getBoolean("State", false);
   // }
        if (savedInstanceState == null) {

//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.feedContainer, new LoginFragment())
//                    .commit();
            if(!readLoginState()){
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.feedContainer, new LoginFragment())
                        .commit();
            }
            else{
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.feedContainer, new DisplayFeedEventFragment())
                        .commit();
            }

        }

        openCategory();
        EventUpdate();
        sendNotification("0x00112233445566778888",null);

        Log.d("lifechk nbf","NearbyFeed onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("lifechk nbf","NearbyFeed onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);

        Log.d("lifechk nbf","NearbyFeed onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);

        Log.d("lifechk nbf","NearbyFeed onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("lifechk nbf","NearbyFeed onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);

        Log.d("lifechk nbf","NearbyFeed onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*getSupportFragmentManager().putFragment(outState, "fragment", fragment);
        Log.d("savestate", "???");*/
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.feedContainer, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public void passStringArrayTo(Fragment fragment, ArrayList<String> arrayListArr, boolean addToBackstack){
        args = new Bundle();
        args.putStringArrayList("passList", arrayListArr);
        fragment.setArguments(args);
        transactionWithString = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.feedContainer, fragment);

        if (addToBackstack) {
            transactionWithString.addToBackStack(null);
        }

        transactionWithString.commit();
    }

    public void passStringTo(Fragment fragment, String stringData, boolean addToBackstack){
        args = new Bundle();
        args.putString("passYeah", stringData);
        fragment.setArguments(args);
        transactionWithString = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.feedContainer, fragment);

        if (addToBackstack) {
            transactionWithString.addToBackStack(null);
        }

        transactionWithString.commit();
    }

    public void backTo(){
        getSupportFragmentManager().popBackStack();
    }

    public void hideKeyboard(Activity activity) {
        inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public int dptopx(float dip){
        float dimension;
        dimension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  dip, Resources.getSystem().getDisplayMetrics());
        return (int) dimension;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(byte[] data, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MonitoringActivity", "coarse location permission granted");
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void verifyBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, getString(R.string.not_support_bluetooth_msg), Toast.LENGTH_SHORT).show();
        }
        else if (!bluetoothAdapter.isEnabled()) {
            // Ask the user to activate the bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        // Set the two identifiers below to null to detect any beacon regardless of identifiers
        /*final Identifier myBeaconNamespaceId = Identifier.parse("0x00112233445566778899");
        Identifier myBeaconInstanceId = Identifier.parse("0xAABBCCDDEEFF");
        Region region = new Region("my-beacon-region", myBeaconNamespaceId, myBeaconInstanceId, null);*/
        Region region = new Region("my-beacon-region", null, null, null);
        /*beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.d("beaconStat", "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                    Beacon firstBeacon = beacons.iterator().next();
                    uuid = firstBeacon.getId1().toString();
                    Log.d("beaconStat", uuid);
                    if (uuid.equals("0x00112233445566778899")) {
                        if (!found.equals("0x00112233445566778899")) {
                            sendNotification(uuid);
                            found = uuid;
                        }
                    }
                    else if (uuid.equals("0x00112233445566778888")){
                        if (!found.equals("0x00112233445566778888")) {
                            sendNotification(uuid);
                            found = uuid;
                        }
                    }
                }

                if (beacons.size() == 0) {
                    Log.d("find beacon", "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                    found = " ";
                }
            }
        });*/

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        beaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        for (Beacon beacon: beacons) {
            //if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
            if (beacons.size() > 0) {
                Log.d("beaconStat", "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                Beacon firstBeacon = beacons.iterator().next();
                uuid = firstBeacon.getId1().toString();
                Log.d("beaconStat", uuid);
                if (uuid.equals("0x00112233445566778899")) {
                    if (!found.equals("0x00112233445566778899")) {
                        sendNotification(uuid,null);
                        found = uuid;
                    }
                }
                else if (uuid.equals("0x00112233445566778888")){
                    if (!found.equals("0x00112233445566778888")) {
                        sendNotification(uuid,null);
                        found = uuid;
                    }
                }

                // This is a Eddystone-UID frame
                data = beacon.getDataFields();
                namespaceId = beacon.getId1();
                instanceId = beacon.getId2();
                distance = beacon.getDistance();
                rssi = beacon.getRssi();
                time = new SimpleDateFormat("h:mm a").format(Calendar.getInstance().getTime());
                //time = " ";

                Log.d("beaconStat", "NamespaceID : " + namespaceId +
                        " and InstanceID : " + instanceId);
                Log.d("beaconStat", "Beacon distance : " + distance + " m");
                Log.d("beaconStat", "Beacon RSSI : " + rssi + " dBm");
                Log.d("beaconStat", "Beacon data : " + data);
                Log.d("beaconStat", "Beacon time : " + time);

                // Do we have telemetry data?
                if (beacon.getExtraDataFields().size() > 0) {
                    telemetryVersion = beacon.getExtraDataFields().get(0);
                    batteryMilliVolts = beacon.getExtraDataFields().get(1);
                    pduCount = beacon.getExtraDataFields().get(3);
                    uptime = beacon.getExtraDataFields().get(4);

                    Log.d("beaconStat", "The above beacon is sending telemetry version " + telemetryVersion +
                            ", has been up for : " + uptime + " seconds" +
                            ", has a battery level of " + batteryMilliVolts + " mV" +
                            ", and has transmitted " + pduCount + " advertisements.");
                }

                sharedBeaconInfo(namespaceId, instanceId, distance, rssi, time);
            }

            /*if (beacons.size() > 0) {
                Log.d("beaconStat", "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                Beacon firstBeacon = beacons.iterator().next();
                uuid = firstBeacon.getId1().toString();
                Log.d("beaconStat", uuid);
                if (uuid.equals("0x00112233445566778899")) {
                    if (!found.equals("0x00112233445566778899")) {
                        sendNotification(uuid);
                        found = uuid;
                    }
                }
                else if (uuid.equals("0x00112233445566778888")){
                    if (!found.equals("0x00112233445566778888")) {
                        sendNotification(uuid);
                        found = uuid;
                    }
                }
            }*/

            if (beacons.size() == 0) {
                Log.d("find beacon", "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                found = " ";
            }
        }
    }

    public void sharedBeaconInfo (Identifier namespaceId, Identifier instanceId, double distance, int rssi, String time) {
        beaconInfoPref = this.getSharedPreferences("beaconInfo", Context.MODE_PRIVATE);
        prefEditor = beaconInfoPref.edit();
        prefEditor.putString("namespaceID", namespaceId.toString());
        prefEditor.putString("instanceID", instanceId.toString());
        prefEditor.putString("distance", String.valueOf(distance));
        prefEditor.putString("rssi", String.valueOf(rssi));
        prefEditor.putString("time", String.valueOf(time));
        prefEditor.apply();
    }

    public String receivedBeaconInfo (String tag) {
        beaconInfoPref = this.getSharedPreferences("beaconInfo", Context.MODE_PRIVATE);
        return beaconInfoPref.getString(tag, "No Beacon Data");
    }

    public void openCategory(){
        menuFragment1 = getIntent().getStringExtra("menuFragment1");
        if (menuFragment1 != null) {
            // Here we can decide what do to -- perhaps load other parameters from the intent extras such as IDs, etc
            if (menuFragment1.equals("DisplayFeedEventFragment1") && firebaseUser != null) {
                passStringTo(new DisplayCategoryFragment(), "0x00112233445566778899", true);
            }
            else {
                Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
            }
        }
        menuFragment2 = getIntent().getStringExtra("menuFragment2");
        if (menuFragment2 != null) {
            // Here we can decide what do to -- perhaps load other parameters from the intent extras such as IDs, etc
            if (menuFragment2.equals("DisplayFeedEventFragment2") && firebaseUser != null) {
                passStringTo(new DisplayCategoryFragment(), "0x00112233445566778888", true);
            }
            else {
                Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
            }
        }

        menuFragment3 = getIntent().getStringExtra("menuFragment3");
        if (menuFragment3 != null) {
            // Here we can decide what do to -- perhaps load other parameters from the intent extras such as IDs, etc
            if (!menuFragment3.equals("") && firebaseUser != null) {
                passStringTo(new DisplayEventFragment(), menuFragment3, true);
            }
            else {
                Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder";
            String description = "ReminderKaaa";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Activity Nearby", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String uuid, String eventPath) {
        if (uuid.equals("0x00112233445566778899")){
            if (!readNotificationState()){
            Intent notificationIntent = new Intent(this, NearbyFeed.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.putExtra("menuFragment1", "DisplayFeedEventFragment1");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder = new NotificationCompat.Builder(this, "Activity Nearby")
                            .setContentTitle("Telecom")
                            .setContentText("กดเพื่อดูกิจกรรมทั้งหมดของภาควิชาโทรคมนาคม")
                            .setSmallIcon(R.drawable.ic_stars_48px)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
            notificationState = true;
            saveNotificationState(notificationState);}
        }

        else if (uuid.equals("0x00112233445566778888")){
            Intent notificationIntent = new Intent(this, NearbyFeed.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.putExtra("menuFragment2", "DisplayFeedEventFragment2");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder = new NotificationCompat.Builder(this, "Activity Nearby")
                            .setContentTitle("Engineering")
                            .setContentText("กดเพื่อดูกิจกรรมของคณะวิศวกรรมศาสตร์")
                            .setSmallIcon(R.drawable.ic_stars_48px)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2, builder.build());}

        else if (uuid.equals("New Event")){
            Intent notificationIntent = new Intent(this, NearbyFeed.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.putExtra("menuFragment3", eventPath);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder = new NotificationCompat.Builder(this, "Activity Nearby")
                    .setContentTitle("New Event Update")
                    .setContentText("กดเพื่อดูกิจกรรมที่อัพเดท")
                    .setSmallIcon(R.drawable.ic_stars_48px)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2, builder.build());}
    }
    public void saveNotificationState(boolean notificationState) {
        SharedPreferences aSharedPreferenes = getSharedPreferences(
                "TelecomNotification", Context.MODE_PRIVATE);
        SharedPreferences.Editor aSharedPreferenesEdit = aSharedPreferenes
                .edit();
        aSharedPreferenesEdit.putBoolean("State", notificationState);
        aSharedPreferenesEdit.apply();
    }
    public boolean readNotificationState(){
        SharedPreferences aSharedPreferenes = getSharedPreferences(
                "TelecomNotification", Context.MODE_PRIVATE);
        return aSharedPreferenes.getBoolean("State", false);
    }

    public boolean readLoginState() {
        SharedPreferences aSharedPreferenes = getSharedPreferences(
                "LoginState", Context.MODE_PRIVATE);
        return aSharedPreferenes.getBoolean("State", false);
    }

    public void EventUpdate() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference eventUpdateReference = firebaseDatabase.getReference().child("AdminUpload").child("Electrical");
        eventChildEventListener = eventUpdateReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                eventList.add(dataSnapshot.getValue(Event.class));
                String key = dataSnapshot.getKey();
                //mKey.add(key);
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                eventList.add(dataSnapshot.getValue(Event.class));
                DatabaseReference eventURL = dataSnapshot.getRef();
                //eventListPath.add(dataSnapshot.getRef());
                String key = dataSnapshot.getKey();
                Log.d("event path",key);
                sendNotification("New Event", String.valueOf(eventURL));
                //eventURL.removeValue();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
