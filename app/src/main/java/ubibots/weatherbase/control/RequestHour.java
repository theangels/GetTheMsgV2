package ubibots.weatherbase.control;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ubibots.weatherbase.model.BeanConstant;
import ubibots.weatherbase.model.BeanTabMessage;
import ubibots.weatherbase.ui.DayView;
import ubibots.weatherbase.ui.HourView;
import ubibots.weatherbase.util.ContextUtil;
import ubibots.weatherbase.util.URLUtil;

public class RequestHour {

    private static RequestHourHandler requestHourHandler;
    private static HourView hourView;
    private static DayView dayView;

    public RequestHour(HourView hourView, DayView dayView) {
        RequestHour.hourView = hourView;
        RequestHour.dayView = dayView;
        requestHourHandler = new RequestHourHandler();
    }

    public void executeRequest() {
        hourView.hour = new BeanTabMessage(new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<String>());
        Calendar hourCalendar = Calendar.getInstance();
        hourCalendar.set(Calendar.SECOND, hourCalendar.get(Calendar.SECOND) - BeanConstant.delayHour / 1000 * (RequestHourHistory.MAX - 1));
        for (int i = 0; i < RequestHourHistory.MAX; i++) {
            hourView.hour.getTemperature().add(0.0);
            hourView.hour.getRainFall().add(0.0);
            hourView.hour.getHumidity().add(0.0);
            hourView.hour.getWindSpeed().add(0.0);
            hourView.hour.getAir().add(0.0);
            hourView.hour.getWindDirection().add(0.0);
            hourView.hour.getPressure().add(0.0);
            hourView.hour.getTimeStamp().add("");
            hourHistory(hourView.hour, hourCalendar, i);
            hourCalendar.set(Calendar.SECOND, hourCalendar.get(Calendar.SECOND) + BeanConstant.delayHour / 1000);
        }

        Toast.makeText(ContextUtil.getInstance(), "正在获取数据中,请耐心等待...",
                Toast.LENGTH_LONG).show();
    }

    private void hourHistory(BeanTabMessage hour, Calendar calendar, int id) {
        String strUrl = URLUtil.combineUrl((Calendar) calendar.clone());
        RequestHourHistory request = new RequestHourHistory(hourView, dayView, hour, id, 0);
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

    static Timer getRequestHourTimer() {
        return requestHourTimer;
    }

    static TimerTask getRequestHourTask() {
        return requestHourTask;
    }

    private static class RequestHourHandler extends Handler {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Calendar calendar = Calendar.getInstance();
                hourStep(hourView.hour, calendar);
            }
            super.handleMessage(msg);
        }
    }

    private static void hourStep(BeanTabMessage hour, Calendar calendar) {
        String strUrl = URLUtil.combineUrl((Calendar) calendar.clone());
        RequestHourStep request = new RequestHourStep(hourView, hour, 0);
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strUrl);
    }
}
