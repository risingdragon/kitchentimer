package tokyo.webstudio.kitchentimer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Created by hasegawa on 2016/09/28.
 */

public class Common {
    private static final String CONFIG_NAME = "config";

    public static void log(Object msg) {
        Log.d("Kitchen", msg == null ? "null" : msg.toString());
    }

    public static void saveProperty(Context context, String name, Object value) {
        SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME, Activity.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(name, String.valueOf(value));
        editor.commit();
    }

    public static void removeProperty(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME, Activity.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.remove(name);
        editor.commit();
    }

    public static String getProperty(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME, Activity.MODE_PRIVATE);
        return sp.getString(name, null);
    }
}
