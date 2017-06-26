package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rmbank.supervision.common.DataListResult;
import com.rmbank.supervision.common.JsonResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.model.FunctionResourceVM;
import com.rmbank.supervision.model.Meta;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.Permission;
import com.rmbank.supervision.model.PermissionResource;
import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.model.Permission;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.FunctionService;
import com.rmbank.supervision.service.PermissionResourceService;
import com.rmbank.supervision.service.PermissionService;
import com.rmbank.supervision.service.ResourceService;
import com.rmbank.supervision.service.PermissionResourceService;
import com.rmbank.supervision.service.PermissionService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.web.controller.SystemAction;

@Scope("prototype")
@Controller
@RequestMapping("/system/permission")
public class PermissionController extends SystemAction {

	@Resource
	private PermissionService permissionService;
	@Resource
	private SysLogService logService;
	@Resource
	private PermissionService PermissionService;
	@Resource
	private PermissionResourceService PermissionResourceService;
	@Resource
	private FunctionService functionService;
	@Resource
	private ResourceService resourceService;
	@Resource
	private PermissionResourceService permissionResourceService;
	
	/**
	 * 加载权限列表
	 * @param resourceConfig
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
    @RequestMapping(value = "/permissionList.do")
//	@RequiresPermissions("system/Permission/PermissionList.do")
    public DataListResult<Permission> PermissionList(Permission permission, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
    	DataListResult<Permission> dr = new DataListResult<Permission>();
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(permission.getSearchName() != null && permission.getSearchName() != ""){
			String searchName =  new String(permission.getSearchName().getBytes(
					"iso8859-1"), "utf-8");
			permission.setSearchName(searchName);
		}
		//设置页面初始值及页面大小
		if (permission.getPageNo() == null)
			permission.setPageNo(1);
		permission.setPageSize(Constants.DEFAULT_PAGE_SIZE);  
		int totalCount =  0;		
		// 分页集合
		List<Permission> permissionList = new ArrayList<Permission>();
		try {
			// t_Permission取满足要求的参数数据
			permissionList = permissionService.getPermissionList(permission);
			// t_Permission取满足要求的记录总数
			totalCount = permissionService.getPermissionCount(permission);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		permission.setTotalCount(totalCount);
		dr.setData(permission);
		dr.setDatalist(permissionList); 
    	return dr;
    }
	
	
	
}
