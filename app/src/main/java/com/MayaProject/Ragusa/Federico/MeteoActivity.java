package com.MayaProject.Ragusa.Federico;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.cmu.pocketsphinx.demo.R;

public class MeteoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("fin", "qui tutto ok");
        try {
            setContentView(R.layout.activity_meteo);
        }catch(Exception e ) {
            e.printStackTrace();
        }

    }
}
