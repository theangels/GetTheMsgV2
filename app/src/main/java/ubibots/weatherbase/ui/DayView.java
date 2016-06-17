package ubibots.weatherbase.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.view.LineChartView;
import ubibots.weatherbase.R;
import ubibots.weatherbase.DisplayHistoryActivity;
import ubibots.weatherbase.model.BeanLineView;
import ubibots.weatherbase.model.BeanTabMessage;

public class DayView {

    private static BeanLineView dayBeanLineView;
    private static BeanTabMessage day;
    private static List<View> dayViewList;
    private static TextView[] dayDots;
    private static int dayCurrentIndex;
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

    public static BeanTabMessage getDay() {
        return day;
    }

    public static void setDay(BeanTabMessage day) {
        DayView.day = day;
    }

    public DayView() {
        dayViewPager = (ViewPager) DisplayHistoryActivity.getActivity().findViewById(R.id.dayView);
        dayViewList = new ArrayList<>();
        View view1 = View.inflate(DisplayHistoryActivity.getContext(), R.layout.temperatureday, null);
        LineChartView temperatureDayView = (LineChartView) view1.findViewById(R.id.temperatureday);
        temperatureDayView.setInteractive(false);
        temperatureDayView.setZoomType(ZoomType.HORIZONTAL);
        temperatureDayView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        temperatureDayView.setVisibility(View.VISIBLE);
        View view2 = View.inflate(DisplayHistoryActivity.getContext(), R.layout.humidityday, null);
        LineChartView humidityDayView = (LineChartView) view2.findViewById(R.id.humidityday);
        humidityDayView.setInteractive(false);
        humidityDayView.setZoomType(ZoomType.HORIZONTAL);
        humidityDayView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        humidityDayView.setVisibility(View.VISIBLE);
        View view3 = View.inflate(DisplayHistoryActivity.getContext(), R.layout.airday, null);
        LineChartView airDayView = (LineChartView) view3.findViewById(R.id.airday);
        airDayView.setInteractive(false);
        airDayView.setZoomType(ZoomType.HORIZONTAL);
        airDayView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        airDayView.setVisibility(View.VISIBLE);
        dayViewList.add(view1);
        dayViewList.add(view2);
        dayViewList.add(view3);
        dayBeanLineView = new BeanLineView(temperatureDayView, humidityDayView, airDayView);

        initDayDots();
        PagerAdapter dayPagerAdapter = new PagerAdapter() {
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

        dayProgressBar = (ProgressBar)DisplayHistoryActivity.getActivity().findViewById(R.id.dayProgressBar);
    }

    /**
     * 初始化底部的点
     */
    private void initDayDots() {
        LinearLayout dayPointLayout = (LinearLayout) DisplayHistoryActivity.getActivity().findViewById(R.id.point);
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
        Bitmap b = BitmapFactory.decodeResource(DisplayHistoryActivity.getActivity().getResources(), id);
        ImageSpan imgSpan = new ImageSpan(DisplayHistoryActivity.getContext(), b);
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spanString);
        if (index == 0) {
            tv.append("温度");
        } else if(index == 1){
            tv.append("湿度");
        }else if(index == 2){
            tv.append("PM2.5");
        }
    }
}
