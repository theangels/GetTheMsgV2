package ubibots.weatherbase.ui;

import android.net.Uri;
import android.util.Log;

import ubibots.weatherbase.DisplayHistoryActivity;
import ubibots.weatherbase.R;
import ubibots.weatherbase.util.MyVideoView;

public class MonitorView {

    private static MyVideoView videoView;

    public static MyVideoView getVideoView() {
        return videoView;
    }

    public MonitorView(){
        videoView = (MyVideoView) DisplayHistoryActivity.getActivity().findViewById(R.id.video);
        videoView.setVideoURI(Uri.parse("rtsp://admin:ZUCCli511@192.168.0.64:554/h264/ch1/sub"));
        videoView.requestFocus();
        Log.i("Tag","Start!");
    }
}
