package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MinDateLimit extends DateLimit {

	public MinDateLimit(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		String limit = this.params.get("limit").toString();
		String operator = "<=";
		String style = this.params.get("format").toString(); 
		return checkDateLimit(value, limit, operator, style);
	}
}
