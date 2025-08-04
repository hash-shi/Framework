package jp.co.tjs_net.java.framework.core;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.Define;
import jp.co.tjs_net.java.framework.Define.MODE;
import jp.co.tjs_net.java.framework.base.ActionBase;
import jp.co.tjs_net.java.framework.common.Common;
import jp.co.tjs_net.java.framework.information.ControllerAction;
import jp.co.tjs_net.java.framework.information.ControllerObjectBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;
import jp.co.tjs_net.java.framework.information.IndexInformation.AuthorityErrorInformation;

/**
 * @author toshiyuki
 *
 */
public class CoreAction extends CoreBase {

	// メンバ変数
	private String headerPath;
	private String bodyPath;
	private String footerPath;
	
	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#getController(lips.fw.fsapp.information.IndexInformation)
	 */
	@Override
	public ControllerObjectBase getController(IndexInformation info) {
		return info.config.getController().getInfo().get(info.mode.mode).get(info.paramID);
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#getModuleClassName(lips.fw.fsapp.information.IndexInformation)
	 */
	@Override
	public String getModuleClassName(IndexInformation info) {
		return info.config.getController().getInfo().get(info.mode.mode).get(info.paramID).getClassName();
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

		//-------------------------
		// 必須パラメータチェック
		//-------------------------
		boolean isRequiredParams			= true;
		ArrayList<String> requiredParams	= ((ControllerAction)info.config.getController().getInfo().get(MODE.ACTION).get(info.module.getID())).getGroups().getRequiredParams();
		for (int count = 0 ; count < requiredParams.size() ; count++){
			String requiredParam			= requiredParams.get(count);
			if (req.getParameter(requiredParam) == null){
				isRequiredParams			= false;
				break;
			}
		}
		if (isRequiredParams == false){
			info.addSystemError(new Exception(info.config.getSystemMessage("_SYS_ERROR_007")));
			return;
		}
		
		//-------------------------
		// JSPパスの設定
		//-------------------------
		headerPath					= null;
		bodyPath					= null;
		footerPath					= null;

		//-------------------------
		// ヘッダー・フッターの設定
		//-------------------------
		headerPath					= info.controller.getGroups().getHeaderPath();
		footerPath					= info.controller.getGroups().getFooterPath();			
		
		//-------------------------
		// 処理
		//-------------------------			
		ActionBase action			= (ActionBase)info.module;
		action.doInit(req, res);
		action.doRun(req, res);
		action.doFinish(req, res);
		
		//-------------------------
		// 表示する画面の確定
		//-------------------------			
		String view					= ((ActionBase)info.module).getView();
		if (view == null)			{ view = "success"; }
		if (view.equals(""))		{ view = "success"; }
		bodyPath					= ((ControllerAction)info.config.getController().getInfo().get(MODE.ACTION).get(info.module.getID())).getViews().get(view).getPath();
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#response(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@Override
	public void response(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		//-------------------------
		// HTML出力
		//-------------------------
		res.setContentType("text/html; charset=" + info.config.getCharset());
		PrintWriter out				= res.getWriter();
		if (info.getSystemErrors().size() == 0 && info.getAuthorityErrors().size() == 0){
			this.responseSuccess(index, req, res, info, out);
		} else {
			if (info.getSystemErrors().size() != 0){
				this.responseSystemError(index, req, res, info, out);
			} else if (info.getAuthorityErrors().size() != 0){
				this.responseAuthorityError(index, req, res, info, out);
			}
		}
	}
	
	/**
	 * @param index
	 * @param req
	 * @param res
	 * @param info
	 * @param out
	 */
	private void responseSuccess(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info, PrintWriter out) throws Exception {

		ControllerAction nowController		= (ControllerAction)info.controller;
		
		//-----------------------------------------------------------------
		// 画面トークン生成
		// Proc制御時に正常なリクエストか判断する為
		//-----------------------------------------------------------------
		String token						= Common.getCsrfToken();
		req.getSession().setAttribute(Define.FRAMEWORK_NAME + "_TOKEN", token);
		req.getSession().setAttribute(Define.FRAMEWORK_NAME + "_DIALOG_TOKENS", null);

		//-----------------------------------------------------------------
		// HTML生成開始
		//-----------------------------------------------------------------
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("	<head>");
		out.println("		<meta charset=\"" + info.config.getCharset() + "\" />");
		out.println("		<meta http-equiv=\"Pragma\" content=\"no-cache\">");
		out.println("		<meta http-equiv=\"Cache-Control\" content=\"no-cache\">");
		//-----------------------------------------------------------------
		// 読込metaタグ
		//-----------------------------------------------------------------
		for (int count = 0 ; count < nowController.getGroups().getMetas().size() ; count++){
			out.println(nowController.getGroups().getMetas().get(count));
		}
		out.println("		<title>" + nowController.getTitle() + "</title>");
		out.println("		<script type=\"text/javascript\" src=\"./javascript/jquery\" charset=\"utf-8\"></script>");
		out.println("		<script type=\"text/javascript\" src=\"./javascript/framework\" charset=\"utf-8\"></script>");
		out.println("		<script type=\"text/javascript\" src=\"./javascript/_message\" charset=\"utf-8\"></script>");
		
		//-----------------------------------------------------------------
		// 読込JavaScript
		//-----------------------------------------------------------------
		String rootPath = req.getServletContext().getRealPath("");
		for (int count = 0 ; count < nowController.getGroups().getScripts().size() ; count++){
			long lastModified = System.currentTimeMillis();
			try {
				String nowScript = nowController.getGroups().getScripts().get(count);
				File nowScriptFile = new File(rootPath + "/" + nowScript);
				lastModified  = nowScriptFile.lastModified();
			} catch (Exception exp){}
			out.println("		<script type=\"text/javascript\" src=\"" + nowController.getGroups().getScripts().get(count) + "?" + Long.toString(lastModified) + "\" charset=\"utf-8\" ></script>");
		}

		//-----------------------------------------------------------------
		// 読込VBScript
		//-----------------------------------------------------------------
		for (int count = 0 ; count < nowController.getGroups().getVBScripts().size() ; count++){
			out.println("		<script language=\"VBScript\" type=\"text/vbscript\" src=\"" + nowController.getGroups().getVBScripts().get(count) + "\" charset=\"Shift-JIS\" ></script>");
		}

		//-----------------------------------------------------------------
		// 読込StyleSheet
		//-----------------------------------------------------------------
		for (int count = 0 ; count < nowController.getGroups().getStyles().size() ; count++){
			out.println("		<link href=\"" + nowController.getGroups().getStyles().get(count) + "\" rel=\"stylesheet\" type=\"text/css\" />");
		}
		
		//-----------------------------------------------------------------
		// 
		//-----------------------------------------------------------------
		out.println("	</head>");
		out.println("	<body>");
		out.println("		<form id=\"inputform\">");
		out.println("			<input type=\"hidden\" name=\"__" + Define.FRAMEWORK_NAME + "_NOWACTION\" id=\"__" + Define.FRAMEWORK_NAME + "_NOWACTION\" value=\"" + Common.encCipher(info.config, info.module.getID()) + "\" />");
		out.println("			<input type=\"hidden\" name=\"__" + Define.FRAMEWORK_NAME + "_VIEWLAYER\" id=\"__" + Define.FRAMEWORK_NAME + "_VIEWLAYER\" value=\"" + Common.encCipher(info.config, info.module.getID()) + "\" />");
		out.println("			<input type=\"hidden\" name=\"__" + Define.FRAMEWORK_NAME + "_TOKEN\"     id=\"__" + Define.FRAMEWORK_NAME + "_TOKEN\"     value=\"" + token  + "\" />");
		out.println("			<input type=\"hidden\" name=\"__" + Define.FRAMEWORK_NAME + "_UUIDS\"     id=\"__" + Define.FRAMEWORK_NAME + "_UUIDS\"     value=\"\" />");
		out.println("			<div id=\"__" + Define.FRAMEWORK_NAME + "_DIALOG_TOKENS\">");
		out.println("			</div>");
		
		//-----------------------------------------------------------------
		// ヘッダーJSPの出力
		//-----------------------------------------------------------------
		if (headerPath != null && !headerPath.equals("")){
			// try { RequestDispatcher header = req.getRequestDispatcher(info.config.getTemplatePath() + File.separator + headerPath); header.include(req, res); }
			// catch (Exception exp)	{}			
			// JSP複数設定に対応
			String[] headerPaths = headerPath.split(",");
			for (int i = 0; i < headerPaths.length; i++) {
				try { RequestDispatcher header = req.getRequestDispatcher(info.config.getTemplatePath() + File.separator + headerPaths[i]); header.include(req, res); }
				catch (Exception exp)	{}
			}
		}
		
		//-----------------------------------------------------------------
		// メインJSPの出力
		//-----------------------------------------------------------------
		if (bodyPath != null && !bodyPath.equals("")){
			// try { RequestDispatcher body = req.getRequestDispatcher(info.config.getTemplatePath() + File.separator + bodyPath); body.include(req, res); }
			// catch (Exception exp)	{}
			// JSP複数設定に対応
			String[] bodyPaths = bodyPath.split(",");
			for (int i = 0; i < bodyPaths.length; i++) {
				try { RequestDispatcher body = req.getRequestDispatcher(info.config.getTemplatePath() + File.separator + bodyPaths[i]); body.include(req, res); }
				catch (Exception exp)	{}
			}
		}

		//-----------------------------------------------------------------
		// フッターJSPの出力
		//-----------------------------------------------------------------
		if (footerPath != null && !footerPath.equals("")){
			// try { RequestDispatcher footer = req.getRequestDispatcher(info.config.getTemplatePath() + File.separator + footerPath); footer.include(req, res); }
			// catch (Exception exp)	{}
			// JSP複数設定に対応
			String[] footerPaths = footerPath.split(",");
			for (int i = 0; i < footerPaths.length; i++) {
				try { RequestDispatcher footer = req.getRequestDispatcher(info.config.getTemplatePath() + File.separator + footerPaths[i]); footer.include(req, res); }
				catch (Exception exp)	{}
			}
		}

		//-----------------------------------------------------------------
		// HTML閉じ
		//-----------------------------------------------------------------
		out.println("		</form>");
		out.println("	</body>");
		out.println("</html>");
	}
	
	/**
	 * @param index
	 * @param req
	 * @param res
	 * @param info
	 * @param out
	 */
	protected void responseSystemError(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info, PrintWriter out) throws Exception {

		//-----------------------------------------------------------------
		// システムエラー用のHTMLの生成
		//-----------------------------------------------------------------
		req.setAttribute(info.config.getErrorsAttrName(), info.getSystemErrors());
		
		final StringBuffer systemErrorResponse		= new StringBuffer();
		MyResponseWrapper wrapperBody				= new MyResponseWrapper(res);
		RequestDispatcher rdMainBody				= req.getRequestDispatcher(info.config.getSystemErrorJspPath());
		rdMainBody.include(req, wrapperBody);
		systemErrorResponse.append(wrapperBody.getWriteDataByJsp());
		
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("	<head>");
		out.println("		<meta charset=\"" + info.config.getCharset() + "\" />");
		out.println("		<meta http-equiv=\"Pragma\" content=\"no-cache\">");
		out.println("		<meta http-equiv=\"Cache-Control\" content=\"no-cache\">");
		out.println("		<title></title>");
		out.println("		<script type=\"text/javascript\" src=\"./javascript/jquery\" charset=\"utf-8\"></script>");
		out.println("		<script type=\"text/javascript\" src=\"./javascript/framework\" charset=\"utf-8\"></script>");
		out.println("		<script type=\"text/javascript\" src=\"./javascript/_message\" charset=\"utf-8\"></script>");
		out.println("	</head>");
		out.println("	<body>");
		out.println("		<form id=\"inputform\">");
		out.println(systemErrorResponse.toString());
		out.println("		</form>");
		out.println("	</body>");
		out.println("</html>");
	}
	
	/**
	 * @param index
	 * @param req
	 * @param res
	 * @param info
	 * @param out
	 */
	protected void responseAuthorityError(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info, PrintWriter out) throws Exception {

		//-----------------------------------------------------------------
		// 認証エラーJSPの出力
		//-----------------------------------------------------------------
		// 対象認証エラーIDの取得
		AuthorityErrorInformation authorityErrorInformation = info.getAuthorityErrors().get(0);

		//-----------------------------------------------------------------
		// 認証エラー用のHTMLの生成
		//-----------------------------------------------------------------
		req.setAttribute(info.config.getController().getAuthorities().get(authorityErrorInformation.getAuthorityID()).getMessageAttrName(), authorityErrorInformation.getErrorMessage());
		
		final StringBuffer authorityErrorResponse	= new StringBuffer();
		MyResponseWrapper wrapperBody				= new MyResponseWrapper(res);
		RequestDispatcher rdMainBody				= req.getRequestDispatcher(info.config.getTemplatePath() + info.config.getController().getAuthorities().get(authorityErrorInformation.getAuthorityID()).getViewPath());
		rdMainBody.include(req, wrapperBody);
		authorityErrorResponse.append(wrapperBody.getWriteDataByJsp());
		
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("	<head>");
		out.println("		<meta charset=\"" + info.config.getCharset() + "\" />");
		out.println("		<meta http-equiv=\"Pragma\" content=\"no-cache\">");
		out.println("		<meta http-equiv=\"Cache-Control\" content=\"no-cache\">");
		out.println("		<title></title>");
		out.println("		<script type=\"text/javascript\" src=\"./javascript/jquery\" charset=\"utf-8\"></script>");
		out.println("		<script type=\"text/javascript\" src=\"./javascript/framework\" charset=\"utf-8\"></script>");
		out.println("		<script type=\"text/javascript\" src=\"./javascript/_message\" charset=\"utf-8\"></script>");
		out.println("	</head>");
		out.println("	<body>");
		out.println("		<form id=\"inputform\">");
		out.println(authorityErrorResponse.toString());
		out.println("		</form>");
		out.println("		<script type=\"text/javascript\">");
		out.println("			$(document).ready(function() {");
		out.println("			" + info.config.getController().getAuthorities().get(authorityErrorInformation.getAuthorityID()).getAfterScript());
		out.println("			});");
		out.println("		</script>");
		out.println("	</body>");
		out.println("</html>");
	}
}
