package imui.jiguang.cn.jmessageuisample;

import android.app.Application;

import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by caiyaoguan on 17/4/25.
 */

public class MainApplication extends Application {

    private static String SAMPLE_CONFIGS = "sample_configs";
    public static String PICTURE_DIR = "sdcard/JChatDemo/pictures/";
    public static String FILE_DIR = "sdcard/JChatDemo/recvFiles/";

    @Override
    public void onCreate() {
        super.onCreate();
        JMessageClient.init(this);
        SharePreferenceManager.init(this, SAMPLE_CONFIGS);
        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);
        JMessageClient.setDebugMode(true);
    }
}
