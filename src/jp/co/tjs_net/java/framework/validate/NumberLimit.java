package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class NumberLimit extends ValidateBase {

	public NumberLimit(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		String limit = this.params.get("length").toString();
		String operator = this.params.get("comparisonoperator").toString();
		return checkNumberLimit(value, limit, operator);
	}


	/**
	 * @param value
	 * @param limit
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	protected boolean checkNumberLimit(String value, String limit, String operator) throws Exception {
		double targetvalue = 0;
		double limitvalue = 0;

		if (value.trim().equals("")){ return true; }

		if (!IsNumber(value)) {
			return true;
		}
		
		targetvalue = Double.parseDouble(value);
		limitvalue = Double.parseDouble(limit);

		if (operator.equals("<")) {
			if (limitvalue < targetvalue) {
				return true;
			}
		} else if (operator.equals("<=")) {
			if (limitvalue <= targetvalue) {
				return true;
			}
		} else if (operator.equals("=")) {
			if (limitvalue == targetvalue) {
				return true;
			}
		} else if (operator.equals(">=")) {
			if (limitvalue >= targetvalue) {
				return true;
			}
		} else if (operator.equals(">")) {
			if (limitvalue > targetvalue) {
				return true;
			}
		}
		return false;
	}
}
