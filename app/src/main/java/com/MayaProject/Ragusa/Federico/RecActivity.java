package com.MayaProject.Ragusa.Federico;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.MayaProject.Ragusa.Federico.data.Channel;
import com.MayaProject.Ragusa.Federico.utilities.News;
import com.MayaProject.Ragusa.Federico.Adapter.NewsAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ai.api.ui.AIButton;
import edu.cmu.pocketsphinx.demo.R;

public class RecActivity extends AppCompatActivity implements RecognitionListener, NewsAdapter.OnCardClickListner{

    protected static final int RESULT_SPEECH = 1;
    private TextToSpeech t1;
    private String[] phoneNumber = new String[2];
    private WindowManager wm;
    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private ArrayList<News> a=new ArrayList<>();

    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDSTOP = "stop";

    private Exception error;
    private RssParser rs;
    public String temperatureUnit = "C";
    private String città;
    private ProgressBar pr;

    private Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                thread.sleep(2000);
                listenForSpeech();
            }  catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    };

    private Channel c;

    private WeatherPArser we=new WeatherPArser();

    private static ViewGroup lWeb;
    private WebView vw;

    private List<ApplicationInfo> packages;
    private PackageManager pm;
    private LinkedHashMap hash = new LinkedHashMap<String, Integer>();
    boolean call = false;
    private JSOUPParser jp=new JSOUPParser();
    public ActivityManager am;
    private ButtonService myService=null;
    public ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            ButtonService.LocalBinder binder1 = (ButtonService.LocalBinder) binder;

            myService = binder1.getServiceInstance();
            songList=myService.fillList(songList);
            myService.setRec(RecActivity.this);
        }
        //binder comes from server to communicate with method's of

        public void onServiceDisconnected(ComponentName className) {
            Log.d("ServiceConnection", "disconnected");
            myService = null;
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
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
    private android.speech.SpeechRecognizer sp;
    private KeyEvent downEvent;
    private KeyEvent upEvent;
    private String speech;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private ArrayList<String> songList=new ArrayList<>();
    private ImageView progre;
    private int count=0;
    private AIButton aiButton;
    private AnimationSet animSet;
    private Toolbar toolbar;
    private boolean newsShowed;
    private String urlN;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main_rec);
            toolbar=(Toolbar) findViewById(R.id.toolbar) ;
            setSupportActionBar(toolbar);

            initToolbar();
            Log.i("qui ci sono", "cazzo");



            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);



            recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
            layoutManager=new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);

            Intent intent = getIntent();


            pm = getPackageManager();
            Intent serviceIntent = new Intent(RecActivity.this, ButtonService.class);
            getApplicationContext().bindService(serviceIntent, myConnection, Context.BIND_AUTO_CREATE);


            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

            wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.ITALIAN);

                    }
                }
            });

            String action = intent.getStringExtra("metodo");
            String jstring = intent.getStringExtra("jsonObject");
            JSONObject start = new JSONObject(jstring);

            Method method = RecActivity.class.getDeclaredMethod(action, JSONObject.class);
            method.invoke(RecActivity.this, start);


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startMove3(int position) {

        try {
            newsShowed=true;
            lWeb = (ViewGroup) findViewById(R.id.relativeWeb);

            vw = (WebView) findViewById(R.id.vWeb);

            WebSettings settings = vw.getSettings();
            settings.setJavaScriptEnabled(true);
            vw.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

            vw.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    Log.i("cazo", "startMove3: ");
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Log.i("failed", failingUrl);
                }
            });
            vw.loadUrl(a.get(position).getLink());

            Log.i("cazo",a.get(position).getLink() );
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(500);
            TransitionManager.beginDelayedTransition(lWeb, autoTransition);
            TransitionManager.beginDelayedTransition(weatherR, autoTransition);

            RelativeLayout.LayoutParams map = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lWeb.setLayoutParams(map);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        final CollapsingToolbarLayout coll=(CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);
        final AppBarLayout appbar=(AppBarLayout) findViewById(R.id.appbar);
        appbar.setExpanded(true);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow=false;
            int scrollRange=-1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(scrollRange==-1) {
                    scrollRange=appbar.getTotalScrollRange();
                }
                if(scrollRange+verticalOffset==0) {
                    coll.setTitle("Notizie");
                    coll.setBackgroundColor(Color.CYAN);
                    coll.setCollapsedTitleTextColor(Color.BLACK);
                    isShow=true;
                }else if(isShow) {
                    coll.setTitle(" ");
                    isShow=false;
                }

            }
        });
    }




    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                strReturnedAddress.append(returnedAddress.getLocality());
                strReturnedAddress.append(", "+returnedAddress.getCountryName());

                strAdd = strReturnedAddress.toString();
                Log.i("My Current address", "" + strReturnedAddress.toString());
            } else {
                Log.i("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("My Current address", "Canont get Address!");
        }
        return strAdd;
    }


    public static List getInstalledApplication(Context c) {
        return c.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(myService!=null & myService.isShowed) {
            Intent intent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            try {
                startActivityForResult(intent2, RESULT_SPEECH);
            } catch (ActivityNotFoundException a) {
                Toast te = Toast.makeText(getApplicationContext(),
                        "Opps! Your device doesn't support Speech to Text",
                        Toast.LENGTH_SHORT);
                te.show();
            }
        }*/
    }

    public void listenForSpeech() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                    .getPackage().getName());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
            intent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE, "maya");
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            sp.startListening(intent);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("resultCode", String.valueOf(requestCode));
        if (resultCode == RESULT_OK && null != data) {
            switch (requestCode) {
                case RESULT_SPEECH: {
                    if (resultCode == RESULT_OK && null != data) {

                        call = true;
                        ArrayList<String> text = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        if (text.get(0).equalsIgnoreCase("prossima")) {
                            long eventtime = SystemClock.uptimeMillis();
                            Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                            KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_3, 1);
                            downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                            sendOrderedBroadcast(downIntent, null);
                        } else {

                            Log.i("contenuto", text.get(0));
                            h = new HttpPars();
                            h.execute(text.get(0));
                        /*

                        if (text.get(0).contains("apri")) {
                            String appName = null, appPackage = null;
                            int j = text.get(0).toLowerCase().indexOf(' ');
                            int x = (int) myService.hash.get(String.valueOf(text.get(0).toLowerCase().charAt(j + 1)));
                            Log.i("prova ", "Index : " + myService.hash.get(String.valueOf(text.get(0).toLowerCase().charAt(0))));
                            for (int i = x; i < myService.packages.size(); i++) {
                                appName = String.valueOf(myService.packages.get(i).loadLabel(pm)).toLowerCase();
                                if (text.get(0).toLowerCase().contains(appName)) {
                                    appPackage = "" + myService.packages.get(i).packageName;
                                    break;
                                }
                            }
                            t1.speak("Sto aprendo " + appName, TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                            Intent i = new Intent(Intent.ACTION_MAIN);
                            PackageManager managerclock = getPackageManager();
                            i = managerclock.getLaunchIntentForPackage(appPackage);
                            i.addCategory(Intent.CATEGORY_LAUNCHER);
                            startActivity(i);
                            callBack();
                        } else if (text.get(0).contains("che tempo fa")) {
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

                                    citta = getCompleteAddressString(loca.getLatitude(), loca.getLongitude());


                                    we.execute();

                                } catch (Exception e) {

                                }
                            }
                            //callBack2();
                        } else if (text.get(0).contains("sblocca")) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                        } else if (text.get(0).contains("ciao")) {
                            t1.speak("Ciao amore mio, io sto bene, tu come stai? ", TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                        } else if (text.get(0).contains("scrivi a")) {
                            call = true;
                            phoneNumber = getNumber(getApplicationContext().getContentResolver(), text.get(0).toLowerCase());
                            if (!phoneNumber[0].isEmpty()) {
                                t1.speak("Cosa vuoi scrivere a" + phoneNumber[1], TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                                while (t1.isSpeaking()) {

                                }
                                Intent i = new Intent(
                                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.ITALIAN);

                                try {
                                    startActivityForResult(i, 4);
                                } catch (ActivityNotFoundException a) {
                                    Toast t = Toast.makeText(getApplicationContext(),
                                            "Opps! Your device doesn't support Speech to Text",
                                            Toast.LENGTH_SHORT);
                                    t.show();
                                }

                            } else {
                                for (int i = 0; i < 50; i++) {
                                    Log.i("Non funzia?", "no");
                                }
                            }
                        } else if (text.get(0).contains(("chiama"))) {
                            call = true;
                            phoneNumber = getNumber(getApplicationContext().getContentResolver(), text.get(0).toLowerCase());

                            if (!phoneNumber[0].isEmpty()) {
                                t1.speak("vuoi chiamare " + phoneNumber[1], TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                                while (t1.isSpeaking()) {

                                }
                                Intent i = new Intent(
                                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.ITALIAN);

                                try {
                                    Log.i("Non funzia?", "si");
                                    startActivityForResult(i, 3);
                                } catch (ActivityNotFoundException a) {
                                    Toast t = Toast.makeText(getApplicationContext(),
                                            "Opps! Your device doesn't support Speech to Text",
                                            Toast.LENGTH_SHORT);
                                    t.show();
                                }

                            }


                        } else if (text.get(0).contains(("canta una canzone d'amore"))) {
                            myService.setSpeech(t1);
                            t1.speak("A te che sei l'unica al mondo, l'unica ragione\n" +
                                    "\n" +
                                    "            Per arrivare fino in fondo ad ogni mio respiro\n" +
                                    "\n" +
                                    "            Quando ti guardo dopo un giorno pieno di parole\n" +
                                    "\n" +
                                    "            Senza che tu mi dica niente tutto si fa chiaro\n" +
                                    "\n" +
                                    "            A te che mi hai trovato all' angolo coi pugni chiusi\n" +
                                    "\n" +
                                    "            Con le mie spalle contro il muro pronto a difendermi\n" +
                                    "\n" +
                                    "            Con gli occhi bassi stavo in fila con i disillusi\n" +
                                    "\n" +
                                    "            Tu mi hai raccolto come un gatto e mi hai portato con te\n" +
                                    "            \n" +
                                    "            A te io canto una canzone perché non ho altro\n" +
                                    "\n" +
                                    "            Niente di meglio da offrirti di tutto quello che ho\n" +
                                    "\n" +
                                    "            Prendi il mio tempo e la magia che con un solo salto\n" +
                                    "\n" +
                                    "            Ci fa volare dentro all'aria come bollicine\n" +
                                    "\n" +
                                    "            A te che seeeeei, semplicemente seeeeei\n" +
                                    "\n" +
                                    "            Sostanza dei giorni miei\n" +
                                    "\n" +
                                    "            Sostanza dei giorni miei", TextToSpeech.QUEUE_FLUSH, null, "firstLook");

                        } else if (text.get(0).toLowerCase().contains("cerca su internet")) {
                            Log.i("indice", text.get(0));
                            int i = text.get(0).indexOf("et") + 3;
                            Log.i("indice", "" + i);
                            String search = text.get(0).substring(i, text.get(0).length());
                            pr.setVisibility(View.VISIBLE);
                            jp.execute(search);
                        }*/
                        }
                        call = true;
                        callBack2();
                    }
                    break;
                }


                case 3:
                    Log.i("Non funzia?", "ci siamo quasi");
                        ArrayList<String> text = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (text.get(0).toLowerCase().contains("si")) {
                            t1.speak("Sto chiamando " + phoneNumber[1], TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                            dialContactPhone(phoneNumber[0]);
                        } else if (text.get(0).contains("no")) {
                            t1.speak("Ok allora non chiamo" + phoneNumber[1], TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                        } else {
                            t1.speak("Sto chiamando " + phoneNumber[1], TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                            dialContactPhone(phoneNumber[0]);
                        }


                    break;
                case 4:

                        ArrayList<String> text2 = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        Uri mUri = Uri.parse("smsto:+" + phoneNumber[0]);
                        Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
                        mIntent.setPackage("com.whatsapp");
                        mIntent.putExtra("sms_body", text2.get(0));
                        mIntent.putExtra("chat", true);
                        startActivity(mIntent);

                    break;
                case 6:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri contactData = data.getData();
                        Cursor c = managedQuery(contactData, null, null, null, null);
                        if (c.moveToFirst()) {
                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            // TODO Whatever you want to do with the selected contact name.
                        }
                    }
                    break;
                default:

                    break;
            }
        }else {
            Log.i("indice", "finito");
            Log.i("indice", "basta");
            finish();
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i("indice", "ready bich");
        Log.i("indice", "c'mon fight");
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

        /*if(count==8) {
            Log.i("x", String.valueOf(myService.layout.getScaleX()));
            try {
                // Scaling
                Animation scale = new ScaleAnimation(myService.layout.getScaleX(), (float) 1.4, myService.layout.getScaleY(), 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
// 1 second duration
                scale.setDuration(400);
// Moving up
// Animation set to join both scaling and moving
                AnimationSet animSet = new AnimationSet(true);
                animSet.setFillEnabled(true);
                animSet.addAnimation(scale);
// Launching animation set

                myService.layout.startAnimation(animSet);
                progre.startAnimation(animSet);
            }catch (Exception e) {

            }
        }else {
            count++;
        }*/
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

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(anim);
    }

    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> text = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.i("boh", text.get(0));
        if (text.get(0).equalsIgnoreCase("prossima")) {
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME, CMDNEXT);
            RecActivity.this.sendBroadcast(i);

        } else {

            Log.i("contenuto", text.get(0));
            h = new HttpPars();
            h.execute(text.get(0));
                        /*

                        if (text.get(0).contains("apri")) {
                            String appName = null, appPackage = null;
                            int j = text.get(0).toLowerCase().indexOf(' ');
                            int x = (int) myService.hash.get(String.valueOf(text.get(0).toLowerCase().charAt(j + 1)));
                            Log.i("prova ", "Index : " + myService.hash.get(String.valueOf(text.get(0).toLowerCase().charAt(0))));
                            for (int i = x; i < myService.packages.size(); i++) {
                                appName = String.valueOf(myService.packages.get(i).loadLabel(pm)).toLowerCase();
                                if (text.get(0).toLowerCase().contains(appName)) {
                                    appPackage = "" + myService.packages.get(i).packageName;
                                    break;
                                }
                            }
                            t1.speak("Sto aprendo " + appName, TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                            Intent i = new Intent(Intent.ACTION_MAIN);
                            PackageManager managerclock = getPackageManager();
                            i = managerclock.getLaunchIntentForPackage(appPackage);
                            i.addCategory(Intent.CATEGORY_LAUNCHER);
                            startActivity(i);
                            callBack();
                        } else if (text.get(0).contains("che tempo fa")) {
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

                                    citta = getCompleteAddressString(loca.getLatitude(), loca.getLongitude());


                                    we.execute();

                                } catch (Exception e) {

                                }
                            }
                            //callBack2();
                        } else if (text.get(0).contains("sblocca")) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                        } else if (text.get(0).contains("ciao")) {
                            t1.speak("Ciao amore mio, io sto bene, tu come stai? ", TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                        } else if (text.get(0).contains("scrivi a")) {
                            call = true;
                            phoneNumber = getNumber(getApplicationContext().getContentResolver(), text.get(0).toLowerCase());
                            if (!phoneNumber[0].isEmpty()) {
                                t1.speak("Cosa vuoi scrivere a" + phoneNumber[1], TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                                while (t1.isSpeaking()) {

                                }
                                Intent i = new Intent(
                                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.ITALIAN);

                                try {
                                    startActivityForResult(i, 4);
                                } catch (ActivityNotFoundException a) {
                                    Toast t = Toast.makeText(getApplicationContext(),
                                            "Opps! Your device doesn't support Speech to Text",
                                            Toast.LENGTH_SHORT);
                                    t.show();
                                }

                            } else {
                                for (int i = 0; i < 50; i++) {
                                    Log.i("Non funzia?", "no");
                                }
                            }
                        } else if (text.get(0).contains(("chiama"))) {
                            call = true;
                            public String[] getNumber(ContentResolver cr, String s) {
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        if(cursor.getCount()>0) {
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
    }public String[] getNumber(ContentResolver cr, String s) {
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        if(cursor.getCount()>0) {
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

                            if (!phoneNumber[0].isEmpty()) {
                                t1.speak("vuoi chiamare " + phoneNumber[1], TextToSpeech.QUEUE_FLUSH, null, "firstLook");
                                while (t1.isSpeaking()) {

                                }
                                Intent i = new Intent(
                                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.ITALIAN);

                                try {
                                    Log.i("Non funzia?", "si");
                                    startActivityForResult(i, 3);
                                } catch (ActivityNotFoundException a) {
                                    Toast t = Toast.makeText(getApplicationContext(),
                                            "Opps! Your device doesn't support Speech to Text",
                                            Toast.LENGTH_SHORT);
                                    t.show();
                                }

                            }


                        } else if (text.get(0).contains(("canta una canzone d'amore"))) {
                            myService.setSpeech(t1);
                            t1.speak("A te che sei l'unica al mondo, l'unica ragione\n" +
                                    "\n" +
                                    "            Per arrivare fino in fondo ad ogni mio respiro\n" +
                                    "\n" +
                                    "            Quando ti guardo dopo un giorno pieno di parole\n" +
                                    "\n" +
                                    "            Senza che tu mi dica niente tutto si fa chiaro\n" +
                                    "\n" +
                                    "            A te che mi hai trovato all' angolo coi pugni chiusi\n" +
                                    "\n" +
                                    "            Con le mie spalle contro il muro pronto a difendermi\n" +
                                    "\n" +
                                    "            Con gli occhi bassi stavo in fila con i disillusi\n" +
                                    "\n" +
                                    "            Tu mi hai raccolto come un gatto e mi hai portato con te\n" +
                                    "            \n" +
                                    "            A te io canto una canzone perché non ho altro\n" +
                                    "\n" +
                                    "            Niente di meglio da offrirti di tutto quello che ho\n" +
                                    "\n" +
                                    "            Prendi il mio tempo e la magia che con un solo salto\n" +
                                    "\n" +
                                    "            Ci fa volare dentro all'aria come bollicine\n" +
                                    "\n" +
                                    "            A te che seeeeei, semplicemente seeeeei\n" +
                                    "\n" +
                                    "            Sostanza dei giorni miei\n" +
                                    "\n" +
                                    "            Sostanza dei giorni miei", TextToSpeech.QUEUE_FLUSH, null, "firstLook");

                        } else if (text.get(0).toLowerCase().contains("cerca su internet")) {
                            Log.i("indice", text.get(0));
                            int i = text.get(0).indexOf("et") + 3;
                            Log.i("indice", "" + i);
                            String search = text.get(0).substring(i, text.get(0).length());
                            pr.setVisibility(View.VISIBLE);
                            jp.execute(search);
                        }*/
        }
        call = true;
        //callBack2();


    }

    @Override
    public void onPartialResults(Bundle bundle) {
        ArrayList<String> text = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.i("boh", text.get(0));

    }

    @Override
    public void onEvent(int i, Bundle bundle) {


    }

    @Override
    public void OnCardClicked(View view, int position) {
        startMove3(position);
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
                myURL = new URL("https://api.api.ai/api/query?v=20150910&query="+query+"&lang=it&sessionId=214105484615454fdfd");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }catch(Exception e) {
                e.printStackTrace();
            }
            try {
                myURLConnection = (HttpsURLConnection)myURL.openConnection();
                Log.i("qui", "ci sono");
                myURLConnection.setRequestProperty ("Authorization","Bearer 7519c62b392b4f8eba60a9bfa98c2e42" );
                myURLConnection.setRequestMethod("GET");
                Log.i("qui", "ci sono");
                s=myURLConnection.getResponseMessage();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
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
                    sb.append(line+"\n");
                    if(line.contains("sessionId")) {
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
                        googleSpeech(speech);
                    }else {
                        Method method = RecActivity.class.getDeclaredMethod(action, JSONObject.class);
                        method.invoke(RecActivity.this, start);
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            super.onPostExecute(s1);
        }
    }
    public void cercaNews(JSONObject j) throws JSONException {

        JSONObject fullfilment = j.getJSONObject("fulfillment");
        speech = fullfilment.getString("speech");
        JSONObject parameters=j.getJSONObject("parameters");
        String news=parameters.getString("giornali-original");
        t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "maya");
        urlN=loadMap(news);
        rs=new RssParser();
        rs.execute();

    }

    private String loadMap(String keyN){
        String url = "";
        LinkedHashMap<String,String> outputMap = new LinkedHashMap<String,String>();
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();

                while(keysItr.hasNext()) {
                    String key=keysItr.next();
                    Log.i("boh", ""+keyN);
                    if(keyN.toLowerCase().contains(key)){
                        url= (String) jsonObject.get(key);
                        Log.i("url", url);
                        break;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public class RssParser extends AsyncTask<String, Object, ArrayList<News>> {

        private Context cont;
        private String urlA="http://www.repubblica.it/rss/homepage/rss2.0.xml";
        private URL url;
        String speech;

        private String title, urlI, desc, link;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected ArrayList<News> doInBackground(String... string) {
            Log.i("cazzo", urlN);

            try {

                url=new URL(urlN);
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream input=conn.getInputStream();
                DocumentBuilderFactory doc= DocumentBuilderFactory.newInstance();
                DocumentBuilder builder=doc.newDocumentBuilder();
                org.w3c.dom.Document xmlDoc=builder.parse(input);
                Element root=xmlDoc.getDocumentElement();
                Node n=root.getLastChild();
                NodeList items = xmlDoc.getElementsByTagName("item");
                title = "";
                desc = "nope";
                urlI = "nope";
                link="";

                for(int i=0; i<items.getLength(); i++) {

                    Node currentChild = items.item(i);
                    if (currentChild.getNodeName().equalsIgnoreCase("item")) {
                        NodeList itemChilds = currentChild.getChildNodes();
                        for (int j = 0; j < itemChilds.getLength(); j++) {

                            Node current = itemChilds.item(j);
                            if (current.getNodeName().equalsIgnoreCase("title")) {
                                Node cT = current.getFirstChild();
                                title= cT.getTextContent();
                            } else if (current.getNodeName().equalsIgnoreCase("link")) {
                                Node cT = current.getFirstChild();
                                link= cT.getTextContent();
                            } else if (current.getNodeName().equalsIgnoreCase("enclosure")) {
                                Element e = (Element) current;
                                urlI= e.getAttribute("url").toString();
                            }else if (current.getNodeName().equalsIgnoreCase("description")) {
                                Node cT = current.getFirstChild();
                                if(cT!=null) {
                                    Element e = (Element) current;
                                    desc= cT.getTextContent();
                                    Log.i("cazzo", desc);
                                    if(desc.substring(0, 4).contains("<p>")) {
                                        desc=desc.substring(desc.lastIndexOf("</a>")+4, desc.lastIndexOf("</p>"));
                                    }else if(desc.contains("<img")) {
                                        Log.i("yes", "daje");
                                        desc=desc.substring(desc.lastIndexOf("<p>")+3, desc.lastIndexOf("</p>"));
                                    }else if(desc.contains("<br/> <b>")) {
                                        desc=desc.substring(0, desc.indexOf("<br/>"))+"\n";
                                        desc=desc+desc.substring(desc.indexOf("<br>"+3, desc.lastIndexOf("</b>")));
                                    }
                                }
                            }


                        }
                        a.add(new News(urlI, title, desc, link));
                        title = "";
                        desc = "nope";
                        urlI = "nope";
                        link="";
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return a;
        }

        @Override
        protected void onPostExecute(ArrayList<News> aVoid) {

            adapter=new NewsAdapter(a ,getApplicationContext());
            recyclerView.setAdapter(adapter);
            adapter.setOnCardClickListner(RecActivity.this);

            startMove2(a);
            rs.cancel(true);
            super.onPostExecute(aVoid);
        }
    }

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    private void startMove2(ArrayList<News> a) {


        weatherR = (ViewGroup) findViewById(R.id.weatherN);
        containerR = (ViewGroup) findViewById(R.id.containerR);

        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(500);
        TransitionManager.beginDelayedTransition(weatherR, autoTransition);

        myService.goToTop();

        RelativeLayout.LayoutParams map = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        weatherR.setLayoutParams(map);
        myService.isShowed=true;

    }

    public void salutoConv(JSONObject j) {
        googleSpeech(speech);
    }

    public void saluto(JSONObject j) {
        googleSpeech(speech);
    }

    public void stopMus(JSONObject j) throws JSONException {
        myService.stopMusic( t1, speech);
    }

    public void playMusic(JSONObject j) throws JSONException {
        JSONObject parameters=j.getJSONObject("parameters");
        String song=parameters.getString("any");
        myService.playMusic(song, t1, speech);
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
        myService.playMusic(song, t1, speech);
    }

    public void meteoRequest(JSONObject j) {
        googleSpeech(speech);
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

                citta = getCompleteAddressString(loca.getLatitude(), loca.getLongitude());
                we.execute();

            } catch (Exception e) {

            }
        }
    }

    private void googleSpeech(String speech) {
        Log.i("speech", speech);
        t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "maya");
        sp.cancel();
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




        //callBack();
    }

    private void callBack2() {
        myService.resumeRecon();
    }

    private void callBack() {
        myService.resumeRecon();
        finish();
    }

    private void print(String msg, Object... args) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(msg, args)));
        startActivity(browserIntent);
        //pr.setVisibility(View.GONE);
        callBack();
    }

    @Override
    protected void onPause() {
        t1.stop();
        overridePendingTransition(0, 0);
        super.onPause();
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

    private void dialContactPhone(final String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            for (int i = 0; i < 50; i++) {
                Log.i("Non funzia?", "no");
            }
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneNumber, null)));
        callBack();
    }




    public String[] getNumber(ContentResolver cr, String s) {
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        if(cursor.getCount()>0) {
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


       private class WeatherPArser extends AsyncTask<String, Void, Channel>  {
            @Override
            protected Channel doInBackground(String[] locations) {
                Channel channel = new Channel();

                String endpoint ="https://api.darksky.net/forecast/37209fe09b2849fd4a010c49a6c86f1b/"+lat+","+longit+"?lang=it&units=si&exclude=minutely,hourly,daily,alerts,flags";
                Log.i("traccia", endpoint);
                try {
                    URL url = new URL(endpoint);

                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);

                    InputStream inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                        Log.i("traccia", line);
                    }

                    JSONObject data = new JSONObject(result.toString());

                    JSONObject queryResults = data.optJSONObject("currently");
;
                    channel.populate(queryResults);

                    return channel;

                } catch (Exception e) {
                    error = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Channel channel) {
                Log.i("traccia", "fino a qui tutto ok");
                c=channel;
                startMove(c);


            }

        }

    private class JSOUPParser extends AsyncTask<String, Void, Channel>  {
        @Override
        protected Channel doInBackground(String[] search) {
            {
                String google = "http://www.google.com/search?q="+search[0];
                String charset = "UTF-8";

                Document doc = null;
                try {
                    doc = Jsoup.connect(google).get();
                } catch (Exception e) {
                    Log.i("prova", "errore");
                    e.printStackTrace();
                }
                Elements links = doc.select("a[href]");
                Elements media = doc.select("[src]");
                Elements imports = doc.select("link[href]");

                Log.i("link", links.get(0).attr("abs:href"));

                print(links.get(0).attr("abs:href"));


            }
            return null;
        }

        @Override
        protected void onPostExecute(Channel channel) {



        }

    }





    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void startMove(Channel channel) {

        //t1.speak("Oggi ci sono "+ channel.getTemperatura().substring(0, channel.getTemperatura().indexOf('.'))+" gradi ed è prevista "+channel.getSommario() , TextToSpeech.QUEUE_FLUSH, null, "firstLook");

        weatherIconImageView = (ImageView) findViewById(R.id.weatherIconImageView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);


        weatherIconImageView.setImageDrawable(getDrawable(R.drawable.icon_47));

        temperatureTextView.setText(channel.getTemperatura()+"℃");
        conditionTextView.setText(channel.getSommario());

        locationTextView.setText(citta);

        try {
            weatherR = (ViewGroup) findViewById(R.id.weatherR);
            containerR = (ViewGroup) findViewById(R.id.containerR);
            Log.i("loc", "DONE222");

            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(500);
            TransitionManager.beginDelayedTransition(weatherR, autoTransition);

            RelativeLayout.LayoutParams map = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            weatherR.setLayoutParams(map);
            myService.isShowed=true;



            Log.i("loc", "Hai finito");


        } catch (NullPointerException e) {

        } catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {

        if(newsShowed) {
            Log.i("ciao", "onBackPressed: ");
            lWeb = (ViewGroup) findViewById(R.id.relativeWeb);

            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(500);
            TransitionManager.beginDelayedTransition(lWeb, autoTransition);

            if (Build.VERSION.SDK_INT < 18) {
                vw.clearView();
            } else {
                vw.loadUrl("about:blank");
            }

            RelativeLayout.LayoutParams map2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,0);
            lWeb.setLayoutParams(map2);
            newsShowed=false;
        }else {
            t1.stop();
            callBack2();
            super.onBackPressed();
        }


    }
}