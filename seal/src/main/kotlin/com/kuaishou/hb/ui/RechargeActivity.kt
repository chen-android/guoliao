package com.kuaishou.hb.ui

import android.os.Bundle
import android.view.View
import android.widget.CheckedTextView
import com.blankj.utilcode.util.SizeUtils
import com.kuaishou.hb.R
import com.kuaishou.hb.server.utils.ColorPhrase
import com.kuaishou.hb.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_recharge.*

class RechargeActivity : BaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_recharge)
		setTitle("快豆充值")
		initView()
	}

	private fun initView() {
		recharge_ctv1.text = getFormatString(10)
		recharge_ctv2.text = getFormatString(50)
		recharge_ctv3.text = getFormatString(100)
		recharge_ctv4.text = getFormatString(500)
		recharge_ctv5.text = getFormatString(1000)
		recharge_ctv6.text = ColorPhrase.from("自定义价格\n{售价:--元}")
				.innerColor(R.color.black_textview)
				.outerColor(R.color.black_textview)
				.innerSize(SizeUtils.sp2px(12f))
				.outSize(SizeUtils.sp2px(14f))
				.format()
		recharge_ctv1.tag = 10
		recharge_ctv2.tag = 50
		recharge_ctv3.tag = 100
		recharge_ctv4.tag = 500
		recharge_ctv5.tag = 1000
		recharge_ctv6.tag = ""

		recharge_ctv1.setOnClickListener(onClickListener)
		recharge_ctv2.setOnClickListener(onClickListener)
		recharge_ctv3.setOnClickListener(onClickListener)
		recharge_ctv4.setOnClickListener(onClickListener)
		recharge_ctv5.setOnClickListener(onClickListener)
		recharge_ctv6.setOnClickListener(onClickListener)
	}

	private var onClickListener = View.OnClickListener {
		if (it is CheckedTextView) {
			recharge_money_et.setText(if (it.isChecked) {
				it.isChecked = false
				""
			} else {
				resetStatus()
				it.isChecked = true
				it.tag.toString()
			})
		}
	}

	private fun getFormatString(price: Int): CharSequence {
		return ColorPhrase.from(getString(R.string.recharge_price_type, price, price))
				.innerColor(R.color.black_textview)
				.outerColor(R.color.black_textview)
				.innerSize(SizeUtils.sp2px(12f))
				.outSize(SizeUtils.sp2px(14f))
				.format()
	}

	private fun resetStatus() {
		recharge_ctv1.isChecked = false
		recharge_ctv2.isChecked = false
		recharge_ctv3.isChecked = false
		recharge_ctv4.isChecked = false
		recharge_ctv5.isChecked = false
		recharge_ctv6.isChecked = false
	}
}
