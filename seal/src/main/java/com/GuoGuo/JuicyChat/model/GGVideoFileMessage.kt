package com.GuoGuo.JuicyChat.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import io.rong.imlib.MessageTag
import io.rong.imlib.model.MessageContent
import org.json.JSONObject

/**
 * Created by chenshuai12619 on 2018-01-06.
 */
@MessageTag(value = "JC:VideoFileMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class GGVideoFileMessage : MessageContent {
    var url: String? = null
    var picurl: String? = null
    var duration: Long = 0

    constructor(url: String, picurl: String, duration: Long) {
        this.url = url
        this.picurl = picurl
        this.duration = duration
    }

    override fun encode(): ByteArray? {
        val json = JSONObject()
        try {
            if (!TextUtils.isEmpty(this.url)) {
                json.put("url", this.url)
            }
            if (!TextUtils.isEmpty(this.picurl)) {
                json.put("picurl", this.picurl)
            }
            if (duration != -1L) {
                json.put("duration", this.duration)
            }
            return json.toString().toByteArray(charset("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    constructor(data: ByteArray) {
        var msg: String? = null
        try {
            msg = String(data, charset("UTF-8"))
            val json = JSONObject(msg)
            if (json.has("url")) {
                this.url = json.optString("url")
            }
            if (json.has("picurl")) {
                this.picurl = json.optString("picurl")
            }
            if (json.has("duration")) {
                this.duration = json.optLong("duration")
            }
        } catch (e: Exception) {

        }

    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.url)
        dest.writeString(this.picurl)
        dest.writeLong(this.duration)
    }

    protected constructor(`in`: Parcel) {
        this.url = `in`.readString()
        this.picurl = `in`.readString()
        this.duration = `in`.readLong()
    }

    companion object {
        val CONTENT_PREFIX = "[视频]"

        fun obtain(url: String, picurl: String, duration: Long): GGVideoFileMessage {
            return GGVideoFileMessage(url, picurl, duration)
        }

        @JvmField
        val CREATOR: Parcelable.Creator<GGVideoFileMessage> = object : Parcelable.Creator<GGVideoFileMessage> {
            override fun createFromParcel(source: Parcel): GGVideoFileMessage {
                return GGVideoFileMessage(source)
            }

            override fun newArray(size: Int): Array<GGVideoFileMessage?> {
                return arrayOfNulls(size)
            }
        }
    }
}
