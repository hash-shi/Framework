package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsTelephone extends IsRegexpFormat {

	public IsTelephone(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		return checkRegexpFormat(value, "[\\d]{1,4}?-[\\d]{1,4}?-[\\d]{1,4}");
	}
}
