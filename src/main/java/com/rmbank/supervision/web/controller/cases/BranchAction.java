 
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
 * 分行立项的Action
 * @author DELL
 *
 */
@Scope("prototype")
@Controller
@RequestMapping("/manage/branch")
public class BranchAction extends SystemAction {
	
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
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/branchFHList.do")
	@RequiresPermissions("manage/branch/branchFHList.do")
	public String branchFHList(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		if (item.getSearchName() != null && item.getSearchName() != "") {
			String searchName = new String(item.getSearchName().getBytes(
					"iso8859-1"), "utf-8");
			item.setSearchName(searchName);
		}		
		if (item.getPageNo() == null){
			item.setPageNo(1);
		} 
		item.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		int totalCount = 0;
		
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
		//成都分行和超级管理员加载所有项目
		if(organ.getOrgtype()==Constants.ORG_TYPE_1 ||
				organ.getOrgtype()==Constants.ORG_TYPE_2 ||
						organ.getOrgtype()==Constants.ORG_TYPE_3 ||
						Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
			//分行立项分行完成
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setSupervisionOrgId(logUserOrg); //完成机构
			item.setPreparerOrgId(logUserOrg);    //立项机构			
			item.setOrgTypeB(Constants.ORG_TYPE_4);			
			itemList=itemService.getItemListByFHLXFHWC(item); 
			totalCount=itemService.getItemCountByFHLXFHWC(item);
			item.setTotalCount(totalCount);
		}else{
			//分行立项分行完成
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setSupervisionOrgId(logUserOrg); //完成机构
			item.setPreparerOrgId(logUserOrg); //立项机构
			item.setOrgTypeA(Constants.ORG_TYPE_4);			
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
		
		List<Role> userRoleList=roleService.getRolesByUserId(loginUser.getId());
		Role userRole = userRoleList.get(0);
		request.setAttribute("userRole", userRole);
		
		request.setAttribute("UserOrg", organ);
		request.setAttribute("itemList", itemList);
		request.setAttribute("Item", item); 
		request.setAttribute("LoginUser", loginUser);
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了分行立项分行完成列表查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		return "web/manage/branch/branchFHList";
	}

	/**
	 * 分行立项中支完成列表
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/branchZZList.do")
	@RequiresPermissions("manage/branch/branchZZList.do")
	public String branchZZList(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		if (item.getSearchName() != null && item.getSearchName() != "") {
			String searchName = new String(item.getSearchName().getBytes(
					"iso8859-1"), "utf-8");
			item.setSearchName(searchName);
		}		
		if (item.getPageNo() == null){
			item.setPageNo(1);
		} 
		item.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		int totalCount = 0;
		
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
						Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
			//分行立项中支完成
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setSupervisionOrgId(logUserOrg); //完成机构
			item.setPreparerOrgId(logUserOrg);    //立项机构
			item.setOrgTypeA(Constants.ORG_TYPE_5);
			itemList=itemService.getItemListByFHLXZZWC(item);
			totalCount=itemService.getItemCountByFHLXZZWCALL(item);
			item.setTotalCount(totalCount);
		}else{
			//分行立项中支完成
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setSupervisionOrgId(logUserOrg); //完成机构
			item.setPreparerOrgId(logUserOrg);    //立项机构
			item.setOrgTypeA(Constants.ORG_TYPE_5);
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
		
		List<Role> userRoleList=roleService.getRolesByUserId(loginUser.getId());
		Role userRole = userRoleList.get(0);
		request.setAttribute("userRole", userRole);
		
		request.setAttribute("UserOrg", organ);
		request.setAttribute("itemList", itemList);
		request.setAttribute("Item", item); 
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了分行立项中支完成列表查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		return "web/manage/branch/branchZZList";
	}
	/**
	 * 跳转到新增项目
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/branchFHInfo.do")
	@RequiresPermissions("manage/branch/branchFHInfo.do")
	public String branchFHInfo( 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		//获取项目分类的集合		
		List<Meta> meatListByKey = configService.getMeatListByKey(Constants.META_PROJECT_KEY);
		request.setAttribute("meatListByKey", meatListByKey);
		
		//获取所有机构
		Organ organ=new Organ();
		List<Organ> organList = organService.getOrganList(organ);
		
		//封装到前台遍历机构集合
		List<OrganVM> list=new ArrayList<OrganVM>();
		OrganVM frvm = null;
		  
		for(Organ rc : organList){
			if(rc.getId()==21){//父节点是分行机关的时候创建返回到前台对象
				frvm = new OrganVM(); 
				List<Organ> itemList = new ArrayList<Organ>();//用于当做OrganVM的itemList
				frvm.setId(rc.getId());
				frvm.setName(rc.getName());					
				for(Organ rc1 : organList){									
					if(rc1.getPid() == rc.getId()){ 
						itemList.add(rc1);
					}
				}
				frvm.setItemList(itemList);
				list.add(frvm);
			}
		}
			  
		request.setAttribute("OrgList", list);	
		return "web/manage/branch/branchFHInfo";
	}
	
	/**
	 * 跳转到新增项目
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/branchZZInfo.do")
	@RequiresPermissions("manage/branch/branchZZInfo.do")
	public String branchZZInfo( 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		//获取项目分类的集合		
		List<Meta> meatListByKey = configService.getMeatListByKey(Constants.META_PROJECT_KEY);
		request.setAttribute("meatListByKey", meatListByKey);
		
		//获取所有机构
		Organ organ=new Organ();
		List<Organ> organList = organService.getOrganList(organ);
		
		//封装到前台遍历机构集合
		List<OrganVM> list=new ArrayList<OrganVM>();
		OrganVM frvm = null;
		 
			//分行立项中支完成，只加载分行营管部机构， 中支机构，县支行（此处县支行包括营管部及中支下属所有县支行）；
			for(Organ rc : organList){
				if(rc.getId()==20 ){
					frvm = new OrganVM(); 
					List<Organ> itemList = new ArrayList<Organ>();//用于当做OrganVM的itemList
					frvm.setId(rc.getId());
					frvm.setName(rc.getName());
					String path = frvm.getId()+"."; //该父节点下所有孙子节点的path都以此开头
					for(Organ rc1 : organList){						
						String path2 = rc1.getPath(); //当前节点的path
						String substring =null;					
						if(path2.length()>path.length()){
							substring= path2.substring(0, path.length());						
						}					
						if(rc1.getPid() == rc.getId() || path.equals(substring)){ 
							itemList.add(rc1);
						}
					}
					frvm.setItemList(itemList);
					list.add(frvm);
				}
			} 
		
		request.setAttribute("OrgList", list);	
		return "web/manage/branch/branchZZInfo";
	}
	/**
	 * 跳转到分行上传资料
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/branchFHFile.do")
	@RequiresPermissions("manage/branch/branchFHFile.do")
	public String branchFHFile(Item item,
			HttpServletRequest request, HttpServletResponse response){
		int tag = item.getTag();
		item = itemService.selectByPrimaryKey(item.getId());
		if(item.getPreparerTime() != null){
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item.getPreparerTime()));
		}
		List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item.getId());  

		if(itemProcessList.size()>0){ 
			for(ItemProcess ip : itemProcessList){  
				List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
				fileList = itemProcessFileService.getFileListByItemId(ip.getId());
				ip.setFileList(fileList);  
				if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_1){
					request.setAttribute("ItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_2){
					request.setAttribute("FileItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_3){
					if(!StringUtil.isEmpty(ip.getContent())){
						if(ip.getContent().contains("&&")){
							String[] content = ip.getContent().split("&&");
							if(content.length >0){
								ip.setContent(content[0]);
							}
							if(content.length>1){
								ip.setChangeContent(content[1]);
							}
						}
					}
					request.setAttribute("ChangeItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_4){
					request.setAttribute("FollowItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_5){
					request.setAttribute("OverItemProcess", ip);
				}
			}
		}
		//获取当前用户
		User lgUser=this.getLoginUser(); 
		   
		request.setAttribute("User", lgUser);  
		request.setAttribute("tag", tag);
		request.setAttribute("Item", item); 
		request.setAttribute("ContentTypeId", Constants.CONTENT_TYPE_ID_ZZZZ_OVER);
		return "web/manage/branch/branchFHFile";
	}
	/**
	 * 跳转到中支上传资料
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/branchZZFile.do")
	@RequiresPermissions("manage/branch/branchZZFile.do")
	public String branchZZFile(Item item,
			HttpServletRequest request, HttpServletResponse response){
 
		item = itemService.selectByPrimaryKey(item.getId());
		if(item.getPreparerTime() != null){
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item.getPreparerTime()));
		}
		List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item.getId()); 
		ItemProcess itemProcess = new ItemProcess();
		if(itemProcessList.size()>0){
			itemProcess = itemProcessList.get(0); 
		}
		
		List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
		if(itemProcess.getId() != null){
			fileList = itemProcessFileService.getFileListByItemId(itemProcess.getId());
		}
		//获取当前用户
		User lgUser=this.getLoginUser(); 
		   
		request.setAttribute("User", lgUser); 
		request.setAttribute("ItemProcess", itemProcess);
		request.setAttribute("Item", item);
		request.setAttribute("FileList", fileList);
		request.setAttribute("ContentTypeId", Constants.CONTENT_TYPE_ID_FHZZ);
		return "web/manage/branch/branchZZFile";
	}
	
	/**
	 * 跳转到分行查看
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/branchFHView.do")
	@RequiresPermissions("manage/branch/branchFHView.do")
	public String branchFHView(Item item,
			HttpServletRequest request, HttpServletResponse response){
 
		item = itemService.selectByPrimaryKey(item.getId());
		if(item.getPreparerTime() != null){
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item.getPreparerTime()));
		}
		List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item.getId());  
		if(itemProcessList.size()>0){ 
			for(ItemProcess ip : itemProcessList){  
				List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
				fileList = itemProcessFileService.getFileListByItemId(ip.getId());
				ip.setFileList(fileList);  
				if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_1){
					request.setAttribute("ItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_2){
					request.setAttribute("FileItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_3){
					if(!StringUtil.isEmpty(ip.getContent())){
						if(ip.getContent().contains("&&")){
							String[] content = ip.getContent().split("&&");
							if(content.length >0){
								ip.setContent(content[0]);
							}
							if(content.length>1){
								ip.setChangeContent(content[1]);
							}
						}
					}
					request.setAttribute("ChangeItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_4){
					request.setAttribute("FollowItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_5){
					if(request.getAttribute("OverItemProcess") != null){
						if(fileList.size() == 0){
							ip.setFileList(null);
						}
						request.setAttribute("OtherProcess", ip);
					}else{
						if(fileList.size() == 0){
							ip.setFileList(null);
						}
						request.setAttribute("OverItemProcess", ip);
					}
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_6){
					request.setAttribute("FollowChangeProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_7){
					request.setAttribute("OverProcess", ip);
				} 
			}
		}
		
		//获取当前用户
		User lgUser=this.getLoginUser(); 
		   
		request.setAttribute("User", lgUser);  
		request.setAttribute("Item", item); 
		request.setAttribute("ContentTypeId", Constants.CONTENT_TYPE_ID_ZZZZ_OVER);
		
		User loginUser = this.getLoginUser();
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了分行立项分行完成项目的查看", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		return "web/manage/branch/branchFHViewForm";
	}
	/**
	 * 跳转到中支查看
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/branchZZView.do")
	@RequiresPermissions("manage/branch/branchZZView.do")
	public String branchZZView(Item item,
			HttpServletRequest request, HttpServletResponse response){
 
		item = itemService.selectByPrimaryKey(item.getId());
		if(item.getPreparerTime() != null){
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item.getPreparerTime()));
		}
		List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item.getId()); 
		
		ItemProcess fitemProcess = new ItemProcess();
		ItemProcess sitemProcess = new ItemProcess();
		if(itemProcessList.size()>0){
			fitemProcess = itemProcessList.get(0);
			if(itemProcessList.size() ==2){
				sitemProcess = itemProcessList.get(1);
			}
		}
		
		List<ItemProcessFile> ffileList = new ArrayList<ItemProcessFile>();
		List<ItemProcessFile> sfileList = new ArrayList<ItemProcessFile>();
		if(fitemProcess.getId() != null){
			ffileList = itemProcessFileService.getFileListByItemId(fitemProcess.getId());
			fitemProcess.setFileList(ffileList);
		}
		if(sitemProcess.getId() != null){
			sfileList = itemProcessFileService.getFileListByItemId(sitemProcess.getId());
			sitemProcess.setFileList(sfileList);
		}
		//获取当前用户
		User lgUser=this.getLoginUser();  
		
		request.setAttribute("User", lgUser); 
		request.setAttribute("FItemProcess", fitemProcess);
		request.setAttribute("SItemProcess", sitemProcess);
		request.setAttribute("Item", item);   
		User loginUser = this.getLoginUser();
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了分行立项中支完成项目的查看", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		return "web/manage/branch/branchZZViewForm";
	}
	/**
	 * 分行监察室，确认是否跟踪项目
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/branchFollowChange.do")
	@RequiresPermissions("manage/branch/branchFollowChange.do")
	public String branchFollowChange(Item item,
			HttpServletRequest request, HttpServletResponse response){ 
		item = itemService.selectByPrimaryKey(item.getId());
		if(item.getPreparerTime() != null){
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item.getPreparerTime()));
		}
		List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item.getId());  
		if(itemProcessList.size()>0){ 
			for(ItemProcess ip : itemProcessList){  
				List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
				fileList = itemProcessFileService.getFileListByItemId(ip.getId());
				ip.setFileList(fileList);  
				if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_1){
					request.setAttribute("ItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_2){
					request.setAttribute("FileItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_3){
					if(!StringUtil.isEmpty(ip.getContent())){
						if(ip.getContent().contains("&&")){
							String[] content = ip.getContent().split("&&");
							if(content.length >0){
								ip.setContent(content[0]);
							}
							if(content.length>1){
								ip.setChangeContent(content[1]);
							}
						}
					}
					request.setAttribute("ChangeItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_4){
					request.setAttribute("FollowItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_5){
					request.setAttribute("OverItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_6){
					request.setAttribute("FollowChangeProcess", ip);
				} 
			}
		}
		
		//获取当前用户
		User lgUser=this.getLoginUser(); 
		   
		request.setAttribute("User", lgUser);  
		request.setAttribute("Item", item);  
		request.setAttribute("ContentTypeId", Constants.CONTENT_TYPE_ID_ZZZZ_OVER);
		return "web/manage/branch/branchFollowChange";
	}
	
	/**
	 * 跳转到被监察对象上报跟踪肩擦整改情况
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/branchFollowForm.do")
	@RequiresPermissions("manage/branch/branchFollowForm.do")
	public String branchFollowForm(Item item,
			HttpServletRequest request, HttpServletResponse response){
		int tag  = item.getTag();
		item = itemService.selectByPrimaryKey(item.getId());
		if(item.getPreparerTime() != null){
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item.getPreparerTime()));
		}
		List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item.getId());  
		if(itemProcessList.size()>0){ 
			for(ItemProcess ip : itemProcessList){  
				List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
				fileList = itemProcessFileService.getFileListByItemId(ip.getId());
				ip.setFileList(fileList);  
				if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_1){
					request.setAttribute("ItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_2){
					request.setAttribute("FileItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_3){
					if(!StringUtil.isEmpty(ip.getContent())){
						if(ip.getContent().contains("&&")){
							String[] content = ip.getContent().split("&&");
							if(content.length >0){
								ip.setContent(content[0]);
							}
							if(content.length>1){
								ip.setChangeContent(content[1]);
							}
						}
					}
					request.setAttribute("ChangeItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_4){
					request.setAttribute("FollowItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_5){
					request.setAttribute("OverItemProcess", ip);
				}else if(ip.getContentTypeId() ==Constants.CONTENT_TYPE_ID_6){
					request.setAttribute("FollowChangeProcess", ip);
				} 
			}
		}
		
		//获取当前用户
		User lgUser=this.getLoginUser(); 
		   
		request.setAttribute("User", lgUser);  
		request.setAttribute("Item", item); 
		request.setAttribute("tag", tag); 
		request.setAttribute("ContentTypeId", Constants.CONTENT_TYPE_ID_ZZZZ_OVER);
		return "web/manage/branch/branchFollowForm";
	}
	
	
	 /** 
	 * 新增,编辑项目
	 * @throws ParseException 
	 */
    @ResponseBody
    @RequestMapping(value = "/jsonSaveOrUpdateItem.do", method=RequestMethod.POST)
    @RequiresPermissions("manage/branch/jsonSaveOrUpdateItem.do")
    public JsonResult<Item> jsonSaveOrUpdateItem(Item item,
    		@RequestParam(value = "pTime", required = false) String pTime,//用于接收前台传过来的String类型的时间
    		@RequestParam(value = "end_time", required = false) String endTime,//用于接收前台传过来的String类型的时间
    		@RequestParam(value = "content", required = false) String content,
    		@RequestParam(value = "OrgId", required = false) Integer[] OrgIds,    		
    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
    	//将前台传过来的String类型的时间转换为Date类型
    	 
    	item.setPreparerTime(Constants.DATE_FORMAT1.parse(pTime)); //制单时间
    	item.setEndTime(Constants.DATE_FORMAT1.parse(endTime)); //制单时间
    	item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE); //项目类型
    	item.setPid(0); //主任务节点的ID
    	item.setStageIndex(new Byte("0")); //工作阶段排序   	
    	//获取当前登录用户
    	User u = this.getLoginUser();
    	item.setPreparerId(u.getId()); //制单人的ID
    	item.setSupervisionUserId(0); //
    	//获取当前用户所属的机构id，当做制单部门的ID
    	List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
    	item.setPreparerOrgId(userOrgIDs.get(0)); //制单部门的ID
    	//新建一个json对象 并赋初值
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");
		
		boolean State =  false;
		try {
			//如为新增，则给id置0
			if (item.getId() == null || item.getId() == 0) {
				item.setId(0);					
			} 		
			//创建用于新增时根据项目名称去查询项目是否存在的对象
			Item im = new Item();
			im.setName(item.getName());
			//根据name去数据库匹配，如编辑，则可以直接保存；如新增，则需匹配该项目是否重复
			List<Item> lc = itemService.getExistItem(im);
			if (lc.size() == 0) {  
				State = itemService.saveOrUpdateItem(item,OrgIds,content);				
				if(State){
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，新增了分行立项", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(0);
					js.setMessage("保存项目信息成功!");
					return js;
				}else{
					return js;
				}
			} else {
				js.setMessage("该项目已存在!");
				return js;
			} 
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return js;
    }
     
     
	/** 
	 * 上传资料
	 * @throws ParseException 
	 * @throws UnsupportedEncodingException 
	 */
    @ResponseBody
    @RequestMapping(value = "/jsonSaveOrUpdateFileItem.do", method=RequestMethod.POST)
    @RequiresPermissions("manage/support/jsonSaveOrUpdateFileItem.do")
    public JsonResult<ItemProcess> jsonSaveOrUpdateFileItem(
            @RequestParam(value = "s", required = true) String uuid,ItemProcess itemProcess, 
    		HttpServletRequest request, HttpServletResponse response) throws ParseException, UnsupportedEncodingException{
    	//新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>(); 
		uuid = URLDecoder.decode(uuid,"utf-8");
    	//获取当前登录用户
    	User u = this.getLoginUser(); 
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");
		try {   
	    	//获取当前用户所属的机构id，当做制单部门的ID
	    	List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
	    	itemProcess.setPreparerOrgId(userOrgIDs.get(0)); //制单部门的ID
	    	itemProcess.setOrgId(userOrgIDs.get(0)); //制单部门的ID
	    	itemProcess.setPreparerId(u.getId());
	    	itemProcess.setPreparerTime(new Date());
			itemProcess.setDefined(false); 
			if(itemProcess.getIsOver() != null){ 
				if(itemProcess.getIsOver() == Constants.IS_OVER){ 
					if(itemProcess.getTag() != null){
						itemProcess.setContentTypeId(itemProcess.getTag());
					}else{
						itemProcess.setContentTypeId(Constants.CONTENT_TYPE_ID_5);
					}
					Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
					if(item != null){
//						item.setEndTime(new Date());
						item.setStatus(Constants.ITEM_STATUS_OVER);
						itemService.updateByPrimaryKeySelective(item);
					} 
					js.setMessage("监察过程处理成功"); 
				}else{
					if(itemProcess.getIsOverStatus() == Constants.IS_OVER){
						if(itemProcess.getTag() != null){
							itemProcess.setContentTypeId(itemProcess.getTag());
						}else{
							itemProcess.setContentTypeId(Constants.CONTENT_TYPE_ID_5);
						}
						Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
						if(item != null){
//							item.setEndTime(new Date());
							item.setStatus(Constants.ITEM_STATUS_OVER);
							itemService.updateByPrimaryKeySelective(item);
						} 
						js.setMessage("监察过程处理成功"); 
					}else{
						if(itemProcess.getIsFollowStatus() != null && itemProcess.getIsFollowStatus() == Constants.NOT_OVER){
							if(!StringUtil.isEmpty(itemProcess.getChangeContent())){
								String changeContent = itemProcess.getChangeContent();
								String totalContent = changeContent + "&&" + itemProcess.getContent();
								itemProcess.setContent(totalContent);
							}
							itemProcess.setContentTypeId(Constants.CONTENT_TYPE_ID_3);
						}else{
							itemProcess.setContentTypeId(Constants.CONTENT_TYPE_ID_6);
						}
					}
					js.setMessage("监察过程处理成功"); 
				} 
			}else{ 
				js.setMessage("监察过程处理成功"); 
			} 

			itemProcessService.insert(itemProcess);
			if(itemProcess.getContentTypeId() == Constants.CONTENT_TYPE_ID_FHZZ){
				Item temp = itemService.selectByPrimaryKey(itemProcess.getItemId());
				temp.setStatus(4);
//				temp.setEndTime(new Date());
				itemService.updateByPrimaryKeySelective(temp);
			}
			js.setCode(0);
		}catch(Exception ex){
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
    }
    /**
     * 删除项目
     * @param id
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/jsondeleteItemById.do", method = RequestMethod.POST)
	@RequiresPermissions("manage/branch/jsondeleteItemById.do")
	public JsonResult<Item> jsondeleteItemById(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		// 新建一个json对象 并赋初值
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("删除失败!");
		boolean state =false;
		try {				
			Item item = itemService.selectByPrimaryKey(id);
			if(item!=null){
				state= itemService.deleteItemById(id);
				if(state){
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，删除了“"+item.getName()+"”项目", 3, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("删除成功!");
					return js;
				}else{
					return js;
				}		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return js;

	} 
	
	 /**
     * 跟踪项目
     * @param item
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/jsonfollowItemById.do", method = RequestMethod.POST)
	@RequiresPermissions("manage/branch/jsonfollowItemById.do")
	public JsonResult<Item> jsonfollowItemById( 
			Item item,
			HttpServletRequest request, HttpServletResponse response) { 
		// 新建一个json对象 并赋初值 
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("操作失败!"); 
		try {		 
			Integer isFollow = item.getIsFollow(); 
			String content = item.getContent();
			item = itemService.selectByPrimaryKey(item.getId());
			if(isFollow>0){
				if(item != null){
//					item.setEndTime(new Date());
					item.setStatus(Constants.ITEM_STATUS_OVER);
					itemService.updateByPrimaryKeySelective(item);
				} 
				js.setCode(0);
				js.setMessage("不跟踪当前项目，当前项目完结!"); 
			}else{
				User u = this.getLoginUser();
		    	List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
				List<ItemProcess> itemprocessList = itemProcessService.getItemProcessItemId(item.getId());
				if(itemprocessList.size()>0){
					ItemProcess it = itemprocessList.get(itemprocessList.size()-1);
			    	it.setPreparerOrgId(userOrgIDs.get(0)); //制单部门的ID
			    	it.setOrgId(userOrgIDs.get(0)); //制单部门的ID
			    	it.setPreparerId(u.getId());
			    	it.setPreparerTime(new Date());
			    	it.setContent(content);
			    	it.setDefined(false); 
			    	UUID uid = UUID.randomUUID();
			    	String uuid = uid.toString();
			    	it.setUuid(uuid);
			    	it.setContentTypeId(Constants.CONTENT_TYPE_ID_6); 
			    	it.setId(0);
			    	itemProcessService.insert(it);
				}
				js.setCode(0);
				js.setMessage("跟踪项目成功 !"); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return js;

	}
	
}