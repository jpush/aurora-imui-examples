package cn.jiguang.imuisample.themes.black

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import cn.jiguang.imui.BuildConfig
import cn.jiguang.imui.R
import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.messages.BaseMessageViewHolder
import cn.jiguang.imui.messages.MessageListStyle
import cn.jiguang.imui.messages.MsgListAdapter
import cn.jiguang.imui.view.RoundImageView


open class BlackTxtViewHolder<MESSAGE : IMessage>(itemView: View, private val mIsSender: Boolean)
    : BaseMessageViewHolder<MESSAGE>(itemView), MsgListAdapter.DefaultMessageViewHolder {

    val msgTextView: TextView? = null
    private val mDateTv: TextView? = null
    private var mDisplayNameTv: TextView? = null
    private val mAvatarIv: RoundImageView? = null
    private val mResendIb: ImageButton? = null
    private val mSendingPb: ProgressBar? = null

    val avatar: ImageView
        get() = mAvatarIv!!

    init {
    }

    override fun onBind(message: MESSAGE) {
        msgTextView.text = message.text
        if (message.timeString != null) {
            mDateTv.text = message.timeString
        }
        val isAvatarExists = message.fromUser.avatarFilePath != null && !message.fromUser.avatarFilePath.isEmpty()
        if (isAvatarExists && mImageLoader != null) {
            mImageLoader.loadAvatarImage(mAvatarIv, message.fromUser.avatarFilePath)
        } else if (mImageLoader == null) {
            mAvatarIv.visibility = View.GONE
        }
        if (mDisplayNameTv!!.visibility == View.VISIBLE) {
            mDisplayNameTv!!.text = message.fromUser.displayName
        }
        if (mIsSender) {
            when (message.messageStatus) {
                IMessage.MessageStatus.SEND_GOING -> {
                    mSendingPb.visibility = View.VISIBLE
                    mResendIb.visibility = View.GONE
                    Log.i("TxtViewHolder", "sending message")
                }
                IMessage.MessageStatus.SEND_FAILED -> {
                    mSendingPb.visibility = View.GONE
                    Log.i("TxtViewHolder", "send message failed")
                    mResendIb.visibility = View.VISIBLE
                    mResendIb.setOnClickListener {
                        if (mMsgStatusViewClickListener != null) {
                            mMsgStatusViewClickListener.onStatusViewClick(message)
                        }
                    }
                }
                IMessage.MessageStatus.SEND_SUCCEED -> {
                    mSendingPb.visibility = View.GONE
                    mResendIb.visibility = View.GONE
                    Log.i("TxtViewHolder", "send message succeed")
                }
            }
        }

        msgTextView.setOnClickListener {
            if (mMsgClickListener != null) {
                mMsgClickListener.onMessageClick(message)
            }
        }

        msgTextView.setOnLongClickListener {
            if (mMsgLongClickListener != null) {
                mMsgLongClickListener.onMessageLongClick(message)
            } else {
                if (BuildConfig.DEBUG) {
                    Log.w("MsgListAdapter", "Didn't set long click listener! Drop event.")
                }
            }
            true
        }

        mAvatarIv.setOnClickListener {
            if (mAvatarClickListener != null) {
                mAvatarClickListener.onAvatarClick(message)
            }
        }
    }

    override fun applyStyle(style: MessageListStyle) {
        msgTextView.maxWidth = (style.windowWidth * style.bubbleMaxWidth).toInt()
        msgTextView.setLineSpacing(style.lineSpacingExtra.toFloat(), style.lineSpacingMultiplier)
        if (mIsSender) {
            msgTextView.background = style.sendBubbleDrawable
            msgTextView.setTextColor(style.sendBubbleTextColor)
            msgTextView.textSize = style.sendBubbleTextSize
            msgTextView.setPadding(style.sendBubblePaddingLeft,
                    style.sendBubblePaddingTop,
                    style.sendBubblePaddingRight,
                    style.sendBubblePaddingBottom)
            if (style.sendingProgressDrawable != null) {
                mSendingPb.progressDrawable = style.sendingProgressDrawable
            }
            if (style.sendingIndeterminateDrawable != null) {
                mSendingPb.indeterminateDrawable = style.sendingIndeterminateDrawable
            }
            if (style.showSenderDisplayName == 1) {
                mDisplayNameTv!!.visibility = View.VISIBLE
            } else {
                mDisplayNameTv!!.visibility = View.GONE
            }
        } else {
            msgTextView.background = style.receiveBubbleDrawable
            msgTextView.setTextColor(style.receiveBubbleTextColor)
            msgTextView.textSize = style.receiveBubbleTextSize
            msgTextView.setPadding(style.receiveBubblePaddingLeft,
                    style.receiveBubblePaddingTop,
                    style.receiveBubblePaddingRight,
                    style.receiveBubblePaddingBottom)
            if (style.showReceiverDisplayName == 1) {
                mDisplayNameTv!!.visibility = View.VISIBLE
            } else {
                mDisplayNameTv!!.visibility = View.GONE
            }
        }
        mDateTv.textSize = style.dateTextSize
        mDateTv.setTextColor(style.dateTextColor)

        val layoutParams = mAvatarIv.layoutParams
        layoutParams.width = style.avatarWidth
        layoutParams.height = style.avatarHeight
        mAvatarIv.layoutParams = layoutParams
        mAvatarIv.setBorderRadius(style.avatarRadius)
    }

}