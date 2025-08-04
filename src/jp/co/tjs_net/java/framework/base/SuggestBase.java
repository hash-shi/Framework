package jp.co.tjs_net.java.framework.base;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public abstract class SuggestBase extends FrameworkBase {
	protected String value;
	public void setValue(String value){ this.value = value; }
	private ArrayList<String> suggestLists;
	public SuggestBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
		this.suggestLists		= new ArrayList<>();
	}
	protected void addSuggestList(String suggestString){ this.suggestLists.add(suggestString); }
	public ArrayList<String> getSuggestLists(){ return this.suggestLists; }
}
