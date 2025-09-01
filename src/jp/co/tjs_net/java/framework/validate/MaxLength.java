package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MaxLength extends Length {

	public MaxLength(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		if (StringUtils.defaultString(value).trim().equals("")){ return true; }
		String type = this.params.get("type").toString();
		String operator = "<=";
		int length = Integer.parseInt(this.params.get("length").toString());
		return checkLength(value, type, length, operator);
	}
}
