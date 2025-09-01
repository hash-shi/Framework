package jp.co.tjs_net.java.framework.validate;

import java.util.Calendar;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsTime extends ValidateBase {

	public IsTime(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		if (StringUtils.defaultString(value).trim().equals("")){ return true; }
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);
		try {
			if (value.length() != 4){ return false; }

			String hour			= value.substring(0,2);
			String minute		= value.substring(2,4);

			if (hour.indexOf("-") != -1){ return false; }
			if (minute.indexOf("-") != -1){ return false; }
			
			int iHour			= Integer.parseInt(hour);
			int iMinute			= Integer.parseInt(minute);
			
			if (iHour < 0){ return false; }
			if (iMinute < 0){ return false; }
			
			cal.set(cal.get(Calendar.YEAR),1,1,iHour,iMinute);
			iHour				= cal.get(Calendar.HOUR_OF_DAY);
			iMinute				= cal.get(Calendar.MINUTE);
		} catch(Exception exp) {
			return false;
		}
		return true; 
	}
}
