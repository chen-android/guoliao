package com.GuoGuo.JuicyChat.utils

import android.app.Activity
import android.os.Handler
import android.os.Message

import java.lang.ref.WeakReference

/**
 *
 */

class StrongHandler(activity: Activity, private val messageListener: HandleMessageListener?) : Handler() {
    private val activityWeakReference: WeakReference<Activity>

    init {
        activityWeakReference = WeakReference(activity)
    }

    override fun handleMessage(msg: Message) {
        val ac = activityWeakReference.get()
        if (ac != null && this.messageListener != null) {
            messageListener.strongHandleMessage(msg)
        }
    }

    interface HandleMessageListener {
        fun strongHandleMessage(msg: Message)
    }
}