package com.GuoGuo.JuicyChat.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.GuoGuo.JuicyChat.App
import com.GuoGuo.JuicyChat.R
import com.GuoGuo.JuicyChat.db.Friend
import com.GuoGuo.JuicyChat.server.response.GetFriendListResponse
import com.GuoGuo.JuicyChat.server.widget.LoadDialog
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView
import io.rong.imageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_service_assistant.*

class ServiceAssistantActivity : BaseActivity() {

    private var adapter: ServiceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_assistant)
        setTitle(R.string.public_service)
        service_assist_lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, UserDetailActivity::class.java)
            intent.putExtra("type", 2)
            intent.putExtra("friend", this.adapter!!.list!![position])
            startActivity(intent)
        }
        LoadDialog.show(this)
        request(REQUEST_LIST)
    }

    override fun doInBackground(requestCode: Int, id: String?): Any {
        when (requestCode) {
            REQUEST_LIST -> {
                return action.serviceList
            }
            else -> {
            }
        }
        return super.doInBackground(requestCode, id)
    }

    override fun onSuccess(requestCode: Int, result: Any?) {
        when (requestCode) {
            REQUEST_LIST -> {
                LoadDialog.dismiss(this)
                val sList = result as GetFriendListResponse
                if (sList.code == 200) {
                    this.adapter = ServiceAdapter(this, sList.data)
                    service_assist_lv.adapter = this.adapter
                }
            }
            else -> {
            }
        }
        super.onSuccess(requestCode, result)
    }

    override fun onFailure(requestCode: Int, state: Int, result: Any?) {
        LoadDialog.dismiss(this)
        super.onFailure(requestCode, state, result)
    }

    internal inner class ServiceAdapter(private val context: Context, var list: List<Friend>?) : BaseAdapter() {

        /**
         * 传入新的数据 刷新UI的方法
         */
        fun updateListView(list: MutableList<Friend>?) {
            this.list = list
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return if (list != null) list!!.size else 0
        }

        override fun getItem(position: Int): Any? {
            if (list == null)
                return null

            return if (position >= list!!.size) null else list!![position]

        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val viewHolder: ViewHolder
            val mContent = list!![position]
            if (convertView == null) {
                viewHolder = ViewHolder()
                convertView = LayoutInflater.from(context).inflate(R.layout.group_item_new, parent, false)
                viewHolder.tvTitle = convertView!!.findViewById(R.id.groupname) as TextView
                viewHolder.mImageView = convertView.findViewById(R.id.groupuri) as SelectableRoundedImageView
                viewHolder.groupId = convertView.findViewById(R.id.group_id) as TextView
                convertView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ViewHolder
            }
            viewHolder.tvTitle!!.text = mContent.nickname
            val portraitUri = mContent.headico
            ImageLoader.getInstance().displayImage(portraitUri, viewHolder.mImageView!!, App.getOptions())
            return convertView
        }


        internal inner class ViewHolder {
            /**
             * 昵称
             */
            var tvTitle: TextView? = null
            /**
             * 头像
             */
            var mImageView: SelectableRoundedImageView? = null
            /**
             * userId
             */
            var groupId: TextView? = null
        }
    }

    companion object {
        private val REQUEST_LIST = 1
    }
}
