package ubibots.weatherbase.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.view.LineChartView;
import ubibots.weatherbase.MainActivity;
import ubibots.weatherbase.R;
import ubibots.weatherbase.control.RequestDay;
import ubibots.weatherbase.control.RequestDayHistory;
import ubibots.weatherbase.model.BeanConstant;
import ubibots.weatherbase.model.BeanLineView;
import ubibots.weatherbase.model.BeanTabMessage;

public class DayView {
    private static BeanLineView dayBeanLineView;
    private static BeanTabMessage day;
    private static RequestDay requestDay;
    private static List<View> dayViewList;
    private static TextView[] dayDots;
    private static int dayCurrentIndex;
    private static RequestDayHandler requestDayHandler;
    private static ViewPager dayViewPager;
    private static ProgressBar dayProgressBar;

    public static BeanLineView getDayBeanLineView() {
        return dayBeanLineView;
    }

    public static ViewPager getDayViewPager() {
        return dayViewPager;
    }

    public static ProgressBar getDayProgressBar() {
        return dayProgressBar;
    }

    private PagerAdapter dayPagerAdapter = new PagerAdapter() {
        //官方建议这么写
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        //返回一共有多少个界面
        @Override
        public int getCount() {
            return dayViewList.size();
        }

        //实例化一个item
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(dayViewList.get(position));
            return dayViewList.get(position);
        }

        //销毁一个item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(dayViewList.get(position));
        }

    };

    public DayView() {
        dayViewInit();

        day = new BeanTabMessage(new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<String>());
        requestDay = new RequestDay();
        Calendar dayCalendar = Calendar.getInstance();
        dayCalendar.set(Calendar.SECOND, dayCalendar.get(Calendar.SECOND) - BeanConstant.delayDay / 1000 * (RequestDayHistory.MAX - 1));
        for (int i = 0; i < RequestDayHistory.MAX; i++) {
            day.getTemperature().add(0.0);
            day.getHumidity().add(0.0);
            day.getDate().add("");
            requestDay.dayHistory(day, dayCalendar, i);
            dayCalendar.set(Calendar.SECOND, dayCalendar.get(Calendar.SECOND) + BeanConstant.delayDay / 1000);
        }

        Toast.makeText(MainActivity.context, "正在获取数据中,请耐心等待...",
                Toast.LENGTH_LONG).show();
    }

    private void dayViewInit() {
        dayViewPager = (ViewPager) MainActivity.activity.findViewById(R.id.dayView);
        dayViewList = new ArrayList<>();
        View view1 = View.inflate(MainActivity.context, R.layout.temperatureday, null);
        LineChartView temperatureDayView = (LineChartView) view1.findViewById(R.id.temperatureday);
        temperatureDayView.setInteractive(false);
        temperatureDayView.setZoomType(ZoomType.HORIZONTAL);
        temperatureDayView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        temperatureDayView.setVisibility(View.VISIBLE);
        View view2 = View.inflate(MainActivity.context, R.layout.humidityday, null);
        LineChartView humidityDayView = (LineChartView) view2.findViewById(R.id.humidityday);
        humidityDayView.setInteractive(false);
        humidityDayView.setZoomType(ZoomType.HORIZONTAL);
        humidityDayView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        humidityDayView.setVisibility(View.VISIBLE);
        dayViewList.add(view1);
        dayViewList.add(view2);
        dayBeanLineView = new BeanLineView(temperatureDayView, humidityDayView);

        requestDayHandler = new RequestDayHandler();

        initDayDots();
        dayViewPager.setAdapter(dayPagerAdapter);
        dayViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                setDayDots(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        dayProgressBar = (ProgressBar)MainActivity.activity.findViewById(R.id.dayProgressBar);
    }

    /**
     * 初始化底部的点
     */
    private void initDayDots() {
        LinearLayout dayPointLayout = (LinearLayout) MainActivity.activity.findViewById(R.id.point_layout1);
        dayDots = new TextView[dayViewList.size()];
        for (int i = 0; i < dayViewList.size(); i++) {
            dayDots[i] = (TextView) dayPointLayout.getChildAt(i);
            setTextDrawable(dayDots[i], R.drawable.dian, i);
        }
        dayCurrentIndex = 0;
        setTextDrawable(dayDots[dayCurrentIndex], R.drawable.dian_down, dayCurrentIndex);
    }


    /**
     * 当滚动的时候更换点的背景图
     */
    private void setDayDots(int position) {
        if (position < 0 || position > dayViewList.size() - 1
                || dayCurrentIndex == position) {
            return;
        }
        setTextDrawable(dayDots[position], R.drawable.dian_down, position);
        setTextDrawable(dayDots[dayCurrentIndex], R.drawable.dian, dayCurrentIndex);
        dayCurrentIndex = position;
    }

    private void setTextDrawable(TextView tv, int id, int index) {
        Bitmap b = BitmapFactory.decodeResource(MainActivity.activity.getResources(), id);
        ImageSpan imgSpan = new ImageSpan(MainActivity.context, b);
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spanString);
        if (index == 0) {
            tv.append("温度");
        } else {
            tv.append("湿度");
        }
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

    public static Timer getRequestDayTimer() {
        return requestDayTimer;
    }

    public static TimerTask getRequestDayTask() {
        return requestDayTask;
    }

    static class RequestDayHandler extends Handler {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Calendar calendar = Calendar.getInstance();
                requestDay.dayStep(day,calendar);
            }
            super.handleMessage(msg);
        }
    }
}
