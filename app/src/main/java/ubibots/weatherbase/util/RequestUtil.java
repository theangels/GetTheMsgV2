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
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import ubibots.weatherbase.DisplayHistoryActivity;
import ubibots.weatherbase.model.BeanConstant;
import ubibots.weatherbase.model.BeanLineView;
import ubibots.weatherbase.model.BeanTabMessage;

public class RequestUtil {

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
        Toast.makeText(DisplayHistoryActivity.getContext(), "¡¨Ω” ß∞‹£¨«ÎºÏ≤ÈÕ¯¬Áª∑æ≥≤¢÷ÿ∆Ù±æ≥Ã–Ú...",
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
        temperatureValuesList.add(new PointValue(0, maxTemperature + mm + 1));
        temperatureValuesList.add(new PointValue(1, minTemperature - mm - 1));
        temperatureLineList.add(temperatureLine);

        LineChartData temperatureData = new LineChartData();
        temperatureData.setLines(temperatureLineList);

        //◊¯±Í÷·
        Axis axisX = new Axis();//X÷·
        axisX.setHasLines(true);
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);
        axisX.setName(xName);
        axisX.setMaxLabelChars(6);
        axisX.setValues(temperatureAxisValue);
        temperatureData.setAxisXBottom(axisX);

        Axis axisY1 = new Axis();//Y1÷·
        axisY1.setHasLines(true);
        axisY1.setTextColor(Color.WHITE);
        axisY1.setName("…„ œ∂»/°Ê");
        axisY1.setMaxLabelChars(4);
        temperatureData.setAxisYLeft(axisY1);

        Axis axisY2 = new Axis();//Y2÷·
        axisY2.setHasLines(true);
        axisY2.setTextColor(Color.WHITE);
        axisY2.setName("…„ œ∂»/°Ê");
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
        humidityValuesList.add(new PointValue(0, maxHumidity + mm + 1));
        humidityValuesList.add(new PointValue(1, minHumidity - mm - 1));
        humidityLineList.add(humidityLine);

        LineChartData humidityData = new LineChartData();
        humidityData.setLines(humidityLineList);

        //◊¯±Í÷·
        axisX = new Axis();//X÷·
        axisX.setHasLines(true);
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);
        axisX.setName(xName);
        axisX.setMaxLabelChars(6);
        axisX.setValues(humidityAxisValue);
        humidityData.setAxisXBottom(axisX);

        axisY1 = new Axis();//Y1÷·
        axisY1.setHasLines(true);
        axisY1.setTextColor(Color.WHITE);
        axisY1.setName(" ™∂»/%RH");
        axisY1.setMaxLabelChars(4);
        humidityData.setAxisYLeft(axisY1);

        axisY2 = new Axis();//Y2÷·0
        axisY2.setHasLines(true);
        axisY2.setTextColor(Color.WHITE);
        axisY2.setName(" ™∂»/%RH");
        axisY2.setMaxLabelChars(4);
        humidityData.setAxisYRight(axisY2);
        lineView.getHumidityView().setLineChartData(humidityData);

        List<Line> airLineList = new ArrayList<>();
        List<PointValue> airValuesList;
        Line airLine = null;
        List<AxisValue> airAxisValue = new ArrayList<>();
        state = -1;
        airValuesList = new ArrayList<>();
        float maxAir = -2333;
        float minAir = 2333;
        for (int i = 0; i < tab.getAir().size(); i++) {
            float tmpAir = tab.getAir().get(i).floatValue();
            maxAir = Math.max(tmpAir, maxAir);
            minAir = Math.min(tmpAir, minAir);
            if (tmpAir < BeanConstant.DOWNAIR && state != LOWLINE) {
                float tmp = -1;
                if (airLine != null) {
                    airLineList.add(airLine);
                    tmp = tab.getAir().get(i - 1).floatValue();
                }
                airValuesList = new ArrayList<>();
                airLine = new Line(airValuesList).setColor(Color.GREEN).setCubic(false);
                airLine.setHasPoints(false);
                if (tmp != -1) {
                    airValuesList.add(new PointValue(i - 1, tmp));
                }
                airValuesList.add(new PointValue(i, tmpAir));
                state = LOWLINE;
            } else if (tmpAir >= BeanConstant.DOWNAIR && tmpAir <= BeanConstant.UPAIR && state != MIDLINE) {
                float tmp = -1;
                if (airLine != null) {
                    airLineList.add(airLine);
                    tmp = tab.getAir().get(i - 1).floatValue();
                }
                airValuesList = new ArrayList<>();
                airLine = new Line(airValuesList).setColor(Color.BLUE).setCubic(false);
                airLine.setHasPoints(false);
                if (tmp != -1) {
                    airValuesList.add(new PointValue(i - 1, tmp));
                }
                airValuesList.add(new PointValue(i, tmpAir));
                state = MIDLINE;
            } else if (tmpAir > BeanConstant.UPAIR && state != UPLINE) {
                float tmp = -1;
                if (airLine != null) {
                    airLineList.add(airLine);
                    tmp = tab.getAir().get(i - 1).floatValue();
                }
                airValuesList = new ArrayList<>();
                airLine = new Line(airValuesList).setColor(Color.RED).setCubic(false);
                airLine.setHasPoints(false);
                if (tmp != -1) {
                    airValuesList.add(new PointValue(i - 1, tmp));
                }
                airValuesList.add(new PointValue(i, tmpAir));
                state = UPLINE;
            } else {
                airValuesList.add(new PointValue(i, tmpAir));
            }
            airAxisValue.add(new AxisValue(i).setLabel(tab.getDate().get(i)));
        }
        airLineList.add(airLine);

        airValuesList = new ArrayList<>();
        airLine = new Line(airValuesList).setColor(Color.BLACK).setCubic(false);
        airLine.setHasPoints(false);
        airLine.setHasLines(false);
        mm = maxAir - minAir;
        airValuesList.add(new PointValue(0, maxAir + mm + 1));
        airValuesList.add(new PointValue(1, minAir - mm - 1));
        airLineList.add(airLine);

        LineChartData airData = new LineChartData();
        airData.setLines(airLineList);

        //◊¯±Í÷·
        axisX = new Axis();//X÷·
        axisX.setHasLines(true);
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);
        axisX.setName(xName);
        axisX.setMaxLabelChars(6);
        axisX.setValues(airAxisValue);
        airData.setAxisXBottom(axisX);

        axisY1 = new Axis();//Y1÷·
        axisY1.setHasLines(true);
        axisY1.setTextColor(Color.WHITE);
        axisY1.setName("PM2.5/ug/m3");
        axisY1.setMaxLabelChars(4);
        airData.setAxisYLeft(axisY1);

        axisY2 = new Axis();//Y2÷·0
        axisY2.setHasLines(true);
        axisY2.setTextColor(Color.WHITE);
        axisY2.setName("PM2.5/ug/m3");
        axisY2.setMaxLabelChars(4);
        airData.setAxisYRight(axisY2);
        lineView.getAirView().setLineChartData(airData);
    }

    public static String combineUrl(Calendar calendar) {
        String ipAddress = "zucc.cloud.thingworx.com:80";
        String appKey = "9653e971-e905-472e-acae-57bab94e8057";
        String things = "WeatherBase";
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
