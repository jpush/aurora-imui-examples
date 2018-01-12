package cn.jiguang.imuisample.themes.black

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import cn.jiguang.imui.BuildConfig
import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.messages.BaseMessageViewHolder
import cn.jiguang.imui.messages.MessageListStyle
import cn.jiguang.imui.messages.MsgListAdapter
import cn.jiguang.imui.view.RoundImageView
import cn.jiguang.imuisample.R
import cn.jiguang.imuisample.data.MyMessage
import java.text.SimpleDateFormat
import java.util.*

open class BlackImageViewHolder<MESSAGE : IMessage>(itemView: View, private var mIsSender: Boolean)
    : BaseMessageViewHolder<MESSAGE>(itemView), MsgListAdapter.DefaultMessageViewHolder{

    private var mPhotoIv: ImageView? = null
    private var mDateTv: TextView? = null
    private var mDisplayNameTv: TextView? = null
    private var mAvatarIv: RoundImageView? = null
    private var mResendIb: ImageButton? = null
    private var mSendingPb: ProgressBar? = null

    init {
        mPhotoIv = itemView.findViewById(R.id.aurora_iv_msgitem_photo)
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
        if (message.timeString != null) {
            mDateTv!!.text = message.timeString
        }
        val isAvatarExists = message.fromUser.avatarFilePath != null && !message.fromUser.avatarFilePath.isEmpty()
        if (isAvatarExists && mImageLoader != null) {
            if (mPosition + 1 < mData.size) {
                val lastWrapper = mData[mPosition + 1]
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
        if (mScroll) {
            mPhotoIv!!.setImageResource(R.drawable.aurora_picture_not_found)
        } else {
            mImageLoader.loadImage(mPhotoIv, message.mediaFilePath)
        }
        if (mIsSender) {
            when (message.messageStatus) {
                IMessage.MessageStatus.SEND_GOING -> {
                    mSendingPb!!.visibility = View.VISIBLE
                    mResendIb!!.visibility = View.GONE
                }
                IMessage.MessageStatus.SEND_FAILED -> {
                    mSendingPb!!.visibility = View.GONE
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
                }
            }
        }
        mAvatarIv!!.setOnClickListener(View.OnClickListener {
            if (mAvatarClickListener != null) {
                mAvatarClickListener.onAvatarClick(message)
            }
        })

        mPhotoIv!!.setOnClickListener(View.OnClickListener {
            if (mMsgClickListener != null) {
                mMsgClickListener.onMessageClick(message)
            }
        })

        mPhotoIv!!.setOnLongClickListener(View.OnLongClickListener {
            if (mMsgLongClickListener != null) {
                mMsgLongClickListener.onMessageLongClick(message)
            } else {
                if (BuildConfig.DEBUG) {
                    Log.w("MsgListAdapter", "Didn't set long click listener! Drop event.")
                }
            }
            true
        })
    }

    override fun applyStyle(style: MessageListStyle) {
        mDateTv!!.textSize = style.dateTextSize
        mDateTv!!.setTextColor(style.dateTextColor)
        if (mIsSender) {
            mPhotoIv!!.background = style.sendPhotoMsgBg
            if (style.sendingProgressDrawable != null) {
                mSendingPb!!.progressDrawable = style.sendingProgressDrawable
            }
            if (style.sendingIndeterminateDrawable != null) {
                mSendingPb!!.indeterminateDrawable = style.sendingIndeterminateDrawable
            }
            if (style.showSenderDisplayName) {
                mDisplayNameTv!!.visibility = View.VISIBLE
            } else {
                mDisplayNameTv!!.visibility = View.GONE
            }
        } else {
            if (style.showReceiverDisplayName) {
                mDisplayNameTv!!.visibility = View.VISIBLE
            } else {
                mDisplayNameTv!!.visibility = View.GONE
            }
            mPhotoIv!!.background = style.receivePhotoMsgBg
        }
        mAvatarIv!!.layoutParams.width = style.avatarWidth
        mAvatarIv!!.layoutParams.height = style.avatarHeight
        mAvatarIv!!.setBorderRadius(style.avatarRadius)
    }

}