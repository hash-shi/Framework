package jp.co.tjs_net.java.framework.core;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

/**
 * @author toshiyuki
 *
 */
public abstract class Index extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public abstract Class<?> getClass(String className);
	public abstract boolean init(HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception;
	public abstract void exception(IndexInformation info, Exception exception) throws Exception;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res){
		this.doPost(req, res);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res){
		
		//-----------------------------
		// 情報保持インスタンス生成
		//-----------------------------
		IndexInformation info		= new IndexInformation(this, req, res);					// 基本的にはエラーになることはない(FW側固有の処理)
		info.log.info("処理開始[MODE:" + info.paramMode + "|" + "ID:" + info.paramID + "|" + "ACTION:" + info.nowAction + "|" + "TOKEN:" + info.token + "|" + "UUID:" + info.uuids[info.uuids.length-1] + "]" );

		//-----------------------------
		// セッション状態の確認
		// デバッグ用
		//-----------------------------
		StringBuffer headerStr = new StringBuffer();
		Enumeration<String> headerNames = req.getHeaderNames();
		while(headerNames.hasMoreElements()){
			String key = headerNames.nextElement();
			headerStr.append((headerStr.length()==0?"":",") + req.getHeader(key));
		}
		
		if (req.getSession() != null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			info.log.info("セッション確認" + " " + "[IPアドレス:" + req.getRemoteAddr() + "]" + " " + "[セッションID:" + req.getSession().getId() + "]" + " " + "[セッション作成時間:" + sdf.format(new Date(req.getSession().getCreationTime())) + "]" + " " + "[セッション最終アクセス時間:" + sdf.format(new Date(req.getSession().getLastAccessedTime())) + "]" + " " + "[ReqHeader:" + headerStr.toString() + "]");	
		} else {
			info.log.info("セッションの取得に失敗しました");
		}

		
		//-----------------------------
		// キャッシュ無効
		//-----------------------------
		java.util.Calendar objCal1=java.util.Calendar.getInstance();
		java.util.Calendar objCal2=java.util.Calendar.getInstance();
		objCal2.set(1970,0,1,0,0,0);
		res.setDateHeader("Last-Modified",objCal1.getTime().getTime());
		res.setDateHeader("Expires",objCal2.getTime().getTime());
		res.setHeader("progma","no-cache");
		res.setHeader("Cache-Control","no-cache");
		
		//-----------------------------
		// 情報保持インスタンス生成
		//-----------------------------
		try { req.setCharacterEncoding("UTF-8"); } catch (UnsupportedEncodingException e) {}
		
		//-----------------------------
		// 処理開始
		// 処理開始時間の取得
		//-----------------------------
		info.start();																		// 内部的には処理時間取得のみの為、エラーになることはない(FW側固有の処理)
		
		try {
			//-------------------------
			//　処理クラス生成
			// フレームワーク用の実行コアインスタンスの生成(生成したコアインスタンスはinfo内に格納)
			//-------------------------
			info.creareCore(this);															// コアクラスの生成。コアクラスはFWの要素なので基本的にはエラーになることはない(FW側固有の処理)

			//-------------------------
			// 初期処理
			// 各プロジェクトでオーバーライドされた初期処理(どんな処理でも必ず強制的に通過する)を実行
			//-------------------------
			this.init(req, res, info);														// 初期処理。自由に記載可能

			//-------------------------
			// モジュールクラス生成
			// URLパラメータ等々から対象となるモジュールを生成
			//-------------------------
			info.createModule(this, req, res);												// モジュールの生成。Controllerファイルの定義・クラスファイルの有無によってエラーになる可能性が高い
	
			//-------------------------
			// 認証処理
			// 認証処理の実施(認証結果はinfo内に格納される)
			//-------------------------
			info.auth(this, req, res);														// 認証。アクセス権限の無いユーザーのはじくためエラーになる可能性が高い
						
			//-------------------------
			// 動作クラス初期処理
			// 入力値チェックなどを実施
			//-------------------------
			if (info.core != null && info.module != null && info.isRun()){
				if (info.getMessageIDConverter() != null){ info.getMessageIDConverter().onRequestTouch(req, res); }
				info.core.init(this, req, res, info);										// モジュールの初期処理
			}
			
			//-------------------------
			// 動作クラス実行
			// コア/モジュール/認証がすべて揃った場合に主処理の実行を行う
			//-------------------------
			if (info.core != null && info.module != null && info.isRun()){
				info.core.run(this, req, res, info);										// モジュールの実処理
			}
		} catch (Exception exp){
			//=================================================================
			// 各プロジェクトで定義している致命的エラー発生時の処理に遷移する
			//=================================================================
			try {
				this.exception(info, exp);
			} catch (Exception exp2){
				// 本当に何もできなかった場合はこちら
				info.addSystemError(exp2);
			}
		}

		//-----------------------------
		// 処理終了時間取得
		//-----------------------------
		info.end();																			// 内部的には処理時間取得のみの為、エラーになることはない(FW側固有の処理)

		//---------------------------------
		// 返却処理
		//---------------------------------
		try { info.core.response(this, req, res, info); }									// 返却処理。エラーになった場合は、Coreの返却タイプを見て適切だと思われる形式のエラーを返却する
		catch (Exception exp){
			try {
				res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, info.config.getSystemMessage("_SYS_ERROR_001"));
			} catch (Exception exp2){}
		}

		//-----------------------------
		// DB切断
		//-----------------------------
		info.closeConnections(req);															// DB切断処理。基本的にはエラーにならない
	}
}
