package ubibots.weatherbase.util;

import android.graphics.Color;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import ubibots.weatherbase.MainActivity;
import ubibots.weatherbase.model.BeanConstant;
import ubibots.weatherbase.model.BeanLineView;
import ubibots.weatherbase.model.BeanTabMessage;

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

    public static void reflashLineView(BeanLineView lineView, BeanTabMessage tab, String xName) {
        List<Line> temperatureLineList = new ArrayList<>();
        List<PointValue> temperatureValuesList;
        Line temperatureLine;
        List<AxisValue> temperatureAxisValue = new ArrayList<>();
        final int LOWLINE = 0;
        final int MIDLINE = 1;
        final int UPLINE = 2;
        int state = -1;
        temperatureValuesList = new ArrayList<>();
        temperatureLine = new Line(temperatureValuesList).setColor(Color.BLACK).setCubic(false);
        temperatureLine.setHasPoints(false);
        temperatureLine.setHasLines(false);
        temperatureLine = null;
        float maxTemperature = -2333;
        float minTemperature = 2333;
        for (int i = 0; i < tab.getTemperature().size(); i++) {
            float tmpTemperature = tab.getTemperature().get(i).floatValue();
            maxTemperature = Math.max(tmpTemperature, maxTemperature);
            minTemperature = Math.min(tmpTemperature, minTemperature);
            if (tmpTemperature < BeanConstant.DOWNTEMP && state != LOWLINE) {
                float tmp = -1;
                if (temperatureLine != null) {
                    temperatureLineList.add(temperatureLine);
                    tmp = tab.getTemperature().get(i - 1).floatValue();
                }
                temperatureValuesList = new ArrayList<>();
                temperatureLine = new Line(temperatureValuesList).setColor(Color.BLUE).setCubic(false);
                temperatureLine.setHasPoints(false);
                if (tmp != -1) {
                    temperatureValuesList.add(new PointValue(i - 1, tmp));
                }
                temperatureValuesList.add(new PointValue(i, tmpTemperature));
                state = LOWLINE;
            } else if (tmpTemperature >= BeanConstant.DOWNTEMP && tmpTemperature <= BeanConstant.UPTEMP && state != MIDLINE) {
                float tmp = -1;
                if (temperatureLine != null) {
                    temperatureLineList.add(temperatureLine);
                    tmp = tab.getTemperature().get(i - 1).floatValue();
                }
                temperatureValuesList = new ArrayList<>();
                temperatureLine = new Line(temperatureValuesList).setColor(Color.GREEN).setCubic(false);
                temperatureLine.setHasPoints(false);
                if (tmp != -1) {
                    temperatureValuesList.add(new PointValue(i - 1, tmp));
                }
                temperatureValuesList.add(new PointValue(i, tmpTemperature));
                state = MIDLINE;
            } else if (tmpTemperature > BeanConstant.UPTEMP && state != UPLINE) {
                float tmp = -1;
                if (temperatureLine != null) {
                    temperatureLineList.add(temperatureLine);
                    tmp = tab.getTemperature().get(i - 1).floatValue();
                }
                temperatureValuesList = new ArrayList<>();
                temperatureLine = new Line(temperatureValuesList).setColor(Color.RED).setCubic(false);
                temperatureLine.setHasPoints(false);
                if (tmp != -1) {
                    temperatureValuesList.add(new PointValue(i - 1, tmp));
                }
                temperatureValuesList.add(new PointValue(i, tmpTemperature));
                state = UPLINE;
            } else {
                temperatureValuesList.add(new PointValue(i, tmpTemperature));
            }
            temperatureAxisValue.add(new AxisValue(i).setLabel(tab.getDate().get(i)));
        }
        temperatureLineList.add(temperatureLine);

        temperatureValuesList = new ArrayList<>();
        temperatureLine = new Line(temperatureValuesList).setColor(Color.BLACK).setCubic(false);
        temperatureLine.setHasPoints(false);
        temperatureLine.setHasLines(false);
        float mm = maxTemperature - minTemperature;
        temperatureValuesList.add(new PointValue(0, maxTemperature + mm));
        temperatureValuesList.add(new PointValue(1, minTemperature - mm));
        temperatureLineList.add(temperatureLine);

        LineChartData temperatureData = new LineChartData();
        temperatureData.setLines(temperatureLineList);

        //坐标轴
        Axis axisX = new Axis();//X轴
        axisX.setHasLines(true);
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);
        axisX.setName(xName);
        axisX.setMaxLabelChars(6);
        axisX.setValues(temperatureAxisValue);
        temperatureData.setAxisXBottom(axisX);

        Axis axisY1 = new Axis();//Y1轴
        axisY1.setHasLines(true);
        axisY1.setTextColor(Color.WHITE);
        axisY1.setName("摄氏度/℃");
        axisY1.setMaxLabelChars(4);
        temperatureData.setAxisYLeft(axisY1);

        Axis axisY2 = new Axis();//Y2轴
        axisY2.setHasLines(true);
        axisY2.setTextColor(Color.WHITE);
        axisY2.setName("摄氏度/℃");
        axisY2.setMaxLabelChars(4);
        temperatureData.setAxisYRight(axisY2);
        lineView.getTemperatureView().setLineChartData(temperatureData);


        List<Line> humidityLineList = new ArrayList<>();
        List<PointValue> humidityValuesList;
        Line humidityLine;
        List<AxisValue> humidityAxisValue = new ArrayList<>();
        state = -1;
        humidityValuesList = new ArrayList<>();
        humidityLine = new Line(humidityValuesList).setColor(Color.BLACK).setCubic(false);
        humidityLine.setHasPoints(false);
        humidityLine.setHasLines(false);
        humidityLine = null;
        float maxHumidity = -2333;
        float minHumidity = 2333;
        for (int i = 0; i < tab.getHumidity().size(); i++) {
            float tmpHumidity = tab.getHumidity().get(i).floatValue();
            maxHumidity = Math.max(tmpHumidity, maxHumidity);
            minHumidity = Math.min(tmpHumidity, minHumidity);
            if (tmpHumidity < BeanConstant.DOWNHUMI && state != LOWLINE) {
                float tmp = -1;
                if (humidityLine != null) {
                    humidityLineList.add(humidityLine);
                    tmp = tab.getHumidity().get(i - 1).floatValue();
                }
                humidityValuesList = new ArrayList<>();
                humidityLine = new Line(humidityValuesList).setColor(Color.BLUE).setCubic(false);
                humidityLine.setHasPoints(false);
                if (tmp != -1) {
                    humidityValuesList.add(new PointValue(i - 1, tmp));
                }
                humidityValuesList.add(new PointValue(i, tmpHumidity));
                state = LOWLINE;
            } else if (tmpHumidity >= BeanConstant.DOWNHUMI && tmpHumidity <= BeanConstant.UPHUMI && state != MIDLINE) {
                float tmp = -1;
                if (humidityLine != null) {
                    humidityLineList.add(humidityLine);
                    tmp = tab.getHumidity().get(i - 1).floatValue();
                }
                humidityValuesList = new ArrayList<>();
                humidityLine = new Line(humidityValuesList).setColor(Color.GREEN).setCubic(false);
                humidityLine.setHasPoints(false);
                if (tmp != -1) {
                    humidityValuesList.add(new PointValue(i - 1, tmp));
                }
                humidityValuesList.add(new PointValue(i, tmpHumidity));
                state = MIDLINE;
            } else if (tmpHumidity > BeanConstant.UPHUMI && state != UPLINE) {
                float tmp = -1;
                if (humidityLine != null) {
                    humidityLineList.add(humidityLine);
                    tmp = tab.getHumidity().get(i - 1).floatValue();
                }
                humidityValuesList = new ArrayList<>();
                humidityLine = new Line(humidityValuesList).setColor(Color.RED).setCubic(false);
                humidityLine.setHasPoints(false);
                if (tmp != -1) {
                    humidityValuesList.add(new PointValue(i - 1, tmp));
                }
                humidityValuesList.add(new PointValue(i, tmpHumidity));
                state = UPLINE;
            } else {
                humidityValuesList.add(new PointValue(i, tmpHumidity));
            }
            humidityAxisValue.add(new AxisValue(i).setLabel(tab.getDate().get(i)));
        }
        humidityLineList.add(humidityLine);

        humidityValuesList = new ArrayList<>();
        humidityLine = new Line(humidityValuesList).setColor(Color.BLACK).setCubic(false);
        humidityLine.setHasPoints(false);
        humidityLine.setHasLines(false);
        mm = maxHumidity - minHumidity;
        humidityValuesList.add(new PointValue(0, maxHumidity + mm));
        humidityValuesList.add(new PointValue(1, minHumidity - mm));
        humidityLineList.add(humidityLine);

        LineChartData humidityData = new LineChartData();
        humidityData.setLines(humidityLineList);

        //坐标轴
        axisX = new Axis();//X轴
        axisX.setHasLines(true);
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);
        axisX.setName(xName);
        axisX.setMaxLabelChars(6);
        axisX.setValues(humidityAxisValue);
        humidityData.setAxisXBottom(axisX);

        axisY1 = new Axis();//Y1轴
        axisY1.setHasLines(true);
        axisY1.setTextColor(Color.WHITE);
        axisY1.setName("摄氏度/℃");
        axisY1.setMaxLabelChars(4);
        humidityData.setAxisYLeft(axisY1);

        axisY2 = new Axis();//Y2轴
        axisY2.setHasLines(true);
        axisY2.setTextColor(Color.WHITE);
        axisY2.setName("摄氏度/℃");
        axisY2.setMaxLabelChars(4);
        humidityData.setAxisYRight(axisY2);
        lineView.getHumidityView().setLineChartData(humidityData);
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

    public static Calendar dateToCalender(String string, String format){
        Calendar calendar = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(string));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return calendar;
    }
}
