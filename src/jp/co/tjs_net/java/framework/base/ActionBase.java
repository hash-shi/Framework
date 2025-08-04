package jp.co.tjs_net.java.framework.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public abstract class ActionBase extends FrameworkBase {
	private String view;
	public ActionBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	public String getView() { return view; }
	public void setView(String view) { this.view = view; }
}
