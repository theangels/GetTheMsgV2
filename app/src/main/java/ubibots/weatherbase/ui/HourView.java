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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.view.ColumnChartView;
import ubibots.weatherbase.MainActivity;
import ubibots.weatherbase.R;
import ubibots.weatherbase.control.RequestHour;
import ubibots.weatherbase.control.RequestHourHistory;
import ubibots.weatherbase.model.Border;
import ubibots.weatherbase.model.ColumnView;
import ubibots.weatherbase.model.TabMessage;


public class HourView {
    private static ColumnView hourColumnView;
    private TabMessage hour;
    private RequestHour requestHour;
    private List<View> hourViewList;
    private TextView[] hourDots;
    private int hourCurrentIndex;
    private static RequestHourHandler requestHourHandler;

    public static ColumnView getHourColumnView() {
        return hourColumnView;
    }

    private PagerAdapter hourPagerAdapter = new PagerAdapter() {
        //官方建议这么写
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        //返回一共有多少个界面
        @Override
        public int getCount() {
            return hourViewList.size();
        }

        //实例化一个item
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(hourViewList.get(position));
            return hourViewList.get(position);
        }

        //销毁一个item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(hourViewList.get(position));
        }

    };

    public HourView() {
        hourViewInit();

        hour = new TabMessage(new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<String>());
        requestHour = new RequestHour();
        Calendar hourCalendar = Calendar.getInstance();
        hourCalendar.set(Calendar.SECOND, hourCalendar.get(Calendar.SECOND) - Border.delay / 1000 * (RequestHourHistory.MAX - 1));
        for (int i = 0; i < RequestHourHistory.MAX; i++) {
            hour.getTemperature().add(0.0);
            hour.getHumidity().add(0.0);
            hour.getDate().add("");
            requestHour.hourHistory(hour, hourCalendar, i);
            hourCalendar.set(Calendar.SECOND, hourCalendar.get(Calendar.SECOND) + Border.delay / 1000);
        }

        Toast.makeText(MainActivity.context, "正在获取数据中,请耐心等待...",
                Toast.LENGTH_LONG).show();
    }

    private void hourViewInit() {
        ViewPager hourViewPager = (ViewPager) MainActivity.activity.findViewById(R.id.viewpager1);
        hourViewList = new ArrayList<>();
        View view1 = View.inflate(MainActivity.context, R.layout.temperaturehour, null);
        ColumnChartView temperatureHourView = (ColumnChartView) view1.findViewById(R.id.temperaturehour);
        temperatureHourView.setInteractive(false);
        temperatureHourView.setZoomType(ZoomType.HORIZONTAL);
        temperatureHourView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        temperatureHourView.setVisibility(View.VISIBLE);
        View view2 = View.inflate(MainActivity.context, R.layout.humidityhour, null);
        ColumnChartView humidityHourView = (ColumnChartView) view2.findViewById(R.id.humidityhour);
        humidityHourView.setInteractive(false);
        humidityHourView.setZoomType(ZoomType.HORIZONTAL);
        humidityHourView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        humidityHourView.setVisibility(View.VISIBLE);
        hourViewList.add(view1);
        hourViewList.add(view2);
        hourColumnView = new ColumnView(temperatureHourView, humidityHourView);

        requestHourHandler = new RequestHourHandler(this);

        initHourDots();
        hourViewPager.setAdapter(hourPagerAdapter);
        hourViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                setHourDots(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    /**
     * 初始化底部的点
     */
    private void initHourDots() {
        LinearLayout hourPointLayout = (LinearLayout) MainActivity.activity.findViewById(R.id.point_layout1);
        hourDots = new TextView[hourViewList.size()];
        for (int i = 0; i < hourViewList.size(); i++) {
            hourDots[i] = (TextView) hourPointLayout.getChildAt(i);
            setTextDrawable(hourDots[i], R.drawable.dian, i);
        }
        hourCurrentIndex = 0;
        setTextDrawable(hourDots[hourCurrentIndex], R.drawable.dian_down, hourCurrentIndex);
    }


    /**
     * 当滚动的时候更换点的背景图
     */
    private void setHourDots(int position) {
        if (position < 0 || position > hourViewList.size() - 1
                || hourCurrentIndex == position) {
            return;
        }
        setTextDrawable(hourDots[position], R.drawable.dian_down, position);
        setTextDrawable(hourDots[hourCurrentIndex], R.drawable.dian, hourCurrentIndex);
        hourCurrentIndex = position;
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

    static class RequestHourHandler extends Handler{

        WeakReference<HourView> mHourView;

        RequestHourHandler(HourView  hourView) {
            mHourView = new WeakReference<>(hourView);
        }

        public void handleMessage(Message msg) {
            HourView theHourView = mHourView.get();
            if (msg.what == 1) {
                Calendar calendar = Calendar.getInstance();
                theHourView.requestHour.hourStep(theHourView.hour,calendar);
            }
            super.handleMessage(msg);
        }
    }
}
