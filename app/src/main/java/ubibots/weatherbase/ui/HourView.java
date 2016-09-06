package ubibots.weatherbase.ui;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.view.LineChartView;
import ubibots.weatherbase.R;
import ubibots.weatherbase.DisplayHistoryActivity;
import ubibots.weatherbase.model.BeanLineView;
import ubibots.weatherbase.model.BeanTabMessage;

public class HourView {
    private static BeanLineView hourBeanLineView;
    private static BeanTabMessage hour;
    private static List<View> hourViewList;
    private static TextView[] hourDots;
    private static int hourCurrentIndex;
    private static ViewPager hourViewPager;
    private static ProgressBar hourProgressBar;

    public static BeanLineView getHourBeanLineView() {
        return hourBeanLineView;
    }

    public static ViewPager getHourViewPager() {
        return hourViewPager;
    }

    public static ProgressBar getHourProgressBar() {
        return hourProgressBar;
    }

    public static BeanTabMessage getHour() {
        return hour;
    }

    public static void setHour(BeanTabMessage hour) {
        HourView.hour = hour;
    }

    public HourView() {
        hourViewPager = (ViewPager) DisplayHistoryActivity.getActivity().findViewById(R.id.hourView);
        hourViewList = new ArrayList<>();
        View view1 = View.inflate(DisplayHistoryActivity.getContext(), R.layout.temperaturehour, null);
        LineChartView temperatureHourView = (LineChartView) view1.findViewById(R.id.temperaturehour);
        temperatureHourView.setInteractive(false);
        temperatureHourView.setZoomType(ZoomType.HORIZONTAL);
        temperatureHourView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        temperatureHourView.setVisibility(View.VISIBLE);
        View view2 = View.inflate(DisplayHistoryActivity.getContext(), R.layout.humidityhour, null);
        LineChartView humidityHourView = (LineChartView) view2.findViewById(R.id.humidityhour);
        humidityHourView.setInteractive(false);
        humidityHourView.setZoomType(ZoomType.HORIZONTAL);
        humidityHourView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        humidityHourView.setVisibility(View.VISIBLE);
        View view3 = View.inflate(DisplayHistoryActivity.getContext(), R.layout.airhour, null);
        LineChartView airHourView = (LineChartView) view3.findViewById(R.id.airhour);
        airHourView.setInteractive(false);
        airHourView.setZoomType(ZoomType.HORIZONTAL);
        airHourView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        airHourView.setVisibility(View.VISIBLE);
        hourViewList.add(view1);
        hourViewList.add(view2);
        hourViewList.add(view3);
        hourBeanLineView = new BeanLineView(temperatureHourView, humidityHourView, airHourView);

        initHourDots();
        PagerAdapter hourPagerAdapter = new PagerAdapter() {
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
        hourProgressBar = (ProgressBar)DisplayHistoryActivity.getActivity().findViewById(R.id.hourProgressBar);
    }

    /**
     * 初始化底部的点
     */
    private void initHourDots() {
        hourDots = new TextView[3];
        hourDots[0] = (TextView)DisplayHistoryActivity.getActivity().findViewById(R.id.currentTemperature);
        hourDots[1] = (TextView)DisplayHistoryActivity.getActivity().findViewById(R.id.currentHumidity);
        hourDots[2] = (TextView)DisplayHistoryActivity.getActivity().findViewById(R.id.currentPM);
        hourCurrentIndex = 0;
        hourDots[0].setTextColor(Color.RED);
    }

    /**
     * 当滚动的时候更换点的背景图
     */
    private void setHourDots(int position) {
        if (position < 0 || position > hourViewList.size() - 1
                || hourCurrentIndex == position) {
            return;
        }

        hourDots[hourCurrentIndex].setTextColor(Color.WHITE);
        hourDots[position].setTextColor(Color.RED);
        hourCurrentIndex = position;
    }
}
