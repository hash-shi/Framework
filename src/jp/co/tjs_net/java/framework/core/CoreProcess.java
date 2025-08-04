package jp.co.tjs_net.java.framework.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.Define;
import jp.co.tjs_net.java.framework.Define.MODE;
import jp.co.tjs_net.java.framework.base.ActionBase;
import jp.co.tjs_net.java.framework.base.FrameworkBase;
import jp.co.tjs_net.java.framework.common.Common;
import jp.co.tjs_net.java.framework.common.RecursiveNode;
import jp.co.tjs_net.java.framework.information.ControllerObjectBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;
import jp.co.tjs_net.java.framework.information.ValidateVariable;
import jp.co.tjs_net.java.framework.information.ValidateVariableCheck;
import jp.co.tjs_net.java.framework.information.ValidateVariableCheck.CHECK_TYPE;

public class CoreProcess extends CoreBase {
	
	// メンバ変数
	private ProcessInfo processInfo;
	
	/**
	 * 
	 */
	public CoreProcess(){
		processInfo		= new ProcessInfo();
	}
	
	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#getController(lips.fw.fsapp.information.IndexInformation)
	 */
	@Override
	public ControllerObjectBase getController(IndexInformation info) {
		if (info.viewLayer.length == 1){
			return info.config.getController().getInfo().get(MODE.ACTION).get(info.nowAction);
		} else {
			return info.config.getController().getInfo().get(MODE.DIALOG).get(info.nowAction);
		}
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#getModuleClassName(lips.fw.fsapp.information.IndexInformation)
	 */
	@Override
	public String getModuleClassName(IndexInformation info) {
		if (info.viewLayer.length == 1){
			return info.config.getController().getInfo().get(MODE.ACTION).get(info.nowAction).getClassName();
		} else {
			return info.config.getController().getInfo().get(MODE.DIALOG).get(info.nowAction).getClassName();
		}			
	}
	
	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#init(lips.fw.fsapp.core.Index, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.IndexInformation)
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public void init(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {

		//---------------------------------------------------------------------
		// トークンチェック
		//---------------------------------------------------------------------
		if (info.config.useTokenCheck()){
			String checkToken		= "";
			String serverToken		= "";
			if (info.viewLayer.length == 1){
				// 画面の場合
				checkToken							= info.token;
				serverToken							= (String)req.getSession().getAttribute(Define.FRAMEWORK_NAME + "_TOKEN");
			} else {
				// ダイアログの場合
				String nowDialogUUID				= info.uuids[info.viewLayer.length-1];
				checkToken							= req.getParameter(nowDialogUUID);
				HashMap<String,String> dialogTokens	= (HashMap<String,String>)req.getSession().getAttribute(Define.FRAMEWORK_NAME + "_DIALOG_TOKENS");
				serverToken							= dialogTokens.get(nowDialogUUID);
			}
			if (!checkToken.equals(serverToken)){
				throw new Exception(info.config.getSystemMessage("_SYS_ERROR_002"));
			}
		}

		//---------------------------------------------------------------------
		// 入力値チェック
		//---------------------------------------------------------------------
		// 入力値チェック用XMLパスの取得
		String validateXMLPath			= req.getServletContext().getRealPath("/") + info.config.getValidateXMLPath() + info.nowAction + ".xml";
		File validateXMLFile			= new File(validateXMLPath);
		boolean ignoreWarning			= (req.getParameter("__ignoreWarning")==null?false:(req.getParameter("__ignoreWarning").toString().toLowerCase().equals("true")?true:false));

		// 入力値チェック用XMLファイルの存在チェック
		if (validateXMLFile.exists()){
			FileInputStream validateXMLIS	= new FileInputStream(validateXMLFile.getPath());
			try {

				RecursiveNode validateProcXML	= null;
				RecursiveNode validateXML		= RecursiveNode.parse(validateXMLIS).n("validate");
				
				// Proc定義の中からチェックすべきProc名を検索する
				for (int count = 0 ; count < validateXML.count("proc") ; count++ ){
					String checkFunc	= (validateXML.n("proc", count).getAttributes().getNamedItem("func")==null?"":validateXML.n("proc", count).getAttributes().getNamedItem("func").getTextContent());
					String[] checkFuncs	= checkFunc.split(",");
					for (int countFunc = 0 ; countFunc < checkFuncs.length ; countFunc++){
						if (checkFuncs[countFunc].equals(info.paramID)){
							validateProcXML = validateXML.n("proc", count);
							break;
						}
					}
					if (validateProcXML != null){ break; }
				}

				// Proc定義が見つかった場合は、その中に定義されている入力値チェック情報を取得する
				if (validateProcXML != null){

					HashMap<String, String> replaceDefinition	= new HashMap<>();

					for (int count = 0 ; count < validateProcXML.count("variable") ; count++){

						RecursiveNode variableXML			= validateProcXML.n("variable", count);

						//-----------------------------------------
						// 変数が単品かテーブル系か判定
						//-----------------------------------------
						boolean isVariableSingle			= true;
						if (variableXML.getAttributes().getNamedItem("type") != null){
							String variableType				= variableXML.getAttributes().getNamedItem("type").getTextContent();
							if ("repetition".equals(variableType.toLowerCase())){ isVariableSingle = false; }
						}
						if (isVariableSingle == true){
							
							//-------------------------------------
							// 変数単品チェック
							//-------------------------------------
							replaceDefinition.clear();
							ValidateVariable validateVariable	= this.variableXml2Class(index, req, res, info, variableXML, ignoreWarning, replaceDefinition);
							if (validateVariable != null){ processInfo.validate.addVariable(validateVariable); }
							
						} else {
							
							//-------------------------------------
							// 変数複数品チェック
							//-------------------------------------
							String repetitionFromString			= variableXML.getAttributes().getNamedItem("from")==null?"-1":variableXML.getAttributes().getNamedItem("from").getTextContent();
							String repetitionToString			= variableXML.getAttributes().getNamedItem("to")==null  ?"-1":variableXML.getAttributes().getNamedItem("to").getTextContent();
							int repetitionFrom					= -1;
							int repetitionTo					= -1;
							boolean emptySkip					= (variableXML.getAttributes().getNamedItem("emptySkip") == null ? false : variableXML.getAttributes().getNamedItem("emptySkip").getTextContent().toLowerCase().equals("true")?true:false);
							boolean duplicateMessage			= (variableXML.getAttributes().getNamedItem("duplicateMessage") == null ? false : variableXML.getAttributes().getNamedItem("duplicateMessage").getTextContent().toLowerCase().equals("true")?true:false);
							
							if (repetitionFromString.indexOf("@param") != -1){ if (repetitionFromString.indexOf("@param") == repetitionFromString.length() - "@param".length()){
								repetitionFromString = req.getParameter(repetitionFromString.substring(0, repetitionFromString.length() - "@param".length()));
							}}
							try { repetitionFrom = Integer.parseInt(repetitionFromString); } catch (Exception exp){ repetitionFrom = -1; }
							
							if (repetitionToString.indexOf("@param") != -1){ if (repetitionToString.indexOf("@param") == repetitionToString.length() - "@param".length()){
								repetitionToString = req.getParameter(repetitionToString.substring(0, repetitionToString.length() - "@param".length()));
							}}
							try { repetitionTo = Integer.parseInt(repetitionToString); } catch (Exception exp){ repetitionTo = -1; }
							
							int validCountRepetition	= 1;
							for (int countRepetition = repetitionFrom ; countRepetition <= repetitionTo ; countRepetition++){

								// スキップチェック
								try {
									RecursiveNode variableRepetitionXML			= variableXML.n("variable", 0);
									String variableName							= variableRepetitionXML.getAttributes().getNamedItem("name")==null?"":variableRepetitionXML.getAttributes().getNamedItem("name").getTextContent();
									replaceDefinition.clear();
									replaceDefinition.put("countRepetition", Integer.toString(countRepetition));
									replaceDefinition.put("validCountRepetition", Integer.toString(validCountRepetition));
									variableName								= Common.replaceConfig(variableName, "{", "}", replaceDefinition);
									if (req.getParameter(variableName)==null){
										if (emptySkip == true)	{ continue; }
										else 					{ break; 	}
									}
								} catch (Exception exp){
									if (emptySkip == true)	{ continue; }
									else 					{ break; 	}
								}

								// 行チェック
								for (int countVariable = 0 ; countVariable < variableXML.count("variable") ; countVariable++){
									RecursiveNode variableRepetitionXML			= variableXML.n("variable", countVariable);
									replaceDefinition.clear();
									replaceDefinition.put("countRepetition", Integer.toString(countRepetition));
									replaceDefinition.put("validCountRepetition", Integer.toString(validCountRepetition));
									ValidateVariable validateVariable	= this.variableXml2Class(index, req, res, info, variableRepetitionXML, ignoreWarning, replaceDefinition);
									if (validateVariable != null){ processInfo.validate.addVariable(validateVariable); }
								}

								validCountRepetition++;
							}
						}
					}					
				}
			} catch (Exception exp){} finally { if (validateXMLIS != null){ validateXMLIS.close(); } }
		}

		// 入力値チェック実行
		processInfo.validate.validate(index, req, res, info);
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#run(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@Override
	public void run(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		//---------------------------------------------------------------------
		// プロセス処理(現在のアクションファイル内に定義された指定されたパラメータで作成されたメソッド)を呼び出す
		//---------------------------------------------------------------------
		if (processInfo.validate.isValidateResult() == true){
			ActionBase action			= (ActionBase)info.module;
			action.doInit(req, res);
			Class<?> actionClass		= action.getClass();
			Class<?>[] params			= new Class[]{HttpServletRequest.class, HttpServletResponse.class};
			Method method				= actionClass.getMethod(info.paramID, params);
			method.invoke(action, new Object[]{req, res});
		}
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#response(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@Override
	public void response(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		
		// 返却処理
		ActionBase process	= (ActionBase)info.module;
		PrintWriter out		= new PrintWriter(new OutputStreamWriter(res.getOutputStream(), "UTF8"),true);
		String json			= this.makeResponseJSON(req, res, info, process.getContents());
		res.setContentType("text/json; charset=" + info.config.getCharset());
		out.println(json);
	}
	
	/**
	 * モジュール生成処理をオーバーライド
	 * JavaScriptの返却処理は適切なモジュールというものが存在しない為、何も処理を行わないモジュールを生成するロジックとする
	 * 
	 * @param index
	 * @param req
	 * @param res
	 * @param config
	 * @param log
	 * @param className
	 * @param paramID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FrameworkBase createModule(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		FrameworkBase module						= null;
		Class<?> moduleClass					= null;
		if (info.viewLayer.length == 1){
			moduleClass							= index.getClass(info.config.getBaseClass() + "." + info.config.getController().getInfo().get(MODE.ACTION).get(info.nowAction).getClassName());
		} else {
			moduleClass							= index.getClass(info.config.getBaseClass() + "." + info.config.getController().getInfo().get(MODE.DIALOG).get(info.nowAction).getClassName());
		}
		Constructor<FrameworkBase> moduleConst	= (Constructor<FrameworkBase>) moduleClass.getConstructor(HttpServletRequest.class, HttpServletResponse.class, IndexInformation.class);
		module									= (FrameworkBase)moduleConst.newInstance(req, res, info);
		return module;
	}
	
	/**
	 * @param index
	 * @param req
	 * @param res
	 * @param info
	 * @param variableXML
	 * @param ignoreWarning
	 * @return
	 */
	private ValidateVariable variableXml2Class(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info, RecursiveNode variableXML, boolean ignoreWarning, HashMap<String, String> replaceDefinition){

		String variableName							= variableXML.getAttributes().getNamedItem("name")==null?"":variableXML.getAttributes().getNamedItem("name").getTextContent();
		String variableDispName						= variableXML.getAttributes().getNamedItem("dispName")==null?"":variableXML.getAttributes().getNamedItem("dispName").getTextContent();
		String ifCondition							= variableXML.getAttributes().getNamedItem("if")==null?"":variableXML.getAttributes().getNamedItem("if").getTextContent();
		variableName								= Common.replaceConfig(variableName, "{", "}", replaceDefinition);
		variableDispName							= Common.replaceConfig(variableDispName, "{", "}", replaceDefinition);
		ifCondition									= Common.replaceConfig(ifCondition, "{", "}", replaceDefinition);
		
		//=====================================================
		// IFチェック
		//=====================================================
		boolean ifConditionResult			= true;
		ifCondition							= ifCondition.trim();
		if (!"".equals(ifCondition)){
			
			String[] ifConditions			= ifCondition.split(" AND ");
			for (int countIf = 0 ; countIf < ifConditions.length ; countIf++ ){
				String nowCheck				= ifConditions[countIf];
				String leftValue			= "";
				String rightValue			= "";
				
				if (nowCheck.indexOf("==") != -1){
					//-----------------------------------------
					// 同一チェック
					//-----------------------------------------
					leftValue				= nowCheck.substring(0,nowCheck.indexOf("==")).trim();
					rightValue				= nowCheck.substring(nowCheck.indexOf("==") + "==".length()).trim();
					
					if (leftValue.indexOf("@param") != -1){ if (leftValue.indexOf("@param") == leftValue.length() - "@param".length()){
						leftValue			= req.getParameter(leftValue.substring(0, leftValue.length() - "@param".length()));
						if (leftValue==null){ leftValue = ""; }
					}}
					if (rightValue.indexOf("@param") != -1){ if (rightValue.indexOf("@param") == rightValue.length() - "@param".length()){
						rightValue			= req.getParameter(rightValue.substring(0, rightValue.length() - "@param".length()));
						if (rightValue==null){ rightValue = ""; }
					}}
					
					if (!leftValue.equals(rightValue)){ ifConditionResult = false;}
			
				} else if (nowCheck.indexOf("!=") != -1){
					//-----------------------------------------
					// 不同一チェック
					//-----------------------------------------									
					leftValue				= nowCheck.substring(0,nowCheck.indexOf("!=")).trim();
					rightValue				= nowCheck.substring(nowCheck.indexOf("!=") + "!=".length()).trim();
		
					if (leftValue.indexOf("@param") != -1){ if (leftValue.indexOf("@param") == leftValue.length() - "@param".length()){
						leftValue			= req.getParameter(leftValue.substring(0, leftValue.length() - "@param".length()));
						if (leftValue==null){ leftValue = ""; }
					}}
					if (rightValue.indexOf("@param") != -1){ if (rightValue.indexOf("@param") == rightValue.length() - "@param".length()){
						rightValue			= req.getParameter(rightValue.substring(0, rightValue.length() - "@param".length()));
						if (rightValue==null){ rightValue = ""; }
					}}
		
					if (leftValue.equals(rightValue)){ ifConditionResult = false;}
		
				} else if (nowCheck.indexOf("in(") != -1){
					//-----------------------------------------
					// inチェック
					//-----------------------------------------									
					leftValue				= nowCheck.substring(0,nowCheck.indexOf("in(")).trim();
					rightValue				= nowCheck.substring(nowCheck.indexOf("in(") + "in(".length()).trim();
					if (")".equals(rightValue.substring(rightValue.length()-")".length()))){ rightValue = rightValue.substring(0, rightValue.length()-")".length()); }
		
					if (leftValue.indexOf("@param") != -1){ if (leftValue.indexOf("@param") == leftValue.length() - "@param".length()){
						leftValue			= req.getParameter(leftValue.substring(0, leftValue.length() - "@param".length()));
						if (leftValue==null){ leftValue = ""; }
					}}
					
					String[] rightValues	= rightValue.split(",");
					for (int countValues = 0 ; countValues < rightValues.length ; countValues++){
						if (rightValues[countValues].indexOf("@param") != -1){ if (rightValues[countValues].indexOf("@param") == rightValues[countValues].length() - "@param".length()){
							rightValues[countValues] = req.getParameter(rightValues[countValues].substring(0, rightValues[countValues].length() - "@param".length()));
							if (rightValues[countValues]==null){ rightValues[countValues] = ""; }
						}}
					}
		
					boolean isEns		= false;
					for (int countValues = 0 ; countValues < rightValues.length ; countValues++){
						if (leftValue.equals(rightValues[countValues])){ isEns = true;  break;}
					}
					if (isEns == false){ ifConditionResult = false; }
				}
			}
		}
		if (ifConditionResult == false){ return null; }
		//=====================================================
		// IFチェックここまで
		//=====================================================
		
		ValidateVariable validateVariable	= new ValidateVariable(variableName,variableDispName);
		for (int countCheck = 0 ; countCheck < variableXML.count("check") ; countCheck++){
			RecursiveNode checkXML			= variableXML.n("check", countCheck);
			String messageID				= checkXML.getAttributes().getNamedItem("messageID")==null?"":checkXML.getAttributes().getNamedItem("messageID").getTextContent();
			String fromXmlMessage			= checkXML.getAttributes().getNamedItem("message")==null?"":checkXML.getAttributes().getNamedItem("message").getTextContent();
		
			HashMap<String,String> messageParams = new HashMap<String,String>();
			for (int countCheckAttr = 0 ; countCheckAttr < checkXML.getAttributes().getLength() ; countCheckAttr++){
				String paramName		= checkXML.getAttributes().item(countCheckAttr).getNodeName();
				String paramValue		= checkXML.getAttributes().item(countCheckAttr).getTextContent();
				if ("class".equals(paramName)){ continue; }
				if ("messageID".equals(paramName)){ continue; }
				if ("message".equals(paramName)){ continue; }
				
				// 特殊文字置換
				replaceDefinition.clear();
				replaceDefinition.put("dispName"	, variableDispName);
				replaceDefinition.put("name"		, variableName);
				paramValue				= Common.replaceConfig(paramValue, "{", "}", replaceDefinition);
				
				// パラメータ読込み
				if (paramValue.indexOf("@param") != -1){ if (paramValue.indexOf("@param") == paramValue.length() - "@param".length()){
					paramValue			= paramValue.substring(0, paramValue.length() - "@param".length());
					paramValue			= req.getParameter(paramValue);
				}}
				messageParams.put(paramName, paramValue);
			}
			
			if (ignoreWarning==true){
				if (checkXML.getAttributes().getNamedItem("isWarning")!=null){ continue; }
			}
			
			String defaultMessage			= info.getMessage(messageID, messageParams);
			if (!"".equals(fromXmlMessage)){
				defaultMessage				= Common.replaceConfig(fromXmlMessage, "!#", "#!", messageParams);
			}
			ValidateVariableCheck check		= new ValidateVariableCheck(checkXML.getAttributes().getNamedItem("isWarning")==null?CHECK_TYPE.ERROR:CHECK_TYPE.WARNING, (checkXML.getAttributes().getNamedItem("class")==null?"":checkXML.getAttributes().getNamedItem("class").getTextContent()), defaultMessage);
			for (int countParam = 0 ; countParam < checkXML.count("param") ; countParam++){
				RecursiveNode paramXML		= checkXML.n("param",countParam);
				String paramName			= paramXML.getAttributes().getNamedItem("name")==null?"":paramXML.getAttributes().getNamedItem("name").getTextContent();
				String paramValue			= paramXML.getTextContent();
				
				if (paramValue.indexOf("@param") != -1){ if (paramValue.indexOf("@param") == paramValue.length() - "@param".length()){
						paramValue			= paramValue.substring(0, paramValue.length() - "@param".length());
						paramValue			= req.getParameter(paramValue);
				}}
				check.addParam(paramName, paramValue);
			}
			validateVariable.addCheck(check);
		}

		// 結果返却
		return validateVariable;
	}
	
	/**
	 * @author toshiyuki
	 *
	 */
	private class ProcessInfo {
		public Validate validate;
		public ProcessInfo() {
			this.validate			= new Validate();
		}
	}
}
