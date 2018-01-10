package com.GuoGuo.JuicyChat.server

import android.app.Service
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Binder
import android.os.IBinder
import com.GuoGuo.JuicyChat.GGConst
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager
import com.GuoGuo.JuicyChat.server.network.async.OnDataListener
import com.GuoGuo.JuicyChat.server.response.QiNiuTokenResponse
import com.GuoGuo.JuicyChat.server.response.VideoListResponse
import com.GuoGuo.JuicyChat.server.response.VideoUploadResponse
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.qiniu.android.storage.*
import org.json.JSONException
import java.io.File

/**
 * Created by chenshuai12619 on 2018-01-04.
 */
class UploadVideoService : Service(), OnDataListener {

    private val videoMap: MutableMap<String, VideoListResponse.VideoData> = mutableMapOf()
    private val videoCancelMap: MutableMap<String, Boolean> = mutableMapOf()
    private var uploadManager: UploadManager? = null
    private var reqMap: MutableMap<Int, PK> = mutableMapOf()
    private val myBinder: MyBinder = MyBinder()
    private var action: SealAction? = null
    private var progressListener: OnProgressUpdateListener? = null

    companion object {
        var REQUEST_TOKEN = 10000
        var REQUEST_UPLOAD_VIDEO = 1
    }

    inner class MyBinder : Binder() {
        fun getVideoList(): MutableCollection<VideoListResponse.VideoData> {
            return videoMap.values
        }

        fun setProgressListener(listener: OnProgressUpdateListener) {
            progressListener = listener
        }

        fun cancelUpload(key: String) {
            videoCancelMap[key] = true
            videoMap.remove(key)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    override fun onCreate() {
        super.onCreate()
        var config = Configuration.Builder()
                .chunkSize(512 * 1024)
                .putThreshhold(1024 * 1024)
                .responseTimeout(20)
                .build()
        this.uploadManager = UploadManager(config)
        action = SealAction(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val path = intent!!.getStringExtra("path")
        val key = intent!!.getStringExtra("fileKey")
        val i = REQUEST_TOKEN++
        this.reqMap.put(i, PK(path, key))
        requestToken(i)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun requestToken(r: Int): Unit {
        AsyncTaskManager.getInstance(this).request(r, this)
    }

    override fun doInBackground(requestCode: Int, parameter: String?): Any? {
        if (requestCode < 10000) {
            val v = this.videoMap[parameter]
            try {
                val media = MediaMetadataRetriever()
                media.setDataSource(v!!.picurl)
                v.duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong() / 1000
            } catch (exc: Exception) {

            }
            return action!!.uploadVideo(v!!.url, v.name, v.duration, 0)
        } else {
            return action!!.qiNiuToken
        }
    }

    override fun onSuccess(requestCode: Int, result: Any?) {
        if (requestCode >= 10000) {
            val tokenResp = result as QiNiuTokenResponse
            if (tokenResp.code == 200) {
                uploadVideo(tokenResp.data.qiniutoken, reqMap[requestCode]!!.path, reqMap[requestCode]!!.key)
            } else {
                ToastUtils.showShort("上传错误")
                this.progressListener?.update(reqMap[requestCode]!!.key, -2)
            }
        } else {
            val resp = result as VideoUploadResponse
            var fileKey = reqMap[requestCode]!!.key
            if (resp.code == 200) {
                this.progressListener?.update(fileKey!!, 100, resp.data?.id)
                this.videoMap.remove(fileKey)
            } else {
                ToastUtils.showShort("上传错误")
                this.progressListener?.update(reqMap[requestCode]!!.key, -2)
                this.videoMap.remove(fileKey)
            }
        }
    }

    override fun onFailure(requestCode: Int, state: Int, result: Any?) {
    }

    private fun uploadVideo(token: String, path: String, fileKey: String) {
        val file = File(path)
        val video = VideoListResponse.VideoData()

        video.picurl = path
        video.progress = 1
        video.name = File(path).name
        video.key = fileKey
        this.videoMap.put(fileKey, video)
        this.videoCancelMap.put(fileKey, false)
        this.uploadManager!!.put(file, fileKey, token, { s, responseInfo, jsonObject ->
            /*成功的回调*/
            if (responseInfo.isOK) {
                try {
                    var key = jsonObject["key"]
                    LogUtils.d("qiniu_key", key)
                    var videoUrl = GGConst.QINIU_URL + key
                    video.url = videoUrl
                    val req = REQUEST_UPLOAD_VIDEO++
                    reqMap.put(req, PK(path, fileKey))
                    AsyncTaskManager.getInstance(this).request(fileKey, req, this)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, UploadOptions(null, null, false, UpProgressHandler { key: String?, percent: Double ->
            LogUtils.d("upload_progress", key + "###" + percent)
            var p = percent * 100
            videoMap[key]?.progress = if (p.toInt() < 99) p.toInt() else 99
            if (this.progressListener != null) {
                this.progressListener!!.update(key!!, p.toInt())
            }
        }, UpCancellationSignal {
            this.videoCancelMap[fileKey]!!
        }))
    }

    data class PK(var path: String, var key: String)

    interface OnProgressUpdateListener {
        fun update(key: String, percent: Int, id: String? = null)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}