package ubibots.weatherbase.model;

import lecho.lib.hellocharts.view.LineChartView;

public class BeanLineView {

    LineChartView temperatureView;
    LineChartView humidityView;
    LineChartView airView;

    public BeanLineView(LineChartView temperatureView, LineChartView humidityView, LineChartView airView) {
        this.temperatureView = temperatureView;
        this.humidityView = humidityView;
        this.airView = airView;
    }

    public LineChartView getTemperatureView() {
        return temperatureView;
    }

    public LineChartView getHumidityView() {
        return humidityView;
    }

    public LineChartView getAirView() {
        return airView;
    }
}
