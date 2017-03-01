package ubibots.weatherbase.ui;

import android.app.Activity;
import android.widget.TextView;

import ubibots.weatherbase.R;
import ubibots.weatherbase.control.ChangePage;
import ubibots.weatherbase.control.RequestHour;
import ubibots.weatherbase.model.BeanCurrentView;

public class DisplayView {

    public DisplayView(Activity activity) {
        BeanCurrentView currentView = new BeanCurrentView();
        currentView.setCurrentTemperature((TextView) activity.findViewById(R.id.currentTemperature));
        currentView.setCurrentHumidity((TextView) activity.findViewById(R.id.currentHumidity));
        currentView.setCurrentAirPressure((TextView) activity.findViewById(R.id.currentAirPressure));
        currentView.setCurrentPM2_5((TextView) activity.findViewById(R.id.currentPM));
        currentView.setCurrentWindSpeed((TextView) activity.findViewById(R.id.currentWindSpeed));
        currentView.setCurrentWindDirection((TextView) activity.findViewById(R.id.currentWindDirection));
        HourView hourView = new HourView(activity, currentView);
        DayView dayView = new DayView(activity);
        new RequestHour(hourView, dayView).executeRequest();
        new ListTab(activity, hourView, dayView);
        new MonitorView(activity);
        new ChangePage(hourView, dayView);
    }
}
