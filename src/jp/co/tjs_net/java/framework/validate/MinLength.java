package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MinLength extends Length {

	public MinLength(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		String type = this.params.get("type").toString();
		String operator = ">=";
		int length = Integer.parseInt(this.params.get("length").toString());
		return checkLength(value, type, length, operator);
	}
}
