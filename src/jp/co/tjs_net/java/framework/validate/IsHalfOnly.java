package jp.co.tjs_net.java.framework.validate;

import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsHalfOnly extends ValidateBase {

	public IsHalfOnly(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
        if (value == null || value.equals("")) {
            return true;
        }
        String regText = "[ -~｡-ﾟ]+";
        Pattern pattern = Pattern.compile(regText);
        return pattern.matcher(value).matches();
	}
}
