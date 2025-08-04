package jp.co.tjs_net.java.framework.common;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import jp.co.tjs_net.java.framework.core.Index;
import jp.co.tjs_net.java.framework.database.ConnectionBase;
import jp.co.tjs_net.java.framework.information.Config;
import jp.co.tjs_net.java.framework.information.DatabaseInformation;
import jp.co.tjs_net.java.framework.startup.Startup;

public class Common {
	
	/**
	 * @param target
	 * @param prefix
	 * @param suffix
	 * @param values
	 * @return
	 */
	public static String replaceConfig(String target, String prefix, String suffix, HashMap<String, String> values) {
		int replaceFromIndex		= -1;
		int replaceToIndex			= -1;
		String replaceKey			= new String("");
		while((replaceFromIndex = target.indexOf(prefix)) != -1) {
			replaceToIndex		= target.indexOf(suffix, replaceFromIndex);
			if (replaceToIndex != -1) {
				replaceKey = target.substring(replaceFromIndex + prefix.length(), replaceToIndex);
				String replaceString = new String("");
				if (values.containsKey(replaceKey)){ replaceString = values.get(replaceKey); }
				target		= target.replaceAll((prefix.equals("{")?"\\{":prefix) + replaceKey + (suffix.equals("}")?"\\}":suffix), replaceString);
			}
		}
		return target;
	}
	
	/**
	 * @param req
	 * @return
	 */
	public static String getLanguage(HttpServletRequest req) {
		String locale	= req.getHeader("Accept-Language");
		if (locale == null){ return ""; }
		if (locale.equals("")){ return ""; }
		
		String[] arrayLocale	= locale.split(",");
		String myLocale			= arrayLocale[0];
		if (myLocale.length() > 2) {
			myLocale = myLocale.substring(0, 2);
		}
		return myLocale;
	}

	/**
	 * @param param
	 * @return
	 */
	public static String getParam(String param){
		try {
			return new String(param.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	/**
	 * @param parent
	 * @param id
	 * @param config
	 * @return
	 */
	public static Connection getConnection(HttpServlet parent, String id, Config config){
		Connection connection							= null;
		try {
			DatabaseInformation databaseInformation		= config.getDatabases().get(id);
			String jndi									= databaseInformation.getJndi();
			ConnectionBase connectionBase				= null;
			Class<?> databaseClass						= null;
			if (parent.getClass().getSuperclass().equals(Startup.class)){ databaseClass = ((Startup)parent).getClass(databaseInformation.getConnectionClassName()); }
			if (parent.getClass().getSuperclass().equals(Index.class))  { databaseClass = ((Index)parent  ).getClass(databaseInformation.getConnectionClassName()); }
			if (databaseClass == null){ return null; }
//			connectionBase								= (ConnectionBase)databaseClass.newInstance();
			connectionBase								= (ConnectionBase)databaseClass.getDeclaredConstructor().newInstance();
			connection									= connectionBase.getConnection(jndi);
		} catch (Exception exp){
			connection									= null;
		}
		return connection;
	}
	
	/**
	 * @param config
	 * @param value
	 * @return
	 */
	public static String encCipher(Config config, String value){
		String result		= "";
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(config.getCipherKey().getBytes(), "AES"));
	        result = new String(Base64.encodeBase64(cipher.doFinal(value.getBytes())));
		} catch (Exception exp){}
		return result;
	}

	/**
	 * @param config
	 * @param value
	 * @return
	 */
	public static String decCipher(Config config, String value){
		String result		= "";
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(config.getCipherKey().getBytes(), "AES"));
	        result = new String(cipher.doFinal(Base64.decodeBase64(value.getBytes())));
		} catch (Exception exp){}
		return result;
	}

	/**
	 * @return
	 */
	private static int TOKEN_LENGTH = 16;
	public static String getCsrfToken() {
		byte token[] = new byte[TOKEN_LENGTH];
		StringBuffer buf		= new StringBuffer();
		SecureRandom random		= null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			random.nextBytes(token);
			for (int i = 0; i < token.length; i++) {
				buf.append(String.format("%02x", token[i]));
			}
		} catch (NoSuchAlgorithmException e) {}	 
		return buf.toString();
	}
}
