package com.GuoGuo.JuicyChat.ui.activity

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView

import com.GuoGuo.JuicyChat.R
import com.GuoGuo.JuicyChat.server.network.http.HttpException
import com.GuoGuo.JuicyChat.server.pinyin.SideBar
import com.GuoGuo.JuicyChat.server.response.GetGroupMemberResponse

/**
 * 群主解除冻结
 * Created by cs on 2017/5/29.
 */

class UnlockMoneyActivity : BaseActivity() {
    private var noTv: TextView? = null
    private var lv: ListView? = null
    private var sb: SideBar? = null
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock_money)
        initView()
        groupId = intent.getStringExtra("groupId")
    }

    private fun initView() {
        noTv = findViewById(R.id.unlocak_money_show_no_friend_tv) as TextView
        lv = findViewById(R.id.unlocak_money_friendlistview_iv) as ListView
        sb = findViewById(R.id.unlocak_money_sidrbar_sb) as SideBar
    }

    @Throws(HttpException::class)
    override fun doInBackground(requestCode: Int, id: String): Any? {
        when (requestCode) {
            REQUEST_LIST -> return action.getLockedGroupMembers(groupId)
        }
        return super.doInBackground(requestCode, id)
    }

    override fun onSuccess(requestCode: Int, result: Any) {
        when (requestCode) {
            REQUEST_LIST -> {
                val groupMemberResponse = result as GetGroupMemberResponse

                if (groupMemberResponse.code == 200) {

                }
            }
        }
    }

    companion object {
        private val REQUEST_LIST = 678
        private val UNLOCK = 535
    }
}
