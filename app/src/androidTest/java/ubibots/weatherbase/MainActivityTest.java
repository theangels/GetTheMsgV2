package ubibots.weatherbase;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.ListView;

import junit.framework.Assert;

import ubibots.weatherbase.ui.ListTab;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    private void setUpTest(){
        MainActivity activity = startActivity(new Intent(), null, null);
        getInstrumentation().callActivityOnStart(activity);
        getInstrumentation().callActivityOnResume(activity);
    }

    public void testListTab(){
        setUpTest();

        //测试Tab切换
        ListTab listTab = new ListTab();
        ListView listView = listTab.getListView();

        Assert.assertEquals(listTab.getCurrentTab()==0,true);

        listView.performItemClick(listView.getChildAt(1), 1, listView.getItemIdAtPosition(1));
        Assert.assertEquals(listTab.getCurrentTab()==1,true);

        listView.performItemClick(listView.getChildAt(2), 2, listView.getItemIdAtPosition(2));
        Assert.assertEquals(listTab.getCurrentTab()==2,true);

        listView.performItemClick(listView.getChildAt(2), 2, listView.getItemIdAtPosition(2));
        Assert.assertEquals(listTab.getCurrentTab()==2,true);
    }
}