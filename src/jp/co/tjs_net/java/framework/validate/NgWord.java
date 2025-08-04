package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class NgWord extends ValidateBase {

	public NgWord(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		char target;
		String ngword = this.params.get("ngword").toString();
		if (value.trim().equals("")){ return true; }
		for (int cnt = 0; cnt < value.length(); cnt++) {
			target = value.charAt(cnt);
			if (ngword.indexOf(target) > -1) {
				return false;
			}
		}
		return true;
	}
}
