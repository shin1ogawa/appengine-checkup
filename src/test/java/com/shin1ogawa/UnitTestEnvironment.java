package com.shin1ogawa;

/**
 * 単体テスト用の{@link com.google.apphosting.api.ApiProxy.Environment}の実装.
 * @author shin1ogawa
 */
public class UnitTestEnvironment implements com.google.apphosting.api.ApiProxy.Environment {

	public String getAppId() {
		return "memories-album-server";
	}

	public String getVersionId() {
		return "unittest";
	}

	public String getRequestNamespace() {
		return "";
	}

	public String getAuthDomain() {
		return "gmail.com";
	}

	public boolean isLoggedIn() {
		return true;
	}

	public String getEmail() {
		return "unittest@gmail.com";
	}

	public boolean isAdmin() {
		return true;
	}

	public java.util.Map<String, Object> getAttributes() {
		java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
		map.put("com.google.appengine.server_url_key", "dummy");
		return map;
	}
}
