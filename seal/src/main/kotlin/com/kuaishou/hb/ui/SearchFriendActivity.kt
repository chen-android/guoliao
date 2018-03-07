package com.kuaishou.hb.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.kuaishou.hb.R
import com.kuaishou.hb.server.network.async.AsyncTaskManager
import com.kuaishou.hb.server.network.http.HttpException
import com.kuaishou.hb.server.response.GetUserInfoByPhoneResponse
import com.kuaishou.hb.server.utils.NToast
import com.kuaishou.hb.server.widget.LoadDialog
import com.kuaishou.hb.ui.activity.BaseActivity
import com.kuaishou.hb.ui.activity.MyAccountActivity
import com.kuaishou.hb.ui.adapter.BaseAdapter
import com.kuaishou.hb.utils.SharedPreferencesContext
import com.kuaishou.hb.utils.transformation.RoundedTransformation
import com.squareup.picasso.Picasso

class SearchFriendActivity : BaseActivity() {
    private var mEtSearch: EditText? = null
    private var mPhone: String? = null
    private var mSearchLv: ListView? = null
    private var mAdapter: MyAdapter? = null
    private var selectedFriendId: String? = null
    private var addFriendDialog: AlertDialog? = null
    private var emptyTv: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setTitle(R.string.search_friend)

        mEtSearch = findViewById(R.id.search_edit) as EditText
        mEtSearch!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mPhone = v.text.toString()
                if (!TextUtils.isEmpty(mPhone)) {
                    hintKbTwo()
                    LoadDialog.show(mContext)
                    request(SEARCH_PHONE, true)
                } else {
                    NToast.shortToast(mContext, "请输入搜索内容")
                }
                return@OnEditorActionListener true
            }
            false
        })
        mSearchLv = findViewById(R.id.search_lv) as ListView
        emptyTv = findViewById(R.id.empty_view) as TextView
        mAdapter = MyAdapter(this)
        mSearchLv!!.adapter = mAdapter
        mSearchLv!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (mAdapter!!.getItem(position)!!.memberId == SharedPreferencesContext.getInstance().userId) {
                startActivity(Intent(this@SearchFriendActivity, MyAccountActivity::class.java))
                return@OnItemClickListener
            }
            val intent = Intent(this@SearchFriendActivity, AddFriendConfirmActivity::class.java)
            intent.putExtra("friendId", mAdapter!!.getItem(position).memberId)
            startActivity(intent)
        }
    }

    @Throws(HttpException::class)
    override fun doInBackground(requestCode: Int, id: String?): Any? {
        when (requestCode) {
            SEARCH_PHONE -> return action.getUserInfoFromPhone(mPhone)
        }
        return super.doInBackground(requestCode, id)
    }

    override fun onSuccess(requestCode: Int, result: Any?) {
        if (result != null) {
            when (requestCode) {
                SEARCH_PHONE -> {
                    val userInfoByPhoneResponse = result as GetUserInfoByPhoneResponse?
                    if (userInfoByPhoneResponse!!.code == 200) {
                        LoadDialog.dismiss(mContext)
                        if (userInfoByPhoneResponse.data != null) {
                            mAdapter!!.clear()
                            mAdapter!!.addCollection(userInfoByPhoneResponse.data)
                            mAdapter!!.notifyDataSetChanged()
                            emptyTv!!.visibility = View.GONE
                        } else {
                            emptyTv!!.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(requestCode: Int, state: Int, result: Any) {
        when (requestCode) {
            SEARCH_PHONE -> {
                if (state == AsyncTaskManager.HTTP_ERROR_CODE || state == AsyncTaskManager.HTTP_NULL_CODE) {
                    super.onFailure(requestCode, state, result)
                } else {
                    NToast.shortToast(mContext, "用户不存在")
                }
                LoadDialog.dismiss(mContext)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hintKbTwo()
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun hintKbTwo() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive && currentFocus != null) {
            if (currentFocus!!.windowToken != null) {
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    private inner class MyAdapter(context: Context) : BaseAdapter<GetUserInfoByPhoneResponse.FriendData>(context) {

        override fun newView(context: Context, position: Int, group: ViewGroup): View {
            return LayoutInflater.from(context).inflate(R.layout.list_item_friend, null)
        }

        override fun bindView(v: View, position: Int, data: GetUserInfoByPhoneResponse.FriendData) {
            val iv = v.findViewById(R.id.list_item_friend_iv) as ImageView
            val tv = v.findViewById(R.id.list_item_friend_name_tv) as TextView
            Picasso.with(v.context).load(data.headIco).placeholder(R.drawable.rc_default_portrait)
                    .transform(RoundedTransformation(5, 0)).fit().centerCrop().into(iv)
            tv.text = data.nickName
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }

    companion object {

        private val CLICK_CONVERSATION_USER_PORTRAIT = 1
        private val SEARCH_PHONE = 10
    }
}
