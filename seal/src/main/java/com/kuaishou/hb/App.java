package com.kuaishou.hb;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.facebook.stetho.inspector.database.DefaultDatabaseConnectionProvider;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;
import com.kuaishou.hb.message.TestMessage;
import com.kuaishou.hb.message.provider.ContactNotificationMessageProvider;
import com.kuaishou.hb.message.provider.GGRedPacketMessageProvider;
import com.kuaishou.hb.message.provider.GGVideoFileMessageProvider;
import com.kuaishou.hb.message.provider.RedPacketNotificationMessageProvider;
import com.kuaishou.hb.message.provider.SealConversationProvider;
import com.kuaishou.hb.message.provider.TestMessageProvider;
import com.kuaishou.hb.model.GGRedPacketMessage;
import com.kuaishou.hb.model.GGRedPacketNotifyMessage;
import com.kuaishou.hb.model.GGVideoFileMessage;
import com.kuaishou.hb.server.utils.NLog;
import com.kuaishou.hb.stetho.RongDatabaseDriver;
import com.kuaishou.hb.stetho.RongDatabaseFilesProvider;
import com.kuaishou.hb.stetho.RongDbFilesDumperPlugin;
import com.kuaishou.hb.utils.SharedPreferencesContext;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.display.FadeInBitmapDisplayer;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.RealTimeLocationMessageProvider;
import io.rong.imlib.ipc.RongExceptionHandler;
import io.rong.push.RongPushClient;
import io.rong.push.common.RongException;


public class App extends MultiDexApplication {

	private static DisplayImageOptions options;

	public static App instance;

	@Override
	public void onCreate() {

		super.onCreate();
		instance = this;
		Stetho.initialize(new Stetho.Initializer(this) {
			@Override
			protected Iterable<DumperPlugin> getDumperPlugins() {
				return new Stetho.DefaultDumperPluginsBuilder(App.this)
						.provide(new RongDbFilesDumperPlugin(App.this, new RongDatabaseFilesProvider(App.this)))
						.finish();
			}

			@Override
			protected Iterable<ChromeDevtoolsDomain> getInspectorModules() {
				Stetho.DefaultInspectorModulesBuilder defaultInspectorModulesBuilder = new Stetho.DefaultInspectorModulesBuilder(App.this);
				defaultInspectorModulesBuilder.provideDatabaseDriver(new RongDatabaseDriver(App.this, new RongDatabaseFilesProvider(App.this), new DefaultDatabaseConnectionProvider()));
				return defaultInspectorModulesBuilder.finish();
			}
		});
		
		PlatformConfig.setWeixin("wxb142990f0ab9b6e4", "175e0b0e678ca6f401a410890cb0e791");
		PlatformConfig.setQQZone("1106453231", "GFidPnZtmuRY8Is2");
		UMShareAPI.get(this);

		if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

//            LeakCanary.install(this);//内存泄露检测
			RongPushClient.registerHWPush(this);
			RongPushClient.registerMiPush(this, "2882303761517583379", "5741758393379");
			try {
				RongPushClient.registerGCM(this);
			} catch (RongException e) {
				e.printStackTrace();
			}

			/**
			 * 注意：
			 *
			 * IMKit SDK调用第一步 初始化
			 *
			 * context上下文
			 *
			 * 只有两个进程需要初始化，主进程和 push 进程
			 */
			RongIM.init(this);
//            RongIM.setServerInfo("nav.cn.ronghub.com", "img.cn.ronghub.com");
			NLog.setDebug(true);//Seal Module Log 开关
			SealAppContext.init(this);

			SharedPreferencesContext.init(this);
			Utils.init(this);
			Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));

			try {
				RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
				RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());
				RongIM.registerMessageType(TestMessage.class);
				RongIM.registerMessageType(GGRedPacketMessage.class);
				RongIM.registerMessageTemplate(new GGRedPacketMessageProvider());
				RongIM.registerMessageTemplate(new GGVideoFileMessageProvider());
				RongIM.registerMessageType(GGVideoFileMessage.class);
				RongIM.registerMessageTemplate(new TestMessageProvider());
				RongIM.registerMessageType(GGRedPacketNotifyMessage.class);
				RongIM.registerMessageTemplate(new RedPacketNotificationMessageProvider());

				RongIM.getInstance().registerConversationTemplate(new SealConversationProvider());
			} catch (Exception e) {
				e.printStackTrace();
			}

			openSealDBIfHasCachedToken();

			options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.de_default_portrait)
					.showImageOnFail(R.drawable.de_default_portrait)
					.showImageOnLoading(R.drawable.de_default_portrait)
					.displayer(new FadeInBitmapDisplayer(300))
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.build();
//			setMyExtensionModule();
//            RongExtensionManager.getInstance().registerExtensionModule(new PTTExtensionModule(this, true, 1000 * 60));
		}
	}

	public static DisplayImageOptions getOptions() {
		return options;
	}

	private void openSealDBIfHasCachedToken() {
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		String cachedToken = sp.getString("loginToken", "");
		if (!TextUtils.isEmpty(cachedToken)) {
			String current = getCurProcessName(this);
			String mainProcessName = getPackageName();
			if (mainProcessName.equals(current)) {
				SealUserInfoManager.getInstance().openDB();
			}
		}
	}

	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

}
