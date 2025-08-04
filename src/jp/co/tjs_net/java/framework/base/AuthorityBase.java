package jp.co.tjs_net.java.framework.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public abstract class AuthorityBase extends FrameworkBase {

	private boolean authorityResult;
	private String authoritErrorMessage;
	public AuthorityBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	public boolean isAuthorityResult() { return authorityResult; }
	public void setAuthorityResult(boolean authorityResult) { this.authorityResult = authorityResult; }
	public String getAuthoritErrorMessage() { return authoritErrorMessage; }
	public void setAuthoritErrorMessage(String authoritErrorMessage) { this.authoritErrorMessage = authoritErrorMessage; }
}
