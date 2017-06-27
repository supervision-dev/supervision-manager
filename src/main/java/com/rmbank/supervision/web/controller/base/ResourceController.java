package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.rmbank.supervision.model.FunctionMenu;
import com.rmbank.supervision.model.Meta;
import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.FunctionService;
import com.rmbank.supervision.service.ResourceService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.web.controller.SystemAction;


@Scope("prototype")
@Controller
@RequestMapping("/system/resource")
public class ResourceController extends SystemAction {
	
	@Resource
	private ResourceService resourceService;
	@Resource
	private SysLogService logService;
	@Resource
	private FunctionService functionService;
	
	/**
	 * 加载资源列表
	 * @param resourceConfig
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */	
	@ResponseBody
    @RequestMapping(value = "/resourceList.do")
//	@RequiresPermissions("system/resource/roleList.do")
    public DataListResult<ResourceConfig> resourceList(ResourceConfig resource, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
    	DataListResult<ResourceConfig> dr = new DataListResult<ResourceConfig>();
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(resource.getSearchName() != null && resource.getSearchName() != ""){
			String searchName =  new String(resource.getSearchName().getBytes(
					"iso8859-1"), "utf-8");
			resource.setSearchName(searchName);
		}
		//设置页面初始值及页面大小
		if (resource.getPageNo() == null)
			resource.setPageNo(1);
		resource.setPageSize(Constants.DEFAULT_PAGE_SIZE);  
		int totalCount =  0;		
		// 分页集合
		List<ResourceConfig> resourceList = new ArrayList<ResourceConfig>();
		try {
			// t_role取满足要求的参数数据
			resourceList = resourceService.getResourceList(resource);
			// t_role取满足要求的记录总数
			totalCount = resourceService.getResourceCount(resource);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		resource.setTotalCount(totalCount);
		dr.setData(resource);
		dr.setDatalist(resourceList); 
    	return dr;
    }

    /**
     * 获取资源所属模块列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @ResponseBody
    @RequestMapping(value = "/getMoudleList.do")
//    @RequiresPermissions("system/resource/getMoudleList.do")
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
     * 根据Id获取资源主体信息
     */
    @ResponseBody
	@RequestMapping(value = "/jsonloadResourceInfo.do", method = RequestMethod.POST)
	public ResourceConfig jsonloadResourceInfo(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
    	ResourceConfig resource = null;
		try{
			if(id != null && id >0){
				resource = resourceService.getResourceById(id);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}			
		return resource;
    }

	/**
	 * 新增/编辑资源
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateResource.do", method = RequestMethod.POST) 
	public JsonResult<ResourceConfig> jsonSaveOrUpdateResource(ResourceConfig resourceConfig,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<ResourceConfig> js = new JsonResult<ResourceConfig>();
		js.setCode(new Integer(1));
		js.setMessage("保存失败!");
		boolean state = false;
		try {
			// 如为新增，则给id置0，给xtype赋默认值0
			if (resourceConfig.getId() == null || resourceConfig.getId() == 0) {
				resourceConfig.setId(0);
				resourceConfig.setXtype(0);
			}

			ResourceConfig r = new ResourceConfig();
			r.setId(resourceConfig.getId());
			r.setName(resourceConfig.getName());
			r.setResource(resourceConfig.getResource());
			// 如为编辑，则给新建ResourceConfig对象赋传来的id值,并根据ID去修改
			if (resourceConfig.getId() > 0) {
				r.setId(resourceConfig.getId());
				state = resourceService.saveOrUpdateResource(resourceConfig);
				if (state) {
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，执行了修改资源信息", 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			}
			// 根据资源名称（name）和资源地址（resource）去数据库匹配，如编辑，则可以直接保存；如新增，则需匹配资源名称和资源地址是否重复
			List<ResourceConfig> lc = resourceService.getExistRresource(r);
			if (lc.size() == 0) {
				state = resourceService.saveOrUpdateResource(resourceConfig);
				if (state) {
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，新增了"+resourceConfig.getName()+"资源", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			} else {
				js.setMessage("该资源已存在!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return js;
	}
}
