package com.hellapinot.thomassmith.metricelltask;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class Initialise {

    /*
    Initialise acts as presenter and intermediary between model and view.
    Handles callbacks from PhoneStateMain.
    */

    private Context context;
    private LogDataAdapter logDataAdapter;
    public PhoneStateMain phoneStateMain;

    private TextView serviceState;
    private TextView locationState;
    private TextView signalStrength;
    private RecyclerView logStream;

    public Initialise(Context context, TextView serviceState, TextView signalStrength, TextView locationState, RecyclerView logStream){
        this.context = context;
        this.signalStrength = signalStrength;
        this.serviceState = serviceState;
        this.locationState = locationState;
        this.logStream = logStream;
    }


    public void init(){
        if(checkPermission()) {
            phoneStateMain = new PhoneStateMain(context, this);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            logStream.setLayoutManager(layoutManager);
            logDataAdapter = new LogDataAdapter(context);
            logStream.setAdapter(logDataAdapter);


            signalStrength.setText(phoneStateMain.getSignalStrength());
            serviceState.setText(phoneStateMain.getServiceState());
            if(phoneStateMain.getLocation() != null) {
                String setLocation = "Long: " + phoneStateMain.getLocation().getLongitude() + " / Lat: " + phoneStateMain.getLocation().getLatitude();
                locationState.setText(setLocation);
            }
            phoneStateMain.startSignalChecker();
            phoneStateMain.getWifiList();

        }
    }

    public void mCallBack(Location location){
//        Log.d(TAG, "mCallBack: called");
        String tempLocation;

        //Check if location is null (is GPS on)
        if (location != null) {
            tempLocation = "Long: " + location.getLongitude() + " / Lat: " + location.getLatitude();
            DataBaseHelper.getInstance(context).addLogEntry(phoneStateMain.getSignalStrength(), phoneStateMain.getServiceState(), "Lo:" + location.getLongitude() + "\n La:" + location.getLatitude());
        } else{
            tempLocation = "No GPS";
            DataBaseHelper.getInstance(context).addLogEntry(phoneStateMain.getSignalStrength(), phoneStateMain.getServiceState(), tempLocation);
        }

        //Checks if the UI is paused, in which case only the log is updated
        if(!MainActivity.paused){
            serviceState.setText(phoneStateMain.getServiceState());
            signalStrength.setText(phoneStateMain.getSignalStrength());
            if(location != null) {
                locationState.setText(tempLocation);
            } else{
                locationState.setText("Turn on GPS for location");
            }
        }
        logDataAdapter.notifyDataSetChanged();
    }

    public boolean checkPermission(){
        if(ContextCompat.checkSelfPermission(context,  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,  Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else { return false; }
    }
}
