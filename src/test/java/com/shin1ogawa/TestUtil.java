package com.shin1ogawa;

import java.io.File;

import com.google.appengine.tools.development.ApiProxyLocal;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;

/**
 * AppEngine環境でのテストを行うためのユーティリティクラス。
 * @author shin1ogawa
 */
public class TestUtil {

	private TestUtil() {
	}


	/** テストに使用するためのユーザID */
	public static String TESTUSERID;


	/**
	 * テスト用のAppEngine環境を起動する。
	 * @param testFolder
	 */
	public static void setUpAppEngine(File testFolder) {
		ApiProxyLocal apiProxyLocal = new ApiProxyLocalImpl(testFolder) {
		};
		Environment environment = new UnitTestEnvironment();
		ApiProxy.setEnvironmentForCurrentThread(environment);
		ApiProxy.setDelegate(apiProxyLocal);
	}

	/**
	 * テスト用のAppEngine環境を終了する。
	 */
	public static void tearDownAppEngine() {
		ApiProxyLocal apiProxyLocal = (ApiProxyLocal) ApiProxy.getDelegate();
		if (apiProxyLocal != null) {
			apiProxyLocal.stop();
		}
		ApiProxy.setDelegate(null);
		ApiProxy.setEnvironmentForCurrentThread(null);
	}
}
