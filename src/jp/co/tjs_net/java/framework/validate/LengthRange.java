package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public class LengthRange extends Length {

	public LengthRange(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
}
