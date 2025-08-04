package jp.co.tjs_net.java.framework.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public abstract class DownloadBase extends FrameworkBase {
	private String filename;
	private byte[] data;
	public DownloadBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	public String getFilename() { return filename; }
	public void setFilename(String filename) { this.filename = filename; }
	public byte[] getData() { return data; }
	public void setData(byte[] data) { this.data = data; }	
}
