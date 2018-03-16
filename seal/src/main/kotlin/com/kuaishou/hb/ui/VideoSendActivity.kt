package com.kuaishou.hb.ui

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.blankj.utilcode.util.ToastUtils
import com.kuaishou.hb.R
import com.kuaishou.hb.model.GGVideoFileMessage
import com.kuaishou.hb.server.UploadVideoService
import com.kuaishou.hb.server.response.BaseResponse
import com.kuaishou.hb.server.response.VideoLimitResponse
import com.kuaishou.hb.server.response.VideoListResponse
import com.kuaishou.hb.server.widget.LoadDialog
import com.kuaishou.hb.ui.activity.BaseActivity
import com.kuaishou.hb.utils.CommonUtils
import com.kuaishou.hb.utils.StrongHandler
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.squareup.picasso.Picasso
import io.rong.imkit.RongIM
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_video_send.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class VideoSendActivity : BaseActivity(), StrongHandler.HandleMessageListener {

	companion object {
		val REQUEST_LIST = 1
		val REQUEST_LIMIT = 2
		val REQUEST_DELETE = 3
		val INTENT_VIDEO = 100
	}

	private var targetId: String? = null
	private var conversationType: Conversation.ConversationType? = null
	private var adapter: VideoAdapter? = null

	private var handler: StrongHandler? = null
	private var threadService = Executors.newCachedThreadPool()
	private var binder: UploadVideoService.MyBinder? = null
	private var removeIndex: Int = -1
	private var maxSize: Long = 100
	private var maxCount: Int = 100
	private var conn: ServiceConnection = object : ServiceConnection {
		override fun onServiceDisconnected(name: ComponentName?) {

		}

		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			binder = service as UploadVideoService.MyBinder
			binder!!.setProgressListener(object : UploadVideoService.OnProgressUpdateListener {
				override fun update(key: String, percent: Int, id: String?, picurl: String?, url: String?) {
					if (adapter != null) {
						val videoList = adapter!!.videoList
						if (videoList != null && videoList.isNotEmpty()) {
							for (index in videoList.indices) {
								val videoData = videoList[index]
								if (videoData.key == key) {
									videoData.progress = percent
									videoData.id = id
									videoData.picurl = picurl
									videoData.url = url
									adapter!!.notifyItemChanged(index, "progress")
									break
								}
							}

						}
					}
				}
			})
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_video_send)
		setTitle("视频文件")
		this.targetId = intent.getStringExtra("targetId")
		this.conversationType = intent.getSerializableExtra("type") as Conversation.ConversationType
		initView()
		request(REQUEST_LIST)
		request(REQUEST_LIMIT)
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
			if (adapter?.videoList?.size!! >= maxSize) {
				ToastUtils.showShort("上传个数不能超过" + maxSize)
				return@setOnClickListener
			}
			val intent = Intent()
			intent.type = "video/*"
			intent.action = Intent.ACTION_GET_CONTENT
			intent.addCategory(Intent.CATEGORY_OPENABLE)
			this.startActivityForResult(intent, INTENT_VIDEO)
		})
		this.adapter = VideoAdapter(this, mutableListOf())
		videoSendRv.adapter = this.adapter
		if (binder == null) {
			bindService(Intent(this, UploadVideoService::class.java), conn, Context.BIND_AUTO_CREATE)
		}

		videoSendSelectAllTv.setOnClickListener { tv ->
			tv as CheckedTextView
			tv.isChecked = tv.isChecked.not()
			adapter!!.videoList?.let {
				it.forEach {
					it.isSelected = tv.isChecked
				}
				adapter!!.notifyDataSetChanged()
			}
		}

		videoSendDelTv.setOnClickListener {
			request(REQUEST_DELETE)
		}

		videoSendSendTv.setOnClickListener {
			adapter!!.videoList?.let {
				it.forEach {
					if (it.isSelected) {
						RongIM.getInstance().sendMessage(
								io.rong.imlib.model.Message.obtain(targetId!!, conversationType!!, GGVideoFileMessage.Companion.obtain(it.url!!, it.picurl!!, it.duration)),
								GGVideoFileMessage.Companion.CONTENT_PREFIX,
								null,
								object : IRongCallback.ISendMessageCallback {
									override fun onAttached(p0: io.rong.imlib.model.Message?) {
									}

									override fun onSuccess(p0: io.rong.imlib.model.Message?) {

									}

									override fun onError(p0: io.rong.imlib.model.Message?, p1: RongIMClient.ErrorCode?) {
									}
								})
					}
				}
				finish()
			}

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
			REQUEST_DELETE -> {
				/**单个左划删除*/
				id?.let {
					return action.deleteVideo(arrayListOf(id))
				}
				/**勾选批量删除*/
				val arrayList: ArrayList<String> = arrayListOf()
				adapter!!.videoList?.let {
					it.forEach {
						if (it.isSelected) {
							arrayList.add(it.id!!)
						}
					}
				}
				if (arrayList.isNotEmpty()) {
					return action.deleteVideo(arrayList)
				} else {
					ToastUtils.showShort("没有选择视频")
				}
			}
			REQUEST_LIMIT -> {
				return action.videoLimit
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
				} else {
					ToastUtils.showShort(result.message)
				}
			}
			REQUEST_DELETE -> {
				LoadDialog.dismiss(this)
				result as BaseResponse
				if (result.code == 200) {
					if (removeIndex != -1) {
						this.adapter!!.videoList!!.removeAt(removeIndex)
						this.adapter!!.notifyItemRemoved(removeIndex)
						this.adapter!!.notifyItemRangeRemoved(removeIndex, this.adapter!!.videoList!!.size)
						removeIndex = -1
						ToastUtils.showShort("删除成功")
					}
				} else {
					ToastUtils.showShort(result.message)
				}
			}
			REQUEST_LIMIT -> {
				result as VideoLimitResponse
				if (result.code == 200) {
					this.maxSize = result.data!!.maxSize
					this.maxCount = result.data!!.maxCount
				} else {
					ToastUtils.showShort(result.message)
				}
			}
			else -> {
			}
		}
		super.onSuccess(requestCode, result)
	}

	override fun onFailure(requestCode: Int, state: Int, result: Any?) {
		super.onFailure(requestCode, state, result)
		ToastUtils.showShort(getString(R.string.net_error_l))
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
			holder!!.img!!.setImageResource(R.drawable.icon_video_default)
			holder.logo!!.visibility = View.GONE
			if (item.url.isNullOrBlank().not() && item.progress == 1) {
				/**刚刚添加的下载的项目，去获取视频第一帧*/
				threadService.execute({
					val media = MediaMetadataRetriever()
					try {
						media.setDataSource(item.url)
						val obtain = Message.obtain()
						val bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
						obtain.obj = ImgHolder(holder.img, holder.logo, bitmap)
						handler!!.sendMessage(obtain)
					} catch (exc: Exception) {

					} finally {
						media.release()
					}

				})


			} else if (item.picurl.isNullOrBlank().not()) {
				Picasso.with(context).load(item.picurl).into(holder.img)
				holder.logo!!.visibility = View.VISIBLE
			}
			holder.name!!.text = item.name
			holder.duration!!.text = "时长  " + SimpleDateFormat("mm:ss", Locale.CHINA).format(item.duration * 1000).toString()
			holder.tip!!.setTextColor(resources.getColor(R.color.rc_text_color_primary))

			if (item.progress <= 0) {
				/**已经成功上传的视频 */
				holder.pbRl!!.visibility = View.GONE
				item.isSelectable = true
				/** 上传出错的视频*/
				if (item.progress == -2) {
					holder.tip!!.text = "上传出错"
					holder.tip!!.setTextColor(resources.getColor(R.color.red))
					item.isSelectable = false
				}
			} else {
				/**正在上传的项目*/
				holder.pbRl!!.visibility = View.VISIBLE
				holder.pb!!.progress = item.progress
				item.isSelectable = false
				if (item.progress == 100) {
					holder.tip!!.text = "完成"
					item.isSelectable = true
				}
			}
			holder.cb!!.isChecked = item.isSelected
			holder.cb!!.visibility = if (item.isSelectable) View.VISIBLE else View.GONE
			holder.del!!.setOnClickListener {
				if (item.id.isNullOrBlank() && item.progress != 100) {
					//本地正在上传，或上传错误的条目
					if (item.progress >= 0) {
						binder?.cancelUpload(item.key)
						ToastUtils.showShort("取消上传")
					} else {
						ToastUtils.showShort("删除成功")
					}
					this.videoList!!.removeAt(holder.adapterPosition)
					this.notifyItemRemoved(holder.adapterPosition)
					this.notifyItemRangeRemoved(holder.adapterPosition, this.videoList!!.size)
					this.notifyDataSetChanged()
				} else {
					//上传成功的条目
					removeIndex = holder.adapterPosition
					LoadDialog.show(context)
					request(item.id, REQUEST_DELETE)
				}
			}
			holder.root!!.setOnClickListener({
				if (item.id.isNullOrBlank() && item.progress != 100) {
					//正在上传的条目
				} else {

					item.isSelected = !item.isSelected
					this.notifyItemChanged(holder.adapterPosition)
					resetCheckStatus()
				}
			})
		}

		override fun onBindViewHolder(holder: ViewHolder?, position: Int, payloads: List<Any>?) {
			if (payloads != null && payloads.isNotEmpty()) {
				val videoData = this.videoList!![position]
				val progress = videoData.progress
				holder!!.pb!!.progress = progress
				holder!!.tip!!.setTextColor(resources.getColor(R.color.rc_text_color_primary))
				if (progress == 100) {
					holder!!.tip!!.text = "完成"
					videoData.isSelectable = true
				}
				if (progress <= 0) {
					holder.pbRl!!.visibility = View.GONE
					videoData.isSelectable = true
					if (progress == -2) {
						videoData.isSelectable = false
						holder!!.tip!!.text = "上传出错"
						holder!!.tip!!.setTextColor(resources.getColor(R.color.red))
					}
				} else {
					videoData.isSelectable = false
					holder.pbRl!!.visibility = View.VISIBLE
				}
				holder.cb!!.visibility = if (videoData.isSelectable) View.VISIBLE else View.GONE
				holder.cb!!.isChecked = videoData.isSelected
			} else {
				onBindViewHolder(holder, position)
			}
		}

		override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
			return ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_video_upload, parent, false))
		}

		inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
			var root: LinearLayout? = null
			var img: ImageView? = null
			var logo: ImageView? = null
			var name: TextView? = null
			var duration: TextView? = null
			var pbRl: RelativeLayout? = null
			var pb: ProgressBar? = null
			var cb: CheckBox? = null
			var tip: TextView? = null
			var del: Button? = null

			init {
				root = itemView.findViewById(R.id.upload_video_ll) as LinearLayout
				img = itemView.findViewById(R.id.upload_video_iv) as ImageView
				logo = itemView.findViewById(R.id.upload_video_logo_iv) as ImageView
				name = itemView.findViewById(R.id.upload_video_name_tv) as TextView
				duration = itemView.findViewById(R.id.upload_video_duration_tv) as TextView
				pb = itemView.findViewById(R.id.upload_video_pb) as ProgressBar
				pbRl = itemView.findViewById(R.id.upload_video_progress_rl) as RelativeLayout
				cb = itemView.findViewById(R.id.upload_video_cb) as CheckBox
				tip = itemView.findViewById(R.id.upload_tip) as TextView
				del = itemView.findViewById(R.id.upload_video_delete_bt) as Button
			}
		}
	}

	inner class ImgHolder(img: ImageView?, logo: ImageView?, bitmap: Bitmap?) {
		var img: ImageView? = null
		var logo: ImageView? = null
		var bitmap: Bitmap? = null

		init {
			this.img = img
			this.bitmap = bitmap
			this.logo = logo
		}

		fun setImg() {
			img?.setImageBitmap(this.bitmap)
			this.logo!!.visibility = View.VISIBLE
		}
	}

	/**
	 * 重置全选按钮的状态
	 */
	private fun resetCheckStatus() {
		var checkedAll = true
		adapter!!.videoList?.let {
			run loop@{
				it.forEach {
					if (it.isSelectable) {
						if (it.isSelected.not()) {
							checkedAll = false
							return@loop
						}
					}
				}
			}
			videoSendSelectAllTv.isChecked = checkedAll
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			INTENT_VIDEO -> {
				if (resultCode == Activity.RESULT_OK) {
					var uri = data!!.data

					val file = File(CommonUtils.getRealFilePath(this, uri))
					if (file.exists()) {
						val fileEx = file.name.substring(file.name.indexOf("", 0, false))
						if (fileEx != ".mp4") {
							ToastUtils.showShort("仅限mp4格式的视频")
							return
						}
						if (file.length() - (maxSize * 1024 * 1024) > 0) {
							ToastUtils.showShort("视频不能超过" + maxSize + "M")
							return
						}
						val video = VideoListResponse.VideoData()
						video.url = file.absolutePath
						video.progress = 1
						video.name = file.name
						video.key = "a_" + System.currentTimeMillis().toString() + fileEx

						try {
							val media = MediaMetadataRetriever()
							media.setDataSource(file.absolutePath)
							video.duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong() / 1000
						} catch (exc: Exception) {

						}
						this.adapter!!.addItem(video)
						this.adapter!!.notifyDataSetChanged()
						var intent = Intent(this, UploadVideoService::class.java)
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

	override fun onDestroy() {
		unbindService(conn)
		threadService.shutdownNow()
		super.onDestroy()
	}
}
