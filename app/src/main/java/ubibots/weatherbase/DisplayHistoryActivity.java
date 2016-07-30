package ubibots.weatherbase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import ubibots.weatherbase.ui.DisplayView;
import ubibots.weatherbase.ui.MonitorView;

public class DisplayHistoryActivity extends Activity {

    private static Context context;
    private static DisplayHistoryActivity activity;

    public static Context getContext() {
        return context;
    }

    public static DisplayHistoryActivity getActivity() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_main);
        context = this;
        activity = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        new DisplayView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MonitorView.getVideoView() != null)
            MonitorView.getVideoView().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MonitorView.getVideoView() != null)
            MonitorView.getVideoView().resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MonitorView.getVideoView() != null)
            MonitorView.getVideoView().stopPlayback();
    }
}