//*****************************************************************************
// ajax形処理共通
//*****************************************************************************
//===============================================
// ajax
//----------------------------------------------- 
// mode				: 動作モード
// id				: 処理ID
// params			: 追加パラメータ
// modeAfterFunc	: コールバック処理
// ignoreWarning	: 警告を無視するか否か
// useFullParameter	: 画面パラメータを自動的にすべて送信するか否か
//===============================================
var __ajaxRequestCount	= 0;
function ajax(mode, id, params, isAsync, modeAfterFunc, ignoreWarning, useFullParameter){

	// パラメータ調整
	if (ignoreWarning == undefined)		{ ignoreWarning    = false; }
	if (useFullParameter == undefined)	{ useFullParameter = true; }
	
	// リクエストデータ設定
	var requestData		 = "";
	if (useFullParameter == true)	{ requestData += (requestData.length==0?"":"&") + $("#inputform").serialize(); }
	for(key in params)				{ requestData += (requestData.length==0?"":"&") + key + "=" + encodeURIComponent(params[key]); }
	if (ignoreWarning == true)		{ requestData += (requestData.length==0?"":"&") + "__ignoreWarning=true"; }
		
	
	// Ajaxリクエスト
	$.ajax({
		 type		: "POST"
		,url		: "." + "/" + mode + "/" + id
		,data		: requestData
		,dataType	: "json"
		,async		: isAsync
		,cashe		: false
		,beforeSend	: function (XMLHttpRequest){
			innerAjaxBeforeSend(XMLHttpRequest, isAsync);
		}
		,complete	: function (data, dataType){
			innerAjaxComplete(data, dataType, isAsync);
		}
		,error		: function (xhr, textStatus, errorThrown){
			innerAjaxError(xhr, textStatus, errorThrown);
		}
		,success	: function (data, dataType){
			innerAjaxSuccess(data, dataType, modeAfterFunc);
		}
	});
}

//===============================================
//onUpload
//----------------------------------------------- 
//mode				: 動作モード
//id				: 処理ID
//params			: 追加パラメータ
//modeAfterFunc		: コールバック処理
//ignoreWarning		: 警告を無視するか否か
//useFullParameter	: 画面パラメータを自動的にすべて送信するか否か
//===============================================
function onUpload(uploadID, fileObjID, callback, isAsync){
	
	if (isAsync == undefined){ isAsync = ajaxASYNC; }
	
	var formData = new FormData($("#inputform")[0]);
	formData.append("__uploadfile", $("#" + fileObjID)[0].files[0]);
	
	$.ajax({
		 url			: "." + "/" + "upload" + "/" + uploadID
		,dataType		: "json"
		,type			: "post"
		,async			: isAsync
		,data			: formData
		,processData	: false
		,contentType	: false
		,cashe			: false
		,beforeSend	: function (XMLHttpRequest){
			innerAjaxBeforeSend(XMLHttpRequest, isAsync);
		}
		,complete	: function (data, dataType){
			innerAjaxComplete(data, dataType, isAsync);
		}
		,error		: function (xhr, textStatus, errorThrown){
			innerAjaxError(xhr, textStatus, errorThrown);
		}
		,success	: function (data, dataType){
			innerAjaxSuccess(data, dataType, callback);
		}
	});
}

//===============================================
// innerAjaxBeforeSend
//===============================================
function innerAjaxBeforeSend(XMLHttpRequest,isAsync){
	__ajaxRequestCount++;
	pjAjaxBefore(isAsync, __ajaxRequestCount);
}

//===============================================
// innerAjaxComplete
//===============================================
function innerAjaxComplete(data, dataType, isAsync){
	__ajaxRequestCount--;
	pjAjaxComplete(isAsync, __ajaxRequestCount);
}

//===============================================
// innerAjaxError
//===============================================
function innerAjaxError(xhr, textStatus, errorThrown){
	pjSystemAlert(errorThrown);
}

//===============================================
// innerAjaxSuccess
//===============================================
function innerAjaxSuccess(data, dataType, callback){
	//-----------------------------------------------------------------
	// 戻り値確認
	// jsonに必要な情報が格納されていない場合は致命的なエラーと判断する
	//-----------------------------------------------------------------
	if (data == undefined){ return; }
	if (typeof data.contents == "undefined" || typeof data.systemErrors == "undefined" || typeof data.authorityErrors == "undefined" || typeof data.validateResults == "undefined" || typeof data.procStartTime == "undefined" || typeof data.procEndTime == "undefined"){
		alert(getMessage("_SYS_ERROR_001"));
		return;
	}

	var systemErrors				= data.systemErrors;
	var authorityErrors				= data.authorityErrors;
	var validateResults				= data.validateResults;

	//-----------------------------------------------------------------
	// エラーチェック
	//-----------------------------------------------------------------
	// [システムエラー]
	if (systemErrors.length > 0){
		pjSystemAlert(systemErrors);
		return;
	}
	
	// [認証エラーメッセージ]
	if (authorityErrors.length > 0){
		pjAuthorityAlert(authorityErrors);
		return;
	}
	
	// [入力値チェックエラー]
	var isContinue	= pjValidateResults(validateResults, data, dataType);
	if (isContinue == false){ return; } 

	//-----------------------------------------------------------------
	// 各処理の実行
	//-----------------------------------------------------------------
	if (callback != undefined){
		callback(data, dataType);
	}
}

//*****************************************************************************
// エラー・警告等の表示
// オーバーライドして使えば、それぞれのプロジェクト毎に自由に出力が可能
//*****************************************************************************
function pjAlert(message){ 
	if (message != undefined){ alert(message); }
}

//===============================================
// pjAjaxBefore
//----------------------------------------------- 
// isAsync			: 非同期モードで呼ばれたか否か
// ajaxRequestCount	: 現在実行中のAjaxリクエスト数
//===============================================
function pjAjaxBefore(isAsync, ajaxRequestCount){
}

//===============================================
//pjAjaxComplete
//----------------------------------------------- 
//isAsync			: 非同期モードで呼ばれたか否か
//ajaxRequestCount	: 現在実行中のAjaxリクエスト数
//===============================================
function pjAjaxComplete(isAsync, ajaxRequestCount){
}

//===============================================
//pjAuthorityAlert
//----------------------------------------------- 
//systemErrors		: システムエラー情報
//===============================================
function pjAuthorityAlert(authorityErrors){
	for (var count = 0 ; count < authorityErrors.length ; count++){
		var authorityError		= authorityErrors[count];
		var authorityID			= authorityError["authorityID"];
		var errorMessage		= authorityError["errorMessage"];
		var afterScript			= authorityError["afterScript"];
		
		if (errorMessage != undefined){ alert(errorMessage); }
		eval(afterScript);
	}
}

//===============================================
//pjSystemAlert
//----------------------------------------------- 
//systemErrors		: システムエラー情報
//===============================================
function pjSystemAlert(systemErrors){
	for (var count = 0 ; count < systemErrors.length ; count++){
		var systemError			= systemErrors[count];
		var message				= systemError["message"];
		var className			= systemError["className"];
		var stackTraces			= systemError["stackTrace"];
		
		var stackTraceText		= "";
		for (var count = 0 ; count < stackTraces.length ; count++){
			var stackTrace		= stackTraces[count];
			stackTraceText		+= stackTrace["className"] + "::" + stackTrace["methodName"] + "(" + stackTrace["lineNumber"] + ")" + "\n";
		}
		
		if (message != undefined && className != undefined && stackTraceText != undefined) {
			alert(message + "\n\n" + "[ExceptionClass]\n" + className + "\n\n" + "[StackTrace]\n" + stackTraceText);	
		}
	}
}

//===============================================
//pjValidateResults
//----------------------------------------------- 
//validateResults	: 入力値チェック情報
//===============================================
function pjValidateResults(validateResults){
	var errorMessage = "";
	for (var count = 0 ; count < validateResults.length ; count++){
		var validateResult		= validateResults[count];
		if (Boolean(validateResult["result"]) == false){ errorMessage += validateResult["message"]  + "\n"; }
	}
	if (errorMessage != ""){
		alert(errorMessage);
	}
	if (errorMessage != ""){
		return false;
	}
	return true;
}

//*****************************************************************************
// アクション発生
//*****************************************************************************
var __prevFunc		= null;
var __prevParams	= null;
var __prevCallback	= null;
var __prevIsASync	= null;
var __prevInitfunc	= null;
//===============================================
// proc
//----------------------------------------------- 
// func				: 
// params			: 
// callback			: 
//===============================================
function proc(func, params, callback, isAsync, initfunc){

	// 同期・非同期モードの判定
	if (isAsync == undefined){
		isAsync		= ajaxASYNC;
	}

	// 前回実行パラメータの保存
	__prevFunc		= func;
	__prevParams	= params;
	__prevCallback	= callback;
	__prevIsAsync	= isAsync;
	__prevInitfunc	= initfunc;
	
	// 初期キック処理が存在している場合はそれを起動する
	if (initfunc != undefined){ initfunc(); }
	
	// 実処理
	ajax("process", func, params, isAsync, function(data, dataType){
		if (callback != undefined){
			callback(data, dataType);
		}
	});
}

//===============================================
// procIgnoreWarning
//----------------------------------------------- 
// func				: 
// params			: 
// callback			: 
//===============================================
function procIgnoreWarning(){
	if (__prevInitfunc != null){ __prevInitfunc(); __prevInitfunc = null; }
	ajax("process", __prevFunc, __prevParams, __prevIsASync, function(data, dataType){
		if (__prevCallback != undefined){
			__prevCallback(data, dataType);
		}
	}, true);	
}

//*****************************************************************************
// ダイアログ開く
//*****************************************************************************
var _dialogInfos	= new Array();

//===============================================
// openDialog
//----------------------------------------------- 
// dialogID			: 
// width			: 
// height			: 
// params			: 
// callback			: 
//===============================================
function openDialog(dialogID, width, height, params, callback){
	ajax("dialog"
		,dialogID
		,params
		,false
		,function(data, dataType){
			
			// ダイアログの大きさを定義
			var halfWidth	= (width / 2) * -1;
			var halfHeight	= (height / 2) * -1;

			// パラメータから必要な情報を取得
			var uuid			= data.contents.uuid;
			var token			= data.contents.token;
			var separator		= data.contents.separator;
			var nowactionID		= data.contents.nowaction_id;
			var viewlayerID		= data.contents.viewlayer_id;
			var uuidsID			= data.contents.uuids_id;
			var dialogID		= data.contents.dialog_id;
			var dialogTokensID	= data.contents.dialogtokens_id;

			// ダイアログ作成用の最前面のz-indexを求める
			var zIndexBackground	= getMaxZIndex() + 1;
			var zIndexDialog		= zIndexBackground + 1;
			
			// HTML生成
			var dialogHTML = "";
			dialogHTML += "<div id='dialogBackground_" + uuid + "' style='position:absolute;left:0px;top:0px;width:100%;height:100%;background-color:#000000;z-index:" + zIndexBackground + ";opacity:0.25;' backgroundLayer></div>";
			if (width == -1 && height == -1){
				dialogHTML += "<div id='dialog_" + uuid + "'       style='position:absolute;background-color:#ffffff;left:0px;top:0px;width:100%;" + "height:100%;" + "z-index:" + zIndexDialog + ";' class='dialogFrame'>";
			} else {
				dialogHTML += "<div id='dialog_" + uuid + "'       style='position:absolute;background-color:#ffffff;left:50%;top:50%;margin-left:" + halfWidth + "px;margin-top:" + halfHeight + "px;width:" + width + "px;" + "height:" + height + "px;" + "z-index:" + zIndexDialog + ";' class='dialogFrame'>";
			} 
			dialogHTML += data.contents.contents;
			dialogHTML += "</div>";
			$("#inputform").append(dialogHTML);
			
			// NOWACTIONの書き換え
			$("#" + nowactionID).val(dialogID);
			
			// VIEWLAYERの書き換え
			var nowViewlayerID	= $("#" + viewlayerID).val();
			var newViewlayerID	= nowViewlayerID + separator + dialogID;
			$("#" + viewlayerID).val(newViewlayerID);
			
			// UUIDSの書き換え
			var nowUUIDsID	= $("#" + uuidsID).val();
			var newUUIDsID	= nowUUIDsID + separator + uuid;
			$("#" + uuidsID).val(newUUIDsID);
			
			// ダイアログトークンの保持
			$("#" + dialogTokensID).append("<input type=\"hidden\" name=\"" + uuid + "\" id=\"" + uuid + "\" value=\"" + token + "\">");
			
			// ダイアログ情報格納配列に定義を追加
			_dialogInfos[_dialogInfos.length]	= { uuid			: uuid
												  , separator		: separator
												  , dialogID		: dialogID
												  , nowactionID		: nowactionID
												  , viewlayerID		: viewlayerID
												  , uuidsID			: uuidsID };
			
			// 定義されているJavaScriptを実行する
			$("div[id='dialog_" + uuid + "']").find("script").each(function(){
				eval($(this).html());
			});

			// ダイアログコール時に強制的に呼ぶ処理を記述する
			openDialogInner(uuid);

			// ダイアログにフォーカスを当てる
			if ($("#dialog_" + uuid).find("input").length != 0){ try { $("#dialog_" + uuid).find("input")[0].focus(); } catch (exp){} }
			else if ($("#dialog_" + uuid).find("button").length != 0){ try { $("#dialog_" + uuid).find("button")[0].focus(); } catch (exp){} }
			else { try { document.activeElement.blur(); } catch (exp){} }

			// コールバック処理
			if (callback != undefined){
				callback();
			}
		}
		,function(){
			
		}
	);
}

//===============================================
// openDialogInner
//----------------------------------------------- 
// uuid				: 
//===============================================
function openDialogInner(uuid){
}

//===============================================
// closeDialog
//----------------------------------------------- 
//===============================================
function closeDialog(){
	var closeDialogInfo			= _dialogInfos[_dialogInfos.length - 1];
	var closeUUID				= closeDialogInfo["uuid"];
	var separator				= closeDialogInfo["separator"];
	var closeDialogID			= closeDialogInfo["dialogID"];
	var nowactionID				= closeDialogInfo["nowactionID"];
	var viewlayerID				= closeDialogInfo["viewlayerID"];
	var uuidsID					= closeDialogInfo["uuidsID"];
	
	$("#" + "dialogBackground_" + closeUUID).hide();
	$("#" + "dialog_"           + closeUUID).hide();
	
	$("#" + "dialogBackground_" + closeUUID).remove();
	$("#" + "dialog_"           + closeUUID).remove();

	// VIEWLAYERの書き換え
	var nowViewlayerID	= $("#" + viewlayerID).val();
	var newViewlayerID	= nowViewlayerID.substr(0, nowViewlayerID.lastIndexOf(separator + closeDialogID));
	$("#" + viewlayerID).val(newViewlayerID);
	var nowDialogID		= "";
	if (newViewlayerID.lastIndexOf(separator) != -1){
		nowDialogID			= newViewlayerID.substr(newViewlayerID.lastIndexOf(separator) + separator.length);
	} else {
		nowDialogID			= newViewlayerID;
	}
	
	// UUIDSの書き換え
	var nowUUIDs	= $("#" + uuidsID).val();
	var newUUIDs	= nowUUIDs.substr(0, nowUUIDs.lastIndexOf(separator + closeUUID));
	$("#" + uuidsID).val(newUUIDs);
	var nowUUID		= "";
	if (newUUIDs.lastIndexOf(separator) != -1){
		nowUUID			= newUUIDs.substr(newUUIDs.lastIndexOf(separator) + separator.length);
	} else {
		nowUUID			= newUUIDs;
	}
	
	// トークン情報の削除
	$("#" + closeUUID).remove();
	
	// NOWACTIONの書き換え
	$("#" + nowactionID).val(nowDialogID);
	
	// 配列からダイアログ情報の削除
	_dialogInfos.pop();
}

//*****************************************************************************
// ダウンロード処理
//*****************************************************************************
//===============================================
// onDownload
//----------------------------------------------- 
// downloadID		: 
//===============================================
function onDownload(downloadID){
	location.href = "./download/" + downloadID;
}

function onDownloadPost(downloadID){
	$("#inputform").attr("method", "post");
	$("#inputform").attr("action", "./download/" + downloadID);
	$("#inputform").submit();
}

//*****************************************************************************
// サジェスト処理
//*****************************************************************************
var _suggestInfos	= new Object();
//===============================================
// regSuggest
//----------------------------------------------- 
// targetID			: 
// suggestID		: 
// divClass			: 
// ulClass			: 
// liClass			: 
//===============================================
function regSuggest(targetID, suggestID, divClass, ulClass, liClass, selectActionMethod){
	if (divClass			== undefined){ divClass				= ""; }
	if (ulClass				== undefined){ ulClass				= ""; }
	if (liClass				== undefined){ liClass				= ""; }
	if (selectActionMethod	== undefined){ selectActionMethod	= null; }
	$("#" + targetID).on("keyup", function(){ closeSuggest(); onSuggest(targetID, suggestID); });
	$("#" + targetID).on("blur", function(){
		if (_suggestInfos[targetID]["onMouse"] == false){
			closeSuggest();
		}
	});
	_suggestInfos[targetID]			= { targetID : targetID, suggestID: suggestID, divClass: divClass, ulClass: ulClass, liClass: liClass, selectActionMethod: selectActionMethod, onMouse:false };
}

//===============================================
// regSuggest
//----------------------------------------------- 
// targetID			: 
// suggestID		: 
//===============================================
function onSuggest(targetID, suggestID){
	if ($("#" + targetID).val()==""){ closeSuggest(); return; }
	ajax("suggest", suggestID, {"__suggestVal":$("#" + targetID).val()}, false, function(data, dataType){
		$("#" + targetID + "_suggest").remove();
		
		var zIndex					= getMaxZIndex() + 1;
		
		var targetObjectOffset	　	 = $("#" + targetID).offset();
		var posLeft				　	 = targetObjectOffset.left;
		var posTop				　	 = targetObjectOffset.top;
		var objHeight			　	 = $("#" + targetID).height();
		var divClass			　	 = _suggestInfos[targetID]["divClass"];
		var ulClass				　	 = _suggestInfos[targetID]["ulClass"]
		var liClass				　	 = _suggestInfos[targetID]["liClass"]
		var suggestHTML			 	 = "<div class=\"suggestFrame " + divClass + "\" id=\"" + targetID + "_suggest\" style=\"position:absolute;left:" + posLeft + "px;top:" + (posTop + objHeight) + "px;z-index:" + zIndex + ";\" onmouseover=\"onMouseOverSuggest('" + targetID + "');\" onmouseout=\"onMouseOutSuggest('" + targetID + "');\" >";
		suggestHTML					+= "<ul class=\"suggestUl " + ulClass + "\">";
		var suggestLists		　	 = data.contents;
		for (var count = 0 ; count < suggestLists.length ; count++){ suggestHTML += "<li class=\"suggestLi " + liClass + "\" onclick=\"clickSuggest('" + targetID + "','" + suggestLists[count] + "');\">" + suggestLists[count] + "</li>"; }
		suggestHTML					+= "</ul>";
		suggestHTML					+= "</div>";
		$("#" + targetID).after(suggestHTML);
	});
}

//===============================================
//onMouseOverSuggest
//----------------------------------------------- 
//targetID			: 
//===============================================
function onMouseOverSuggest(targetID){
	_suggestInfos[targetID]["onMouse"] = true;
}

//===============================================
//onMouseOverSuggest
//----------------------------------------------- 
//targetID			: 
//===============================================
function onMouseOutSuggest(targetID){
	_suggestInfos[targetID]["onMouse"] = false;
	$("#" + targetID).focus();
}

//===============================================
// clickSuggest
//----------------------------------------------- 
// targetID			: 
// value			: 
//===============================================
function clickSuggest(targetID, value){
	if (_suggestInfos[targetID]["selectActionMethod"] != null){
		var tempMethod		= _suggestInfos[targetID]["selectActionMethod"]; 
		value				= tempMethod(value);
	}
	$("#" + targetID).val(value);
	closeSuggest();
}

//===============================================
// closeSuggest
//----------------------------------------------- 
//===============================================
function closeSuggest(){
	for(key in _suggestInfos){
		var suggestInfo			= _suggestInfos[key];
		$("#" + suggestInfo["targetID"] + "_suggest").remove();
	}
}

//*****************************************************************************
// メッセージ
//*****************************************************************************
function getMessage(messageID) {
	var message = eval("MSG_" + messageID);
	return message;
}

//*****************************************************************************
// 画面上のz-indexの最大値を求める(共通部品)
//*****************************************************************************
function getMaxZIndex(){
	var maxZIndex = 0;
	$("*").each(function(){
		if (maxZIndex <= parseInt($(this).css("zIndex"), 10)){ maxZIndex = parseInt($(this).css("zIndex"), 10); }
	});
	return maxZIndex;
}
