package com.example.customspan

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import org.xml.sax.XMLReader
import java.util.HashMap

/**
 *
 *   created by  liushu
 *   created on  2019-05-05
 *   description：
 *
 **/
class CustomTagHandler(private val mOriginColors: ColorStateList?) : Html.TagHandler {

    private var startIndex = 0
    private var stopIndex = 0

    internal val attributes = HashMap<String, String>()

    override fun handleTag(opening: Boolean, tag: String, output: Editable,
                           xmlReader: XMLReader
    ) {
        processAttributes(xmlReader)

        if (tag.equals(mFont, ignoreCase = true)) {
            if (opening) {
                startSpan(tag, output, xmlReader)
            } else {
                endSpan(tag, output, xmlReader)
                attributes.clear()
            }
        }

    }

    fun startSpan(tag: String, output: Editable, xmlReader: XMLReader) {
        startIndex = output.length
    }

    fun endSpan(tag: String, output: Editable, xmlReader: XMLReader) {
        stopIndex = output.length

        val color = attributes["color"]
        var size = attributes["size"]
        val fontWeight = attributes["font-weight"]
        val style = attributes["style"]
        if (!TextUtils.isEmpty(style)) {
            analysisStyle(startIndex, stopIndex, output, style!!)
        }
        if (!TextUtils.isEmpty(size)) {
            size = size!!.split("px".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        }
        if (!TextUtils.isEmpty(color)) {
            if (color!!.startsWith("@")) {
                val res = Resources.getSystem()
                val name = color.substring(1)
                val colorRes = res.getIdentifier(name, "color", "android")
                if (colorRes != 0) {
                    output.setSpan(ForegroundColorSpan(colorRes), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                try {
                    output.setSpan(ForegroundColorSpan(Color.parseColor(color)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                } catch (e: Exception) {
                    e.printStackTrace()
                    reductionFontColor(startIndex, stopIndex, output)
                }

            }
        }
        if (!TextUtils.isEmpty(size)) {
           // val fontSizePx = DisplayUtil.dp2px(size!!.toFloat())

            output.setSpan(AbsoluteSizeSpan(size!!.toInt()), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        if (!TextUtils.isEmpty(fontWeight)) {
            val normal = formatWeight(fontWeight!!)
            output.setSpan(StyleSpan(normal), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun processAttributes(xmlReader: XMLReader) {
        try {
            val elementField = xmlReader.javaClass.getDeclaredField("theNewElement")
            elementField.isAccessible = true
            val element = elementField.get(xmlReader)
            val attsField = element.javaClass.getDeclaredField("theAtts")
            attsField.isAccessible = true
            val atts = attsField.get(element)
            val dataField = atts.javaClass.getDeclaredField("data")
            dataField.isAccessible = true
            val data = dataField.get(atts) as Array<String>
            val lengthField = atts.javaClass.getDeclaredField("length")
            lengthField.isAccessible = true
            val len = lengthField.get(atts) as Int

            /**
             * MSH: Look for supported attributes and add to hash map.
             * This is as tight as things can get :)
             * The data index is "just" where the keys and values are stored.
             */
            for (i in 0 until len)
                attributes[data[i * 5 + 1]] = data[i * 5 + 4]
        } catch (e: Exception) {
        }

    }

    /**
     * 还原为原来的颜色
     * @param startIndex
     * @param stopIndex
     * @param editable
     */
    private fun reductionFontColor(startIndex: Int, stopIndex: Int, editable: Editable) {
        if (null != mOriginColors) {
            editable.setSpan(
                TextAppearanceSpan(null, 0, 0, mOriginColors, null),
                startIndex, stopIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            editable.setSpan(ForegroundColorSpan(-0xd4d4d5), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    /**
     * 解析style属性
     * @param startIndex
     * @param stopIndex
     * @param editable
     * @param style
     */
    private fun analysisStyle(startIndex: Int, stopIndex: Int, editable: Editable, style: String) {

        val attrArray = style.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val attrMap = HashMap<String, String>()
        if (null != attrArray) {
            for (attr in attrArray) {
                val keyValueArray = attr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (null != keyValueArray && keyValueArray.size == 2) {
                    // 记住要去除前后空格
                    attrMap[keyValueArray[0].trim { it <= ' ' }] = keyValueArray[1].trim { it <= ' ' }
                }
            }
        }
        val color = attrMap["color"]
        var fontSize = attrMap["font-size"]
        val fontWeight = attrMap["font-weight"]
        if (!TextUtils.isEmpty(fontSize)) {
            fontSize = fontSize!!.split("px".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        }
        if (!TextUtils.isEmpty(color)) {
            if (color!!.startsWith("@")) {
                val res = Resources.getSystem()
                val name = color.substring(1)
                val colorRes = res.getIdentifier(name, "color", "android")
                if (colorRes != 0) {
                    editable.setSpan(ForegroundColorSpan(colorRes), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                try {
                    editable.setSpan(ForegroundColorSpan(Color.parseColor(color)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                } catch (e: Exception) {
                    e.printStackTrace()
                    reductionFontColor(startIndex, stopIndex, editable)
                }

            }
        }
        if (!TextUtils.isEmpty(fontSize)) {
          //  val fontSizePx = DisplayUtil.dp2px(fontSize!!.toFloat())
            editable.setSpan(AbsoluteSizeSpan(fontSize!!.toInt()), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        if (!TextUtils.isEmpty(fontWeight)) {
            val normal = formatWeight(fontWeight!!)
            editable.setSpan(StyleSpan(normal), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun formatWeight(weight: String): Int {

        return when (weight.toUpperCase()) {

            //正常
            "NORMAL" -> {
                Typeface.NORMAL
            }
            //加粗
            "BOLD" -> {
                Typeface.BOLD
            }
            //斜体
            "ITALIC" -> {
                Typeface.ITALIC
            }
            //加粗斜体
            "BOLD-ITALIC" -> {
                Typeface.BOLD_ITALIC
            }
            else -> {

                Typeface.NORMAL
            }
        }
    }

    companion object {

        //由于span标签在各版本兼容性问题，需要统一替换为系统未知的自定义标签
        var mFont = "customFont"
    }

}