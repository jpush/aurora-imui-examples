package cn.jiguang.imuisample.themes

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import cn.jiguang.imuisample.R
import cn.jiguang.imuisample.databinding.ActivityMainBinding
import cn.jiguang.imuisample.themes.black.BlackActivity
import cn.jiguang.imuisample.themes.default.DefaultActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.default_theme_btn -> {
                val intent = Intent(this, DefaultActivity::class.java)
                startActivity(intent)
            }
            R.id.light_theme_btn -> {

            }
            R.id.black_theme_btn -> {
                val intent = Intent(this, BlackActivity::class.java)
                startActivity(intent)
            }
        }
    }



}
