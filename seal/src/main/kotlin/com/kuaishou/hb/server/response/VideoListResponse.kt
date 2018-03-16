package com.kuaishou.hb.server.response

import com.kuaishou.hb.server.BaseSelectLinstener

/**
 * Created by chenshuai12619 on 2018-01-04.
 */
class VideoListResponse {
    var code: Int = 0
    var message: String? = null
    var data: MutableList<VideoData>? = null

    class VideoData : BaseSelectLinstener() {
        var id: String? = null
        var userid: String? = null
        var url: String? = null
        var picurl: String? = null
        var createtime: String? = null
        var name: String? = null
        var duration: Long = 0
        var size: Double = 0.0

        //local
        var progress: Int = 0
        var key: String = ""
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as VideoData

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id?.hashCode() ?: 0
        }


    }
}