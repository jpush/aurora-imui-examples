package cn.jiguang.imuisample.themes.black

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.messages.BaseMessageViewHolder
import cn.jiguang.imui.messages.MessageListStyle
import cn.jiguang.imui.messages.MsgListAdapter
import cn.jiguang.imui.view.RoundImageView
import cn.jiguang.imuisample.BuildConfig
import cn.jiguang.imuisample.R
import cn.jiguang.imuisample.data.MyMessage
import java.text.SimpleDateFormat
import java.util.*

class BlackTxtViewHolder<MESSAGE : IMessage>(itemView: View, private var mIsSender: Boolean)
    : BaseMessageViewHolder<MESSAGE>(itemView), MsgListAdapter.DefaultMessageViewHolder {

    var mMsgTv: TextView? = null
    private var mDateTv: TextView? = null
    private var mDisplayNameTv: TextView? = null
    private var mAvatarIv: RoundImageView? = null
    private var mResendIb: ImageButton? = null
    private var mSendingPb: ProgressBar? = null

    init {
        mMsgTv = itemView.findViewById(R.id.aurora_tv_msgitem_message)
        mDateTv = itemView.findViewById(R.id.aurora_tv_msgitem_date)
        if (mIsSender) {
            mDisplayNameTv = itemView.findViewById(R.id.aurora_tv_msgitem_sender_display_name)
            mSendingPb = itemView.findViewById(R.id.aurora_pb_msgitem_sending)
            mResendIb = itemView.findViewById(R.id.aurora_ib_msgitem_resend)
        } else {
            mDisplayNameTv = itemView.findViewById(R.id.aurora_tv_msgitem_receiver_display_name)
        }
        mAvatarIv = itemView.findViewById(R.id.aurora_iv_msgitem_avatar)
    }

    override fun onBind(message: MESSAGE) {
        val myMessage = message as MyMessage
        mMsgTv!!.text = message.text
        if (message.timeString != null) {
            mDateTv!!.text = message.timeString
        }
        val isAvatarExists = message.fromUser.avatarFilePath != null && !message.fromUser.avatarFilePath.isEmpty()
        if (isAvatarExists && mImageLoader != null) {
            if (mPosition + 1 < mData.size) {
                val lastWrapper = mData.get(mPosition + 1)
                if (lastWrapper.item is IMessage) {
                    val lastMsg = lastWrapper.item as MyMessage
                    // config time string
                    if (!TextUtils.isEmpty(message.timeString) && !TextUtils.isEmpty(lastMsg.timeString)) {
                        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val date1 = dateFormat.parse(message.timeString)
                        val date2 = dateFormat.parse(lastMsg.timeString)
                        if (date1.time - date2.time > 1 * 60 * 1000) {
                            mDateTv!!.visibility = View.VISIBLE
                        } else {
                            mDateTv!!.visibility = View.GONE
                        }
                    }

                    if (lastMsg.user!!.id == myMessage.user!!.id) {
                        mAvatarIv!!.visibility = View.GONE
                    } else {
                        mAvatarIv!!.visibility = View.VISIBLE
                        mImageLoader.loadAvatarImage(mAvatarIv, message.fromUser.avatarFilePath)
                    }
                }
            } else {
                mAvatarIv!!.visibility = View.VISIBLE
                mImageLoader.loadAvatarImage(mAvatarIv, message.fromUser.avatarFilePath)
            }
        } else if (mImageLoader == null) {
            mAvatarIv!!.visibility = View.GONE
        }
        if (mDisplayNameTv!!.visibility == View.VISIBLE) {
            mDisplayNameTv!!.text = message.fromUser.displayName
        }
        if (mIsSender) {
            when (message.messageStatus) {
                IMessage.MessageStatus.SEND_GOING -> {
                    mSendingPb!!.visibility = View.VISIBLE
                    mResendIb!!.visibility = View.GONE
                    Log.i("TxtViewHolder", "sending message")
                }
                IMessage.MessageStatus.SEND_FAILED -> {
                    mSendingPb!!.visibility = View.GONE
                    Log.i("TxtViewHolder", "send message failed")
                    mResendIb!!.visibility = View.VISIBLE
                    mResendIb!!.setOnClickListener {
                        if (mMsgStatusViewClickListener != null) {
                            mMsgStatusViewClickListener.onStatusViewClick(message)
                        }
                    }
                }
                else -> {
                    mSendingPb!!.visibility = View.GONE
                    mResendIb!!.visibility = View.GONE
                    Log.i("TxtViewHolder", "send message succeed")
                }
            }
        }

        mMsgTv!!.setOnClickListener {
            if (mMsgClickListener != null) {
                mMsgClickListener.onMessageClick(message)
            }
        }

        mMsgTv!!.setOnLongClickListener {
            if (mMsgLongClickListener != null) {
                mMsgLongClickListener.onMessageLongClick(message)
            } else {
                if (BuildConfig.DEBUG) {
                    Log.w("MsgListAdapter", "Didn't set long click listener! Drop event.")
                }
            }
            true
        }

        mAvatarIv!!.setOnClickListener {
            if (mAvatarClickListener != null) {
                mAvatarClickListener.onAvatarClick(message)
            }
        }
    }

    override fun applyStyle(style: MessageListStyle) {
        mMsgTv!!.maxWidth = (style.windowWidth * style.bubbleMaxWidth).toInt()
        mMsgTv!!.setLineSpacing(style.lineSpacingExtra.toFloat(), style.lineSpacingMultiplier)
        if (mIsSender) {
            mMsgTv!!.background = style.sendBubbleDrawable
            mMsgTv!!.setTextColor(style.sendBubbleTextColor)
            mMsgTv!!.textSize = style.sendBubbleTextSize
            mMsgTv!!.setPadding(style.sendBubblePaddingLeft,
                    style.sendBubblePaddingTop,
                    style.sendBubblePaddingRight,
                    style.sendBubblePaddingBottom)
            if (style.sendingProgressDrawable != null) {
                mSendingPb!!.progressDrawable = style.sendingProgressDrawable
            }
            if (style.sendingIndeterminateDrawable != null) {
                mSendingPb!!.indeterminateDrawable = style.sendingIndeterminateDrawable
            }
            if (style.showSenderDisplayName == 1) {
                mDisplayNameTv!!.visibility = View.VISIBLE
            } else {
                mDisplayNameTv!!.visibility = View.GONE
            }
        } else {
            mMsgTv!!.background = style.receiveBubbleDrawable
            mMsgTv!!.setTextColor(style.receiveBubbleTextColor)
            mMsgTv!!.textSize = style.receiveBubbleTextSize
            mMsgTv!!.setPadding(style.receiveBubblePaddingLeft,
                    style.receiveBubblePaddingTop,
                    style.receiveBubblePaddingRight,
                    style.receiveBubblePaddingBottom)
            if (style.showReceiverDisplayName == 1) {
                mDisplayNameTv!!.visibility = View.VISIBLE
            } else {
                mDisplayNameTv!!.visibility = View.GONE
            }
        }
        mDateTv!!.textSize = style.dateTextSize
        mDateTv!!.setTextColor(style.dateTextColor)

        val layoutParams = mAvatarIv!!.layoutParams
        layoutParams.width = style.avatarWidth
        layoutParams.height = style.avatarHeight
        mAvatarIv!!.layoutParams = layoutParams
        mAvatarIv!!.setBorderRadius(style.avatarRadius)
    }
}