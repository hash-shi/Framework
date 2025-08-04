package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsGroupRequired extends ValidateBase  {

	public IsGroupRequired(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		boolean result = false;
		String target = "";
		String[] items = this.params.get("item").toString().split(",");

		if (items.length > 0) {
			for (int cnt = 0; cnt < items.length; cnt++) {
				target = req.getParameter(items[cnt]);
				if (target != null && !target.trim().equals("")) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
}
