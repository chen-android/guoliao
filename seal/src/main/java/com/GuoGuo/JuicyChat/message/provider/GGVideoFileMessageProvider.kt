package com.GuoGuo.JuicyChat.message.provider

import android.content.Context
import android.content.Intent
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
import io.rong.imkit.model.ProviderTag
import io.rong.imkit.model.UIMessage
import io.rong.imkit.widget.provider.IContainerItemProvider
import io.rong.imlib.model.Message
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by chenshuai12619 on 2018-01-06.
 */
@ProviderTag(messageContent = GGVideoFileMessage::class, showReadState = true)
class GGVideoFileMessageProvider : IContainerItemProvider.MessageProvider<GGVideoFileMessage>() {

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
        val media = MediaMetadataRetriever()
        try {
            media.setDataSource(p2!!.url, hashMapOf())
            val duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
            val bitmap = media.frameAtTime
            holder.img!!.setImageBitmap(bitmap)
            holder.duration!!.text = SimpleDateFormat("mm:ss", Locale.CHINA).format(duration).toString()
        } catch (exc: Exception) {

        } finally {
            media.release()
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
}