package jp.co.tjs_net.java.framework.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;
import jp.co.tjs_net.java.framework.information.ValidateResult;

public abstract class ValidateBase extends FrameworkBase {
	
	public ValidateBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
		this.validateMessages			= new ArrayList<>();
		this.otherValidateResults		= new ArrayList<>();
	}
	
	// XMLから渡されたパラメータの格納
	protected HashMap<String, String> params;
	public void setParams(HashMap<String, String> params) { this.params = params; }
	
	// 返却メッセージのカスタマイズ
	private ArrayList<String> validateMessages;
	protected void addValidateMessage(String validateMessage){ this.validateMessages.add(validateMessage); }
	public ArrayList<String> getValidateMessages(){ return this.validateMessages; }	
	
	// 他の入力欄の制御も実施する
	private ArrayList<ValidateResult> otherValidateResults;
	protected void addOtherValidateResult(ValidateResult validateResult){ this.otherValidateResults.add(validateResult); }
	public ArrayList<ValidateResult> getOtherValidateResult(){ return this.otherValidateResults; }	
	
	// このメソッドをオーバーライドして入力値チェック処理を記述する
	public abstract boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception;
	
	// doRunはこの場合利用しない為、ダミー関数で上書き
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {}
	
	// 基本的なチェックの提供
	protected boolean IsNumber(String value) {
		try {
			Double.parseDouble(value);
			if (value.substring(value.length()-1).equals(".")){ return false; }
			return true;
		} catch (NumberFormatException e){
			return false;
		}
	}
	
	/**
	 * @param value
	 * @param style
	 * @return
	 */
	protected boolean IsDate(String value, String style) {
		SimpleDateFormat format = new SimpleDateFormat(style);
		try {
			format.setLenient(false);
			format.parse(value);
			return true;
		} catch(ParseException e) {
			return false;
		}
	}
}
