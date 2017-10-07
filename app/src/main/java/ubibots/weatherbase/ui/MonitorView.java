package ubibots.weatherbase.ui;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import ubibots.weatherbase.R;
import ubibots.weatherbase.model.MyVideoView;
import ubibots.weatherbase.util.ContextUtil;

public class MonitorView {
    private static final String TAG = MonitorView.class.getSimpleName();


    public static MonitorView thisClass;

    private MyVideoView videoView;

    public MyVideoView getVideoView() {
        return videoView;
    }

    MonitorView(Activity activity) {
        thisClass = this;

        videoView = (MyVideoView) activity.findViewById(R.id.video);
        final String url = ContextUtil.getInstance().getString(R.string.url_monitor);
        videoView.setVideoURI(Uri.parse(url));
        Log.i("Tag", "Start!");

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                Log.e(TAG, "What: " + what + ", Extra: " + extra);
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    //媒体服务器挂掉了。此时，程序必须释放MediaPlayer 对象，并重新new 一个新的。
                    Toast.makeText(ContextUtil.getInstance(),
                            "网络服务错误",
                            Toast.LENGTH_LONG).show();

                } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    if (extra == MediaPlayer.MEDIA_ERROR_IO) {
                        //文件不存在或错误，或网络不可访问错误
                        Toast.makeText(ContextUtil.getInstance(),
                                "网络文件错误",
                                Toast.LENGTH_LONG).show();
                    } else if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                        //超时
                        Toast.makeText(ContextUtil.getInstance(),
                                "网络超时",
                                Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                Log.e(TAG, "What: " + what + ", Extra: " + extra);
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    Toast.makeText(ContextUtil.getInstance(),
                            "正在缓冲",
                            Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }
}
