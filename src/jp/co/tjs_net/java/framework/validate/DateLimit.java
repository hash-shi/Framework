package jp.co.tjs_net.java.framework.validate;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class DateLimit extends ValidateBase {

	public DateLimit(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		String limit = this.params.get("length").toString();
		String operator = this.params.get("comparisonoperator").toString();
		String style = this.params.get("format").toString();
		return checkDateLimit(value, limit, operator, style);
	}

	/**
	 * @param value
	 * @param limit
	 * @param operator
	 * @param style
	 * @return
	 * @throws Exception
	 */
	protected boolean checkDateLimit(String value, String limit, String operator, String style) throws Exception {

		Date targetvalue = null;
		Date limitvalue = null;
		
		if (StringUtils.defaultString(value).trim().equals("")){ return true; }

		SimpleDateFormat format = new SimpleDateFormat(style);

		if (!IsDate(value, style)) {
			return true;
		}

		format.setLenient(false);

		targetvalue = format.parse(value);
		limitvalue = format.parse(limit);

		if (operator.equals("<")) {
			if (limitvalue.compareTo(targetvalue) < 0) {
				return true;
			}
		} else if (operator.equals("<=")) {
			if (limitvalue.compareTo(targetvalue) <= 0) {
				return true;
			}
		} else if (operator.equals("=")) {
			if (limitvalue.compareTo(targetvalue) == 0) {
				return true;
			}
		} else if (operator.equals(">=")) {
			if (limitvalue.compareTo(targetvalue) >= 0) {
				return true;
			}
		} else if (operator.equals(">")) {
			if (limitvalue.compareTo(targetvalue) > 0) {
				return true;
			}
		}
		return false;
	}
}
