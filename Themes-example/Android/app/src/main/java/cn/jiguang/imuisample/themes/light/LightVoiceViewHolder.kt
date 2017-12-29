package cn.jiguang.imuisample.themes.light

import android.graphics.drawable.AnimationDrawable
import android.media.AudioManager
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
import cn.jiguang.imui.messages.ViewHolderController
import cn.jiguang.imui.view.RoundImageView
import cn.jiguang.imuisample.R
import cn.jiguang.imuisample.data.MyMessage
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LightVoiceViewHolder<MESSAGE : IMessage>(itemView: View, private var mIsSender: Boolean)
    : BaseMessageViewHolder<MESSAGE>(itemView), MsgListAdapter.DefaultMessageViewHolder {
    var mMsgTv: TextView? = null
    private var mDateTv: TextView? = null
    private var mDisplayNameTv: TextView? = null
    private var mAvatarIv: RoundImageView? = null
    private var mResendIb: ImageButton? = null
    private var mSendingPb: ProgressBar? = null
    private var mVoiceAnimIv: ImageView? = null
    private var mLengthTv: TextView? = null
    private var mController: ViewHolderController? = null
    private var mSetData = false
    private var mVoiceAnimation: AnimationDrawable? = null
    private var mFIS: FileInputStream? = null

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
        mLengthTv = itemView.findViewById(R.id.aurora_tv_voice_length)
        mAvatarIv = itemView.findViewById(R.id.aurora_iv_msgitem_avatar)
        mVoiceAnimIv = itemView.findViewById(R.id.aurora_iv_msgitem_voice_anim)
        mController = ViewHolderController.getInstance()
    }


    override fun onBind(message: MESSAGE) {
        val myMessage = message as MyMessage
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING)
        mMediaPlayer.setOnErrorListener { mp, what, extra -> false }
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
                }
            }
            mAvatarIv!!.visibility = View.VISIBLE
            mImageLoader.loadAvatarImage(mAvatarIv, message.fromUser.avatarFilePath)
        }
        val duration = message.duration
        val lengthStr = duration.toString() + mContext.getString(cn.jiguang.imui.R.string.aurora_symbol_second)
        val width = (-0.04 * duration.toDouble() * duration.toDouble() + 4.526 * duration + 75.214).toInt()
        mMsgTv!!.width = (width * mDensity).toInt()
        mLengthTv!!.setText(lengthStr)
        if (mDisplayNameTv!!.visibility == View.VISIBLE) {
            mDisplayNameTv!!.text = message.fromUser.displayName
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
                    mResendIb!!.setOnClickListener(View.OnClickListener {
                        if (mMsgStatusViewClickListener != null) {
                            mMsgStatusViewClickListener.onStatusViewClick(message)
                        }
                    })
                }
                else -> {
                    mSendingPb!!.visibility = View.GONE
                    mResendIb!!.visibility = View.GONE
                }
            }
        } else {
            when (message.messageStatus) {
                IMessage.MessageStatus.RECEIVE_FAILED -> {
                    mResendIb!!.visibility = View.VISIBLE
                    mResendIb!!.setOnClickListener(View.OnClickListener {
                        if (mMsgStatusViewClickListener != null) {
                            mMsgStatusViewClickListener.onStatusViewClick(message)
                        }
                    })
                }
                IMessage.MessageStatus.RECEIVE_SUCCEED -> mResendIb!!.visibility = View.GONE
            }
        }

        mMsgTv!!.setOnClickListener(View.OnClickListener {
            if (mMsgClickListener != null) {
                mMsgClickListener.onMessageClick(message)
            }

            mController!!.notifyAnimStop()
            mController!!.message = message
            if (mIsSender) {
                mVoiceAnimIv!!.setImageResource(R.drawable.light_send_voice_bg)
            } else {
                mVoiceAnimIv!!.setImageResource(R.drawable.light_receive_voice_bg)
            }
            mVoiceAnimation = mVoiceAnimIv!!.drawable as AnimationDrawable
            mController!!.addView(adapterPosition, mVoiceAnimIv)
            // If audio is playing, pause
            Log.e("VoiceViewHolder", "MediaPlayer playing " + mMediaPlayer.isPlaying + "now position " + adapterPosition)
            if (mController!!.lastPlayPosition == adapterPosition) {
                if (mMediaPlayer.isPlaying) {
                    pauseVoice()
                    mVoiceAnimation!!.stop()
                    if (mIsSender) {
                        mVoiceAnimIv!!.setImageResource(R.drawable.light_send_voice_3)
                    } else {
                        mVoiceAnimIv!!.setImageResource(R.drawable.light_receive_voice_3)
                    }
                } else if (mSetData) {
                    mMediaPlayer.start()
                    mVoiceAnimation!!.start()
                } else {
                    playVoice(adapterPosition, message)
                }
                // Start playing audio
            } else {
                playVoice(adapterPosition, message)
            }
        })

        mMsgTv!!.setOnLongClickListener(View.OnLongClickListener {
            if (mMsgLongClickListener != null) {
                mMsgLongClickListener.onMessageLongClick(message)
            } else {
                if (BuildConfig.DEBUG) {
                    Log.w("MsgListAdapter", "Didn't set long click listener! Drop event.")
                }
            }
            true
        })

        mAvatarIv!!.setOnClickListener(View.OnClickListener {
            if (mAvatarClickListener != null) {
                mAvatarClickListener.onAvatarClick(message)
            }
        })
    }

    fun playVoice(position: Int, message: MESSAGE) {
        mController!!.setLastPlayPosition(position, mIsSender)
        try {
            mMediaPlayer.reset()
            mFIS = FileInputStream(message.mediaFilePath)
            mMediaPlayer.setDataSource(mFIS!!.fd)
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL)
            mMediaPlayer.prepare()
            mMediaPlayer.setOnPreparedListener { mp ->
                mVoiceAnimation!!.start()
                mp.start()
            }
            mMediaPlayer.setOnCompletionListener { mp ->
                mVoiceAnimation!!.stop()
                mp.reset()
                mSetData = false
                if (mIsSender) {
                    mVoiceAnimIv!!.setImageResource(R.drawable.light_send_voice_3)
                } else {
                    mVoiceAnimIv!!.setImageResource(R.drawable.light_receive_voice_3)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (mFIS != null) {
                    mFIS!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun pauseVoice() {
        mMediaPlayer.pause()
        mSetData = true
    }

    override fun applyStyle(style: MessageListStyle) {
        mDateTv!!.textSize = style.dateTextSize
        mDateTv!!.setTextColor(style.dateTextColor)
        if (mIsSender) {
            mVoiceAnimIv!!.setImageResource(R.drawable.light_send_voice_3)
            mMsgTv!!.background = style.sendBubbleDrawable
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
            mVoiceAnimIv!!.setImageResource(R.drawable.light_receive_voice_3)
            mMsgTv!!.background = style.receiveBubbleDrawable
            if (style.showReceiverDisplayName == 1) {
                mDisplayNameTv!!.visibility = View.VISIBLE
            } else {
                mDisplayNameTv!!.visibility = View.GONE
            }
        }
        mAvatarIv!!.layoutParams.width = style.avatarWidth
        mAvatarIv!!.layoutParams.height = style.avatarHeight
        mAvatarIv!!.setBorderRadius(style.avatarRadius)
    }
}