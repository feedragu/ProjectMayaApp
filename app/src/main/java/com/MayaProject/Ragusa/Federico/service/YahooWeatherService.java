/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yoel Nunez <dev@nunez.guru>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package com.MayaProject.Ragusa.Federico.service;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.MayaProject.Ragusa.Federico.data.Channel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class YahooWeatherService {
    private Exception error;
    public String temperatureUnit = "C";


    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public void refreshWeather(String location) {

        new AsyncTask<String, Void, Channel>() {
            @Override
            protected Channel doInBackground(String[] locations) {

                String location = locations[0];

                Channel channel = new Channel();


                String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"Via Bergognone, 45, 20144 Milano, Italy\") and u='c'");
                Log.i("end point", YQL);
                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));

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

                    JSONObject queryResults = data.optJSONObject("query");

                    int count = queryResults.optInt("count");

                    if (count == 0) {
                        error = new LocationWeatherException("No weather information found for " + location);
                        return null;
                    }

                    JSONObject channelJSON = queryResults.optJSONObject("results").optJSONObject("channel");
                    channel.populate(channelJSON);

                    return channel;

                } catch (Exception e) {
                    error = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Channel channel) {

            }

        }.execute(location);
    }

    public static class LocationWeatherException extends Exception {
        public LocationWeatherException(String detailMessage) {
            super(detailMessage);
        }
    }
}
