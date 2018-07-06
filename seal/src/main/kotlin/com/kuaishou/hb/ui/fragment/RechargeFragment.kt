package com.kuaishou.hb.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import com.base.bj.paysdk.domain.TrPayResult
import com.base.bj.paysdk.utils.TrPay
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.kuaishou.hb.GGConst.GUOGUO_LOGIN_ID
import com.kuaishou.hb.R
import com.kuaishou.hb.server.SealAction
import com.kuaishou.hb.server.network.async.AsyncTaskManager
import com.kuaishou.hb.server.network.async.OnDataListener
import com.kuaishou.hb.server.response.GetRechargePathResponse
import com.kuaishou.hb.server.utils.ColorPhrase
import kotlinx.android.synthetic.main.fragment_recharge.view.*


/**
 *
 */
class RechargeFragment : Fragment() {
	private lateinit var rootView: View
	private lateinit var sp: SharedPreferences
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		sp = activity.getSharedPreferences("config", Context.MODE_PRIVATE)
	}

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

		rootView.recharge_confirm_bt.setOnClickListener {
			val money = rootView.recharge_money_et.text.toString().toFloat()
			if (money > 0) {
				AsyncTaskManager.getInstance(activity).request(1, object : OnDataListener {
					override fun doInBackground(requestCode: Int, parameter: String?): Any {
						return SealAction(activity).getRechargePath(money)
					}

					override fun onSuccess(requestCode: Int, result: Any?) {
						result?.let {
							it as GetRechargePathResponse
//							var intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.data.payUrl))
//							startActivity(intent)
							TrPay.getInstance(activity).callPay("充值${money}元", it.data.orderid, money.toLong(), "", "API/ChackOrder.aspx", sp.getString(GUOGUO_LOGIN_ID, "")) { context, outtradeno, resultCode, resultString, payType, amount, tradename ->
								/**
								 * 支付完成回调
								 * @param context        上下文
								 * @param outtradeno   商户系统订单号
								 * @param resultCode   支付状态(RESULT_CODE_SUCC：支付成功、RESULT_CODE_FAIL：支付失败)
								 * @param resultString  支付结果
								 * @param payType      支付类型（1：支付宝 2：微信 3：银联）
								 * @param amount       支付金额
								 * @param tradename   商品名称
								 */
								if (resultCode == TrPayResult.RESULT_CODE_SUCC.id) {
									//支付成功逻辑处理
								} else if (resultCode == TrPayResult.RESULT_CODE_FAIL.id) {
									//支付失败逻辑处理
								}
							}
						}
					}

					override fun onFailure(requestCode: Int, state: Int, result: Any?) {
						ToastUtils.showShort("服务器开小差，请稍后重试")
					}

				})

			} else {
				ToastUtils.showShort("金额无效")
			}
		}
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
