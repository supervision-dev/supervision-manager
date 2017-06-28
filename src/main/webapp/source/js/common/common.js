
var returnResult = {
    result_success:200,
    result_success_message:"load data success",
    result_failed:201,
    result_failed_message:"load data failed",
    result_error:202,
    result_error_message:"load data error",
    result_exception:203,
    result_exception_message:"load data exception",
    result_params:204,
    result_params_message:"param is not invalid"
};
var constantsValue = {
    enterValue:13,
    server_type_gpu:"GPU",
    server_type_janus:"GATEWAY",
    server_type_gshare:"GSHARE"
};
/**获得弹出层居中的方法**/
function getDialogPosition(wrap,offsize){
	var temp = $(wrap).css("width");
	var dl = $(document).scrollLeft(),
		dt = $(document).scrollTop(),
		ww = $(window).width(),
		wh = $(window).height(),
		ow = temp.substring(0,temp.indexOf("p")),
		oh = $(wrap).height(),
		left = (ww - ow) / 2 + dl,
		top = top = (oh < 4 * wh / 7 ? wh * 0.382 - oh / 2 : (wh - oh) / 2) + dt;
	
    left = Math.max(left, dl),
    top = Math.max(top, dt) - offsize;
    sc = new Array();
    sc[0] = top;
    sc[1] = left;
	    
	return sc;
}
/**获得弹出层居中的方法**/
function exitlogin(){
	window.location.href = $.yw.currURL+"/logout.do";
}
function valueTrim(obj){
	$(obj).val($.trim($(obj).val()));
}
function showProcess(isShow, title, msg) {
    if (!isShow) {
        $.messager.progress('close');
        return;
    }
    $.messager.progress({
        title: title,
        msg: msg
    });
}

function GetQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}