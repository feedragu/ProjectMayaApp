package com.MayaProject.Ragusa.Federico.utilities;

import android.app.PendingIntent;
import android.graphics.drawable.BitmapDrawable;

import java.io.Serializable;

/**
 * Created by Federico on 23/01/2017.
 */

public class NotificationMaya implements Serializable{

    public PendingIntent p;
    public String title;
    public String text;
    public BitmapDrawable icon;

    public NotificationMaya(String title, String text, BitmapDrawable icon, PendingIntent p) {
        this.title = title;
        this.text = text;
        this.icon = icon;
        this.p=p;
    }


    public PendingIntent getP() {
        return p;
    }


    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BitmapDrawable getIcon() {
        return icon;
    }

    public void setIcon(BitmapDrawable icon) {
        this.icon = icon;
    }

}
