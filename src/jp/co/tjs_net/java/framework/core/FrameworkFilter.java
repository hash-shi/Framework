package jp.co.tjs_net.java.framework.core;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.Define;

/**
 * @author toshiyuki
 *
 */
public class FrameworkFilter implements Filter {

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		//---------------------------------------------------------------------
		// URI情報の取得
		//---------------------------------------------------------------------
		HttpServletRequest httpReq		= (HttpServletRequest)req;
		HttpServletResponse httpRes		= (HttpServletResponse)res;

		//---------------------------------------------------------------------
		// フィルター内で決定したい内容
		//---------------------------------------------------------------------
		String mode						= "";
		String id						= "";
		String nowAction				= "";
		String viewLayer				= "";
		String token					= "";
		String uuids					= "";

		//---------------------------------------------------------------------
		// URIを分解し、モードとIDを取得する
		//---------------------------------------------------------------------
		String servletPath				= httpReq.getServletPath();
		if (servletPath.length() >= 1){ if (servletPath.substring(0, 1).equals("/")){ servletPath = servletPath.substring(1); } }
		String[] uriSplit				= servletPath.split("/");
		if (uriSplit.length == 2){
			mode						= uriSplit[0].toUpperCase();
			id							= uriSplit[1];
		} else if (uriSplit.length == 1) {
			mode						= "action".toUpperCase();
			id							= uriSplit[0];
		} else {
			mode						= "action".toUpperCase();
			id							= "index";
		}
		
		//---------------------------------------------------------------------
		// modeが適切か否かを判断する(適切ではない場合は、トップページが表示される)
		//---------------------------------------------------------------------
		if (!Define.MODE_INFO.containsKey(mode)){
			mode						= "action".toUpperCase();
			id							= "index";
		}
		if ("".equals(id)){
			id							= "index";
		}

		//---------------------------------------------------------------------
		// 現在のアクションとレイヤーの取得
		//---------------------------------------------------------------------
		nowAction						= httpReq.getParameter("__" + Define.FRAMEWORK_NAME + "_NOWACTION") == null ? "" : httpReq.getParameter("__" + Define.FRAMEWORK_NAME + "_NOWACTION");
		viewLayer						= httpReq.getParameter("__" + Define.FRAMEWORK_NAME + "_VIEWLAYER") == null ? "" : httpReq.getParameter("__" + Define.FRAMEWORK_NAME + "_VIEWLAYER");
		token							= httpReq.getParameter("__" + Define.FRAMEWORK_NAME + "_TOKEN"    ) == null ? "" : httpReq.getParameter("__" + Define.FRAMEWORK_NAME + "_TOKEN"    );
		uuids							= httpReq.getParameter("__" + Define.FRAMEWORK_NAME + "_UUIDS"    ) == null ? "" : httpReq.getParameter("__" + Define.FRAMEWORK_NAME + "_UUIDS"    );
		
		//---------------------------------------------------------------------
		// 画面にモードとIDを渡す
		//---------------------------------------------------------------------
		httpReq.setAttribute("_" + Define.FRAMEWORK_NAME + "_" + "MODE", mode);
		httpReq.setAttribute("_" + Define.FRAMEWORK_NAME + "_" + "ID"  , id);
		httpReq.setAttribute("_" + Define.FRAMEWORK_NAME + "_" + "NOWACTION", nowAction);
		httpReq.setAttribute("_" + Define.FRAMEWORK_NAME + "_" + "VIEWLAYER", viewLayer);
		httpReq.setAttribute("_" + Define.FRAMEWORK_NAME + "_" + "TOKEN", token);
		httpReq.setAttribute("_" + Define.FRAMEWORK_NAME + "_" + "UUIDS", uuids);
		
		//---------------------------------------------------------------------
		// 処理に移行する
		//---------------------------------------------------------------------		
		chain.doFilter(httpReq, httpRes);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}
}
