package ubibots.weatherbase.util;

import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import ubibots.weatherbase.model.BeanConstant;
import ubibots.weatherbase.model.BeanLineView;
import ubibots.weatherbase.model.BeanTabMessage;
import ubibots.weatherbase.ui.MonitorView;

public class RequestUtil {
    public static void flushView(BeanLineView lineView, BeanTabMessage tab, String xName) {
        //温度
        List<Line> temperatureLineList = new ArrayList<>();
        List<PointValue> temperatureValuesList;
        Line temperatureLine;
        List<AxisValue> temperatureAxisValue = new ArrayList<>();
        final int LOWLINE = 0;
        final int MIDLINE = 1;
        final int UPLINE = 2;
        int state = -1;
        temperatureValuesList = new ArrayList<>();
        temperatureLine = new Line(temperatureValuesList).setColor(Color.BLACK).setCubic(true);
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
                temperatureLine = new Line(temperatureValuesList).setColor(Color.BLUE).setCubic(true);
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
                temperatureLine = new Line(temperatureValuesList).setColor(Color.GREEN).setCubic(true);
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
                temperatureLine = new Line(temperatureValuesList).setColor(Color.RED).setCubic(true);
                temperatureLine.setHasPoints(false);
                if (tmp != -1) {
                    temperatureValuesList.add(new PointValue(i - 1, tmp));
                }
                temperatureValuesList.add(new PointValue(i, tmpTemperature));
                state = UPLINE;
            } else {
                temperatureValuesList.add(new PointValue(i, tmpTemperature));
            }
            temperatureAxisValue.add(new AxisValue(i).setLabel(tab.getTimeStamp().get(i)));
        }
        temperatureLineList.add(temperatureLine);

        //最后显示点
        temperatureValuesList = new ArrayList<>();
        float lastTemperature = tab.getTemperature().get(tab.getTemperature().size() - 1).floatValue();
        if (lastTemperature < BeanConstant.DOWNTEMP) {
            temperatureLine = new Line(temperatureValuesList).setColor(Color.BLUE).setCubic(true);
        } else if (lastTemperature <= BeanConstant.UPTEMP) {
            temperatureLine = new Line(temperatureValuesList).setColor(Color.GREEN).setCubic(true);
        } else {
            temperatureLine = new Line(temperatureValuesList).setColor(Color.RED).setCubic(true);
        }
        temperatureLine.setHasPoints(true);
        temperatureLine.setHasLines(false);
        temperatureLine.setHasLabels(true);
        temperatureValuesList.add(new PointValue(tab.getTemperature().size() - 1, lastTemperature));
        temperatureLineList.add(temperatureLine);

        //上下空白
        temperatureValuesList = new ArrayList<>();
        temperatureLine = new Line(temperatureValuesList).setColor(Color.BLACK).setCubic(true);
        temperatureLine.setHasPoints(false);
        temperatureLine.setHasLines(false);
        float mm = maxTemperature - minTemperature;
        temperatureValuesList.add(new PointValue(0, maxTemperature + mm + 1));
        temperatureValuesList.add(new PointValue(1, minTemperature - mm - 1));
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

        //湿度
        List<Line> humidityLineList = new ArrayList<>();
        List<PointValue> humidityValuesList;
        Line humidityLine;
        List<AxisValue> humidityAxisValue = new ArrayList<>();
        state = -1;
        humidityValuesList = new ArrayList<>();
        humidityLine = new Line(humidityValuesList).setColor(Color.BLACK).setCubic(true);
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
                humidityLine = new Line(humidityValuesList).setColor(Color.BLUE).setCubic(true);
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
                humidityLine = new Line(humidityValuesList).setColor(Color.GREEN).setCubic(true);
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
                humidityLine = new Line(humidityValuesList).setColor(Color.RED).setCubic(true);
                humidityLine.setHasPoints(false);
                if (tmp != -1) {
                    humidityValuesList.add(new PointValue(i - 1, tmp));
                }
                humidityValuesList.add(new PointValue(i, tmpHumidity));
                state = UPLINE;
            } else {
                humidityValuesList.add(new PointValue(i, tmpHumidity));
            }
            humidityAxisValue.add(new AxisValue(i).setLabel(tab.getTimeStamp().get(i)));
        }
        humidityLineList.add(humidityLine);

        //最后显示点
        humidityValuesList = new ArrayList<>();
        float lastHumidity = tab.getHumidity().get(tab.getHumidity().size() - 1).floatValue();
        if (lastHumidity < BeanConstant.DOWNHUMI) {
            humidityLine = new Line(humidityValuesList).setColor(Color.BLUE).setCubic(true);
        } else if (lastHumidity <= BeanConstant.UPHUMI) {
            humidityLine = new Line(humidityValuesList).setColor(Color.GREEN).setCubic(true);
        } else {
            humidityLine = new Line(humidityValuesList).setColor(Color.RED).setCubic(true);
        }
        humidityLine.setHasPoints(true);
        humidityLine.setHasLines(false);
        humidityLine.setHasLabels(true);
        humidityValuesList.add(new PointValue(tab.getHumidity().size() - 1, lastHumidity));
        humidityLineList.add(humidityLine);

        //上下空白
        humidityValuesList = new ArrayList<>();
        humidityLine = new Line(humidityValuesList).setColor(Color.BLACK).setCubic(true);
        humidityLine.setHasPoints(false);
        humidityLine.setHasLines(false);
        mm = maxHumidity - minHumidity;
        humidityValuesList.add(new PointValue(0, maxHumidity + mm + 1));
        humidityValuesList.add(new PointValue(1, minHumidity - mm - 1));
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
        axisY1.setName("湿度/%RH");
        axisY1.setMaxLabelChars(4);
        humidityData.setAxisYLeft(axisY1);

        axisY2 = new Axis();//Y2轴0
        axisY2.setHasLines(true);
        axisY2.setTextColor(Color.WHITE);
        axisY2.setName("湿度/%RH");
        axisY2.setMaxLabelChars(4);
        humidityData.setAxisYRight(axisY2);
        lineView.getHumidityView().setLineChartData(humidityData);

        //PM2.5
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
                airLine = new Line(airValuesList).setColor(Color.GREEN).setCubic(true);
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
                airLine = new Line(airValuesList).setColor(Color.BLUE).setCubic(true);
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
                airLine = new Line(airValuesList).setColor(Color.RED).setCubic(true);
                airLine.setHasPoints(false);
                if (tmp != -1) {
                    airValuesList.add(new PointValue(i - 1, tmp));
                }
                airValuesList.add(new PointValue(i, tmpAir));
                state = UPLINE;
            } else {
                airValuesList.add(new PointValue(i, tmpAir));
            }
            airAxisValue.add(new AxisValue(i).setLabel(tab.getTimeStamp().get(i)));
        }
        airLineList.add(airLine);

        //最后显示点
        airValuesList = new ArrayList<>();
        float lastAir = tab.getAir().get(tab.getAir().size() - 1).floatValue();
        if (lastAir < BeanConstant.DOWNAIR) {
            airLine = new Line(airValuesList).setColor(Color.GREEN).setCubic(true);
        } else if (lastAir <= BeanConstant.UPAIR) {
            airLine = new Line(airValuesList).setColor(Color.BLUE).setCubic(true);
        } else {
            airLine = new Line(airValuesList).setColor(Color.RED).setCubic(true);
        }
        airLine.setHasPoints(true);
        airLine.setHasLines(false);
        airLine.setHasLabels(true);
        airValuesList.add(new PointValue(tab.getAir().size() - 1, lastAir));
        airLineList.add(airLine);

        //上下空白
        airValuesList = new ArrayList<>();
        airLine = new Line(airValuesList).setColor(Color.BLACK).setCubic(true);
        airLine.setHasPoints(false);
        airLine.setHasLines(false);
        mm = maxAir - minAir;
        airValuesList.add(new PointValue(0, maxAir + mm + 1));
        airValuesList.add(new PointValue(1, minAir - mm - 1));
        airLineList.add(airLine);

        LineChartData airData = new LineChartData();
        airData.setLines(airLineList);

        //坐标轴
        axisX = new Axis();//X轴
        axisX.setHasLines(true);
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);
        axisX.setName(xName);
        axisX.setMaxLabelChars(6);
        axisX.setValues(airAxisValue);
        airData.setAxisXBottom(axisX);

        axisY1 = new Axis();//Y1轴
        axisY1.setHasLines(true);
        axisY1.setTextColor(Color.WHITE);
        axisY1.setName("PM2.5/ug/m3");
        axisY1.setMaxLabelChars(4);
        airData.setAxisYLeft(axisY1);

        axisY2 = new Axis();//Y2轴0
        axisY2.setHasLines(true);
        axisY2.setTextColor(Color.WHITE);
        axisY2.setName("PM2.5/ug/m3");
        axisY2.setMaxLabelChars(4);
        airData.setAxisYRight(axisY2);
        lineView.getAirView().setLineChartData(airData);
    }

    public static void connectFailed() {
        Toast.makeText(ContextUtil.getInstance(), "获取数据失败，可能该时间段不存在数据或者网络连接中断!",
                Toast.LENGTH_SHORT).show();
    }
}