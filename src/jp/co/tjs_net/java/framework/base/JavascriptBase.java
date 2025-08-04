package jp.co.tjs_net.java.framework.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public abstract class JavascriptBase extends FrameworkBase {
	public JavascriptBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
}
