package ubibots.weatherbase.control;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ubibots.weatherbase.model.TabMessage;
import ubibots.weatherbase.util.RequestUtil;

public class RequestHourHistory {

    public void hourHistory(TabMessage hour, Calendar calendar, int id) {
        String ipAddress = "zucc.cloud.thingworx.com:80";
        String appKey = "deaf648e-e691-4e9e-88a9-1a80b21145c3";
        String things = "DHT21Thing";
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
        RequestHourHistory_Call requestTemperature = new RequestHourHistory_Call(hour, id, 0);
        requestTemperature.execute(strUrl);
    }
}
