package jp.co.tjs_net.java.framework.core;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;*/

import jp.co.tjs_net.java.framework.base.UploadBase;
import jp.co.tjs_net.java.framework.information.ControllerObjectBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class CoreUpload extends CoreBase {
	
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

		/*		UploadBase upload			= (UploadBase)info.module;
		
				FileItem fileItem					= null;
				DiskFileItemFactory factory		= new DiskFileItemFactory();
				ServletFileUpload sfu				= new ServletFileUpload(factory);
				List<?> list						= sfu.parseRequest(req);
		Iterator<?> iterator				= list.iterator();
		while(iterator.hasNext()){
			FileItem tempFileItem			= (FileItem)iterator.next();
			if ("__uploadfile".equals(tempFileItem.getFieldName())){ fileItem = tempFileItem; break; }
			else {
				upload.addParams(tempFileItem.getFieldName(), Common.getParam(tempFileItem.getString()));
			}
		}
		
		upload.setUploadFile(fileItem);
				upload.doInit(req, res);
				upload.doRun(req, res);
				upload.doFinish(req, res);*/
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#response(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@Override
	public void response(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		UploadBase upload			= (UploadBase)info.module;
		PrintWriter out				= new PrintWriter(new OutputStreamWriter(res.getOutputStream(), "UTF8"),true);
		String json					= this.makeResponseJSON(req, res, info, upload.getContents());
		res.setContentType("text/json; charset=" + info.config.getCharset());
		out.println(json);
	}
}
