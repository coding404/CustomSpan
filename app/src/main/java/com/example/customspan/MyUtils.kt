package com.example.customspan

import android.os.Build
import android.text.Html
import android.widget.TextView

/**
 *
 *   created by  liushu
 *   created on  2019-05-05
 *   description：工具类
 *
 **/
object MyUtils {

    /**
     * @param tv 填写的textview
     * @param str 内容
     */
    fun useHtml(tv: TextView?, str: String?) {

        try {
            if (tv == null || str.isNullOrEmpty()) return

            val realStr = "<body>${str.replace("span", CustomTagHandler.mFont)}</body>"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tv.text = Html.fromHtml(realStr, Html.FROM_HTML_MODE_COMPACT, null, CustomTagHandler(tv.textColors))

            } else {
                tv.text = Html.fromHtml(realStr, null, CustomTagHandler(tv.textColors))
            }
        } catch (e: Exception) {
            tv?.text = ""
        }

    }
}