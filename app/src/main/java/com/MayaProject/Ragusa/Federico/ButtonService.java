package com.MayaProject.Ragusa.Federico;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.MayaProject.Ragusa.Federico.Adapter.MeteoAdapter;
import com.MayaProject.Ragusa.Federico.data.Channel;
import com.MayaProject.Ragusa.Federico.utilities.NotificationMaya;
import com.MayaProject.Ragusa.Federico.utilities.Songs;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;
import com.txusballesteros.bubbles.BubblesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import ai.api.ui.AIButton;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.demo.R;

import static ai.api.AIDataService.TAG;
import static android.graphics.PixelFormat.TRANSLUCENT;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class ButtonService extends Service implements
        edu.cmu.pocketsphinx.RecognitionListener, View.OnTouchListener, RecognitionListener {

    public static final String mBroadcastStringAction = "federico";
    public static final String mBroadcastIntegerAction = "com.truiton.broadcast.integer";
    public static final String mBroadcastPostedAction = "com.truiton.broadcast.post";
    private static final int SWIPE_MIN_DISTANCE = 40;
    private static final int SWIPE_THRESHOLD_VELOCITY = 120;

    private View topLeftView;
    private static final String KWS_SEARCH = "wakeup";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String PHONE_SEARCH = "phones";
    private static final String MENU_SEARCH = "menu";

    private static final String KEYPHRASE1 = "ciao maya";
    private static final String KEYPHRASE2 = "maya";
    private static final String KEYPHRASE3 = "ehi maya";
    private static final String KEYPHRASE4 = "ok maya";


    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    public ImageButton overlayedButton;
    private TextView textV;
    private EditText txt;
    private final IBinder mBinder = new LocalBinder();
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private ClipDrawable clip;
    private int count = 0;
    private CountDownTimer c;
    private long time;
    private long touchStartTime = 0;
    protected static final int RESULT_SPEECH = 1;
    private String[] phoneNumber = new String[2];
    private WindowManager wm;
    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;

    private StringBuilder email=new StringBuilder();
    private StringBuilder myE=new StringBuilder();
    private StringBuilder destE=new StringBuilder();
    private StringBuilder obj=new StringBuilder();
    private EditText editDest, editMail, editObj;

    private TextView songM;
    private CardView card;
    private SeekBar mu;

    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDSTOP = "stop";
    private Exception error;
    public String temperatureUnit = "C";
    private String città;
    private ProgressBar pr;

    public TextView descr;
    public ImageView image;
    public TextView titleNoti;
    public CardView cardView;
    public BitmapDrawable ob;
    public byte[] by;

    private Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                thread.sleep(2000);
                listenForSpeech();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    };

    private Thread thread3 = new Thread() {
        @Override
        public void run() {
            try {
                thread.sleep(100);
                panel.setVisibility(View.GONE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    };

    private List<ApplicationInfo> packages;
    private PackageManager pm;
    private LinkedHashMap hash = new LinkedHashMap<String, Integer>();
    boolean call = false;
    private JSOUPParser jp = new JSOUPParser();
    public ActivityManager am;
    private ButtonService myService = null;
    public ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            ButtonService.LocalBinder binder1 = (ButtonService.LocalBinder) binder;

            myService = binder1.getServiceInstance();
            songList = myService.fillList(songList);
        }
        //binder comes from server to communicate with method's of

        public void onServiceDisconnected(ComponentName className) {
            Log.d("ServiceConnection", "disconnected");
            myService = null;
        }
    };
    private GoogleApiClient client;
    private String lat, longit;
    private ViewGroup weatherR;
    private ViewGroup containerR;
    private LocationManager lm;
    private LocationListener locationListener;
    private String citta;
    private ProgressDialog progress;
    private HttpURLConnection myURLConnection;
    private HttpPars h;
    private AudioManager mAudioManager;
    private KeyEvent downEvent;
    private KeyEvent upEvent;
    private String speech;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private MeteoAdapter adapter;
    private ArrayList<String> songList = new ArrayList<>();
    private ImageView progre;
    private AIButton aiButton;
    private AnimationSet animSet;
    private Thread thread2 = new Thread() {
        @Override
        public void run() {
            getMusic();
            pm = getPackageManager();
            packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            Collections.sort(packages, new ApplicationInfo.DisplayNameComparator(pm));
            char c = 'a', sec;
            int i = 0;
            String appNameI = "";
            hash.put("a", 0);
            for (ApplicationInfo packageInfo : packages) {
                sec = String.valueOf(packageInfo.loadLabel(pm)).toLowerCase().charAt(0);
                if (sec > c) {
                    hash.put(String.valueOf(sec), i);
                    c = sec;
                }
                i++;
            }

        }
    };
    public boolean audioVoice = false;
    private TextToSpeech t1;
    public boolean isShowed = false;
    private TextToSpeech speech1;
    private NotificationReciver nlservicereciver;
    private IntentFilter mIntentFilter;
    private ArrayList<Songs> arrayList = new ArrayList<Songs>();
    private MediaPlayer player;
    public ImageView progressBar;
    private ImageButton roundedButton;
    public View layout;
    public ConstraintLayout consL;
    private Channel c1;
    private LayoutInflater li;
    private CardView move;
    private RelativeLayout panel;
    private int height, width;
    private SpeechRecognizer sr;
    private boolean call1;
    private boolean musicIsPlaying = false;
    private CountDownTimer speak = new CountDownTimer(1500, 1500) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            if (partial.equalsIgnoreCase("prossima")) {
                Intent i = new Intent(SERVICECMD);
                i.putExtra(CMDNAME, CMDNEXT);
                ButtonService.this.sendBroadcast(i);
            } else {
                Log.i("contenuto", partial);
                h = new HttpPars();
                h.execute(partial);
            }
            call = true;
        }
    };
    private boolean isRunnning;
    private CountDownTimer finishSpeak = new CountDownTimer(600,150) {

        @Override
        public void onTick(long l) {
            isRunnning=true;
        }

        @Override
        public void onFinish() {
            Log.i("contenuto countdown", partial);
            h = new HttpPars();
            h.execute(partial);
            sr.cancel();
            call = true;
            isRunnning=false;
            partial="";
        }
    };
    private String partial=" ";
    private View layoutMail;
    private WindowManager.LayoutParams param3;
    private WindowManager.LayoutParams oldParam;
    private int chooseOption=0;
    private String prec="&";
    private String part;
    private String part2;
    private long totalDuration;
    private String KEY_API_AI;
    private boolean isListening=false;
    private String currentColor="#FFFFFF00";
    private boolean started=false;
    private RecActivity rec;
    private int countRms=0;
    private BubblesManager bubblesManager;
    private WindowManager.LayoutParams params;
    private boolean shouldStickToWall = true;
    private MoveAnimator animator;
    private SizeAnimator animatorS;
    private MoveAnimatorMail animatorM;
    private int widthS;
    private int heightS;
    public WindowManager.LayoutParams param;
    private RelativeLayout button;
    private View buttonView;
    private RelativeLayout music;
    private WindowManager.LayoutParams paramMusic;
    private CountDownTimer cMusic;
    private AnimatorPlayer animatorPlayer;
    private WindowManager.LayoutParams paramNotif;
    private GestureDetector gdt;
    private CountDownTimer removeViewCount;
    private CountDownTimer notifCountDown = new CountDownTimer(5000, 4999) {
        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            Animation animation = AnimationUtils.loadAnimation (getApplicationContext(), R.anim.slide_out_up_notif);
            animation.setStartOffset(800);
            cardView.startAnimation(animation);
            removeViewCount = new CountDownTimer(2000, 1999) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    isShowNot=false;
                    try {
                        wm.removeViewImmediate(notificationView);
                        countDownFinish=true;
                    }catch (IllegalArgumentException e) {
                        countDownFinish=true;
                    }

                }

            }.start();

        }

    };
    private TextView bigtext;


    @Override
    public void onCreate() {
        super.onCreate();

        KEY_API_AI= Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {

            runRecognizerSetup();

            thread2.start();

            li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            wm = (WindowManager) getSystemService(WINDOW_SERVICE);

            buttonView = li.inflate(R.layout.button_overlay_actions, null);


            button = (RelativeLayout) buttonView.findViewById(R.id.mainView);
            music = (RelativeLayout) buttonView.findViewById(R.id.musicView);
            //overlayedButton =(ImageButton) buttonView.findViewById(R.id.overlayedButton);
            overlayedButton = new ImageButton(this);
            overlayedButton.setBackground(getDrawable(R.drawable.round_drawable));
            overlayedButton.setImageResource(android.R.drawable.ic_btn_speak_now);

            height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics());
            width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());

            params = new WindowManager.LayoutParams(180, 180, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.RIGHT;
            params.x = 0;
            params.y = 0;




            overlayedButton.setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, final MotionEvent event) {
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            if (Math.abs(event.getRawX() - initialTouchX) <= 3) {
                                Log.i(TAG, "onTouch: ");
                                overlayedButton.performClick();
                                overlayedButton.setPressed(true);
                                return false;
                            } else {
                                goToWall();
                            }
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            params.x = initialX - (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            paramMusic.x=params.x;
                            paramMusic.y=params.y;
                            wm.updateViewLayout(buttonView, paramMusic);
                            wm.updateViewLayout(overlayedButton, params);
                            return true;
                    }
                    return false;
                }
            });

            new AsyncTask<Void, Void, Exception>() {


                @Override
                protected Exception doInBackground(Void... parameters) {
                    try {
                        Log.i("prova1", "ha finito back");


                        layout = li.inflate(R.layout.button_layout, null);

                        paramMusic = new WindowManager.LayoutParams(0,
                                0,
                                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                PixelFormat.TRANSLUCENT);
                        paramMusic.gravity = Gravity.RIGHT;
                        paramMusic.x = 0;
                        paramMusic.y = 0;

                        Log.i("prova2", "ha finito back");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Exception result) {
                    try {

                        param3 = new WindowManager.LayoutParams(0, 0, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, TRANSLUCENT);
                        param3.gravity = Gravity.CENTER;
                        param3.x = 0;
                        param3.y = 0;
                        layoutMail = li.inflate(R.layout.mail_send, null);

                        panel = (RelativeLayout) layoutMail.findViewById(R.id.moveMail);

                        editDest = (EditText) layoutMail.findViewById(R.id.to);
                        editMail = (EditText) layoutMail.findViewById(R.id.mail);
                        editObj = (EditText) layoutMail.findViewById(R.id.obj);


                        move = (CardView) layout.findViewById(R.id.moveThis);

                        Log.i(TAG, "onPostExecute: "+move.getMeasuredHeight());

                        int pxMusic = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 165, getResources().getDisplayMetrics());


                        param = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                                pxMusic,
                                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                PixelFormat.TRANSLUCENT);
                        param.gravity = Gravity.TOP;
                        param.x = 0;
                        param.y = -pxMusic-getStatusBarHeight();

                        songM = (TextView) layout.findViewById(R.id.musicText);
                        mu=(SeekBar) layout.findViewById(R.id.musicSeek);
                        card = (CardView) layout.findViewById(R.id.card_view);

                        notificationView = li.inflate(R.layout.notification_overlay, null);


                        cardView = (CardView) notificationView.findViewById(R.id.card_view_n);
                        image= (ImageView) notificationView.findViewById(R.id.icon);
                        titleNoti= (TextView) notificationView.findViewById(R.id.title);
                        descr=(TextView) notificationView.findViewById(R.id.mex);
                        bigtext=(TextView) notificationView.findViewById(R.id.bigText);

                        gdt = new GestureDetector(new GestureListener());

                        cardView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(final View view, final MotionEvent event) {
                                gdt.onTouchEvent(event);
                                return true;
                            }
                        });

                        paramNotif = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                                0,
                                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                                PixelFormat.TRANSLUCENT);
                        paramNotif.gravity = Gravity.TOP;
                        paramNotif.x = 0;
                        paramNotif.y = 60;

                        card.setOnTouchListener(new View.OnTouchListener() {
                            private int initialY;
                            private float initialTouchY;

                            @Override
                            public boolean onTouch(View v, final MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        initialY = param.y;
                                        initialTouchY = event.getRawY();
                                        return true;
                                    case MotionEvent.ACTION_UP:
                                        return true;
                                    case MotionEvent.ACTION_MOVE:
                                        if(initialTouchY <=1.0) {
                                            param.y = initialY + (int) (event.getRawY() - initialTouchY);
                                            wm.updateViewLayout(move, param);
                                        }
                                        return true;
                                }
                                return false;
                            }
                        });


                        wm.addView(move, param);

                        wm.addView(panel, param3);

                        wm.addView(buttonView, paramMusic);

                        wm.addView(overlayedButton, params);



                        Log.i("prova", "ha finito");

                        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    t1.setLanguage(Locale.ITALIAN);
                                }
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.execute();








        }catch (Exception e){
            e.printStackTrace();
        }

        Log.i("button", "speriamo 1");
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
        mIntentFilter.addAction(mBroadcastIntegerAction);
        mIntentFilter.addAction(mBroadcastPostedAction);

        registerReceiver(mReceiver, mIntentFilter);

        Log.i("button", "speriamo 2");

        animator = new MoveAnimator();
        animatorPlayer = new AnimatorPlayer();
        animatorM=new MoveAnimatorMail();
        updateSize();

        overlayedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isListening=false;
                if (musicIsPlaying) {
                    listenForSpeech();
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            sr.stopListening();
                        }
                    }.start();
                } else {
                    if(sr!=null) {
                        sr.destroy();
                        sr = SpeechRecognizer.createSpeechRecognizer(ButtonService.this);
                        sr.setRecognitionListener(ButtonService.this);
                        /*paramMusic.width=WindowManager.LayoutParams.WRAP_CONTENT;
                        paramMusic.height=WindowManager.LayoutParams.WRAP_CONTENT;
                        paramMusic.x+= (int) (overlayedButton.getX()/2f);
                        wm.updateViewLayout(buttonView, paramMusic);*/
                        listenForSpeech();
                    }else  {
                        sr = SpeechRecognizer.createSpeechRecognizer(ButtonService.this);
                        sr.setRecognitionListener(ButtonService.this);
                        listenForSpeech();
                    }
                }
                recognizer.stop();
            }
        });


    }

    public void goToWall() {
        if(shouldStickToWall){
            int middle = widthS / 2;
            float nearestXWall = params.x >= middle ? widthS-overlayedButton.getWidth() : 0;
            Log.i(TAG, "goToWall: "+nearestXWall);
            Log.i(TAG, "goToWall: "+middle);
            animator.start(overlayedButton, nearestXWall, params.y);
        }
    }

    public void goToTop() {
            animator.start(overlayedButton, params.x, -heightS);

    }

    private class MoveAnimator implements Runnable {
        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;
        private View v;

        private void start(View v, float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            this.v=v;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
                float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 1200f);
                float deltaX = (destinationX -  params.x) * progress;
                float deltaY = (destinationY -  params.y) * progress;
                move(v, deltaX, deltaY);
                if (progress < 1) {
                    handler.post(this);
                }
            }


        private void stop() {
            handler.removeCallbacks(this);
        }

    }

    private class SizeAnimator implements Runnable {
        private Handler handler = new Handler(Looper.getMainLooper());
        private int destinationX;
        private int destinationY;
        private long startingTime;
        private View v;

        private void start(View v, int x, int y) {
            this.destinationX = x;
            this.destinationY = y;
            this.v=v;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
            Log.i(TAG, "current time: "+(System.currentTimeMillis() - startingTime));
            //int deltaX = (int) ((destinationX -  param.width) * progress);
            Log.i(TAG, "boh: "+(param.height  -  (System.currentTimeMillis() - startingTime)*1.8));
            int deltaY = (int) (param.height  -  (System.currentTimeMillis() - startingTime)*1.8);
            Log.i(TAG, "run: "+deltaY);

            if (deltaY > 0) {
                handler.post(this);
            }
        }


        private void stop() {
            handler.removeCallbacks(this);
        }

    }

    private class AnimatorPlayer implements Runnable {
        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;
        private View v;
        boolean goDown=false;

        private void start(View v, float x, float y, boolean b) {
            this.destinationX = x;
            this.destinationY = y;
            this.v=v;
            Log.i(TAG, "start: son qui");
            startingTime = System.currentTimeMillis();
            handler.post(this);
            this.goDown=b;

        }

        @Override
        public void run() {
            float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 2500f);
            Log.i(TAG, "progress: "+progress);
            Log.i(TAG, "run: "+destinationX+", "+destinationY);
            Log.i(TAG, "current x: "+param.x+", "+ param.y);
            if(goDown) {
                float deltaX = (destinationX + param3.x) * progress;
                float deltaY = (destinationY - param3.y) / progress;
                Log.i(TAG, "current y: " + param.y + ", " + destinationY);
                movePlayer(v, deltaX, deltaY);
                if (progress < 1 & param.y <= destinationY) {
                    handler.post(this);
                }
            }else {
                float deltaX = (destinationX + param3.x) * progress;
                float deltaY = (destinationY + param3.y) / progress;
                Log.i(TAG, "current y: " + param.y + ", " + destinationY);
                movePlayer(v, deltaX, deltaY);
                if (progress < 1 & param.y >= destinationY) {
                    handler.post(this);
                }
            }
        }


        private void stop() {
            handler.removeCallbacks(this);
        }

    }


    private class MoveAnimatorMail implements Runnable {
        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;
        private View v;

        private void start(View v, float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            this.v=v;
            Log.i(TAG, "start: son qui");
            startingTime = System.currentTimeMillis();
            handler.post(this);

        }

        @Override
        public void run() {
            float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 2000f);
            Log.i(TAG, "run: "+destinationX+", "+destinationY);
            Log.i(TAG, "current x: "+param3.x+", "+ param3.y);
            float deltaX = (destinationX -  param3.x) * progress;
            float deltaY = (destinationY -  param3.y) * progress;
            Log.i(TAG, "run: ");
            moveMail(v, deltaX, deltaY);
            if (progress < 1) {
                handler.post(this);
            }
        }


        private void stop() {
            handler.removeCallbacks(this);
        }

    }

    private void move(View v, float deltaX, float deltaY) {
        params.x += deltaX;
        params.y += deltaY;
        wm.updateViewLayout(v, params);
    }


    private void moveMail(View v, float deltaX, float deltaY) {
        param3.x += deltaX;
        param3.y += deltaY;
        Log.i(TAG, "moveMail: ");
        wm.updateViewLayout(v, param3);
    }

    private void movePlayer(View v, float deltaX, float deltaY) {
        param.x += deltaX;
        param.y += deltaY;
        Log.i(TAG, "moveMail: ");
        wm.updateViewLayout(v, param);
    }


    private void updateSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Log.i(TAG, "updateSize: "+size);
        widthS = (size.x - overlayedButton.getWidth());
        heightS=(size.y - overlayedButton.getWidth());

    }

    public void closeService (JSONObject j) {
        googleSpeechClosing(speech);
    }
    public void navTo(JSONObject j) throws JSONException {
        JSONObject parameters = j.getJSONObject("parameters");
        String address1 = parameters.getString("address-origin");
        String address2 = parameters.getString("address-dest");
        if (address1.equals("")) {
            recognizer.startListening(KWS_SEARCH);
            isListening=true;
            double[] addr2 = getLocationFromAddress(this, address2);
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address2);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mapIntent);

        } else {
            recognizer.startListening(KWS_SEARCH);
            isListening=true;
            double[] addr1 = getLocationFromAddress(this, address1);
            double[] addr2 = getLocationFromAddress(this, address2);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?&daddr=" + addr2[0] + "," + addr2[1]));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public double[] getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        double[] latlong = new double[2];
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            latlong[0] = location.getLatitude();
            latlong[1] = location.getLongitude();


        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return latlong;
    }


    public void apriApp(JSONObject j) throws JSONException {
        JSONObject parameters = j.getJSONObject("parameters");
        String app = parameters.getString("apps-original");
        String appName = null, appPackage = null;
        int s = app.indexOf(' ');
        int x = (int) hash.get(String.valueOf(app.charAt(0)));
        Log.i("prova ", "Index : " + hash.get(String.valueOf(app.charAt(0))));
        for (int i = x; i < packages.size(); i++) {
            appName = String.valueOf(packages.get(i).loadLabel(pm)).toLowerCase();
            Log.i("boh", appName);
            if (app.toLowerCase().contains(appName)) {
                appPackage = "" + packages.get(i).packageName;
                break;
            }
        }
        recognizer.startListening(KWS_SEARCH);
        isListening=true;
        Intent i = new Intent(Intent.ACTION_MAIN);
        PackageManager managerclock = getPackageManager();
        i = managerclock.getLaunchIntentForPackage(appPackage);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    public void callBack() {
        CountDownTimer c = new CountDownTimer(400, 400) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                recognizer.startListening(KWS_SEARCH);
                isListening=true;
            }
        };
    }


    private int counter = 0;

    private View notificationView;
    private boolean isShowNot=false;
    private boolean countDownFinish=true;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastPostedAction)) {
                titleNoti.setText("");
                descr.setText("");
                bigtext.setText("");
                Bundle b=intent.getExtras();
                String title = b.getString("title");
                String text = b.getString("text");
                PendingIntent p=b.getParcelable("pending");
                String bigtextS=b.getString("bigText");
                Log.i("title",  title);
                Log.i("text",  text);
                by= b.getByteArray("picture");
                if (by != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(by, 0, by.length);
                    ob = new BitmapDrawable(getResources(), bmp);
                    Log.i("icon",  ob.toString());
                }
                NotificationMaya nm=new NotificationMaya(title, text, ob, p);




                /*while(isShowNot) {
                }*/
                if(!isShowNot) {
                    if(nm.getIcon()!=null) {
                        image.setImageBitmap(nm.getIcon().getBitmap());
                    }
                    titleNoti.setText(nm.getTitle());
                    String adjusted = nm.getText().replaceAll("(?m)^[ \t]*\r?\n", "");
                    descr.setText(adjusted);
                    if(bigtextS!=null) {
                        bigtext.setText(bigtextS);
                    }
                    Animation animation = AnimationUtils.loadAnimation (getApplicationContext(), R.anim.slide_in_down);
                    animation .setStartOffset (1000);
                    cardView.startAnimation (animation);

                    paramNotif = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                            PixelFormat.TRANSLUCENT);
                    paramNotif.gravity = Gravity.TOP;
                    paramNotif.x = 0;
                    paramNotif.y = 5;
                    if(countDownFinish) {
                        wm.addView(notificationView, paramNotif);
                    }
                    notifCountDown.start();
                    countDownFinish=false;
                }else {
                    notifCountDown.cancel();
                    Animation animation2 = AnimationUtils.loadAnimation (getApplicationContext(), R.anim.slide_out_up_notif);
                    animation2.setStartOffset(200);
                    cardView.startAnimation(animation2);
                    removeViewCount = new CountDownTimer(510, 509) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            isShowNot=false;
                            //wm.removeViewImmediate(notificationView);

                        }

                    }.start();

                    if(nm.getIcon()!=null) {
                        image.setImageBitmap(nm.getIcon().getBitmap());
                    }
                    titleNoti.setText(nm.getTitle());
                    String adjusted = nm.getText().replaceAll("(?m)^[ \t]*\r?\n", "");
                    descr.setText(adjusted);
                    Animation animation = AnimationUtils.loadAnimation (getApplicationContext(), R.anim.slide_in_down);
                    animation .setStartOffset (500);
                    cardView.startAnimation (animation);
                    notifCountDown.start();
                    countDownFinish=false;
                }




            }
        }
    };

    public void getMusic() {
        try {
            ContentResolver contentResolver = getContentResolver();
            Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

            if (songCursor != null && songCursor.moveToFirst()) {
                int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int pathSong = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);


                do {
                    long currentId = songCursor.getLong(songId);
                    String currentTitle = songCursor.getString(songTitle);
                    String currentArtist = songCursor.getString(songArtist);
                    String path = songCursor.getString(pathSong);
                    arrayList.add(new Songs(currentId, currentTitle, currentArtist, path));
                } while (songCursor.moveToNext());
                songCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playMusic(String song, TextToSpeech t1, String s) {
        for (int i = 0; i < arrayList.size(); i++) {
            Log.i(TAG, "playMusic: "+arrayList.get(i).getSongTitle().toLowerCase());
            if (arrayList.get(i).getSongTitle().toLowerCase().contains(song.toLowerCase())) {
                t1.speak("riproduco", TextToSpeech.QUEUE_FLUSH, null, "maya");
                player = MediaPlayer.create(this, Uri.parse(arrayList.get(i).getPath()));
                totalDuration = player.getDuration();
                player.setLooping(false);
                player.setVolume(100, 100);
                player.start();
                mu.setMax(0);
                mu.setMax((int) (totalDuration));
                mu.destroyDrawingCache();
                mu.refreshDrawableState();
                cMusic = new CountDownTimer(totalDuration, 1000) {
                    @Override
                    public void onTick(long l) {
                        mu.setProgress((int) (totalDuration-l));
                    }

                    @Override
                    public void onFinish() {
                        mu.setProgress(0);
                        recognizer.startListening(KWS_SEARCH);
                        isListening=true;
                    }

                }.start();
                try {
                    /*param = new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT,
                            width,
                            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            PixelFormat.TRANSLUCENT);
                    param.gravity = Gravity.TOP;
                    param.x = 0;*/
                    songM.setText(arrayList.get(i).getSongTitle());
                    animatorPlayer.start(move, move.getX(), 1, true);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
        String time=String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(totalDuration),
                TimeUnit.MILLISECONDS.toSeconds(totalDuration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDuration))
        );

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void stopMusic(TextToSpeech t1, String s) {
        t1.speak(s, TextToSpeech.QUEUE_FLUSH, null, "maya");
        mu.setProgress(0);
        cMusic.cancel();
        player.pause();
        resumeRecon();
    }

    public ArrayList<String> fillList(ArrayList<String> songList) {
        for (int i = 0; i < arrayList.size(); i++) {
            songList.add(arrayList.get(i).getSongTitle());
        }
        return songList;
    }

    public void setRec(RecActivity recActivity) {
        rec=recActivity;
    }


    class NotificationReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("notification_event");
            String text = intent.getStringExtra("text");
            String pack = intent.getStringExtra("pack");
            recognizer.stop();
            isListening=false;
            t1.speak("è arrivato un messaggio da " + title, TextToSpeech.QUEUE_FLUSH, null, "firstLook");
            while (t1.isSpeaking()) {

            }
            recognizer.startListening(KWS_SEARCH);
            isListening=true;
        }

    }


    public void listenForSpeech() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                    .getPackage().getName());
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            intent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE, "maya");
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 400);

            sr.startListening(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }

    public LinkedHashMap getHash() {
        return hash;
    }

    public List<ApplicationInfo> getPack() {
        return packages;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public void resumeRecon() {
        animatorPlayer.start(move, move.getX(), -600, false);
        CountDownTimer c = new CountDownTimer(500, 500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                recognizer.startListening(KWS_SEARCH);
                isListening=true;
            }
        }.start();
    }


    public class LocalBinder extends Binder {
        public ButtonService getServiceInstance() {
            return ButtonService.this;
        }
    }

    private void runRecognizerSetup() throws Exception {
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(getApplicationContext());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                Log.i(TAG, "onPostExecute: fine fine");
                if (result != null) {
                    Log.i("prova", "Failed to init recognizer " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }


    private class JSOUPParser extends AsyncTask<String, Void, Channel> {
        @Override
        protected Channel doInBackground(String[] search) {
            {
                String google = "http://www.google.com/search?q=" + search[0];

                Document doc = null;
                try {
                    doc = Jsoup
                            .connect(google).get();

                } catch (Exception e) {
                    Log.i("prova", "errore");
                    e.printStackTrace();
                }
                Elements links = doc.select("a[href]");
                Elements media = doc.select("[src]");
                Elements imports = doc.select("link[href]");

                for(int i=0; i<links.size(); i++) {
                    Log.i("link", links.get(i).attr("abs:href"));
                }

/*
                try {

                    // need http protocol, set this as a Google bot agent :)
                    doc = Jsoup
                            .connect(google)
                            .userAgent(
                                    "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                            .timeout(5000).get();

                    // get all links
                    Elements links = doc.select("a[href]");
                    for (Element link : links) {
                        String temp = link.attr("abs:href");
                        Log.i("link", temp);
                        if(temp.startsWith("/url?q=")){
                            Log.i("link", temp);

                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

*/


            }
            return null;
        }

        @Override
        protected void onPostExecute(Channel channel) {


        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (overlayedButton != null) {
            wm.removeView(overlayedButton);
            wm.removeView(layout);
            overlayedButton = null;
            topLeftView = null;
        }

        if (recognizer != null) {
            Log.i("tent", "nope bitch");
            recognizer.cancel();
            recognizer.shutdown();
            isListening=false;
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i("ready bitch", "c'mom fight");
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {
        int color=(int)v;
        countRms++;
        if(countRms>1) {
            try {
                countRms=0;
                switch (color) {
                    case -1:
                        changeColor(overlayedButton, "#FFFFFF00");
                        break;
                    case -2:
                        changeColor(overlayedButton, "#FFFFFF00");
                        break;
                    case 0:
                        changeColor(overlayedButton, "#FFFFFF00");
                        break;
                    case 1:
                        changeColor(overlayedButton, "#FFFFEE00");
                        break;
                    case 2:
                        changeColor(overlayedButton, "#FFFFDD00");
                        break;
                    case 3:
                        changeColor(overlayedButton, "#FFFFCC00");
                        break;
                /*case 4:
                    changeColor(overlayedButton, "#FFFFBB00");
                    break;*/
                    case 5:
                        changeColor(overlayedButton, "#FFFF9900");
                        break;
                    case 6:
                        changeColor(overlayedButton, "#FFFF7700");
                        break;
                /*case 7:
                    changeColor(overlayedButton, "#FFFF5500");
                    break;*/
                    case 8:
                        changeColor(overlayedButton, "#FFFF3300");
                        break;
                    /*case 9:
                        changeColor(overlayedButton, "#FFFF2200");
                        break;*/
                    case 10:
                        changeColor(overlayedButton, "#FFFF0000");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void changeColor(final ImageButton btt, String color) throws Exception{
        final float[] from = new float[3],
                to =   new float[3];

        Color.colorToHSV(Color.parseColor(currentColor), from);
        Color.colorToHSV(Color.parseColor(color), to);     // to red

        currentColor=color;

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(280);

        final float[] hsv  = new float[3];
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();
                try {
                    GradientDrawable bgShape = (GradientDrawable) btt.getBackground();
                    bgShape.setColor(Color.HSVToColor(hsv));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        anim.start();
    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> text = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.i("boh", text.get(0));
        if (text.get(0).equalsIgnoreCase("prossima")) {
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME, CMDNEXT);
            this.sendBroadcast(i);
        } else {
            /*Log.i("contenuto", text.get(0));
            h = new HttpPars();
            h.execute(text.get(0));*/
        }
        /*destE.delete(0, destE.length());
        email.delete(0, destE.length());*/
        prec="";
        part="";
        part2="";
        chooseOption=0;
        call = true;

        //callBack2();

    }

    @Override
    public void onPartialResults(Bundle bundle) {
        final ArrayList<String> text = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        switch(chooseOption) {
            case 0:
                partial=text.get(0);
                if(isRunnning) {
                    finishSpeak.cancel();
                    finishSpeak.start();
                    Log.i(TAG, "merda scrivi qualcosa di intelligente");
                }else {
                    if(!partial.equals("")) {
                        finishSpeak.start();
                        Log.i(TAG, "questo non va bene");
                    }
                }
                break;
            case 1:
                if(prec.toString().equalsIgnoreCase(text.get(0))==false  & prec.length()<=text.get(0).length()) {
                    part=text.get(0).replaceAll(prec, "");
                    prec=text.get(0);
                    part=part.replaceAll(" ", "");
                    if(part.equalsIgnoreCase("chiocciola") || part.equalsIgnoreCase("at")) {
                        editDest.append("@");
                        destE.append("@");
                    }
                    else {
                        editDest.append(part.toLowerCase());
                        destE.append(part.toLowerCase());
                    }
                    Log.i("destE", destE.toString());
                }
                break;
            case 2:
                Log.i("prova",  prec +"  "+prec.length()+"  "+text.get(0).length());
                Log.i("boolean", prec.equalsIgnoreCase(text.get(0)) +" "+String.valueOf(prec.length()<=text.get(0).length()));
                if(prec.equalsIgnoreCase(text.get(0))==false  & prec.length()<=text.get(0).length()) {
                    part = text.get(0).replaceAll(prec, "");
                    part2 = part;
                    prec = text.get(0);
                    if (part2.equalsIgnoreCase("virgola")) {
                        editDest.append(", ");
                        email.append(", ");
                    } else if(part2.equalsIgnoreCase("a capo")){
                        editMail.append("\n");
                        email.append("\n");
                    }else {
                        editMail.append(part2);
                        email.append(part2);
                    }
                    Log.i("email", email.toString());
                }
                break;

        }

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        Log.i(TAG, "onPartialResult: "+text);
        if (text.equals(KEYPHRASE4)) {
            sr = SpeechRecognizer.createSpeechRecognizer(this);
            sr.setRecognitionListener(this);
            recognizer.stop();
            isListening=false;
            listenForSpeech();
        }

    }

    @Override
    public void onResult(Hypothesis hypothesis) {

    }

    /**
     * This callback is called when we stop the recognizer.
     */


    private void switchSearch(String searchName) {
        Log.i("prova result", "culi nudi");
        //recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH)) {
            recognizer.startListening(searchName);
            isListening=true;
        } else {
            Log.i("prova result", "culi nudi");
            recognizer.startListening(searchName, 10000);
            isListening=true;
        }
        Log.i("prova result", "culi nudi finale");

        //String caption = getResources().getString(captions.get(searchName));
        //Log.i("prova3", caption);
    }

    private class HttpPars extends AsyncTask<String, Void, Integer> {
        private String s;
        private int i;

        @Override
        protected Integer doInBackground(String... strings) {
            i = -24;
            Log.i("qui", "ci sono");
            URL myURL = null;
            try {
                String query = URLEncoder.encode(strings[0].toLowerCase(), "utf-8");
                myURL = new URL("https://api.api.ai/api/query?v=20150910&query=" + query + "&lang=it&sessionId="+KEY_API_AI);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                myURLConnection = (HttpsURLConnection) myURL.openConnection();

                myURLConnection.setRequestProperty("Authorization", "Bearer 7519c62b392b4f8eba60a9bfa98c2e42");
                myURLConnection.setRequestMethod("GET");

                s = myURLConnection.getResponseMessage();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return i;
        }

        @Override
        protected void onPostExecute(Integer s1) {

            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    if (line.contains("sessionId")) {
                        sb.append("}");
                        break;
                    }
                }

                Log.i("string", sb.toString());

                try {
                    JSONObject result = new JSONObject(sb.toString());
                    JSONObject start = result.getJSONObject("result");
                    String action = start.getString("action");
                    JSONObject fullfilment = start.getJSONObject("fulfillment");
                    speech = fullfilment.getString("speech");
                    Log.i("speech", action);
                    if (action.toLowerCase().contains("smalltalk")) {
                        googleSpeech2(speech);
                    } else {

                        callMethod(action, start);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            super.onPostExecute(s1);
        }
    }

    private class SetContext extends AsyncTask<String, Void, Integer> {
        private String s;
        private int i;

        @Override
        protected Integer doInBackground(String... strings) {
            i = -24;
            Log.i("qui", "ci sono");
            URL myURL = null;
            try {
                String query = URLEncoder.encode(strings[0].toLowerCase(), "utf-8");
                myURL = new URL("https://api.api.ai/api/query?v=20150910&query=" + query + "&lang=it&sessionId=214105484615454fdfd");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                myURLConnection = (HttpsURLConnection) myURL.openConnection();
                Log.i("qui", "ci sono");
                myURLConnection.setRequestProperty("Authorization", "Bearer 7519c62b392b4f8eba60a9bfa98c2e42");
                myURLConnection.setRequestMethod("GET");
                Log.i("qui", "ci sono");
                s = myURLConnection.getResponseMessage();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return i;
        }

        @Override
        protected void onPostExecute(Integer s1) {



            super.onPostExecute(s1);
        }
    }
    private void googleSpeechClosing(String speech) {
        Log.i("speech", speech);
        t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "maya");
        sr.cancel();
        while(t1.isSpeaking()) {

        }
        stopSelf();
    }

    private void googleSpeech4(String speech) {
        Log.i("speech", speech);
        t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "maya");
        sr.cancel();
        /*while(t1.isSpeaking()) {

        }*/
    }

    private void googleSpeech3(String speech) {
        Log.i("speech", speech);
        t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "maya");
        sr.cancel();
        while(t1.isSpeaking()) {

        }
        CountDownTimer c=new CountDownTimer(500, 500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                listenForSpeech();
            }
        }.start();
    }

    private void googleSpeech2(String speech) {
        Log.i("speech", speech);
        t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "maya");
        sr.destroy();
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(ButtonService.this);
        while(t1.isSpeaking()) {

        }
        CountDownTimer c=new CountDownTimer(500, 500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                listenForSpeech();
            }
        }.start();
    }



    private void callMethod(String action, JSONObject start) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = ButtonService.class.getDeclaredMethod(action, JSONObject.class);
        method.invoke(this, start);
    }

    public void createEnity() {
        try {
            JSONObject parent = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONArray entriesA = new JSONArray();
            for (int i = 0; i < arrayList.size(); i++) {
                jsonArray.put(arrayList.get(i).getSongTitle());
            }
            jsonObject.put("value", "music");
            jsonObject.put("synonyms", jsonArray);

            entriesA.put(0, jsonObject);

            parent.put("sessionId", "214105484615454fdfd");
            parent.put("name", "music");
            parent.put("extend", true);
            parent.putOpt("entries", entriesA);

            HttpPars h = new HttpPars();
            //h.execute(parent.toString(2));

            Log.i("output", parent.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    public void changeColor2(final ImageButton btt) throws Exception{
        final float[] from = new float[3],
                to =   new float[3];


        Color.colorToHSV(Color.parseColor("#FFFFA600"), from);
        Color.colorToHSV(Color.parseColor("#FFFF3C00"), to);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(300);                              // for 300 ms

        final float[] hsv  = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();
                try {
                    GradientDrawable bgShape = (GradientDrawable) btt.getBackground();
                    bgShape.setColor(Color.HSVToColor(hsv));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        anim.start();
    }


    public void changeColor3(final ImageButton btt) throws Exception{
        final float[] from = new float[3],
                to =   new float[3];



        Color.colorToHSV(Color.parseColor("#FFFF3C00"), from);
        Color.colorToHSV(Color.parseColor("#FFFF1A00"), to);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(300);

        final float[] hsv  = new float[3];
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();
                try {
                    GradientDrawable bgShape = (GradientDrawable) btt.getBackground();
                    bgShape.setColor(Color.HSVToColor(hsv));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        anim.start();
    }

    public void changeColor4(final ImageButton btt) throws Exception{
        final float[] from = new float[3],
                to =   new float[3];

        Color.colorToHSV(Color.parseColor("#FFFF1A00"), from);
        Color.colorToHSV(Color.parseColor("#FFFF0900"), to);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(300);

        final float[] hsv  = new float[3];
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();
                try {
                    GradientDrawable bgShape = (GradientDrawable) btt.getBackground();
                    bgShape.setColor(Color.HSVToColor(hsv));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        anim.start();
    }

    public String[] getNumber(ContentResolver cr, String s) {
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                try {
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (s.contains(name.toLowerCase())) {
                        String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (phones.moveToNext()) {
                                String phoneNo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                phoneNumber[0] = phoneNo;
                                phoneNumber[1] = name;
                            }
                            phones.close();

                        }
                        break;
                    }
                } catch (Exception e) {
                }
            }
        }

        return phoneNumber;
    }

    private void whatsappPreSending(JSONObject j) throws JSONException {
        googleSpeech3(speech);
        JSONObject parameters = j.getJSONObject("parameters");
        String name = parameters.getString("given-name");
        phoneNumber=getNumber(getApplicationContext().getContentResolver(), name.toLowerCase());
    }

    private void whatsappSend(JSONObject j) throws JSONException, UnsupportedEncodingException {
        googleSpeech4(speech);
        JSONObject parameters = j.getJSONObject("parameters");
        String message = parameters.getString("any");
        String sEncoded = URLEncoder.encode(message, "UTF-8");
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("smsto:" + "" + phoneNumber[0]));
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(sendIntent);
    }

    private void setupRecognizer(File assetsDir) throws Exception {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        try {

            recognizer = defaultSetup()
                    .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                    .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                    .setRawLogDir(assetsDir).setKeywordThreshold(1e-20f)
                    //.setFloat("-beam", 1e-20f)
                    .getRecognizer();
            recognizer.addListener(this);

            recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception error) {
        Log.e("", error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }

    public void cercaNews(JSONObject j) {
        if(!started) {
            Intent intent = new Intent(this, RecActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("metodo", "cercaNews");
            intent.putExtra("jsonObject", j.toString());
            started=true;
            startActivity(intent);
        }else {

        }
    }

    public void nonRisp(JSONObject j) {
            googleSpeech2(speech);
    }

    public void salutoConv(JSONObject j) {
        googleSpeech2(speech);
    }

    public void saluto(JSONObject j) {
        googleSpeech2(speech);
    }

    public void showNotification(JSONObject j) {
        googleSpeech4(speech);
        Intent intent = new Intent(ButtonService.this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Log.i(TAG, "showNotification: ");
    }

    public void mailToDest(JSONObject j) {
        googleSpeech3(speech);
        startMoveMail();
    }

    public void addMexMail(JSONObject j) {
        googleSpeech2(speech);
        chooseOption=2;
    }

    public void mailSendNeg(JSONObject j) throws Exception {
        googleSpeech3(speech+" "+destE.toString());
        String[] addresses=new String[1];
        addresses[0]=destE.toString();
        moveBack();
        Log.i("email", email.toString());
        composeEmail(addresses, email.toString());
    }

    public void mailMess(JSONObject j) {
        googleSpeech2(speech);
    }

    private void startMoveMail() {
        oldParam=param3;
        panel.setVisibility(View.VISIBLE);
        chooseOption=1;
        param3 = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, TRANSLUCENT);
        param3.gravity=Gravity.BOTTOM | Gravity.RIGHT;
        ImageView img=(ImageView) layoutMail.findViewById(R.id.imgV);
        Button bt=(Button) layoutMail.findViewById(R.id.btnChiudi);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                param3= new WindowManager.LayoutParams(0, 0, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, TRANSLUCENT);
                param3.width=0;
                param3.height=0;
                param3.gravity=Gravity.BOTTOM | Gravity.RIGHT;
                wm.updateViewLayout(panel, param3);
                //thread3.start();
            }
        });
        Picasso.with(getApplicationContext())
                .load(R.drawable.gmail)
                .fit()
                .into(img);

        animatorM.start(panel, widthS/2-20, heightS/2f);
    }

    public void moveBack() {
        param3= new WindowManager.LayoutParams(0, 0, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, TRANSLUCENT);
        param3.width=0;
        param3.height=0;
        param3.gravity=Gravity.BOTTOM | Gravity.RIGHT;
        wm.updateViewLayout(panel, param3);
    }

    public void composeEmail(String[] addresses, String text) throws Exception{
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void composeEmailObj(String[] addresses, String subject, String text) throws Exception{
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void stopMus(JSONObject j) throws JSONException {
        stopMusic(t1, speech);
        musicIsPlaying = false;
    }

    public void playMusic(JSONObject j) throws JSONException {
        JSONObject parameters = j.getJSONObject("parameters");
        String song = parameters.getString("any");
        playMusic(song, t1, speech);
        musicIsPlaying = true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void azioneOpz(JSONObject j) throws JSONException {
        googleSpeech4(speech);
        JSONObject parameters = j.getJSONObject("parameters");
        String opt = parameters.getString("opzioni1-original");
        switch (opt) {
            case "wi fi":
                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(true);
                break;
            case "gps":
                turnGPSOn();
                break;
            case "bluetooth":
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                }
                break;
            case "3g" :

                break;
        }

    }

    public void openWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private void turnGPSOn()
    {


    }


    public void chiama(JSONObject j) throws JSONException {
        JSONObject parameters = j.getJSONObject("parameters");
        String name = parameters.getString("given-name");
        phoneNumber = getNumber(getApplicationContext().getContentResolver(), name.toLowerCase());
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber[0]));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.i("fanculo", "cazzo");
            return;
        }
        startActivity(intent);

    }

    public void searchOnInternet(JSONObject j) throws JSONException {
        JSONObject parameters = j.getJSONObject("parameters");
        String ricerca = parameters.getString("any");
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        String term = ricerca;
        intent.putExtra(SearchManager.QUERY, term);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        /*jp.execute(ricerca);
        jp=new JSOUPParser();*/
        googleSpeech4(speech);

    }

    public void setSveglia(JSONObject j) throws JSONException {
        JSONObject parameters = j.getJSONObject("parameters");
        String ora = parameters.getString("time-original");
        String hour=ora.substring(0, ora.indexOf(':'));
        String minutes=ora.substring(ora.indexOf(':')+1);
        createAlarm("", Integer.parseInt(hour), Integer.parseInt(minutes));
    }

    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }else {
            Log.i("cazzo", "cazzo");
        }
    }

    public void playMusicAns(JSONObject j) throws JSONException {
        String song = null;
        try {
            JSONObject parameters = j.getJSONObject("parameters");
            song = parameters.getString("music");
            Log.i("ciao", song);
        } catch (Exception e) {
            e.printStackTrace();
        }
        playMusic(song, t1, speech);
    }

    public void meteoRequest(JSONObject j) {
        googleSpeech2(speech);
        boolean gps_enabled = false, network_enabled = false;
        if (lm == null)
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled || !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("GPS seems to be not enabled");
            dialog.setPositiveButton("enable GPS", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);


                }
            });
            dialog.show();

        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            try {
                //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Location loca = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.i("latitude", String.valueOf(loca.getLatitude()));
                Log.i("latitude", String.valueOf(loca.getLongitude()));


                Log.i("request", "ciao " + "     ciao beli");

                lat = String.valueOf(loca.getLatitude());
                longit = String.valueOf(loca.getLongitude());
                call = true;

                /*citta = getCompleteAddressString(loca.getLatitude(), loca.getLongitude());
                we.execute();*/

            } catch (Exception e) {

            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Right to left
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Left to right
            }

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                Animation animation = AnimationUtils.loadAnimation (getApplicationContext(), R.anim.slide_out_up_notif);
                animation .setStartOffset (0);
                Log.i(TAG, "onFling: ");
                cardView.startAnimation (animation);
                notifCountDown.cancel();
                if(removeViewCount!=null) {
                    removeViewCount.cancel();
                }
                removeViewCount = new CountDownTimer(510, 509) {
                    @Override
                    public void onTick(long l) {
                    }

                    @Override
                    public void onFinish() {
                        isShowNot=false;
                        wm.removeViewImmediate(notificationView);
                        countDownFinish=true;

                    }

                }.start();
                return false;
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }
            return false;
        }
    }





}
