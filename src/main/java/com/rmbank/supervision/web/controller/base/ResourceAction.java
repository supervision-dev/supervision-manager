package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

import com.rmbank.supervision.common.JsonResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ResourceService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.web.controller.SystemAction;


@Scope("prototype")
@Controller
@RequestMapping("/system/resource")
public class ResourceAction extends SystemAction {
	
	@Resource
	private ResourceService resourceService;
	@Resource
	private SysLogService logService;
	
	/**
	 * 加载资源列表
	 * @param resourceConfig
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/resourceList.do")
	@RequiresPermissions("system/resource/resourceList.do")
	public String resourceList(ResourceConfig resourceConfig, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		//判断搜索名是否为空，不为空则转为utf-8编码
		if(resourceConfig.getSearchName() != null && resourceConfig.getSearchName() != ""){
			String searchName = URLDecoder.decode(resourceConfig.getSearchName(),"utf-8");
			resourceConfig.setSearchName(searchName);
		}
		//设置页面初始值及页面大小
		if (resourceConfig.getPageNo() == null)
			resourceConfig.setPageNo(1);
		resourceConfig.setPageSize(Constants.DEFAULT_PAGE_SIZE);  
		int totalCount =  0;
		//分页集合
		List<ResourceConfig> resourceList = new ArrayList<ResourceConfig>();
		try{
			//t_resource取满足要求的参数数据
			resourceList =  resourceService.getResourceList(resourceConfig);
			//t_resource取满足要求的记录总数
			totalCount = resourceService.getResourceCount(resourceConfig);
		}catch(Exception ex){ 
			ex.printStackTrace();
		}			
		resourceConfig.setTotalCount(totalCount); 	
		//通过request对象传值到前台
		request.setAttribute("ResourceConfig", resourceConfig);    	
    	request.setAttribute("resourceList", resourceList);
		
    	User loginUser = this.getLoginUser();
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了资源列表查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		return "web/base/resource/resourceList";
	}
	
	
	/**
	 * 跳转到新增资源/编辑资源页面
	 */
	@RequestMapping(value = "/resourceInfo.do")
	@RequiresPermissions("system/resource/resourceInfo.do")
	public String editResource(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest req, HttpServletResponse res) {

		// 根据参数id判断是新增还是编辑，新增为0，不用传值；编辑为选中的参数id值，取对象，传值
		if (id != null && id != 0) {
			ResourceConfig resourceConfig =new ResourceConfig();
			try {
				resourceConfig = resourceService.getResourceById(id);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			req.setAttribute("ResourceConfig", resourceConfig);
		}
		return "web/base/resource/resourceInfo";
	}
	
	
	/**
	 * 新增/编辑资源
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateResource.do", method = RequestMethod.POST)
	@RequiresPermissions("system/resource/jsonSaveOrUpdateResource.do")
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
	/**
	 * 删除资源
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsondeleteResourceById.do", method = RequestMethod.POST)
	@RequiresPermissions("system/resource/jsondeleteResourceById.do")
	public JsonResult<ResourceConfig> jsondeleteRoleById(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		// 新建一个json对象 并赋初值
		JsonResult<ResourceConfig> js = new JsonResult<ResourceConfig>();
		js.setCode(new Integer(1));
		js.setMessage("删除失败!");
		boolean state =false;
		try {	
			
			ResourceConfig resourceById = resourceService.getResourceById(id);
			state= resourceService.deleteResourceById(id);			
			if(state){
				User loginUser = this.getLoginUser();
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，删除了"+resourceById.getName()+"资源", 3, loginUser.getId(), loginUser.getUserOrgID(), ip);
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
