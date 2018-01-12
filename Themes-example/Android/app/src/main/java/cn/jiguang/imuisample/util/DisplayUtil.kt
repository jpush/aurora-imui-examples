package cn.jiguang.imuisample.util

import android.content.Context
import android.util.TypedValue

class DisplayUtil {
    companion object {
        fun dp2px(context: Context, dp: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                    context.resources.displayMetrics).toInt()
        }
    }
}