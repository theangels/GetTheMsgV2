package ubibots.weatherbase.control;

import java.util.Calendar;

import ubibots.weatherbase.model.TabMessage;
import ubibots.weatherbase.util.RequestUtil;

public class RequestHour {

    public void hourHistory(TabMessage hour, Calendar calendar, int id) {
        String strUrl = RequestUtil.combineUrl((Calendar) calendar.clone());
        RequestHourHistory requestTemperature = new RequestHourHistory(hour, id, 0);
        requestTemperature.execute(strUrl);
    }

    public void hourStep(TabMessage hour, Calendar calendar) {
        String strUrl = RequestUtil.combineUrl((Calendar) calendar.clone());
        RequestHourStep requestTemperature = new RequestHourStep(hour, 0);
        requestTemperature.execute(strUrl);
    }
}
