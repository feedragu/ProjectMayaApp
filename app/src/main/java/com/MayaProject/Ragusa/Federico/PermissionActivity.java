package com.MayaProject.Ragusa.Federico;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.cmu.pocketsphinx.demo.R;

public class PermissionActivity extends AppCompatActivity {

    final private int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 124;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        requestPermissions(new String[]{
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.KILL_BACKGROUND_PROCESSES,
                         Manifest.permission.SYSTEM_ALERT_WINDOW,
                         Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
