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

import com.MayaProject.Ragusa.Federico.utilities.News;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.cmu.pocketsphinx.demo.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Federico on 24/11/2016.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.PlanetViewHolder> {

    ArrayList<News> newsList;
    Context c;
    private OnCardClickListner onCardClickListner;

    public NewsAdapter(ArrayList<News> planetList, Context context) {
        this.newsList = planetList;
        c=context;
    }

    @Override
    public NewsAdapter.PlanetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.news_row,parent,false);
        PlanetViewHolder viewHolder=new PlanetViewHolder(v);
        return viewHolder;
    }






    @Override
    public void onBindViewHolder(final NewsAdapter.PlanetViewHolder holder, final int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                onCardClickListner.OnCardClicked(v, position);
            }
        });

        if(newsList.get(position).getUrlImg().equalsIgnoreCase("nope")) {
            Picasso.with(c)
                    .load(R.drawable._195)
                    .resize(1, 1)
                    .into(holder.image);
            }else {
                Picasso.with(c)
                        .load(newsList.get(position).getUrlImg())
                        .resize(320,240)
                        .into(holder.image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                //Try again online if cache failed
                                Picasso.with(c)
                                        .load(newsList.get(position).getUrlImg())
                                        .resize(320,240)
                                        .into(holder.image, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError() {
                                                Log.v("Picasso","Could not fetch image");
                                            }
                                        });
                            }
                        });
            }
            if(!(newsList.get(position).getDescr().equalsIgnoreCase("nope"))) {
                holder.descr.setText(newsList.get(position).getDescr());
            }else {
                holder.descr.setVisibility(View.GONE);
            }
            holder.title.setText(newsList.get(position).getTitle());

    }



    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class PlanetViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        protected ImageView image;
        protected TextView title;
        protected TextView descr;
        protected LinearLayout l, i;
        protected int co=0;


        public PlanetViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            image= (ImageView) itemView.findViewById(R.id.imgView);
            title= (TextView) itemView.findViewById(R.id.title);
            descr=(TextView) itemView.findViewById(R.id.descr);
            l= (LinearLayout) itemView.findViewById(R.id.linearL);
            i= (LinearLayout) itemView.findViewById(R.id.linearI);

        }
    }

    public interface OnCardClickListner {
        void OnCardClicked(View view, int position);
    }

    public void setOnCardClickListner(OnCardClickListner onCardClickListner) {
        this.onCardClickListner = onCardClickListner;
    }
}