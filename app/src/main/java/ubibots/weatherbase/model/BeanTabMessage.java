package ubibots.weatherbase.model;

import java.util.ArrayList;

public class BeanTabMessage {
    public int count = 0;
    private ArrayList<Double> temperature;
    private ArrayList<Double> rainFall;
    private ArrayList<Double> humidity;
    private ArrayList<Double> windSpeed;
    private ArrayList<Double> air;
    private ArrayList<Double> windDirection;
    private ArrayList<Double> pressure;
    private ArrayList<String> timeStamp;

    public BeanTabMessage(ArrayList<Double> temperature, ArrayList<Double> rainFall, ArrayList<Double> humidity, ArrayList<Double> windSpeed, ArrayList<Double> air, ArrayList<Double> windDirection, ArrayList<Double> pressure, ArrayList<String> timeStamp) {
        this.temperature = temperature;
        this.rainFall = rainFall;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.air = air;
        this.windDirection = windDirection;
        this.pressure = pressure;
        this.timeStamp = timeStamp;
    }

    public ArrayList<Double> getTemperature() {
        return temperature;
    }

    public ArrayList<Double> getRainFall() {
        return rainFall;
    }

    public ArrayList<Double> getHumidity() {
        return humidity;
    }

    public ArrayList<Double> getWindSpeed() {
        return windSpeed;
    }

    public ArrayList<Double> getAir() {
        return air;
    }

    public ArrayList<Double> getWindDirection() {
        return windDirection;
    }

    public ArrayList<Double> getPressure() {
        return pressure;
    }

    public ArrayList<String> getTimeStamp() {
        return timeStamp;
    }
}
