package jp.co.tjs_net.java.framework.database;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public class PreparedStatementFactory {

	// メンバ変数
	private HashMap<String, PreparedStatementFactoryDefinition> setterDefinitions;
	private ArrayList<PreparedStatementFactoryInformation> statementValues;
	
	/**
	 * 
	 */
	public PreparedStatementFactory() {
		this.statementValues				= new ArrayList<>();
		this.setterDefinitions				= new HashMap<>();
		this.setterDefinitions.put("String"		, new PreparedStatementFactoryDefinition("String"		, String.class					, String.class						, true));
		this.setterDefinitions.put("Long"  		, new PreparedStatementFactoryDefinition("Long"			, long.class					, Long.class						, false));
		this.setterDefinitions.put("Int"		, new PreparedStatementFactoryDefinition("Int"			, int.class						, Integer.class						, false));
		this.setterDefinitions.put("Date"		, new PreparedStatementFactoryDefinition("Date"			, java.sql.Date.class			, java.sql.Date.class				, true));
		this.setterDefinitions.put("Timestamp"	, new PreparedStatementFactoryDefinition("Timestamp"	, java.sql.Timestamp.class		, java.sql.Timestamp.class			, true));
		this.setterDefinitions.put("BigDecimal"	, new PreparedStatementFactoryDefinition("BigDecimal"	, java.math.BigDecimal.class	, java.math.BigDecimal.class		, false));
		this.setterDefinitions.put("Bytes"		, new PreparedStatementFactoryDefinition("Bytes"		, byte[].class					, byte[].class						, false));
	}
	
	/**
	 * @param setterName
	 * @param value
	 */
	public void addValue(String setterName, Object value){
		this.statementValues.add(new PreparedStatementFactoryInformation(setterName, value));
	}

	/**
	 * @param inSql
	 * @return
	 */
	public String addInStrings(String inSql){
		return this.addInStrings(inSql, "String");
	}
	
	/**
	 * @param setterName
	 * @param inSql
	 * @return
	 */
	public String addInStrings(String inSql, String type){
		StringBuffer result		= new StringBuffer();
		result.append("(");
		String[] inSqls		= inSql.split(",");
		for (int count = 0 ; count < inSqls.length ; count++){
			String val		= new String(inSqls[count]);
			if (val.indexOf("(") == 0){ val = val.substring("(".length()); }
			if (val.lastIndexOf(")") == val.length() - ")".length()){ val = val.substring(0, val.length()-")".length()); }
			if ("'".equals(val.substring(0,1)) && "'".equals(val.substring(val.length()-1,val.length()))){ val = val.substring(1, val.length() - 1); }
			Object objValue		= null;
			if ("String".equals(type))	{ objValue = val; }
			if ("Int".equals(type))		{ objValue = Integer.parseInt(val); }
			if ("Long".equals(type))	{ objValue = Long.parseLong(val); }
			this.statementValues.add(new PreparedStatementFactoryInformation(type, objValue));
			if (result.length()!=1){ result.append(","); }result.append("?");
		}
		result.append(")");
		return result.toString();
	}
	
	/**
	 * @param types
	 */
	public void addOutput(int types){
		this.statementValues.add(new PreparedStatementFactoryInformation(types));
	}
	
	/**
	 * @param setterName
	 * @param value
	 */
	public void clear(){
		this.statementValues.clear();
	}
	
	/**
	 * @param preparedStatement
	 * @throws Exception
	 */
	public void setPreparedStatement(Object preparedStatement) throws Exception {
		Method method;
		for (int count = 1 ; count <= this.statementValues.size() ; count++){
			PreparedStatementFactoryInformation information			= this.statementValues.get(count-1);
			if (information.isOutput == false){
				PreparedStatementFactoryDefinition setterDefinitions	= this.setterDefinitions.get(information.getSetterType());
				method													= PreparedStatement.class.getMethod("set" + information.getSetterType(), int.class, setterDefinitions.getMethodClass());
				method.invoke(preparedStatement, count, setterDefinitions.castClass.cast(information.getValue()));
			} else {
				method													= CallableStatement.class.getMethod("registerOutParameter", int.class, int.class);
				method.invoke(preparedStatement, count, information.getOutputType());
			}
		}
	}
	
	/**
	 * @param preparedStatement
	 * @param sql
	 * @param info
	 * @throws Exception
	 */
	public void setPreparedStatement(Object preparedStatement, String sql, IndexInformation info) throws Exception {

		//---------------------------------------------------------------------
		// SQLログの出力
		//---------------------------------------------------------------------
		try {
			StringBuffer logSql	= new StringBuffer();
			String[] sqlSep		= sql.split("\\?");
			for (int count = 0 ; count < sqlSep.length ; count++){
				logSql.append(sqlSep[count]);
				if (statementValues.size() > count){
					PreparedStatementFactoryInformation information			= statementValues.get(count);
					
					boolean isNull											= false;
					String value											= "";
					
					// OUTPUTオブジェクトとか否かを判断する
					if (information.isOutput == false){
						PreparedStatementFactoryDefinition setterDefinitions	= this.setterDefinitions.get(information.getSetterType());
						if (information.getValue() != null){
							if (information.getSetterType().equals("Bytes")){
								value										= "[Bytes]";
							} else {
								value										= setterDefinitions.castClass.cast(information.getValue()).toString();
							}
						} else {
							value											= "NULL";
							isNull											= true;
						}
						if (setterDefinitions.useQuotation && !isNull){ logSql.append("'"); }
						if (setterDefinitions.useQuotation && !isNull){ value = value.replaceAll("'", "''"); }
						logSql.append(value);
						if (setterDefinitions.useQuotation && !isNull){ logSql.append("'"); }
					} else {
						value												= "[OUTPUT]";
						logSql.append(value);
					}
				}
			}
			info.log.info(logSql.toString());
		} catch (Exception exp){
			try {
				info.log.info(sql);
				for (int count = 0 ; count < statementValues.size() ; count++){
					PreparedStatementFactoryInformation information			= statementValues.get(count);
					PreparedStatementFactoryDefinition setterDefinitions	= this.setterDefinitions.get(information.getSetterType());
					String value											= "";
					if (information.isOutput == false){
						if (information.getValue() != null){
							if (information.getSetterType().equals("Bytes")){
								value										= "[Bytes]";
							} else {
								value										= setterDefinitions.castClass.cast(information.getValue()).toString();
							}
						} else {
							value											= "NULL";
						}
					} else {
						value												= "[OUTPUT]";
					}
					info.log.info("[" + (count + 1) + "]:" + value);
				}
			} catch (Exception exp2){
			}
		}

		//---------------------------------------------------------------------
		// 値の設定
		//---------------------------------------------------------------------
		this.setPreparedStatement(preparedStatement);
	}
		
	/**
	 * @author toshiyuki
	 *
	 */
	private class PreparedStatementFactoryInformation {
		private boolean isOutput;
		private String setterType;
		private Object value;
		private int outputType;
		public PreparedStatementFactoryInformation(String setterType, Object value){
			this.isOutput		= false;
			this.setterType		= setterType;
			this.value			= value;
			this.outputType		= -1;
		}
		public PreparedStatementFactoryInformation(int outputType){
			this.isOutput		= true;
			this.outputType		= outputType;
		}
		public String getSetterType() {
			return setterType;
		}
		public Object getValue() {
			return value;
		}
		public int getOutputType(){
			return outputType;
		}
	}

	/**
	 * @author toshiyuki
	 *
	 */
	@SuppressWarnings("unused")
	private class PreparedStatementFactoryDefinition {
		private String type;
		private Class<?> methodClass;
		private Class<?> castClass;
		private boolean useQuotation;
		public PreparedStatementFactoryDefinition(String type, Class<?> methodClass, Class<?> castClass, boolean useQuotation){
			this.type				= type;
			this.methodClass		= methodClass;
			this.castClass			= castClass;
			this.useQuotation		= useQuotation;
		}
		public String getType() {
			return type;
		}
		public Class<?> getMethodClass() {
			return methodClass;
		}
		public Class<?> getCastClass() {
			return castClass;
		}
		public boolean useQuotation(){
			return useQuotation;
		}
	}
}
