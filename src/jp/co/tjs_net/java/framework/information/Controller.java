package jp.co.tjs_net.java.framework.information;

import java.io.FileInputStream;
import java.util.HashMap;

import jakarta.servlet.ServletContext;

import jp.co.tjs_net.java.framework.Define;
import jp.co.tjs_net.java.framework.Define.MODE;
import jp.co.tjs_net.java.framework.common.RecursiveNode;

/**
 * @author toshiyuki
 *
 */
public class Controller {

	// メンバ変数一覧
	private ControllerGroup													common;			// 共通定義(すべてに適用される)
	private HashMap<String, ControllerGroup>								groups;			// グループ定義
	private HashMap<String, ControllerAuthority>							authorities;	// 
	private HashMap<Define.MODE, HashMap<String, ControllerObjectBase>> 	info;			// コントローラーの情報

	/**
	 * コンストラクタ
	 */
	public Controller(){
		this.common				= null;
		this.groups				= new HashMap<>();
		this.authorities		= new HashMap<>();
		this.info				= new HashMap<>();
	}
	
	/**
	 * 読み込み処理
	 * 
	 * @param sc
	 * @param controllerFilePath
	 * @return
	 */
	public boolean read(ServletContext sc, String controllerFilePath){
		
		// 結果
		boolean result = true;
		FileInputStream controllerFileIS	= null;
		
		// 読み込み開始
		try {
			// 設定ファイル読み込み準備
			controllerFileIS				= new FileInputStream(sc.getRealPath("/") + controllerFilePath);
			RecursiveNode controllerXml		= RecursiveNode.parse(controllerFileIS);
			
			// 読み込み開始
			RecursiveNode rootNode			= controllerXml.n("controller");

			//-----------------------------------------------------------------
			// Common
			//-----------------------------------------------------------------
			if (rootNode.count("common") == 1){
				RecursiveNode commonNode	= rootNode.n("common");
				this.common					= new ControllerGroup(commonNode);
			}
			
			//-----------------------------------------------------------------
			// Group
			//-----------------------------------------------------------------
			if (rootNode.count("groups") == 1){
				RecursiveNode groupsNode	= rootNode.n("groups");
				for (int count = 0 ; count < groupsNode.count("group") ; count++){
					ControllerGroup group		= new ControllerGroup(groupsNode.n("group", count));
					groups.put(group.getId(), group);
				}
			}

			//-----------------------------------------------------------------
			// Authorities
			//-----------------------------------------------------------------
			if (rootNode.count("authorities") == 1){
				RecursiveNode authoritiesNode	= rootNode.n("authorities");
				for (int count = 0 ; count < authoritiesNode.count("authority") ; count++){
					ControllerAuthority authority		= new ControllerAuthority(authoritiesNode.n("authority", count));
					authorities.put(authority.getId(), authority);
				}
			}
			
			//-----------------------------------------------------------------
			// Action
			//-----------------------------------------------------------------
			HashMap<String, ControllerObjectBase> actions	= new HashMap<>();
			if (rootNode.count("actions") == 1){
				RecursiveNode actionsNode	= rootNode.n("actions");
				for (int count = 0 ; count < actionsNode.count("action") ; count++){
					ControllerAction action		= new ControllerAction(actionsNode.n("action", count), this.groups, this.common);
					actions.put(action.getId(), action);
				}
			}
			info.put(MODE.ACTION, actions);
			
			//-----------------------------------------------------------------
			// Dialog
			//-----------------------------------------------------------------
			HashMap<String, ControllerObjectBase> dialogs	= new HashMap<>();
			if (rootNode.count("dialogs") == 1){
				RecursiveNode dialogsNode	= rootNode.n("dialogs");
				for (int count = 0 ; count < dialogsNode.count("dialog") ; count++){
					ControllerAction dialog		= new ControllerAction(dialogsNode.n("dialog", count), this.groups, this.common);
					dialogs.put(dialog.getId(), dialog);
				}
			}
			info.put(MODE.DIALOG, dialogs);
			
			//-----------------------------------------------------------------
			// Download
			//-----------------------------------------------------------------
			HashMap<String, ControllerObjectBase> downloads	= new HashMap<>();
			if (rootNode.count("downloads") == 1){
				RecursiveNode downloadsNode	= rootNode.n("downloads");
				for (int count = 0 ; count < downloadsNode.count("download") ; count++){
					ControllerDownload download	= new ControllerDownload(downloadsNode.n("download", count));
					downloads.put(download.getId(), download);
				}
			}
			info.put(MODE.DOWNLOAD, downloads);
			
			//-----------------------------------------------------------------
			// Suggest
			//-----------------------------------------------------------------
			HashMap<String, ControllerObjectBase> suggests	= new HashMap<>();
			if (rootNode.count("suggests") == 1){
				RecursiveNode suggestsNode	= rootNode.n("suggests");
				for (int count = 0 ; count < suggestsNode.count("suggest") ; count++){
					ControllerSuggest suggest	= new ControllerSuggest(suggestsNode.n("suggest", count));
					suggests.put(suggest.getId(), suggest);
				}
			}
			info.put(MODE.SUGGEST, suggests);
			
			//-----------------------------------------------------------------
			// Upload
			//-----------------------------------------------------------------
			HashMap<String, ControllerObjectBase> uploads	= new HashMap<>();
			if (rootNode.count("uploads") == 1){
				RecursiveNode uploadsNode	= rootNode.n("uploads");
				for (int count = 0 ; count < uploadsNode.count("upload") ; count++){
					ControllerUpload upload		= new ControllerUpload(uploadsNode.n("upload", count));
					uploads.put(upload.getId(), upload);
				}
			}
			info.put(MODE.UPLOAD, uploads);

			//-----------------------------------------------------------------
			// Image
			//-----------------------------------------------------------------
			HashMap<String, ControllerObjectBase> images	= new HashMap<>();
			if (rootNode.count("images") == 1){
				RecursiveNode imagesNode	= rootNode.n("images");
				for (int count = 0 ; count < imagesNode.count("image") ; count++){
					ControllerImage image		= new ControllerImage(imagesNode.n("image", count));
					images.put(image.getId(), image);
				}
			}
			info.put(MODE.IMAGE, images);
			
			//-----------------------------------------------------------------
			// Other
			//-----------------------------------------------------------------
			HashMap<String, ControllerObjectBase> others	= new HashMap<>();
			if (rootNode.count("others") == 1){
				RecursiveNode othersNode	= rootNode.n("others");
				for (int count = 0 ; count < othersNode.count("other") ; count++){
					ControllerOther other		= new ControllerOther(othersNode.n("other", count));
					others.put(other.getId(), other);
				}
			}
			info.put(MODE.OTHER, others);
			
		} catch (Exception exp){
			exp.printStackTrace();
			result = false;
		} finally {
		}

		// 設定ファイルクローズ
		if (controllerFileIS != null){
			try { controllerFileIS.close(); } catch(Exception exp){ exp.printStackTrace(); }
		}

		// 結果返却
		return result;
	}

	public ControllerGroup getCommon() { return common; }
	public HashMap<String, ControllerGroup> getGroups() { return groups; }
	public HashMap<String, ControllerAuthority> getAuthorities() { return authorities; }
	public HashMap<Define.MODE, HashMap<String, ControllerObjectBase>> getInfo() { return info; }	
}
