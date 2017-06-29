package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import com.rmbank.supervision.common.utils.StringUtil;
import com.rmbank.supervision.model.FunctionMenu;
import com.rmbank.supervision.model.FunctionResourceVM;
import com.rmbank.supervision.model.Permission;
import com.rmbank.supervision.model.PermissionResource;
import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.RolePermission;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.FunctionService;
import com.rmbank.supervision.service.PermissionService;
import com.rmbank.supervision.service.ResourceService;
import com.rmbank.supervision.service.RolePermissionService;
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
	private RolePermissionService rolePermissionService;
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

    /**
     * 根据Id获取权限主体信息
     */
    @ResponseBody
	@RequestMapping(value = "/jsonloadRoleInfo.do", method = RequestMethod.POST)
	public Role jsonloadRoleInfo(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
    	Role role = null;
		try{
			if(id != null && id >0){
				role = roleService.getRoleById(id);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}			
		return role;
    }

	/**
	 * 新增/编辑角色
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateRole.do", method = RequestMethod.POST)
	public JsonResult<Role> jsonSaveOrUpdateRole(Role role,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<Role> js = new JsonResult<Role>();
		js.setCode(new Integer(1));
		js.setMessage("保存失败!");
		boolean saveOrUpdateRole = false;
		try {
			// 如为新增，则给id置0
			if (role.getId() == null || role.getId() == 0) {
				role.setId(0);
			}

			Role r = new Role();
			r.setId(role.getId());
			r.setName(role.getName());
			// 如为编辑，则给新建role对象赋传来的id值
			if (role.getId() > 0) {
				r.setId(role.getId());
				saveOrUpdateRole = roleService.saveOrUpdateRole(role);
				if (saveOrUpdateRole) {
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，执行了修改角色信息操作", 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			}
			// 根据设备编号和id去数据库匹配，如编辑，则可以直接保存；如新增，则需匹配设备编号是否重复
			List<Role> lc = roleService.getExistRole(r);
			if (lc.size() == 0) {
				saveOrUpdateRole = roleService.saveOrUpdateRole(role);
				if (saveOrUpdateRole) {
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，新增了"+role.getName()+"角色", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			} else {
				js.setMessage("该角色已存在!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return js;
	}

	/**
	 * 新增/编辑权限资源分配
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateRolePermission.do", method = RequestMethod.POST) 
	public JsonResult<RolePermission> jsonSaveOrUpdateRolePermission(
			RolePermission rolePermission,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<RolePermission> js = new JsonResult<RolePermission>();
		js.setCode(1);
		js.setMessage("保存失败!"); 
		try {
			 if(rolePermission.getRoleId()>0){
				 if(StringUtil.isEmpty(rolePermission.getPermissionIds())){
					 rolePermissionService.deleteByRoleId(rolePermission.getRoleId());
				 }else{
					 String[] ids = rolePermission.getPermissionIds().split(",");
					 List<Integer> idList = new ArrayList<Integer>();
					 if(ids.length>0){ 
						 for(String id : ids){
							 if(!StringUtil.isEmpty(id)){
								 idList.add(Integer.parseInt(id));
							 }
						 }
					 }
					 rolePermissionService.saveRolePermission(rolePermission.getRoleId(), idList);
				 }
				js.setCode(0);
				js.setMessage("角色分配权限成功!"); 
			 }else{
				js.setMessage("角色分配权限失败，对应角色为空!"); 
			 }
		} catch (Exception ex) {
			ex.printStackTrace();
			js.setMessage("角色分配权限出现异常!"); 
		}
		return js;
	}
	
	
	 /**
     * 获取权限所属模块列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @ResponseBody
    @RequestMapping(value = "/getPermissionList.do")
    public List<FunctionResourceVM>  getPermissionList(
			HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException { 
    	List<FunctionResourceVM> frvmList = new ArrayList<FunctionResourceVM>();
    	FunctionMenu fm = new FunctionMenu();
    	fm.setLeaf(1);
    	List<FunctionMenu> fmList = functionService.getFunctionMenuList(fm);
    	if(fmList != null && fmList.size()>0 ){
    		for(FunctionMenu f : fmList){
    			FunctionResourceVM frvm = new FunctionResourceVM();
    			frvm.setId(f.getId());
    			frvm.setName(f.getName());
    			List<Permission> perList = new ArrayList<Permission>();
    			perList = permissionService.getPermissionByModelId(f.getId());
    			frvm.setPermissionList(perList);
    			frvmList.add(frvm);
    		}
    	}
    	return frvmList;
    }
    /**
     * 根据角色，获取分配的权限列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @ResponseBody
    @RequestMapping(value = "/jsonloadPermitList.do")
    public List<RolePermission>  jsonloadPermitList(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException { 
    	List<RolePermission> list = rolePermissionService.selectByRoleId(id);
    	return list;
    }
	
}
