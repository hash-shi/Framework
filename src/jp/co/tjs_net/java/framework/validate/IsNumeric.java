package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsNumeric extends ValidateBase {

	public IsNumeric(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		if("".equals(StringUtils.defaultString(value))){ return true; }
		
		// パラメータ取得
	//	String type = this.params.get("type").toString();
		

		// 埋込文字の設定
	//	if (type.toLowerCase().equals("full")) {
	//	} else {
	//	}
		return checkValue(value);
	}
	/**
	 * @param value
	 * @return
	 */
	protected boolean checkValue(String value) throws Exception
	{
		
		if(value.matches("^[0-9]*$")){
			return true;
		}
		return false;
	}
}