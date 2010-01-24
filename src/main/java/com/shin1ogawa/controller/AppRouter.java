package com.shin1ogawa.controller;

import org.slim3.controller.router.RouterImpl;

public class AppRouter extends RouterImpl {

	public AppRouter() {
		super();
		addRouting("/_ah/mail/{address}", "/mail/receive?address={address}");
	}
}
