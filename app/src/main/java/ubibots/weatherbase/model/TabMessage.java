package ubibots.weatherbase.model;

import java.util.ArrayList;

public class TabMessage {
    public static int delay = 30000;
    public int count = 0;
    private ArrayList<Double> temperature;
    private ArrayList<Double> humidity;
    private ArrayList<String> date;

    public TabMessage(ArrayList<Double> temperature, ArrayList<Double> humidity, ArrayList<String> date) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.date = date;
    }

    public ArrayList<Double> getTemperature() {
        return temperature;
    }

    public void setTemperature(ArrayList<Double> temperature) {
        this.temperature = temperature;
    }

    public ArrayList<Double> getHumidity() {
        return humidity;
    }

    public void setHumidity(ArrayList<Double> humidity) {
        this.humidity = humidity;
    }

    public ArrayList<String> getDate() {
        return date;
    }

    public void setDate(ArrayList<String> date) {
        this.date = date;
    }
}
