package ubibots.weatherbase.control;


import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import ubibots.weatherbase.ui.DayView;
import ubibots.weatherbase.ui.HourView;
import ubibots.weatherbase.ui.ListTab;

public class ChangePage {

    private static HourView hourView;
    private static DayView dayView;

    private static ChangePageHandler changePageHandler;

    public ChangePage(HourView hourView, DayView dayView) {
        ChangePage.hourView = hourView;
        ChangePage.dayView = dayView;
        changePageHandler = new ChangePageHandler();
        changePageTimer.schedule(changePageTask, 10000, 10000);
    }

    private static Timer changePageTimer = new Timer();
    private static TimerTask changePageTask = new TimerTask() {
        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            changePageHandler.sendMessage(message);
        }
    };

    private static class ChangePageHandler extends Handler {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                executeChangePage();
            }
            super.handleMessage(msg);
        }
    }

    private static void executeChangePage() {
        int tabId = ListTab.thisClass.getCurrentTab();
        if (tabId == 0) {
            int id = hourView.getHourViewPager().getCurrentItem();
            id++;
            if (id < 3) {
                hourView.getHourViewPager().setCurrentItem(id);
            } else {
                hourView.getHourViewPager().setCurrentItem(0);
                ListTab.getListView().performItemClick(ListTab.getListView().getChildAt(1), 1, ListTab.getListView().getItemIdAtPosition(1));
            }
        } else {
            int id = dayView.getDayViewPager().getCurrentItem();
            id++;
            if (id < 3) {
                dayView.getDayViewPager().setCurrentItem(id);
            } else {
                dayView.getDayViewPager().setCurrentItem(0);
                ListTab.getListView().performItemClick(ListTab.getListView().getChildAt(0), 0, ListTab.getListView().getItemIdAtPosition(0));
            }
        }
    }
}
