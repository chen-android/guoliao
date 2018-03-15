package com.kuaishou.hb.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.kuaishou.hb.R
import com.kuaishou.hb.ui.activity.BaseActivity
import com.kuaishou.hb.ui.fragment.RechargeFragment
import kotlinx.android.synthetic.main.activity_recharge.*

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
					RechargeFragment.newInstance("", "")
				} else {
					RechargeFragment.newInstance("", "")
				}
			}

			override fun getCount(): Int = 2
		}
		recharge_tl.setupWithViewPager(recharge_vp)
	}


}
