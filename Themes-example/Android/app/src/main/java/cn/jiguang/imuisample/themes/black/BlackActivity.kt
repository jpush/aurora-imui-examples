package cn.jiguang.imuisample.themes.black

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import cn.jiguang.imuisample.R
import cn.jiguang.imuisample.ViewModelFactory
import cn.jiguang.imuisample.databinding.ActivityBlackBinding
import cn.jiguang.imuisample.model.MessageViewModel
import cn.jiguang.imuisample.util.ActivityUtils

class BlackActivity : AppCompatActivity() {

    lateinit private var mBinding: ActivityBlackBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_black)

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

    private fun obtainViewFragment(): BlackFragment {
        // View Fragment
        val fragment = BlackFragment.newInstance()
        // Send the task ID to the fragment
//            val bundle = Bundle()
//            bundle.putString(DefaultFragment.ARGUMENT_EDIT_TASK_ID,
//                    intent.getStringExtra(DefaultFragment.ARGUMENT_EDIT_TASK_ID))
//            addEditTaskFragment!!.setArguments(bundle)
        return fragment
    }
}