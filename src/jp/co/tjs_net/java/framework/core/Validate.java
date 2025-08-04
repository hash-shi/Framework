package jp.co.tjs_net.java.framework.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.Define;
import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;
import jp.co.tjs_net.java.framework.information.ValidateResult;
import jp.co.tjs_net.java.framework.information.ValidateVariable;
import jp.co.tjs_net.java.framework.information.ValidateVariableCheck;
import jp.co.tjs_net.java.framework.information.ValidateVariableCheck.CHECK_TYPE;

public class Validate {
	
	// メンバ変数定義
	private ArrayList<ValidateVariable> variables;
	private boolean validateResult;
	
	/**
	 * 
	 */
	public Validate(){
		this.variables			= new ArrayList<>();
		this.validateResult		= true;
	}
	
	/**
	 * @param variable
	 */
	public void addVariable(ValidateVariable variable){
		this.variables.add(variable);
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void validate(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info){
		
		boolean result = false;
		
		//======================================================================
		// 変数１つずつをチェックしていく
		//======================================================================
		for (int countVariable = 0 ; countVariable < variables.size() ; countVariable++){
			
			// 変数情報の取得
			ValidateVariable variable		= variables.get(countVariable);
			String value					= req.getParameter(variable.getName());

			// 入力値チェック結果格納領域の定義
			ValidateResult validateResult	= new ValidateResult(variable.getName(), value, variable.getDispName());
			
			// 入力値チェック分だけループする
			for(int countCheck = 0 ; countCheck < variable.getChecks().size() ; countCheck++){
				
				// 入力値チェックの内容を取得する
				ValidateVariableCheck check	= variable.getChecks().get(countCheck);
			
				// 入力値チェッククラスを動的に生成する
				String validateClassName				= null;
				Class<?> validateClass					= null;
				Constructor<ValidateBase> validate		= null;
				ValidateBase validateModule 			= null;
				
				validateClassName						= info.config.getValidateBaseClass() + "." + check.getClassName();
				validateClass							= index.getClass(validateClassName);
				if (validateClass == null){
					validateClassName					= null;
					validateClassName					= Define.FRAMEWORK_PACKAGE + ".validate." + check.getClassName();
					validateClass						= index.getClass(validateClassName);
				}
				
				// クラスが取得できた場合は、入力値チェックを実施する
				if (validateClass != null){
					try {
						Class<?>[] constructorTypes			= { HttpServletRequest.class, HttpServletResponse.class, IndexInformation.class };
						validate							= (Constructor<ValidateBase>) validateClass.getConstructor(constructorTypes);
						Object[] constructorArgs			= { req, res, info };
						validateModule						= (ValidateBase)validate.newInstance(constructorArgs);
						validateModule.setParams(check.getParams());
					} catch (Exception exp){ validateModule	= null; }
					finally {}
				} else {
					validateModule						= null;
				}			
				
				// チェッククラスのインスタンスが生成できた場合は、そのチェックインスタンスを使って入力値チェックを実施する
				if (validateModule != null){
					try {
						result = false;
						result							= validateModule.doValidate(req, res, value, info);		// 入力値チェック処理
						String message					= "";													// 標準メッセージの取得
						
						// 入力値チェック処理内でメッセージを生成された場合は、標準メッセージではなく、そちらを採用する
						if (validateModule.getValidateMessages().size()==0){
							message						= check.getMessage();
						} else {
							message						= "";
							for (int count = 0 ; count < validateModule.getValidateMessages().size() ; count++ ){
								message					+= (count==0?"":"\n") + validateModule.getValidateMessages().get(count);
							}
						}
						
						if (validateModule.getOtherValidateResult().size()!=0){
							for (int count = 0 ; count < validateModule.getOtherValidateResult().size() ; count++ ){
								info.addValidateResult(validateModule.getOtherValidateResult().get(count).getForClient());								
							}
						}
						
						if (result == false && !"".equals(message)){
							validateResult.setResult(result);
							validateResult.setWarning(check.getCheckType().equals(CHECK_TYPE.ERROR)?false:true);
							validateResult.setMessage(message);
							break;
						}
					} catch (Exception exp){
					} finally {
						// 基本的に外部コネクションの開放は信用しないので、ここで切断する
						info.closeOtherConnections();
					}
				}
			}

			// 結果を格納していく
			info.addValidateResult(validateResult.getForClient());
			
			// 20250710-エラーが発生した時点でチェックを終了する。
			if (result == false) { break; }
		}
	}
	
	/**
	 * @return
	 */
	public boolean isValidateResult() {
		return validateResult;
	}
}
