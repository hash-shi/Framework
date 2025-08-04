package jp.co.tjs_net.java.framework.information;

import jp.co.tjs_net.java.framework.common.RecursiveNode;

/**
 * @author toshiyuki
 *
 */
public class ControllerView {

	private String id;
	private String path;
	
	/**
	 * @param node
	 */
	public ControllerView(RecursiveNode node){
		this.id				= node.getAttributes().getNamedItem("return").getTextContent();
		this.path			= node.getAttributes().getNamedItem("path").getTextContent();
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}
}
