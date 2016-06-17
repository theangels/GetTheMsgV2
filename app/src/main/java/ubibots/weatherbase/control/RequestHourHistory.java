package ubibots.weatherbase.control;

import android.os.AsyncTask;
import android.view.View;

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

    //�÷�������������UI�̵߳��У���Ҫ�����첽�����������ڸ÷����в��ܶ�UI���еĿռ�������ú��޸�
    @Override
    protected String doInBackground(String... params) {
        //System.out.println("Url: " + params[0]);
        URL url;
        try {
            url = new URL(params[0]);
            strURL = params[0];
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true); //����������������������
            urlConn.setDoOutput(true); //������������������ϴ�
            urlConn.setUseCaches(false); //��ʹ�û���
            urlConn.setRequestMethod("POST"); //ʹ��get����
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

    //��doInBackground����ִ�н���֮�������У�����������UI�̵߳��� ���Զ�UI�ռ��������
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Pattern pattern = Pattern.compile("<TD>(.*?)</TD>");
            Matcher matcher = pattern.matcher(result);

            ArrayList<String> tmp = new ArrayList<>();
            while (matcher.find()) {
                tmp.add(matcher.group(1));
            }

            if (tmp.size() >= 4) {
                String dateString = tmp.get(0);
                double temp = 0;
                String tempString = tmp.get(1);
                if (!tempString.equals("---")) {
                    temp = Double.valueOf(tempString);
                }
                String humiString = tmp.get(2);
                double humi = 0;
                if(!humiString.equals("---")){
                    humi = Double.valueOf(humiString);
                }
                String airString = tmp.get(3);
                double air = 0;
                if(!airString.equals("---")){
                    air = Double.valueOf(airString);
                }

                //�����ط�
                if (dateString.length() != 24 || temp < 0 || humi < 0 || air < 0) {
                    reconnect(strURL, hour, id);
                    return;
                }

                dateString = dateString.substring(0, 10) + " " + dateString.substring(11, 23);
                Calendar calendar = RequestUtil.dateToCalender(dateString, "yyyy-MM-dd HH:mm:ss.SSS");
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 8);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                dateString = sdf.format(calendar.getTime());

                hour.getDate().set(id, dateString);
                hour.getTemperature().set(id, temp);
                hour.getHumidity().set(id, humi);
                hour.getAir().set(id, air);
                hour.count++;

                //��ʷ�����ռ����
                if (hour.count == MAX) {
                    //ˢ�½���
                    RequestUtil.reflashLineView(HourView.getHourBeanLineView(), hour, "ʱ:��:��");

                    RequestHour.getRequestHourTimer().schedule(RequestHour.getRequestHourTask(), BeanConstant.delayHour, BeanConstant.delayHour);
                    HourView.getHourProgressBar().setVisibility(View.GONE);

                    new RequestDay().executeRequest();
                }
                HourView.getHourProgressBar().setProgress(100 * hour.count / MAX);
                System.out.println("Time: " + hour.getDate().get(id) + " " + "Temperature: " + hour.getTemperature().get(id) + " " + "Humidity: " + hour.getHumidity().get(id) + " " + "Num: " + id + " " + "Count: " + hour.count + " " + "Time: " + time);
            } else {//�����ط�
                reconnect(strURL, hour, id);
            }
        } else {
            RequestUtil.connectFailed();
        }
    }

    //�÷���������UI�̵߳���,����������UI�̵߳��� ���Զ�UI�ռ��������
    @Override
    protected void onPreExecute() {
    }

    public void reconnect(String strURL, BeanTabMessage hour, int id) {
        int time = this.time + 1;
        if (time <= BeanConstant.MAXTIME) {
            RequestHourHistory another = new RequestHourHistory(hour, id, time + 1);
            System.out.println("time: " + time);
            System.out.println(strURL);
            another.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strURL);
        }
    }
}