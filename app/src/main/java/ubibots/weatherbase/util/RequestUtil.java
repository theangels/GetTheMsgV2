package ubibots.weatherbase.util;

import android.graphics.Color;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import ubibots.weatherbase.MainActivity;
import ubibots.weatherbase.model.Border;
import ubibots.weatherbase.model.ColumnView;
import ubibots.weatherbase.model.TabMessage;

public class RequestUtil {
    public static String addParameter(String path, Map<String, String> params) {
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

    public static String UTCDateFormat(Calendar calendar) {
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

    public static void connectFailed() {
        Toast.makeText(MainActivity.context, "连接失败，请检查网络环境并重启本程序...",
                Toast.LENGTH_SHORT).show();
    }

    public static void reflashColumView(ColumnView columnView, TabMessage tab, String xName) {
        List<Column> temperatureHourColumnList = new ArrayList<>();
        List<SubcolumnValue> temperatureHourValuesList = new ArrayList<>();
        Column temperatureHourColumn = new Column(temperatureHourValuesList);
        List<AxisValue> temperatureHourAxisValue = new ArrayList<>();
        float m = 0;
        for (int i = 0; i < tab.getTemperature().size(); i++) {
            float tmp = tab.getTemperature().get(i).floatValue();
            m = Math.max(m, tmp);
            if ((tmp < Border.DOWNTEMP)) {//重写低线
                temperatureHourValuesList.add(new SubcolumnValue(tmp, Color.BLUE));
            } else if (tmp <= Border.UPTEMP) {//重写中线
                temperatureHourValuesList.add(new SubcolumnValue(tmp, Color.GREEN));
            } else {//重写高线
                temperatureHourValuesList.add(new SubcolumnValue(tmp, Color.RED));
            }
            temperatureHourColumnList.add(temperatureHourColumn);
            try {
                temperatureHourAxisValue.add(new AxisValue(i).setLabel(tab.getDate().get(i).substring(11, 19)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            temperatureHourValuesList = new ArrayList<>();
            temperatureHourColumn = new Column(temperatureHourValuesList);
        }

        m = m / Border.gold;
        SubcolumnValue ruler = new SubcolumnValue(m, Color.BLACK);
        temperatureHourValuesList.add(ruler);
        temperatureHourColumnList.add(temperatureHourColumn);
        temperatureHourAxisValue.add(new AxisValue(tab.getTemperature().size()).setLabel("Before"));
        ColumnChartData temperatureHourData = new ColumnChartData(temperatureHourColumnList);

        //坐标轴
        Axis axisX = new Axis();//X轴
        axisX.setHasLines(true);
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);
        axisX.setName(xName);
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
        columnView.getTemperatureView().setColumnChartData(temperatureHourData);

        List<Column> humidityHourColumnList = new ArrayList<>();
        List<SubcolumnValue> humidityHourValuesList = new ArrayList<>();
        Column humidityHourColumn = new Column(humidityHourValuesList);
        List<AxisValue> humidityHourAxisValue = new ArrayList<>();
        m = 0;
        for (int i = 0; i < tab.getHumidity().size(); i++) {
            float tmp = (tab.getHumidity().get(i).floatValue());
            m = Math.max(m, tmp);
            if ((tmp < Border.DOWNHUMI)) {//重写高线
                humidityHourValuesList.add(new SubcolumnValue(tmp, Color.BLUE));
            } else if ((tmp >= Border.DOWNHUMI && tmp <= Border.UPHUMI)) {//重写中线
                humidityHourValuesList.add(new SubcolumnValue(tmp, Color.GREEN));
            } else if (tmp > Border.UPHUMI) {//重写低线
                humidityHourValuesList.add(new SubcolumnValue(tmp, Color.RED));
            }
            humidityHourColumnList.add(humidityHourColumn);
            try {
                humidityHourAxisValue.add(new AxisValue(i).setLabel(tab.getDate().get(i).substring(11, 19)));
            } catch (Exception ex) {
                System.out.println(tab.getDate().get(i));
            }
            humidityHourValuesList = new ArrayList<>();
            humidityHourColumn = new Column(humidityHourValuesList);
        }
        m = m / Border.gold;
        ruler = new SubcolumnValue(m, Color.BLACK);
        humidityHourValuesList.add(ruler);
        humidityHourColumnList.add(humidityHourColumn);
        humidityHourAxisValue.add(new AxisValue(tab.getHumidity().size()).setLabel("Before"));
        ColumnChartData humidityHourData = new ColumnChartData(humidityHourColumnList);

        //坐标轴
        axisX = new Axis();//X轴
        axisX.setHasLines(true);
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);
        axisX.setName(xName);
        axisX.setMaxLabelChars(6);
        axisX.setValues(humidityHourAxisValue);
        humidityHourData.setAxisXBottom(axisX);

        axisY1 = new Axis();//Y1轴
        axisY1.setHasLines(true);
        axisY1.setTextColor(Color.WHITE);
        axisY1.setName("湿度/%RH");
        axisY1.setMaxLabelChars(4);
        humidityHourData.setAxisYLeft(axisY1);

        axisY2 = new Axis();//Y2轴
        axisY2.setHasLines(true);
        axisY2.setTextColor(Color.WHITE);
        axisY2.setName("湿度/%RH");
        axisY2.setMaxLabelChars(4);
        humidityHourData.setAxisYRight(axisY2);
        columnView.getHumidityView().setColumnChartData(humidityHourData);
    }

    public static String combineUrl(Calendar calendar) {
        String ipAddress = "zucc.cloud.thingworx.com:80";
        String appKey = "deaf648e-e691-4e9e-88a9-1a80b21145c3";
        String things = "DHT21Thing";
        String service;
        String strUrl;
        String startDate;
        String endDate;
        Calendar tmp = (Calendar) calendar.clone();

        endDate = RequestUtil.UTCDateFormat(tmp);
        tmp.set(Calendar.YEAR, tmp.get(Calendar.YEAR) - 1);
        startDate = RequestUtil.UTCDateFormat(tmp);

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

        strUrl = RequestUtil.addParameter(strUrl, params);

        return strUrl;
    }
}
