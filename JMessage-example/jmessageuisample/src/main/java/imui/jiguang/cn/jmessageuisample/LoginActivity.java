package imui.jiguang.cn.jmessageuisample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by caiyaoguan on 17/4/26.
 */

public class LoginActivity extends Activity implements View.OnClickListener, LoginView.OnSizeChangedListener {

    private LoginView mLoginView;
    private String TARGET_ID = "0002";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginView = (LoginView) findViewById(R.id.login_view);
        mLoginView.initModule();
        mLoginView.setListeners(this);
        mLoginView.setOnSizeChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                //隐藏软键盘
                InputMethodManager manager = ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE));
                if (getWindow().getAttributes().softInputMode
                        != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getCurrentFocus() != null) {
                        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }

                final String userId = mLoginView.getUserId();
                final String password = mLoginView.getPassword();

                if (userId.equals("")) {
                    mLoginView.userNameError(this);
                    break;
                } else if (password.equals("")) {
                    mLoginView.passwordError(this);
                    break;
                }
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage(this.getString(R.string.dialog_logging_in));
                dialog.show();
                JMessageClient.login(userId, password, new BasicCallback() {
                    @Override
                    public void gotResult(final int status, final String desc) {
                        dialog.dismiss();
                        if (status == 0) {
                            String username = JMessageClient.getMyInfo().getUserName();
                            SharePreferenceManager.setCachedUsername(username);
                            String appKey = JMessageClient.getMyInfo().getAppKey();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("targetAppKey", appKey);
                            intent.putExtra("targetId", TARGET_ID);
                            LoginActivity.this.startActivity(intent);
                        } else {
                            Log.e("LoginActivity", "status = " + status);
                        }
                    }
                });
                break;
            case R.id.register_btn:
                break;
        }
    }

    @Override
    public void onSoftKeyboardShown(int w, int h, int oldw, int oldh) {
        if (oldh - h > 300) {
            int height = oldh - h;
            if (SharePreferenceManager.getCachedKeyboardHeight() != height) {
                SharePreferenceManager.setCachedKeyboardHeight(height);
            }
            Log.i("LoginActivity", "SoftKeyboard height: " + height);
        }
    }
}
