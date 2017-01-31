package com.MayaProject.Ragusa.Federico;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.txusballesteros.bubbles.BubblesManager;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.cmu.pocketsphinx.demo.R;

public class MainActivity extends Activity {

    private static final int RESULT_PERMISSION =1 ;
    public final static int REQUEST_CODE = -1010101;
    private SharedPreferences preferences;
    private BubblesManager bubblesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);*/
        //checkdrawPermission();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("boh", String.valueOf(preferences.getBoolean(getString(R.string.permission_granted), false)));

        boolean b=preferences.getBoolean(getString(R.string.permission_granted), false);
        if(!b) {
        startActivityForResult(new Intent(this, PermissionActivity.class), RESULT_PERMISSION);
            LinkedHashMap<String, String> aMap = new LinkedHashMap<String, String>();
            aMap.put("corriere", "http://xml.corriereobjects.it/rss/homepage.xml");
            aMap.put("repubblica", "http://www.repubblica.it/rss/homepage/rss2.0.xml");
            aMap.put("messaggero", "http://www.ilmessaggero.it/rss/home.xml");
            aMap.put("gazzetta dello sport", "http://www.gazzetta.it/rss/home.xml");
            aMap.put("sole 24 ore", "http://www.ilsole24ore.com/rss/primapagina.xml");
            aMap.put("bbc", "http://feeds.bbci.co.uk/news/rss.xml?edition=int#");
            saveMap(aMap);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.permission_granted), true);
            editor.commit();

        }else {
            checkDrawOverlayPermission();

        }
    }

    private void startActivityForResult(Intent intent) {
    }

    public void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this.getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }else {
                Intent svc = new Intent(this, ButtonService.class);
                svc.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startService(svc);
                finish();
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("resultCode", String.valueOf(resultCode));
        switch (requestCode) {
            case RESULT_PERMISSION: {
                if (resultCode == RESULT_OK && null != data) {
                    Intent svc = new Intent(this, ButtonService.class);
                    svc.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startService(svc);
                    finish();
                }
            }
            case REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        Intent svc = new Intent(this, ButtonService.class);
                        svc.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startService(svc);
                        finish();
                    }
                }
                break;
        }
    }
    private void saveMap(Map<String,String> inputMap){
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("My_map").commit();
            editor.putString("My_map", jsonString);
            editor.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
