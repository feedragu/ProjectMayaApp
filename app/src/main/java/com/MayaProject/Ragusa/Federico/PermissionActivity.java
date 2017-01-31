package com.MayaProject.Ragusa.Federico;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import edu.cmu.pocketsphinx.demo.R;

public class PermissionActivity extends AppCompatActivity {

    final private int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 124;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Intent i=getIntent();
        String[] permission=i.getStringArrayExtra("permission");
        requestPermissions(permission,
                ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
