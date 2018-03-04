package com.kuaishou.hb.server.response;

/**
 * Created by chenshuai12619 on 2017-08-26.
 */

public class GetVersionResponse {
	private String message;
	private int code;
	private GetVersionData data;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public GetVersionData getData() {
		return data;
	}

	public void setData(GetVersionData data) {
		this.data = data;
	}

	public static class GetVersionData {
		private String appName;
		private String android_version;
		private String android_version_lowest;
		private String updateUrl;
		private int updateState;

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getAndroid_version() {
			return android_version;
		}

		public void setAndroid_version(String android_version) {
			this.android_version = android_version;
		}

		public String getAndroid_version_lowest() {
			return android_version_lowest;
		}

		public void setAndroid_version_lowest(String android_version_lowest) {
			this.android_version_lowest = android_version_lowest;
		}

		public String getUpdateUrl() {
			return updateUrl;
		}

		public void setUpdateUrl(String updateUrl) {
			this.updateUrl = updateUrl;
		}

		public int getUpdateState() {
			return updateState;
		}

		public void setUpdateState(int updateState) {
			this.updateState = updateState;
		}
	}
}
