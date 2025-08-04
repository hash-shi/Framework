package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsNotEquals extends ValidateBase {

	public IsNotEquals(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		String targetvalue = this.params.get("targetvalue").toString();
		String target	= req.getParameter(targetvalue);
		if (!value.equals(target)) {
			return true;
		}
		return false;
	}
}
