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

import com.rmbank.supervision.common.DataListResult;
import com.rmbank.supervision.common.JsonResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.User;
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
//	@RequiresPermissions("system/role/roleList.do")
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
	
	
}
