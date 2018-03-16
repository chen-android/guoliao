package com.kuaishou.hb.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kuaishou.hb.App
import com.kuaishou.hb.GGConst
import com.kuaishou.hb.R
import com.kuaishou.hb.SealUserInfoManager
import com.kuaishou.hb.db.Groups
import com.kuaishou.hb.server.broadcast.BroadcastManager
import com.kuaishou.hb.server.pinyin.PinyinGroupComparator
import com.kuaishou.hb.server.widget.SelectableRoundedImageView
import com.kuaishou.hb.ui.activity.BaseActivity
import io.rong.imageloader.core.ImageLoader
import io.rong.imkit.RongIM
import java.util.*

/**
 * Created by AMing on 16/3/8.
 * Company RongCloud
 */
class GroupListActivity : BaseActivity() {

    private var mGroupListView: ListView? = null
    private var adapter: GroupAdapter? = null
    private var mNoGroups: TextView? = null
    private var mSearch: EditText? = null
    private var mList: MutableList<Groups>? = null
    private var mTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fr_group_list)
        setTitle(R.string.my_groups)
        mGroupListView = findViewById(R.id.group_listview) as ListView
        mNoGroups = findViewById(R.id.show_no_group) as TextView
        mSearch = findViewById(R.id.group_search) as EditText
        mTextView = findViewById(R.id.foot_group_size) as TextView
        initData()
        BroadcastManager.getInstance(mContext).addAction(GGConst.GROUP_LIST_UPDATE, object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                initData()
            }
        })
    }

    private fun initData() {
        SealUserInfoManager.getInstance().getGroups(object : SealUserInfoManager.ResultCallback<List<Groups>>() {
	        override fun onSuccess(groupsList: List<Groups>?) {
		        groupsList?.let {
			        mList = groupsList as MutableList
			        Collections.sort(mList!!, PinyinGroupComparator())
			        if (mList!!.size > 0) {
				        adapter = GroupAdapter(mContext, mList)
				        mGroupListView!!.adapter = adapter
				        mNoGroups!!.visibility = View.GONE
				        mTextView!!.visibility = View.VISIBLE
				        mTextView!!.text = getString(R.string.ac_group_list_group_number, mList!!.size)
				        mGroupListView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
					        val bean = adapter!!.getItem(position) as Groups?
					        RongIM.getInstance().startGroupChat(this@GroupListActivity, bean!!.groupid, bean.groupname)
				        }
				        mSearch!!.addTextChangedListener(object : TextWatcher {
					        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

					        }

					        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
						        filterData(s.toString())
					        }

					        override fun afterTextChanged(s: Editable) {}
				        })
			        } else {
				        mNoGroups!!.visibility = View.VISIBLE
			        }
                }
            }

            override fun onError(errString: String) {

            }
        })
    }

    private fun filterData(s: String) {
        var filterDataList: MutableList<Groups>? = mutableListOf()
        if (TextUtils.isEmpty(s)) {
            filterDataList = mList
        } else {
            for (groups in mList!!) {
                if (groups.groupname.contains(s)) {
                    filterDataList!!.add(groups)
                }
            }
        }
        adapter!!.updateListView(filterDataList)
        mTextView!!.text = getString(R.string.ac_group_list_group_number, filterDataList!!.size)
    }


    internal inner class GroupAdapter(private val context: Context, private var list: List<Groups>?) : BaseAdapter() {

        /**
         * 传入新的数据 刷新UI的方法
         */
        fun updateListView(list: MutableList<Groups>?) {
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
            viewHolder.tvTitle!!.text = mContent.groupname
            val portraitUri = mContent.grouphead
            ImageLoader.getInstance().displayImage(portraitUri, viewHolder.mImageView!!, App.getOptions())
            if (context.getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("isDebug", false)) {
                viewHolder.groupId!!.visibility = View.VISIBLE
                viewHolder.groupId!!.text = mContent.groupid
            }
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

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        BroadcastManager.getInstance(mContext).destroy(GGConst.GROUP_LIST_UPDATE)
    }


}
