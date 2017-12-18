package cn.jiguang.imuisample.themes.default

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import cn.jiguang.imuisample.R
import cn.jiguang.imuisample.ViewModelFactory
import cn.jiguang.imuisample.databinding.ActivityDefaultBinding
import cn.jiguang.imuisample.model.MessageViewModel
import cn.jiguang.imuisample.util.ActivityUtils


class DefaultActivity : AppCompatActivity() {

    lateinit private var mBinding: ActivityDefaultBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_default)

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

    private fun obtainViewFragment(): DefaultFragment {
        // View Fragment
        var fragment = DefaultFragment.newInstance()
            // Send the task ID to the fragment
//            val bundle = Bundle()
//            bundle.putString(DefaultFragment.ARGUMENT_EDIT_TASK_ID,
//                    intent.getStringExtra(DefaultFragment.ARGUMENT_EDIT_TASK_ID))
//            addEditTaskFragment!!.setArguments(bundle)
        return fragment
    }
}