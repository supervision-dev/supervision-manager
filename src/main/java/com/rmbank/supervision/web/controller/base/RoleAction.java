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

import com.rmbank.supervision.common.JsonResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.model.FunctionPermissionVM;
import com.rmbank.supervision.model.FunctionResourceVM;
import com.rmbank.supervision.model.Permission;
import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.RolePermission;
import com.rmbank.supervision.model.User;
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
public class RoleAction extends SystemAction {

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
	 * 
	 * @param organ
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/roleList.do")
	@RequiresPermissions("system/role/roleList.do")
	public String roleList(Role role, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		// 判断搜索名是否为空，不为空则转为utf-8编码
		if (role.getSearchName() != null && role.getSearchName() != "") {
			String searchName = URLDecoder.decode(role.getSearchName(),"utf-8");
			role.setSearchName(searchName);
		}
		// 设置页面初始值及页面大小
		if (role.getPageNo() == null)
			role.setPageNo(1);
		role.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		int totalCount = 0;
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
		// 通过request对象传值到前台
		role.setTotalCount(totalCount);
		request.setAttribute("Role", role);
		request.setAttribute("roleList", roleList);

		User loginUser = this.getLoginUser();
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了角色列表查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		return "web/base/role/roleList";
	}

	/**
	 * 跳转到资源授权页面 
	 * @throws UnsupportedEncodingException 
	 **/
	@RequestMapping(value = "/toAuthorizationResource.do")
	@RequiresPermissions("system/role/toAuthorizationResource.do")
	public String authorizeResource(ResourceConfig resourceConfig,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{		
		//根据角色id回显该角色对应的资源
		List<RolePermission> roleResourceList=roleResourceService.selectByRoleId(resourceConfig.getId());
		request.setAttribute("roleResourceList", roleResourceList);

		
		//根据moudleId获取资源
		ResourceConfig resource=new ResourceConfig();
		//List<ResourceConfig> resourceList=resourceService.getResourceListBymoudleId(resourceConfig);
		
		
		/*Collections.sort(resourceList, new Comparator<ResourceConfig>() {
            public int compare(ResourceConfig arg0, ResourceConfig arg1) {
                return arg0.getMoudleId().compareTo(arg1.getMoudleId());
            }
        });*/
		
		Permission permission=new Permission();
		List<Permission> permissionList=permissionService.getPermissionList(permission);

		List<FunctionPermissionVM> list = new ArrayList<FunctionPermissionVM>();
		int tempId = 0;
		FunctionPermissionVM frvm = null;
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(Permission rc : permissionList){
			if(tempId != rc.getMoudleId()){
				tempId = rc.getMoudleId(); 
				frvm = new FunctionPermissionVM();
			}else{ 
				//用于当做FunctionResourceVM的itemList
				List<Permission> itemList = new ArrayList<Permission>();
				
				frvm.setId(rc.getMoudleId());
				frvm.setName(rc.getfName());
				for(Permission rc1 : permissionList){
					if(rc1.getMoudleId() == tempId){ 
						itemList.add(rc1);
					}
				}
				frvm.setItemList(itemList);
				if(map.isEmpty()){
					list.add(frvm);
					map.put(frvm.getId(), frvm.getId());
				}else{
					if(map.get(frvm.getId()) == null){ 
						list.add(frvm);
						map.put(frvm.getId(), frvm.getId());
					}
				}
			}
		}
		/*List<ResourceConfig> resourceList=resourceService.getResourceList(resource);
		
		List<FunctionResourceVM> list = new ArrayList<FunctionResourceVM>();
		int tempId = 0;
		FunctionResourceVM frvm = null;
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(ResourceConfig rc : resourceList){
			if(tempId != rc.getMoudleId()){
				tempId = rc.getMoudleId(); 
				frvm = new FunctionResourceVM();
			}else{ 
				//用于当做FunctionResourceVM的itemList
				List<ResourceConfig> itemList = new ArrayList<ResourceConfig>();
				
				frvm.setId(rc.getMoudleId());
				frvm.setName(rc.getFunctionName());
				for(ResourceConfig rc1 : resourceList){
					if(rc1.getMoudleId() == tempId){ 
						itemList.add(rc1);
					}
				}
				frvm.setItemList(itemList);
				if(map.isEmpty()){
					list.add(frvm);
					map.put(frvm.getId(), frvm.getId());
				}else{
					if(map.get(frvm.getId()) == null){ 
						list.add(frvm);
						map.put(frvm.getId(), frvm.getId());
					}
				}
			}
		}*/
		 
		request.setAttribute("resourceList", list);
		request.setAttribute("resourceConfig", resourceConfig);
		return "web/base/role/authorizeResource";
	}
	
	/**
	 * 资源授权
	 * @param roleId
	 * @param resourceIds
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateRoleResource.do", method = RequestMethod.POST)
	@RequiresPermissions("system/role/jsonSaveOrUpdateRoleResource.do")
	public JsonResult<RolePermission> jsonSaveOrUpdateRoleResource(
			@RequestParam(value="roleId", required=false) Integer roleId,
			@RequestParam(value="permissionId", required=false) Integer [] permissionIds,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<RolePermission> js = new JsonResult<RolePermission>();
		js.setCode(new Integer(1));
		js.setMessage("保存失败!");
		boolean state = false;
		try {
			//当roleId不等于0并且不等于null的时候才去新增
			if (roleId != 0 && roleId !=null) {
				state = roleResourceService.saveRoleResource(roleId,permissionIds);
				if (state) {
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，执行了角色授权操作", 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			}				
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return js;
	}

	/**
	 * 跳转到新增角色/编辑角色
	 */
	@RequestMapping(value = "/roleInfo.do")
	@RequiresPermissions("system/role/roleInfo.do")
	public String editRole(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest req, HttpServletResponse res) {

		// 根据参数id判断是新增还是编辑，新增为0，不用传值；编辑为选中的参数id值，取对象，传值
		if (id != null && id != 0) {
			Role role = new Role();
			try {
				role = roleService.getRoleById(id);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			req.setAttribute("Role", role);
		}
		return "web/base/role/roleInfo";
	}

	/**
	 * 新增/编辑角色
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateRole.do", method = RequestMethod.POST)
	@RequiresPermissions("system/role/jsonSaveOrUpdateRole.do")
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
	 * 删除角色
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsondeleteRoleById.do", method = RequestMethod.POST)
	@RequiresPermissions("system/role/jsondeleteRoleById.do")
	public JsonResult<Role> jsondeleteRoleById(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		// 新建一个json对象 并赋初值
		JsonResult<Role> js = new JsonResult<Role>();
		js.setCode(new Integer(1));
		js.setMessage("删除失败!");
		boolean deleteRoleById =false;
		try {
			Role role = roleService.getRoleById(id);
			deleteRoleById= roleService.deleteRoleById(id);			
			if(deleteRoleById){
				User loginUser = this.getLoginUser();
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，删除了"+role.getName()+"角色", 3, loginUser.getId(), loginUser.getUserOrgID(), ip);
				js.setCode(new Integer(0));
				js.setMessage("删除成功!");
				return js;
			}else{
				return js;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return js;

	}
}
