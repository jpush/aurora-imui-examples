package imui.jiguang.cn.jmessageuisample;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by caiyaoguan on 17/4/28.
 */

public class SharePreferenceManager {

    static SharedPreferences sp;

    public static void init(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private static final String KEY_CACHED_USERNAME = "cached_username";

    public static void setCachedUsername(String username) {
        if (null != sp) {
            sp.edit().putString(KEY_CACHED_USERNAME, username).apply();
        }
    }

    public static String getCachedUsername() {
        if (null != sp) {
            return sp.getString(KEY_CACHED_USERNAME, null);
        }
        return null;
    }

    private static final String SOFT_KEYBOARD_HEIGHT = "SoftKeyboardHeight";

    public static void setCachedKeyboardHeight(int height){
        if(null != sp){
            sp.edit().putInt(SOFT_KEYBOARD_HEIGHT, height).apply();
        }
    }

    public static int getCachedKeyboardHeight(){
        if(null != sp){
            return sp.getInt(SOFT_KEYBOARD_HEIGHT, 500);
        }
        return 500;
    }
}
