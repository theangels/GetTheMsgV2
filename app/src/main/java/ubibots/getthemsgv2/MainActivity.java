package ubibots.getthemsgv2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class MainActivity extends Activity {

    private int delayTime = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        hourViewInit();
        initRequest();
        initReceive();
    }

    /**
     * Hour
     */
    private ArrayList<Double> temperatureHour;
    private ArrayList<Double> humidityHour;
    private ArrayList<String> hourTime;
    private static ColumnChartView temperatureHourView;
    private static ColumnChartView humidityHourView;
    private List<View> hourViewList;
    private TextView[] hourDots;
    private int hourCurrentIndex;

    private void hourViewInit() {
        ViewPager hourViewPager = (ViewPager) findViewById(R.id.viewpager1);
        hourViewList = new ArrayList<>();
        View view1 = View.inflate(getApplicationContext(), R.layout.temperaturehour, null);
        temperatureHourView = (ColumnChartView) view1.findViewById(R.id.temperaturehour);
        temperatureHourView.setInteractive(false);
        temperatureHourView.setZoomType(ZoomType.HORIZONTAL);
        temperatureHourView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        temperatureHourView.setVisibility(View.VISIBLE);
        View view2 = View.inflate(getApplicationContext(), R.layout.humidityhour, null);
        humidityHourView = (ColumnChartView) view2.findViewById(R.id.humidityhour);
        humidityHourView.setInteractive(false);
        humidityHourView.setZoomType(ZoomType.HORIZONTAL);
        humidityHourView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        humidityHourView.setVisibility(View.VISIBLE);
        hourViewList.add(view1);
        hourViewList.add(view2);

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

    /**
     * 初始化底部的点
     */
    private void initHourDots() {
        LinearLayout hourPointLayout = (LinearLayout) findViewById(R.id.point_layout1);
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

    /**
     * Request
     */
    private RequestHandler requestHandler;
    private int[] count;

    private void initRequest() {
        temperatureHour = new ArrayList<>();
        humidityHour = new ArrayList<>();
        hourTime = new ArrayList<>();
        count = new int[]{0,0,0,0,0};//Hour Day Week Month Year
        for (int i = 0; i < 120; i++) {
            temperatureHour.add(0.0);
            humidityHour.add(0.0);
            hourTime.add("");
        }
        requestHandler = new RequestHandler(this);
        requestTimer.schedule(requestTask, 0, delayTime);
    }

    private void CalRequest(Calendar calendar, int id) {
        String ipAddress = "zucc.cloud.thingworx.com:80";
        String appKey = "deaf648e-e691-4e9e-88a9-1a80b21145c3";
        String things = "DHT21Thing";
        String service;
        String strUrl;
        String startDate;
        String endDate;
        Calendar tmp = (Calendar) calendar.clone();

        endDate = UTCDateFormat(tmp);
        tmp.set(Calendar.MINUTE, tmp.get(Calendar.MINUTE) - 30);
        startDate = UTCDateFormat(tmp);

        service = "QueryPropertyHistory";
        strUrl = "http://"
                + ipAddress
                + "/Thingworx"
                + "/Things/" + things
                + "/Services/" + service + "?";

        Map<String, String> params;
        params = new HashMap<>();
        params.put("method", "post");
        params.put("appKey", appKey);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("maxItems", "1");
        params.put("oldestFirst", "false");

        strUrl = addParameter(strUrl, params);
        RequestHourService requestTemperature = new RequestHourService(temperatureHour, humidityHour, hourTime, id);
        requestTemperature.execute(strUrl);
    }

    Timer requestTimer = new Timer();
    TimerTask requestTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            requestHandler.sendMessage(message);
        }
    };

    static class RequestHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        RequestHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public synchronized void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 1:
                    Calendar calendar = Calendar.getInstance();
                    for (int i = 0; i < 120; i++) {
                        theActivity.CalRequest(calendar, i);
                        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - 30);
                    }
                    break;
            }
        }
    }

    /**
     * Receive
     */
    ReceiveHandler receiveHandler;

    private void initReceive() {
        receiveHandler = new ReceiveHandler(this);
        receiveTimer.schedule(receiveTask, delayTime, delayTime);
    }

    Timer receiveTimer = new Timer();
    TimerTask receiveTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            receiveHandler.sendMessage(message);
        }
    };

    static class ReceiveHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        ReceiveHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public synchronized void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            final float UPTEMP = 30;
            final float DOWNTEMP = 20;
            final float UPHUMI = 60;
            final float DOWNHUMI = 30;
            final float gold = (float) ((Math.sqrt(5) - 1) / 2);
            switch (msg.what) {
                case 1:
                    if (theActivity.temperatureHour.size() != 0) {//TemperatureHour
                        List<Column> temperatureHourColumnList = new ArrayList<>();
                        List<SubcolumnValue> temperatureHourValuesList = new ArrayList<>();
                        Column temperatureHourColumn = new Column(temperatureHourValuesList);
                        List<AxisValue> temperatureHourAxisValue = new ArrayList<>();
                        float m = 0;
                        for (int i = theActivity.temperatureHour.size() - 1, num = 0; i >= 0; i--, num++) {
                            float tmp = theActivity.temperatureHour.get(i).floatValue();
                            m = Math.max(m, tmp);
                            if ((tmp < DOWNTEMP)) {//重写低线
                                temperatureHourValuesList.add(new SubcolumnValue(tmp, Color.BLUE));
                            } else if (tmp <= UPTEMP) {//重写中线
                                temperatureHourValuesList.add(new SubcolumnValue(tmp, Color.GREEN));
                            } else {//重写高线
                                temperatureHourValuesList.add(new SubcolumnValue(tmp, Color.RED));
                            }
                            temperatureHourColumnList.add(temperatureHourColumn);
                            try {
                                temperatureHourAxisValue.add(new AxisValue(num).setLabel(theActivity.hourTime.get(num).substring(11, 19)));
                            }catch (Exception ex){
                                System.out.println(theActivity.hourTime.get(num));
                            }
                                temperatureHourValuesList = new ArrayList<>();
                            temperatureHourColumn = new Column(temperatureHourValuesList);
                        }

                        m = m / gold;
                        SubcolumnValue ruler = new SubcolumnValue(m, Color.BLACK);
                        temperatureHourValuesList.add(ruler);
                        temperatureHourColumnList.add(temperatureHourColumn);
                        temperatureHourAxisValue.add(new AxisValue(theActivity.temperatureHour.size()).setLabel("Before"));
                        ColumnChartData temperatureHourData = new ColumnChartData(temperatureHourColumnList);

                        //坐标轴
                        Axis axisX = new Axis();//X轴
                        axisX.setHasLines(true);
                        axisX.setHasTiltedLabels(true);
                        axisX.setTextColor(Color.WHITE);
                        axisX.setName("时间(时:分:秒)");
                        axisX.setMaxLabelChars(6);
                        axisX.setValues(temperatureHourAxisValue);
                        temperatureHourData.setAxisXBottom(axisX);

                        Axis axisY1 = new Axis();//Y1轴
                        axisY1.setHasLines(true);
                        axisY1.setTextColor(Color.WHITE);
                        axisY1.setName("摄氏度/℃");
                        axisY1.setMaxLabelChars(4);
                        temperatureHourData.setAxisYLeft(axisY1);

                        Axis axisY2 = new Axis();//Y2轴
                        axisY2.setHasLines(true);
                        axisY2.setTextColor(Color.WHITE);
                        axisY2.setName("摄氏度/℃");
                        axisY2.setMaxLabelChars(4);
                        temperatureHourData.setAxisYRight(axisY2);
                        temperatureHourView.setColumnChartData(temperatureHourData);
                    }

                    if (theActivity.humidityHour.size() != 0) {//HumidityHour
                        List<Column> humidityHourColumnList = new ArrayList<>();
                        List<SubcolumnValue> humidityHourValuesList = new ArrayList<>();
                        Column humidityHourColumn = new Column(humidityHourValuesList);
                        List<AxisValue> humidityHourAxisValue = new ArrayList<>();
                        float m = 0;
                        for (int i = theActivity.humidityHour.size() - 1, num = 0; i >= 0; i--, num++) {
                            float tmp = (theActivity.humidityHour.get(i).floatValue());
                            m = Math.max(m, tmp);
                            if ((tmp < DOWNHUMI)) {//重写高线
                                humidityHourValuesList.add(new SubcolumnValue(tmp, Color.BLUE));
                            } else if ((tmp >= DOWNHUMI && tmp <= UPHUMI)) {//重写中线
                                humidityHourValuesList.add(new SubcolumnValue(tmp, Color.GREEN));
                            } else if (tmp > UPHUMI) {//重写低线
                                humidityHourValuesList.add(new SubcolumnValue(tmp, Color.RED));
                            }
                            humidityHourColumnList.add(humidityHourColumn);
                            try{
                                humidityHourAxisValue.add(new AxisValue(num).setLabel(theActivity.hourTime.get(num).substring(11,19)));
                            }catch (Exception ex){
                                System.out.println(theActivity.hourTime.get(num));
                            }
                            humidityHourValuesList = new ArrayList<>();
                            humidityHourColumn = new Column(humidityHourValuesList);
                        }
                        m = m / gold;
                        SubcolumnValue ruler = new SubcolumnValue(m, Color.BLACK);
                        humidityHourValuesList.add(ruler);
                        humidityHourColumnList.add(humidityHourColumn);
                        humidityHourAxisValue.add(new AxisValue(theActivity.humidityHour.size()).setLabel("Before"));
                        ColumnChartData humidityHourData = new ColumnChartData(humidityHourColumnList);

                        //坐标轴
                        Axis axisX = new Axis();//X轴
                        axisX.setHasLines(true);
                        axisX.setHasTiltedLabels(true);
                        axisX.setTextColor(Color.WHITE);
                        axisX.setName("时间(时:分:秒)");
                        axisX.setMaxLabelChars(6);
                        axisX.setValues(humidityHourAxisValue);
                        humidityHourData.setAxisXBottom(axisX);

                        Axis axisY1 = new Axis();//Y1轴
                        axisY1.setHasLines(true);
                        axisY1.setTextColor(Color.WHITE);
                        axisY1.setName("湿度/%RH");
                        axisY1.setMaxLabelChars(4);
                        humidityHourData.setAxisYLeft(axisY1);

                        Axis axisY2 = new Axis();//Y2轴
                        axisY2.setHasLines(true);
                        axisY2.setTextColor(Color.WHITE);
                        axisY2.setName("湿度/%RH");
                        axisY2.setMaxLabelChars(4);
                        humidityHourData.setAxisYRight(axisY2);
                        humidityHourView.setColumnChartData(humidityHourData);
                    }
            }
        }
    }

    /**
     * Extras
     */
    private String addParameter(String path, Map<String, String> params) {
        String URL = path;
        if (params != null && URL.length() != 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    URL += entry.getKey() + "=" + entry.getValue();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                URL += "&";
            }
            URL = URL.substring(0, URL.length() - 1);
        }
        return URL;
    }

    private String UTCDateFormat(Calendar calendar) {
        String UTCDate;
        SimpleDateFormat sdf;
        Calendar tmp = (Calendar) calendar.clone();
        tmp.set(Calendar.HOUR_OF_DAY, tmp.get(Calendar.HOUR_OF_DAY) - 8);
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        UTCDate = sdf.format(tmp.getTime()) + "T";
        sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        UTCDate += sdf.format(tmp.getTime()) + ".";
        sdf = new SimpleDateFormat("SSS", Locale.getDefault());
        UTCDate += sdf.format(tmp.getTime()) + "Z";
        return UTCDate;
    }

    private void setTextDrawable(TextView tv, int id, int index) {
        Bitmap b = BitmapFactory.decodeResource(getResources(), id);
        ImageSpan imgSpan = new ImageSpan(this, b);
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spanString);
        if (index == 0) {
            tv.append("温度");
        } else {
            tv.append("湿度");
        }
    }
}
