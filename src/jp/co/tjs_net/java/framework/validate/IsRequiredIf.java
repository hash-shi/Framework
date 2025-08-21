package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IsRequiredIf extends ValidateBase {
	
	public IsRequiredIf(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) {
		
		// 特定の項目に値がある場合のみ必須チェックを実施する。
		
		// 条件となる項目
		String target = null;
		if (this.params.get("targetvalue") != null) {
			target = this.params.get("targetvalue").toString();
			target	= req.getParameter(target).trim();
		}
		
		// 条件となる値
		String condition = null;
		if (this.params.get("conditionvalue") != null) {
			condition = this.params.get("conditionvalue").toString().trim();
		}
		
		// 条件がない場合、通常の必須として扱う
		if (target == null && condition == null) {
			value = StringUtils.defaultString(value);
			if (value.trim().equals("")){
				return false;
			} else {
				return true;
			}
		}
		
		// 条件が両方ある場合、target = conditionの時だけ必須
		else if (target != null && condition != null) {
			if (target.equals(condition)) {
				value = StringUtils.defaultString(value);
				if (value.trim().equals("")){
					return false;
				} else {
					return true;
				}
			}
		}
		
		// 条件がtargetのみの場合、targetに値がある場合のみ必須
		else if (target != null && condition == null) {
			if (!target.equals("")) {
				value = StringUtils.defaultString(value);
				if (value.trim().equals("")){
					return false;
				} else {
					return true;
				}
			}
		}
		
		return true;
	}
}
