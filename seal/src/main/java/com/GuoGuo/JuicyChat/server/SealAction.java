package com.GuoGuo.JuicyChat.server;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.request.AddGroupMemberRequest;
import com.GuoGuo.JuicyChat.server.request.AddToBlackListRequest;
import com.GuoGuo.JuicyChat.server.request.AgreeFriendsRequest;
import com.GuoGuo.JuicyChat.server.request.BaseTokenRequest;
import com.GuoGuo.JuicyChat.server.request.ChangePasswordRequest;
import com.GuoGuo.JuicyChat.server.request.CheckPhoneRequest;
import com.GuoGuo.JuicyChat.server.request.CreateGroupRequest;
import com.GuoGuo.JuicyChat.server.request.DeleteFriendRequest;
import com.GuoGuo.JuicyChat.server.request.DismissGroupRequest;
import com.GuoGuo.JuicyChat.server.request.FriendInvitationRequest;
import com.GuoGuo.JuicyChat.server.request.GetGroupMemberRequest;
import com.GuoGuo.JuicyChat.server.request.GetUserInfosRequest;
import com.GuoGuo.JuicyChat.server.request.GroupMemberRequest;
import com.GuoGuo.JuicyChat.server.request.JoinGroupRequest;
import com.GuoGuo.JuicyChat.server.request.LoginRequest;
import com.GuoGuo.JuicyChat.server.request.RegisterRequest;
import com.GuoGuo.JuicyChat.server.request.RemoveFromBlacklistRequest;
import com.GuoGuo.JuicyChat.server.request.RestPasswordRequest;
import com.GuoGuo.JuicyChat.server.request.SearchFriendRequest;
import com.GuoGuo.JuicyChat.server.request.SendCodeRequest;
import com.GuoGuo.JuicyChat.server.request.SetFriendDisplayNameRequest;
import com.GuoGuo.JuicyChat.server.request.SetGroupDisplayNameRequest;
import com.GuoGuo.JuicyChat.server.request.SetGroupNameRequest;
import com.GuoGuo.JuicyChat.server.request.SetGroupPortraitRequest;
import com.GuoGuo.JuicyChat.server.request.SetNameRequest;
import com.GuoGuo.JuicyChat.server.request.SetPortraitRequest;
import com.GuoGuo.JuicyChat.server.request.SetSexRequest;
import com.GuoGuo.JuicyChat.server.request.SetWhatupRequest;
import com.GuoGuo.JuicyChat.server.request.VerifyCodeRequest;
import com.GuoGuo.JuicyChat.server.response.AddGroupMemberResponse;
import com.GuoGuo.JuicyChat.server.response.AddToBlackListResponse;
import com.GuoGuo.JuicyChat.server.response.BaseResponse;
import com.GuoGuo.JuicyChat.server.response.ChatroomListResponse;
import com.GuoGuo.JuicyChat.server.response.CheckPhoneResponse;
import com.GuoGuo.JuicyChat.server.response.CheckRedPacketCountResponse;
import com.GuoGuo.JuicyChat.server.response.CreateGroupResponse;
import com.GuoGuo.JuicyChat.server.response.DeleteFriendResponse;
import com.GuoGuo.JuicyChat.server.response.DeleteGroupMemberResponse;
import com.GuoGuo.JuicyChat.server.response.DismissGroupResponse;
import com.GuoGuo.JuicyChat.server.response.FriendInvitationResponse;
import com.GuoGuo.JuicyChat.server.response.GetBlackListResponse;
import com.GuoGuo.JuicyChat.server.response.GetFriendInfoByIDResponse;
import com.GuoGuo.JuicyChat.server.response.GetFriendListResponse;
import com.GuoGuo.JuicyChat.server.response.GetGroupInfoResponse;
import com.GuoGuo.JuicyChat.server.response.GetGroupMemberResponse;
import com.GuoGuo.JuicyChat.server.response.GetGroupResponse;
import com.GuoGuo.JuicyChat.server.response.GetMoneyResponse;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketDetailResponse;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketStatisticResponse;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketUsersResponse;
import com.GuoGuo.JuicyChat.server.response.GetShareRewardResponse;
import com.GuoGuo.JuicyChat.server.response.GetTokenResponse;
import com.GuoGuo.JuicyChat.server.response.GetUserInfoByIdResponse;
import com.GuoGuo.JuicyChat.server.response.GetUserInfoByPhoneResponse;
import com.GuoGuo.JuicyChat.server.response.GetUserInfoByTokenResponse;
import com.GuoGuo.JuicyChat.server.response.GetUserInfosResponse;
import com.GuoGuo.JuicyChat.server.response.GetVersionResponse;
import com.GuoGuo.JuicyChat.server.response.JoinGroupResponse;
import com.GuoGuo.JuicyChat.server.response.LockMoneyListResponse;
import com.GuoGuo.JuicyChat.server.response.LoginResponse;
import com.GuoGuo.JuicyChat.server.response.OpenRedPacketResponse;
import com.GuoGuo.JuicyChat.server.response.QiNiuTokenResponse;
import com.GuoGuo.JuicyChat.server.response.QuitGroupResponse;
import com.GuoGuo.JuicyChat.server.response.RedPacketReceiveResponse;
import com.GuoGuo.JuicyChat.server.response.RedPacketSendResponse;
import com.GuoGuo.JuicyChat.server.response.RegisterResponse;
import com.GuoGuo.JuicyChat.server.response.RemoveFromBlackListResponse;
import com.GuoGuo.JuicyChat.server.response.SendCodeResponse;
import com.GuoGuo.JuicyChat.server.response.SendRedPacketResponse;
import com.GuoGuo.JuicyChat.server.response.SetFriendDisplayNameResponse;
import com.GuoGuo.JuicyChat.server.response.SetGroupNameResponse;
import com.GuoGuo.JuicyChat.server.response.SetGroupPortraitResponse;
import com.GuoGuo.JuicyChat.server.response.SetNameResponse;
import com.GuoGuo.JuicyChat.server.response.SetPortraitResponse;
import com.GuoGuo.JuicyChat.server.response.ShareLinkResponse;
import com.GuoGuo.JuicyChat.server.response.SyncTotalDataResponse;
import com.GuoGuo.JuicyChat.server.response.TransferRecordResponse;
import com.GuoGuo.JuicyChat.server.response.TransferRecordTypesRes;
import com.GuoGuo.JuicyChat.server.response.VerifyCodeResponse;
import com.GuoGuo.JuicyChat.server.response.VersionResponse;
import com.GuoGuo.JuicyChat.server.utils.NLog;
import com.GuoGuo.JuicyChat.server.utils.json.JsonMananger;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
@SuppressWarnings("deprecation")
public class SealAction extends BaseAction {
	private final String CONTENT_TYPE = "application/json";
	private final String ENCODING = "utf-8";
	
	/**
	 * 构造方法
	 *
	 * @param context 上下文
	 */
	public SealAction(Context context) {
		super(context);
	}
	
	
	/**
	 * 检查手机是否被注册
	 *
	 * @param region 国家码
	 * @param phone  手机号
	 * @throws HttpException
	 */
	public CheckPhoneResponse checkPhoneAvailable(String region, String phone) throws HttpException {
		String url = getURL("user/check_phone_available");
		String json = JsonMananger.beanToJson(new CheckPhoneRequest(phone, region));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		CheckPhoneResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, CheckPhoneResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 发送验证码
	 *
	 * @param account 手机号
	 * @throws HttpException
	 */
	public SendCodeResponse sendCode(String account) throws HttpException {
		String url = getURL("VerificationCode.aspx");
		String json = JsonMananger.beanToJson(new SendCodeRequest(account));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SendCodeResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = JsonMananger.jsonToBean(result, SendCodeResponse.class);
		}
		return response;
	}

    /*
    * 200: 验证成功
    1000: 验证码错误
    2000: 验证码过期
    异常返回，返回的 HTTP Status Code 如下：

    400: 错误的请求
    500: 应用服务器内部错误
    * */
	
	/**
	 * 验证验证码是否正确(必选先用手机号码调sendcode)
	 *
	 * @param region 国家码
	 * @param phone  手机号
	 * @throws HttpException
	 */
	public VerifyCodeResponse verifyCode(String region, String phone, String code) throws HttpException {
		String url = getURL("user/verify_code");
		String json = JsonMananger.beanToJson(new VerifyCodeRequest(region, phone, code));
		VerifyCodeResponse response = null;
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			Log.e("VerifyCodeResponse", result);
			response = jsonToBean(result, VerifyCodeResponse.class);
		}
		return response;
	}
	
	/**
	 * 注册
	 *
	 * @param account  昵称
	 * @param password 密码
	 * @param code     验证码
	 * @throws HttpException
	 */
	public RegisterResponse register(String account, String password, String code, String openid) throws HttpException {
		String url = getURL("Register.aspx");
		StringEntity entity = null;
		try {
			entity = new StringEntity(JsonMananger.beanToJson(new RegisterRequest(account, password, code, openid)), ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		RegisterResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			NLog.e("RegisterResponse", result);
			response = jsonToBean(result, RegisterResponse.class);
		}
		return response;
	}
	
	/**
	 * 登录: 登录成功后，会设置 Cookie，后续接口调用需要登录的权限都依赖于 Cookie。
	 *
	 * @param region   国家码
	 * @param phone    手机号
	 * @param password 密码
	 * @throws HttpException
	 */
	public LoginResponse login(String region, String phone, String password) throws HttpException {
		String uri = getURL("Login.aspx");
		String json = JsonMananger.beanToJson(new LoginRequest(phone, password));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, uri, entity, CONTENT_TYPE);
		LoginResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			NLog.e("LoginResponse", result);
			response = JsonMananger.jsonToBean(result, LoginResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 获取 token 前置条件需要登录   502 坏的网关 测试环境用户已达上限
	 *
	 * @throws HttpException
	 */
	public GetTokenResponse getToken() throws HttpException {
		String url = getURL("user/get_token");
		String result = httpManager.get(url);
		GetTokenResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			NLog.e("GetTokenResponse", result);
			response = jsonToBean(result, GetTokenResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 检查有没有设置过支付密码
	 *
	 * @throws HttpException
	 */
	public BaseResponse checkIsNewPwd() throws HttpException {
		String url = getURL("CheckPayPwd.aspx");
		String json = JsonMananger.beanToJson(new BaseTokenRequest());
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BaseResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 获取最新版本信息
	 *
	 * @return
	 * @throws HttpException
	 */
	public GetVersionResponse getVersion() throws HttpException {
		String url = getURL("GetVersion.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("platform", "android");
		map.put("version", AppUtils.getAppVersionName());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		GetVersionResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetVersionResponse.class);
		}
		return response;
	}
	
	public String getNewApkUrl() throws HttpException {
		String url = getURL("GetDomain.aspx");
		String result = httpManager.post(url);
		if (!TextUtils.isEmpty(result)) {
			return JSON.parseObject(result).getJSONObject("data").getString("url");
		}
		return "";
	}
	
	public String getWxLoginAccessToken(String url) throws HttpException {
		String result = httpManager.get(mContext, url);
		try {
			org.json.JSONObject object = new org.json.JSONObject(result);
			return object.getString("unionid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public BaseResponse requestRecharge(long money, long toUserId, String paypwd, String note) throws HttpException {
		String url = getURL("TransferMoney.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		map.put("money", money);
		map.put("toUserId", toUserId);
		map.put("paypwd", paypwd);
		map.put("note", note);
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BaseResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 微信登录
	 *
	 * @param unionid
	 * @return
	 * @throws HttpException
	 */
	public LoginResponse wxLogin(String unionid) throws HttpException {
		String url = getURL("LoginByWeChat.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("wechat", unionid);
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		LoginResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, LoginResponse.class);
		}
		return response;
	}
	
	/**
	 * qq登录
	 *
	 * @param uid
	 * @return
	 * @throws HttpException
	 */
	public LoginResponse qqLogin(String uid) throws HttpException {
		String url = getURL("LoginByQQ.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("qqcode", uid);
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		LoginResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, LoginResponse.class);
		}
		return response;
	}
	
	
	public String getShareUrl() throws HttpException {
		String url = getURL("getShareImg.aspx");
		String result = httpManager.post(url);
		if (!TextUtils.isEmpty(result)) {
			return JSON.parseObject(result).getJSONObject("data").getString("url");
		}
		return "";
	}
	
	public ShareLinkResponse getShareLink() throws HttpException {
		String url = getURL("GetShareLink.aspx");
		String json = JsonMananger.beanToJson(new BaseTokenRequest());
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ShareLinkResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, ShareLinkResponse.class);
		}
		return response;
	}
	
	/**
	 * 获取分享奖励
	 *
	 * @return
	 * @throws HttpException
	 */
	public GetShareRewardResponse getShareReward() throws HttpException {
		String url = getURL("GetShareReward.aspx");
		String json = JsonMananger.beanToJson(new BaseTokenRequest());
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		GetShareRewardResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetShareRewardResponse.class);
		}
		return response;
	}
	
	/**
	 * 获取被冻结金额的组员列表
	 *
	 * @param groupId
	 * @return
	 * @throws HttpException
	 */
	public GetGroupMemberResponse getLockedGroupMembers(String groupId) throws HttpException {
		String url = getURL("GetGroupLockMoneyList.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("groupId", groupId);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		GetGroupMemberResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetGroupMemberResponse.class);
		}
		return response;
	}
	
	/**
	 * 解冻金额
	 *
	 * @param groupId
	 * @return
	 * @throws HttpException
	 */
	public BaseResponse unLockedGroupMembers(List<String> memIds, String groupId) throws HttpException {
		String url = getURL("UnLockUserMoney.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("userList", memIds);
		map.put("groupId", groupId);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BaseResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 设置新支付密码
	 *
	 * @throws HttpException
	 */
	public BaseResponse setNewPayPwd(String pwd) throws HttpException {
		String url = getURL("SetPaypwd.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("paypwd", pwd);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BaseResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 更新支付密码
	 *
	 * @throws HttpException
	 */
	public BaseResponse editPayPwd(String oldPwd, String pwd) throws HttpException {
		String url = getURL("UpdatePaypwd.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("paypwd", pwd);
		map.put("oldPaypwd", oldPwd);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BaseResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 获取余额
	 *
	 * @throws HttpException
	 */
	public GetMoneyResponse getRemainMoney() throws HttpException {
		String url = getURL("GetMoney.aspx");
		String json = JsonMananger.beanToJson(new BaseTokenRequest());
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		GetMoneyResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = JsonMananger.jsonToBean(result, GetMoneyResponse.class);
		}
		return response;
	}
	
	/**
	 * 检查红包是不是抢过
	 *
	 * @throws HttpException
	 */
	public BaseResponse checkRedPacketHasOpened(String redpacketId) throws HttpException {
		String url = getURL("CheckRedPacketUnPacked.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("redpacketid", redpacketId);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		BaseResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 检查红包是不是还有剩余
	 *
	 * @throws HttpException
	 */
	public CheckRedPacketCountResponse checkRedPacketHasRemain(String redpacketId) throws HttpException {
		String url = getURL("GetRedPacketCount.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("redpacketid", redpacketId);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		CheckRedPacketCountResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, CheckRedPacketCountResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 发红包
	 *
	 * @throws HttpException
	 */
	public SendRedPacketResponse sendRedPacket(int toId, long money, String pwd, String note, int sort, int type, int count) throws HttpException {
		String url = getURL("SendRedPacket.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("toMemberId", toId);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		map.put("money", money);
		map.put("paypwd", pwd);
		map.put("note", note);
		map.put("sort", sort);
		map.put("type", type);
		map.put("count", count);
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SendRedPacketResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = JsonMananger.jsonToBean(result, SendRedPacketResponse.class);
		}
		return response;
	}
	
	/**
	 * 发红包
	 *
	 * @throws HttpException
	 */
	public OpenRedPacketResponse openRedPacket(String redid) throws HttpException {
		String url = getURL("UnPackRedPacket.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("redpacketid", redid);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		OpenRedPacketResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = JsonMananger.jsonToBean(result, OpenRedPacketResponse.class);
		}
		return response;
	}
	
	/**
	 * 发红包
	 *
	 * @throws HttpException
	 */
	public GetRedPacketStatisticResponse getMyRedPacketInfo() throws HttpException {
		String url = getURL("GetRedPacketStatistic.aspx");
		String json = JsonMananger.beanToJson(new BaseTokenRequest());
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		GetRedPacketStatisticResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = JsonMananger.jsonToBean(result, GetRedPacketStatisticResponse.class);
		}
		return response;
	}
	
	/**
	 * 获取接受红包记录列表
	 *
	 * @param index 页码
	 * @return
	 * @throws HttpException
	 */
	public RedPacketReceiveResponse getRedPacketReceiveList(int index) throws HttpException {
		String url = getURL("GetRedPacketReciveRecord.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("index", index);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		RedPacketReceiveResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = JsonMananger.jsonToBean(result, RedPacketReceiveResponse.class);
		}
		return response;
	}
	
	/**
	 * 获取发送红包记录列表
	 *
	 * @param index 页码
	 * @return
	 * @throws HttpException
	 */
	public RedPacketSendResponse getRedPacketSendList(int index) throws HttpException {
		String url = getURL("GetRedPacketSendRecord.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("index", index);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		RedPacketSendResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = JsonMananger.jsonToBean(result, RedPacketSendResponse.class);
		}
		return response;
	}
	
	/**
	 * 检查红包锁定金额情况
	 *
	 * @throws HttpException
	 */
	public BaseResponse checkLockMoney(long groupId) throws HttpException {
		String url = getURL("CheckLockMoney.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("groupId", groupId);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BaseResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = JsonMananger.jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 红包详情
	 *
	 * @throws HttpException
	 */
	public GetRedPacketDetailResponse getRedPacketDetail(String redid) throws HttpException {
		String url = getURL("GetRedPacketInfo.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("redpacketid", redid);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		GetRedPacketDetailResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = JsonMananger.jsonToBean(result, GetRedPacketDetailResponse.class);
		}
		return response;
	}
	
	/**
	 * 红包成员列表
	 *
	 * @throws HttpException
	 */
	public GetRedPacketUsersResponse getRedPacketMenber(String redid) throws HttpException {
		String url = getURL("GetRedPacketUsers.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("redpacketid", redid);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		GetRedPacketUsersResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = JsonMananger.jsonToBean(result, GetRedPacketUsersResponse.class);
		}
		return response;
	}
	
	/**
	 * 设置自己的昵称
	 *
	 * @param nickname 昵称
	 * @throws HttpException
	 */
	public SetNameResponse setName(String nickname) throws HttpException {
		String url = getURL("UpdateMyInfo.aspx");
		String json = JsonMananger.beanToJson(new SetNameRequest(nickname, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SetNameResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, SetNameResponse.class);
		}
		return response;
	}
	
	public SetNameResponse setWhatsup(String whatsup) throws HttpException {
		String url = getURL("UpdateMyInfo.aspx");
		String json = JsonMananger.beanToJson(new SetWhatupRequest(whatsup, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SetNameResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, SetNameResponse.class);
		}
		return response;
	}
	
	/**
	 * 设置自己的性别
	 *
	 * @param sex 性别
	 * @throws HttpException
	 */
	public BaseResponse setSex(String sex) throws HttpException {
		String url = getURL("UpdateMyInfo.aspx");
		String json = JsonMananger.beanToJson(new SetSexRequest(sex, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BaseResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 设置用户头像
	 *
	 * @param portraitUri 头像 path
	 * @throws HttpException
	 */
	public SetPortraitResponse setPortrait(String portraitUri) throws HttpException {
		String url = getURL("UpdateMyInfo.aspx");
		String json = JsonMananger.beanToJson(new SetPortraitRequest(portraitUri, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SetPortraitResponse response = null;
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, SetPortraitResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 当前登录用户通过旧密码设置新密码  前置条件需要登录才能访问
	 *
	 * @param oldPassword 旧密码
	 * @param newPassword 新密码
	 * @throws HttpException
	 */
	public BaseResponse changePassword(String oldPassword, String newPassword) throws HttpException {
		String url = getURL("UpdatePassword.aspx");
		String json = JsonMananger.beanToJson(new ChangePasswordRequest(oldPassword, newPassword, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		BaseResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 通过手机验证码重置密码
	 *
	 * @param password           密码
	 * @param verification_token
	 * @throws HttpException
	 */
	public BaseResponse restPayPassword(String password, String verification_token) throws HttpException {
		String uri = getURL("ResetPaypwd.aspx");
		String json = JsonMananger.beanToJson(new RestPasswordRequest(password, verification_token, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, uri, entity, CONTENT_TYPE);
		BaseResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			NLog.e("RestPasswordResponse", result);
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	public BaseResponse restPassword(String account, String password, String verification_token) throws HttpException {
		String uri = getURL("ResetPassword.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("account", account);
		map.put("code", verification_token);
		map.put("password", password);
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, uri, entity, CONTENT_TYPE);
		BaseResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			NLog.e("RestPasswordResponse", result);
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 根据 id 去服务端查询用户信息
	 *
	 * @param token 用户token
	 * @throws HttpException
	 */
	public GetUserInfoByTokenResponse getUserInfoByToken(String token) throws HttpException {
		String url = getURL("GetUserInfo.aspx");
		String json = JsonMananger.beanToJson(new GetUserInfosRequest(token));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		GetUserInfoByTokenResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetUserInfoByTokenResponse.class);
		}
		return response;
	}
	
	/**
	 * 根据 id 去服务端查询用户信息
	 *
	 * @throws HttpException
	 */
	public GetUserInfoByIdResponse getUserInfoById(String id) throws HttpException {
		String url = getURL("GetUser.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("userId", id);
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		GetUserInfoByIdResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetUserInfoByIdResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 通过国家码和手机号查询用户信息
	 *
	 * @param phone 手机号
	 * @throws HttpException
	 */
	public GetUserInfoByPhoneResponse getUserInfoFromPhone(String phone) throws HttpException {
		String url = getURL("Search.aspx");
		String json = JsonMananger.beanToJson(new SearchFriendRequest(phone));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		GetUserInfoByPhoneResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetUserInfoByPhoneResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 发送好友邀请
	 *
	 * @throws HttpException
	 */
	public LockMoneyListResponse getLockMoneyList() throws HttpException {
		String url = getURL("GetLockMoneyList.aspx");
		String json = JsonMananger.beanToJson(new BaseTokenRequest());
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		LockMoneyListResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, LockMoneyListResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 发送好友邀请
	 *
	 * @param userid 好友id
	 * @throws HttpException
	 */
	public FriendInvitationResponse sendFriendInvitation(String userid) throws HttpException {
		String url = getURL("AddFriend.aspx");
		String json = JsonMananger.beanToJson(new FriendInvitationRequest(userid, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		FriendInvitationResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, FriendInvitationResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 获取发生过用户关系的列表
	 *
	 * @throws HttpException
	 */
	public GetFriendListResponse getAllUserRelationship() throws HttpException {
		String url = getURL("GetFriends.aspx");
		
		String json = JsonMananger.beanToJson(new BaseTokenRequest(SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		
		GetFriendListResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetFriendListResponse.class);
		}
		return response;
	}
	
	/**
	 * 根据userId去服务器查询好友信息
	 *
	 * @throws HttpException
	 */
	public GetFriendInfoByIDResponse getFriendInfoByID(String userid) throws HttpException {
		String url = getURL("GetFriendInfo.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("friendid", userid);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		GetFriendInfoByIDResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetFriendInfoByIDResponse.class);
		}
		return response;
	}
	
	/**
	 * 同意对方好友邀请
	 *
	 * @param friendId 好友ID
	 * @throws HttpException
	 */
	public BaseResponse agreeFriends(String friendId) throws HttpException {
		String url = getURL("UpdateFriend.aspx");
		String json = JsonMananger.beanToJson(new AgreeFriendsRequest(friendId, SharedPreferencesContext.getInstance().getToken(), 1));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		BaseResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 删除一条好友请求消息
	 *
	 * @param friendId 好友ID
	 * @throws HttpException
	 */
	public BaseResponse deleteFriendsRequestMsg(String friendId) throws HttpException {
		String url = getURL("RemoveFriendFromNewFriends.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("friendId", friendId);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		BaseResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 创建群组
	 *
	 * @param name      群组名
	 * @param memberIds 群组成员id
	 * @throws HttpException
	 */
	public CreateGroupResponse createGroup(String name, String headico, List<Integer> memberIds) throws HttpException {
		String url = getURL("CreateGroup.aspx");
		String json = JsonMananger.beanToJson(new CreateGroupRequest(name, SharedPreferencesContext.getInstance().getToken(), headico, memberIds));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		CreateGroupResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, CreateGroupResponse.class);
		}
		return response;
	}
	
	/**
	 * 创建者设置群组头像
	 *
	 * @param groupId     群组Id
	 * @param portraitUri 群组头像
	 * @throws HttpException
	 */
	public SetGroupPortraitResponse setGroupPortrait(String groupId, String portraitUri) throws HttpException {
		String url = getURL("UpdateGroup.aspx");
		String json = JsonMananger.beanToJson(new SetGroupPortraitRequest(groupId, SharedPreferencesContext.getInstance().getToken(), portraitUri));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		SetGroupPortraitResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, SetGroupPortraitResponse.class);
		}
		return response;
	}
	
	/**
	 * 获取当前用户所属群组列表
	 *
	 * @throws HttpException
	 */
	public GetGroupResponse getGroups() throws HttpException {
		String url = getURL("getGroupList.aspx");
		String json = JsonMananger.beanToJson(new BaseTokenRequest());
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		GetGroupResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetGroupResponse.class);
		}
		return response;
	}
	
	/**
	 * 根据 群组id 查询该群组信息   403 群组成员才能看
	 *
	 * @param groupId 群组Id
	 * @throws HttpException
	 */
	public GetGroupInfoResponse getGroupInfo(String groupId) throws HttpException {
		String url = getURL("GetGroupinfo.aspx");
		Map<String, String> map = new HashMap<>();
		map.put("groupId", groupId);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		GetGroupInfoResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetGroupInfoResponse.class);
		}
		return response;
	}
	
	/**
	 * 根据群id获取群组成员
	 *
	 * @param groupId 群组Id
	 * @throws HttpException
	 */
	public GetGroupMemberResponse getGroupMember(String groupId) throws HttpException {
		String url = getURL("getGroupUserList.aspx");
		String json = JsonMananger.beanToJson(new GetGroupMemberRequest(groupId, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		GetGroupMemberResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetGroupMemberResponse.class);
		}
		return response;
	}
	
	/**
	 * 当前用户添加群组成员
	 *
	 * @param groupId   群组Id
	 * @param memberIds 成员集合
	 * @throws HttpException
	 */
	public AddGroupMemberResponse addGroupMember(String groupId, List<String> memberIds) throws HttpException {
		String url = getURL("AddUserToGroup.aspx");
		String json = JsonMananger.beanToJson(new AddGroupMemberRequest(groupId, memberIds, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		AddGroupMemberResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, AddGroupMemberResponse.class);
		}
		return response;
	}
	
	/**
	 * 创建者将群组成员提出群组
	 *
	 * @param groupId   群组Id
	 * @param memberIds 成员集合
	 * @throws HttpException
	 */
	public DeleteGroupMemberResponse deleGroupMember(String groupId, List<String> memberIds) throws HttpException {
		String url = getURL("RemoveUserFromGroup.aspx");
		String json = JsonMananger.beanToJson(new GroupMemberRequest(groupId, memberIds, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		DeleteGroupMemberResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, DeleteGroupMemberResponse.class);
		}
		return response;
	}
	
	/**
	 * 根据群id获取已被禁言的群成员
	 *
	 * @param groupId 群组Id
	 * @throws HttpException
	 */
	public GetGroupMemberResponse getGagGroupMember(String groupId) throws HttpException {
		String url = getURL("GetGroupGagUser.aspx");
		String json = JsonMananger.beanToJson(new GetGroupMemberRequest(groupId, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		GetGroupMemberResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetGroupMemberResponse.class);
		}
		return response;
	}
	
	/**
	 * 创建者将群组成员禁言
	 *
	 * @param groupId   群组Id
	 * @param memberIds 成员集合
	 * @throws HttpException
	 */
	public BaseResponse setGagGroupMember(String groupId, List<String> memberIds) throws HttpException {
		String url = getURL("SetGroupGagUser.aspx");
		String json = JsonMananger.beanToJson(new GroupMemberRequest(groupId, memberIds, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		BaseResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 创建者将群组禁言成员解禁
	 *
	 * @param groupId   群组Id
	 * @param memberIds 成员集合
	 * @throws HttpException
	 */
	public BaseResponse removeGagGroupMember(String groupId, List<String> memberIds) throws HttpException {
		String url = getURL("RemoveGagUser.aspx");
		String json = JsonMananger.beanToJson(new GroupMemberRequest(groupId, memberIds, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		BaseResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	
	
	/**
	 * 创建者更改群组昵称
	 *
	 * @param groupId 群组Id
	 * @param name    群昵称
	 * @throws HttpException
	 */
	public SetGroupNameResponse setGroupName(String groupId, String name) throws HttpException {
		String url = getURL("group/rename");
		String json = JsonMananger.beanToJson(new SetGroupNameRequest(groupId, name));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		SetGroupNameResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, SetGroupNameResponse.class);
		}
		return response;
	}
	
	/**
	 * 用户自行退出群组
	 *
	 * @param groupId 群组Id
	 * @throws HttpException
	 */
	public QuitGroupResponse quitGroup(String groupId) throws HttpException {
		String url = getURL("RemoveGroup.aspx");
		String json = JsonMananger.beanToJson(new DismissGroupRequest(groupId, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		QuitGroupResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, QuitGroupResponse.class);
		}
		return response;
	}
	
	/**
	 * 创建者解散群组
	 *
	 * @param groupId 群组Id
	 * @throws HttpException
	 */
	public DismissGroupResponse dissmissGroup(String groupId) throws HttpException {
		String url = getURL("RemoveGroup.aspx");
		String json = JsonMananger.beanToJson(new DismissGroupRequest(groupId, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		DismissGroupResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, DismissGroupResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 修改自己的当前的群昵称
	 *
	 * @param groupId 群组Id
	 * @throws HttpException
	 */
	public BaseResponse updateGroupInfo(String groupId, String name, String redPack, String limit, String headIco, int isnonotice, String gonggao, int iscanadduser) throws HttpException {
		String url = getURL("UpdateGroup.aspx");
		String json = JsonMananger.beanToJson(new SetGroupDisplayNameRequest(groupId, SharedPreferencesContext.getInstance().getToken(), name, redPack, limit, headIco, isnonotice, gonggao, iscanadduser));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		BaseResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	/**
	 * 删除好友
	 *
	 * @param friendId 好友Id
	 * @throws HttpException
	 */
	public DeleteFriendResponse deleteFriend(String friendId) throws HttpException {
		String url = getURL("RemoveFriend.aspx");
		String json = JsonMananger.beanToJson(new DeleteFriendRequest(friendId, SharedPreferencesContext.getInstance().getToken()));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		DeleteFriendResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, DeleteFriendResponse.class);
		}
		return response;
	}
	
	/**
	 * 设置好友的备注名称
	 *
	 * @param friendId    好友Id
	 * @param displayName 备注名
	 * @throws HttpException
	 */
	public SetFriendDisplayNameResponse setFriendDisplayName(String friendId, String displayName) throws HttpException {
		String url = getURL("UpdateFriend.aspx");
		String json = JsonMananger.beanToJson(new SetFriendDisplayNameRequest(SharedPreferencesContext.getInstance().getToken(), friendId, displayName));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		SetFriendDisplayNameResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, SetFriendDisplayNameResponse.class);
		}
		return response;
	}
	
	/**
	 * 获取黑名单
	 *
	 * @throws HttpException
	 */
	public GetBlackListResponse getBlackList() throws HttpException {
		String url = getURL("user/blacklist");
		String result = httpManager.get(mContext, url);
		GetBlackListResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetBlackListResponse.class);
		}
		return response;
	}
	
	/**
	 * 加入黑名单
	 *
	 * @param friendId 群组Id
	 * @throws HttpException
	 */
	public AddToBlackListResponse addToBlackList(String friendId) throws HttpException {
		String url = getURL("user/add_to_blacklist");
		String json = JsonMananger.beanToJson(new AddToBlackListRequest(friendId));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		AddToBlackListResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, AddToBlackListResponse.class);
		}
		return response;
	}
	
	/**
	 * 移除黑名单
	 *
	 * @param friendId 好友Id
	 * @throws HttpException
	 */
	public RemoveFromBlackListResponse removeFromBlackList(String friendId) throws HttpException {
		String url = getURL("user/remove_from_blacklist");
		String json = JsonMananger.beanToJson(new RemoveFromBlacklistRequest(friendId));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		RemoveFromBlackListResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, RemoveFromBlackListResponse.class);
		}
		return response;
	}
	
	public QiNiuTokenResponse getQiNiuToken() throws HttpException {
		String url = getURL("GetQiNiuToken.aspx");
		String json = JsonMananger.beanToJson(new BaseTokenRequest());
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		QiNiuTokenResponse q = null;
		if (!TextUtils.isEmpty(result)) {
			q = jsonToBean(result, QiNiuTokenResponse.class);
		}
		return q;
	}
	
	
	/**
	 * 当前用户加入某群组
	 *
	 * @param groupId 群组Id
	 * @throws HttpException
	 */
	public JoinGroupResponse JoinGroup(String groupId) throws HttpException {
		String url = getURL("group/join");
		String json = JsonMananger.beanToJson(new JoinGroupRequest(groupId));
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		JoinGroupResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, JoinGroupResponse.class);
		}
		return response;
	}
	
	
	/**
	 * 获取默认群组 和 聊天室
	 *
	 * @throws HttpException
	 */
	public ChatroomListResponse getDefaultConversation() throws HttpException {
		String url = getURL("GetChatroomList.aspx");
		String json = JsonMananger.beanToJson(new BaseTokenRequest());
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		ChatroomListResponse q = null;
		if (!TextUtils.isEmpty(result)) {
			q = jsonToBean(result, ChatroomListResponse.class);
		}
		return q;
	}
	
	/**
	 * 根据一组ids 获取 一组用户信息
	 *
	 * @param ids 用户 id 集合
	 * @throws HttpException
	 */
	public GetUserInfosResponse getUserInfos(List<String> ids) throws HttpException {
		String url = getURL("user/batch?");
		StringBuilder sb = new StringBuilder();
		for (String s : ids) {
			sb.append("id=");
			sb.append(s);
			sb.append("&");
		}
		String stringRequest = sb.substring(0, sb.length() - 1);
		String newUrl = url + stringRequest;
		String result = httpManager.get(mContext, newUrl);
		GetUserInfosResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, GetUserInfosResponse.class);
		}
		return response;
	}
	
	/**
	 * 获取版本信息
	 *
	 * @throws HttpException
	 */
	public VersionResponse getSealTalkVersion() throws HttpException {
		String url = getURL("misc/client_version");
		String result = httpManager.get(mContext, url.trim());
		VersionResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, VersionResponse.class);
		}
		return response;
	}
	
	/**
	 * 提交反馈信息
	 *
	 * @throws HttpException
	 */
	public BaseResponse submitFeedBack(String content, List<String> imgUrl) throws HttpException {
		String url = getURL("AddComplain.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("content", content);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		map.put("imgList", imgUrl);
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		BaseResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, BaseResponse.class);
		}
		return response;
	}
	
	public SyncTotalDataResponse syncTotalData(String version) throws HttpException {
		String url = getURL("user/sync/" + version);
		String result = httpManager.get(mContext, url);
		SyncTotalDataResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, SyncTotalDataResponse.class);
		}
		return response;
	}

//    /**
//     * 根据userId去服务器查询好友信息
//     *
//     * @throws HttpException
//     */
//    public GetFriendInfoByIDResponse getFriendInfoByID(String userid) throws HttpException {
//        String url = getURL("friendship/" + userid + "/profile");
//        String result = httpManager.get(url);
//        GetFriendInfoByIDResponse response = null;
//        if (!TextUtils.isEmpty(result)) {
//            response = jsonToBean(result, GetFriendInfoByIDResponse.class);
//        }
//        return response;
//    }
	
	/**
	 * 获取交易记录
	 *
	 * @param index
	 * @param month
	 * @return
	 * @throws HttpException
	 */
	public TransferRecordResponse getTransferRecord(int index, String month, int type) throws HttpException {
		String url = getURL("GetUserBills.aspx");
		Map<String, Object> map = new HashMap<>();
		map.put("index", index);
		map.put("month", month);
		map.put("token", SharedPreferencesContext.getInstance().getToken());
		map.put("type", type);
		String json = JSON.toJSONString(map);
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		TransferRecordResponse response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, TransferRecordResponse.class);
		}
		return response;
	}
	
	/**
	 * 获取交易类型
	 *
	 * @return
	 * @throws HttpException
	 */
	public TransferRecordTypesRes getTransferRecordTypes() throws HttpException {
		String url = getURL("GetBillTypes.aspx");
		String json = JsonMananger.beanToJson(new BaseTokenRequest());
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, ENCODING);
			entity.setContentType(CONTENT_TYPE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
		TransferRecordTypesRes response = null;
		if (!TextUtils.isEmpty(result)) {
			response = jsonToBean(result, TransferRecordTypesRes.class);
		}
		return response;
	}
}
