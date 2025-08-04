package jp.co.tjs_net.java.framework.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsRegex extends ValidateBase {

	public IsRegex(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		String format = this.params.get("format").toString();
		Pattern p = Pattern.compile(format);
		Matcher m = p.matcher(value);
		if (m.find()) {
			return true;
		}
		return false;
	}
}
