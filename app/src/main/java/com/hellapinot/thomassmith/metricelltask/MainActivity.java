package com.hellapinot.thomassmith.metricelltask;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.karan.churi.PermissionManager.PermissionManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private TextView signalStrength;
    private TextView serviceState;
    private TextView locationState;
    private RecyclerView logStream;

    public static boolean paused = true;
    private PermissionManager permissionManager;
    public Initialise initialise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paused = false;

        signalStrength = findViewById(R.id.signal_strength);
        serviceState = findViewById(R.id.service_state);
        locationState = findViewById(R.id.location_state);
        logStream = findViewById(R.id.log_stream);

        initialise = new Initialise(this, serviceState, signalStrength, locationState, logStream);



        permissionManager = new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);

        initialise.init();
    }



    @Override
    protected void onDestroy() {
        initialise.phoneStateMain.stopSignalChecker();
        super.onDestroy();
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

    //Waits for user to grant all permissions before initialising.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
        initialise.init();
    }


}
