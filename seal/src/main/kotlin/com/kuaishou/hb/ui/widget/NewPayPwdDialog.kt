package com.kuaishou.hb.ui.widget

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.blankj.utilcode.util.KeyboardUtils
import com.kuaishou.hb.R
import com.kuaishou.hb.server.SealAction
import com.kuaishou.hb.server.network.async.AsyncTaskManager
import com.kuaishou.hb.server.network.async.OnDataListener
import com.kuaishou.hb.server.response.GetMoneyResponse
import com.kuaishou.hb.server.utils.StringUtils
import kotlinx.android.synthetic.main.dialog_red_packet_pay.view.*

/**
 * @author chenshuai12619
 * @date 2018-03-13
 */
class NewPayPwdDialog : DialogFragment() {
	lateinit var rootView: View

	private var price: Double = 0.0
	private var balance: Double = 0.0
	private var pwdList: Array<TextView> = emptyArray()
	private var pwdBuilder: StringBuilder = StringBuilder()
	var mInputCompletedListener: (String) -> Unit = {}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(DialogFragment.STYLE_NO_TITLE, 0)
		price = arguments.getDouble(KEY_PRICE)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		rootView = inflater!!.inflate(R.layout.dialog_red_packet_pay, container, false)
		init()
		return rootView
	}

	private fun init() {
		rootView.dialog_red_packet_pay_money_tv.text = StringUtils.getFormatMoney(price)
		rootView.dialog_red_packet_pay_remain_money_tv.text = StringUtils.getFormatMoney(balance)
		rootView.dialog_red_packet_pay_et.setText("")
		for (i in 0 until 6) {
			pwdList[i] = rootView.dialog_red_packet_pwd_ll.getChildAt(i) as TextView
		}
		rootView.dialog_red_packet_pay_et.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				if (s.toString().isNotBlank()) {
					if (pwdBuilder.length > 5) {
						rootView.dialog_red_packet_pay_et.setText("")
					} else {
						pwdBuilder.append(s)
						rootView.dialog_red_packet_pay_et.setText("")
						if (pwdBuilder.length == 6) {
							KeyboardUtils.hideSoftInput(rootView.dialog_red_packet_pay_et)
							mInputCompletedListener(pwdBuilder.toString())
							this@NewPayPwdDialog.dismiss()
						}
					}
					refreshPwdView()
				}
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

			}
		})
		rootView.dialog_red_packet_pay_et.setOnKeyListener { _, keyCode, event ->
			if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
				keyDelete()
			} else {
				false
			}
		}
		rootView.dialog_red_packet_close_tv.setOnClickListener {
			this.dismiss()
		}
		AsyncTaskManager.getInstance(activity).request(1, object : OnDataListener {
			override fun doInBackground(requestCode: Int, parameter: String?): Any {
				return SealAction(activity).remainMoney
			}

			override fun onSuccess(requestCode: Int, result: Any?) {
				result?.let {
					it as GetMoneyResponse
					if (it.code == 200) {
						rootView.dialog_red_packet_pay_remain_money_tv.text = StringUtils.getFormatMoney(it.data.money)
					}
				}
			}

			override fun onFailure(requestCode: Int, state: Int, result: Any?) {

			}
		})
	}

	private fun refreshPwdView() {
		for (i in 0 until 6) {
			pwdList[i].text = if (i < pwdBuilder.length) "●" else ""
		}
	}

	private fun keyDelete(): Boolean =
			if (pwdBuilder.isNotEmpty()) {
				pwdBuilder.delete(pwdBuilder.length - 1, pwdBuilder.length)
				refreshPwdView()
				true
			} else {
				false
			}


	companion object {
		private const val KEY_PRICE: String = "price"
		fun getInstance(price: Double): NewPayPwdDialog {
			return NewPayPwdDialog().apply {
				this.arguments = Bundle().apply {
					this.putDouble(KEY_PRICE, price)
				}
			}
		}
	}
}