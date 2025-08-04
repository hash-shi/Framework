package jp.co.tjs_net.java.framework.base;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;

import jakarta.servlet.http.HttpServlet;

import jp.co.tjs_net.java.framework.common.Common;
import jp.co.tjs_net.java.framework.information.Config;

/**
 * @author toshiyuki
 *
 */
public abstract class MessageBase {
	
	private HttpServlet parent;
	private Config config;
	private HashMap<String, Connection> connections;						// この処理内のDBコネクション一覧

	public abstract HashMap<String, String> getMessage() throws Exception;	// 

	public MessageBase(HttpServlet parent, Config config){
		this.parent				= parent;
		this.config				= config;
		this.connections		= new HashMap<>();
	}
	
	/**
	 * メッセージ取得用ＤＢコネクション取得
	 * @param id
	 * @return
	 */
	protected Connection getConnection(String id){

		Connection connection				= null;
		System.out.println("MessageBase::getConnection Start[" + id + "]");
		
		if (this.connections.containsKey(id)){
			connection						= this.connections.get(id);
		} else {
			connection						= Common.getConnection(this.parent, id, this.config);
			this.connections.put(id, connection);
		}
		try {
			if (connection.getAutoCommit()==false) {
				connection.rollback();
				connection.setAutoCommit(true);
			}
		} catch (Exception e){ }	

		return connection;
	}
	
	/**
	 * メッセージ取得用ＤＢコネクションクリア
	 */
	public void finish(){
		Iterator<Connection> ite		= this.connections.values().iterator();
		while(ite.hasNext()){
			Connection connection	= (Connection)ite.next();
			if (connection == null){ return; }
			try  {
				if (connection.getAutoCommit() == false) {
					connection.rollback();
				}
				connection.setAutoCommit(true);				
				System.out.println("MessageBase::connectionClose");
				connection.close();
			} catch (Exception e) {
			}
		}
		this.connections.clear();
	}
}
