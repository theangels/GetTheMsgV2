package ubibots.weatherbase.util;

import android.app.Application;

public class ContextUtil extends Application {

    private static ContextUtil instance;

    public static ContextUtil getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
