package com.MayaProject.Ragusa.Federico.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.cmu.pocketsphinx.demo.R;

/**
 * Created by Federico on 24/11/2016.
 */
public class MeteoAdapter extends RecyclerView.Adapter<MeteoAdapter.PlanetViewHolder> {

    ArrayList<String> songList;

    public MeteoAdapter(ArrayList<String> planetList, Context context) {
        this.songList = planetList;
    }

    @Override
    public MeteoAdapter.PlanetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.meteo_row,parent,false);
        PlanetViewHolder viewHolder=new PlanetViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MeteoAdapter.PlanetViewHolder holder, int position) {
        holder.text.setText(songList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class PlanetViewHolder extends RecyclerView.ViewHolder{

        protected ImageView image;
        protected TextView text;

        public PlanetViewHolder(View itemView) {
            super(itemView);
            image= (ImageView) itemView.findViewById(R.id.image_id);
            text= (TextView) itemView.findViewById(R.id.text_id);
        }
    }
}