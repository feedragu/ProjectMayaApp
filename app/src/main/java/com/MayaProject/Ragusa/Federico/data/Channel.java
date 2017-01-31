
package com.MayaProject.Ragusa.Federico.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Channel implements JSONPopulator {

    private String sommario, icona, temperatura, vento;

    public String getSommario() {
        return sommario;
    }

    public void setSommario(String sommario) {
        this.sommario = sommario;
    }

    public String getIcona() {
        return icona;
    }

    public void setIcona(String icona) {
        this.icona = icona;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getVento() {
        return vento;
    }

    public void setVento(String vento) {
        this.vento = vento;
    }

    @Override
    public void populate(JSONObject data) {
        try {
            sommario=data.getString("summary");
            icona=data.getString("icon");
            temperatura=data.getString("temperature");
            vento=data.getString("windSpeed");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public JSONObject toJSON() {

        JSONObject data = new JSONObject();


        return data;
    }

}
