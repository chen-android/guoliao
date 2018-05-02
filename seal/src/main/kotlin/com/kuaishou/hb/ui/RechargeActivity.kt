package com.kuaishou.hb.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.Gravity
import android.widget.TextView
import com.kuaishou.hb.R
import com.kuaishou.hb.ui.activity.BaseActivity
import com.kuaishou.hb.ui.fragment.RechargeFragment
import com.kuaishou.hb.ui.fragment.RechargeVipFragment
import kotlinx.android.synthetic.main.activity_recharge.*

/**
 * 快豆充值界面
 */
class RechargeActivity : BaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_recharge)
		setTitle("快豆充值")
		initView()
	}

	private fun initView() {
		recharge_vp.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
			override fun getItem(position: Int): Fragment {
				return if (position == 0) {
					RechargeFragment.newInstance()
				} else {
					RechargeVipFragment.newInstance()
				}
			}

			override fun getCount(): Int = 2
		}
		recharge_tl.setupWithViewPager(recharge_vp)
		val tabLeft = recharge_tl.getTabAt(0)
		tabLeft?.customView = TextView(this).apply {
			this.gravity = Gravity.CENTER
			this.text = "VIP专用充值"
			this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_recharge_tab_vip, 0, 0, 0)
			this.compoundDrawablePadding = 10
		}

		val tabRight = recharge_tl.getTabAt(1)
		tabRight?.customView = TextView(this).apply {
			this.gravity = Gravity.CENTER
			this.text = "在线充值"
			this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_recharge_tabchong_selected, 0, 0, 0)
			this.compoundDrawablePadding = 10
		}
	}


}
