package jp.co.tjs_net.java.framework.common;

public class UtilEscape {
	/**
	 * 文字列をHTMLエスケープして返却する
	 * 
	 * @param str エスケープしたい文字列
	 * @return エスケープされた文字列
	 */
	public static String htmlspecialchars(String str) {
		if (str == null){ return ""; }
		String ret_val = new String(str);
//		String[] escape = {"&", "<", ">", "\"", "\'", "\n", "\t"};
//		String[] replace = {"&amp;", "&lt;", "&gt;", "&quot;", "&#39;", "<br />", "&#x0009;"};
		String[] escape = {"&", "<", ">", "\"", "\'", "\t"};
		String[] replace = {"&amp;", "&lt;", "&gt;", "&quot;", "&#39;", "&#x0009;"};

		for ( int i=0; i < escape.length; i++ ) {
			ret_val = ret_val.replace(escape[i], replace[i]);
		}
		return ret_val;
	}
	
	/**
	 * @param str
	 * @return
	 */
	public static String htmlspecialcharsMultiRow(String str) {
		str					= str.replaceAll("\r\n", "\n");
		String[] strs		= str.split("\n", -1);
		String ret_val		= "";
		for (int count = 0 ; count < strs.length ; count++ ){
			ret_val			+= (ret_val.equals("")?"":"<br />") + htmlspecialchars(strs[count]);
		}
		return ret_val;
	}
}
