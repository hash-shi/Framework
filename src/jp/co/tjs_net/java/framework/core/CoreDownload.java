package jp.co.tjs_net.java.framework.core;

import java.io.OutputStream;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.information.ControllerObjectBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class CoreDownload extends CoreAction {
	
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
		DownloadBase download			= (DownloadBase)info.module;
		download.doInit(req, res);
		download.doRun(req, res);
		download.doFinish(req, res);
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#response(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@Override
	public void response(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {

		//-------------------------
		// HTML出力
		//-------------------------
		if (info.getSystemErrors().size() == 0 && info.getAuthorityErrors().size() == 0){
			this.responseSuccess(index, req, res, info);
		} else {
			PrintWriter out				= res.getWriter();
			if (info.getSystemErrors().size() != 0){
				responseSystemError(index, req, res, info, out);
			} else if (info.getAuthorityErrors().size() != 0){
				responseAuthorityError(index, req, res, info, out);
			}
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
		
		DownloadBase download			= (DownloadBase)info.module;

		// ダウンロードヘッダーの返却
		res.setContentType("application/octet-stream");
		String encodeAttachName = new String((download.getFilename()).getBytes("Shift_JIS"),"8859_1"); 
		res.setHeader("Content-Disposition" ,"attachment; filename=\"" + encodeAttachName + "\"");
		res.addHeader("Cache-Control", "public");
		res.addHeader("Pragma", "public");
		res.addHeader("Expires", "0");

		// ダウンロードストリーム
        OutputStream outStream = null;
        res.setContentLength((int)download.getData().length);
    	outStream = res.getOutputStream();
    	if (outStream != null)
    	{
    		try {
    			outStream.write(download.getData(), 0, download.getData().length);
    		} catch(Exception exp){}
    	}	
	}
}
