package jp.co.tjs_net.java.framework.information;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import jp.co.tjs_net.java.framework.Define;
import jp.co.tjs_net.java.framework.Define.MODE_DETAIL;
import jp.co.tjs_net.java.framework.base.AuthorityBase;
import jp.co.tjs_net.java.framework.base.FrameworkBase;
import jp.co.tjs_net.java.framework.base.MessageIDConverterBase;
import jp.co.tjs_net.java.framework.common.Common;
import jp.co.tjs_net.java.framework.core.CoreBase;
import jp.co.tjs_net.java.framework.core.Index;

/**
 * @author toshiyuki
 *
 */
public class IndexInformation {
	
	public Index index;														// 主Servlet
	public Logger log;														// log4j
	public Config config;													// フレームワーク設定情報
	public String paramMode;												// URLから取得したモード
	public String paramID;													// URLから取得した処理ID
	public String nowAction;												// 現在の主クラス
	public String[] viewLayer;												// 閲覧View階層
	public String token;													// 画面のトークン
	public String[] uuids;													// ダイアログUUID一覧
	public MODE_DETAIL mode;												// URLから取得したモードからモードの詳細情報の定義を格納する
	public ControllerObjectBase controller;									// 現在のコントローラー情報
	public String className;												// 処理IDに紐付く処理用クラスの名称
	public FrameworkBase module;											// 上記クラスのインスタンス
	public CoreBase core;													// 上記クラスを処理する為のフレームワーク管理クラス
	public String language;													// 言語

	public long procStartTime;												// 処理開始時間
	public long procEndTime;												// 処理終了時間

	private ArrayList<String> requestValues;								// リクエスト値
	private ArrayList<Exception> systemErrors;								// システムエラー
	private ArrayList<AuthorityErrorInformation> authorityErrors;			// 認証エラーが発生した対象認証ID
	private ArrayList<HashMap<String,Object>> validateResults;				// 入力値チェック結果
	
	private HashMap<String, Connection> connections;						// この処理内のDBコネクション一覧
	private int otherConnectionCount;										// 外部コネクション判定用カウンタ
	
	private MessageIDConverterBase messageIDConverter;						// メッセージIDコンバーター(追加カスタマイズ)

	/**
	 * @param index
	 * @param req
	 * @param res
	 */
	@SuppressWarnings("unchecked")
	public IndexInformation(Index index, HttpServletRequest req, HttpServletResponse res){
		
		this.index			= index;
		
		//=====================================================================
		// Log4j
		//=====================================================================
		this.log = null;
		try {
			this.log		= Logger.getLogger(this.getClass().getName());
			DOMConfigurator.configure(index.getServletContext().getRealPath("/") + "/WEB-INF/log4j.xml");
		} catch (Exception exp){ log = null; }

		//=====================================================================
		// Config
		//=====================================================================
		this.config		= (Config)index.getServletContext().getAttribute(Define.FRAMEWORK_NAME + "_FRAMEWORK_CONFIG");

		// この時点でConfigファイルが未読込みの場合は、再度Configファイルを読み込みする
		if (this.config == null){
			this.config = new Config();
			this.config.read(index, index.getServletContext(), req);
			index.getServletContext().setAttribute(Define.FRAMEWORK_NAME + "_FRAMEWORK_CONFIG", this.config);			
		}

		// 開発者モードの場合は再読み込みを実施する
		if (this.config.isDebug() == true){
			this.config.read(index, index.getServletContext(), req);			
			index.getServletContext().setAttribute(Define.FRAMEWORK_NAME + "_FRAMEWORK_CONFIG", this.config);			
		}

		// メッセージファイル再読込み
		if (this.config.isMessageReloadable()){
			this.config.readAllMessages(index, req);
		}

		// メッセージコンバーターインスタンス生成
		if (this.config.getMessageIDConverterClassName() != null){

			MessageIDConverterBase messageIDConverter	= null;
			try {
				Class<?> messageIDConverterClass		= null;
				messageIDConverterClass = index.getClass(config.getBaseClass() + "." + this.config.getMessageIDConverterClassName());
				if (messageIDConverterClass == null){
					messageIDConverterClass = index.getClass(this.config.getMessageIDConverterClassName());
				}
				if (messageIDConverterClass == null){ throw new Exception(""); }
				Constructor<MessageIDConverterBase> messageIDConverterClassConst	= (Constructor<MessageIDConverterBase>) messageIDConverterClass.getConstructor();
				messageIDConverter						= (MessageIDConverterBase)messageIDConverterClassConst.newInstance(new Object[]{});
			} catch (Exception exp){
				messageIDConverter						= null;
			}
			if (messageIDConverter != null){
				this.messageIDConverter					= messageIDConverter;
			} else {
				this.messageIDConverter					= null;	
			}
		}
		
		//=====================================================================
		// 文字コード設定
		//=====================================================================
		try {
			req.setCharacterEncoding(this.config.getCharset());
		} catch (UnsupportedEncodingException e2){
			e2.printStackTrace();
		}

		//=====================================================================
		// 動作モード等Filter情報の取得
		//=====================================================================
		this.paramMode			= (String) req.getAttribute("_" + Define.FRAMEWORK_NAME + "_" + "MODE");									// Filterで取得しておいたモードの取得
		this.paramID			= (String) req.getAttribute("_" + Define.FRAMEWORK_NAME + "_" + "ID");										// Filterで取得しておいたIDの取得
		this.nowAction			= Common.decCipher(this.config, (String)req.getAttribute("_" + Define.FRAMEWORK_NAME + "_" + "NOWACTION"));	// 現在の処理クラス
		this.mode				= Define.MODE_INFO.get(this.paramMode);																		// 動作モード
		String viewLayer		= (String)req.getAttribute("_" + Define.FRAMEWORK_NAME + "_" + "VIEWLAYER");								// レイヤー一覧の取得
		String[] viewLayers		= viewLayer.split(Define.VIEWLAYER_DIALOG_SEPARATOR);														// 
		this.viewLayer			= new String[viewLayers.length];																			// 
		for (int count = 0 ; count < viewLayers.length ; count++){																			// 
			this.viewLayer[count]	= new String(Common.decCipher(this.config, viewLayers[count]));											// 
		}
		this.token				= (String) req.getAttribute("_" + Define.FRAMEWORK_NAME + "_" + "TOKEN");									// Filterで取得しておいたTOKENの取得
		String uuid				= (String)req.getAttribute("_" + Define.FRAMEWORK_NAME + "_" + "UUIDS");
		String[] uuids			= uuid.split(Define.VIEWLAYER_DIALOG_SEPARATOR);															// 
		this.uuids				= new String[uuids.length];																					// 
		for (int count = 0 ; count < uuids.length ; count++){																				// 
			this.uuids[count]	= new String(uuids[count]);																					// 
		}																																	// 

		//=====================================================================
		// 言語設定
		//=====================================================================
		this.language = Common.getLanguage(req);
		if (this.language.equals("")){ this.language	= this.config.getLanguage(); }
		if (this.language.equals("")){ this.language	= "ja"; }
		
		//=====================================================================
		// メッセージ
		//=====================================================================
		this.systemErrors		= new ArrayList<>();
		this.authorityErrors	= new ArrayList<>();
		this.validateResults	= new ArrayList<>();
		
		//=====================================================================
		// DBコネクション一覧
		//=====================================================================
		this.connections			= new HashMap<>();
		this.otherConnectionCount	= 0;
	}

	public void addSystemError(Exception exception)							{ this.systemErrors.add(exception); }
	public void addValidateResult(HashMap<String, Object> validateResult)	{ this.validateResults.add(validateResult); }
	public ArrayList<String> getRequestValues()								{ return requestValues; }
	public ArrayList<Exception> getSystemErrors()							{ return systemErrors; }
	public ArrayList<AuthorityErrorInformation> getAuthorityErrors()		{ return authorityErrors; }
	public ArrayList<HashMap<String,Object>> getValidateResults()			{ return validateResults; }
	public MessageIDConverterBase getMessageIDConverter()					{ return this.messageIDConverter; }
	
	/**
	 * 主処理を実行しても良いか否かの判断
	 * @return
	 */
	public boolean isRun(){
		boolean result = true;
		if (systemErrors.size() != 0){ result = false; }
		if (authorityErrors.size() != 0){ result = false; }
		boolean hasValidateError = false;
		for (int count = 0 ; count < this.validateResults.size() ; count++){ if ((boolean)this.validateResults.get(count).get("result") == false){ hasValidateError = true; break; }}
		if (hasValidateError == true){ result = false; }
		return result;
	}
	
	/**
	 * フレームワークコアクラスの生成
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public void creareCore(Index index){
		try {
			Class<?> coreClass						= index.getClass(this.mode.coreClassName);
			Constructor<CoreBase> coreConst			= (Constructor<CoreBase>) coreClass.getConstructor();
			this.core								= coreConst.newInstance();
		} catch(Exception exp) {
			this.core								= null;
			this.systemErrors.add(new Exception(config.getSystemMessage("_SYS_ERROR_005")));
		}
		
		if (core != null){
			try {
				this.controller						= core.getController(this);
				this.className						= core.getModuleClassName(this);
			} catch (Exception exp){
				this.systemErrors.add(new Exception(config.getSystemMessage("_SYS_ERROR_006")));
			}
		}
	}
	
	/**
	 * 処理モジュールクラスの生成
	 * @param index
	 * @param req
	 * @param res
	 */
	public void createModule(Index index, HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (this.core != null){
			this.module								= this.core.createModule(index, req, res, this);
		}		
	}
	
	/**
	 * 処理開始
	 */
	public void start(){
		this.procStartTime			= Calendar.getInstance().getTimeInMillis();		
	}
	
	/**
	 * 処理終了
	 */
	public void end(){
		this.procEndTime			= Calendar.getInstance().getTimeInMillis();
	}
	
	/**
	 * @param id
	 * @return
	 */
	public String getMessage(String id){
		if (messageIDConverter != null){ id = messageIDConverter.convertMessageIDLogic(id); }
		return config.getInnerMessage(id);
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	public String getMessage(String id, String... args){
		if (messageIDConverter != null){ id = messageIDConverter.convertMessageIDLogic(id); }
		return config.getInnerMessage(id, args);
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	public String getMessage(String id, ArrayList<String> args){
		if (messageIDConverter != null){ id = messageIDConverter.convertMessageIDLogic(id); }
		return config.getInnerMessage(id, args);
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	public String getMessage(String id, HashMap<String, String> args){
		if (messageIDConverter != null){ id = messageIDConverter.convertMessageIDLogic(id); }
		return config.getInnerMessage(id, args);
	}
	
	/**
	 * 認証処理
	 * @param index
	 * @param req
	 * @param res
	 */
	@SuppressWarnings("unchecked")
	public void auth(Index index, HttpServletRequest req, HttpServletResponse res){
		if (this.controller != null){
			for (int count = 0 ; count < this.controller.getGroups().getAuthorities().size() ; count++){
				
				String authorityID						= this.controller.getGroups().getAuthorities().get(count);
				if (this.config.getController().getAuthorities().containsKey(authorityID) == false){ continue; }
				ControllerAuthority controllerAuthority	= this.config.getController().getAuthorities().get(authorityID);
				
				AuthorityBase authority							= null;
				try {
					Class<?> authorityClass						= index.getClass(config.getBaseClass() + "." + controllerAuthority.getClassName());
					Constructor<AuthorityBase> authorityConst	= (Constructor<AuthorityBase>) authorityClass.getConstructor(HttpServletRequest.class, HttpServletResponse.class, IndexInformation.class);
					authority									= (AuthorityBase)authorityConst.newInstance(req, res, this);
				} catch(Exception exp) {
				} finally {}
				
				if (authority != null){
					boolean authorityResult	= false;
					try {
						authority.doRun(req, res);
						authorityResult		= authority.isAuthorityResult();
					} catch (Exception exp){
						authorityResult = false;
						authority.setAuthoritErrorMessage(config.getSystemMessage("_SYS_ERROR_004"));
					}
					if (authorityResult == false){
						this.authorityErrors.add(new AuthorityErrorInformation(this, authorityID, authority.getAuthoritErrorMessage()));
						break;
					}
				}
			}
		}
	}
	
	/**
	 * コネクション取得処理
	 * @return
	 */
	public Connection getConnection(String id, HttpServletRequest req) throws Exception {
		Connection connection				= null;
		if (this.connections.containsKey(id)){
			connection						= this.connections.get(id);
		} else {
			connection						= Common.getConnection(this.index, id, this.config);
			this.connections.put(id, connection);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			System.out.println(req.getRemoteAddr() + " " + sdf.format(Calendar.getInstance().getTime()) + " " + "Connection get![" + id + "]");
		}
		try {
			if (connection.getAutoCommit()==false) {
				connection.rollback();
				connection.setAutoCommit(true);
			}
		} catch (Exception e){ }	
		return connection;
	}
	
	/**
	 * @param connection
	 */
	public void addOtherConnections(Connection connection){
		otherConnectionCount++;
		String connectionID		= "otherConnection_" + Integer.toString(otherConnectionCount);
		this.connections.put(connectionID, connection);
		System.out.println("Connection add![" + connectionID + "]");
	}
	
	/**
	 * コネクション開放処理
	 */
	public void closeConnections(HttpServletRequest req){
		Iterator<String> ite			= this.connections.keySet().iterator();
		while(ite.hasNext()){
			String connectionID		= (String)ite.next();
			Connection connection	= this.connections.get(connectionID);
			if (connection == null){ return; }
			try  {
				if (connection.getAutoCommit() == false) {
					connection.rollback();
				}
				connection.setAutoCommit(true);
				connection.close();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				System.out.println(req.getRemoteAddr() + " " + sdf.format(Calendar.getInstance().getTime()) + " " + "Connection close![" + connectionID + "]");
			} catch (Exception e) {
			}
		}
		this.connections.clear();
	}
	
	/**
	 * Framework純正以外のコネクションのみ開放
	 */
	public void closeOtherConnections(){
		Iterator<String> ite			= this.connections.keySet().iterator();
		while(ite.hasNext()){
			String connectionID		= (String)ite.next();
			if (connectionID.indexOf("otherConnection_")!=0){ continue; }
			Connection connection	= this.connections.get(connectionID);
			if (connection == null){ return; }
			try  {
				if (connection.getAutoCommit() == false) {
					connection.rollback();
				}
				connection.setAutoCommit(true);
				connection.close();
				System.out.println("Connection close![" + connectionID + "]");
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * @author toshiyuki
	 *
	 */
	public class AuthorityErrorInformation{
		
		private String authorityID;
		private String errorMessage;
		private String afterScript;
		
		public AuthorityErrorInformation(IndexInformation info, String authorityID, String errorMessage){
			this.authorityID			= authorityID;
			this.errorMessage			= errorMessage;
			this.afterScript				= info.config.getController().getAuthorities().get(authorityID).getAfterScript();
		}
		
		public String getAuthorityID() { return authorityID; }
		public String getErrorMessage() { return errorMessage; }		
		public String getAfterScript() { return afterScript; }
	}
	
	/**
	 * 帳票テンプレート取得処理
	 * @return
	 */
	public String getTemplateFile(String id, HttpServletRequest req) throws Exception {
		String templateFile							= null;
		String templateFilePath						= null;
		String templateFileName						= null;
		HashMap<String, String> templateFileNames	= null;
		
		templateFilePath					= this.config.getTemplateFilePath();
		if (templateFilePath == null || "".equals(templateFilePath)) { return ""; }
		
		templateFileNames					= this.config.getTemplateFileNames();
		if (templateFileNames == null || templateFileNames.size() == 0) { return ""; }
		
		templateFileName					= templateFileNames.get(id);
		if (templateFileName == null || "".equals(templateFileName)) { return ""; }

		templateFile = req.getServletContext().getRealPath(templateFilePath + templateFileName);
		
		return templateFile;
	}
}
