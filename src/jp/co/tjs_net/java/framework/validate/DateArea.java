package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.information.IndexInformation;


public class DateArea extends DateLimit {
	public DateArea(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		String from = StringUtils.defaultString(this.params.get("from"));
		String to = StringUtils.defaultString(this.params.get("to"));
		
		if (from.trim().equals("") || to.trim().equals("")) {
			return true;
		}
		String style = this.params.get("format").toString();
		if (!IsDate(this.params.get("from").toString(), style) || !IsDate(this.params.get("to").toString(), style)) {
			return true;
		}
//		String from = this.params.get("from").toString();
//		String to = this.params.get("to").toString();
		String operator = "<=";
		return checkDateLimit(to, from, operator, style);
	}
}
