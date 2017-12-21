package cn.jiguang.imuisample.themes

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import cn.jiguang.imuisample.R
import cn.jiguang.imuisample.ViewModelFactory
import cn.jiguang.imuisample.databinding.ActivityThemeBinding
import cn.jiguang.imuisample.model.MessageViewModel
import cn.jiguang.imuisample.util.ActivityUtils


class ThemeActivity : AppCompatActivity() {

    lateinit private var mBinding: ActivityThemeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_theme)
        val fragment = obtainViewFragment()
        ActivityUtils.replaceFragmentInActivity(supportFragmentManager,
                fragment, mBinding.contentFrame.id)

    }

    companion object {
        fun obtainViewModel(activity: FragmentActivity): MessageViewModel {
            val viewModelFactory = ViewModelFactory.getInstance(activity.application)
            return ViewModelProviders.of(activity, viewModelFactory).get(MessageViewModel::class.java)
        }
    }

    private fun obtainViewFragment(): Fragment {
        var style = ThemeStyle.DEFAULT
        val titleTv = mBinding.chatTitleBar!!.findViewById<TextView>(R.id.title_tv)
        val styleStr = intent.getStringExtra("style")
        if (styleStr == ThemeStyle.BLACK.name) {
            style = ThemeStyle.BLACK
            mBinding.chatTitleBar!!.setBackgroundColor(Color.parseColor("#3A4149"))
            titleTv.text = resources.getString(R.string.black_theme)
        } else if (styleStr == ThemeStyle.LIGHT.name) {
            style = ThemeStyle.LIGHT
            titleTv.text = resources.getString(R.string.light_theme)
        }
        return ThemeFragment.newInstance(style)
    }
}