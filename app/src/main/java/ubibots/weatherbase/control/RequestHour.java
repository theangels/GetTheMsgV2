package ubibots.weatherbase.control;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ubibots.weatherbase.DisplayHistoryActivity;
import ubibots.weatherbase.model.BeanConstant;
import ubibots.weatherbase.model.BeanTabMessage;
import ubibots.weatherbase.ui.HourView;
import ubibots.weatherbase.util.RequestUtil;
import ubibots.weatherbase.util.URLUtil;

public class RequestHour {
    private static RequestHourHandler requestHourHandler;

    public RequestHour() {
        requestHourHandler = new RequestHourHandler();
    }

    public void executeRequest() {
        HourView.setHour(new BeanTabMessage(new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<String>()));
        Calendar hourCalendar = Calendar.getInstance();
        hourCalendar.set(Calendar.SECOND, hourCalendar.get(Calendar.SECOND) - BeanConstant.delayHour / 1000 * (RequestHourHistory.MAX - 1));
        for (int i = 0; i < RequestHourHistory.MAX; i++) {
            HourView.getHour().getTemperature().add(0.0);
            HourView.getHour().getRainFall().add(0.0);
            HourView.getHour().getHumidity().add(0.0);
            HourView.getHour().getWindSpeed().add(0.0);
            HourView.getHour().getAir().add(0.0);
            HourView.getHour().getWindDirection().add(0.0);
            HourView.getHour().getPressure().add(0.0);
            HourView.getHour().getTimeStamp().add("");
            hourHistory(HourView.getHour(), hourCalendar, i);
            hourCalendar.set(Calendar.SECOND, hourCalendar.get(Calendar.SECOND) + BeanConstant.delayHour / 1000);
        }

        Toast.makeText(DisplayHistoryActivity.getContext(), "正在获取数据中,请耐心等待...",
                Toast.LENGTH_LONG).show();
    }

    public static void hourHistory(BeanTabMessage hour, Calendar calendar, int id) {
        String strUrl = URLUtil.combineUrl((Calendar) calendar.clone());
        RequestHourHistory request = new RequestHourHistory(hour, id, 0);
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strUrl);
    }

    private static Timer requestHourTimer = new Timer();
    private static TimerTask requestHourTask = new TimerTask() {
        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            requestHourHandler.sendMessage(message);
        }
    };

    public static Timer getRequestHourTimer() {
        return requestHourTimer;
    }

    public static TimerTask getRequestHourTask() {
        return requestHourTask;
    }

    static class RequestHourHandler extends Handler {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Calendar calendar = Calendar.getInstance();
                hourStep(HourView.getHour(), calendar);
            }
            super.handleMessage(msg);
        }
    }

    public static void hourStep(BeanTabMessage hour, Calendar calendar) {
        String strUrl = URLUtil.combineUrl((Calendar) calendar.clone());
        RequestHourStep request = new RequestHourStep(hour, 0);
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strUrl);
    }
}
