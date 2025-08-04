package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsLength extends ValidateBase {

	public IsLength(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		// パラメータ取得
		String type = this.params.get("type").toString();
		String operator = this.params.get("comparisonoperator").toString();
		int length = Integer.parseInt(this.params.get("length").toString());

		// 埋込文字の設定
		if (type.toLowerCase().equals("full")) {
		} else {
		}
		return checkLength(value, type, length, operator);
	}
	/**
	 * @param value
	 * @param type
	 * @param length
	 * @param operator
	 * @return
	 */
	protected boolean checkLength(String value, String type, int length, String operator) throws Exception
	{
		int bytelength = 0;

		if (type.toLowerCase().equals("half")) {
			bytelength = value.getBytes("UTF-8").length;
		} else if (type.toLowerCase().equals("full")) {
			bytelength = value.getBytes("UTF-8").length;
		}

		if (operator.equals("<")){
			if (bytelength < length) {
				return true;
			}
		} else if (operator.equals("<=")) {
			if (bytelength <= length) {
				return true;
			}
		} else if (operator.equals("=")) {
			if (bytelength == length) {
				return true;
			}
		} else if (operator.equals(">=")) {
			if (bytelength >= length) {
				return true;
			}
		} else if (operator.equals(">")) {
			if (bytelength > length) {
				return true;
			}
		}
		return false;
	}
}
