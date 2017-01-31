package com.MayaProject.Ragusa.Federico.utilities;

/**
 * Created by Federico on 23/11/2016.
 */

public class Songs
{
    private long mSongID;
    private String mSongTitle;
    private String artist;
    private String path;

    public Songs(long id, String title, String artist, String pa){
        mSongID = id;
        mSongTitle = title;
        path=pa;
    }

    public long getSongID(){
        return mSongID;
    }

    public String getSongTitle(){
        return mSongTitle;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

}