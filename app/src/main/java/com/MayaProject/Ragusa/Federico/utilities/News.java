package com.MayaProject.Ragusa.Federico.utilities;

/**
 * Created by Federico on 25/11/2016.
 */

public class News {
    private String urlImg;
    private String title;
    private String descr;
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public News(String urlImg, String title, String descr, String urlI) {

        this.urlImg = urlImg;
        this.title = title;
        this.descr = descr;
        link=urlI;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getTitle() {
        return title;
    }

    public String getDescr() {
        return descr;
    }
}
