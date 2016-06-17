package ubibots.weatherbase.ui;

import android.widget.ListView;

import ubibots.weatherbase.R;
import ubibots.weatherbase.DisplayHistoryActivity;

public class RecommandView {

    private static ListView recommandList;

    public static ListView getRecommandList() {
        return recommandList;
    }

    public RecommandView(){
        recommandList = (ListView) DisplayHistoryActivity.getActivity().findViewById(R.id.recommandList);
        recommandList.setCacheColorHint(0);
        recommandList.setClickable(false);
    }
}
