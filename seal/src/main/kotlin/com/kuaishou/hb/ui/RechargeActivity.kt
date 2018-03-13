package com.kuaishou.hb.ui

import android.os.Bundle
import com.kuaishou.hb.R
import com.kuaishou.hb.ui.activity.BaseActivity

class RechargeActivity : BaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_recharge)
		setTitle("快豆充值")
	}
}
