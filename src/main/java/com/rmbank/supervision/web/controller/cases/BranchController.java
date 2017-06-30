 
package com.rmbank.supervision.web.controller.cases;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.rmbank.supervision.model.FunctionResourceVM;
import com.rmbank.supervision.model.GradeScheme;
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.ItemProcess;
import com.rmbank.supervision.model.ItemProcessFile;
import com.rmbank.supervision.model.Meta;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.OrganVM;
import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ConfigService;
import com.rmbank.supervision.service.ItemProcessFileService;
import com.rmbank.supervision.service.ItemProcessService;
import com.rmbank.supervision.service.ItemService;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.RoleService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;
/**
 * 分行立项的Controller
 * @author DELL
 *
 */
@Scope("prototype")
@Controller
@RequestMapping("/manage/branch")
public class BranchController extends SystemAction {
	
	/**
	 * 资源注入
	 */
	@Resource
	private ConfigService configService;
	@Resource
	private OrganService organService;
	@Resource
	private ItemService itemService;
	@Resource
	private ItemProcessService itemProcessService;
	@Resource
	private ItemProcessFileService itemProcessFileService;
	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;
	@Resource
	private SysLogService logService;

	/**
	 * 分行立项分行完成列表
	 * @param gradeScheme
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
    @RequestMapping(value = "/branchFHList.do")
//	@RequiresPermissions("manage/casemanage/gradeSchemeList.do")
    public DataListResult<Item> branchFHList(Item item, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
    	
		DataListResult<Item> dr = new DataListResult<Item>();
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(item.getSearchName() != null && item.getSearchName() != ""){
			String searchName =  new String(item.getSearchName().getBytes(
					"iso8859-1"), "utf-8");
			item.setSearchName(searchName);
		}
		//设置页面初始值及页面大小
		if (item.getPageNo() == null)
			item.setPageNo(1);
		item.setPageSize(Constants.DEFAULT_PAGE_SIZE);  
		int totalCount =  0;		
		//当前登录用户所属的机构
		User loginUser = this.getLoginUser();
		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Integer logUserOrg = userOrgByUserId.get(0).getId(); //当前登录用户所属的机构ID
		Organ organ = userOrgByUserId.get(0);
		//获取项目列表,根据不同的机构类型加载不同的项目
		List<Item> itemList =null;
		//成都分行和超级管理员加载所有项目
		if(organ.getOrgtype()==Constants.ORG_TYPE_1 ||
				organ.getOrgtype()==Constants.ORG_TYPE_2 ||
						organ.getOrgtype()==Constants.ORG_TYPE_3 ||
								organ.getOrgtype()==Constants.ORG_TYPE_4 ||
						Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
			//分行立项分行完成
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setSupervisionOrgId(logUserOrg); //完成机构
			item.setPreparerOrgId(Constants.BRANCH_INPUTITEM_ORGID);    //立项机构			
			item.setOrgTypeB(Constants.ORG_TYPE_5);	 //完成机构的类型		
			itemList=itemService.getItemListByFHLXFHWC(item); 
			totalCount=itemService.getItemCountByFHLXFHWC(item);
			item.setTotalCount(totalCount);
		}else{
			//分行立项分行完成
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setSupervisionOrgId(logUserOrg); //完成机构
			item.setPreparerOrgId(Constants.BRANCH_INPUTITEM_ORGID); //立项机构
			item.setOrgTypeA(Constants.ORG_TYPE_5);	//完成机构的类型		
			itemList=itemService.getItemListByLogOrgFHLXFHWC(item); 
			totalCount=itemService.getItemCountByLogOrgFHLXFHWC(item);
			item.setTotalCount(totalCount);
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		for (Item it : itemList) {
			Date preparerTime = it.getPreparerTime();
			String format = formatter.format(preparerTime);
			it.setShowDate(format);
			List<ItemProcess> itemprocessList = itemProcessService.getItemProcessItemId(it.getId());
			if(itemprocessList.size()>0){
				ItemProcess lastItem = itemprocessList.get(itemprocessList.size()-1);
				it.setLasgTag(lastItem.getContentTypeId());
			}
		}
		item.setTotalCount(totalCount);
		dr.setData(item);
		dr.setDatalist(itemList); 
    	return dr;
    }
	
	
	
	/**
	 * 分行立项中支完成列表
	 * @param gradeScheme
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
    @RequestMapping(value = "/branchZZList.do")
//	@RequiresPermissions("manage/casemanage/gradeSchemeList.do")
    public DataListResult<Item> branchZZList(Item item, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
    	
		DataListResult<Item> dr = new DataListResult<Item>();
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(item.getSearchName() != null && item.getSearchName() != ""){
			String searchName =  new String(item.getSearchName().getBytes(
					"iso8859-1"), "utf-8");
			item.setSearchName(searchName);
		}
		//设置页面初始值及页面大小
		if (item.getPageNo() == null)
			item.setPageNo(1);
		item.setPageSize(Constants.DEFAULT_PAGE_SIZE);  
		int totalCount =  0;		
		//获取项目分类的集合,用于搜索条件		
		List<Meta> meatListByKey = configService.getMeatListByKey(Constants.META_PROJECT_KEY);
		request.setAttribute("meatListByKey", meatListByKey);
		
		//当前登录用户所属的机构
		User loginUser = this.getLoginUser();

		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Integer logUserOrg = userOrgByUserId.get(0).getId(); //当前登录用户所属的机构ID
		Organ organ = userOrgByUserId.get(0);
		//获取项目列表,根据不同的机构类型加载不同的项目
		List<Item> itemList =null; 
		if(organ.getOrgtype()==Constants.ORG_TYPE_1 ||
				organ.getOrgtype()==Constants.ORG_TYPE_2 ||
					organ.getOrgtype()==Constants.ORG_TYPE_3 ||
						organ.getOrgtype()==Constants.ORG_TYPE_4 ||
					Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
			//分行立项中支完成
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setSupervisionOrgId(logUserOrg); //完成机构
			item.setPreparerOrgId(Constants.BRANCH_INPUTITEM_ORGID);    //立项机构
			item.setOrgTypeA(Constants.ORG_TYPE_6); //完成机构的类型
			itemList=itemService.getItemListByFHLXZZWC(item);
			totalCount=itemService.getItemCountByFHLXZZWCALL(item);
			item.setTotalCount(totalCount);
		}else{
			//分行立项中支完成
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setSupervisionOrgId(logUserOrg); //完成机构
			item.setPreparerOrgId(Constants.BRANCH_INPUTITEM_ORGID);    //立项机构
			item.setOrgTypeA(Constants.ORG_TYPE_6); //完成机构的类型
			itemList=itemService.getItemListByLogOrgFHLXZZWC(item);
			totalCount=itemService.getItemCountByLogOrgFHLXZZWC(item);
			item.setTotalCount(totalCount);
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		for (Item it : itemList) {
			Date preparerTime = it.getPreparerTime();
			String format = formatter.format(preparerTime);
			it.setShowDate(format);
		}
		
		item.setTotalCount(totalCount);
		dr.setData(item);
		dr.setDatalist(itemList); 
    	return dr;
    }
}