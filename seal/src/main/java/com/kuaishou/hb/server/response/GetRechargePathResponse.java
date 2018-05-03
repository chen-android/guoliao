package com.kuaishou.hb.server.response;

/**
 * @author chenshuai12619
 * @date 2018-05-03
 */
public class GetRechargePathResponse {
	private int code;
	private String message;
	private ResultEntity data;
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message == null ? "" : message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public ResultEntity getData() {
		return data;
	}
	
	public void setData(ResultEntity data) {
		this.data = data;
	}
	
	public static class ResultEntity {
		private String orderid;
		private String money;
		private String payUrl;
		
		public String getOrderid() {
			return orderid == null ? "" : orderid;
		}
		
		public void setOrderid(String orderid) {
			this.orderid = orderid;
		}
		
		public String getMoney() {
			return money == null ? "" : money;
		}
		
		public void setMoney(String money) {
			this.money = money;
		}
		
		public String getPayUrl() {
			return payUrl == null ? "" : payUrl;
		}
		
		public void setPayUrl(String payUrl) {
			this.payUrl = payUrl;
		}
	}
}
