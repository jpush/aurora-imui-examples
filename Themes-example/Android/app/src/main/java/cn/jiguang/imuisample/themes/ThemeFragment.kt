package cn.jiguang.imuisample.themes

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import cn.jiguang.imui.chatinput.listener.OnCameraCallbackListener
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener
import cn.jiguang.imui.chatinput.model.FileItem
import cn.jiguang.imui.chatinput.model.VideoItem
import cn.jiguang.imui.commons.ImageLoader
import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.messages.CustomMsgConfig
import cn.jiguang.imui.messages.MsgListAdapter
import cn.jiguang.imui.messages.ptr.PtrDefaultHeader
import cn.jiguang.imuisample.R
import cn.jiguang.imuisample.data.DefaultUser
import cn.jiguang.imuisample.data.MyMessage
import cn.jiguang.imuisample.data.source.MessageDataSource
import cn.jiguang.imuisample.databinding.FragmentThemeBinding
import cn.jiguang.imuisample.model.MessageViewModel
import cn.jiguang.imuisample.themes.black.BlackTxtViewHolder
import cn.jiguang.imuisample.themes.black.BlackVoiceViewHolder
import cn.jiguang.imuisample.util.DisplayUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ThemeFragment : Fragment(), View.OnTouchListener {

    val RC_RECORD_VOICE : Int = 0x0001
    val RC_CAMERA : Int = 0x0002
    val RC_PHOTO : Int = 0x0003

    companion object {
        val BLACK_SEND_TXT: Int = 13
        val BLACK_RECEIVE_TXT: Int = 14
        val BLACK_SEND_VOICE: Int = 15
        val BLACK_RECEIVE_VOICE: Int = 16
        val LIGHT_SEND_TXT : Int = 17
        var STYLE: ThemeStyle = ThemeStyle.DEFAULT
        fun newInstance(style: ThemeStyle): ThemeFragment {
            STYLE = style
            return ThemeFragment()
        }
    }

    var mBinding: FragmentThemeBinding? = null
    private var mViewModel: MessageViewModel? = null
    var mAdapter: MsgListAdapter<MyMessage>? = null
    var mImm : InputMethodManager? = null
    var mWindow : Window? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater?.inflate(R.layout.fragment_theme, container, false)
        if (mBinding == null) {
            mBinding = FragmentThemeBinding.bind(root!!)
        }
        mViewModel = ThemeActivity.obtainViewModel(activity)
        setup()
        mBinding!!.msgList.setOnTouchListener(this)
        mBinding!!.chatInput.setOnTouchListener(this)
        return mBinding!!.root
    }

    fun setup() {
        val ptrLayout = mBinding!!.pullToRefreshLayout
        val header = PtrDefaultHeader(activity)
        val msgList = mBinding!!.msgList
        val chatInput = mBinding!!.chatInput
        val holdersConfig = MsgListAdapter.HoldersConfig()
        // Construct image loader
        val imageLoader = object: ImageLoader {
            override fun loadImage(imageView: ImageView?, string: String?) {
                // You can use other image load libraries.
                Glide.with(activity.applicationContext)
                        .load(string)
                        .fitCenter()
                        .placeholder(R.drawable.aurora_picture_not_found)
                        .override(400, Target.SIZE_ORIGINAL)
                        .into(imageView)
            }

            override fun loadAvatarImage(avatarImageView: ImageView?, string: String?) {
                if (string!!.contains("R.drawable")) {
                    val resId = resources.getIdentifier(string.replace("R.drawable.", ""),
                            "drawable", activity.packageName)
                    avatarImageView!!.setImageResource(resId)
                } else {
                    Glide.with(activity.applicationContext)
                            .load(string)
                            .placeholder(R.drawable.aurora_headicon_default)
                            .into(avatarImageView)
                }
            }
        }
        mAdapter = MsgListAdapter("0", holdersConfig, imageLoader)

        val sendUser = DefaultUser("0", "user1", "R.drawable.ironman")
        val msg1 = MyMessage("Hello world", IMessage.MessageType.SEND_TEXT.ordinal, sendUser)
        val receiverUser = DefaultUser("1", "user2", "R.drawable.deadpool")
        val msg2 = MyMessage("Hi", IMessage.MessageType.RECEIVE_TEXT.ordinal, receiverUser)
        msg1.setTimeString(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
        msg2.setTimeString(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
        // config style
        when(STYLE) {
            // black theme
            ThemeStyle.BLACK -> {
                ptrLayout.setBackgroundColor(Color.parseColor("#F9FAFC"))
                msgList.setSendBubbleDrawable(R.drawable.black_send_bubble)
                msgList.setReceiveBubbleDrawable(R.drawable.black_receive_bubble)
                msgList.setSendBubblePaddingLeft(DisplayUtil.dp2px(activity, 10f))
                msgList.setSendBubblePaddingRight(DisplayUtil.dp2px(activity, 10f))
                msgList.setReceiveBubblePaddingLeft(DisplayUtil.dp2px(activity, 10f))
                msgList.setReceiveBubblePaddingRight(DisplayUtil.dp2px(activity, 10f))
                // custom type
                msg1.type = BLACK_SEND_TXT
                msg2.type = BLACK_RECEIVE_TXT
                val blackSendTxtConfig = CustomMsgConfig(BLACK_SEND_TXT, R.layout.item_msglist_black_send_txt, true, BlackTxtViewHolder::class.java)
                val blackReceiveTxtConfig = CustomMsgConfig(BLACK_RECEIVE_TXT, R.layout.item_msglist_black_receive_txt, false, BlackTxtViewHolder::class.java)
                val blackSendVoiceConfig = CustomMsgConfig(BLACK_SEND_VOICE, R.layout.item_msglist_black_send_voice, true, BlackVoiceViewHolder::class.java)
                val blackReceiveVoiceConfig = CustomMsgConfig(BLACK_RECEIVE_VOICE, R.layout.item_msglist_black_receive_txt, false, BlackVoiceViewHolder::class.java)
                mAdapter!!.addCustomMsgType(BLACK_SEND_TXT, blackSendTxtConfig)
                mAdapter!!.addCustomMsgType(BLACK_RECEIVE_TXT, blackReceiveTxtConfig)
                mAdapter!!.addCustomMsgType(BLACK_SEND_VOICE, blackSendVoiceConfig)
                mAdapter!!.addCustomMsgType(BLACK_RECEIVE_VOICE, blackReceiveVoiceConfig)
            }
            ThemeStyle.LIGHT -> {
                ptrLayout.setBackgroundColor(Color.WHITE)
                msgList.setSendBubbleDrawable(R.drawable.light_send_bubble)
                msgList.setReceiveBubbleDrawable(R.drawable.light_receive_bubble)
                msgList.setSendBubbleTextColor(activity.resources.getColor(R.color.light_send_text_color))
                msgList.setReceiveBubbleTextColor(activity.resources.getColor(R.color.light_receive_text_color))
                msgList.setSendBubblePaddingLeft(DisplayUtil.dp2px(activity, 10f))
                msgList.setSendBubblePaddingRight(DisplayUtil.dp2px(activity, 10f))
                msgList.setReceiveBubblePaddingLeft(DisplayUtil.dp2px(activity, 10f))
                msgList.setReceiveBubblePaddingRight(DisplayUtil.dp2px(activity, 10f))
                msgList.setSendBubblePaddingTop(DisplayUtil.dp2px(activity, 8f))
                msgList.setSendBubblePaddingBottom(DisplayUtil.dp2px(activity, 8f))
                msgList.setReceiveBubblePaddingTop(DisplayUtil.dp2px(activity, 8f))
                msgList.setReceiveBubblePaddingBottom(DisplayUtil.dp2px(activity, 8f))
                msgList.setSendVoiceDrawable(R.drawable.light_send_voice_bg)
            }
            else -> {
                // default type, do nothing
            }
        }
        mAdapter!!.addToStart(msg1, false)
        mAdapter!!.addToStart(msg2, false)
        mImm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mWindow = activity.window

        val colors = resources.getIntArray(R.array.google_colors)
        header.setColorSchemeColors(colors)
        header.layoutParams = RelativeLayout.LayoutParams(-1, -2)
        header.setPadding(0, DisplayUtil.dp2px(activity, 15f), 0,
                DisplayUtil.dp2px(activity, 10f))
        header.setPtrFrameLayout(ptrLayout)

        msgList.setHasFixedSize(true)
        msgList.setAdapter(mAdapter)
        ptrLayout.setLoadingMinTime(1000)
        ptrLayout.setDurationToCloseHeader(1500)
        ptrLayout.headerView = header
        ptrLayout.addPtrUIHandler(header)
        // 下拉刷新时，内容固定，只有 Header 变化
        ptrLayout.isPinContent = true

        ptrLayout.setPtrHandler({
            Log.i("MessageListActivity", "Loading next page")
            mViewModel!!.loadMessages(object : MessageDataSource.LoadHistoryMessages {
                override fun onResult(list: List<MyMessage>) {
                    activity.runOnUiThread(Runnable {
                        mAdapter!!.addToEnd(list)
                        ptrLayout.refreshComplete()
                    })
                }
            })
        })
        chatInput.setMenuClickListener(object: OnMenuClickListener {
            override fun switchToMicrophoneMode(): Boolean {
                scrollToBottom()
                val params = arrayOf(Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (!EasyPermissions.hasPermissions(activity, *params)) {
                    EasyPermissions.requestPermissions(activity,
                            resources.getString(R.string.rationale_record_voice),
                            RC_RECORD_VOICE, *params)
                }
                return true
            }

            override fun switchToEmojiMode(): Boolean {
                scrollToBottom()
                return true
            }

            override fun switchToCameraMode(): Boolean {
                scrollToBottom()
                val params = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)

                if (!EasyPermissions.hasPermissions(activity, *params)) {
                    EasyPermissions.requestPermissions(activity,
                            resources.getString(R.string.rationale_camera),
                            RC_CAMERA, *params)
                }
                return true
            }

            override fun onSendTextMessage(input: CharSequence?): Boolean {
                if (input!!.isEmpty()) {
                    return false
                }
                val message: MyMessage
                when (STYLE) {
                    ThemeStyle.BLACK -> {
                        message = MyMessage(input.toString(), BLACK_SEND_TXT)
                    }
                    ThemeStyle.LIGHT -> {
                        message = MyMessage(input.toString(), LIGHT_SEND_TXT)
                    }
                    else -> {
                        message = MyMessage(input.toString(), IMessage.MessageType.SEND_TEXT.ordinal)
                    }
                }

                message.user = DefaultUser("0", "user1", "R.drawable.ironman")
                message.setTimeString(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
                mAdapter!!.addToStart(message, true)
                return true
            }

            override fun onSendFiles(list: MutableList<FileItem>?) {
                if (list == null || list.isEmpty()) {
                    return
                }

                var message: MyMessage
                for (item in list) {
                    if (item.type == FileItem.Type.Image) {
                        message = MyMessage("", IMessage.MessageType.SEND_IMAGE.ordinal)

                    } else if (item.type == FileItem.Type.Video) {
                        message = MyMessage("", IMessage.MessageType.SEND_VIDEO.ordinal)
                        message.duration = (item as VideoItem).duration

                    } else {
                        throw RuntimeException("Invalid FileItem type. Must be Type.Image or Type.Video")
                    }

                    message.setTimeString(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
                    message.setMediaFilePath(item.filePath)
                    message.user = DefaultUser("0", "user1", "R.drawable.ironman")

                    activity.runOnUiThread({ mAdapter!!.addToStart(message, true) })
                }
            }

            override fun switchToGalleryMode(): Boolean {
                scrollToBottom()
                val params = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

                if (!EasyPermissions.hasPermissions(activity, *params)) {
                    EasyPermissions.requestPermissions(activity,
                            resources.getString(R.string.rationale_photo),
                            RC_PHOTO, *params)
                }
                return true
            }

        })

        chatInput.setOnClickEditTextListener({
            Handler().postDelayed({ msgList.smoothScrollToPosition(0) }, 100)
        })

        // Record voice callback
        chatInput.recordVoiceButton.setRecordVoiceListener(object : RecordVoiceListener {
            override fun onFinishRecord(voiceFile: File, duration: Int) {
                val message = MyMessage("", IMessage.MessageType.SEND_VOICE.ordinal)
                if (STYLE == ThemeStyle.BLACK) {
                    message.type = BLACK_SEND_VOICE
                }
                message.user = DefaultUser("0", "user1", "R.drawable.ironman")
                message.setMediaFilePath(voiceFile.path)
                message.duration = duration.toLong()
                message.setTimeString(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
                mAdapter!!.addToStart(message, true)
            }

            override fun onCancelRecord() {
            }

            override fun onStartRecord() {
                // set voice file path, after recording, audio file will save here
                val path = Environment.getExternalStorageDirectory().path + "/voice"
                val destDir = File(path)
                if (!destDir.exists()) {
                    destDir.mkdirs()
                }
                chatInput.recordVoiceButton.setVoiceFilePath(destDir.path, DateFormat.format("yyyy-MM-dd-hhmmss",
                        Calendar.getInstance(Locale.getDefault())).toString() + "")
            }

        })

        // Camera callback
        chatInput.setOnCameraCallbackListener(object : OnCameraCallbackListener {
            override fun onFinishVideoRecord(videoPath: String) {

            }

            override fun onCancelVideoRecord() {
            }

            override fun onTakePictureCompleted(photoPath: String) {
                val message = MyMessage("", IMessage.MessageType.SEND_IMAGE.ordinal)
                message.setTimeString(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
                message.setMediaFilePath(photoPath)
                message.user = DefaultUser("0", "user1", "R.drawable.ironman")
                activity.runOnUiThread( Runnable {
                    mAdapter!!.addToStart(message, true)
                })
            }

            override fun onStartVideoRecord() {
            }

        })

        mAdapter!!.setOnMsgClickListener {
            // do something
        }

        mAdapter!!.setOnAvatarClickListener {
            // do something
        }

        mAdapter!!.setMsgLongClickListener {
            // do something
        }
    }

    fun scrollToBottom() {
        mAdapter!!.layoutManager.scrollToPosition(0)
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        when (motionEvent?.action) {
            MotionEvent.ACTION_DOWN -> {
                val chatInputView = mBinding!!.chatInput
                Log.i("ThemeFragment","on touch event ")
                if (view?.id == chatInputView.id) {
                    if (chatInputView.menuState == View.VISIBLE && !chatInputView.softInputState) {
                        chatInputView.dismissMenuAndResetSoftMode()
                        return false
                    } else {
                        return false
                    }
                }
                if (mBinding!!.chatInput.menuState == View.VISIBLE) {
                    mBinding!!.chatInput.dismissMenuLayout()
                }
                try {
                    val v = activity.currentFocus;
                    if (mImm != null && v != null) {
                        mImm!!.hideSoftInputFromWindow(v.windowToken, 0)
                        mWindow!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                        view?.clearFocus()
                        mBinding!!.chatInput.softInputState = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            MotionEvent.ACTION_UP -> {
                view?.performClick()
            }
        }
        return false
    }


}