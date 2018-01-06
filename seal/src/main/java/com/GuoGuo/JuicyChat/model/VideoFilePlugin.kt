package com.GuoGuo.JuicyChat.model

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

import com.GuoGuo.JuicyChat.R
import com.GuoGuo.JuicyChat.ui.activity.VideoSendActivity

import io.rong.imkit.RongExtension
import io.rong.imkit.plugin.IPluginModule

/**
 * Created by chenshuai12619 on 2017-12-29.
 */

object VideoFilePlugin : IPluginModule {

    override fun obtainDrawable(context: Context): Drawable {
        return ContextCompat.getDrawable(context, R.drawable.selector_video_file)
    }

    override fun obtainTitle(context: Context): String {
        return "视频文件"
    }

    override fun onClick(fragment: Fragment, rongExtension: RongExtension) {
        rongExtension.startActivityForPluginResult(
                Intent(fragment.context, VideoSendActivity::class.java)
                        .putExtra("targetId", rongExtension.targetId)
                        .putExtra("type", rongExtension.conversationType),
                1,
                this)
        rongExtension.collapseExtension()
    }

    override fun onActivityResult(i: Int, i1: Int, intent: Intent?) {

    }
}
