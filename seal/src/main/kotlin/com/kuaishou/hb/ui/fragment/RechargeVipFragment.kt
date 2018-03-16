package com.kuaishou.hb.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuaishou.hb.R


/**
 *
 */
class RechargeVipFragment : Fragment() {

	private lateinit var rootView: View

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		rootView = inflater!!.inflate(R.layout.fragment_recharge_vip, container, false)
		init()
		return rootView
	}

	private fun init() {

	}


	companion object {
		fun newInstance(): RechargeVipFragment = RechargeVipFragment()
	}
}
