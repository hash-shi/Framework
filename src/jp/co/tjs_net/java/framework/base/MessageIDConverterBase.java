package jp.co.tjs_net.java.framework.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class MessageIDConverterBase {
	public abstract void onRequestTouch(HttpServletRequest req, HttpServletResponse res);
	public abstract String convertMessageIDLogic(String messageID);
}
