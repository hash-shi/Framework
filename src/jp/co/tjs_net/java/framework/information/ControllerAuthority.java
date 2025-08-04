package jp.co.tjs_net.java.framework.information;

import jp.co.tjs_net.java.framework.common.RecursiveNode;

/**
 * @author toshiyuki
 *
 */
public class ControllerAuthority extends ControllerObjectBase {
	
	// メンバ変数
	private String viewPath;									// 認証エラー発生時に表示するJSP画面パス
	private String messageAttrName;								// 認証エラー発生時に表示するJSP画面でエラーメッセージを表示するためのアトリビュート名
	private String afterScript;									// 画面表示後にキックするJavaScript(関数でも可)
	
	/**
	 * 
	 */
	public ControllerAuthority(RecursiveNode node){
		
		super(node, true);
				
		try {
			viewPath					= (node.getAttributes().getNamedItem("viewPath")		== null ? "" : node.getAttributes().getNamedItem("viewPath").getTextContent());
			messageAttrName				= (node.getAttributes().getNamedItem("messageAttrName")	== null ? "" : node.getAttributes().getNamedItem("messageAttrName").getTextContent());
			afterScript					= (node.getAttributes().getNamedItem("afterScript")		== null ? "" : node.getAttributes().getNamedItem("afterScript").getTextContent());

		} catch (Exception exp){}
	}

	public String getViewPath() 		{ return viewPath; }
	public String getMessageAttrName()	{ return messageAttrName; }
	public String getAfterScript()		{ return afterScript; }
}
