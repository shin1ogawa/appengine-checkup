package com.shin1ogawa.controller.twitter;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class OauthController extends Controller {

	@Override
	protected Navigation run() {
		try {
			return runInternal();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	Navigation runInternal() throws Exception {
		if (StringUtils.isNotEmpty(asString("oauth_verifier"))) {
			return save(request, response);
		} else {
			return redirect(request, response);
		}
	}


	static final String consumerKey = "DcSwxkuzSkvLAl4ej7dw";

	static final String consumerSecret = "21iRmb3wfRbnbmo9uXJLo4OAyfJqzHd5NQVzm6viY";


	Navigation redirect(HttpServletRequest request, HttpServletResponse response) {
		Twitter client = new Twitter();
		client.setOAuthConsumer(consumerKey, consumerSecret);
		try {
			String requestURL = request.getRequestURL().toString();
			System.out.println("request.getRequestURL()=" + requestURL);
			RequestToken requestToken = client.getOAuthRequestToken(requestURL);
			sessionScope("user", asString("id"));
			sessionScope("requestToken", requestToken);
			String authorizationURL = requestToken.getAuthorizationURL();
			System.out.println("authorizationURL=" + authorizationURL);
			return new Navigation(authorizationURL, true);
		} catch (TwitterException e) {
			Logger.getLogger(OauthController.class.getName()).log(Level.WARNING,
					"failure to get request token.", e);
			return null;
		}
	}

	Navigation save(HttpServletRequest request, HttpServletResponse response) {
		String pin = asString("oauth_verifier");
		Twitter client = new Twitter();
		client.setOAuthConsumer(consumerKey, consumerSecret);
		try {
			String user = super.sessionScope("user");
			RequestToken requestToken = super.sessionScope("requestToken");
			AccessToken accessToken = client.getOAuthAccessToken(requestToken, pin);
			System.out.println("token=" + accessToken.getToken());
			System.out.println("tokenSecret=" + accessToken.getTokenSecret());
			super.request.getSession().invalidate();
			String path =
					request.getRequestURI().replace("oauth", "mentions") + "?user=" + user + "&t="
							+ accessToken.getToken() + "&ts=" + accessToken.getTokenSecret();
			return new Navigation(path, true);
		} catch (TwitterException e) {
			Logger.getLogger(OauthController.class.getName()).log(Level.WARNING,
					"failure to get access token.", e);
			throw new RuntimeException(e);
		}
	}
}
