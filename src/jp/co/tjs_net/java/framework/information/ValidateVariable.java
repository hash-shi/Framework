package jp.co.tjs_net.java.framework.information;

import java.util.ArrayList;

/**
 * 
 * @author toshiyuki
 *
 */
public class ValidateVariable {
	
	private String name;
	private String dispName;
	private ArrayList<ValidateVariableCheck> checks;
	
	/**
	 * @param name
	 * @param dispName
	 */
	public ValidateVariable(String name, String dispName){
		this.name				= name;
		this.dispName			= dispName;
		this.checks				= new ArrayList<>();
	}
	
	/**
	 * @param validateVariableCheck
	 */
	public void addCheck(ValidateVariableCheck validateVariableCheck){
		this.checks.add(validateVariableCheck);
	}
	public String getName() { return name; }
	public String getDispName() { return dispName; }
	public ArrayList<ValidateVariableCheck> getChecks() { return checks; }
}
