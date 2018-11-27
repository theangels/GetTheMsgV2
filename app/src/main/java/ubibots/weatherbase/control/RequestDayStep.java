package ubibots.weatherbase.control;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ubibots.weatherbase.model.BeanConstant;
import ubibots.weatherbase.model.BeanTabMessage;
import ubibots.weatherbase.ui.DayView;
import ubibots.weatherbase.util.DateUtil;
import ubibots.weatherbase.util.RequestUtil;

class RequestDayStep extends AsyncTask<String, Integer, String> {

    private final static int MAX = 48;
    private BeanTabMessage day;
    private String strURL;
    private int time;
    private DayView dayView;

    RequestDayStep(DayView dayView, BeanTabMessage day, int time) {
        this.dayView = dayView;
        this.day = day;
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
        if (result != null) {
            Pattern pattern = Pattern.compile("<TD>(.*?)</TD>");
            Matcher matcher = pattern.matcher(result);
            ArrayList<String> tmp = new ArrayList<>();
            while (matcher.find()) {
                tmp.add(matcher.group(1));
                if (tmp.size() == 8) {
                    break;
                }
            }
            if (tmp.size() >= 8) {
                String tempString = tmp.get(0);
                double t = 0;
                if (!tempString.equals("---")) {
                    t = Double.valueOf(tempString);
                }
                String rainFallString = tmp.get(1);
                double r = 0;
                if (!rainFallString.equals("---")) {
                    r = Double.valueOf(rainFallString);
                }
                String humidityString = tmp.get(2);
                double h = 0;
                if (!humidityString.equals("---")) {
                    h = Double.valueOf(humidityString);
                }
                String windSpeedString = tmp.get(3);
                double s = 0;
                if (!windSpeedString.equals("---")) {
                    s = Double.valueOf(windSpeedString);
                }
                String airString = tmp.get(4);
                double a = 0;
                if (!airString.equals("---")) {
                    a = Double.valueOf(airString);
                }
                String windDirectionString = tmp.get(5);
                String d = "";
                if (!windDirectionString.equals("---")) {
                    d = windDirectionString;
                }
                String pressureString = tmp.get(6);
                double p = 0;
                if (!pressureString.equals("---")) {
                    p = Double.valueOf(pressureString);
                }
                String timeStampString = tmp.get(7);
                timeStampString = timeStampString.replace("&#x3a;", ":");
                timeStampString = timeStampString.replace("&#x2b;", "+");

                //丢包重发
                if (t < 0 || r < 0 || h < 0 || s < 0 || a < 0 || p < 0 || timeStampString.length() != 29) {
                    reconnect(strURL, day);
                    return;
                }

                timeStampString = timeStampString.substring(0, 10) + " " + timeStampString.substring(11, 23);
                Calendar calendar = DateUtil.dateToCalender(timeStampString, "yyyy-MM-dd HH:mm:ss.SSS");
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                timeStampString = sdf.format(calendar.getTime());

                day.getTemperature().remove(0);
                day.getTemperature().add(t);
                day.getRainFall().remove(0);
                day.getRainFall().add(r);
                day.getHumidity().remove(0);
                day.getHumidity().add(h);
                day.getWindSpeed().remove(0);
                day.getWindSpeed().add(s);
                day.getAir().remove(0);
                day.getAir().add(a);
                day.getWindDirection().remove(0);
                day.getWindDirection().add(d);
                day.getPressure().remove(0);
                day.getPressure().add(p);
                day.getTimeStamp().remove(0);
                day.getTimeStamp().add(timeStampString);

                //刷新界面
                dayView.flushView(dayView.getDayBeanLineView(), day);

                System.out.println("Time: " + day.getTimeStamp().get(MAX - 1) + " " + "Temperature: " + day.getTemperature().get(MAX - 1) + " " + "Humidity: " + day.getHumidity().get(MAX - 1) + " " + "Time: " + time);
            } else {//丢包重发
                Log.e("Tag", "数据错误Size: " + tmp.size());
                reconnect(strURL, day);
            }
        } else {
            Log.e("Tag", "没有数据");
            reconnect(strURL, day);
        }
    }

    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {
    }

    private void reconnect(String strURL, BeanTabMessage day) {
        int time = this.time + 1;
        if (time <= BeanConstant.MAXTIME) {
            RequestDayStep another = new RequestDayStep(dayView, day, time);
            System.out.println("time: " + time);
            System.out.println(strURL);
            another.execute(strURL);
        } else {
            RequestUtil.connectFailed();
        }
    }
}
