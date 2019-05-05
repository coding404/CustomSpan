package com.example.customspan

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val content="测试一下<span style='font-size:24px'>设置内字体大小为24px</span>,颜色为<span style='color:red'>红色</span>字号 "
        MyUtils.useHtml(tv_content,content)
    }
}
