package ubibots.getthemsgv2;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHourService extends AsyncTask<String, Integer, String> {
    private ArrayList<Double> temperatureHour;
    private ArrayList<Double> humidityHour;
    private ArrayList<String> hourTime;
    private int id;
    private String strURL;

    RequestHourService(ArrayList<Double> temperatureHour, ArrayList<Double> humidityHour, ArrayList<String> hourTime, int id) {
        this.temperatureHour = temperatureHour;
        this.humidityHour = humidityHour;
        this.hourTime = hourTime;
        this.id = id;
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
            }
            if (tmp.size() >= 3) {
                String t = tmp.get(0);
                double temp = toDouble(tmp.get(1));
                double humi = toDouble(tmp.get(2));

                if (t.length() != 24 || temp <= 0 || humi <= 0) {
                    RequestHourService another = new RequestHourService(temperatureHour, humidityHour, hourTime, id);
                    System.out.println(strURL);
                    another.execute(strURL);
                    return;
                }
                hourTime.set(id, tmp.get(0));
                temperatureHour.set(id, temp);
                humidityHour.set(id, humi);
                System.out.println("Time: " + hourTime.get(id) + " " + "Temperature: " + temperatureHour.get(id) + " " + "Humidity: " + humidityHour.get(id) + " " + "Num: " + id);
            } else {
                RequestHourService another = new RequestHourService(temperatureHour, humidityHour, hourTime, id);
                System.out.println(strURL);
                another.execute(strURL);
            }
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
}