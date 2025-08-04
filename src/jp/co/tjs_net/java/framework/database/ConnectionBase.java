package jp.co.tjs_net.java.framework.database;

import java.sql.Connection;

public abstract class ConnectionBase {
	public abstract Connection getConnection(String jndi);
}
