package ubibots.weatherbase.control;

import java.util.Calendar;

import ubibots.weatherbase.model.BeanTabMessage;
import ubibots.weatherbase.util.RequestUtil;

public class RequestDay {

    public void dayHistory(BeanTabMessage day, Calendar calendar, int id) {
        String strUrl = RequestUtil.combineUrl((Calendar) calendar.clone());
        RequestDayHistory request = new RequestDayHistory(day, id, 0);
        request.execute(strUrl);
    }

    public void dayStep(BeanTabMessage day, Calendar calendar) {
        String strUrl = RequestUtil.combineUrl((Calendar) calendar.clone());
        RequestDayStep request = new RequestDayStep(day, 0);
        request.execute(strUrl);
    }
}
