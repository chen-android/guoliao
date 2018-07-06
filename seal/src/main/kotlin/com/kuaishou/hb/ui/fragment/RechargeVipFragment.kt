package com.kuaishou.hb.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.kuaishou.hb.R
import com.kuaishou.hb.server.SealAction
import com.kuaishou.hb.server.network.async.AsyncTaskManager
import com.kuaishou.hb.server.network.async.OnDataListener
import com.kuaishou.hb.server.response.GetVipUserListResponse
import com.squareup.picasso.Picasso
import io.rong.imkit.RongIM
import kotlinx.android.synthetic.main.fragment_recharge_vip.view.*


/**
 *
 */
class RechargeVipFragment : Fragment() {

	private lateinit var rootView: View

	private lateinit var vipList: List<LinearLayout>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		rootView = inflater!!.inflate(R.layout.fragment_recharge_vip, container, false)
		initView()
		init()
		return rootView
	}

	private fun initView() {
		vipList = listOf(rootView.recharge_vip1_ll, rootView.recharge_vip2_ll, rootView.recharge_vip3_ll, rootView.recharge_vip4_ll)
		rootView.recharge_vip_refresh_bt.setOnClickListener {
			init()
		}
	}

	private fun init() {
		AsyncTaskManager.getInstance(activity).request(1, object : OnDataListener {
			override fun doInBackground(requestCode: Int, parameter: String?): Any {
				return SealAction(activity).vipUserList
			}

			override fun onSuccess(requestCode: Int, result: Any?) {
				result?.let {
					it as GetVipUserListResponse
					var vips = it.data
					for (i in 0..3) {
						if (i < vips.size) {
							vipList[i].let {
								it.visibility = View.VISIBLE
								var vip = vips[i]
								it.getChildAt(0).apply {
									this as ImageView
									Picasso.with(activity).load(vip.headico).into(this)
								}
								it.getChildAt(1).apply {
									this as TextView
									this.text = vip.nickname
								}
								it.getChildAt(2).apply {
									this as TextView
									this.text = vip.whatsup
								}
								it.getChildAt(3).setOnClickListener {
									RongIM.getInstance().startPrivateChat(activity, vip.userid, vip.nickname)
								}
							}
						} else {
							vipList[i].visibility = View.GONE
						}
					}
				}
			}

			override fun onFailure(requestCode: Int, state: Int, result: Any?) {
				ToastUtils.showShort("服务器开小差，请稍后重试")
			}

		})
	}

	companion object {
		fun newInstance(): RechargeVipFragment = RechargeVipFragment()
	}
}
