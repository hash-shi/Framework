package jp.co.tjs_net.java.framework.information;

public class DatabaseInformation {

	private String id;
	private String jndi;
	private String connectionClassName;
	
	public DatabaseInformation(String id, String jndi, String connectionClassName){
		this.id						= id;
		this.jndi					= jndi;
		this.connectionClassName	= connectionClassName;
	}
	
	public String getId() { return id; }
	public String getJndi() { return jndi; }
	public String getConnectionClassName() { return connectionClassName; }
}
