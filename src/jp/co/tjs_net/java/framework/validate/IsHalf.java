package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsHalf extends ValidateBase {

	public IsHalf(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		if (StringUtils.defaultString(value).trim().equals("")){ return true; }
		int length;
		byte[] byteLength;
		length		= value.length();
		byteLength	= value.getBytes();
		if (length != byteLength.length){
			return false;
		}
		return true;
	}
}
