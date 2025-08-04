package jp.co.tjs_net.java.framework.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.OtherBase;
import jp.co.tjs_net.java.framework.information.ControllerObjectBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

/**
 * @author toshiyuki
 *
 */
public class CoreOther extends CoreBase {
	
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
		OtherBase other			= (OtherBase)info.module;
		other.doInit(req, res);
		other.doRun(req, res);
		other.doFinish(req, res);
		
	}

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.CoreInterface#response(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, lips.fw.fsapp.information.Config, lips.fw.fsapp.base.FrameworkBase)
	 */
	@Override
	public void response(Index index, HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		OtherBase other			= (OtherBase)info.module;
		other.response(index, req, res, info);
	}
}
