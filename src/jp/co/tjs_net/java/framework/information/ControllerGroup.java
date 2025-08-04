package jp.co.tjs_net.java.framework.information;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.tjs_net.java.framework.common.RecursiveNode;

/**
 * @author toshiyuki
 *
 */
public class ControllerGroup extends ControllerObjectBase {
	
	// メンバ変数
	private ArrayList<String> authorities;						// 認証クラス
	private ArrayList<String> scripts;							// JavaScript
	private ArrayList<String> vbScripts;						// VBScript
	private ArrayList<String> styles; 							// CSS
	private ArrayList<String> metas;							// メタタグ
	private ArrayList<String> requiredParams;					// 必須パラメータ
	private String headerPath;									// ヘッダーパス
	private String footerPath;									// フッターパス
	
	/**
	 * 
	 */
	public ControllerGroup(RecursiveNode node){
		
		super(node, true);
		
		this.authorities				= new ArrayList<>();	// 認証クラス情報
		this.scripts					= new ArrayList<>();	// JavaScript情報
		this.vbScripts					= new ArrayList<>();	// VBScript情報
		this.styles						= new ArrayList<>();	// CSS情報
		this.metas						= new ArrayList<>();	// メタタグ情報
		this.requiredParams				= new ArrayList<>();	// 必須パラメータ
		
		try {
			String attrAuthority		= (node.getAttributes().getNamedItem("authority")		== null ? "" : node.getAttributes().getNamedItem("authority").getTextContent());
			String attrScript			= (node.getAttributes().getNamedItem("script")			== null ? "" : node.getAttributes().getNamedItem("script").getTextContent());
			String attrVBScript			= (node.getAttributes().getNamedItem("vbScript")		== null ? "" : node.getAttributes().getNamedItem("vbScript").getTextContent());
			String attrStyle			= (node.getAttributes().getNamedItem("style")			== null ? "" : node.getAttributes().getNamedItem("style").getTextContent());
			String attrMeta				= (node.getAttributes().getNamedItem("meta")			== null ? "" : node.getAttributes().getNamedItem("meta").getTextContent());
			String attrRequiredParam	= (node.getAttributes().getNamedItem("requiredParam")	== null ? "" : node.getAttributes().getNamedItem("requiredParam").getTextContent());
			
			String[] attrAuthorityArray		= attrAuthority.split(",");
			String[] attrScriptArray		= attrScript.split(",");
			String[] attrVBScriptArray		= attrVBScript.split(",");
			String[] attrStyleArray			= attrStyle.split(",");
			String[] attrMetaArray			= attrMeta.split(",");
			String[] attrRequiredParamArray	= attrRequiredParam.split(",");
			
			for (int count = 0 ; count < attrAuthorityArray.length		; count++){ if (!"".equals(attrAuthorityArray[count]))		{ this.authorities.add(attrAuthorityArray[count]);			} }
			for (int count = 0 ; count < attrScriptArray.length			; count++){ if (!"".equals(attrScriptArray[count]))			{ this.scripts.add(attrScriptArray[count]);					} }
			for (int count = 0 ; count < attrVBScriptArray.length		; count++){ if (!"".equals(attrVBScriptArray[count]))		{ this.vbScripts.add(attrVBScriptArray[count]);				} }
			for (int count = 0 ; count < attrStyleArray.length			; count++){ if (!"".equals(attrStyleArray[count]))			{ this.styles.add(attrStyleArray[count]);					} }
			for (int count = 0 ; count < attrMetaArray.length			; count++){ if (!"".equals(attrMetaArray[count]))			{ this.metas.add(attrMetaArray[count]); 					} }
			for (int count = 0 ; count < attrRequiredParamArray.length	; count++){ if (!"".equals(attrRequiredParamArray[count]))	{ this.requiredParams.add(attrRequiredParamArray[count]); 	} }
			
			headerPath					= (node.getAttributes().getNamedItem("header")		== null ? "" : node.getAttributes().getNamedItem("header").getTextContent());
			footerPath					= (node.getAttributes().getNamedItem("footer")		== null ? "" : node.getAttributes().getNamedItem("footer").getTextContent());

		} catch (Exception exp){}
	}

	public ArrayList<String> getAuthorities() { return authorities; }
	public ArrayList<String> getScripts() { return scripts; }
	public ArrayList<String> getVBScripts() { return vbScripts; }
	public ArrayList<String> getStyles() { return styles; }
	public ArrayList<String> getMetas() { return metas; }
	public ArrayList<String> getRequiredParams() { return requiredParams; }
	
	public String getHeaderPath() { return headerPath; }
	public String getFooterPath() { return footerPath; }

	public void setHeaderPath(String headerPath){ this.headerPath = headerPath; }
	public void setFooterPath(String footerPath){ this.footerPath = footerPath; }

	public void addAuthority(String authority)				{ this.authorities.add(authority); 			this.fixList(this.authorities); }
	public void addScript(String script)					{ this.scripts.add(script); 				this.fixList(this.scripts); }
	public void addVBScript(String vbScript)				{ this.vbScripts.add(vbScript);				this.fixList(this.vbScripts); }
	public void addStyle(String style)						{ this.styles.add(style); 					this.fixList(this.styles); }
	public void addMeta(String meta)						{ this.metas.add(meta); 					this.fixList(this.metas); }
	public void addRequiredParam(String requiredParam)		{ this.requiredParams.add(requiredParam);	this.fixList(this.requiredParams); }
	
	public void addAuthority(ArrayList<String> authorities)			{ this.authorities.addAll(authorities);			this.fixList(this.authorities); }
	public void addScript(ArrayList<String> scripts)				{ this.scripts.addAll(scripts);					this.fixList(this.scripts); }
	public void addVBScript(ArrayList<String> vbScripts)			{ this.vbScripts.addAll(vbScripts);				this.fixList(this.vbScripts); } 
	public void addStyle(ArrayList<String> styles)					{ this.styles.addAll(styles);					this.fixList(this.styles); }
	public void addMeta(ArrayList<String> metas)					{ this.metas.addAll(metas);						this.fixList(this.metas); }
	public void addRequiredParam(ArrayList<String> requiredParams)	{ this.requiredParams.addAll(requiredParams);	this.fixList(this.requiredParams); }
	
	private ArrayList<String> fixList(ArrayList<String> param){
		HashMap<String, Integer> checker = new HashMap<>();
		for (int count = 0 ; count < param.size() ; count++){ checker.put(param.get(count), 0); }
		for (int count = 0 ; count < param.size() ; count++){
			if ((Integer)checker.get(param.get(count)) == 0){
				checker.put(param.get(count), 1);
			} else {
				param.remove(count);
				count--;
			}
		}
		return param;
	}
	
	public void clearAuthority(){ this.authorities.clear(); }
	public void clearScript(){ this.scripts.clear(); }
	public void clearVBScript(){ this.scripts.clear(); }
	public void clearStyle(){ this.styles.clear(); }
	public void clearMeta(){ this.metas.clear(); }
}
