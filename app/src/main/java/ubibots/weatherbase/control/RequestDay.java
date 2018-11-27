package ubibots.weatherbase.control;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ubibots.weatherbase.model.BeanConstant;
import ubibots.weatherbase.model.BeanTabMessage;
import ubibots.weatherbase.ui.DayView;
import ubibots.weatherbase.util.ContextUtil;
import ubibots.weatherbase.util.URLUtil;

class RequestDay {

    private static DayView dayView;

    private static RequestDayHandler requestDayHandler;

    RequestDay(DayView dayView) {
        RequestDay.dayView = dayView;
        requestDayHandler = new RequestDayHandler();
    }

    void executeRequest() {
        dayView.day = new BeanTabMessage(new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<String>(), new ArrayList<Double>(), new ArrayList<String>());
        Calendar dayCalendar = Calendar.getInstance();
        dayCalendar.set(Calendar.SECOND, dayCalendar.get(Calendar.SECOND) - BeanConstant.delayDay / 1000 * (RequestDayHistory.MAX - 1));
        for (int i = 0; i < RequestDayHistory.MAX; i++) {
            dayView.day.getTemperature().add(0.0);
            dayView.day.getRainFall().add(0.0);
            dayView.day.getHumidity().add(0.0);
            dayView.day.getWindSpeed().add(0.0);
            dayView.day.getAir().add(0.0);
            dayView.day.getWindDirection().add("");
            dayView.day.getPressure().add(0.0);
            dayView.day.getTimeStamp().add("");
            dayHistory(dayView.day, dayCalendar, i);
            dayCalendar.set(Calendar.SECOND, dayCalendar.get(Calendar.SECOND) + BeanConstant.delayDay / 1000);
        }

        Toast.makeText(ContextUtil.getInstance(), "正在获取数据中,请耐心等待...",
                Toast.LENGTH_LONG).show();
    }

    private void dayHistory(BeanTabMessage day, Calendar calendar, int id) {
        String strUrl = URLUtil.combineUrl((Calendar) calendar.clone());
        RequestDayHistory request = new RequestDayHistory(dayView, day, id, 0);
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strUrl);
    }

    private static Timer requestDayTimer = new Timer();
    private static TimerTask requestDayTask = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            requestDayHandler.sendMessage(message);
        }
    };

    static Timer getRequestDayTimer() {
        return requestDayTimer;
    }

    static TimerTask getRequestDayTask() {
        return requestDayTask;
    }

    private static class RequestDayHandler extends Handler {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Calendar calendar = Calendar.getInstance();
                dayStep(dayView.day, calendar);
            }
            super.handleMessage(msg);
        }
    }

    private static void dayStep(BeanTabMessage day, Calendar calendar) {
        String strUrl = URLUtil.combineUrl((Calendar) calendar.clone());
        RequestDayStep request = new RequestDayStep(dayView, day, 0);
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strUrl);
    }
}
