package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MaxTextarea extends Length {

	public MaxTextarea(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		if (StringUtils.defaultString(value).trim().equals("")){ return true;}
		String type		= this.params.get("type");
		String operator = "<=";
		int length		= Integer.parseInt(this.params.get("length"));
		int linage		= Integer.parseInt(this.params.get("linage"));
		value					= value.replaceAll("\r\n", "\n");
		value					= value.replaceAll("\r", "");
		String[] valueLines		= value.split("\n");
		if (valueLines.length > linage) {
			return false;
		}
		for (int count = 0 ; count < valueLines.length ; count++ ){
			String tempValue		= valueLines[count];
			if (!checkLength(tempValue, type, length, operator)){
				return false;
			}
		}
		return true;
	}
}

