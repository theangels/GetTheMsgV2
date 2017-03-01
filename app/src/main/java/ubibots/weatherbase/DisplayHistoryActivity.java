package ubibots.weatherbase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import ubibots.weatherbase.ui.DisplayView;
import ubibots.weatherbase.ui.MonitorView;

public class DisplayHistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        new DisplayView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MonitorView.thisClass != null)
            MonitorView.thisClass.getVideoView().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MonitorView.thisClass != null){
            MonitorView.thisClass.getVideoView().resume();
	}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MonitorView.thisClass != null)
            MonitorView.thisClass.getVideoView().stopPlayback();
    }
}