package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsNumber extends ValidateBase {

	public IsNumber(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		if (StringUtils.defaultString(value).trim().equals("")){ return true; }
		
		// カンマを除去するか否かのパラメータ取得
		String isReplaceComma = this.params.get("isReplaceComma")==null?"":this.params.get("isReplaceComma").toString();
		if ("true".equals(isReplaceComma)){
			value = value.replace(",", "");
		}		
		return this.IsNumber(value);
	}
}
