package com.MayaProject.Ragusa.Federico;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.MayaProject.Ragusa.Federico.Adapter.NotificationAdapter;
import com.MayaProject.Ragusa.Federico.utilities.NotificationMaya;

import java.util.LinkedList;

import edu.cmu.pocketsphinx.demo.R;


public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.OnCardClickListner {

    public static final String mBroadcastStringAction = "federico";
    public static final String getList = "lista.notifiche";
    public static final String mBroadcastIntegerAction = "com.truiton.broadcast.integer";
    public static final String mBroadcastNotificrAction = "com.truiton.broadcast.notific";

    private IntentFilter mIntentFilter;
    private LinkedList<NotificationMaya> list=new LinkedList<>();
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    public NotificationAdapter adapter;
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifcation_activity);

        recyclerView= (RecyclerView) findViewById(R.id.list_notification);
        layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter=new NotificationAdapter(list ,getApplicationContext());
        recyclerView.setAdapter(adapter);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
        mIntentFilter.addAction(mBroadcastIntegerAction);

        registerReceiver(mReceiver, mIntentFilter);

        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(20,  ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(NotificationActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                remove(viewHolder.getAdapterPosition());
            }
        };

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(mBroadcastNotificrAction);
        sendBroadcast(broadcastIntent);
    }

    private void remove(int adapterPosition) {
        list.remove(adapterPosition);
        adapter.notifyItemRemoved(adapterPosition);
    }

    private ItemTouchHelper itemTouchHelper;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public byte[] by=null;
        public BitmapDrawable ob=null;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("notificationActivity", intent.getAction());
            if (intent.getAction().equals(mBroadcastIntegerAction)) {
                Bundle b=intent.getExtras();
                String title = b.getString("title");
                String text = b.getString("text");
                PendingIntent p=b.getParcelable("pending");
                Log.i("title",  title);
                Log.i("text",  text);
                by= b.getByteArray("picture");
                if (by != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(by, 0, by.length);
                    ob = new BitmapDrawable(getResources(), bmp);
                    Log.i("icon",  ob.toString());
                }
                NotificationMaya nm=new NotificationMaya(title, text, ob, p);
                list.add(nm);

            }else if(intent.getAction().equals(mBroadcastStringAction)) {
                /*adapter=new NotificationAdapter(list ,getApplicationContext());
                recyclerView.setAdapter(adapter);*/
                adapter.notifyDataSetChanged();
                adapter.setOnCardClickListner(NotificationActivity.this);
                itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);
            }
        }
    };

    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        registerReceiver(mReceiver, mIntentFilter);
    }

    /*protected void onPause()
    {

        //this.getCurrentFocus().startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_up));
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
        unregisterReceiver(mReceiver);

        super.onPause();
    }*/

    @Override
    public void finish() {
        unregisterReceiver(mReceiver);
        super.finish();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void OnCardClicked(View view, int position, NotificationAdapter.NotificationHolder holder) {
        //itemTouchHelper.startSwipe(holder);
        Log.i("click", "clicked");
        if(list.get(position).getP()!=null) {
            try {
                list.get(position).getP().send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }


    }

}
