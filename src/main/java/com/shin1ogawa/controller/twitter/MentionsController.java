package com.shin1ogawa.controller.twitter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MentionsController extends Controller {

	@Override
	protected Navigation run() {
		try {
			return runInternal();
		} catch (TwitterException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Navigation runInternal() throws TwitterException, IOException {
		Twitter client = new Twitter();
		client.setOAuthConsumer(OauthController.consumerKey, OauthController.consumerSecret);
		String accessToken = asString("t");
		String accessTokenSecret = asString("ts");
		client.setOAuthAccessToken(accessToken, accessTokenSecret);
		PrintWriter w = response.getWriter();
		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		List<Status> mentions = client.getMentions();
		for (Status status : mentions) {
			w.println(status.getUser().getScreenName() + "/" + status.getCreatedAt());
			w.println(status.getText());
			w.println();
		}
		return null;
	}
}
