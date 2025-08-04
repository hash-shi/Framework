package jp.co.tjs_net.java.framework.information;

import jp.co.tjs_net.java.framework.common.RecursiveNode;

/**
 * @author toshiyuki
 *
 */
public class ControllerObjectBase {
	
	// メンバ変数
	private String id;
	private String className;
	private ControllerGroup groups;

	/**
	 * @param node
	 */
	public ControllerObjectBase(RecursiveNode node, boolean isGroup) {
		this.id				= (node.getAttributes().getNamedItem("id") == null ? "" : node.getAttributes().getNamedItem("id").getTextContent());
		this.className		= (node.getAttributes().getNamedItem("class") == null ? "" : node.getAttributes().getNamedItem("class").getTextContent());
		if (isGroup == false){
			this.groups			= new ControllerGroup(node);
		}
	}
	
	public String getId() { return id; }
	public String getClassName() { return className; }
	public ControllerGroup getGroups() { return groups; }
}
