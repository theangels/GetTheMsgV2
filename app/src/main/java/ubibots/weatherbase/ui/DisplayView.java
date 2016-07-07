package ubibots.weatherbase.ui;

import android.widget.TextView;

import ubibots.weatherbase.DisplayHistoryActivity;
import ubibots.weatherbase.R;
import ubibots.weatherbase.control.RequestHour;

public class DisplayView {
    private static final TextView textView = (TextView)DisplayHistoryActivity.getActivity().findViewById(R.id.currentView);

    public static TextView getTextView() {
        return textView;
    }

    public DisplayView() {
        new HourView();
        new RequestHour().executeRequest();
        new DayView();
        new ListTab();
    }
}
