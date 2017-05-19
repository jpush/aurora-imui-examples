package imui.jiguang.cn.jmessageuisample;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import cn.jpush.im.android.api.JMessageClient;


public class LoginView extends LinearLayout {

    private EditText mUserId;
    private EditText mPassword;
    private Button mLoginBtn;
    private Button mRegisterBtnOnLogin;
    private OnSizeChangedListener mListener;
    private CheckBox mTestEvnCB;
    private Context mContext;
    private static final boolean DEV_FLAG = false;

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }
    public void initModule() {
        mUserId = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mRegisterBtnOnLogin = (Button) findViewById(R.id.register_btn);
        mTestEvnCB = (CheckBox) findViewById(R.id.testEvn_cb);
        initTestEvnCB();
    }

    private void initTestEvnCB() {
        if (!DEV_FLAG) {
            mTestEvnCB.setVisibility(View.GONE);
        } else {
            Boolean isTestEvn = invokeIsTestEvn();
            mTestEvnCB.setChecked(isTestEvn);
        }
    }

    private Boolean invokeIsTestEvn() {
        try {
            Method method = JMessageClient.class.getDeclaredMethod("isTestEnvironment");
            Object result = method.invoke(null);
            return (Boolean)result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        mListener = listener;
    }

    public void setListeners(OnClickListener onClickListener) {
        mLoginBtn.setOnClickListener(onClickListener);
        mRegisterBtnOnLogin.setOnClickListener(onClickListener);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener){
        mTestEvnCB.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public String getUserId() {
        return mUserId.getText().toString().trim();
    }

    public String getPassword() {
        return mPassword.getText().toString().trim();
    }

    public void userNameError(Context context) {
        Toast.makeText(context, context.getString(R.string.toast_username_null), Toast.LENGTH_SHORT).show();
    }

    public void passwordError(Context context) {
        Toast.makeText(context, context.getString(R.string.toast_password_null), Toast.LENGTH_SHORT).show();
    }

//    public void isShowReturnBtn(boolean fromSwitch) {
//        if(fromSwitch){
//            mReturnBtn.setVisibility(VISIBLE);
//        }else mReturnBtn.setVisibility(INVISIBLE);
//    }

    public interface OnSizeChangedListener {
        void onSoftKeyboardShown(int w, int h, int oldw, int oldh);
    }

    public void setRegisterBtnVisible(int visibility) {
        mRegisterBtnOnLogin.setVisibility(visibility);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mListener != null) {
            mListener.onSoftKeyboardShown(w, h, oldw, oldh);
        }
    }
}
