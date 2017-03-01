package ubibots.weatherbase.ui;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ubibots.weatherbase.R;
import ubibots.weatherbase.model.BeanFlag;
import ubibots.weatherbase.util.ContextUtil;

public class ListTab {

    public static ListTab thisClass;

    private static ListView listView;
    private int currentTab;
    private HourView hourView;
    private DayView dayView;

    public int getCurrentTab() {
        return currentTab;
    }

    public static ListView getListView() {
        return listView;
    }

    ListTab(Activity activity, HourView hourView, DayView dayView) {
        thisClass = this;
        this.hourView = hourView;
        this.dayView = dayView;

        listView = (ListView) activity.findViewById(R.id.listview);
        listView.setBackgroundColor(Color.GRAY);
        listView.setCacheColorHint(0);
        final List<String> data = new ArrayList<>();
        data.add("每时");
        data.add("每日");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ContextUtil.getInstance(), android.R.layout.simple_expandable_list_item_1, data);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String click = data.get(i);
                switch (click) {
                    case "每时":
                        hourVisible();
                        currentTab = 0;
                        break;
                    case "每日":
                        dayVisible();
                        currentTab = 1;
                        break;
                }
            }
        });
        hourVisible();
        currentTab = 0;
    }

    public void hourVisible() {
        if (hourView.getHourViewPager() != null) {
            if (BeanFlag.isFinishRoadHour) {
                hourView.getHourViewPager().setVisibility(View.VISIBLE);
            }
            dayInvisible();
        }
        if (hourView.getHourProgressBar().getVisibility() != View.GONE) {
            hourView.getHourProgressBar().setVisibility(View.VISIBLE);
        }
    }

    private void hourInvisible() {
        if (hourView.getHourViewPager() != null) {
            hourView.getHourViewPager().setVisibility(View.INVISIBLE);
            if (hourView.getHourProgressBar().getVisibility() != View.GONE) {
                hourView.getHourProgressBar().setVisibility(View.INVISIBLE);
            }
        }
    }

    private void dayVisible() {
        if (dayView.getDayViewPager() != null) {
            if (BeanFlag.isFinishRoadDay) {
                dayView.getDayViewPager().setVisibility(View.VISIBLE);
            }
            hourInvisible();
        }
        if (dayView.getDayProgressBar().getVisibility() != View.GONE) {
            dayView.getDayProgressBar().setVisibility(View.VISIBLE);
        }
    }

    private void dayInvisible() {
        if (dayView.getDayViewPager() != null) {
            dayView.getDayViewPager().setVisibility(View.INVISIBLE);
            if (dayView.getDayProgressBar().getVisibility() != View.GONE) {
                dayView.getDayProgressBar().setVisibility(View.INVISIBLE);
            }
        }
    }
}
