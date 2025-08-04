package jp.co.tjs_net.java.framework.core;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class ValidateFactory {
	// メンバ変数定義
	private Class<?> validateClass;
	private ValidateBase validateObject;
	private IndexInformation info;
	
	/**
	 * @param validateClass
	 * @param req
	 * @param res
	 * @param info
	 */
	@SuppressWarnings("unchecked")
	public ValidateFactory(Class<?> validateClass, HttpServletRequest req, HttpServletResponse res, IndexInformation info){
		this.validateClass						= validateClass;
		this.info								= info;
		Class<?>[] constTypes					= {HttpServletRequest.class, HttpServletResponse.class, IndexInformation.class};
		Constructor<ValidateBase> constructor	= null;
		try{		
			constructor							= (Constructor<ValidateBase>) this.validateClass.getConstructor(constTypes);
			Object[] constArgs					= { req, res, info };
			this.validateObject					= constructor.newInstance(constArgs);
		} catch (Exception exp){
			this.validateObject					= null;
		}		
	}
	
	/**
	 * @param value
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public boolean check(String value, HashMap<String, String> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (this.validateObject == null){ return false; }
		if (params != null){ this.validateObject.setParams(params); }
		return this.validateObject.doValidate(req, res, value, info);
	}
}
