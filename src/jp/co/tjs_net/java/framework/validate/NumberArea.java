package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public class NumberArea extends NumberLimit {

	public NumberArea(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {

		String from	= StringUtils.defaultString(this.params.get("from")).toString();
		String to	= StringUtils.defaultString(this.params.get("to")).toString();

		// カンマを除去するか否かのパラメータ取得
		String isReplaceComma = this.params.get("isReplaceComma")==null?"":this.params.get("isReplaceComma").toString();
		if ("true".equals(isReplaceComma)){
			from = from.replace(",", "");
			to   = to.replace(",", "");
		}		
		
		if (from.trim().equals("") || to.trim().equals("")) {
			return true;
		}

		if (!IsNumber(from) || !IsNumber(to)) {
			return true;
		}
		String operator = "<=";
		return checkNumberLimit(to, from, operator);
	}
}
