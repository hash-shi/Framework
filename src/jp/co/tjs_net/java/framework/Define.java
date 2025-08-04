package jp.co.tjs_net.java.framework;

import java.util.HashMap;
import java.util.Map;

public class Define {
	//=========================================================================
	// 編集可能定数
	//=========================================================================
	public static String FRAMEWORK_NAME					= "TJSJavaFramework";
	public static String COMPANY_NAME					= "TJS";
	public static String FRAMEWORK_PACKAGE				= "jp.co.tjs_net.java.framework";
	public static String VIEWLAYER_DIALOG_SEPARATOR		= "@=>@";
	
	//=========================================================================
	// 編集不可定数
	//=========================================================================
	// 動作モード一覧
	public static enum MODE {
		 ACTION
		,PROCESS
		,DIALOG
		,JAVASCRIPT
		,DOWNLOAD
		,UPLOAD
		,SUGGEST
		,IMAGE
		,OTHER
	}
	
	// 動作モードに対応する返却形式(エラー時のデフォルト返却方式として利用)
	public static enum RESPONSE_TYPE {
		 HTML
		,JSON
		,META
		,NONE
	}
	
	// 処理モードの定義
	public static Map<String, MODE_DETAIL> MODE_INFO = new HashMap<String, MODE_DETAIL>() {
		private static final long serialVersionUID = 1L; {
	        put(MODE.ACTION.name()		, new MODE_DETAIL(MODE.ACTION		, RESPONSE_TYPE.HTML	, FRAMEWORK_PACKAGE + ".core.CoreAction"		));
	        put(MODE.PROCESS.name()		, new MODE_DETAIL(MODE.PROCESS		, RESPONSE_TYPE.JSON	, FRAMEWORK_PACKAGE + ".core.CoreProcess"		));
	        put(MODE.DIALOG.name()		, new MODE_DETAIL(MODE.DIALOG		, RESPONSE_TYPE.JSON	, FRAMEWORK_PACKAGE + ".core.CoreDialog"		));
	        put(MODE.JAVASCRIPT.name()	, new MODE_DETAIL(MODE.JAVASCRIPT	, RESPONSE_TYPE.NONE	, FRAMEWORK_PACKAGE + ".core.CoreJavascript"	));
	        put(MODE.DOWNLOAD.name()	, new MODE_DETAIL(MODE.DOWNLOAD		, RESPONSE_TYPE.HTML	, FRAMEWORK_PACKAGE + ".core.CoreDownload"		));
	        put(MODE.UPLOAD.name()		, new MODE_DETAIL(MODE.UPLOAD		, RESPONSE_TYPE.JSON	, FRAMEWORK_PACKAGE + ".core.CoreUpload"		));
	        put(MODE.SUGGEST.name()		, new MODE_DETAIL(MODE.SUGGEST		, RESPONSE_TYPE.JSON	, FRAMEWORK_PACKAGE + ".core.CoreSuggest"		));
	        put(MODE.IMAGE.name()		, new MODE_DETAIL(MODE.IMAGE		, RESPONSE_TYPE.NONE	, FRAMEWORK_PACKAGE + ".core.CoreImage"			));
	        put(MODE.OTHER.name()		, new MODE_DETAIL(MODE.OTHER		, RESPONSE_TYPE.HTML	, FRAMEWORK_PACKAGE + ".core.CoreOther"			));
		}
	};

	// モード詳細情報の保持クラス
	public static class MODE_DETAIL{
		public MODE mode;
		public RESPONSE_TYPE responseType;
		public String coreClassName;
		public MODE_DETAIL(MODE mode, RESPONSE_TYPE responseType, String coreClassName){
			this.mode				= mode;
			this.responseType		= responseType;
			this.coreClassName		= coreClassName;
		}
	}
}
