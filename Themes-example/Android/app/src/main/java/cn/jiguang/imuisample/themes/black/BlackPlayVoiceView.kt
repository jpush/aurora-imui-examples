package cn.jiguang.imuisample.themes.black

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import cn.jiguang.imui.utils.DisplayUtil
import cn.jiguang.imuisample.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.PI

open class BlackPlayVoiceView : View {

    var mPaint: Paint? = null
    var mVolumeCount: Int = 8
    var mDirectionLeft: Boolean = true
    var mDuration: Int = 0
    var mBaseX: Float = 0f
    var mBaseY: Float = 0f
    var mInitHeight: Int = 0
    var mRectWidth: Int = 0
    private var mWaveList: ArrayList<Wave>? = null
    var mPlaying: AtomicBoolean = AtomicBoolean(false)
    var mSimplePlayer: SimpleExoPlayer? = null
    var mMediaUri: Uri? = null
    var mThread: Thread? = null
    var mHandler: MyHandler? = null

    init {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mInitHeight = DisplayUtil.dp2px(context, 4f)
        mRectWidth = DisplayUtil.dp2px(context, 2f)
        mWaveList = ArrayList<Wave>()
        mHandler = MyHandler()
        val bandwidthMeter = DefaultBandwidthMeter()
        val factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val selector = DefaultTrackSelector(factory)
        mSimplePlayer = ExoPlayerFactory.newSimpleInstance(context, selector)
        mSimplePlayer!!.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            }

            override fun onSeekProcessed() {
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPositionDiscontinuity(reason: Int) {
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    Log.i("PlayVoiceView", "Finished playing voice")
                    mPlaying.set(false)
                    for (i in 0 until mWaveList!!.size) {
                        mWaveList!![i].height = mInitHeight.toFloat()
                    }
                    invalidate()
                }
            }

        })
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.BlackPlayVoiceView)
        mDirectionLeft = ta.getBoolean(R.styleable.BlackPlayVoiceView_directionLeft, true)
        ta.recycle()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    }

    open fun setMediaSource(path: String?, duration: Int) {
        mMediaUri = Uri.parse(path)
        mDuration = duration
        if (mDuration in 5..10) {
            mVolumeCount = 10
        } else if (mDuration in 11..20) {
            mVolumeCount = 12
        } else if (mVolumeCount > 20){
            mVolumeCount = 15
        }
        for (i in 0..mVolumeCount) {
            mWaveList!!.add(Wave(mInitHeight.toFloat()))
        }
    }

    open fun setDirection(direction: Boolean) {
        mDirectionLeft = direction
    }

    open fun isPlaying(): Boolean {
        return mPlaying.get()
    }

    open fun start() {
        Log.i("PlayVoiceView", "Playing voice!")
        mPlaying.set(true)
        val timer = Timer()
        mThread = Thread(MyRunnable())
        mThread!!.start()
        timer.schedule(object : TimerTask() {
            override fun run() {
                mPlaying.set(false)
            }
        }, mDuration * 1000L)
        val bandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "SimpleIMUI"), bandwidthMeter)
        val extractorsFactory = DefaultExtractorsFactory()
        val mediaSource = ExtractorMediaSource(mMediaUri, dataSourceFactory, extractorsFactory, null, null)
        mSimplePlayer!!.playWhenReady = true
        mSimplePlayer!!.prepare(mediaSource)
    }

    open fun stop() {
        mPlaying.set(false)
        mSimplePlayer!!.stop()
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mBaseY = height / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mDirectionLeft) {
            mBaseX = DisplayUtil.dp2px(context, 5f).toFloat()
            mPaint!!.color = Color.BLACK
        } else {
            mBaseX = 0f
            mPaint!!.color = Color.WHITE
        }

        Log.i("PlayVoiceVIew", "BaseX: $mBaseX, BaseY: $mBaseY, paddingTop:  $paddingTop")
        for (i in 0..mVolumeCount) {
            canvas?.drawRect(mBaseX, mBaseY - mWaveList!![i].height / 2, mBaseX + mRectWidth,
                    mBaseY + mWaveList!![i].height / 2, mPaint)
            mBaseX += 2 * mRectWidth
        }
    }

    inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            invalidate()
        }
    }

    inner class MyRunnable : Runnable {

        override fun run() {
            try {
                var n = 0.0
                while (n < Int.MAX_VALUE && mPlaying.get()) {
                    for (i in 0 until mWaveList!!.size) {
                        val rate = Math.abs(Math.sin(i + n)).toFloat()
                        mWaveList!![i].height = rate * mBaseY
                        Log.i("PlayVoiceView", "current height " + mWaveList!![i].height)
                    }
                    Thread.sleep(40)
                    mHandler!!.sendEmptyMessage(0)
                    n += 0.1
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

    }


    class Wave(var height: Float) {

    }
}
