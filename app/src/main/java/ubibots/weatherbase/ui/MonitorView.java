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
                    /*
                    错误常数

                        MEDIA_ERROR_IO
                        文件不存在或错误，或网络不可访问错误
                        值: -1004 (0xfffffc14)

                        MEDIA_ERROR_MALFORMED
                        流不符合有关标准或文件的编码规范
                        值: -1007 (0xfffffc11)

                        MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK
                        视频流及其容器不适用于连续播放视频的指标（例如：MOOV原子）不在文件的开始.
                        值: 200 (0x000000c8)

                        MEDIA_ERROR_SERVER_DIED
                        媒体服务器挂掉了。此时，程序必须释放MediaPlayer 对象，并重新new 一个新的。
                        值: 100 (0x00000064)

                        MEDIA_ERROR_TIMED_OUT
                        一些操作使用了过长的时间，也就是超时了，通常是超过了3-5秒
                        值: -110 (0xffffff92)

                        MEDIA_ERROR_UNKNOWN
                        未知错误
                        值: 1 (0x00000001)

                        MEDIA_ERROR_UNSUPPORTED
                        比特流符合相关编码标准或文件的规格，但媒体框架不支持此功能
                        值: -1010 (0xfffffc0e)


                        what int: 标记的错误类型:
                            MEDIA_ERROR_UNKNOWN
                            MEDIA_ERROR_SERVER_DIED
                        extra int: 标记的错误类型.
                            MEDIA_ERROR_IO
                            MEDIA_ERROR_MALFORMED
                            MEDIA_ERROR_UNSUPPORTED
                            MEDIA_ERROR_TIMED_OUT
                            MEDIA_ERROR_SYSTEM (-2147483648) - low-level system error.

                        * */
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
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    Toast.makeText(ContextUtil.getInstance(),
                            "正在缓冲",
                            Toast.LENGTH_LONG).show();
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    videoView.destroyDrawingCache();
                }
                return false;
            }
        });
    }
}
