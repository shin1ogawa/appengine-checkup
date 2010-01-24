package com.shin1ogawa.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class IndexController extends Controller {

	@Override
	protected Navigation run() {
		try {
			runInternal();
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void runInternal() throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		PrintWriter w = response.getWriter();
		w.println("色々実験するアプリ");
		response.flushBuffer();
	}
}
