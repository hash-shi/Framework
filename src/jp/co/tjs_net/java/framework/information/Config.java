	package jp.co.tjs_net.java.framework.information;

//import java.io.BufferedReader;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import jp.co.tjs_net.java.framework.Define;
import jp.co.tjs_net.java.framework.Define.MODE;
import jp.co.tjs_net.java.framework.base.MessageBase;
import jp.co.tjs_net.java.framework.common.Common;
import jp.co.tjs_net.java.framework.common.RecursiveNode;
import jp.co.tjs_net.java.framework.core.Index;
import jp.co.tjs_net.java.framework.startup.Startup;

public class Config {
	
	// メンバ変数定義
	private String htmlVersion;								// HTMLバージョン
	private String charset;									// 文字コード
	private String cipherKey;								// 暗号化キー
	private String name;									// システム名
	private String baseClass;								// ベースクラス
	private boolean debug;									// デバッグモードか否か
	private boolean ajaxAsync;								// Ajax通信を非同期モードで実施するか否か
	private String language;								// 言語
	private String url;										// URL
	private String developerName;							// 開発者名
	private String developerMailaddress;					// 開発者メールアドレス
	private String customerName;							// 顧客名
	private String customerMailaddress;						// 顧客メールアドレス
	private String administratorName;						// 管理者名
	private String administratorMailaddress;				// 管理者メールアドレス
	private HashMap<String,DatabaseInformation> databases;	// データベース定義
	private String templatePath;							// JSPテンプレートパス
	private String systemErrorJspPath;						// エラー時に表示する画面
	private String errorsAttrName;							// エラー時に表示する画面に定義されているエラーパラメータ名
	private String validateBaseClass;						// 入力値チェック基本クラス
	private String validateXMLPath;							// 入力値チェックXMLファイルパス
	private String exceptionViewPath;						// 致命的エラー画面パス
	private boolean useTokenCheck;							// トークンチェックを使用するか否か(画面遷移で戻るボタンを使用できなくなる)
	private Controller controller;							// コントローラー定義

	private boolean messageReloadable;						// メッセージをアクセスごとに読み直すか否か
	private boolean messageUseJavaScript;					// メッセージをJavaScriptでも使用するか否か
	private ArrayList<String> messageClasses;				// メッセージ定義
	private HashMap<String, String> messages;				// メッセージ格納領域
	private String messageIDConverterClassName = null;		// メッセージIDコンバーター定義
	
	private ArrayList<String> systemMessageClasses;			// システムメッセージ定義
	private HashMap<String, String> systemMessages;			// システムメッセージ格納領域
	private String templateFilePath;						// 帳票テンプレートパス
	private HashMap<String, String> templateFileNames;		// 帳票テンプレートファイル名
	
	// コンストラクタ
	public Config(){
		this.htmlVersion							= new String("5");
		this.databases								= new HashMap<>();
		this.messages								= new HashMap<>();
		this.systemMessages							= new HashMap<>();
		this.templateFileNames						= new HashMap<>();
	}
	
	// Getter
	public String getHtmlVersion()								{ return this.htmlVersion; }
	public String getCharset()									{ return this.charset; }
	public String getCipherKey()								{ return this.cipherKey; }
	public String getName()										{ return this.name; }
	public String getBaseClass()								{ return this.baseClass; }
	public boolean isDebug()									{ return this.debug; }
	public boolean ajaxAsync()									{ return this.ajaxAsync; }
	public String getLanguage()									{ return this.language; }
	public String getUrl()										{ return this.url; }
	public String getDeveloperName()							{ return this.developerName; }
	public String getDeveloperMailaddress()						{ return this.developerMailaddress; }
	public String getCustomerName()								{ return this.customerName; }
	public String getCustomerMailaddress()						{ return this.customerMailaddress; }
	public String getAdministratorName()						{ return this.administratorName; }
	public String getAdministratorMailaddress()					{ return this.administratorMailaddress; }
	public HashMap<String, DatabaseInformation> getDatabases()	{ return this.databases; }
	public String getTemplatePath()								{ return this.templatePath; }
	public String getSystemErrorJspPath()						{ return this.systemErrorJspPath; }
	public String getErrorsAttrName()							{ return this.errorsAttrName; }
	public String getValidateBaseClass()						{ return this.validateBaseClass; }
	public String getValidateXMLPath()							{ return this.validateXMLPath; }
	public String getExceptionViewPath()						{ return this.exceptionViewPath; }
	public boolean useTokenCheck()								{ return this.useTokenCheck; }
	public Controller getController()							{ return this.controller; }
	public boolean isMessageReloadable()						{ return this.messageReloadable; }
	public boolean isMessageUseJavaScript()						{ return this.messageUseJavaScript; }
	public ArrayList<String> getMessageClasses()				{ return this.messageClasses; }
	public ArrayList<String> getSystemMessageClasses()			{ return this.systemMessageClasses; }
	
	public String getTemplateFilePath()							{ return this.templateFilePath; }
	public HashMap<String, String> getTemplateFileNames()		{ return this.templateFileNames; }
	
	/**
	 * 
	 */
	private void init(){
		this.messageClasses							= new ArrayList<>();
		this.systemMessageClasses					= new ArrayList<>();
	}
	
	/**
	 * @param sc
	 * @return
	 */
	public boolean read(HttpServlet parent, ServletContext sc, HttpServletRequest req){

		// データ初期化
		init();

		// 変数定義
		boolean result					= true;
		FileInputStream configFileIS	= null;

		HashMap<String, String> values	= new HashMap<>();
		values.put("FRAMEWORK_PACKAGE", Define.FRAMEWORK_PACKAGE);

		// 読み込み処理開始
		try{
			// 設定ファイル読み込み準備
			configFileIS					= new FileInputStream(sc.getRealPath("/") + "WEB-INF/config.xml");
			RecursiveNode configXml			= RecursiveNode.parse(configFileIS);
			
			// ルートノード読み込み
			RecursiveNode rootNode			= configXml.n("config");
			this.htmlVersion				= (rootNode.n("htmlVersion") == null ? "5" : Common.replaceConfig(rootNode.n("htmlVersion").getTextContent(), "!#", "#!", values));
			this.charset					= (rootNode.n("charset") == null ? "" : Common.replaceConfig(rootNode.n("charset").getTextContent(), "!#", "#!", values));
			this.cipherKey					= (rootNode.n("cipherKey") == null ? "" : Common.replaceConfig(rootNode.n("cipherKey").getTextContent(), "!#", "#!", values));
			
			// プロジェクト設定読み込み
			RecursiveNode projectNode		= rootNode.n("project");
			this.name						= (projectNode.n("name") == null ? "" : Common.replaceConfig(projectNode.n("name").getTextContent(), "!#", "#!", values));
			this.baseClass					= (projectNode.n("baseClass") == null ? "" : Common.replaceConfig(projectNode.n("baseClass").getTextContent(), "!#", "#!", values));
			this.debug						= (projectNode.n("isDebug") == null ? false : projectNode.n("isDebug").getTextContent().toLowerCase().equals("true")?true:false);
			this.url						= (projectNode.n("url") == null ? "" : Common.replaceConfig(projectNode.n("url").getTextContent(), "!#", "#!", values));
			this.language					= (projectNode.n("language") == null ? "" : Common.replaceConfig(projectNode.n("language").getTextContent(), "!#", "#!", values));
			this.ajaxAsync					= (projectNode.n("ajaxAsync") == null ? false : projectNode.n("ajaxAsync").getTextContent().toLowerCase().equals("true")?true:false);
			
			// 開発者情報
			RecursiveNode developerNode		= rootNode.n("developer");
			this.developerName				= (developerNode.n("name") == null ? "" : Common.replaceConfig(developerNode.n("name").getTextContent(), "!#", "#!", values));
			this.developerMailaddress		= (developerNode.n("mailaddress") == null ? "" : Common.replaceConfig(developerNode.n("mailaddress").getTextContent(), "!#", "#!", values));
			
			// 顧客情報
			RecursiveNode customerNode		= rootNode.n("customer");
			this.customerName				= (customerNode.n("name") == null ? "" : Common.replaceConfig(customerNode.n("name").getTextContent(), "!#", "#!", values));
			this.customerMailaddress		= (customerNode.n("mailaddress") == null ? "" : Common.replaceConfig(customerNode.n("mailaddress").getTextContent(), "!#", "#!", values));
			
			// 管理者情報
			RecursiveNode administratorNode	= rootNode.n("administrator");
			this.administratorName			= (administratorNode.n("name") == null ? "" : Common.replaceConfig(administratorNode.n("name").getTextContent(), "!#", "#!", values));
			this.administratorMailaddress	= (administratorNode.n("mailaddress") == null ? "" : Common.replaceConfig(administratorNode.n("mailaddress").getTextContent(), "!#", "#!", values));
		
			// コントローラー
			this.controller					= new Controller();
			RecursiveNode controllersNode	= rootNode.n("controllers");
			for (int count = 0 ; count < controllersNode.count("path") ; count++){
				this.controller.read(sc, Common.replaceConfig(controllersNode.n("path", count).getTextContent(), "!#", "#!", values));
			}
			
			// データベース
			RecursiveNode databasesNode		= rootNode.n("databases");
			for (int count = 0 ; count < databasesNode.count("database") ; count++){
				RecursiveNode databaseNode		= databasesNode.n("database", count);
				String id						= databaseNode.n("id")==null					? null : Common.replaceConfig(databaseNode.n("id").getTextContent(), "!#", "#!", values);
				String jndi						= databaseNode.n("jndi")==null					? null : Common.replaceConfig(databaseNode.n("jndi").getTextContent(), "!#", "#!", values);
				String connectionClassName		= databaseNode.n("connectionClassName")==null	? null : Common.replaceConfig(databaseNode.n("connectionClassName").getTextContent(), "!#", "#!", values);
				
				if (id					== null){ continue; }  if (id.equals					("")){ continue; }
				if (jndi				== null){ continue; }  if (jndi.equals					("")){ continue; }
				if (connectionClassName	== null){ continue; }  if (connectionClassName.equals	("")){ continue; }

				this.databases.put(id, new DatabaseInformation(id, jndi, connectionClassName));
			}

			// テンプレート
			RecursiveNode templateNode		= rootNode.n("template");
			this.templatePath				= (templateNode.n("path") == null ? "" : Common.replaceConfig(templateNode.n("path").getTextContent(), "!#", "#!", values));
			
			// エラー情報
			RecursiveNode errorNode			= rootNode.n("error");
			this.systemErrorJspPath			= (errorNode.n("systemErrorjspPath") == null ? "" : Common.replaceConfig(errorNode.n("systemErrorjspPath").getTextContent(), "!#", "#!", values));
			this.errorsAttrName				= (errorNode.n("errorsAttrName")     == null ? "" : Common.replaceConfig(errorNode.n("errorsAttrName").getTextContent(), "!#", "#!", values));
			
			// 入力値チェッククラス
			RecursiveNode validateNode		= rootNode.n("validate");
			this.validateBaseClass			= (validateNode.n("baseClass") == null ? "" : Common.replaceConfig(validateNode.n("baseClass").getTextContent(), "!#", "#!", values));
			
			// 入力値チェックXMLパス
			RecursiveNode validateXMLNode	= rootNode.n("validateXML");
			this.validateXMLPath			= (validateXMLNode.n("path") == null ? "" : Common.replaceConfig(validateXMLNode.n("path").getTextContent(), "!#", "#!", values));

			// 例外発生時画面
			RecursiveNode exceptionViewNode	= rootNode.n("exceptionView");
			this.exceptionViewPath			= (exceptionViewNode.n("path") == null ? "" : Common.replaceConfig(exceptionViewNode.n("path").getTextContent(), "!#", "#!", values));
			
			// トークンチェックチェックを行うか否か
			RecursiveNode useTokenCheckNode	= rootNode.n("useTokenCheck");
			this.useTokenCheck				= (useTokenCheckNode == null ? false : useTokenCheckNode.getTextContent().toLowerCase().equals("true")?true:false);

			// メッセージクラス
			RecursiveNode messagesNode		= rootNode.n("messages");
			this.messageReloadable			= (messagesNode.getAttributes().getNamedItem("reloadable") == null ? false : messagesNode.getAttributes().getNamedItem("reloadable").getTextContent().toLowerCase().equals("true")?true:false);
			this.messageUseJavaScript		= (messagesNode.getAttributes().getNamedItem("useJavaScript") == null ? false : messagesNode.getAttributes().getNamedItem("useJavaScript").getTextContent().toLowerCase().equals("true")?true:false);
			for (int count = 0 ; count < messagesNode.count("messageClass") ; count++){
				this.messageClasses.add(messagesNode.n("messageClass", count).getTextContent());
			}
			
			// メッセージIDコンバータークラスの定義
			if (messagesNode.count("messageIDConverterClass") == 1){
				messageIDConverterClassName			= messagesNode.n("messageIDConverterClass").getTextContent();
			} else {
				messageIDConverterClassName			= null;
			}
			
			// システムメッセージクラス
			RecursiveNode systemMessagesNode= rootNode.n("systemMessages");
			this.systemMessageClasses.add(Define.FRAMEWORK_PACKAGE + ".message" + ".readFrameworkMessage");
			for (int count = 0 ; count < systemMessagesNode.count("messageClass") ; count++){
				this.systemMessageClasses.add(systemMessagesNode.n("messageClass", count).getTextContent());
			}
			
			// 帳票テンプレートパス
			RecursiveNode templateFilePathNode	= rootNode.n("templateFilePath");
			this.templateFilePath				= (templateFilePathNode.n("path") == null ? "" : Common.replaceConfig(templateFilePathNode.n("path").getTextContent(), "!#", "#!", values));
			
			// 帳票テンプレート名
			RecursiveNode templateFileNamesNode	= rootNode.n("templateFileNames");
			for (int count = 0 ; count < templateFileNamesNode.count("fileName"); count++){
				RecursiveNode fileNameNode		= templateFileNamesNode.n("fileName", count);
				String id						= fileNameNode.n("id")==null	? null : Common.replaceConfig(fileNameNode.n("id").getTextContent(), "!#", "#!", values);
				String name						= fileNameNode.n("name")==null	? null : Common.replaceConfig(fileNameNode.n("name").getTextContent(), "!#", "#!", values);
				
				if (id					== null){ continue; }  if (id.equals					("")){ continue; }
				if (name				== null){ continue; }  if (name.equals					("")){ continue; }
				
				this.templateFileNames.put(id, name);
			}
			
		} catch (Exception exp){
			System.out.println("config.xmlの読み込みに失敗しました");
			exp.printStackTrace();
			result						= false;
		} finally {
		}
		
		// 設定ファイルクローズ
		if (configFileIS != null){
			try { configFileIS.close(); } catch(Exception exp){ exp.printStackTrace(); }
		}
		
		//---------------------------------------------------------------------
		// メッセージ読み込み
		//---------------------------------------------------------------------
		readSystemMessage(parent, req);
		readAllMessages(parent, req);
		
		//---------------------------------------------------------------------
		// 結果返却
		//---------------------------------------------------------------------
		return result;
	}

	public boolean readSystemMessage(HttpServlet parent, HttpServletRequest req){
		return readMessages(parent, req, getSystemMessageClasses(), this.systemMessages);
	}
	
	public boolean readAllMessages(HttpServlet parent, HttpServletRequest req){
		return readMessages(parent, req, getMessageClasses(), this.messages);
	}
	
	/**
	 * メッセージ読み込み
	 */
	@SuppressWarnings("unchecked")
	public boolean readMessages(HttpServlet parent, HttpServletRequest req, ArrayList<String> messageClasses, HashMap<String,String> target){

		// メッセージ読込み判定
		boolean isReadMessage	= true;
		if (req != null){
			String mode		= (String)req.getAttribute("_" + Define.FRAMEWORK_NAME + "_" + "MODE");
			if(mode != null) {
				if(!MODE.ACTION.name().equals(mode)){
					isReadMessage = false;
				}
			}
		}
		if (isReadMessage == false){ return false; } 
		
		// 結果返却
		boolean result = true;
		for (int count = 0 ; count < messageClasses.size() ; count++){
			
			// メッセージ解析クラスを格納するエリアの準備
			MessageBase message		= null;
			
			//-------------------------------------------------------
			// メッセージ解析クラスの取得
			//-------------------------------------------------------
			try {
				Class<?> messageClass					= null;
				if (parent.getClass().getSuperclass().equals(Startup.class)){ messageClass = ((Startup)parent).getClass(getBaseClass() + "." + messageClasses.get(count)); }
				if (parent.getClass().getSuperclass().equals(Index.class))  { messageClass = ((Index)  parent).getClass(getBaseClass() + "." + messageClasses.get(count)); }
				if (messageClass == null){
					if (parent.getClass().getSuperclass().equals(Startup.class)){ messageClass = ((Startup)parent).getClass(messageClasses.get(count)); }
					if (parent.getClass().getSuperclass().equals(Index.class))  { messageClass = ((Index)  parent).getClass(messageClasses.get(count)); }
				}
				if (messageClass == null){ continue; }
				Constructor<MessageBase> messageConst	= (Constructor<MessageBase>) messageClass.getConstructor(HttpServlet.class, Config.class);
				message									= (MessageBase)messageConst.newInstance(new Object[]{parent, this});
			} catch (Exception exp){
				message									= null;
				result									= false;
			}
			
			//-------------------------------------------------------
			// メッセージ解析クラスの処理を実行(インスタンスが生成できた場合のみ)
			//-------------------------------------------------------
			if (message != null){
				try {
					target.putAll(message.getMessage());
				} catch (Exception exp){}
				
				// メッセージ解析クラスの終了処理
				message.finish();
			}
		}
		return result;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public String getSystemMessage(String id){
		String systemMessage		= "";
		if (systemMessages.containsKey(id)){
			systemMessage		= systemMessages.get(id);
		}
		return systemMessage;
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	public String getSystemMessage(String id, HashMap<String, String> args){
		String systemMessage		= getSystemMessage(id);
		systemMessage				= Common.replaceConfig(systemMessage, "!#", "#!", args);
		return systemMessage;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public String getInnerMessage(String id){
		String message		= "";
		if (messages.containsKey(id)) {
			message		= messages.get(id);
		}
		return message;
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	public String getInnerMessage(String id, String... args){
		ArrayList<String> newArgs	= new ArrayList<>();
		for (int count = 0 ; count < args.length ; count++){ newArgs.add(args[count]); }
		return getInnerMessage(id, newArgs);
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	public String getInnerMessage(String id, ArrayList<String> args){
		String message		= getInnerMessage(id);
		for (int count = 1 ; count <= args.size() ; count++){
			String key			= "{" + "$" + Integer.toString(count) + "}";
			if (message.indexOf(key) == -1){ continue; }
			message				= message.replace("{" + "$" + Integer.toString(count) + "}", args.get(count-1));
		}
		return message;
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	public String getInnerMessage(String id, HashMap<String, String> args){
		String message		= getInnerMessage(id);
		message				= Common.replaceConfig(message, "!#", "#!", args);
		return message;
	}

	/**
	 * @return
	 */
	public HashMap<String, String> getMessages(){
		return this.messages;
	}
	
	/**
	 * @return
	 */
	public String getMessageIDConverterClassName(){
		return this.messageIDConverterClassName;
	}
}
