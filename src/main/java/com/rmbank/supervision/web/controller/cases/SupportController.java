package com.rmbank.supervision.web.controller.cases;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
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
import com.rmbank.supervision.model.GradeScheme;
import com.rmbank.supervision.model.GradeSchemeDetail;
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.ItemProcess;
import com.rmbank.supervision.model.ItemProcessFile;
import com.rmbank.supervision.model.ItemProcessGrade;
import com.rmbank.supervision.model.Meta;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.OrganVM;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ConfigService;
import com.rmbank.supervision.service.GradeSchemeDetailService;
import com.rmbank.supervision.service.GradeSchemeService;
import com.rmbank.supervision.service.ItemProcessFileService;
import com.rmbank.supervision.service.ItemProcessService;
import com.rmbank.supervision.service.ItemService;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;

/**
 * 中支立项Action
 * @author DELL
 *
 */
@Scope("prototype")
@Controller
@RequestMapping("/manage/support")
public class SupportController extends SystemAction {

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
	private SysLogService logService;
	@Resource
	private GradeSchemeService gradeSchemeService;
	@Resource
	private GradeSchemeDetailService gradeSchemeDetailService;
	
	/**
	 * 中支立项列表
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ResponseBody
	@RequestMapping("/supportList.do")
//	@RequiresPermissions("manage/support/supportList.do")
	public DataListResult<Item> supportList(Item item, 
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
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
		
		
		//获取项目分类的集合		
		List<Meta> meatListByKey = configService.getMeatListByKey(Constants.META_PROJECT_KEY);
		request.setAttribute("meatListByKey", meatListByKey);

		//当前登录用户所属的机构
		User loginUser = this.getLoginUser();  
		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Integer logUserOrg = userOrgByUserId.get(0).getId(); //当前登录用户所属的机构ID
		item.setLogOrgId(logUserOrg);
		Organ organ = userOrgByUserId.get(0);
		//获取项目列表,根据不同的机构类型加载不同的项目
		List<Item> itemList =null;
		if(organ.getOrgtype()==Constants.ORG_TYPE_1 ||
				organ.getOrgtype()==Constants.ORG_TYPE_2 ||
				organ.getOrgtype()==Constants.ORG_TYPE_3 ||
						organ.getOrgtype()==Constants.ORG_TYPE_4 ||
				Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
			//成都分行监察室和超级管理员加载所有的中支立项项目
			//itemList=itemService.getItemList(item);
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setOrgTypeA(Constants.ORG_TYPE_7);			
			itemList=itemService.getItemListByOrgType(item);
			totalCount=itemService.getItemCountZZLXALL(item);
		}else{
			//当前登录机构只加载当前登录中支立的项目和子机构完成的项目
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setOrgTypeA(Constants.ORG_TYPE_7);	//立项机构为中支监察室
			item.setOrgTypeB(Constants.ORG_TYPE_8); //完成机构为中支部门
			item.setOrgTypeC(Constants.ORG_TYPE_9); //完成机构为县支行
			item.setSupervisionOrgId(logUserOrg);
			item.setPreparerOrgId(logUserOrg);
			itemList=itemService.getItemListByOrgTypeAndLogOrg(item);
			totalCount=itemService.getItemCountZZLX(item);
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
	 * 中支立项保存
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateItem.do")
	public JsonResult<Item> jsonSaveOrUpdateItem( 
			Item item,
			HttpServletRequest request, HttpServletResponse response) {
		
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(1);
		js.setMessage("保存中支立项失败");
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
				itemService.saveOrUpdateItemList(superVisionOrgId,item);
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，创建了中支立项项目"+item.getName(), 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
				js.setCode(0);
				js.setMessage("保存中支立项成功");
			}else{
				js.setMessage("保存中支立项失败,传入对象为空");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			js.setMessage("保存中支立项出现异常");
		}
		return js;
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
				logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，删除中支立项项目"+item.getName(), 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
				js.setCode(0);
				js.setMessage("删除项目成功"); 
			 }
		} catch (Exception ex) {
			ex.printStackTrace();
			js.setMessage("删除项目出现异常");
		}
		return js;
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
	@RequestMapping(value = "/loadItemInfo.do")
	public BaseItemResult loadItemInfo(   
			HttpServletRequest request, HttpServletResponse response) { 
		BaseItemResult br = new BaseItemResult();
		Item item = new Item();
		List<ItemProcess> processList = new ArrayList<ItemProcess>();
		try { 
	    	Integer sessionItemId =(Integer)request.getSession().getAttribute("ZZZZItemId");
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
	    				if(itp.getContentTypeId().intValue()==Constants.CONTENT_TYPE_ID_ZZZZ_RE_FILE.intValue()){
	    					List<ItemProcessGrade> gsList = gradeSchemeService.getGradeListByItemProcessId(itp.getId());
	    					if(gsList.size()>0){
	    						double valueTypeValue = 0;
	    						for(ItemProcessGrade ipg : gsList){
	    							valueTypeValue = valueTypeValue + ipg.getGrade();
	    						}
	    						itp.setValueTypeValue(valueTypeValue);
	    					}
	    				}
	    				itp.setFileList(fileList);  
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
		 * 加载所有项目类型
		 * 
		 * @param pid
		 * @param request
		 * @param response
		 * @return
		 */
		@ResponseBody
		@RequestMapping(value = "/jsonSaveFile.do")
		public JsonResult<ItemProcess> jsonSaveFile( 
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
						itemProcessService.insertSelective(itemProcess);
						if(itemProcess.getContentTypeId() == Constants.CONTENT_TYPE_ID_ZZZZ_OVER){
							if(item.getStatus() == 3){
								item.setStatus(5);
							}else{
								item.setStatus(4);
							}
							itemService.updateByPrimaryKeySelective(item);
						}
						String ip = IpUtil.getIpAddress(request);		
						logService.writeLog(Constants.LOG_TYPE_LXGL, "被监察对象："+organ.getName()+"上传了 "+item.getName()+" 的监察资料", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
						js.setCode(0);
						js.setMessage("被监察对象上传文件成功");
						
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(); 
			}
			return js;
		}
		 
		

		/** 
		 * 加载所有有效的量化模型
		 * @throws ParseException 
		 */
	    @ResponseBody
	    @RequestMapping(value = "/jsonLoadGradeSchemeList.do", method=RequestMethod.POST)
	    public List<GradeScheme> jsonLoadGradeSchemeList(GradeSchemeDetail gradeSchemeDetail, 
	    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
	    	//新建一个json对象 并赋初值
	    	List<GradeScheme> gsList = new ArrayList<GradeScheme>();
	    	GradeScheme gradeScheme = new GradeScheme();
			gradeScheme.setUsed(1);
			gsList = gradeSchemeService.getGradeSchemeList(gradeScheme);  
			return gsList;
	    }
		/** 
		 * 根据选择的模型，加载量化指标
		 * @throws ParseException 
		 */
	    @ResponseBody
	    @RequestMapping(value = "/jsonLoadGradeSchemeDetail.do", method=RequestMethod.POST)
	    public List<GradeSchemeDetail> jsonLoadGradeSchemeDetail(GradeSchemeDetail gradeSchemeDetail, 
	    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
	    	//新建一个json对象 并赋初值
	    	List<GradeSchemeDetail> templist = new ArrayList<GradeSchemeDetail>();
	    	List<GradeSchemeDetail> list = new ArrayList<GradeSchemeDetail>();
	    	templist = gradeSchemeDetailService.getGradeSchemeDetailListByGradeId(gradeSchemeDetail);
	    	if(templist.size()>0){
	    		for(GradeSchemeDetail gsd : templist){
	    			if(gsd.getLevel() == 0){
	    				List<GradeSchemeDetail> child = new ArrayList<GradeSchemeDetail>();
		    			for(GradeSchemeDetail a : templist){ 
		    				if(a.getPid()==gsd.getId() && a.getLevel() == 1){
		    					List<GradeSchemeDetail> subchild = new ArrayList<GradeSchemeDetail>();
		    					for(GradeSchemeDetail b : templist){
		    						if(a.getId()==b.getPid() && b.getLevel() == 2){
		    							subchild.add(b); 
		    	    				}
		    					}
		    					a.setChildren(subchild);
		    					child.add(a);
		    				}
		    			}
		    			gsd.setChildren(child);
		    			list.add(gsd);
	    			}
	    		}
	    		
	    	}
	    	
			return list;
	    }
	    

		/** 
		 * 量化项目
		 * @throws ParseException 
		 */
	    @ResponseBody
	    @RequestMapping(value = "/jsonSaveOrUpdateItemValue.do", method=RequestMethod.POST)
	    public JsonResult<ItemProcess> jsonSaveOrUpdateItemValue(ItemProcess itemProcess, 
	    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
	    	//新建一个json对象 并赋初值
			JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
	    	//获取当前登录用户
			js.setCode(new Integer(1));
			js.setMessage("监察室量化项目失败!");
			User loginUser = this.getLoginUser();
			List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
			Organ organ = userOrgByUserId.get(0);
			try {   
		    	itemProcess.setPreparerOrgId(organ.getId()); //制单部门的ID
		    	itemProcess.setOrgId(organ.getId()); //制单部门的ID
		    	itemProcess.setPreparerId(loginUser.getId());
		    	itemProcess.setPreparerTime(new Date());
		    	itemProcess.setContentTypeId(Constants.CONTENT_TYPE_ID_ZZZZ_RE_FILE);
		    	UUID uid = UUID.randomUUID();
		    	String uuid = uid.toString().replace("-", "");
		    	itemProcess.setUuid(uuid); 
				itemProcess.setDefined(false); 
				itemProcessService.insert(itemProcess);
				if(itemProcess.getIsValue() == Constants.IS_VALUE){
					List<ItemProcessGrade> gradeList = new ArrayList<ItemProcessGrade>();
					String[] values = itemProcess.getValues().split(",");
					String[] detailIds = itemProcess.getDetailId().split(",");
					for(int i = 0; i<values.length;i++){ 
						 ItemProcessGrade grade = new ItemProcessGrade();
						 grade.setId(0); 
						 grade.setGradeSchemeId(itemProcess.getGradeSchemeId());
						 grade.setItemProcessId(itemProcess.getId());
						 grade.setGrade(Double.valueOf(values[i]));
						 grade.setGradeDetailId(Integer.parseInt(detailIds[i]));
						 gradeList.add(grade); 
					 }
					 gradeSchemeService.insertGradeList(gradeList);
					js.setCode(0);
					js.setMessage("监察室量化项目成功，等待中支继续上传资料"); 
				} else{ 
					js.setCode(0);
					js.setMessage("项目未量化，等待中支继续上传资料"); 
				}
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_LXGL, "中支监察室："+organ.getName()+" 对项目进行了量化 ", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
				
			}catch(Exception ex){
				js.setMessage("监察室量化项目成功发生异常!");
				ex.printStackTrace();
			}
			return js;
	    }
	    
} 
