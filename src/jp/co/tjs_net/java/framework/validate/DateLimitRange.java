package jp.co.tjs_net.java.framework.validate;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class DateLimitRange extends ValidateBase {

	public DateLimitRange(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		Date targetvalue = null;
		Date maxlimitvalue = null;
		Date minlimitvalue = null;
		if (StringUtils.defaultString(value).trim().equals("")){ return true; }
		String style = this.params.get("format").toString();
		SimpleDateFormat format = new SimpleDateFormat(style);
		if (!IsDate(value, style)) {
			return true;
		}
		format.setLenient(false);
		targetvalue = format.parse(value);
		maxlimitvalue = format.parse(this.params.get("max").toString());
		minlimitvalue = format.parse(this.params.get("min").toString());
		if (minlimitvalue.compareTo(targetvalue) <= 0 && targetvalue.compareTo(maxlimitvalue) <= 0) {
			return true;
		}
		return false;
	}
}
