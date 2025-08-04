package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class EqualLength extends ValidateBase {

	public EqualLength(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		if("".equals(value)){ return true; }
		
		// パラメータ取得
		String type = this.params.get("type").toString();
		int length = Integer.parseInt(this.params.get("length").toString());

		// 埋込文字の設定
		if (type.toLowerCase().equals("full")) {
		} else {
		}
		return checkValue(value, type, length);
	}
	/**
	 * @param value
	 * @param type
	 * @param length
	 * @param operator
	 * @return
	 */
	protected boolean checkValue(String value, String type, int length) throws Exception
	{

		int bytelength = 0;
		
		
		if (type.toLowerCase().equals("half")){
			bytelength = value.getBytes("UTF-8").length;
			if (bytelength == length){
				return true;
			}
			return false;
		}
		else if(type.toLowerCase().equals("full")){
			bytelength = value.getBytes("UTF-8").length;
			if (bytelength == length/2){
				return true;
			}
			return false;
		}
		return false;
	}
}