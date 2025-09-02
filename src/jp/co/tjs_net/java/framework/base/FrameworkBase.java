package jp.co.tjs_net.java.framework.base;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public abstract class FrameworkBase {
	
	// メンバ変数定義
	protected HttpServletRequest req;					// リクエスト
	protected HttpServletResponse res;					// レスポンス
	protected IndexInformation info;					// 共通情報
	private HashMap<String, Object> contents;

	/**
	 * @param index
	 * @param req
	 * @param res
	 * @param config
	 * @param log
	 */
	public FrameworkBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		this.req				= req;		// 
		this.res				= res;		// 
		this.info				= info;		// 
		this.contents			= new HashMap<>();
	}

	// 仮想メソッド定義
	public void doInit 	(HttpServletRequest req, HttpServletResponse res) throws Exception {};
	public abstract void doRun  	(HttpServletRequest req, HttpServletResponse res) throws Exception;
	public void doFinish	(HttpServletRequest req, HttpServletResponse res) throws Exception {};
	
	/**
	 * @return
	 */
	public String getID() {
		return this.info.paramID;
	}

	/**
	 * @param param
	 * @return
	 */
	protected String getParameter(String param) {
		String result = this.req.getParameter(param);
		if (result == null){ result = ""; }
		try {
			result = new String(result.getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			result = "";
		}
		return result;
	}
	
	/**
	 * @param id
	 * @return
	 */
	protected String getMessage(String id){
		return this.info.getMessage(id);
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	protected String getMessage(String id, String... args){
		return this.info.getMessage(id, args);
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	protected String getMessage(String id, ArrayList<String> args){
		return this.info.getMessage(id, args);
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	protected String getMessage(String id, HashMap<String,String> args){
		return this.info.getMessage(id, args);
	}
	
	/**
	 * @param id
	 * @return
	 */
	protected Connection getConnection(String id, HttpServletRequest req) throws Exception {
		return info.getConnection(id, req);
	}
	
	/**
	 * @param connection
	 * @throws Exception
	 */
	public void addOtherConnection(Connection connection) {
		info.addOtherConnections(connection);
	}
	
	/**
	 * @param key
	 * @param value
	 */
	protected void addContent(String key, Object value){
		this.contents.put(key, value);
	}
	
	/**
	 * @return
	 */
	public HashMap<String,Object> getContents(){
		return this.contents;
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	protected String getTemplateFile(String id, HttpServletRequest req) throws Exception {
		return this.info.getTemplateFile(id, req);
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	protected String getTemplateFilePath(HttpServletRequest req) throws Exception {
		return this.info.getTemplateFilePath(req);
	}
	
	/**
	 * @param id
	 * @param args
	 * @return
	 */
	protected String getTemplateFileName(String id) throws Exception {
		return this.info.getTemplateFileName(id);
	}
}
