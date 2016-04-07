package ubibots.weatherbase.model;


import lecho.lib.hellocharts.view.LineChartView;

public class BeanLineView {
    LineChartView temperatureView;
    LineChartView humidityView;

    public BeanLineView(LineChartView temperatureView, LineChartView humidityView) {
        this.temperatureView = temperatureView;
        this.humidityView = humidityView;
    }

    public LineChartView getTemperatureView() {
        return temperatureView;
    }

    public LineChartView getHumidityView() {
        return humidityView;
    }
}
