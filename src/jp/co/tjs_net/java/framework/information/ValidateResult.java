package jp.co.tjs_net.java.framework.information;

import java.util.HashMap;

/**
 * @author toshiyuki
 *
 */
public class ValidateResult {
	
	// メンバ変数定義
	private String paramName;					// パラメータのName
	private String paramValue;					// パラメータの入力値
	private String paramDispName;				// パラメータのラベル名称
	private boolean result;						// 入力値チェック結果
	private boolean isWarning;					// 入力値チェック結果がfalseの場合、それは警告扱いか？
	private String message;						// メッセージ
	private HashMap<String, Object> forClient;	// クライアント引渡し用
	
	public ValidateResult(String paramName, String paramValue, String paramDispName){
		this.paramName		= paramName;
		this.paramValue		= paramValue;
		this.paramDispName	= paramDispName;
		this.result			= true;
		this.isWarning		= false;
		this.message		= null;
		this.forClient		= new HashMap<>();
	}
	
	public ValidateResult(String paramName, String paramValue, String paramDispName, boolean result, boolean isWarning){
		this.paramName		= paramName;
		this.paramValue		= paramValue;
		this.paramDispName	= paramDispName;
		this.result			= result;
		this.isWarning		= isWarning;
		this.message		= null;
		this.forClient		= new HashMap<>();
	}
	
	public void setParamName(String paramName) { this.paramName = paramName; }
	public void setParamValue(String paramValue) { this.paramValue = paramValue; }
	public void setParamDispName(String paramDispName) { this.paramDispName = paramDispName; }
	public void setResult(boolean result) { this.result = result; }
	public void setWarning(boolean isWarning) { this.isWarning = isWarning; }
	public void setMessage(String message) { this.message = message; } 
	public HashMap<String, Object> getForClient(){
		this.forClient.put("paramName"		, this.paramName);
		this.forClient.put("paramValue"		, this.paramValue);
		this.forClient.put("paramDispName"	, this.paramDispName);
		this.forClient.put("result"			, this.result);
		this.forClient.put("isWarning"		, this.isWarning);
		this.forClient.put("message"		, this.message);
		return this.forClient;
	}
}
