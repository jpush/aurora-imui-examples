package imui.jiguang.cn.jmessageuisample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.jiguang.api.JCoreInterface;
import cn.jiguang.imui.chatinput.ChatInputView;
import cn.jiguang.imui.chatinput.listener.OnCameraCallbackListener;
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener;
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener;
import cn.jiguang.imui.chatinput.model.FileItem;
import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.messages.MsgListAdapter;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;


public class MainActivity extends Activity implements ChatView.OnKeyboardChangedListener,
        ChatView.OnSizeChangedListener, View.OnTouchListener {

    private final static String TAG = "MainActivity";

    private MsgListAdapter<JMUIMessage> mAdapter;
    private Context mContext;
    private List<JMUIMessage> mData = new ArrayList<>();
    private List<Message> mMsgList = new ArrayList<>();
    private ChatView mChatView;
    private final int REQUEST_RECORD_VOICE_PERMISSION = 0x0001;
    private final int REQUEST_CAMERA_PERMISSION = 0x0002;
    private final int REQUEST_PHOTO_PERMISSION = 0x0003;

    private InputMethodManager mImm;
    private Window mWindow;
    private Conversation mConv;
    private String mTargetId;
    private String mTargetAppKey;
    private boolean mIsSingle = true;
    private Long mGroupId;
    private UserInfo mMyInfo;
    private JMUserInfo mTargetInfo;
    public static final int PAGE_MESSAGE_COUNT = 18;
    private int mOffset = PAGE_MESSAGE_COUNT;
    private int mStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChatView = (ChatView) findViewById(R.id.chat_view);
        JMessageClient.registerEventReceiver(this);
        mContext = this;
        mMyInfo = JMessageClient.getMyInfo();
        try {
            mTargetId = getIntent().getStringExtra("targetId");
            mTargetAppKey = getIntent().getStringExtra("targetAppKey");
            mChatView.initModule();
            mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
            if (mConv == null) {
                Log.i(TAG, "create new conversation");
                mConv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
            }
            this.mMsgList = mConv.getMessagesFromNewest(0, mOffset);
            mStart = mOffset;
            if (mMsgList.size() > 0) {
                for (Message message : mMsgList) {
                    JMUIMessage jmuiMessage = new JMUIMessage(message);
                    mData.add(jmuiMessage);
                }
            }
            UserInfo userInfo = (UserInfo) mConv.getTargetInfo();
            if (userInfo == null) {
                JMessageClient.getUserInfo(mTargetId, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int status, String s, UserInfo userInfo) {
                        if (status == 0) {
                            mChatView.setTitle(userInfo.getUserName());
                        }
                    }
                });
            } else {
                mChatView.setTitle(userInfo.getUserName());
            }
            mTargetInfo = new JMUserInfo(userInfo);
            this.mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mWindow = getWindow();
            initMsgAdapter();
            mChatView.setKeyboardChangedListener(this);
            mChatView.setOnSizeChangedListener(this);
            mChatView.setOnTouchListener(this);
            mChatView.setMenuClickListener(new OnMenuClickListener() {

                @Override
                public boolean onSendTextMessage(CharSequence input) {
                    if (input.length() == 0) {
                        return false;
                    }
                    TextContent textContent = new TextContent(input.toString());
                    Message message = mConv.createSendMessage(textContent);
                    JMessageClient.sendMessage(message);
                    final JMUIMessage msg = new JMUIMessage(message);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addToStart(msg, true);
                        }
                    });
                    message.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int status, String desc) {
                            mAdapter.updateMessage(msg);
                            if (status == 0) {
                                Log.i(TAG, "send message succeed!");
                            } else {
                                Log.i(TAG, "send message failed " + desc);
                            }
                        }
                    });
                    return true;
                }

                @Override
                public void onSendFiles(List<FileItem> list) {
                    if (list == null || list.isEmpty()) {
                        return;
                    }

                    for (final FileItem item : list) {
                        if (item.getType() == FileItem.Type.Image) {
                            Bitmap bitmap = BitmapLoader.getBitmapFromFile(item.getFilePath(), 720, 1280);
                            ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                                @Override
                                public void gotResult(int status, String desc, ImageContent imageContent) {
                                    if (status == 0) {
                                        Message msg = mConv.createSendMessage(imageContent);
                                        JMessageClient.sendMessage(msg);
                                        final JMUIMessage jmuiMessage = new JMUIMessage(msg);
                                        jmuiMessage.setMediaFilePath(item.getFilePath());
                                        MainActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.addToStart(jmuiMessage, true);
                                            }
                                        });
                                        msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                                            @Override
                                            public void onProgressUpdate(double v) {
                                                jmuiMessage.setProgress(Math.ceil(v * 100) + "%");
                                                Log.w(TAG, "Uploading image progress" + Math.ceil(v * 100) + "%");
                                                mAdapter.updateMessage(jmuiMessage);
                                            }
                                        });
                                        msg.setOnSendCompleteCallback(new BasicCallback() {
                                            @Override
                                            public void gotResult(int status, String desc) {
                                                mAdapter.updateMessage(jmuiMessage);
                                                if (status == 0) {
                                                    Log.i(TAG, "Send image succeed");
                                                } else {
                                                    Log.i(TAG, "Send image failed, " + desc);
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        } else if (item.getType() == FileItem.Type.Video) {
//                        message = new JMUIMessage(null, IMessage.MessageType.SEND_VIDEO);
                        } else {
                            throw new RuntimeException("Invalid FileItem type. Must be Type.Image or Type.Video.");
                        }
                    }
                }

                @Override
                public void switchToMicrophoneMode() {
                    if ((ActivityCompat.checkSelfPermission(MainActivity.this,
                            "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mContext,
                            "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mContext,
                            "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                "android.permission.RECORD_AUDIO",
                                "android.permission.WRITE_EXTERNAL_STORAGE",
                                "android.permission.READ_EXTERNAL_STORAGE"}, REQUEST_RECORD_VOICE_PERMISSION);
                    }
                }

                @Override
                public void switchToGalleryMode() {
//                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
//                            Manifest.permission.READ_EXTERNAL_STORAGE
//                    }, REQUEST_PHOTO_PERMISSION);
//                }
                }

                @Override
                public void switchToCameraMode() {
//                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{
//                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    }, REQUEST_CAMERA_PERMISSION);
//                }

                    File rootDir = mContext.getFilesDir();
                    String fileDir = rootDir.getAbsolutePath() + "/photo";
                    mChatView.setCameraCaptureFile(fileDir, "temp_photo");
                }

            });

            mChatView.setRecordVoiceListener(new RecordVoiceListener() {
                @Override
                public void onStartRecord() {
                    // Show record voice interface
                    // 设置存放录音文件目录
                    File rootDir = mContext.getFilesDir();
                    String fileDir = rootDir.getAbsolutePath() + "/voice";
                    mChatView.setRecordVoiceFile(fileDir, new DateFormat().format("yyyy_MMdd_hhmmss",
                            Calendar.getInstance(Locale.CHINA)) + "");
                }

                @Override
                public void onFinishRecord(File voiceFile, int duration) {
                    try {
                        VoiceContent content = new VoiceContent(voiceFile, duration);
                        Message msg = mConv.createSendMessage(content);
                        JMessageClient.sendMessage(msg);
                        final JMUIMessage jmuiMessage = new JMUIMessage(msg);
                        mAdapter.addToStart(jmuiMessage, true);
                        msg.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int status, String s) {
                                mAdapter.updateMessage(jmuiMessage);
                                if (status == 0) {
                                    Log.i(TAG, "send voice message succeed!");
                                } else {
                                    Log.i(TAG, "send voice message failed");
                                }
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelRecord() {

                }
            });

            mChatView.setOnCameraCallbackListener(new OnCameraCallbackListener() {
                @Override
                public void onTakePictureCompleted(final String photoPath) {
                    Bitmap bitmap = BitmapLoader.getBitmapFromFile(photoPath, 720, 1280);
                    ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                        @Override
                        public void gotResult(int status, String desc, ImageContent imageContent) {
                            if (status == 0) {
                                Message msg = mConv.createSendMessage(imageContent);
                                JMessageClient.sendMessage(msg);
                                final JMUIMessage jmuiMessage = new JMUIMessage(msg);
                                jmuiMessage.setMediaFilePath(photoPath);
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.addToStart(jmuiMessage, true);
                                    }
                                });
                                msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                                    @Override
                                    public void onProgressUpdate(double v) {
                                        jmuiMessage.setProgress(Math.ceil(v * 100) + "%");
                                        Log.w(TAG, "Uploading image progress" + Math.ceil(v * 100) + "%");
                                        mAdapter.updateMessage(jmuiMessage);
                                    }
                                });
                                msg.setOnSendCompleteCallback(new BasicCallback() {
                                    @Override
                                    public void gotResult(int status, String desc) {
                                        mAdapter.updateMessage(jmuiMessage);
                                        if (status == 0) {
                                            Log.i(TAG, "Send image succeed");
                                        } else {
                                            Log.i(TAG, "Send image failed, " + desc);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

                @Override
                public void onStartVideoRecord() {

                }

                @Override
                public void onFinishVideoRecord(String videoPath) {

                }

                @Override
                public void onCancelVideoRecord() {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, "will start LoginActivity");
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCoreInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JCoreInterface.onResume(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_VOICE_PERMISSION) {
            if (grantResults.length <= 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Permission denied
                Toast.makeText(mContext, "User denied permission, can't use record voice feature.",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length <= 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Permission denied
                Toast.makeText(mContext, "User denied permission, can't use take photo feature.",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_PHOTO_PERMISSION) {
            if (grantResults.length <= 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Permission denied
                Toast.makeText(mContext, "User denied permission, can't use select photo feature.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initMsgAdapter() {
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                if (string.contains("R.drawable")) {
                    Integer resId = getResources().getIdentifier(string.replace("R.drawable.", ""),
                            "drawable", getPackageName());

                    avatarImageView.setImageResource(resId);
                } else {
                    Glide.with(getApplicationContext())
                            .load(string)
                            .placeholder(R.drawable.aurora_headicon_default)
                            .into(avatarImageView);
                }
            }

            @Override
            public void loadImage(ImageView imageView, String url) {
                Glide.with(mContext).load(url).into(imageView);
            }
        };
        MsgListAdapter.HoldersConfig holdersConfig = new MsgListAdapter.HoldersConfig();
        // Use default layout
        mAdapter = new MsgListAdapter<JMUIMessage>("0", holdersConfig, imageLoader);

        // If you want to customise your layout, try to create custom ViewHolder:
        // holdersConfig.setSenderTxtMsg(CustomViewHolder.class, layoutRes);
        // holdersConfig.setReceiverTxtMsg(CustomViewHolder.class, layoutRes);
        // CustomViewHolder must extends ViewHolders defined in MsgListAdapter.
        // Current ViewHolders are TxtViewHolder, VoiceViewHolder.

        mAdapter.setOnMsgClickListener(new MsgListAdapter.OnMsgClickListener<JMUIMessage>() {
            @Override
            public void onMessageClick(JMUIMessage message) {
                Toast.makeText(mContext, mContext.getString(R.string.message_click_hint),
                        Toast.LENGTH_SHORT).show();
                // do something
            }
        });

        mAdapter.setMsgLongClickListener(new MsgListAdapter.OnMsgLongClickListener<JMUIMessage>() {
            @Override
            public void onMessageLongClick(JMUIMessage message) {
                Toast.makeText(mContext, mContext.getString(R.string.message_long_click_hint),
                        Toast.LENGTH_SHORT).show();
                // do something
            }
        });

        mAdapter.setOnAvatarClickListener(new MsgListAdapter.OnAvatarClickListener<JMUIMessage>() {
            @Override
            public void onAvatarClick(JMUIMessage message) {
                JMUserInfo userInfo = message.getFromUser();
                Toast.makeText(mContext, mContext.getString(R.string.avatar_click_hint),
                        Toast.LENGTH_SHORT).show();
                // Do something
            }
        });

        mAdapter.setMsgResendListener(new MsgListAdapter.OnMsgResendListener<JMUIMessage>() {
            @Override
            public void onMessageResend(final JMUIMessage message) {
                Log.d(TAG, "Resend message: " + message);
                Message msg = message.getJMessage();
                JMessageClient.sendMessage(msg);
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int status, String desc) {
                        mAdapter.updateMessage(message);
                    }
                });
            }
        });

        mAdapter.setOnLoadMoreListener(new MsgListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalCount) {
                Log.i(TAG, "Loading next page");
                if (totalCount == mData.size()) {
                    loadNextPage();
                }
            }
        });
        mChatView.setAdapter(mAdapter);
        mAdapter.addToEnd(mData);
    }

    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mConv != null) {
                    List<Message> msgList = mConv.getMessagesFromNewest(mStart, PAGE_MESSAGE_COUNT);
                    if (msgList != null) {
                        for (Message msg : msgList) {
                            JMUIMessage jmuiMessage = new JMUIMessage(msg);
                            mData.add(0, jmuiMessage);
                        }
                        if (msgList.size() > 0) {
//                            checkSendingImgMsg();
                            mOffset = msgList.size();
                        } else {
                            mOffset = 0;
                        }
                        mStart += mOffset;
                        mAdapter.addToEnd(mData.subList(0, msgList.size()));
                    }
                }
            }
        }, 1000);
    }

    @Override
    public void onKeyBoardStateChanged(int state) {
        switch (state) {
            case ChatInputView.KEYBOARD_STATE_INIT:
                ChatInputView chatInputView = mChatView.getChatInputView();
                if (mImm != null) {
                    mImm.isActive();
                }
                if (chatInputView.getMenuState() == View.INVISIBLE || (!chatInputView.getSoftInputState()
                        && chatInputView.getMenuState() == View.GONE)) {

                    mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                            | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    chatInputView.dismissMenuLayout();
                }
                break;
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (oldh - h > 300) {
            if (SharePreferenceManager.getCachedKeyboardHeight() != oldh - h) {
                SharePreferenceManager.setCachedKeyboardHeight(oldh - h);
                mChatView.setMenuHeight(oldh - h);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ChatInputView chatInputView = mChatView.getChatInputView();
                if (view.getId() == chatInputView.getInputView().getId()) {
                    if (chatInputView.getMenuState() == View.VISIBLE && !chatInputView.getSoftInputState()) {
                        chatInputView.dismissMenuAndResetSoftMode();
                        return false;
                    } else {
                        return false;
                    }
                }
                if (chatInputView.getMenuState() == View.VISIBLE) {
                    chatInputView.dismissMenuLayout();
                }
                if (chatInputView.getSoftInputState()) {
                    View v = getCurrentFocus();
                    if (mImm != null && v != null) {
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        chatInputView.setSoftInputState(false);
                    }
                }
                break;
        }
        return false;
    }

    /**
     * 接收消息类事件
     *
     * @param event 消息事件
     */
    public void onEvent(MessageEvent event) {
        final Message msg = event.getMessage();
        //若为群聊相关事件，如添加、删除群成员
        Log.i(TAG, event.getMessage().toString());
        if (msg.getContentType() == ContentType.eventNotification) {
            GroupInfo groupInfo = (GroupInfo) msg.getTargetInfo();
            long groupId = groupInfo.getGroupID();
            EventNotificationContent.EventNotificationType type = ((EventNotificationContent) msg
                    .getContent()).getEventNotificationType();
            if (groupId == mGroupId) {
                switch (type) {
                    case group_member_added:
                        //添加群成员事件
                        List<String> userNames = ((EventNotificationContent) msg.getContent()).getUserNames();
                        //群主把当前用户添加到群聊，则显示聊天详情按钮
//                        refreshGroupNum();
                        if (userNames.contains(mMyInfo.getNickname()) || userNames.contains(mMyInfo.getUserName())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    mChatView.showRightBtn();
                                }
                            });
                        }

                        break;
                    case group_member_removed:
                        //删除群成员事件
//                        userNames = ((EventNotificationContent) msg.getContent()).getUserNames();
//                        //群主删除了当前用户，则隐藏聊天详情按钮
//                        if (userNames.contains(mMyInfo.getNickname()) || userNames.contains(mMyInfo.getUserName())) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mChatView.dismissRightBtn();
//                                    GroupInfo groupInfo = (GroupInfo) mConv.getTargetInfo();
//                                    if (TextUtils.isEmpty(groupInfo.getGroupName())) {
//                                        mChatView.setChatTitle(IdHelper.getString(mContext, "group"));
//                                    } else {
//                                        mChatView.setChatTitle(groupInfo.getGroupName());
//                                    }
//                                    mChatView.dismissGroupNum();
//                                }
//                            });
//                        } else {
//                            refreshGroupNum();
//                        }
                        break;
                    case group_member_exit:
//                        refreshGroupNum();
                        break;
                }
            }
        }
        //刷新消息
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //收到消息的类型为单聊
                if (msg.getTargetType() == ConversationType.single) {
                    UserInfo userInfo = (UserInfo) msg.getTargetInfo();
                    String targetId = userInfo.getUserName();
                    String appKey = userInfo.getAppKey();
                    //判断消息是否在当前会话中
                    if (mIsSingle && targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
                        JMUIMessage jmuiMessage = new JMUIMessage(msg);
                        Log.i(TAG, "Receiving msg! " + msg);
                        mAdapter.addToStart(jmuiMessage, true);
//                        Message lastMsg = mAdapter.getLastMsg();
                        //收到的消息和Adapter中最后一条消息比较，如果最后一条为空或者不相同，则加入到MsgList
//                        if (lastMsg == null || msg.getId() != lastMsg.getId()) {

//                        } else {
//                            mAdapter.notifyDataSetChanged();
//                        }
                    }
                } else {
                    Log.e(TAG, "unexpected!");
//                    long groupId = ((GroupInfo) msg.getTargetInfo()).getGroupID();
//                    if (groupId == mGroupId) {
//                        Message lastMsg = mChatAdapter.getLastMsg();
//                        if (lastMsg == null || msg.getId() != lastMsg.getId()) {
//                            mChatAdapter.addMsgToList(msg);
//                        } else {
//                            mChatAdapter.notifyDataSetChanged();
//                        }
//                    }
                }
            }
        });
    }
}
