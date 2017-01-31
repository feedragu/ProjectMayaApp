package com.MayaProject.Ragusa.Federico.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Federico on 22/11/2016.
 */

public class RssParser extends AsyncTask<Object, Object, ArrayList<News>> {

    private Context cont;
    private String urlA="http://www.repubblica.it/rss/homepage/rss2.0.xml";
    private URL url;
    private ArrayList<News> a=new ArrayList<>();

    public RssParser(Context c) {
        cont=c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<News> doInBackground(Object... voids) {

        try {
            url=new URL(urlA);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream input=conn.getInputStream();
            DocumentBuilderFactory doc= DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=doc.newDocumentBuilder();
            Document xmlDoc=builder.parse(input);
            Element root=xmlDoc.getDocumentElement();
            Node n=root.getLastChild();
            NodeList items=n.getChildNodes();
            for(int i=0; i<items.getLength(); i++) {
                Node currentChild = items.item(i);
                if (currentChild.getNodeName().equalsIgnoreCase("item")) {
                    NodeList itemChilds = currentChild.getChildNodes();
                    for (int j = 0; j < itemChilds.getLength(); j++) {
                        Node current = itemChilds.item(j);
                        if (current.getNodeName().equalsIgnoreCase("title")) {
                            Node cT = current.getFirstChild();
                            Log.i("conterct " + j, cT.getTextContent());

                        } else if (current.getNodeName().equalsIgnoreCase("link")) {
                            Node cT = current.getFirstChild();
                            Log.i("link " + j, cT.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("enclosure")) {
                            Element e = (Element) current;
                            Log.i("url " + j, e.getAttribute("url").toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return a;
    }

    @Override
    protected void onPostExecute(ArrayList<News> aVoid) {
        super.onPostExecute(aVoid);
    }
}
