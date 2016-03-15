package ubibots.weatherbase.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

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
}
