package com.MayaProject.Ragusa.Federico;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.txusballesteros.bubbles.BubblesManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.pocketsphinx.demo.R;

import static ai.api.AIDataService.TAG;

public class MainActivity extends Activity {

    private static final int RESULT_PERMISSION =1 ;
    public final static int REQUEST_CODE = -1010101;
    final private int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 124;
    private SharedPreferences preferences;
    private BubblesManager bubblesManager;
    private String[] permission;
    private ArrayList<String> list=new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        PackageManager pm = getPackageManager();
        try
        {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = null;
            Log.i(TAG, "onCreate: ");
            if (packageInfo != null) {
                requestedPermissions = packageInfo.requestedPermissions;
            }

            if (requestedPermissions.length > 0)
            {
                List<String> requestedPermissionsList = Arrays.asList(requestedPermissions);
                ArrayList<String> requestedPermissionsArrayList = new ArrayList<String>();
                requestedPermissionsArrayList.addAll(requestedPermissionsList);
                for(int i=0; i<requestedPermissionsArrayList.size(); i++) {
                    if(pm.checkPermission(requestedPermissionsArrayList.get(i), getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                        continue;
                    }else {
                        list.add(requestedPermissionsArrayList.get(i));
                    }
                }


            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        if(list.size()>0) {
            permission=new String[list.size()];
            for(int j=0; j<list.size(); j++) {
                permission[j]=list.get(j);
            }

            Intent i=new Intent(this, PermissionActivity.class);
            i.putExtra("permission", permission);

            startActivityForResult(i, RESULT_PERMISSION);

        }else {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Log.i("boh", String.valueOf(preferences.getBoolean(getString(R.string.permission_granted), false)));

            boolean b=preferences.getBoolean(getString(R.string.permission_granted), false);
            if(!b) {
                //startActivityForResult(new Intent(this, PermissionActivity.class), RESULT_PERMISSION);
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

        /*Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);*/
        //checkdrawPermission();


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
                    preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    Log.i("boh", String.valueOf(preferences.getBoolean(getString(R.string.permission_granted), false)));

                    boolean b=preferences.getBoolean(getString(R.string.permission_granted), false);
                    if(!b) {
                        //startActivityForResult(new Intent(this, PermissionActivity.class), RESULT_PERMISSION);
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
