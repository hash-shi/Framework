package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class NumberLimitRange extends ValidateBase {

	public NumberLimitRange(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		double targetvalue = 0;
		double maxlimitvalue = 0;
		double minlimitvalue = 0;

		String maxlimit = this.params.get("max").toString();
		String minlimit = this.params.get("min").toString();

		if (StringUtils.defaultString(value).trim().equals("")){ return true; }

		if (!IsNumber(value)){
			return true;
		}
		
		targetvalue = Double.parseDouble(value);
		maxlimitvalue = Double.parseDouble(maxlimit);
		minlimitvalue = Double.parseDouble(minlimit);

		if (minlimitvalue <= targetvalue && targetvalue <= maxlimitvalue){
			return true;
		}
		return false;
	}
}
