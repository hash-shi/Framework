package jp.co.tjs_net.java.framework.validate;

import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsRegexpFormat extends ValidateBase {

	public IsRegexpFormat(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		return checkRegexpFormat(value, this.params.get("regexpformat").toString());
	}

	protected boolean checkRegexpFormat(String value, String regexpformat)
	{
		if (value.trim().equals("")){ return true; }
		if (Pattern.matches(regexpformat, value)) {
			return true;
		}
		return false;
	}
}
