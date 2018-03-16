package com.kuaishou.hb.server

/**
 * @author chenshuai12619
 * @date 2018-03-16
 */
abstract class BaseSelectLinstener {
	/** 是否选中 */
	var isSelected: Boolean = false
		get() = isSelectable && isSelected
		set(value) {
			field = value && isSelectable
		}
	/** 能否被选 */
	var isSelectable: Boolean = true
		set(value) {
			field = value
			if (value.not()) {
				isSelected = false
			}
		}
}