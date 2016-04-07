/**
 * @Means 一步一步创建小时数据
 */
package ubibots.weatherbase.control;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ubibots.weatherbase.model.BeanConstant;
import ubibots.weatherbase.model.BeanTabMessage;
import ubibots.weatherbase.ui.HourView;
import ubibots.weatherbase.util.RequestUtil;

public class RequestHourStep extends AsyncTask<String, Integer, String> {
    public final static int MAX = 120;
    private BeanTabMessage hour;
    private String strURL;
    private int time;

    public RequestHourStep(BeanTabMessage hour, int time) {
        this.hour = hour;
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
        if (result != null && time < BeanConstant.MAXTIME) {
            Pattern pattern = Pattern.compile("<TD>(.*?)</TD>");
            Matcher matcher = pattern.matcher(result);
            ArrayList<String> tmp = new ArrayList<>();
            while (matcher.find()) {
                tmp.add(matcher.group(1));
            }
            if (tmp.size() >= 3) {
                String date = tmp.get(0);
                double temp = Double.valueOf(tmp.get(1));
                double humi = Double.valueOf(tmp.get(2));

                if (date.length() != 24 || temp <= 0 || humi <= 0) {//丢包重发
                    reconnect(strURL, hour);
                    return;
                }
                int GT = Double.valueOf(date.substring(11,13)).intValue() + 8;
                if (GT >= 24) {
                    GT -= 24;
                }
                String all = tmp.get(0);
                date = all.substring(0, 11);
                if (GT < 10) {
                    date += "0";
                }
                date += GT + all.substring(13, all.length());
                hour.getDate().remove(0);
                hour.getDate().add(date);
                hour.getTemperature().remove(0);
                hour.getTemperature().add(temp);
                hour.getHumidity().remove(0);
                hour.getHumidity().add(humi);
                RequestUtil.reflashLineView(HourView.getHourBeanLineView(), hour, "时:分:秒");//刷新界面
                System.out.println("Time: " + hour.getDate().get(RequestHourStep.MAX - 1) + " " + "Temperature: " + hour.getTemperature().get(RequestHourStep.MAX - 1) + " " + "Humidity: " + hour.getHumidity().get(RequestHourStep.MAX - 1) + " " + "Time: " + time);
            } else {//丢包重发
                reconnect(strURL, hour);
            }
        } else {
            RequestUtil.connectFailed();
        }
    }

    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {
    }


    public void reconnect(String strURL, BeanTabMessage hour) {
        RequestHourStep another = new RequestHourStep(hour, time + 1);
        System.out.println("time: " + time);
        System.out.println(strURL);
        another.execute(strURL);
    }
}
