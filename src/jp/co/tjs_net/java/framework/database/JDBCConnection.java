package jp.co.tjs_net.java.framework.database;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JDBCConnection extends ConnectionBase {
	
	@Override
	public Connection getConnection(String jndi) {
		Connection connection = null;
		InitialContext initContext;
		try {
			initContext = new InitialContext();
			try {
				DataSource ds = (DataSource)initContext.lookup(jndi);
				connection = ds.getConnection();
			} catch(Exception exp) {
				try {
					DataSource ds = (DataSource)initContext.lookup("java:comp/env/" + jndi);
					connection = ds.getConnection();
				} catch(Exception exp2) {
					connection = null;
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return connection;		
	}
}
