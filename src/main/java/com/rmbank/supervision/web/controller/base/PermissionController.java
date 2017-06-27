package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.rmbank.supervision.model.FunctionMenu;
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
//	@RequiresPermissions("system/permission/permissionList.do")
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

    /**
     * 获取权限所属模块列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @ResponseBody
    @RequestMapping(value = "/getMoudleList.do")
//    @RequiresPermissions("system/permission/getMoudleList.do")
    public List<FunctionMenu> getPostList(HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException { 
    	FunctionMenu fm = new FunctionMenu();
    	fm.setLeaf(1);
    	List<FunctionMenu>  list = functionService.getFunctionMenuList(fm);
    	if(list != null){
	    	Collections.sort(list, new Comparator<FunctionMenu>() {
	            public int compare(FunctionMenu arg0, FunctionMenu arg1) {
	                return arg0.getName().compareTo(arg1.getName());
	            }
	        });
    	}
    	return list;
    }


    /**
     * 根据Id获取权限主体信息
     */
    @ResponseBody
	@RequestMapping(value = "/jsonloadPermissionInfo.do", method = RequestMethod.POST)
	public Permission jsonloadPermissionInfo(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
    	Permission permit = null;
		try{
			if(id != null && id >0){
				permit = permissionService.selectByPrimaryKey(id);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}			
		return permit;
    }

	/**
	 * 新增/编辑权限
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdatePermission.do", method = RequestMethod.POST) 
	public JsonResult<Permission> jsonSaveOrUpdatePermission(Permission permission,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<Permission> js = new JsonResult<Permission>();
		js.setCode(new Integer(1));
		js.setMessage("保存失败!");
		boolean saveOrUpdateRole = false;
		try {
			// 如为新增，则给id置0
			if (permission.getId() == null || permission.getId() == 0) {
				permission.setId(0);
			}

			Permission per = new Permission();
			per.setId(permission.getId());
			per.setName(permission.getName());
			// 如为编辑，则给新建role对象赋传来的id值
			if (permission.getId() > 0) {
				per.setId(permission.getId());
				saveOrUpdateRole = permissionService.saveOrUpdatePermission(permission);
				if (saveOrUpdateRole) {
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，执行了修改权限信息操作", 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			}
			// 根据设备编号和id去数据库匹配，如编辑，则可以直接保存；如新增，则需匹配设备编号是否重复
			List<Permission> lc = permissionService.getExistPermission(per);
			if (lc.size() == 0) {
				saveOrUpdateRole = permissionService.saveOrUpdatePermission(permission);
				if (saveOrUpdateRole) {
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，新增了"+per.getName()+"权限", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			} else {
				js.setMessage("该权限已存在!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
    @RequestMapping(value = "/getRecourceList.do")
    public FunctionResourceVM  getRecourceList(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException { 
    	FunctionResourceVM frvm  = new FunctionResourceVM();
    	FunctionMenu fm = functionService.getFunctionMenusById(id); 
		frvm.setId(fm.getId());
		frvm.setName(fm.getName());
		List<ResourceConfig> itemList = new ArrayList<ResourceConfig>();
		ResourceConfig rc = new ResourceConfig();
		rc.setMoudleId(fm.getId());
		itemList = resourceService.getResourceListBymoudleId(rc);
		frvm.setItemList(itemList); 
		
    	return frvm;
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
    @RequestMapping(value = "/jsonloadResList.do")
    public List<PermissionResource>  jsonloadResList(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException { 
    	List<PermissionResource> list = permissionResourceService.selectByPermissionId(id);
    	return list;
    }
	
}
