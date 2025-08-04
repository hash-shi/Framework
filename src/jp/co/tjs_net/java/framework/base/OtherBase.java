package jp.co.tjs_net.java.framework.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.core.Index;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public abstract class OtherBase extends FrameworkBase {
	public OtherBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	public abstract void response(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception;
}
