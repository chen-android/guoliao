package com.GuoGuo.JuicyChat.ui.activity

import android.os.Bundle
import com.GuoGuo.JuicyChat.R
import com.GuoGuo.JuicyChat.server.response.FriendInvitationResponse
import com.GuoGuo.JuicyChat.server.utils.NToast
import com.GuoGuo.JuicyChat.server.widget.LoadDialog
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext
import kotlinx.android.synthetic.main.activity_add_friend_confirm.*

class AddFriendConfirmActivity : BaseActivity() {
    private var selectedFriendId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend_confirm)
        selectedFriendId = intent.getStringExtra("friendId")
        init()
    }

    fun init() {
        setTitle("好友验证")
        headRightButton.text = "发送"
        headRightButton.setOnClickListener {
            request(REQUEST_ADD_FRIEND)
        }
        add_friend_confirm_et.setText(SharedPreferencesContext.getInstance().name + "想加你为好友")
    }

    override fun doInBackground(requestCode: Int, id: String?): Any {
        when (requestCode) {
            REQUEST_ADD_FRIEND -> return action.sendFriendInvitation(selectedFriendId, add_friend_confirm_et.text.toString())
            else -> {
            }
        }
        return super.doInBackground(requestCode, id)
    }

    override fun onSuccess(requestCode: Int, result: Any?) {
        when (requestCode) {
            REQUEST_ADD_FRIEND -> {
                val fres = result as FriendInvitationResponse?
                if (fres!!.code == 200) {
                    NToast.shortToast(mContext, getString(R.string.request_success))
                    LoadDialog.dismiss(mContext)
                    finish()
                } else {
                    NToast.shortToast(mContext, fres.message)
                    LoadDialog.dismiss(mContext)
                }
            }
            else -> {
            }
        }
        super.onSuccess(requestCode, result)
    }

    override fun onFailure(requestCode: Int, state: Int, result: Any?) {
        when (requestCode) {
            REQUEST_ADD_FRIEND -> {
                NToast.shortToast(mContext, "你们已经是好友")
                LoadDialog.dismiss(mContext)
            }
            else -> {
            }
        }
        super.onFailure(requestCode, state, result)
    }

    companion object {
        private val REQUEST_ADD_FRIEND = 11
    }
}
