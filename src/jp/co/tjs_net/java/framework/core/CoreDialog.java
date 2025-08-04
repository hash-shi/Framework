package jp.co.tjs_net.java.framework.core;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.UUID;

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

public class CoreDialog extends CoreBase {
	
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
		ActionBase dialog			= (ActionBase)info.module;
		dialog.doInit(req, res);
		dialog.doRun(req, res);
		dialog.doFinish(req, res);

		//-------------------------
		// 表示する画面の確定
		//-------------------------			
		String view					= ((ActionBase)info.module).getView();
		if (view == null)			{ view = "success"; }
		if (view.equals(""))		{ view = "success"; }
		bodyPath					= ((ControllerAction)info.config.getController().getInfo().get(MODE.DIALOG).get(info.module.getID())).getViews().get(view).getPath();
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#response(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void response(Index index, HttpServletRequest req, HttpServletResponse res, final IndexInformation info) throws Exception {

		// ダイアログを一意に識別するためのUUIDの動的生成
		final UUID dialogUUID						= UUID.randomUUID();

		//-----------------------------------------------------------------
		// 画面トークン生成
		// Proc制御時に正常なリクエストか判断する為
		//-----------------------------------------------------------------
		final String token					= Common.getCsrfToken();
		HashMap<String, String> dialogTokens= null;
		if (req.getSession().getAttribute(Define.FRAMEWORK_NAME + "_DIALOG_TOKENS") == null){
			dialogTokens					= new HashMap<String, String>();
		} else {
			dialogTokens					= (HashMap<String, String>)req.getSession().getAttribute(Define.FRAMEWORK_NAME + "_DIALOG_TOKENS");
		}
		dialogTokens.put(dialogUUID.toString(), token);
		req.getSession().setAttribute(Define.FRAMEWORK_NAME + "_DIALOG_TOKENS", dialogTokens);
		
		//-----------------------------------------------------------------
		// ダイアログ用のHTMLの生成
		//-----------------------------------------------------------------
		final StringBuffer dialogResponse			= new StringBuffer();

		if (info.getSystemErrors().size() == 0 && info.getAuthorityErrors().size() == 0){
			
			MyResponseWrapper wrapperHeader		= new MyResponseWrapper(res);
			RequestDispatcher rdMainHeader		= req.getRequestDispatcher(info.config.getTemplatePath() + File.separator + headerPath);
			rdMainHeader.include(req, wrapperHeader);
			dialogResponse.append(wrapperHeader.getWriteDataByJsp());
	
			MyResponseWrapper wrapperBody		= new MyResponseWrapper(res);
			RequestDispatcher rdMainBody		= req.getRequestDispatcher(info.config.getTemplatePath() + File.separator + bodyPath);
			rdMainBody.include(req, wrapperBody);
			dialogResponse.append(wrapperBody.getWriteDataByJsp());
			
			MyResponseWrapper wrapperFooter		= new MyResponseWrapper(res);
			RequestDispatcher rdMainFooter		= req.getRequestDispatcher(info.config.getTemplatePath() + File.separator + footerPath);
			rdMainFooter.include(req, wrapperFooter);
			dialogResponse.append(wrapperFooter.getWriteDataByJsp());
		}
		
		// 返却処理
		final IndexInformation thatInfo		= info;
		PrintWriter out		= new PrintWriter(new OutputStreamWriter(res.getOutputStream(), "UTF8"),true);
		@SuppressWarnings("serial")
		String json			= this.makeResponseJSON(req, res, info, new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				String dialogResponseContents	= Common.replaceConfig(dialogResponse.toString(), "!#", "#!", new HashMap<String, String>() {{put("TITLE", ((ControllerAction)info.controller).getTitle());}});
				
		        put("uuid"				, dialogUUID.toString());										// ダイアログを一意に識別するID
		        put("token"				, token);														// ダイアログトークン			
		        put("separator"			, Define.VIEWLAYER_DIALOG_SEPARATOR);							// ダイアログレイヤーを判別する区切り文字
		        put("nowaction_id"		, "__" + Define.FRAMEWORK_NAME + "_" + "NOWACTION");			// 現在のアクションを格納しておくinputタグのID
		        put("viewlayer_id"		, "__" + Define.FRAMEWORK_NAME + "_" + "VIEWLAYER");			// 現在のレイヤーを格納しておくinputタグのID
		        put("uuids_id"			, "__" + Define.FRAMEWORK_NAME + "_" + "UUIDS");				// 現在のレイヤーのUUIDを格納しておくinputタグのID
		        put("dialogtokens_id"	, "__" + Define.FRAMEWORK_NAME + "_" + "DIALOG_TOKENS");		// ダイアログトークンを格納しておくDIVタグのID
		        put("dialog_id"			, Common.encCipher(thatInfo.config, thatInfo.module.getID()));	// 今回表示するダイアログ名を暗号化したもの
		        put("contents"			, dialogResponseContents);									// ダイアログの中身そのもの
		    }
		});
		res.setContentType("text/json; charset=" + info.config.getCharset());
		out.println(json);		
	}
}
