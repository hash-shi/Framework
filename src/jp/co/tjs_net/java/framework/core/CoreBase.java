package jp.co.tjs_net.java.framework.core;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import org.json.simple.JSONObject;

import jp.co.tjs_net.java.framework.base.FrameworkBase;
import jp.co.tjs_net.java.framework.information.ControllerObjectBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;
import jp.co.tjs_net.java.framework.information.IndexInformation.AuthorityErrorInformation;

public abstract class CoreBase {
	
	public abstract void init(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception;		// 事前処理(実行前の入力値チェックなど)
	public abstract void run(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception;			// 実処理
	public abstract void response(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception;	// 返却処理
	public abstract ControllerObjectBase getController(IndexInformation info);
	public abstract String getModuleClassName(IndexInformation info);
	
	/**
	 * 標準的なモジュール生成メソッド
	 * (例外的なモジュール生成メソッドを実装する必要がある場合は、このメソッドをオーバーライドしてください)
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
	@SuppressWarnings({ "unchecked", "serial" })
	public FrameworkBase createModule(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception  {
		FrameworkBase module					= null;
		final String moduleClassName			= info.config.getBaseClass() + "." + info.className;
		Class<?> moduleClass					= index.getClass(info.config.getBaseClass() + "." + info.className);
		if (moduleClass == null){ throw new Exception(info.config.getSystemMessage("_SYS_ERROR_003", new HashMap<String, String>() {{put("CLASSNAME", moduleClassName);}})); }
		Constructor<FrameworkBase> moduleConst	= (Constructor<FrameworkBase>) moduleClass.getConstructor(HttpServletRequest.class, HttpServletResponse.class, IndexInformation.class);
		module									= (FrameworkBase)moduleConst.newInstance(req, res, info);
		return module;
	}

	/**
	 * フレームワーク共通のXML返却フォーマットを作成する
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String makeResponseJSON(HttpServletRequest req, HttpServletResponse res, IndexInformation info, Object contents) throws Exception {
		
		// jsonのIDを生成
		UUID responseUUID				= UUID.randomUUID();
	
		// jsonに返却データを格納
		JSONObject json					= new JSONObject();
		json.put("id"					, responseUUID.toString());								// レスポンスID
		json.put("nowAction"			, info.nowAction);										// 現在のアクション
		json.put("paramID"				, info.paramID);										// 現在の動作ID
		json.put("procStartTime"		, info.procStartTime);									// 処理開始時間
		json.put("procEndTime"			, info.procEndTime);									// 処理終了時間
		json.put("systemErrors"			, exceptions2ArrayHash(info.getSystemErrors()));		// システムエラーメッセージ(情報)
		json.put("authorityErrors"		, authorityError2ArrayHash(info.getAuthorityErrors()));	// 
		json.put("validateResults"		, info.getValidateResults());							// 入力値チェックエラーメッセージ(情報)
		json.put("contents"				, contents);											// 返却データ
	
		
		// json形式で返却
		return json.toString(); 
	}
		
	/**
	 * @param authorityErrors
	 * @return
	 */
	private ArrayList<HashMap<String,Object>> authorityError2ArrayHash(ArrayList<AuthorityErrorInformation> authorityErrors){

		ArrayList<HashMap<String,Object>> result	= new ArrayList<>();
		
		for (int count = 0 ; count < authorityErrors.size() ; count++){

			HashMap<String, Object> authorityErrorInfo	= new HashMap<String, Object>();

			AuthorityErrorInformation authorityError		= authorityErrors.get(count);
			
			authorityErrorInfo.put("authorityID"	, authorityError.getAuthorityID());
			authorityErrorInfo.put("errorMessage"	, authorityError.getErrorMessage());
			authorityErrorInfo.put("afterScript"	, authorityError.getAfterScript());

			result.add(authorityErrorInfo);
		}
		return result;

	}
	
	/**
	 * @param exceptions
	 * @return
	 */
	private ArrayList<HashMap<String,Object>> exceptions2ArrayHash(ArrayList<Exception> exceptions){
		
		ArrayList<HashMap<String,Object>> result	= new ArrayList<>();
		
		for (int count = 0 ; count < exceptions.size() ; count++){

			HashMap<String, Object> exceptionInfo	= new HashMap<String, Object>();

			Exception exception		= exceptions.get(count);
			
			exceptionInfo.put("message", exception.getMessage());
			exceptionInfo.put("className", exception.getClass().getName());
			
			ArrayList<HashMap<String, String>> stackTraces		= new ArrayList<>();
			for (int countStackTrace = 0 ; countStackTrace < exception.getStackTrace().length ; countStackTrace++){
				HashMap<String, String> stackTrace				= new HashMap<String, String>();
				StackTraceElement stackTraceElement		= exception.getStackTrace()[countStackTrace];
				stackTrace.put("className"		, stackTraceElement.getClassName());
				stackTrace.put("fileName"		, stackTraceElement.getFileName());
				stackTrace.put("methodName"		, stackTraceElement.getMethodName());
				stackTrace.put("lineNumber"		, Integer.toString(stackTraceElement.getLineNumber()));
				stackTrace.put("classGetName"	, stackTraceElement.getClass().getName());
				stackTraces.add(stackTrace);
			}
			exceptionInfo.put("stackTrace", stackTraces);
			result.add(exceptionInfo);
		}		

		return result;
	}
	
	/**
	 * @author toshiyuki
	 *
	 */
	public class MyResponseWrapper extends HttpServletResponseWrapper
	{
		private MyResponseOutputStream os;
		private CharArrayWriter writer;
		
		/**
		 * @param response
		 */
		public MyResponseWrapper(HttpServletResponse response){
			super(response);
			this.os				= new MyResponseOutputStream();
			this.writer			= new CharArrayWriter();
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponseWrapper#getOutputStream()
		 */
		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return os;
		}
		
		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponseWrapper#getWriter()
		 */
		@Override
		public PrintWriter getWriter() throws IOException {
			return new PrintWriter(writer);
		}

		/**
		 * @return
		 */
		public String getWriteDataByJsp(){
			return writer.toString();
		}
		
		/**
		 * 
		 */
		public void clearStream(){
			this.os				= new MyResponseOutputStream();
			this.writer			= new CharArrayWriter();
		}
	}
	
	/**
	 * @author toshiyuki
	 *
	 */
	public class MyResponseOutputStream extends ServletOutputStream
	{
		private ByteArrayOutputStream baos;

		public MyResponseOutputStream()
		{
			this.baos		= new ByteArrayOutputStream();
		}
		
		@Override
		public void write(int b) throws IOException {
			baos.write(b);
		}

		@Override
		public boolean isReady() {
			// TODO 自動生成されたメソッド・スタブ
			return true;
		}

		@Override
		public void setWriteListener(WriteListener arg0) {
			// TODO 自動生成されたメソッド・スタブ
			
		}
		
		// public byte[] getWriteData() {
		// 	return baos.toByteArray();
		// }
	}
}
