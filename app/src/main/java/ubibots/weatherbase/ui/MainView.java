package ubibots.weatherbase.ui;


import ubibots.weatherbase.control.RequestHour;

public class MainView {
    public MainView() {
        new HourView();
        new RequestHour().executeRequest();
        new DayView();
        new ListTab();
    }
}
