package com.kuaishou.hb.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import io.rong.imlib.MessageTag
import io.rong.imlib.model.MessageContent
import java.io.UnsupportedEncodingException

/**
 * Created by cs on 2017/5/12.
 */

@MessageTag(value = "JC:RedPacketMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class GGRedPacketMessage : MessageContent {
    var redpacketId: String? = null
    var tomemberid: Long = -1
    var fromuserid: Long = -1
    var type = -1//1私聊  2群
    var money: Long = -1
    var content: String? = null
    var sort = -1//1普通  2任务
    var count = -1
    var state = -1//1未领取  2已领取  3已退回
    var createtime: String? = null

    constructor(redpacketId: String, tomemberid: Long, fromuserid: Long, type: Int, money: Long, content: String, sort: Int, count: Int, state: Int, createtime: String) {
        this.redpacketId = redpacketId
        this.tomemberid = tomemberid
        this.fromuserid = fromuserid
        this.type = type
        this.money = money
        this.content = content
        this.sort = sort
        this.count = count
        this.state = state
        this.createtime = createtime
    }

    override fun encode(): ByteArray? {
        val jsonObj = JSONObject()

        try {
            if (!TextUtils.isEmpty(this.redpacketId)) {
                jsonObj.put("redpacketId", this.redpacketId)
            }
            if (tomemberid != -1L) {
                jsonObj.put("tomemberid", tomemberid)
            }
            if (fromuserid != -1L) {
                jsonObj.put("fromuserid", fromuserid)
            }
            if (type != -1) {
                jsonObj.put("type", type)
            }
            if (money != -1L) {
                jsonObj.put("money", money)
            }
            if (!TextUtils.isEmpty(content)) {
                jsonObj.put("content", content)
            }
            if (sort != -1) {
                jsonObj.put("sort", sort)
            }
            if (count != -1) {
                jsonObj.put("count", count)
            }
            if (state != -1) {
                jsonObj.put("state", state)
            }
            if (!TextUtils.isEmpty(createtime)) {
                jsonObj.put("createtime", createtime)
            }
        } catch (e: JSONException) {
            Log.e("JSONException", e.message)
        }

        try {
            return jsonObj.toString().toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return null
    }

    constructor(bytes: ByteArray) {
        var msg: String? = null

        try {
            msg = String(bytes, charset("UTF-8"))
        } catch (var5: UnsupportedEncodingException) {

        }

        try {
            val var3 = org.json.JSONObject(msg)
            if (var3.has("redpacketId")) {
                this.redpacketId = var3.optString("redpacketId")
            }
            if (var3.has("tomemberid")) {
                this.tomemberid = var3.optLong("tomemberid")
            }
            if (var3.has("fromuserid")) {
                this.fromuserid = var3.optLong("fromuserid")
            }
            if (var3.has("type")) {
                this.type = var3.optInt("type")
            }
            if (var3.has("money")) {
                this.money = var3.optLong("money")
            }
            if (var3.has("content")) {
                this.content = var3.optString("content")
            }
            if (var3.has("sort")) {
                this.sort = var3.optInt("sort")
            }
            if (var3.has("count")) {
                this.count = var3.optInt("count")
            }
            if (var3.has("state")) {
                this.state = var3.optInt("state")
            }
            if (var3.has("createtime")) {
                this.createtime = var3.optString("createtime")
            }
        } catch (var4: org.json.JSONException) {

        }

    }


    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.redpacketId)
        dest.writeLong(this.tomemberid)
        dest.writeLong(this.fromuserid)
        dest.writeInt(this.type)
        dest.writeLong(this.money)
        dest.writeString(this.content)
        dest.writeInt(this.sort)
        dest.writeInt(this.count)
        dest.writeInt(this.state)
        dest.writeString(this.createtime)
    }

    protected constructor(`in`: Parcel) {
        this.redpacketId = `in`.readString()
        this.tomemberid = `in`.readLong()
        this.fromuserid = `in`.readLong()
        this.type = `in`.readInt()
        this.money = `in`.readLong()
        this.content = `in`.readString()
        this.sort = `in`.readInt()
        this.count = `in`.readInt()
        this.state = `in`.readInt()
        this.createtime = `in`.readString()
    }

    companion object {
        val CONTENT_PREFIX = "[红包]"


        fun obtain(redpacketId: String, tomemberid: Long, fromuserid: Long, type: Int, money: Long, content: String, sort: Int, count: Int, state: Int, createtime: String): GGRedPacketMessage {
            return GGRedPacketMessage(redpacketId, tomemberid, fromuserid, type, money, content, sort, count, state, createtime)
        }

        @JvmField
        val CREATOR: Parcelable.Creator<GGRedPacketMessage> = object : Parcelable.Creator<GGRedPacketMessage> {
            override fun createFromParcel(source: Parcel): GGRedPacketMessage {
                return GGRedPacketMessage(source)
            }

            override fun newArray(size: Int): Array<GGRedPacketMessage?> {
                return arrayOfNulls(size)
            }
        }
    }
}
