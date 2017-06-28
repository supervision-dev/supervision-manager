$.extend($.fn.validatebox.defaults.rules, {
	maxLength: {
	    validator: function(value, param){
	        var len=$.trim(value).length;
	        return len <= param[1];
	    },
	    message: '{0}长度不能超过{1}'
    },
    mixLength: {
    	validator: function(value, param){
    		return $.trim(value).length >= param[0];
		},
		message: '请输入至少（2）个字符.'
    },
    Length:{
    	validator:function(value,param){
		var len=$.trim(value).length;
		return len>=param[0]&&len<=param[1];
		},
		message:"输入内容长度必须介于{0}和{1}之间."
    },
    CHS: {
        validator: function (value, param) {
            return /^[\u0391-\uFFE5]+$/.test(value);
        },
        message: '请输入汉字'
    },
    ZIP: {
        validator: function (value, param) {
            return /^[1-9]\d{5}$/.test(value);
        },
        message: '请输入正确的邮政编码'
    },
    QQ: {
        validator: function (value, param) {
            return /^[1-9]\d{4,10}$/.test(value);
        },
        message: '请输入正确的QQ号码'
    },
    mobile: {
        validator: function (value, param) {
            return /^13\d{9}$/g.test(value)||(/^15\d{9}$/g.test(value))|| (/^18\d{9}/g.test(value)||(/^17\d{9}$/g.test(value)));
        },
        message: '请输入正确的手机号码'
    },     
    loginName: {
        validator: function (value, param) {
            return /^[\u0391-\uFFE5\w]+$/.test(value);
        },
        message: '登录名称只允许汉字、英文字母、数字及下划线。'
    },
    baseValue: {
        validator: function (value, param) {
            return /^[\u0391-\uFFE5\w]+$/.test(value);
        },
        message: '只允许汉字、英文字母、数字及下划线。'
    },
    safepass: {
        validator: function (value, param) {    	            	
            return safePassword(value);            
        },
        message: '{0}只能由6-16位英文字母和数字组成'
    },
    equalTo: {
        validator: function (value, param) {
            return value == $(param[0]).val();
        },
        message: '两次输入的密码不一致'
    },
    number: {
        validator: function (value, param) {
            return /^\d+$/.test(value);
        },
        message: '请输入数字'
    },
    realNumber: {
        validator: function (value, param) {
            return /^-?\d+(\.\d+)?$/.test(value);
        },
        message: '请输入数字'
    },
    phone: {
        validator: function (value, param) {
            return /^\d+$/.test(value);
        },
        message: '电话号码只能为数字'
    },
    idcard: {
        validator: function (value, param) {
            return idCard(value);
        },
        message:'请输入正确的身份证号码'
    },
    EMAIL: {
        validator: function (value, param) {
            return   /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/.test(value);
        },
        message:'请输入正确的邮箱'
    },
    IP: {
        validator: function (value, param) {
            return   /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/.test(value);
        },
        message:'请输入正确的IP地址'
    },
    LoctionX: {
        validator: function (value, param) {
            return   /^-?(?:(?:180(?:\.0{1,5})?)|(?:(?:(?:1[0-7]\d)|(?:[1-9]?\d))(?:\.\d{1,6})?))$/.test(value);
        },
        message:'请输入正确的经度'
    },
    LoctionY: {
        validator: function (value, param) {
            return   /^-?(?:90(?:\.0{1,5})?|(?:[1-8]?\d(?:\.\d{1,6})?))$/.test(value);
        },
        message:'请输入正确的纬度'
    },
    SpecialWord: {
    	validator: function (value, param) {
    		var myReg = /^[^@\/\'\\\"#$%&\^\*]+$/;
            return myReg.test(value);
        },
        message:'输入查询条件不能包含特殊字符'
    }
});

/* 密码由字母和数字组成，至少6位 */
var safePassword = function (value) {
	 //var reg = /^[x00-x7f]+$/;
    return (/^[0-9a-zA-Z]{6,16}$/.test(value));
}

var idCard = function (value) {
    if (value.length == 18 && 18 != value.length) return false;
    var number = value.toLowerCase();
    var d, sum = 0, v = '10x98765432', w = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2], a = '11,12,13,14,15,21,22,23,31,32,33,34,35,36,37,41,42,43,44,45,46,50,51,52,53,54,61,62,63,64,65,71,81,82,91';
    var re = number.match(/^(\d{2})\d{4}(((\d{2})(\d{2})(\d{2})(\d{3}))|((\d{4})(\d{2})(\d{2})(\d{3}[x\d])))$/);
    if (re == null || a.indexOf(re[1]) < 0) return false;
    if (re[2].length == 9) {
        number = number.substr(0, 6) + '19' + number.substr(6);
        d = ['19' + re[4], re[5], re[6]].join('-');
    } else d = [re[9], re[10], re[11]].join('-');
    if (!isDateTime.call(d, 'yyyy-MM-dd')) return false;
    for (var i = 0; i < 17; i++) sum += number.charAt(i) * w[i];
    return (re[2].length == 9 || number.charAt(17) == v.charAt(sum % 11));
}

var isDateTime = function (format, reObj) {
    format = format || 'yyyy-MM-dd';
    var input = this, o = {}, d = new Date();
    var f1 = format.split(/[^a-z]+/gi), f2 = input.split(/\D+/g), f3 = format.split(/[a-z]+/gi), f4 = input.split(/\d+/g);
    var len = f1.length, len1 = f3.length;
    if (len != f2.length || len1 != f4.length) return false;
    for (var i = 0; i < len1; i++) if (f3[i] != f4[i]) return false;
    for (var i = 0; i < len; i++) o[f1[i]] = f2[i];
    o.yyyy = s(o.yyyy, o.yy, d.getFullYear(), 9999, 4);
    o.MM = s(o.MM, o.M, d.getMonth() + 1, 12);
    o.dd = s(o.dd, o.d, d.getDate(), 31);
    o.hh = s(o.hh, o.h, d.getHours(), 24);
    o.mm = s(o.mm, o.m, d.getMinutes());
    o.ss = s(o.ss, o.s, d.getSeconds());
    o.ms = s(o.ms, o.ms, d.getMilliseconds(), 999, 3);
    if (o.yyyy + o.MM + o.dd + o.hh + o.mm + o.ss + o.ms < 0) return false;
    if (o.yyyy < 100) o.yyyy += (o.yyyy > 30 ? 1900 : 2000);
    d = new Date(o.yyyy, o.MM - 1, o.dd, o.hh, o.mm, o.ss, o.ms);
    var reVal = d.getFullYear() == o.yyyy && d.getMonth() + 1 == o.MM && d.getDate() == o.dd && d.getHours() == o.hh && d.getMinutes() == o.mm && d.getSeconds() == o.ss && d.getMilliseconds() == o.ms;
    return reVal && reObj ? d : reVal;
    function s(s1, s2, s3, s4, s5) {
        s4 = s4 || 60, s5 = s5 || 2;
        var reVal = s3;
        if (s1 != undefined && s1 != '' || !isNaN(s1)) reVal = s1 * 1;
        if (s2 != undefined && s2 != '' && !isNaN(s2)) reVal = s2 * 1;
        return (reVal == s1 && s1.length != s5 || reVal > s4) ? -10000 : reVal;
    }
};

/**
 * 1）扩展jquery easyui tree的节点检索方法。使用方法如下：
 * $("#treeId").tree("search", searchText);	 
 * 其中，treeId为easyui tree的根UL元素的ID，searchText为检索的文本。
 * 如果searchText为空或""，将恢复展示所有节点为正常状态
 */
	
$.extend($.fn.tree.methods, {
	/**
	 * 扩展easyui tree的搜索方法
	 * @param tree easyui tree的根DOM节点(UL节点)的jQuery对象
	 * @param id 检索的文本 检测过程中，判断是公司还是员工
	 * @param this-context easyui tree的tree对象
	 */
	findByCondition:function(jqTree, nodeId){
		//easyui tree的tree对象。可以通过tree.methodName(jqTree)方式调用easyui tree的方法
		var tree = this;
		
		//获取所有的树节点
		var nodeList = getAllNodes(jqTree, tree);
		
  		//如果没有搜索条件，则展示所有树节点
		nodeId = $.trim(nodeId);
  		if (nodeId == "") {
  			for (var i=0; i<nodeList.length; i++) {
  				$(".tree-node-targeted", nodeList[i].target).removeClass("tree-node-targeted");
  	  			$(nodeList[i].target).show();
  	  		}
  			//展开已选择的节点（如果之前选择了）
  			var selectedNode = tree.getSelected(jqTree);
  			if (selectedNode) {
  				tree.expandTo(jqTree, selectedNode.target);
  			}
  			return;
  		}
  		
  		//搜索匹配的节点并返回节点
  		var matchedNode;
  		if (nodeList && nodeList.length>0) {
  			var node = null;
  			for (var i=0; i<nodeList.length; i++) {
  				node = nodeList[i];
  				if (isMatchNodeId(nodeId, node.id,node.dataType)) {
  					matchedNode = node;
  					break;
  				}
  			} 
  		} 
  		return matchedNode;
	},
	/**
	 * 扩展easyui tree的搜索方法
	 * @param tree easyui tree的根DOM节点(UL节点)的jQuery对象
	 * @param searchText 检索的文本
	 * @param this-context easyui tree的tree对象
	 */
	search: function(jqTree, searchText) {
		//easyui tree的tree对象。可以通过tree.methodName(jqTree)方式调用easyui tree的方法
		var tree = this;
		
		//获取所有的树节点
		var nodeList = getAllNodes(jqTree, tree);
		
  		//如果没有搜索条件，则展示所有树节点
		searchText = $.trim(searchText);
  		if (searchText == "") {
  			for (var i=0; i<nodeList.length; i++) {
  				$(".tree-node-targeted", nodeList[i].target).removeClass("tree-node-targeted");
  	  			$(nodeList[i].target).show();
  	  		}
  			//展开已选择的节点（如果之前选择了）
  			var selectedNode = tree.getSelected(jqTree);
  			if (selectedNode) {
  				tree.expandTo(jqTree, selectedNode.target);
  			}
  			return;
  		}
  		
  		//搜索匹配的节点并返回节点
  		var matchedNode;
  		if (nodeList && nodeList.length>0) {
  			var node = null;
  			for (var i=0; i<nodeList.length; i++) {
  				node = nodeList[i];
  				if (isMatchText(searchText, node.text)) {
  					matchedNode = node;
  					break;
  				}
  			}
  			
  			/*//隐藏所有节点
  	  		for (var i=0; i<nodeList.length; i++) {
  	  			$(".tree-node-targeted", nodeList[i].target).removeClass("tree-node-targeted");
  	  			$(nodeList[i].target).hide();
  	  		}  			
  			
  			//折叠所有节点
  	  		tree.collapseAll(jqTree);
  			
  			//展示所有匹配的节点以及父节点  			
  			for (var i=0; i<matchedNodeList.length; i++) {
  				showMatchedNode(jqTree, tree, matchedNodeList[i]);
  			}*/
  		} 
  		
  		/* //搜索匹配的节点并高亮显示  
        var matchedNodeList = [];  
        if (nodeList && nodeList.length>0) {  
            var node = null;  
            for (var i=0; i<nodeList.length; i++) {  
                node = nodeList[i];  
                if (isMatchText(searchText, node.text)) {  
                    matchedNodeList.push(node);  
                }  
            }  
              
            //隐藏所有节点  
            for (var i=0; i<nodeList.length; i++) {  
                $(".tree-node-targeted", nodeList[i].target).removeClass("tree-node-targeted");  
                $(nodeList[i].target).hide();  
            }             
              
            //折叠所有节点  
            tree.collapseAll(jqTree);  
              
            //展示所有匹配的节点以及父节点              
            for (var i=0; i<matchedNodeList.length; i++) {  
                showMatchedNode(jqTree, tree, matchedNodeList[i]);  
            }  
        }      */
  		return matchedNode;
	},
	
	/**
	 * 展示节点的子节点（子节点有可能在搜索的过程中被隐藏了）
	 * @param node easyui tree节点
	 */
	showChildren: function(jqTree, node) {
		//easyui tree的tree对象。可以通过tree.methodName(jqTree)方式调用easyui tree的方法
		var tree = this;
		
		//展示子节点
		if (!tree.isLeaf(jqTree, node.target)) {
			var children = tree.getChildren(jqTree, node.target);
			if (children && children.length>0) {
				for (var i=0; i<children.length; i++) {
					if ($(children[i].target).is(":hidden")) {
						$(children[i].target).show();
					}
				}
			}
		}  	
	},
	
	/**
	 * 将滚动条滚动到指定的节点位置，使该节点可见（如果有滚动条才滚动，没有滚动条就不滚动）
	 * @param param {
	 * 	  treeContainer: easyui tree的容器（即存在滚动条的树容器）。如果为null，则取easyui tree的根UL节点的父节点。
	 *    targetNode:  将要滚动到的easyui tree节点。如果targetNode为空，则默认滚动到当前已选中的节点，如果没有选中的节点，则不滚动
	 * } 
	 */
	scrollTo: function(jqTree, param) {
		//easyui tree的tree对象。可以通过tree.methodName(jqTree)方式调用easyui tree的方法
		var tree = this;
		
		//如果node为空，则获取当前选中的node
		var targetNode = param && param.targetNode ? param.targetNode : tree.getSelected(jqTree);
		
		if (targetNode != null) {
			//判断节点是否在可视区域				
			var root = tree.getRoot(jqTree);
			var $targetNode = $(targetNode.target);
			var container = param && param.treeContainer ? param.treeContainer : jqTree.parent();
			var containerH = container.height();
			var nodeOffsetHeight = $targetNode.offset().top - container.offset().top;
			if (nodeOffsetHeight > (containerH - 30)) {
				var scrollHeight = container.scrollTop() + nodeOffsetHeight - containerH + 30;
				container.scrollTop(scrollHeight);
			}							
		}
	}
});




/**
 * 展示搜索匹配的节点
 */
function showMatchedNode(jqTree, tree, node) {
	//展示所有父节点
	$(node.target).show();
	$(".tree-title", node.target).addClass("tree-node-targeted");
	var pNode = node;
	while ((pNode = tree.getParent(jqTree, pNode.target))) {
		$(pNode.target).show();  			
	}
	//展开到该节点
	tree.expandTo(jqTree, node.target);
	//如果是非叶子节点，需折叠该节点的所有子节点
	if (!tree.isLeaf(jqTree, node.target)) {
		tree.collapse(jqTree, node.target);
	}
}  	 

/**
 * 判断searchText是否与targetText匹配
 * @param searchText 检索的文本
 * @param targetText 目标文本
 * @return true-检索的文本与目标文本匹配；否则为false.
 */
function isMatchText(searchText, targetText) {
	//模糊匹配
	//return $.trim(targetText)!="" && targetText.indexOf(searchText)!=-1;  
	//完全匹配
	return $.trim(targetText)!="" && targetText == searchText;
}
/**
 * 判断id是否与targetId匹配
 * @param id 检索的文本
 * @param targetId 目标文本
 * @return true-检索的文本与目标文本匹配；否则为false.
 */
function isMatchNodeId(searchId, targetId,targetType) { 
	return $.trim(targetId)!="" && targetId == searchId && targetType == 1;
}


/**
 * 获取easyui tree的所有node节点
 */
function getAllNodes(jqTree, tree) {
	var allNodeList = jqTree.data("allNodeList");
	if (!allNodeList) {
		var roots = tree.getRoots(jqTree);
		allNodeList = getChildNodeList(jqTree, tree, roots);
		jqTree.data("allNodeList", allNodeList);
	}
	return allNodeList;
}

/**
 * 定义获取easyui tree的子节点的递归算法
 */
function getChildNodeList(jqTree, tree, nodes) {
	var childNodeList = [];
	if (nodes && nodes.length>0) {  			
		var node = null;
		for (var i=0; i<nodes.length; i++) {
			node = nodes[i];
			childNodeList.push(node);
			if (!tree.isLeaf(jqTree, node.target)) {
				var children = tree.getChildren(jqTree, node.target);
				childNodeList = childNodeList.concat(getChildNodeList(jqTree, tree, children));
			}
		}
	}
	return childNodeList;
} 
/**
 * 指定位置显示$.messager.show
 * options $.messager.show的options
 * param = {left,top,right,bottom}
 */
$.extend($.messager, {
	showBySite : function(options,param) {
		var site = $.extend( {
			left : "",
			top : "",
			right : 0,
			bottom : -document.body.scrollTop
					- document.documentElement.scrollTop
		}, param || {});
		var win = $("body > div .messager-body");
		if(win.length<=0)
			$.messager.show(options);
		win = $("body > div .messager-body");
		win.window("window").css( {
			left : site.left,
			top : site.top,
			right : site.right,
			zIndex : $.fn.window.defaults.zIndex++,
			bottom : site.bottom
		});
	}
});

function getnowDate() {
	var nowtime = new Date();
	var year = nowtime.getFullYear();
	var month = padleft0(nowtime.getMonth() + 1);
	var day = padleft0(nowtime.getDate());
	return year + "-" + month + "-" + day;
}
function getnowtime() {
	var nowtime = new Date();
	var year = nowtime.getFullYear();
	var month = padleft0(nowtime.getMonth() + 1);
	var day = padleft0(nowtime.getDate());
	var hour = padleft0(nowtime.getHours());
	var minute = padleft0(nowtime.getMinutes());
	var second = padleft0(nowtime.getSeconds());
	var millisecond = nowtime.getMilliseconds(); millisecond = millisecond.toString().length == 1 ? "00" + millisecond : millisecond.toString().length == 2 ? "0" + millisecond : millisecond;
	return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + "." + millisecond;
}
function padleft0(obj) {
	return obj.toString().replace(/^[0-9]{1}$/, "0" + obj);
}
function getlasdWeekDate(){
	var nowtime = new Date();
	nowtime.setDate(nowtime.getDate()-7);
	var year = nowtime.getFullYear();
	var month = padleft0(nowtime.getMonth() + 1);
	var day = padleft0(nowtime.getDate());
	return year + "-" + month + "-" + day;
	//alert(n.getFullYear()+"-"+ (n.getMonth()+1)+"-"+ n.getDate());
}
function getlasdMonthDate(){
	var nowtime = new Date();
	nowtime.setDate(nowtime.getDate()-30);
	var year = nowtime.getFullYear();
	var month = padleft0(nowtime.getMonth() + 1);
	var day = padleft0(nowtime.getDate());
	return year + "-" + month + "-" + day;
}
function diffTime(startTime,endTime){
	var start=new Date(startTime.replace("-", "/").replace("-", "/"));
	var end=new Date(endTime.replace("-", "/").replace("-", "/"));
	if(end<start){
		return false;
	}
	return true;
}

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}
function setDefaultDate(){
	var t1 = getUrlParam("schBeginTime");
	var t2 = getUrlParam("schEndTime");
	if(t1 == null && t2 == null){
		var lastDate = getlasdMonthDate();
		var nowDate = getnowDate();
		$("#dtx_schBeginTime").datebox("setValue",lastDate);
		$("#dtx_schEndTime").datebox("setValue",nowDate);
	} 
} 

function stTimeslt(date){
	var stT = $("#dtx_schBeginTime").datebox("getValue");
	var edT = $("#dtx_schEndTime").datebox("getValue");
	if(stT != null && stT != "" && edT != null && edT != "" ){
		var resutl = diffTime(stT,edT);
		if(!resutl){
			$.messager.alert("操作提示","查询条件 ：结束时间不能小于开始时间~","error");
			var edT = $("#dtx_schEndTime").datebox("setValue","");
		}
	}
}