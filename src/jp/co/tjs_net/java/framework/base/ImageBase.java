package jp.co.tjs_net.java.framework.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

public abstract class ImageBase extends FrameworkBase {
	private byte[] data;
	public ImageBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	public byte[] getData() { return data; }
	public void setData(byte[] data) { this.data = data; }	
}
