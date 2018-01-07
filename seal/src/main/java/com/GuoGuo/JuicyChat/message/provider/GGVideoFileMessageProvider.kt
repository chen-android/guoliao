package com.GuoGuo.JuicyChat.message.provider

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.text.Spannable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.GuoGuo.JuicyChat.R
import com.GuoGuo.JuicyChat.model.GGVideoFileMessage
import com.GuoGuo.JuicyChat.utils.CommonUtils
import io.rong.imkit.model.ProviderTag
import io.rong.imkit.model.UIMessage
import io.rong.imkit.utilities.RongUtils
import io.rong.imkit.widget.provider.IContainerItemProvider
import io.rong.imlib.model.Message
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by chenshuai12619 on 2018-01-06.
 */
@ProviderTag(messageContent = GGVideoFileMessage::class, showReadState = true)
class GGVideoFileMessageProvider : IContainerItemProvider.MessageProvider<GGVideoFileMessage>() {

    companion object {
        val threadPool: ExecutorService = Executors.newFixedThreadPool(4)
    }

    var context: Context? = null
    override fun newView(p0: Context?, p1: ViewGroup?): View {
        this.context = p0
        val view = LayoutInflater.from(p0).inflate(R.layout.item_video_file_message, null)
        val holder = ViewHolder()
        holder.img = view.findViewById(R.id.item_video_msg_img) as ImageView
        holder.duration = view.findViewById(R.id.item_video_msg_duration_tv) as TextView
        view.tag = holder
        return view
    }

    override fun bindView(p0: View?, p1: Int, p2: GGVideoFileMessage?, p3: UIMessage?) {
        val holder = p0!!.tag as ViewHolder
        if (p3!!.messageDirection == Message.MessageDirection.SEND) {
            p0.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_no_right)
        } else {
            p0.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_no_left)
        }
        val bm = CommonUtils.imgCache[p2!!.url]

        if (bm != null) {
            holder.img!!.setImageBitmap(bm)
            setLayoutParam(bm, holder.img!!)
        } else {
            threadPool.execute({
                val media = MediaMetadataRetriever()
                try {
                    media.setDataSource(p2!!.url, hashMapOf())
                    val duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
                    val bitmap = media.frameAtTime
                    CommonUtils.imgCache.set(p2!!.url, bitmap)
                    (context as Activity).runOnUiThread({
                        holder.img!!.setImageBitmap(bitmap)
                        setLayoutParam(bitmap, holder.img!!)
                        holder.duration!!.text = SimpleDateFormat("mm:ss", Locale.CHINA).format(duration).toString()
                    })
                } catch (exc: Exception) {

                } finally {
                    media.release()
                }
            })
        }
    }

    override fun onItemClick(p0: View?, p1: Int, p2: GGVideoFileMessage?, p3: UIMessage?) {
        val action = "io.rong.imkit.intent.action.webview"
        var intent = Intent(action)
        intent.`package` = p0!!.context.packageName
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        if (p2!!.url.isNullOrBlank().not()) {
            intent.putExtra("url", p2.url)
        }

        p0.context.startActivity(intent)
    }

    override fun getContentSummary(p0: GGVideoFileMessage?): Spannable {
        return SpannableString(GGVideoFileMessage.Companion.CONTENT_PREFIX)
    }

    inner class ViewHolder {
        var img: ImageView? = null
        var duration: TextView? = null
    }

    private fun setLayoutParam(bitmap: Bitmap, img: ImageView) {
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        val minSize = 100
        val minShortSideSize = RongUtils.dip2px(120f)
        if (width > minShortSideSize && height > minShortSideSize) {
            val params = img.layoutParams
            params.height = RongUtils.dip2px(150f)
            params.width = RongUtils.dip2px(150f)
            img.layoutParams = params
        } else {
            val scale = width / height
            var finalWidth: Int
            var finalHeight: Int
            if (scale > 1.0f) {
                finalHeight = (minShortSideSize / scale).toInt()
                if (finalHeight < minSize) {
                    finalHeight = minSize
                }

                finalWidth = minShortSideSize
            } else {
                finalHeight = minShortSideSize
                finalWidth = (minShortSideSize * scale).toInt()
                if (finalWidth < minSize) {
                    finalWidth = minSize
                }
            }

            val params = img.layoutParams
            params.height = finalHeight
            params.width = finalWidth
            img.layoutParams = params
        }

    }
}