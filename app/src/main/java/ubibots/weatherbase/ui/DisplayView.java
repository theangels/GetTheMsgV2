package ubibots.weatherbase.ui;

import android.widget.TextView;

import ubibots.weatherbase.DisplayHistoryActivity;
import ubibots.weatherbase.R;
import ubibots.weatherbase.control.RequestHour;
import ubibots.weatherbase.model.BeanCurrentView;

public class DisplayView {
    private static BeanCurrentView currentView;

    public static BeanCurrentView getCurrentView() {
        return currentView;
    }

    public DisplayView() {
        currentView = new BeanCurrentView();
        currentView.setCurrentTemperature((TextView) DisplayHistoryActivity.getActivity().findViewById(R.id.currentTemperature));
        currentView.setCurrentHumidity((TextView) DisplayHistoryActivity.getActivity().findViewById(R.id.currentHumidity));
        currentView.setCurrentAirPressure((TextView) DisplayHistoryActivity.getActivity().findViewById(R.id.currentAirPressure));
        currentView.setCurrentPM2_5((TextView) DisplayHistoryActivity.getActivity().findViewById(R.id.currentPM));
        currentView.setCurrentWindSpeed((TextView) DisplayHistoryActivity.getActivity().findViewById(R.id.currentWindSpeed));
        currentView.setCurrentWindDirection((TextView) DisplayHistoryActivity.getActivity().findViewById(R.id.currentWindDirection));
        new HourView();
        new RequestHour().executeRequest();
        new DayView();
        new ListTab();
    }
}
