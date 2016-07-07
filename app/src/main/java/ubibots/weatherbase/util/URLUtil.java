package ubibots.weatherbase.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class URLUtil {
    public static String combineUrl(Calendar calendar) {
        String ipAddress = "zucc.cloud.thingworx.com:80";
        String appKey = "9653e971-e905-472e-acae-57bab94e8057";
        String things = "WeatherBase";
        String service;
        String strUrl;
        String startDate;
        String endDate;
        Calendar tmp = (Calendar) calendar.clone();

        endDate = DateUtil.UTCDateFormat(tmp);
        tmp.set(Calendar.YEAR, tmp.get(Calendar.YEAR) - 1);
        startDate = DateUtil.UTCDateFormat(tmp);

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

        strUrl = addParameter(strUrl, params);

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
}
