package jp.co.tjs_net.java.framework.message;

import java.util.HashMap;

import jakarta.servlet.http.HttpServlet;

import jp.co.tjs_net.java.framework.base.MessageBase;
import jp.co.tjs_net.java.framework.common.RecursiveNode;
import jp.co.tjs_net.java.framework.information.Config;

/**
 * @author toshiyuki
 *
 */
public class readFrameworkMessage extends MessageBase {
	
	/**
	 * @param parent
	 * @param config
	 */
	public readFrameworkMessage(HttpServlet parent, Config config) {
		super(parent, config);
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.base.MessageBase#getMessage()
	 */
	@Override
	public HashMap<String, String> getMessage() throws Exception {
		HashMap<String, String> messages		= new HashMap<>();
		try {
			RecursiveNode messageXml		= RecursiveNode.parse(getClass().getResourceAsStream("/message/message.xml"));
			RecursiveNode rootNode			= messageXml.n("messages");
			for (int count = 0 ; count < rootNode.count("message") ; count++){
				RecursiveNode messageNode	= rootNode.n("message", count);
				String messageID			= "";
				String message				= "";
				messageID					= (messageNode.getAttributes().getNamedItem("id")==null?"":messageNode.getAttributes().getNamedItem("id").getTextContent());
				message						= messageNode.getTextContent();
				messages.put(messageID, message);
			}
		} catch (Exception exp){}	
		return messages;
	}
}
