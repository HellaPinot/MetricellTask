package com.hellapinot.thomassmith.metricelltask;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import java.util.List;


public class PhoneStateMain extends PhoneStateListener implements LocationListener {

    /*
    Listener class that detects changes in values and relays them back to UI
     */

    private static final String TAG = "PhoneStateMain";

    private int mServiceState;
    private TelephonyManager mTelMgr;
    private LocationManager mLocMgr;
    private Context context;
    private String mSignalStrength;
    private Handler mHandler;
    private int mInterval;
    private Initialise initialise;



    //Constructor
    public PhoneStateMain(Context context, Initialise initialise) {
        mTelMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mLocMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        this.initialise = initialise;
        this.context = context;
        mInterval = 2500;
        mServiceState = 0;
        mSignalStrength = "Waiting for service";

        mHandler = new Handler();

    }

    //Handler, checks for updated values every 2.5 seconds and calls mCallBack
    //Calls 'listen' each time to force more frequent updates from listener classes
    Runnable statusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                listen();
                initialise.mCallBack(getLocation());
            } finally {
                stopListen();
                mHandler.postDelayed(statusChecker, mInterval);
            }
        }
    };


    public void listen() {
        mTelMgr.listen(this, PhoneStateListener.LISTEN_SERVICE_STATE);
        mTelMgr.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        if(checkPermission()) {
            mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 1, this);
            mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2500, 1, this);
        }
    }

    public void stopListen() {
        mTelMgr.listen(this, PhoneStateListener.LISTEN_NONE);
    }



    //Location check methods
    public Location getLocation() {
        Location l = null;
        if(checkPermission()) {

            if (mLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                l = new Location(
                        mLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            } else if (mLocMgr
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                l = new Location(
                        mLocMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }
        }
        return l;
    }

    @Override
    public void onLocationChanged(Location location) {
    }



    //Service State check Method
    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        Log.d(TAG, "onServiceStateChanged: called" + Integer.toString(mServiceState) + " " + serviceState.getState());
        mServiceState = serviceState.getState();
    }



    //Signal Strength check methods
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//        Log.d(TAG, "onSignalStrengthsChanged: called");
        checkSignalStrengthsChanged();
    }

    public void checkSignalStrengthsChanged() {
//        Log.d(TAG, "checkSignalStrengthsChanged: called");
        if(checkPermission()) {
            List<CellInfo> cellInfos = mTelMgr.getAllCellInfo();
            if (cellInfos != null && !cellInfos.isEmpty()) {
                for (int i = 0; i < cellInfos.size(); i++) {
                    if (cellInfos.get(i).isRegistered()) {
                        if (cellInfos.get(i) instanceof CellInfoWcdma) {
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) mTelMgr.getAllCellInfo().get(0);
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                            mSignalStrength = "CDMA: " + String.valueOf(cellSignalStrengthWcdma.getDbm());
                        } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                            CellInfoGsm cellInfogsm = (CellInfoGsm) mTelMgr.getAllCellInfo().get(0);
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                            mSignalStrength = "GSM: " + String.valueOf(cellSignalStrengthGsm.getDbm());
                        } else if (cellInfos.get(i) instanceof CellInfoLte) {
                            CellInfoLte cellInfoLte = (CellInfoLte) mTelMgr.getAllCellInfo().get(0);
                            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                            mSignalStrength = "LTE: " + String.valueOf(cellSignalStrengthLte.getDbm());
                        }
                    }
                }
            }else {
                mSignalStrength = "No Signal";
            }
        }
    }


    //Getters
    public String getServiceState() {
//        Log.d(TAG, "getServiceState: called");
        switch (mServiceState) {
            case ServiceState.STATE_EMERGENCY_ONLY:
                return "Emergency Only";
            case ServiceState.STATE_IN_SERVICE:
                return "In Service";
            case ServiceState.STATE_POWER_OFF:
                return "Sensor Off";
            case ServiceState.STATE_OUT_OF_SERVICE:
                return "Out Of Service";
            default:
                return "Unknown";
        }
    }

    public String getSignalStrength() {
        return mSignalStrength;
    }



    //Check manifest permissions
    public boolean checkPermission(){
        if(ContextCompat.checkSelfPermission(context,  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,  Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            Toast.makeText(context, "Update permissions for full usage.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }



    //Handler Start/Stop
    void startSignalChecker() {
        statusChecker.run();
    }

    void stopSignalChecker() {
        mHandler.removeCallbacks(statusChecker);
    }





    //Unused implementations
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

