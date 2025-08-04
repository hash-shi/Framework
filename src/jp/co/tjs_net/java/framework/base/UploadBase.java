package jp.co.tjs_net.java.framework.base;

import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public abstract class UploadBase extends FrameworkBase {
	
	// メンバ変数
	protected FileItem uploadFile;
	private HashMap<String, String> params;
	
	/**
	 * @param req
	 * @param res
	 * @param info
	 */
	public UploadBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
		params			= new HashMap<>();
	}
	
	/**
	 * @param name
	 * @param value
	 */
	public void addParams(String name, String value){
		this.params.put(name, value);
	}
	
	/**
	 * @param name
	 * @return
	 */
	protected String getParam(String name){
		String result		= "";
		if (this.params.containsKey(name)){
			result			= this.params.get(name);
		}
		return result;
	}
	
	/**
	 * @param uploadFile
	 */
	public void setUploadFile(FileItem uploadFile) {
		this.uploadFile = uploadFile;
	}
}
