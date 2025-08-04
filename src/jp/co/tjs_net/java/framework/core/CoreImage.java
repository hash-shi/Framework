package jp.co.tjs_net.java.framework.core;

import java.io.OutputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ImageBase;
import jp.co.tjs_net.java.framework.common.ImageType;
import jp.co.tjs_net.java.framework.common.ImageType.Format;
import jp.co.tjs_net.java.framework.information.ControllerObjectBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class CoreImage extends CoreAction {
	
	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#getController(lips.fw.fsapp.information.IndexInformation)
	 */
	@Override
	public ControllerObjectBase getController(IndexInformation info) {
		return info.config.getController().getInfo().get(info.mode.mode).get(info.paramID);
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#getModuleClassName(lips.fw.fsapp.information.IndexInformation)
	 */
	@Override
	public String getModuleClassName(IndexInformation info) {
		return info.config.getController().getInfo().get(info.mode.mode).get(info.paramID).getClassName();
	}
	
	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreBase#init(lips.fw.fsapp.core.Index, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.IndexInformation)
	 */
	@Override
	public void init(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		// 処理なし
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#run(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@Override
	public void run(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		//-------------------------
		// 処理
		//-------------------------			
		ImageBase image			= (ImageBase)info.module;
		image.doInit(req, res);
		image.doRun(req, res);
		image.doFinish(req, res);
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#response(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@Override
	public void response(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {

		ImageBase image			= (ImageBase)info.module;
		byte[] imageData		= image.getData();
		Format imageFormat		= ImageType.getFormat(imageData);
	
		//-------------------------
		// 画像出力
		//-------------------------
		if (info.getSystemErrors().size() == 0 && info.getAuthorityErrors().size() == 0 && !imageFormat.toString().equals(ImageType.UNKNOWN)){
			this.responseSuccess(index, req, res, info);
		} else {
			responseError(index, req, res, info);
		}
	}
	
	/**
	 * @param index
	 * @param req
	 * @param res
	 * @param info
	 * @param out
	 */
	private void responseSuccess(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		
		ImageBase image			= (ImageBase)info.module;
		
		byte[] imageData		= image.getData();
		Format imageFormat		= ImageType.getFormat(imageData);
		
		String contentType		= "";
			 if (imageFormat.toString().equals(ImageType.BMP))		{ contentType = "image/bmp"; }
		else if (imageFormat.toString().equals(ImageType.GIF))		{ contentType = "image/gif"; }
		else if (imageFormat.toString().equals(ImageType.JPEG))	{ contentType = "image/jpeg"; }
		else if (imageFormat.toString().equals(ImageType.PICT))	{ contentType = "image/pict"; }
		else if (imageFormat.toString().equals(ImageType.PNG))		{ contentType = "image/png"; }
		else if (imageFormat.toString().equals(ImageType.TIFF))	{ contentType = "image/tiff"; }
		
		// ダウンロードヘッダーの返却
		res.setContentType(contentType);
		res.addHeader("Cache-Control", "public");
		res.addHeader("Pragma", "public");
		res.addHeader("Expires", "0");

		// ダウンロードストリーム
        OutputStream outStream = null;
        res.setContentLength((int)image.getData().length);
    	outStream = res.getOutputStream();
    	if (outStream != null) {
    		try {
    			outStream.write(image.getData(), 0, image.getData().length);
    		} catch(Exception exp){}
    	}	
	}
	
	/**
	 * @param index
	 * @param req
	 * @param res
	 * @param info
	 * @param out
	 */
	protected void responseError(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {

		// ダウンロードヘッダーの返却
		res.setContentType("image/jpeg");
		res.addHeader("Cache-Control", "public");
		res.addHeader("Pragma", "public");
		res.addHeader("Expires", "0");
        OutputStream outStream = res.getOutputStream();
        byte[] empty		= new byte[0];
        outStream.write(empty);
	}
}
