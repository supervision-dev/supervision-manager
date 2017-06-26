package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rmbank.supervision.common.DataListResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.service.FunctionService;
import com.rmbank.supervision.service.PermissionService;
import com.rmbank.supervision.service.ResourceService;
import com.rmbank.supervision.service.RoleResourceService;
import com.rmbank.supervision.service.RoleService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.web.controller.SystemAction;

@Scope("prototype")
@Controller
@RequestMapping("/system/role")
public class RoleController extends SystemAction {
	/**
	 * 资源注入
	 */
	@Resource
	private RoleService roleService;
	@Resource
	private RoleResourceService roleResourceService;
	@Resource
	private FunctionService functionService;
	@Resource
	private ResourceService resourceService;
	@Resource
	private SysLogService logService;
	@Resource
	private PermissionService permissionService;
	
	/**
	 * 角色列表
	 * @param role
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
    @RequestMapping(value = "/roleList.do")
//	@RequiresPermissions("system/role/roleList.do")
    public DataListResult<Role> roleList(Role role, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
    	DataListResult<Role> dr = new DataListResult<Role>();
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(role.getSearchName() != null && role.getSearchName() != ""){
			String searchName =  new String(role.getSearchName().getBytes(
					"iso8859-1"), "utf-8");
			role.setSearchName(searchName);
		}
		//设置页面初始值及页面大小
		if (role.getPageNo() == null)
			role.setPageNo(1);
		role.setPageSize(Constants.DEFAULT_PAGE_SIZE);  
		int totalCount =  0;		
		// 分页集合
		List<Role> roleList = new ArrayList<Role>();
		try {
			// t_role取满足要求的参数数据
			roleList = roleService.getRoleList(role);
			// t_role取满足要求的记录总数
			totalCount = roleService.getRoleCount(role);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		role.setTotalCount(totalCount);
		dr.setData(role);
		dr.setDatalist(roleList); 
    	return dr;
    }
}
