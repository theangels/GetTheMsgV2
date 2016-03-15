package ubibots.weatherbase.model;


import lecho.lib.hellocharts.view.ColumnChartView;

public class ColumnView {
    ColumnChartView temperatureView;
    ColumnChartView humidityView;

    public ColumnView(ColumnChartView temperatureView, ColumnChartView humidityView) {
        this.temperatureView = temperatureView;
        this.humidityView = humidityView;
    }

    public ColumnChartView getTemperatureView() {
        return temperatureView;
    }

    public ColumnChartView getHumidityView() {
        return humidityView;
    }
}
