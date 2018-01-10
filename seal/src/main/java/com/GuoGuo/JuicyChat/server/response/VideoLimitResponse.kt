package com.GuoGuo.JuicyChat.server.response

/**
 * Created by chenshuai12619 on 2018-01-08.
 */
class VideoLimitResponse {
	var code: Int = 0
	var message: String? = null
	var data: VideoLimitData? = null

	class VideoLimitData {
		var maxSize: Long = 0
		var maxCount: Int = 0
	}
}