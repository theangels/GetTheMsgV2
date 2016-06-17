package ubibots.weatherbase.ui;

import ubibots.weatherbase.control.RequestHour;

public class DisplayView {

    public DisplayView() {
        new HourView();
        new RequestHour().executeRequest();
        new DayView();
        new RecommandView();
        new ListTab();
    }
}
