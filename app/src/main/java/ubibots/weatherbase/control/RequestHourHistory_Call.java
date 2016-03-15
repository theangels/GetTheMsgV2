/**
 * @Means 一次性获取历史数据
 */
package ubibots.weatherbase.control;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import ubibots.weatherbase.MainActivity;
import ubibots.weatherbase.model.TabMessage;
import ubibots.weatherbase.ui.HourView;

public class RequestHourHistory_Call extends AsyncTask<String, Integer, String> {
    public final static int MAX = 120;
    private final static int MAXTIME = 3;
    final float UPTEMP = 30;
    final float DOWNTEMP = 20;
    final float UPHUMI = 60;
    final float DOWNHUMI = 30;
    final float gold = (float) ((Math.sqrt(5) - 1) / 2);
    private TabMessage hour;
    private int id;
    private String strURL;
    private int time;

    public RequestHourHistory_Call(TabMessage hour, int id, int time) {
        this.hour = hour;
        this.id = id;
        this.time = time;
    }

    //该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
    @Override
    protected String doInBackground(String... params) {
        //System.out.println("Url: " + params[0]);
        URL url;
        try {
            url = new URL(params[0]);
            strURL = params[0];
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true); //允许输入流，即允许下载
            urlConn.setDoOutput(true); //允许输出流，即允许上传
            urlConn.setUseCaches(false); //不使用缓冲
            urlConn.setRequestMethod("POST"); //使用get请求
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            String result = "";
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                result += readLine;
            }
            in.close();
            urlConn.disconnect();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPostExecute(String result) {
        if (result != null && time < MAXTIME) {
            Pattern pattern = Pattern.compile("<TD>(.*?)</TD>");
            Matcher matcher = pattern.matcher(result);
            ArrayList<String> tmp = new ArrayList<>();
            while (matcher.find()) {
                tmp.add(matcher.group(1));
            }
            if (tmp.size() >= 3) {
                String t = tmp.get(0);
                double temp = toDouble(tmp.get(1));
                double humi = toDouble(tmp.get(2));

                if (t.length() != 24 || temp <= 0 || humi <= 0) {//丢包重发
                    reconnect();
                    return;
                }
                int GT = (int) toDouble(t.substring(11, 13)) + 8;
                if (GT >= 24) {
                    GT -= 24;
                }
                String all = tmp.get(0);
                t = all.substring(0, 11);
                if (GT < 10) {
                    t += "0";
                }
                t += GT + all.substring(13, all.length());
                hour.getDate().set(id, t);
                hour.getTemperature().set(id, temp);
                hour.getHumidity().set(id, humi);
                hour.count++;
                if (hour.count == 120) {//历史数据收集完毕
                    reflashView();//刷新界面
                    //准备启动步进请求
                }
                System.out.println("Time: " + hour.getDate().get(id) + " " + "Temperature: " + hour.getTemperature().get(id) + " " + "Humidity: " + hour.getHumidity().get(id) + " " + "Num: " + id + " " + "Count: " + hour.count);
            } else {//丢包重发
                reconnect();
            }
        } else {
            connectFailed();
        }
    }

    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {
    }

    private double toDouble(String init) {
        double ret = 0;
        if (init != null) {
            double mult = 1;
            for (int i = 0; i < init.length(); i++) {
                if (init.charAt(i) != '.') {
                    if (mult >= 0.5) {
                        ret = ret * mult + init.charAt(i) - '0';
                        mult *= 10;
                    } else {
                        ret = ret + (init.charAt(i) - '0') * mult;
                        mult *= 0.01;
                    }
                } else {
                    mult = 0.1;
                }
            }
            int ch = (int) (ret * 10);
            ret = (double) ch / 10;
        }
        return ret;
    }

    private void reflashView() {
        List<Column> temperatureHourColumnList = new ArrayList<>();
        List<SubcolumnValue> temperatureHourValuesList = new ArrayList<>();
        Column temperatureHourColumn = new Column(temperatureHourValuesList);
        List<AxisValue> temperatureHourAxisValue = new ArrayList<>();
        float m = 0;
        for (int i = 0; i < hour.getTemperature().size(); i++) {
            float tmp = hour.getTemperature().get(i).floatValue();
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
                temperatureHourAxisValue.add(new AxisValue(i).setLabel(hour.getDate().get(i).substring(11, 19)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            temperatureHourValuesList = new ArrayList<>();
            temperatureHourColumn = new Column(temperatureHourValuesList);
        }

        m = m / gold;
        SubcolumnValue ruler = new SubcolumnValue(m, Color.BLACK);
        temperatureHourValuesList.add(ruler);
        temperatureHourColumnList.add(temperatureHourColumn);
        temperatureHourAxisValue.add(new AxisValue(hour.getTemperature().size()).setLabel("Before"));
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
        HourView.getTemperatureHourView().setColumnChartData(temperatureHourData);

        List<Column> humidityHourColumnList = new ArrayList<>();
        List<SubcolumnValue> humidityHourValuesList = new ArrayList<>();
        Column humidityHourColumn = new Column(humidityHourValuesList);
        List<AxisValue> humidityHourAxisValue = new ArrayList<>();
        m = 0;
        for (int i = 0; i < hour.getHumidity().size(); i++) {
            float tmp = (hour.getHumidity().get(i).floatValue());
            m = Math.max(m, tmp);
            if ((tmp < DOWNHUMI)) {//重写高线
                humidityHourValuesList.add(new SubcolumnValue(tmp, Color.BLUE));
            } else if ((tmp >= DOWNHUMI && tmp <= UPHUMI)) {//重写中线
                humidityHourValuesList.add(new SubcolumnValue(tmp, Color.GREEN));
            } else if (tmp > UPHUMI) {//重写低线
                humidityHourValuesList.add(new SubcolumnValue(tmp, Color.RED));
            }
            humidityHourColumnList.add(humidityHourColumn);
            try {
                humidityHourAxisValue.add(new AxisValue(i).setLabel(hour.getDate().get(i).substring(11, 19)));
            } catch (Exception ex) {
                System.out.println(hour.getDate().get(i));
            }
            humidityHourValuesList = new ArrayList<>();
            humidityHourColumn = new Column(humidityHourValuesList);
        }
        m = m / gold;
        ruler = new SubcolumnValue(m, Color.BLACK);
        humidityHourValuesList.add(ruler);
        humidityHourColumnList.add(humidityHourColumn);
        humidityHourAxisValue.add(new AxisValue(hour.getHumidity().size()).setLabel("Before"));
        ColumnChartData humidityHourData = new ColumnChartData(humidityHourColumnList);

        //坐标轴
        axisX = new Axis();//X轴
        axisX.setHasLines(true);
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);
        axisX.setName("时间(时:分:秒)");
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
        HourView.getHumidityHourView().setColumnChartData(humidityHourData);
    }

    private void connectFailed() {
        Toast.makeText(MainActivity.context, "连接失败，请检查网络环境并重启本程序...",
                Toast.LENGTH_SHORT).show();
    }

    private void reconnect() {
        RequestHourHistory_Call another = new RequestHourHistory_Call(hour, id, time + 1);
        System.out.println("time: " + time);
        System.out.println(strURL);
        another.execute(strURL);
    }
}