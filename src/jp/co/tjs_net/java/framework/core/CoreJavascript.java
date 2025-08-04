package jp.co.tjs_net.java.framework.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.FrameworkBase;
import jp.co.tjs_net.java.framework.base.FrameworkEmpty;
import jp.co.tjs_net.java.framework.information.ControllerObjectBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

/**
 * @author toshiyuki
 *
 */
public class CoreJavascript extends CoreBase {
	
	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#getController(lips.fw.fsapp.information.IndexInformation)
	 */
	@Override
	public ControllerObjectBase getController(IndexInformation info) {
		return null;
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#getModuleClassName(lips.fw.fsapp.information.IndexInformation)
	 */
	@Override
	public String getModuleClassName(IndexInformation info) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#init(lips.fw.fsapp.core.Index, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.IndexInformation)
	 */
	@Override
	public void init(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		// 処理なし
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#run(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@Override
	public void run(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		// 実処理なし
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#response(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@Override
	public void response(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {

		// Frameworkが保持ているJavaScriptの返却処理
		BufferedReader bufferReader	= null;
		PrintWriter out				= null;
		
		if (info.module.getID().equals("_message")){
			//-----------------------------------------------------------------
			// ローカル用メッセージファイルの読み込み
			//-----------------------------------------------------------------
			HashMap<String, String> messages	= info.config.getMessages();
			Iterator<String> iterator			= messages.keySet().iterator();
			out = new PrintWriter(new OutputStreamWriter(res.getOutputStream(), "UTF8"),true);

			// メッセージファイルの返却
			StringBuffer messageResponse		= new StringBuffer();
			messageResponse.append("var isDEBUG   = " + (info.config.isDebug()?"true":"false") + ";" + "\n");
			messageResponse.append("var ajaxASYNC = " + (info.config.ajaxAsync()?"true":"false") + ";" + "\n");
			
			if (info.config.isMessageUseJavaScript()){
				while (iterator.hasNext()){
					try {
						String id						= iterator.next();
						String message					= messages.get(id);
						messageResponse.append("var " + "MSG_" + id + " = \"" + message.replaceAll("\"", "\\\\\"") + "\";" + "\n");
					} catch (Exception exp){}
				}
			}
			res.setContentType("text/javascript; charset=UTF-8");
			res.setContentLength(messageResponse.toString().getBytes("UTF-8").length);
			out.println(messageResponse.toString());
		} else {
			//-----------------------------------------------------------------
			// JavaScriptファイルの読み込み
			//-----------------------------------------------------------------
			//URL javascriptURL	= getClass().getResource("/script/" + info.module.getID() + ".js");
			//File javascriptFile = new File(javascriptURL.toURI());
			bufferReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/script/" + info.module.getID() + ".js"), "UTF8"));
			out = new PrintWriter(new OutputStreamWriter(res.getOutputStream(), "UTF8"),true);
			
			// JavaScript返却処理
			StringBuilder javascriptResponse	= new StringBuilder();
			String javascriptText = bufferReader.readLine();
			while(javascriptText != null){
				javascriptResponse.append(javascriptText+"\n");
				javascriptText = bufferReader.readLine();
			}
			javascriptResponse.append(javascriptText+"\n");
			
			res.setContentType("text/javascript; charset=UTF-8");
			res.setContentLength(javascriptResponse.toString().getBytes("UTF-8").length);
			out.println(javascriptResponse.toString());
	
			// JavaScript読み込み完了
			if (bufferReader != null){ try { bufferReader.close(); } catch (Exception exp){} }
		}
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
	public FrameworkBase createModule(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		FrameworkBase module						= new FrameworkEmpty(req, res, info);
		return module;
	}
}
