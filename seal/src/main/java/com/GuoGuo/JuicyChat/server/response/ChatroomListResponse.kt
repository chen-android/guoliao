package com.GuoGuo.JuicyChat.server.response

/**
 * Created by chenshuai12619 on 2017-08-26.
 */

class ChatroomListResponse {
    var code: Int = 0
    var message: String? = null
    var data: List<ChatroomData>? = null

    class ChatroomData {
        var id: String? = null
        var name: String? = null
        var limitmoney: Long = 0
        var createtime: String? = null
        var headico: String? = null
        var limitipstart: String? = null
        var limitipend: String? = null
    }
}
