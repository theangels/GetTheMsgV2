package ubibots.weatherbase.ui;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ubibots.weatherbase.MainActivity;
import ubibots.weatherbase.R;

public class ListTab {

    public ListTab(){
        ListView listView = (ListView) MainActivity.activity.findViewById(R.id.listview);
        listView.setBackgroundColor(Color.GRAY);
        listView.setCacheColorHint(0);
        final List<String> data = new ArrayList<>();
        data.add("每时");
        data.add("每日");
        data.add("每周");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.context, android.R.layout.simple_expandable_list_item_1, data);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String click = data.get(position);
                switch (click) {
                    case "每时":
                        hourVisible();
                        break;
                    case "每日":
                        dayVisible();
                        break;
                    case "每周":
                        hourInvisible();
                        break;
                }
            }
        });
        hourVisible();
    }

    private void hourVisible(){
        HourView.getHourViewPager().setVisibility(View.VISIBLE);
        dayInvisible();
        if(HourView.getHourProgressBar().getVisibility()!=View.GONE) {
            HourView.getHourProgressBar().setVisibility(View.VISIBLE);
        }
    }

    private void hourInvisible(){
        HourView.getHourViewPager().setVisibility(View.INVISIBLE);
        if(HourView.getHourProgressBar().getVisibility()!=View.GONE) {
            HourView.getHourProgressBar().setVisibility(View.INVISIBLE);
        }
    }

    private void dayVisible(){
        DayView.getDayViewPager().setVisibility(View.VISIBLE);
        hourInvisible();
        if(DayView.getDayProgressBar().getVisibility()!=View.GONE) {
            DayView.getDayProgressBar().setVisibility(View.VISIBLE);
        }
    }

    private void dayInvisible(){
        DayView.getDayViewPager().setVisibility(View.INVISIBLE);
        if(DayView.getDayProgressBar().getVisibility()!=View.GONE) {
            DayView.getDayProgressBar().setVisibility(View.INVISIBLE);
        }
    }
}
