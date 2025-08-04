package jp.co.tjs_net.java.framework.startup;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

import jp.co.tjs_net.java.framework.Define;
import jp.co.tjs_net.java.framework.information.Config;

/**
 * @author toshiyuki
 *
 */
public abstract class Startup extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public abstract Class<?> getClass(String className);
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {

		//---------------------------------------------------------------------
		// 開始ログ
		//---------------------------------------------------------------------
		System.out.println(Define.FRAMEWORK_NAME + " Start!!");
		
		//---------------------------------------------------------------------
		// ServletContextの取得
		//---------------------------------------------------------------------
		ServletContext sc = getServletContext();
		
		//---------------------------------------------------------------------
		// 設定ファイル読み込み
		//---------------------------------------------------------------------
		System.out.println("Config XML read start!!");
		Config config = new Config();
		if (!config.read(this,sc,null))	{ System.out.println("Config XML read error!!");							}
		else							{ System.out.println(Define.FRAMEWORK_NAME + " Config read complete!!");	}
		sc.setAttribute(Define.FRAMEWORK_NAME + "_FRAMEWORK_CONFIG", config);
		
		//---------------------------------------------------------------------
		// 終了ログ
		//---------------------------------------------------------------------
		System.out.println(Define.FRAMEWORK_NAME + " End!!");
	}
}
