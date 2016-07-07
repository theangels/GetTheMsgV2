package ubibots.weatherbase.model;

import android.widget.TextView;

public class BeanCurrentView {
    private TextView currentTemperature;
    private TextView currentHumidity;
    private TextView currentAirPressure;
    private TextView currentPM2_5;

    public TextView getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(TextView currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public TextView getCurrentHumidity() {
        return currentHumidity;
    }

    public void setCurrentHumidity(TextView currentHumidity) {
        this.currentHumidity = currentHumidity;
    }

    public TextView getCurrentAirPressure() {
        return currentAirPressure;
    }

    public void setCurrentAirPressure(TextView currentAirPressure) {
        this.currentAirPressure = currentAirPressure;
    }

    public TextView getCurrentPM2_5() {
        return currentPM2_5;
    }

    public void setCurrentPM2_5(TextView currentPM2_5) {
        this.currentPM2_5 = currentPM2_5;
    }
}
