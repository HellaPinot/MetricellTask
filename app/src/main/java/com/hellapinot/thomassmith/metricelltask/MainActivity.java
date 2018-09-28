package com.hellapinot.thomassmith.metricelltask;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.karan.churi.PermissionManager.PermissionManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private static TextView signalStrength;
    private static TextView serviceState;
    private static TextView locationState;
    private RecyclerView logStream;
    private LogDataAdapter logDataAdapter;
    private static LogDataAdapter staticLDA;

    public static PhoneStateMain psm;
    private static boolean paused = true;
    private PermissionManager permissionManager;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paused = false;
        context = this;

        signalStrength = findViewById(R.id.signal_strength);
        serviceState = findViewById(R.id.service_state);
        locationState = findViewById(R.id.location_state);


        permissionManager = new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);

        init();
    }



    public void init(){
        if(checkPermission()) {
            psm = new PhoneStateMain(this, logDataAdapter);

            logStream = findViewById(R.id.log_stream);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            logStream.setLayoutManager(layoutManager);
            logDataAdapter = new LogDataAdapter(this);
            logStream.setAdapter(logDataAdapter);

            staticLDA = logDataAdapter;

            signalStrength.setText(psm.getSignalStrength());
            serviceState.setText(psm.getServiceState());
            if(psm.getLocation() != null) {
                locationState.setText("Long: " + psm.getLocation().getLongitude() + " / Lat: " + psm.getLocation().getLatitude());
            }
            psm.startSignalChecker();
        }
    }

    @Override
    protected void onDestroy() {
        psm.stopSignalChecker();
        super.onDestroy();
    }



    public static void mCallBack(Location location){
//        Log.d(TAG, "mCallBack: called");
        String tempLocation;

        if (location != null) {
            tempLocation = "Long: " + location.getLongitude() + " / Lat: " + location.getLatitude();
            DataBaseHelper.getInstance(context).addLogEntry(psm.getSignalStrength(), psm.getServiceState(), "Lo:" + location.getLongitude() + "\n La:" + location.getLatitude());
        } else{
            tempLocation = "No GPS";
            DataBaseHelper.getInstance(context).addLogEntry(psm.getSignalStrength(), psm.getServiceState(), tempLocation);
        }

        if(!MainActivity.paused){
            serviceState.setText(psm.getServiceState());
            signalStrength.setText(psm.getSignalStrength());
            if(location != null) {
                locationState.setText(tempLocation);
            } else{
                locationState.setText("Turn on GPS for location");
            }
        }
        staticLDA.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: called");
        paused = true;
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        paused = false;
        super.onPostResume();
    }

    //Waits for user to grant all permissions before continuation of app functionality.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
        init();
    }

    public boolean checkPermission(){
        if(ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,  Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else { return false; }
    }
}
