/**
 * @Means 一次性获取历史数据
 */
package ubibots.weatherbase.control;

import android.os.AsyncTask;
import android.view.View;

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

public class RequestHourHistory extends AsyncTask<String, Integer, String> {
    public final static int MAX = 120;
    private BeanTabMessage hour;
    private int id;
    private String strURL;
    private int time;

    public RequestHourHistory(BeanTabMessage hour, int id, int time) {
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
                    reconnect(strURL, hour, id);
                    return;
                }
                int GT = Double.valueOf(date.substring(11, 13)).intValue() + 8;
                if (GT >= 24) {
                    GT -= 24;
                }
                String all = tmp.get(0);
                date = all.substring(0, 11);
                if (GT < 10) {
                    date += "0";
                }
                date += GT + all.substring(13, all.length());
                hour.getDate().set(id, date);
                hour.getTemperature().set(id, temp);
                hour.getHumidity().set(id, humi);
                hour.count++;
                if (hour.count == 120) {//历史数据收集完毕
                    RequestUtil.reflashLineView(HourView.getHourBeanLineView(), hour, "时:分:秒");//刷新界面
                    HourView.getRequestHourTimer().schedule(HourView.getRequestHourTask(), 0, BeanConstant.delay);
                    HourView.getHourProgressBar().setVisibility(View.GONE);
                }
                HourView.getHourProgressBar().setProgress(100 * hour.count / MAX);
                System.out.println("Time: " + hour.getDate().get(id) + " " + "Temperature: " + hour.getTemperature().get(id) + " " + "Humidity: " + hour.getHumidity().get(id) + " " + "Num: " + id + " " + "Count: " + hour.count + " " + "Time: " + time);
            } else {//丢包重发
                reconnect(strURL, hour, id);
            }
        } else {
            RequestUtil.connectFailed();
        }
    }

    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {
    }

    public void reconnect(String strURL, BeanTabMessage hour, int id) {
        RequestHourHistory another = new RequestHourHistory(hour, id, time + 1);
        System.out.println("time: " + time);
        System.out.println(strURL);
        another.execute(strURL);
    }
}