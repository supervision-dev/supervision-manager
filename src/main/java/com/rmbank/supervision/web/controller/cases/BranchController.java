 
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

import com.rmbank.supervision.common.BaseItemResult;
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
import com.rmbank.supervision.model.ItemProcessGrade;
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
		item.setLogOrgId(logUserOrg);
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
		dr.setLoginOrganRoleType(organ.getOrgtype());
		dr.setData(item);
		dr.setDatalist(itemList); 
    	return dr;
    }
	
	
	
	/**
	 * 分行立项分行立项分行完成完成列表
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
		item.setLogOrgId(logUserOrg);
		//获取项目列表,根据不同的机构类型加载不同的项目
		List<Item> itemList =null; 
		if(organ.getOrgtype()==Constants.ORG_TYPE_1 ||
				organ.getOrgtype()==Constants.ORG_TYPE_2 ||
					organ.getOrgtype()==Constants.ORG_TYPE_3 ||
						organ.getOrgtype()==Constants.ORG_TYPE_4 ||
					Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
			//分行立项分行立项分行完成完成
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setSupervisionOrgId(logUserOrg); //完成机构
			item.setPreparerOrgId(Constants.BRANCH_INPUTITEM_ORGID);    //立项机构
			item.setOrgTypeA(Constants.ORG_TYPE_6); //完成机构的类型
			itemList=itemService.getItemListByFHLXZZWC(item);
			totalCount=itemService.getItemCountByFHLXZZWCALL(item);
			item.setTotalCount(totalCount);
		}else{
			//分行立项分行立项分行完成完成
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
			List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(it.getId());
			if(itemProcessList.size()>0){
				ItemProcess ip = itemProcessList.get(itemProcessList.size() - 1);
				it.setLasgTag(ip.getContentTypeId());
			} 
		}
		
		item.setTotalCount(totalCount);
		dr.setLoginOrganRoleType(organ.getOrgtype());
		dr.setData(item);
		dr.setDatalist(itemList); 
    	return dr;
    }
	

	/**
	 * 加载所有项目类型
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loadItemType.do")
	public List<Meta> loadItemType( 
			HttpServletRequest request, HttpServletResponse response) {
		List<Meta> configList = new ArrayList<Meta>();
		try { 
			configList = configService.getMeatListByKey("PROJECT");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return configList;
	}  

	

	/**
	 * 加载机构的树
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loadOrganTreeListByType.do")
	public List<Organ> getOrganList(
			@RequestParam(value = "ptype", required = false) Integer pType,
			HttpServletRequest request, HttpServletResponse response) {
		
		Organ organ = new Organ();
		if(pType == 0){
			organ.setPid(0);
			organ.setOrgtype(1);
		}else{
			organ.setPid(0);
			organ.setOrgtype(0);
		}
		//获取用户所属的机构  
		List<Organ> list = organService.getOrganByPIdAndPType(organ);	 
		return list;// json.toString();
	} 

	/***********************************/
	/************分行立项分行立项分行完成完成逻辑  开始*********/
	/***********************************/

	/**
	 * 分行立项分行立项分行完成完成保存
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateFHItem.do")
	public JsonResult<Item> jsonSaveOrUpdateItem( 
			Item item,
			HttpServletRequest request, HttpServletResponse response) {
		
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(1);
		js.setMessage("保存分行立项分行完成立项失败");
		User loginUser = this.getLoginUser();
		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Organ organ = userOrgByUserId.get(0);
		List<Integer> superVisionOrgId = new ArrayList<Integer>();
		try { 
			if(item != null){
				if(StringUtil.isEmpty(item.getPreparerTimes())){
					js.setMessage("请选择立项时间");
					return js;
				}else{
					item.setPreparerTime(Constants.DATE_FORMAT1.parse(item.getPreparerTimes()));
				}
				if(StringUtil.isEmpty(item.getEndTimes())){
					js.setMessage("请选择规定完成时间");
					return js;
				}else{
					item.setEndTime(Constants.DATE_FORMAT1.parse(item.getEndTimes()));
				}
				if(StringUtil.isEmpty(item.getSupervisionOrgIds())){
					js.setMessage("请选择被监察对象");
					return js;
				}else{
					String[] ids = item.getSupervisionOrgIds().split(",");
					for(String id :ids){
						if(!StringUtil.isEmpty(id)){
							superVisionOrgId.add(Integer.parseInt(id));
						}
					}
					if(superVisionOrgId.size() == 0){
						js.setMessage("请选择被监察对象");
						return js;
					}
				}
				item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
				item.setPid(0); //主任务节点的ID
		    	item.setStageIndex(new Byte("0")); //工作阶段排序   	
		    	item.setSupervisionUserId(0);
		    	item.setPreparerOrgId(organ.getId());
		    	item.setPreparerId(loginUser.getId());
		    	item.setIsStept(0);
		    	item.setStatus(1);
		    	item.setSuperItemType(null); 
		    	item.setContentTypeId(Constants.CONTENT_TYPE_ID_1);
				itemService.saveOrUpdateItemList(superVisionOrgId,item);
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，创建了分行立项分行完成项目"+item.getName(), 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
				js.setCode(0);
				js.setMessage("保存分行立项分行完成立项成功");
			}else{
				js.setMessage("保存分行立项分行完成立项失败,传入对象为空");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			js.setMessage("保存分行立项分行完成立项出现异常");
		}
		return js;
	} 

	/**
	 * 分行立项中支完成保存
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateZZItem.do")
	public JsonResult<Item> jsonSaveOrUpdateZZItem( 
			Item item,
			HttpServletRequest request, HttpServletResponse response) {
		
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(1);
		js.setMessage("保存分行立项中支完成立项失败");
		User loginUser = this.getLoginUser();
		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Organ organ = userOrgByUserId.get(0);
		List<Integer> superVisionOrgId = new ArrayList<Integer>();
		try { 
			if(item != null){
				if(StringUtil.isEmpty(item.getPreparerTimes())){
					js.setMessage("请选择立项时间");
					return js;
				}else{
					item.setPreparerTime(Constants.DATE_FORMAT1.parse(item.getPreparerTimes()));
				}
				if(StringUtil.isEmpty(item.getEndTimes())){
					js.setMessage("请选择规定完成时间");
					return js;
				}else{
					item.setEndTime(Constants.DATE_FORMAT1.parse(item.getEndTimes()));
				}
				if(StringUtil.isEmpty(item.getSupervisionOrgIds())){
					js.setMessage("请选择被监察对象");
					return js;
				}else{
					String[] ids = item.getSupervisionOrgIds().split(",");
					for(String id :ids){
						if(!StringUtil.isEmpty(id)){
							superVisionOrgId.add(Integer.parseInt(id));
						}
					}
					if(superVisionOrgId.size() == 0){
						js.setMessage("请选择被监察对象");
						return js;
					}
				}
				item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
				item.setPid(0); //主任务节点的ID
		    	item.setStageIndex(new Byte("0")); //工作阶段排序   	
		    	item.setSupervisionUserId(0);
		    	item.setPreparerOrgId(organ.getId());
		    	item.setPreparerId(loginUser.getId());
		    	item.setIsStept(0);
		    	item.setStatus(1);
		    	item.setSuperItemType(null); 
		    	item.setContentTypeId(Constants.CONTENT_TYPE_ID_FHZZ);
				itemService.saveOrUpdateItemList(superVisionOrgId,item);
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，创建了分行立项中支完成项目"+item.getName(), 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
				js.setCode(0);
				js.setMessage("保存分行立项中支完成立项成功");
			}else{
				js.setMessage("保存分行立项中支完成立项失败,传入对象为空");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			js.setMessage("保存分行立项中支完成立项出现异常");
		}
		return js;
	} 
	/**
	 * 加载所有项目类型明细
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loadItemInfo.do")
	public BaseItemResult loadItemInfo(   
			HttpServletRequest request, HttpServletResponse response) { 
		BaseItemResult br = new BaseItemResult();
		Item item = new Item();
		List<ItemProcess> processList = new ArrayList<ItemProcess>();
		try { 
	    	Integer sessionItemId =(Integer)request.getSession().getAttribute("FHFHItemId");
	    	if(sessionItemId != null){
	    		item = itemService.selectByPrimaryKey(sessionItemId);
	    		if(item != null){
	    			br.setResultItem(item);
	    		}
	    		processList = itemProcessService.getItemProcessItemId(sessionItemId);
	    		if(processList != null && processList.size()>0){
	    			for(ItemProcess itp: processList){
	    				List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
	    				fileList = itemProcessFileService.getFileListByItemId(itp.getId());
	    				itp.setFileList(fileList);  
	    				if(itp.getContentTypeId() == Constants.CONTENT_TYPE_ID_3){
	    					if(!StringUtil.isEmpty(itp.getContent()) && itp.getContent().contains("&&")){
	    						String[] cts = itp.getContent().split("&&");
	    						if(cts.length >0){
	    							itp.setContent(cts[0]);
								}
								if(cts.length>1){
									itp.setChangeContent(cts[1]);
								}
	    					}
	    				}
	    			}
	    			br.setResultItemProcess(processList);
	    		}
	    	}
		} catch (Exception ex) {
			ex.printStackTrace(); 
		}
		return br;
	}

	/**
	 * 被监察对象上传文件保存
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveFHZZFile.do")
	public JsonResult<ItemProcess> jsonSaveFHZZFile( 
			ItemProcess itemProcess,
			HttpServletRequest request, HttpServletResponse response) { 
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		js.setCode(1);
		js.setMessage("被监察对象上传文件失败");
		User loginUser = this.getLoginUser();
		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Organ organ = userOrgByUserId.get(0);
		try { 
			if(itemProcess != null && itemProcess.getItemId() != null && itemProcess.getItemId()>0){
				Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
				if(item != null){
					itemProcess.setId(0);
					itemProcess.setDefined(false);
					itemProcess.setOrgId(organ.getId());
					itemProcess.setPreparerOrgId(organ.getId());
					itemProcess.setPreparerId(loginUser.getId());
					itemProcess.setPreparerTime(new Date());
					itemProcess.setContentTypeId(Constants.CONTENT_TYPE_ID_FHZZ_OVER);
					itemProcessService.insertSelective(itemProcess);
				 
					if(item.getStatus() == 3){
						item.setStatus(5);
					}else{
						item.setStatus(4);
					} 
					itemService.updateByPrimaryKeySelective(item);
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_LXGL, "被监察对象："+organ.getName()+"上传了 "+item.getName()+" 的监察资料", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(0);
					js.setMessage("被监察对象上传文件成功,当前项目完结");
					
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); 
		}
		return js;
	}
	/**
	 * 分行上传文件保存
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveFHFHFile.do")
	public JsonResult<ItemProcess> jsonSaveFHFHFile( 
			ItemProcess itemProcess,
			HttpServletRequest request, HttpServletResponse response) { 
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		js.setCode(1);
		js.setMessage("被监察对象上传文件失败");
		User loginUser = this.getLoginUser();
		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Organ organ = userOrgByUserId.get(0);
		try { 
			if(itemProcess != null && itemProcess.getItemId() != null && itemProcess.getItemId()>0){
				Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
				if(item != null){
					itemProcess.setId(0);
					itemProcess.setDefined(false);
					itemProcess.setOrgId(organ.getId());
					itemProcess.setPreparerOrgId(organ.getId());
					itemProcess.setPreparerId(loginUser.getId());
					itemProcess.setPreparerTime(new Date());
					itemProcess.setContentTypeId(Constants.CONTENT_TYPE_ID_2);
					itemProcessService.insertSelective(itemProcess); 
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_LXGL, "被监察对象："+organ.getName()+"上传了 "+item.getName()+" 的监察资料", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(0);
					js.setMessage("被监察对象上传文件成功,等待监察室审核监察");
					
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); 
			js.setMessage("被监察对象上传文件失败,保存上传文件发生异常");
		}
		return js;
	}
	/**
	 * 中支上传文件保存
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateOpinion.do")
	public JsonResult<ItemProcess> jsonSaveOrUpdateOpinion( 
			ItemProcess itemProcess,
			HttpServletRequest request, HttpServletResponse response) { 
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		js.setCode(1);
		js.setMessage("监察室给定监察意见失败");
		User loginUser = this.getLoginUser();
		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Organ organ = userOrgByUserId.get(0);
		try { 
			if(itemProcess != null && itemProcess.getItemId() != null && itemProcess.getItemId()>0){
				Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
				if(item != null){
					itemProcess.setId(0);
					itemProcess.setDefined(false);
					itemProcess.setOrgId(organ.getId());
					itemProcess.setPreparerOrgId(organ.getId());
					itemProcess.setPreparerId(loginUser.getId());
					itemProcess.setPreparerTime(new Date());
					if(itemProcess.getIsOver() == Constants.NOT_OVER){
						itemProcess.setContentTypeId(Constants.CONTENT_TYPE_ID_3);
					}else{
						itemProcess.setContentTypeId(Constants.CONTENT_TYPE_ID_7);
						if(item.getStatus() == 3){
							item.setStatus(5);
						}else{
							item.setStatus(4);
						}
						itemService.updateByPrimaryKeySelective(item);
					}
					itemProcessService.insertSelective(itemProcess); 
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_LXGL, "监察室："+organ.getName()+" 对项目  "+item.getName()+" 给定了监察意见", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(0);
					js.setMessage("监察室给定监察意见成功");
					
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); 
			js.setMessage("监察室给定监察意见发生异常");
		}
		return js;
	}
	
	/**
	 * 中支上传文件保存
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateChangeFile.do")
	public JsonResult<ItemProcess> jsonSaveOrUpdateChangeFile( 
			ItemProcess itemProcess,
			HttpServletRequest request, HttpServletResponse response) { 
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		js.setCode(1);
		js.setMessage("上传整改情况失败");
		User loginUser = this.getLoginUser();
		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Organ organ = userOrgByUserId.get(0);
		try { 
			if(itemProcess != null && itemProcess.getItemId() != null && itemProcess.getItemId()>0){
				Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
				if(item != null){
					itemProcess.setId(0);
					itemProcess.setDefined(false);
					itemProcess.setOrgId(organ.getId());
					itemProcess.setPreparerOrgId(organ.getId());
					itemProcess.setPreparerId(loginUser.getId());
					itemProcess.setPreparerTime(new Date());
					itemProcess.setContentTypeId(Constants.CONTENT_TYPE_ID_4); 
					itemProcessService.insertSelective(itemProcess); 
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_LXGL, "被监察对象："+organ.getName()+"上传了 "+item.getName()+" 的整改情况资料", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(0);
					js.setMessage("被监察对象上传文件成功,等待监察室审核监察");
					
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); 
			js.setMessage("被监察对象上传文件失败,保存上传文件发生异常");
		}
		return js;
	}
	
	/**
	 * 删除项目
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteItem.do")
	public JsonResult<Item> deleteItem(  
    		@RequestParam(value="id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(1);
		js.setMessage("删除项目失败"); 
		User loginUser = this.getLoginUser();
		try { 
			 Item item = itemService.selectByPrimaryKey(id);
			 if(item != null){
				item.setStatus(9);
				itemService.updateByPrimaryKeySelective(item);
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，删除分行立项项目"+item.getName(), 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
				js.setCode(0);
				js.setMessage("删除项目成功"); 
			 }
		} catch (Exception ex) {
			ex.printStackTrace();
			js.setMessage("删除项目出现异常");
		}
		return js;
	}  
	

	/***********************************/
	/************分行立项分行立项分行完成完成逻辑 结束*********/
	/***********************************/
}