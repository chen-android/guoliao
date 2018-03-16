package com.kuaishou.hb.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import com.blankj.utilcode.util.SizeUtils
import com.kuaishou.hb.R
import com.kuaishou.hb.server.utils.ColorPhrase
import kotlinx.android.synthetic.main.fragment_recharge.view.*

/**
 *
 */
class RechargeFragment : Fragment() {
	private lateinit var rootView: View

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		rootView = inflater!!.inflate(R.layout.fragment_recharge, container, false)
		init()
		return rootView
	}

	private fun init() {
		rootView.recharge_ctv1.text = getFormatString(10)
		rootView.recharge_ctv2.text = getFormatString(50)
		rootView.recharge_ctv3.text = getFormatString(100)
		rootView.recharge_ctv4.text = getFormatString(500)
		rootView.recharge_ctv5.text = getFormatString(1000)
		rootView.recharge_ctv6.text = ColorPhrase.from("自定义价格\n{售价:--元}")
				.innerColor(R.color.black_textview)
				.outerColor(R.color.black_textview)
				.innerSize(SizeUtils.sp2px(12f))
				.outSize(SizeUtils.sp2px(14f))
				.format()
		rootView.recharge_ctv1.tag = 10
		rootView.recharge_ctv2.tag = 50
		rootView.recharge_ctv3.tag = 100
		rootView.recharge_ctv4.tag = 500
		rootView.recharge_ctv5.tag = 1000
		rootView.recharge_ctv6.tag = ""

		rootView.recharge_ctv1.setOnClickListener(onClickListener)
		rootView.recharge_ctv2.setOnClickListener(onClickListener)
		rootView.recharge_ctv3.setOnClickListener(onClickListener)
		rootView.recharge_ctv4.setOnClickListener(onClickListener)
		rootView.recharge_ctv5.setOnClickListener(onClickListener)
		rootView.recharge_ctv6.setOnClickListener(onClickListener)
	}

	private var onClickListener = View.OnClickListener {
		if (it is CheckedTextView) {
			rootView.recharge_money_et.setText(if (it.isChecked) {
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
		rootView.recharge_ctv1.isChecked = false
		rootView.recharge_ctv2.isChecked = false
		rootView.recharge_ctv3.isChecked = false
		rootView.recharge_ctv4.isChecked = false
		rootView.recharge_ctv5.isChecked = false
		rootView.recharge_ctv6.isChecked = false
	}

	companion object {
		private val ARG_PARAM1 = "param1"
		private val ARG_PARAM2 = "param2"

		fun newInstance(): RechargeFragment = RechargeFragment()
	}
}
