package io.rong.app;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.GuoGuo.JuicyChat.server.pinyin.CharacterParser;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
	public ApplicationTest() {
		super(Application.class);
		CharacterParser mCharacterParser = CharacterParser.getInstance();
		String aaa = mCharacterParser.convert("你好");
		String bbb = mCharacterParser.getSpelling("呵呵");
		System.out.println(aaa + bbb);
	}
}