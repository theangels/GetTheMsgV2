package ubibots.weatherbase.ui;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import ubibots.weatherbase.R;
import ubibots.weatherbase.model.MyVideoView;
import ubibots.weatherbase.util.ContextUtil;

public class MonitorView {

    public static MonitorView thisClass;

    private MyVideoView videoView;

    public MyVideoView getVideoView() {
        return videoView;
    }

    MonitorView(Activity activity){
        thisClass = this;

        videoView = (MyVideoView) activity.findViewById(R.id.video);
        String url = ContextUtil.getInstance().getString(R.string.url_monitor);
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        Log.i("Tag","Start!");
    }
}
