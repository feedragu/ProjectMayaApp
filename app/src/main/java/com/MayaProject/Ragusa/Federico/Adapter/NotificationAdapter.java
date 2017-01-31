package com.MayaProject.Ragusa.Federico.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.MayaProject.Ragusa.Federico.utilities.NotificationMaya;

import java.util.LinkedList;

import edu.cmu.pocketsphinx.demo.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Federico on 24/01/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    LinkedList<NotificationMaya> newsList;
    Context c;
    private NotificationAdapter.OnCardClickListner onCardClickListner;

    public NotificationAdapter(LinkedList<NotificationMaya> planetList, Context context) {
        this.newsList = planetList;
        c=context;
    }

    @Override
    public NotificationAdapter.NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row,parent,false);
        NotificationAdapter.NotificationHolder viewHolder=new NotificationAdapter.NotificationHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NotificationHolder holder, final int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                onCardClickListner.OnCardClicked(v, position, holder);
            }
        });

        if(newsList.get(position).getIcon()!=null) {
            holder.image.setImageBitmap(newsList.get(position).getIcon().getBitmap());
        }
        holder.title.setText(newsList.get(position).getTitle());
        String adjusted = newsList.get(position).getText().replaceAll("(?m)^[ \t]*\r?\n", "");
        holder.descr.setText(adjusted);

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        protected ImageView image;
        protected TextView title;
        protected TextView descr;
        protected LinearLayout l, i;
        protected int co=0;


        public NotificationHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view_n);
            image= (ImageView) itemView.findViewById(R.id.icon);
            title= (TextView) itemView.findViewById(R.id.title);
            descr=(TextView) itemView.findViewById(R.id.mex);
            /*l= (LinearLayout) itemView.findViewById(R.id.linearL);
            i= (LinearLayout) itemView.findViewById(R.id.linearI);*/

        }
    }

    public interface OnCardClickListner {
        void OnCardClicked(View view, int position, NotificationHolder holder);
    }

    public void setOnCardClickListner(NotificationAdapter.OnCardClickListner onCardClickListner) {
        this.onCardClickListner = onCardClickListner;
    }
}
