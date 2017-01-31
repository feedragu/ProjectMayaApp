package com.MayaProject.Ragusa.Federico;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.util.Log;

import com.MayaProject.Ragusa.Federico.utilities.NotificationMaya;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static ai.api.AIDataService.TAG;

public class NotificationService extends NotificationListenerService {
    Context context;
    private String text;
    private String title;
    private IntentFilter mIntentFilter;
    public static final String mBroadcastStringAction = "federico";
    public static final String getList = "lista.notifiche";
    public static final String mBroadcastIntegerAction = "com.truiton.broadcast.integer";
    public static final String mBroadcastPostedAction = "com.truiton.broadcast.post";
    public static final String mBroadcastNotificrAction = "com.truiton.broadcast.notific";
    private List<android.support.v4.app.NotificationCompat.Action> actionL;
    public android.support.v4.app.NotificationCompat.Action actionS;
    public Notification n;
    public HashMap<String, NotificationMaya> hash=new HashMap<>();
    private ReceiverListener mReceiverListener;
    private byte[] bytesB;
    private String bigText;
    private LinkedList<String> list=new LinkedList<>();

    @Override

    public void onCreate() {

        super.onCreate();

        Log.i("sto creando il cazzo", "speriamo 2");
        context = getApplicationContext();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastNotificrAction);
        mIntentFilter.addAction(mBroadcastIntegerAction);

        Log.i(TAG, "onCreate: creato il receiver");
        mReceiverListener = new ReceiverListener();
        registerReceiver(mReceiverListener, mIntentFilter);
        Log.i(TAG, "onCreate: dopo il receiver");

    }

    class ReceiverListener extends BroadcastReceiver {

        private SpannableString titleM;
        private int counter=0;
        private BitmapDrawable bit;
        private byte[] by;
        private Bundle bundle1;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("notificationService", intent.getAction());
                if (intent.getAction().equals(mBroadcastNotificrAction)) {
                    for (StatusBarNotification sbn : NotificationService.this.getActiveNotifications()) {
                        Bundle extras =  sbn.getNotification().extras;
                        PendingIntent p1=sbn.getNotification().contentIntent;
                        if (p1!=null) {
                            Log.i(TAG, "onReceive: " + p1.toString());
                        }
                        try {
                            Log.i(TAG, "onReceive: "+extras.toString());
                        if(extras!=null) {
                            try {
                                text = extras.getCharSequence("android.text").toString();
                                title = extras.getCharSequence("android.title").toString();
                                ApplicationInfo aInfo= (ApplicationInfo) extras.get("android.rebuild.applicationInfo");
                                Log.i(TAG, "application info  :  "+aInfo.packageName);
                            }catch (NullPointerException e){
                                continue;
                            }

                            if (text != null & title != null) {
                                String pkg = sbn.getPackageName();

                                Intent broadcastIntent = new Intent();
                                Bundle b = new Bundle();
                                broadcastIntent.setAction(mBroadcastIntegerAction);
                                b.putString("title", title);
                                b.putString("text", text);


                                Notification n = sbn.getNotification();
                                Bitmap bitM;

                                bundle1 = extras.getBundle("android.wearable.EXTENSIONS");
                                if (bundle1 != null) {
                                    Log.i("funziaNotificationServ", "onReceive: " + title);
                                    Iterator iterator2 = ((ArrayList) bundle1.get("actions")).iterator();
                                    android.app.Notification.Action action2 = null;
                                    do {
                                        action2 = (android.app.Notification.Action) iterator2.next();
                                        Intent intent3 = new Intent();
                                        Bundle bundle = new Bundle();
                                        PendingIntent p=action2.actionIntent;
                                        Log.i(TAG, "onReceive: "+p.toString());
                                        if (action2.getRemoteInputs() != null) {
                                            /*for (RemoteInput remoteIn : action2.getRemoteInputs()) {
                                                Log.i("", "RemoteInput: " + remoteIn.getLabel());
                                                bundle.putCharSequence(remoteIn.getResultKey(), "baso infame per te solo le lame");
                                            }
                                            RemoteInput.addResultsToIntent(action2.getRemoteInputs(), intent3, bundle);
                                            try {
                                                action2.actionIntent.send(getApplicationContext(), 0, intent3);
                                                Log.i(TAG, "onReceive: fine");
                                            } catch (PendingIntent.CanceledException e) {
                                                e.printStackTrace();
                                            }*/
                                        }
                                    }
                                    while (iterator2.hasNext());
                                }

                                Bitmap getLarge = getLarge(n);
                                if (!pkg.equals("com.whatsapp") || getLarge == null) {
                                    bitM = n.largeIcon;
                                } else {
                                    bitM = getLarge;
                                }
                               if(bitM!=null) {
                                    try {
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitM.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                        by = baos.toByteArray();
                                    } catch (ClassCastException e) {

                                    }
                                }else {
                                   int x=extras.getInt("android.icon");
                                   //Log.i(TAG, "onReceive: "+x);
                                   final PackageManager pm=getPackageManager();
                                   final ApplicationInfo applicationInfo=pm.getApplicationInfo(pkg,PackageManager.GET_META_DATA);
                                   final Resources resources=pm.getResourcesForApplication(applicationInfo);
                                   final int appIconResId=applicationInfo.icon;
                                   final Bitmap appIconBitmap=BitmapFactory.decodeResource(resources,appIconResId);
                                   //og.i("bitmap", appIconBitmap.toString());

                                        try {
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            appIconBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                            by = baos.toByteArray();
                                        } catch (ClassCastException e) {
                                        }
                                }
                                b.putByteArray("picture", by);
                                b.putParcelable("pending", p1);
                                broadcastIntent.putExtras(b);
                                sendBroadcast(broadcastIntent);

                            }
                        }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }/*
                        Notification n=sbn.getNotification();
                        Icon ic= n.getLargeIcon();
                        if(ic==null) {
                            ic=n.getSmallIcon();
                        }
                        Drawable d=ic.loadDrawable(NotificationService.this);
                        try {
                            bit = (BitmapDrawable) d;
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bit.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
                            b = baos.toByteArray();
                        }catch (ClassCastException e ) {
                            VectorDrawable bit = (VectorDrawable) d;
                        }*/
                           /* Intent broadcastIntent = new Intent();
                        Bundle b=new Bundle();
                            broadcastIntent.setAction(mBroadcastNotificrAction);
                        b.putString("title", "ciao mamma");*/

                            //broadcastIntent.putExtras(b);

                           /* broadcastIntent.putExtra("text",text);
                            broadcastIntent.putExtra("picture", b);*/
                            //sendBroadcast(broadcastIntent);
                    }
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(mBroadcastStringAction);
                    sendBroadcast(broadcastIntent);

                //} else {
                    counter++;
                    /*String title = intent.getStringExtra("prova");

                    Notification n2 = null;
                    for (StatusBarNotification sbn : NotificationService.this.getActiveNotifications()) {
                        Bundle extras = extras = sbn.getNotification().extras;
                        if (extras != null) {
                            Bundle bundle1 = null;

                            //titleM = SpannableString.valueOf(extras.getCharSequence("android.title"));
                            bundle1 = extras.getBundle("android.wearable.EXTENSIONS");
                            if (bundle1 != null) {
                                Log.i("funziaNotificationServ", "onReceive: " + title);
                                Iterator iterator2 = ((ArrayList) bundle1.get("actions")).iterator();
                                android.app.Notification.Action action2 = null;
                                do {
                                    action2 = (android.app.Notification.Action) iterator2.next();
                                    Intent intent3 = new Intent();
                                    Bundle bundle = new Bundle();
                                    if (action2.getRemoteInputs() != null) {
                                        for (RemoteInput remoteIn : action2.getRemoteInputs()) {
                                            Log.i("", "RemoteInput: " + remoteIn.getLabel());
                                            bundle.putCharSequence(remoteIn.getResultKey(), "baso infame per te solo le lame");
                                        }
                                        RemoteInput.addResultsToIntent(action2.getRemoteInputs(), intent3, bundle);
                                        try {
                                            action2.actionIntent.send(getApplicationContext(), 0, intent3);
                                            Log.i(TAG, "onReceive: fine");
                                        } catch (PendingIntent.CanceledException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                while (iterator2.hasNext());
                                Log.i("prova finale", "ciao ciao 11111 ");
                                //Log.i("title ", titleM.toString());
                            }

                            Log.i("prova finale", "ciao ciao ");
                        }
                        Log.i("prova finale", "ciao ciao fine fione fiidfosdfsdf");
                    }*/
                }
            ;/*

            Log.i("merda merda", "onReceive2222222: " + n2.actions[0].title);
            NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(n2);
            Log.i("mannaggia a chi temmurt", "onReceive: " + wearableExtender.getActions().size());


            try {
                if (wearableExtender.getActions().size() >= 0) {
                    for (NotificationCompat.Action action : wearableExtender.getActions()) {
                        Log.i(TAG, "onScassat: " + action.title.toString().toLowerCase());
                        if (action.title.toString().toLowerCase().contains("rispondi")) {
                            Intent intent3 = new Intent();
                            Bundle bundle = new Bundle();
                            for (android.support.v4.app.RemoteInput remoteIn : action.getRemoteInputs()) {
                                Log.i("", "RemoteInput: " + remoteIn.getLabel());
                                bundle.putCharSequence(remoteIn.getResultKey(), " prova");
                            }

                            android.support.v4.app.RemoteInput.addResultsToIntent(action.getRemoteInputs(), intent3, bundle);
                            try {
                                action.actionIntent.send(getApplicationContext(), 0, intent3);
                                Log.i(TAG, "onReceive: fine");
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }

    public Bitmap getLarge(Notification notif) {
        NotificationCompat.CarExtender ca = new NotificationCompat.CarExtender(notif);
        if (ca != null) {
            return ca.getLargeIcon();
        }
        return null;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
        boolean find=false;
        int pos=0;

        Bundle extras =  sbn.getNotification().extras;
        String pkg = sbn.getPackageName();
        Log.i(TAG, "onReceive: " + extras.toString());
        title = extras.getCharSequence("android.title").toString();
        CharSequence[] obj=extras.getCharSequenceArray("android.textLines");
        if(list.size()==0) {
            list.add(sbn.getTag());
        }else {
            for(int i=0; i<list.size(); i++) {
                if(list.get(i).equals(sbn.getTag())) {
                    find=true;
                    pos=i;
                    break;
                }
            }
        }
        if(find) {

            Log.i(TAG, "onNotificationPosted: " + sbn.getTag());
            if (obj == null) {


                if (!pkg.equals("android")) {
                    try {

                        if (extras != null) {
                            try {
                                text = extras.getCharSequence("android.text").toString();
                                bigText = extras.getCharSequence("android.bigText").toString();
                                ApplicationInfo aInfo = (ApplicationInfo) extras.get("android.rebuild.applicationInfo");
                                Log.i(TAG, "application info  :  " + aInfo.packageName);
                            } catch (NullPointerException e) {
                            }
                            PendingIntent p1 = sbn.getNotification().contentIntent;
                            if (p1 != null) {
                                Log.i(TAG, "onReceive: " + p1.toString());
                            }

                            if (text != null & title != null) {

                                Intent broadcastIntent = new Intent();
                                Bundle b = new Bundle();
                                broadcastIntent.setAction(mBroadcastPostedAction);
                                b.putString("title", title);
                                b.putString("text", text);

                                if (bigText != null) {
                                    b.putString("bigText", bigText);
                                }
                                Notification n = sbn.getNotification();
                                Bitmap bitM;

                                Bitmap getLarge = getLarge(n);
                                if (!pkg.equals("com.whatsapp") || getLarge == null) {
                                    bitM = n.largeIcon;
                                } else {
                                    bitM = getLarge;
                                }
                                if (bitM != null) {
                                    try {
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitM.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                        bytesB = baos.toByteArray();
                                    } catch (ClassCastException e) {

                                    }
                                } else {
                                    int x = extras.getInt("android.icon");
                                    //Log.i(TAG, "onReceive: "+x);
                                    final PackageManager pm = getPackageManager();
                                    final ApplicationInfo applicationInfo = pm.getApplicationInfo(pkg, PackageManager.GET_META_DATA);
                                    final Resources resources = pm.getResourcesForApplication(applicationInfo);
                                    final int appIconResId = applicationInfo.icon;
                                    final Bitmap appIconBitmap = BitmapFactory.decodeResource(resources, appIconResId);
                                    //og.i("bitmap", appIconBitmap.toString());

                                    try {
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        appIconBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                        bytesB = baos.toByteArray();
                                    } catch (ClassCastException e) {
                                    }
                                }
                                b.putByteArray("picture", bytesB);
                                b.putParcelable("pending", p1);
                                broadcastIntent.putExtras(b);
                                sendBroadcast(broadcastIntent);

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (int i = 0; i < obj.length; i++) {
                    Log.i(TAG, "onNotificationPosted: " + obj[i]);
                }
            }
        }else {

        }

    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification was removed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiverListener);
    }
}
