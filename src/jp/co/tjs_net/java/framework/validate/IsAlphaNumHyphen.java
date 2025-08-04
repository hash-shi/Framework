package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsAlphaNumHyphen extends ValidateBase {

	public IsAlphaNumHyphen(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		if("".equals(value)){ return true; }
		return checkValue(value);
	}
	/**
	 * @param value
	 * @return
	 */
	protected boolean checkValue(String value) throws Exception {

		if(value.matches("^[0-9a-zA-Z-]+$")){
			return true;
		}
		return false;
	}
}