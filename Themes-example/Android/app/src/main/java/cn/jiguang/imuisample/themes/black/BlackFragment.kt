package cn.jiguang.imuisample.themes.black

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.model.FileItem
import cn.jiguang.imui.chatinput.model.VideoItem
import cn.jiguang.imui.commons.ImageLoader
import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.messages.MsgListAdapter
import cn.jiguang.imui.messages.ptr.PtrDefaultHeader
import cn.jiguang.imuisample.R
import cn.jiguang.imuisample.data.DefaultUser
import cn.jiguang.imuisample.data.MyMessage
import cn.jiguang.imuisample.data.source.MessageDataSource
import cn.jiguang.imuisample.databinding.FragmentBlackBinding
import cn.jiguang.imuisample.model.MessageViewModel
import cn.jiguang.imuisample.util.DisplayUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by caiyaoguan on 2017/12/14.
 */
class BlackFragment: Fragment(), View.OnTouchListener {
    val RC_RECORD_VOICE : Int = 0x0001
    val RC_CAMERA : Int = 0x0002
    val RC_PHOTO : Int = 0x0003

    companion object {
        fun newInstance(): BlackFragment {
            return BlackFragment()
        }
    }

    var binding: FragmentBlackBinding? = null
    private var mViewModel: MessageViewModel? = null
    var mAdapter: MsgListAdapter<MyMessage>? = null
    var mImm : InputMethodManager? = null
    var mWindow : Window? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater?.inflate(R.layout.fragment_black, container, false)
        if (binding == null) {
            binding = FragmentBlackBinding.bind(root!!)
        }
        mViewModel = BlackActivity.obtainViewModel(activity)
        setup()
        binding!!.msgList.setOnTouchListener(this)
        binding!!.chatInput.setOnTouchListener(this)
        return binding!!.root
    }

    fun setup() {
        mImm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mWindow = activity.window
        val ptrLayout = binding!!.pullToRefreshLayout
        val header = PtrDefaultHeader(activity)
        val colors = resources.getIntArray(R.array.google_colors)
        header.setColorSchemeColors(colors)
        header.layoutParams = RelativeLayout.LayoutParams(-1, -2)
        header.setPadding(0, DisplayUtil.dp2px(activity, 15f), 0,
                DisplayUtil.dp2px(activity, 10f))
        header.setPtrFrameLayout(ptrLayout)
        val msgList = binding!!.msgList
        val holdersConfig = MsgListAdapter.HoldersConfig()
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
        val msg1 = MyMessage("Hello world", IMessage.MessageType.SEND_TEXT, sendUser)
        val receiverUser = DefaultUser("1", "user2", "R.drawable.deadpool")
        val msg2 = MyMessage("Hi", IMessage.MessageType.RECEIVE_TEXT, receiverUser)
        mAdapter!!.addToStart(msg1, false)
        mAdapter!!.addToStart(msg2, false)
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
        val chatInput = binding!!.chatInput
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
                val message = MyMessage(input.toString(), IMessage.MessageType.SEND_TEXT)
                message.user = DefaultUser("1", "Ironman", "R.drawable.ironman")
                message.timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
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
                        message = MyMessage(null, IMessage.MessageType.SEND_IMAGE)

                    } else if (item.type == FileItem.Type.Video) {
                        message = MyMessage(null, IMessage.MessageType.SEND_VIDEO)
                        message.duration = (item as VideoItem).duration

                    } else {
                        throw RuntimeException("Invalid FileItem type. Must be Type.Image or Type.Video")
                    }

                    message.timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    message.mediaFilePath = item.filePath
                    message.user = DefaultUser("1", "Ironman", "R.drawable.ironman")

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
                val chatInputView = binding!!.chatInput
                Log.i("DefaultFragment","on touch event ")
                if (view?.id == chatInputView.id) {
                    if (chatInputView.menuState == View.VISIBLE && !chatInputView.softInputState) {
                        chatInputView.dismissMenuAndResetSoftMode()
                        return false
                    } else {
                        return false
                    }
                }
                if (binding!!.chatInput.menuState == View.VISIBLE) {
                    binding!!.chatInput.dismissMenuLayout()
                }
                try {
                    val v = activity.currentFocus;
                    if (mImm != null && v != null) {
                        mImm!!.hideSoftInputFromWindow(v.windowToken, 0)
                        mWindow!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                        view?.clearFocus()
                        binding!!.chatInput.softInputState = false
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