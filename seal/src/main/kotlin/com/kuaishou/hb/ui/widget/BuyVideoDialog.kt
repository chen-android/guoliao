package com.kuaishou.hb.ui.widget

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.KeyboardUtils
import com.kuaishou.hb.R
import com.kuaishou.hb.server.utils.StringUtils
import kotlinx.android.synthetic.main.dialog_buy_video.view.*

/**
 * @author chenshuai12619
 * @date 2018-03-12
 */
class BuyVideoDialog : DialogFragment() {

	lateinit var rootView: View
	private lateinit var confirmListener: (Double) -> Unit

	//	private var balance: Double = 0.0
	private var price: Double = 0.0
	private var videoCount: Int = 0

	companion object {
		private const val KEY_PRICE: String = "price"
		fun getInstance(price: Double): BuyVideoDialog {
			return BuyVideoDialog().apply {
				this.arguments = Bundle().apply {
					this.putDouble(KEY_PRICE, price)
				}
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(DialogFragment.STYLE_NO_TITLE, 0)
//		balance = arguments.getDouble(KEY_BALANCE)
		price = arguments.getDouble(KEY_PRICE)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		rootView = inflater!!.inflate(R.layout.dialog_buy_video, container, false)
		init()
		return rootView
	}

	private fun init() {
		rootView.close_bt.setOnClickListener {
			this.dismiss()
		}
		rootView.buy_count_et.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {

			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				videoCount = s.toString().toIntOrNull() ?: 0
				if (videoCount <= 0) {
					rootView.confirm_bt.isEnabled = false
					rootView.pay_amount_tv.text = ""
				} else {
					rootView.confirm_bt.isEnabled = true
					rootView.pay_amount_tv.text = "${StringUtils.getFormatMoney(videoCount * price)}快豆"
				}
			}

		})
		rootView.confirm_bt.setOnClickListener {
			confirmListener(videoCount * price)
			KeyboardUtils.hideSoftInput(activity)
			this.dismiss()
		}
	}

	fun setOnConfirmListener(listener: (Double) -> Unit) {
		this.confirmListener = listener
	}
}