package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsRequired extends ValidateBase {
	
	public IsRequired(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) {
		
		value = StringUtils.defaultString(value);
		if (value.trim().equals("")){
			return false;
		} else {
			return true;
		}
	}
}
