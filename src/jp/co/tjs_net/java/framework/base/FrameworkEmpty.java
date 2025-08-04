package jp.co.tjs_net.java.framework.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public class FrameworkEmpty extends FrameworkBase {
	public FrameworkEmpty(HttpServletRequest req, HttpServletResponse res, IndexInformation info) { super(req, res, info); }
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {}
}
