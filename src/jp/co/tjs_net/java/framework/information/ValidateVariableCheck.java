package jp.co.tjs_net.java.framework.information;

import java.util.HashMap;

public class ValidateVariableCheck {
	
	/**
	 * エラー種別
	 * @author toshiyuki
	 *
	 */
	public enum CHECK_TYPE {
		 ERROR
		,WARNING
	}
	
	// メンバ変数
	private CHECK_TYPE checkType;					// エラー種別
	private String className;						// エラーチェッククラス(ValidateBaseクラスを継承していること)
	private HashMap<String, String> params;			// エラーチェッククラスに渡すパラメータ
	private boolean result;							// 結果格納領域
	private String defaultMessage;					// 何も指定しない場合の標準エラーメッセージ
	private String message;							// 結果に対するメッセージ格納領域
	
	/**
	 * @param className
	 */
	public ValidateVariableCheck(CHECK_TYPE checkType, String className, String defaultMessage){
		this.checkType			= checkType;
		this.className			= className;
		this.params				= new HashMap<>();
		this.result				= true;
		this.defaultMessage		= defaultMessage;
		this.message			= "";
	}
	public void addParam(String name, String value){ this.params.put(name, value); }
	public String getClassName() { return className; }
	public HashMap<String, String> getParams() { return params; }
	public CHECK_TYPE getCheckType() {
		return checkType;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	
	public String getMessage() {
		if (!"".equals(this.message)){
			return this.message;
		} else {
			return this.defaultMessage;
		}
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
