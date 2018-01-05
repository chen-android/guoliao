package com.GuoGuo.JuicyChat.ui.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.GuoGuo.JuicyChat.R
import com.GuoGuo.JuicyChat.server.UploadService
import com.GuoGuo.JuicyChat.server.response.VideoListResponse
import com.GuoGuo.JuicyChat.utils.CommonUtils
import com.GuoGuo.JuicyChat.utils.StrongHandler
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_video_send.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class VideoSendActivity : BaseActivity(), StrongHandler.HandleMessageListener {

    companion object {
        val REQUEST_LIST = 1
        val REQUEST_LIMIT = 2
        val INTENT_VIDEO = 100
    }

    private var adapter: VideoAdapter? = null

    private var handler: StrongHandler? = null
    private var threadService = Executors.newFixedThreadPool(4)
    private var binder: UploadService.MyBinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_send)
        setTitle("视频文件")
        initView()
        request(REQUEST_LIST)
    }

    private fun initView() {
        videoSendSrl.refreshHeader = ClassicsHeader(this)
        videoSendSrl.refreshFooter = ClassicsFooter(this)
        videoSendSrl.isEnableLoadmore = false
        videoSendSrl.isEnableRefresh = true
        videoSendSrl.setOnRefreshListener { request(REQUEST_LIST) }
        videoSendRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        videoSendRv.itemAnimator = DefaultItemAnimator()
        handler = StrongHandler(this, this)
        val rightText = getmHeadRightText()
        rightText.visibility = View.VISIBLE
        rightText.text = "选择视频"
        rightText.setOnClickListener({
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            this.startActivityForResult(intent, INTENT_VIDEO)
        })
        this.adapter = VideoAdapter(this, mutableListOf())
        videoSendRv.adapter = this.adapter
        if (binder == null) {
            bindService(Intent(this, UploadService::class.java), object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName?) {

                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    binder = service as UploadService.MyBinder
                    binder!!.setProgressListener(object : UploadService.OnProgressUpdateListener {
                        override fun update(key: String, percent: Int) {
                            if (adapter != null) {
                                val videoList = adapter!!.videoList
                                if (videoList != null && videoList.isNotEmpty()) {
                                    for (index in videoList.indices) {
                                        val videoData = videoList[index]
                                        if (videoData.key == key) {
                                            videoData.progress = percent
                                            adapter!!.notifyItemChanged(index)
                                            break
                                        }
                                    }

                                }
                            }
                        }
                    })
                }

            }, Context.BIND_AUTO_CREATE)
        }
    }

    override fun strongHandleMessage(msg: Message) {
        val holder = msg.obj as ImgHolder
        holder.setImg()
    }

    override fun doInBackground(requestCode: Int, id: String?): Any {
        when (requestCode) {
            REQUEST_LIST -> {
                return action.videoList
            }
            else -> {
            }
        }
        return super.doInBackground(requestCode, id)
    }

    override fun onSuccess(requestCode: Int, result: Any?) {
        when (requestCode) {
            REQUEST_LIST -> {
                videoSendSrl.finishRefresh()
                val videoResp = result as VideoListResponse
                if (videoResp.code == 200) {
                    val videoList = videoResp.data
                    if (binder != null) {
                        videoList!!.addAll(0, binder!!.getVideoList())
                    }
                    adapter!!.videoList = videoList
                    adapter!!.notifyDataSetChanged()
                }
            }
            else -> {
            }
        }
        super.onSuccess(requestCode, result)
    }

    private inner class VideoAdapter(context: Context, videoList: MutableList<VideoListResponse.VideoData>) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
        var context: Context? = null
        var videoList: MutableList<VideoListResponse.VideoData>? = null

        init {
            this.context = context
            this.videoList = videoList
        }

        override fun getItemCount(): Int {
            return this.videoList?.size ?: 0
        }

        fun addItem(video: VideoListResponse.VideoData) {
            this.videoList!!.add(0, video)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val item = videoList!![position]
            holder!!.img!!.setBackgroundResource(R.drawable.icon_video_default)
            if (item.url.isNullOrBlank().not()) {
                threadService.execute({
                    val media = MediaMetadataRetriever()
                    try {
                        media.setDataSource(item.url, hashMapOf())
                        val obtain = Message.obtain()
                        obtain.obj = ImgHolder(holder?.img, media.frameAtTime)
                        handler!!.sendMessage(obtain)
                    } catch (exc: Exception) {

                    } finally {
                        media.release()
                    }
                })
            } else if (item.picurl.isNullOrBlank().not()) {
                threadService.execute({
                    val media = MediaMetadataRetriever()
                    try {
                        media.setDataSource(item.picurl)
                        val obtain = Message.obtain()
                        obtain.obj = ImgHolder(holder?.img, media.frameAtTime)
                        handler!!.sendMessage(obtain)
                    } catch (exc: Exception) {

                    } finally {
                        media.release()
                    }
                })
            }
            holder!!.name!!.text = item.name
            holder.duration!!.text = "时长  " + SimpleDateFormat("mm:ss", Locale.CHINA).format(item.duration * 1000).toString()
            holder!!.tip!!.setTextColor(resources.getColor(R.color.rc_text_color_primary))

            if (item.progress <= 0) {
                holder.pbRl!!.visibility = View.GONE
                if (item.progress == -2) {
                    holder!!.tip!!.text = "上传出错"
                    holder!!.tip!!.setTextColor(resources.getColor(R.color.red))
                }
            } else {
                holder.pbRl!!.visibility = View.VISIBLE
                holder!!.pb!!.progress = item.progress
                if (item.progress == 100) {
                    holder!!.tip!!.text = "完成"
                }
            }
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int, payloads: List<Any>?) {
            if (payloads != null && payloads.isNotEmpty()) {
                val videoData = this.videoList!![position]
                val progress = videoData.progress
                holder!!.pb!!.progress = progress
                holder!!.tip!!.setTextColor(resources.getColor(R.color.rc_text_color_primary))
                if (progress == 100) {
                    holder!!.tip!!.text = "完成"
                }
                if (progress <= 0) {
                    holder.pbRl!!.visibility = View.GONE
                    if (progress == -2) {
                        holder!!.tip!!.text = "上传出错"
                        holder!!.tip!!.setTextColor(resources.getColor(R.color.red))
                    }
                } else {
                    holder.pbRl!!.visibility = View.VISIBLE
                }

            } else {
                onBindViewHolder(holder, position)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            return ViewHolder(View.inflate(this.context, R.layout.item_video_upload, null))
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var img: ImageView? = null
            var name: TextView? = null
            var duration: TextView? = null
            var pbRl: RelativeLayout? = null
            var pb: ProgressBar? = null
            var tip: TextView? = null

            init {
                img = itemView.findViewById(R.id.upload_video_iv) as ImageView
                name = itemView.findViewById(R.id.upload_video_name_tv) as TextView
                duration = itemView.findViewById(R.id.upload_video_duration_tv) as TextView
                pb = itemView.findViewById(R.id.upload_video_pb) as ProgressBar
                pbRl = itemView.findViewById(R.id.upload_video_progress_rl) as RelativeLayout
                tip = itemView.findViewById(R.id.upload_tip) as TextView
            }
        }
    }

    inner class ImgHolder(img: ImageView?, bitmap: Bitmap?) {
        var img: ImageView? = null
        var bitmap: Bitmap? = null

        init {
            this.img = img
            this.bitmap = bitmap
        }

        fun setImg() {
            img?.setImageBitmap(this.bitmap)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            INTENT_VIDEO -> {
                if (resultCode == Activity.RESULT_OK) {
                    var uri = data!!.data

                    val file = File(CommonUtils.getRealFilePath(this, uri))
                    if (file.exists()) {
                        if (file.length() / 1024 / 1024 > 100) {
                            ToastUtils.showShort("视频不能超过100M")
                            return
                        }
                        val video = VideoListResponse.VideoData()
                        video.picurl = file.absolutePath
                        video.progress = 1
                        video.name = file.name
                        video.key = System.currentTimeMillis().toString() + file.name
                        this.adapter!!.addItem(video)
                        this.adapter!!.notifyDataSetChanged()
                        var intent = Intent(this, UploadService::class.java)
                        intent.putExtra("path", file.absolutePath)
                        intent.putExtra("fileKey", video.key)
                        startService(intent)
                    }
                }
            }
            else -> {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
