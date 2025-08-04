package jp.co.tjs_net.java.framework.information;

import java.util.HashMap;

import jp.co.tjs_net.java.framework.common.RecursiveNode;

/**
 * @author toshiyuki
 *
 */
public class ControllerAction extends ControllerObjectBase {
	
	// メンバ変数定義
	private String group;
	private String title;
	private HashMap<String, ControllerView> views;
	
	/**
	 * @param node
	 */
	public ControllerAction(RecursiveNode node, HashMap<String, ControllerGroup> groups, ControllerGroup common){
		// 共通系定義の読み込み
		super(node, false);

		// グループの読み込み
		this.group			= (node.getAttributes().getNamedItem("group") == null ? "" : node.getAttributes().getNamedItem("group").getTextContent());
		if (groups.containsKey(this.group)){
			this.getGroups().addScript(groups.get(this.group).getScripts());
			this.getGroups().addVBScript(groups.get(this.group).getVBScripts());
			this.getGroups().addStyle(groups.get(this.group).getStyles());
			this.getGroups().addAuthority(groups.get(this.group).getAuthorities());
			this.getGroups().addMeta(groups.get(this.group).getMetas());
			if ("".equals(this.getGroups().getHeaderPath()) && !"".equals(groups.get(this.group).getHeaderPath())){ this.getGroups().setHeaderPath(groups.get(this.group).getHeaderPath()); }
			if ("".equals(this.getGroups().getFooterPath()) && !"".equals(groups.get(this.group).getFooterPath())){ this.getGroups().setFooterPath(groups.get(this.group).getFooterPath()); }
		}
		
		// 共通
		this.getGroups().addScript(common.getScripts());
		this.getGroups().addVBScript(common.getVBScripts());
		this.getGroups().addStyle(common.getStyles());
		this.getGroups().addAuthority(common.getAuthorities());
		this.getGroups().addMeta(common.getMetas());
		if ("".equals(this.getGroups().getHeaderPath()) && !"".equals(common.getHeaderPath())){ this.getGroups().setHeaderPath(common.getHeaderPath()); }
		if ("".equals(this.getGroups().getFooterPath()) && !"".equals(common.getFooterPath())){ this.getGroups().setFooterPath(common.getFooterPath()); }
		
		// NULL定義の場合は強制的に無効とする
		if (node.getAttributes().getNamedItem("authority")	!= null){ if (node.getAttributes().getNamedItem("authority").getTextContent().equals("NULL")){ this.getGroups().clearAuthority(); 	} }
		if (node.getAttributes().getNamedItem("script")		!= null){ if (node.getAttributes().getNamedItem("script").getTextContent().equals("NULL")	){ this.getGroups().clearScript(); 		} }
		if (node.getAttributes().getNamedItem("vbScript")	!= null){ if (node.getAttributes().getNamedItem("vbScript").getTextContent().equals("NULL")	){ this.getGroups().clearVBScript(); 	} }
		if (node.getAttributes().getNamedItem("style")		!= null){ if (node.getAttributes().getNamedItem("style").getTextContent().equals("NULL")	){ this.getGroups().clearStyle(); 		} }
		if (node.getAttributes().getNamedItem("meta")		!= null){ if (node.getAttributes().getNamedItem("meta").getTextContent().equals("NULL")		){ this.getGroups().clearMeta(); 		} }
		if (this.getGroups().getHeaderPath().equals("NULL")){ this.getGroups().setHeaderPath(""); }
		if (this.getGroups().getFooterPath().equals("NULL")){ this.getGroups().setFooterPath(""); }

		// 画面タイトルの読み込み
		this.title			= (node.getAttributes().getNamedItem("title") == null ? "" : node.getAttributes().getNamedItem("title").getTextContent());
		
		// 画面一覧の読み込み
		this.views			= new HashMap<>();
		for (int count = 0 ; count < node.count("view") ; count++) {
			String id = (node.n("view", count).getAttributes().getNamedItem("return") == null ? "" : node.n("view", count).getAttributes().getNamedItem("return").getTextContent());
			if (!"".equals(id)) { this.views.put(id, new ControllerView(node.n("view", count))); }
		}		
	}

	public String getTitle() { return title; }
	public HashMap<String, ControllerView> getViews() { return views; }
}
