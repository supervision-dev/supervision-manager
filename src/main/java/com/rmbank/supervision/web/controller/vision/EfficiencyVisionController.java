package com.rmbank.supervision.web.controller.vision;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.ItemProcess;
import com.rmbank.supervision.model.ItemProcessFile;
import com.rmbank.supervision.model.Meta;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.OrganVM;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ConfigService;
import com.rmbank.supervision.service.ItemProcessFileService;
import com.rmbank.supervision.service.ItemProcessService;
import com.rmbank.supervision.service.ItemService;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.service.UserRoleService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;

/**
 * 效能监察控制器
 * @author DELL
 *
 */ 

@Scope("prototype")
@Controller
@RequestMapping("/vision/efficiency")
public class EfficiencyVisionController extends SystemAction {
	
	@Resource
	private ItemService itemService;
	@Resource
	private UserService userService;
	@Resource
	private OrganService organService;
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private ItemProcessService itemProcessService;
	@Resource
	private ItemProcessFileService itemProcessFileService;
	@Resource
	private SysLogService logService;
	private HttpSession session;
	
	/**
     * 效能监察列表展示
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
	@ResponseBody
    @RequestMapping(value = "/efficiencyList.do")
//  @RequiresPermissions("vision/efficiency/efficiencyList.do")
    public DataListResult<Item> efficiencyList(Item item, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
    	
    	DataListResult<Item> dr = new DataListResult<Item>();
    	
    	// 判断搜索名是否为空，不为空则转为utf-8编码
		if (item.getSearchName() != null && item.getSearchName() != "") {
			String searchName = URLDecoder.decode(item.getSearchName(),"utf-8");
			item.setSearchName(searchName);
		}
		// 设置页面初始值及页面大小
		if (item.getPageNo() == null)
			item.setPageNo(1);
		item.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		int totalCount = 0;
		
		//获取当前登录用户
		User loginUser = this.getLoginUser();
		//获取当前用户对应的机构列表
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		//获取当前用户对应的第一个机构
		Organ userOrg=userOrgList.get(0);
		
		//获取当前用户对应的角色
		List<Role> rolesByUserId = userRoleService.getRolesByUserId(loginUser.getId());
		Role userRole = rolesByUserId.get(0);
		
		// 分页集合
		List<Item> itemList = new ArrayList<Item>();
		try {
			//成都分行和超级管理员获取所有项目
			if(userOrg.getOrgtype()==Constants.ORG_TYPE_1 ||
					userOrg.getOrgtype()==Constants.ORG_TYPE_2 ||
							userOrg.getOrgtype()==Constants.ORG_TYPE_3 ||
									userOrg.getOrgtype()==Constants.ORG_TYPE_4 ||
					Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
				
				item.setSupervisionTypeId(2); //2代表效能监察
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListByType(item);			
				totalCount = itemService.getItemCountBySSJC(item); //实时监察分页
			}else {
				//当前登录用户只加载自己完成的项目,如果是中支监察室还需要加载自己录入的和当前中支办公室录入的
				//如果是中支监察室，需要获取到和当前中支监察在同一个中支下的办公室的
				List<Item> BGSitemList = new ArrayList<Item>();
				int BGStotalCount = 0;
				if(userOrg.getOrgtype()== Constants.ORG_TYPE_7){
					Organ BGS = organService.getOrganByPidAndName(userOrg.getPid(), "办公室");
					item.setSupervisionTypeId(2); //2代表效能监察
					item.setSupervisionOrgId(BGS.getId());
					item.setPreparerOrgId(BGS.getId());
					item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
					BGSitemList = itemService.getItemListByTypeAndLogOrg(item);
					BGStotalCount = itemService.getItemCountByLogOrgSSJC(item); //实时监察分页
				}
				item.setSupervisionTypeId(2); //2代表效能监察
				item.setSupervisionOrgId(userOrg.getId());
				item.setPreparerOrgId(userOrg.getId());
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListByTypeAndLogOrg(item);
				itemList.addAll(BGSitemList);
				// 取满足要求的记录总数
				totalCount = itemService.getItemCountByLogOrgSSJC(item); //实时监察分页
				totalCount +=BGStotalCount;
			}
			
			
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			for (Item it : itemList) {
				//格式化时间
				Date endTime = it.getEndTime();
				if(endTime!=null){
					String format = formatter.format(endTime);
					it.setShowDate(format);
				}
				
				//设置流程节点
				List<ItemProcess> itemprocessList = itemProcessService.getItemProcessItemId(it.getId());
				if(itemprocessList.size()>0){
					ItemProcess lastItem = itemprocessList.get(itemprocessList.size()-1);
					it.setLasgTag(lastItem.getContentTypeId());
				}
				
				//将登录机构的类型添加到项目中
				it.setOrgType(userOrg.getOrgtype());
				
				//将登陆用户的角色id添加到项目中
				it.setUserRole(userRole.getId());
				
				//将登陆用户的机构Id添加
				it.setLogOrgId(userOrg.getId());
				
				//获取添加项目的机构类型和登录机构类型是否相同 
				Organ itemOrg = organService.selectByPrimaryKey(it.getPreparerOrgId());
				if(itemOrg.getOrgtype()==userOrg.getOrgtype()){
					it.setIsItemOrg("true");
				}else {
					it.setIsItemOrg("false");
				}
				
				//解决办公室录入项目，监察室立项问题
				
				if(itemOrg.getOrgtype() == Constants.ORG_TYPE_2 && userRole.getId()==3 && userOrg.getOrgtype()== Constants.ORG_TYPE_4){
					//如果录入项目的机构类型是分行办公室，用户角色是分行监察角色并且当前登录机构也是分行监察室
					it.setIsProject("true");
				}else if(itemOrg.getOrgtype() == Constants.ORG_TYPE_10 && userRole.getId()==4 && userOrg.getOrgtype()== Constants.ORG_TYPE_7){
					//如果录入项目的机构类型是中支办公室，用户角色是中支监察角色并且当前登录机构也是中支监察室
					it.setIsProject("true");
				}else{
					it.setIsProject("false");
				}
			}
	    	
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了效能监察列表查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
	    	
			item.setTotalCount(totalCount);
			item.setOrgType(userOrg.getOrgtype());
			dr.setData(item);
			dr.setDatalist(itemList); 
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
    	return dr;
    }
    
	
	
	
	
	/**
	 * 加载责任领导
	 */
	@ResponseBody
    @RequestMapping(value = "/getLeader.do")
//  @RequiresPermissions("vision/efficiency/getLeader.do")
	 public DataListResult<User> getLeader(
			 HttpServletRequest request, HttpServletResponse response){
		DataListResult<User> dr = new DataListResult<User>();
		 
		//获取当前登录用户
		User lgUser = this.getLoginUser();
		//获取当前用户对应的机构列表
		List<Organ> userOrgList=userService.getUserOrgByUserId(lgUser.getId());
		Organ organ2 = userOrgList.get(0);
		Integer orgtype = organ2.getOrgtype();
		List<User> userListByOrgId =null;
		//如果是中支监察室，加载中支领导
		if(orgtype == Constants.ORG_TYPE_7){			
			Integer pid = organ2.getPid();
			String OrgName="行领导";
			//获取当前中支的行领导机构下的用户
			Organ HORG=organService.getOrganByPidAndName(pid,OrgName);
			userListByOrgId = userService.getUserListByOrgId(HORG.getId());
		}else if (orgtype == Constants.ORG_TYPE_4) {
			//分行监察室加载分行领导
			userListByOrgId = userService.getUserListByOrgId(1);
		}
		dr.setData(lgUser);
		dr.setDatalist(userListByOrgId); 
		return dr;
	 }
	
    /** 
   	 * 新增项目
   	 * @throws ParseException 
   	 */
   @ResponseBody
   @RequestMapping(value = "/jsonSaveOrUpdateItem.do", method=RequestMethod.POST)
   @RequiresPermissions("vision/efficiency/jsonSaveOrUpdateItem.do")
   public JsonResult<Item> jsonSaveOrUpdateItem(Item item,
   		@RequestParam(value = "end_time", required = false) String end_time,//用于接收前台传过来的String类型的时间
   		@RequestParam(value = "content", required = false) String content,
   		@RequestParam(value = "OrgId", required = false) Integer[] OrgIds,    		
   		HttpServletRequest request, HttpServletResponse response) throws ParseException{
	  
	    
		//将前台传过来的String类型的时间转换为Date类型
		if (end_time != null) {
		   	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		   	Date date = sdf.parse(end_time);    	
		   	item.setEndTime(date); //完成时间
		}
	   	item.setPreparerTime(new Date());
	   	item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //项目类型
	   	item.setSupervisionTypeId(2);
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
					logService.writeLog(Constants.LOG_TYPE_XLJC, "用户："+loginUser.getName()+"，添加了效能监察的工作事项", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
					
					js.setCode(new Integer(0));
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
     * 监察室进行立项
     */
    @ResponseBody
	@RequestMapping(value = "/jsonsetProjectById.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/efficiency/jsonsetProjectById.do")
	public JsonResult<Item> jsonsetProjectById(Item item,
			@RequestParam(value = "end_time", required = false) String end_time,//用于接收前台传过来的String类型的时间
	   		@RequestParam(value = "content", required = false) String content,
	   		@RequestParam(value = "OrgId", required = false) Integer[] OrgIds,    		
			HttpServletRequest request, HttpServletResponse response) {
		
    	// 新建一个json对象 并赋初值
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("立项失败!");
		
		HttpSession session = request.getSession();
    	Integer sessionItemId =(Integer) session.getAttribute("efficiencyItemId");
    	
		
		// 获取当前登录用户
		User u = this.getLoginUser();
		//获取要立项的项目
		Item item2 = itemService.selectByPrimaryKey(sessionItemId);
		try {
			// 将前台传过来的String类型的时间转换为Date类型
			if (end_time != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(end_time);
				item2.setEndTime(date); // 完成时间			
				item2.setStatus(1);
				item2.setUuid(item.getUuid());
				item2.setIsStept(item.getIsStept());
			}

			//首先删除当前未立项的这条项目
			boolean states = itemService.deleteItemById(item2.getId());
			//获取当前项目的流程，有且只有一个
			List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item2.getId()); 
			ItemProcess getItemProcess = itemProcessList.get(0);
			String uuid = getItemProcess.getUuid();
			//获取初始化流程的附件集合
			List<ItemProcessFile> fileList = itemProcessFileService.getFileListByItemId(itemProcessList.get(0).getId());
			List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());	
			//立项返回的项目ID
			List<Integer> itemIds= new ArrayList<Integer>();
			for (Integer orgId : OrgIds) {
				getItemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_0);
				getItemProcess.setContent(null);
				getItemProcess.setUuid(uuid);
				item2.setId(0);
				item2.setSupervisionOrgId(orgId);
				itemService.insertSelective(item2);//根据机构数对项目进行立项
				Integer itemId = item2.getId(); //立项返回的ID
				itemIds.add(itemId);
				
				getItemProcess.setId(0);
				getItemProcess.setItemId(itemId);	
				getItemProcess.setDefined(false);
				getItemProcess.setOrgId(userOrgIDs.get(0));
				//getItemProcess.setContent(content);
				itemProcessService.insert(getItemProcess);//将项目的初始化流程赋给立项的项目				
				Integer itemProcessId = getItemProcess.getId(); //返回的id
				
				for (ItemProcessFile itemProcessFile : fileList) {
					itemProcessFile.setId(0);
					itemProcessFile.setItemProcessId(itemProcessId);
					itemProcessFileService.insertSelective(itemProcessFile);//将初始化的附件赋给立项项目
				}
								
				//保存立项时监察项目信息
				ItemProcess  ItemInformation= getItemProcess;
				ItemInformation.setId(0);
				ItemInformation.setUuid(item.getUuid());
				ItemInformation.setContentTypeId(Constants.EFFICIENCY_VISION_00);
				ItemInformation.setContent(item.getName());
				itemProcessService.insert(ItemInformation);//监察项目的信息
				
				//保存立项监察内容信息	
				ItemInformation.setId(0);
				ItemInformation.setContent(content);
				ItemInformation.setContentTypeId(Constants.EFFICIENCY_VISION_01);
				itemProcessService.insert(ItemInformation);//监察内容的信息
				
				ItemInformation = new ItemProcess(); //清空
			}
			
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_ZFJC, "用户："+loginUser.getName()+"，对执法监察的项目进行了立项", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
			
			js.setCode(new Integer(0));
			js.setMessage("保存项目信息成功!");
			return js;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}
		return js;
	}
    
    /**
     * 被监察对象签收项目
     */
     @ResponseBody
 	@RequestMapping(value = "/jsonSignItemById.do", method = RequestMethod.POST)
 	@RequiresPermissions("vision/efficiency/jsonSignItemById.do")
 	public JsonResult<Item> jsonSignItemById(
 			@RequestParam(value = "itemId", required = false) Integer itemId,
 			HttpServletRequest request, HttpServletResponse response) {
 		
 		// 新建一个json对象 并赋初值
 		JsonResult<Item> js = new JsonResult<Item>();
 		js.setCode(new Integer(1));
 		js.setMessage("签收项目失败!");			
 		try {
 			Item item=new Item();
 			item.setId(itemId);
 			item.setLasgTag(Constants.EFFICIENCY_VISION_1);//进行到签收状态

 			boolean state = itemProcessService.insertItemProcessByItemId(itemId);
 			if(state==true){
 				User loginUser = this.getLoginUser();
 				String ip = IpUtil.getIpAddress(request);		
 				logService.writeLog(Constants.LOG_TYPE_XLJC, "用户："+loginUser.getName()+"，签收了效能监察的工作事项", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
 				js.setCode(new Integer(0));
 				js.setMessage("签收项目成功!");
 				return js;
 			}else {
 				return js;
 			}
 		} catch (Exception e) {
 			e.printStackTrace();
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
	@RequiresPermissions("vision/efficiency/jsondeleteItemById.do")
	public JsonResult<Item> jsondeleteItemById(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		// 新建一个json对象 并赋初值
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("删除失败!");
		boolean state =false;
		try {			
			state= itemService.deleteItemById(id);
			if(state){
				User loginUser = this.getLoginUser();
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_XLJC, "用户："+loginUser.getName()+"，删除了效能监察的工作事项", 3, loginUser.getId(), loginUser.getUserOrgID(), ip);
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
    
    
    
    /**
     * 上传资料
     */
    @ResponseBody
    @RequestMapping(value = "/jsonSaveOrUpdateItemProcess.do", method=RequestMethod.POST)
    @RequiresPermissions("vision/efficiency/jsonSaveOrUpdateItemProcess.do")
    public JsonResult<ItemProcess> jsonSaveOrUpdateFileItem(ItemProcess itemProcess, 
    		@RequestParam(value="contentID",required=false) Integer contentID,
    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
    	
    	HttpSession session = request.getSession();
    	Integer sessionItemId =(Integer) session.getAttribute("efficiencyItemId");
    	
    	
    	//新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
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
			itemProcess.setItemId(sessionItemId);
			Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
			if(contentID!=null && contentID==72){
				itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_12);//未完结时，上传的资料
			}else{
				itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_2);//被监察对象上传资料
			}			
			itemProcessService.insert(itemProcess);		
			
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_XLJC, "用户："+loginUser.getName()+"，上传了效能监察的资料", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
			
			js.setCode(0);
			js.setMessage("上传资料成功!"); 
		}catch(Exception ex){
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
    }
    
   
    
    
    /**
     * 被监察对象整改
     */
    @ResponseBody
    @RequestMapping(value = "/jsonSaveResetItem.do", method=RequestMethod.POST)
    @RequiresPermissions("vision/efficiency/jsonSaveResetItem.do")
    public JsonResult<ItemProcess> jsonSaveResetItem(ItemProcess itemProcess, 
    		@RequestParam(value="status",required = false) Integer status,
    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
    	
    	HttpSession session = request.getSession();
    	Integer sessionItemId =(Integer) session.getAttribute("efficiencyItemId");
    	
    	
    	//新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
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
			itemProcess.setItemId(sessionItemId);
			if(status!=null){
				if(status==null ||status!=4){//监察对象给出监察意见
					itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_666);				
					Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
					item.setLasgTag(Constants.EFFICIENCY_VISION_4);
				}else{
					itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_6);//流程完结
					Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
					item.setEndTime(new Date());
					item.setStatus(Constants.ITEM_STATUS_OVER);
					itemService.updateByPrimaryKeySelective(item);
				}
			}
			if(status==null){//被监察对象整改操作
				itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_666);				
				//Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
				//item.setLasgTag(Constants.EFFICIENCY_VISION_4);
			}
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_XLJC, "用户："+loginUser.getName()+"，执行了效能监察流程操作", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
			itemProcessService.insert(itemProcess);	
			
			js.setCode(0);
			js.setMessage("上传资料成功!"); 
		}catch(Exception ex){
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
    }
    
    /**
     * 给出监察建议
     * @param itemProcess
     * @param status
     * @param request
     * @param response
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @RequestMapping(value = "/jsonSaveOpinion", method=RequestMethod.POST)
    @RequiresPermissions("vision/efficiency/jsonSaveOpinion.do")
    public JsonResult<ItemProcess> jsonSaveOpinion(ItemProcess itemProcess, 
    		@RequestParam(value="status",required = false) Integer status,
    		@RequestParam(value="wanjie",required = false) Integer wanjie,    		
    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
    	
    	HttpSession session = request.getSession();
    	Integer sessionItemId =(Integer) session.getAttribute("efficiencyItemId");
    	itemProcess.setItemId(sessionItemId);
    	
    	//新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
    	//获取当前登录用户
    	User u = this.getLoginUser(); 
		js.setCode(new Integer(1));
		js.setMessage("保存信息失败!");
		try {   
			Item item = itemService.selectByPrimaryKey(sessionItemId);
			if(wanjie==null){
				if(status == null){
					itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_6); //监察室给出监察结论，项目完结
					
					if(item != null){
						if(item.getStatus() == 3){
							item.setStatus(5);
						}else{
							item.setStatus(4);
						}
						itemService.updateByPrimaryKeySelective(item);
					}	
					
					
				}else if(status == 4){
					itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_3);//监察意见为不需要整改，进入监察室录入监察结论
				}else if(status == 0){			
					itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_4); //监察意见为需要整改，进入被监察对象录入整改情况
				}	
			}else{
				if(wanjie==1){//完结
					if(status == 4){
						itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_33);//监察意见为不需要整改，进入监察室录入监察结论
					}else if(status == 0){			
						itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_4); //监察意见为需要整改，进入被监察对象录入整改情况
					}
				}else if(wanjie==0){//未完结
					itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_11); //项目未完结，继续录入资料
				}
			}
					
	    	//获取当前用户所属的机构id，当做制单部门的ID
	    	List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
	    	itemProcess.setPreparerOrgId(userOrgIDs.get(0)); //制单部门的ID
	    	itemProcess.setOrgId(userOrgIDs.get(0)); //制单部门的ID
	    	itemProcess.setPreparerId(u.getId());
	    	itemProcess.setPreparerTime(new Date());
			itemProcess.setDefined(false); 
			itemProcess.setItemId(sessionItemId);
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_XLJC, "用户："+loginUser.getName()+"，执行了效能监察流程操作", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
			itemProcessService.insert(itemProcess);			
			
			js.setCode(0);
			js.setMessage("保存信息成功!"); 
		}catch(Exception ex){
			js.setMessage("保存信息失败!");
			ex.printStackTrace();
		}
		return js;
    }
    
    
   /**
    * 查看项目
    * @param item
    * @param request
    * @param response
    * @return
    */
    @ResponseBody
    @RequestMapping(value = "/showItem.do")
    @RequiresPermissions("vision/efficiency/showItem.do")
    public BaseItemResult showItem( 
            HttpServletRequest request, HttpServletResponse response){
    	
    	HttpSession session = request.getSession();
    	Integer itemId = (Integer) session.getAttribute("effshowItemId");
    	
    	if(itemId==null){
    		itemId = (Integer) session.getAttribute("efficiencyItemId");
    	}
    	
    	BaseItemResult showResult = new BaseItemResult();
    	List<ItemProcess> drIPList=new ArrayList<ItemProcess>();
    	
    	Item item =new Item();
    	item = itemService.selectByPrimaryKey(itemId);
    	
		if(item.getEndTime() != null){
			item.setEndTimes(Constants.DATE_FORMAT1.format(item.getEndTime()));
		}
		List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item.getId());  
		if(itemProcessList.size()>0){ 
			List<ItemProcess> ipList=new ArrayList<ItemProcess>(); //分节点上传的资料
			List<ItemProcess> ipYJList=new ArrayList<ItemProcess>(); //监察分节点上传的资料的意见
			for(ItemProcess ip : itemProcessList){  
				List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
				fileList = itemProcessFileService.getFileListByItemId(ip.getId());
				ip.setFileList(fileList);  
				//格式化录入时间
				if(ip.getPreparerTime() != null){
					ip.setPreparerTimes(Constants.DATE_FORMAT.format(ip.getPreparerTime()));
				}
				
				if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_0){
					//request.setAttribute("ItemProcess", ip); //监察内容
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_01){					
					//request.setAttribute("ItemProcess2", ip); //已经立项，立项项目
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_00){					
					//request.setAttribute("ItemProcess2", ip); //已经立项，立项内容
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_1){					
					//request.setAttribute("ItemProcess2", ip); //已签收状态
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_2){					
					//request.setAttribute("ItemProcess2", ip); //已经上传资料
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_3){					
					//request.setAttribute("ItemProcess3", ip); //监察意见
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_4){
					//request.setAttribute("ItemProcess4", ip); //监察室已经给出意见，需要整改，进入到被监察对象录入整改情况
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==666){
					//request.setAttribute("ItemProcess5", ip); //被监察对象录入整改情况
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==688){
					//request.setAttribute("ItemProcess10", ip); //不问责，进入监察室给出结论
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==777){
					//request.setAttribute("ItemProcess6", ip); //问责
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==778){
					request.setAttribute("ItemProcess7", ip); //再次上传整改情况
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_6){
					request.setAttribute("ItemProcess8", ip); //给出监察结论项目完结
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==779){ 
					//request.setAttribute("ItemProcess9", ip); //已经录入问责资料
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==800){ 
					//request.setAttribute("ItemProcess9", ip); //不问责
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==999){ 				
					//ipList.add(ip);					//分节点上传资料
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==888){ 				
					//ipList.add(ip);					//监察分节点上传资料的意见为完结
					drIPList.add(ip);
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_11){					
					//ipYJList.add(ip);			  //监察分节点上传资料的意见
					drIPList.add(ip);
				}
			}
			if(ipList.size()>0){
				request.setAttribute("ipList", ipList);
			}
			if(ipYJList.size()>0){
				request.setAttribute("ipYJList", ipYJList);
			}
		}
		
		//获取当前用户
		User lgUser=this.getLoginUser(); 
		
		request.setAttribute("User", lgUser);  
		request.setAttribute("Item", item); 
		request.setAttribute("ContentTypeId", Constants.CONTENT_TYPE_ID_ZZZZ_OVER);
		
		User loginUser = this.getLoginUser();
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，查看了效能监察的项目", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		
		showResult.setResultItem(item);
		showResult.setResultItemProcess(drIPList); 
    	return showResult;
    }
    
    
    
    /**
     * 问责处理
     * @param itemProcess
     * @param id
     * @param isFollow
     * @param request
     * @param response
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @RequestMapping(value = "/jsonfollowItemById.do", method=RequestMethod.POST)
    @RequiresPermissions("vision/efficiency/jsonfollowItemById.do")
    public JsonResult<ItemProcess> jsonfollowItemById(ItemProcess itemProcess,
    		@RequestParam(value="isFollow" ,required=false) Integer isFollow,
    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
    	
    	HttpSession session = request.getSession();
    	Integer sessionItemId =(Integer) session.getAttribute("efficiencyItemId");
    	
    	
    	//新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
    	//获取当前登录用户
    	User u = this.getLoginUser(); 
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");
		try {   
	    	//获取当前用户所属的机构id，当做制单部门的ID
	    	List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
	    	itemProcess.setId(0);
	    	itemProcess.setPreparerOrgId(userOrgIDs.get(0)); //制单部门的ID
	    	itemProcess.setOrgId(userOrgIDs.get(0)); //制单部门的ID
	    	itemProcess.setPreparerId(u.getId());
	    	itemProcess.setPreparerTime(new Date());
			itemProcess.setDefined(false); 
			itemProcess.setItemId(sessionItemId);
			String temp=itemProcess.getContent();
			String tempUuid = itemProcess.getUuid();
			if(isFollow == 1){
				itemProcess.setContent("不问责");
				itemProcess.setUuid(null);
				itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_13);//不问责
				itemProcessService.insert(itemProcess);
				
				itemProcess.setId(0);
				itemProcess.setContent(temp);
				itemProcess.setUuid(tempUuid);
				itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_6); //监察室给出监察结论，项目完结
				itemProcessService.insert(itemProcess);
				
				Item item = itemService.selectByPrimaryKey(sessionItemId);
				if(item != null){
					if(item.getStatus() == 3){
						item.setStatus(5);
					}else{
						item.setStatus(4);
					}
					itemService.updateByPrimaryKeySelective(item);
				}	
				
				js.setCode(0);
				js.setMessage("监察室录入监察结论成功!"); 
			}else if(isFollow == 0){
				itemProcess.setContent("问责");
				itemProcess.setUuid(null);
				itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_7);//进入监察室录入问责资料
				itemProcessService.insert(itemProcess);
				
				itemProcess.setId(0);
				itemProcess.setContent(temp);
				itemProcess.setUuid(tempUuid);
				itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_9);//监察室录入问责资料
				itemProcessService.insert(itemProcess);
				js.setCode(0);
				js.setMessage("监察室录入了问责资料!"); 
			}	
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_XLJC, "用户："+loginUser.getName()+"，对效能监察项目进行问责操作", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
						
			
		}catch(Exception ex){
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
    }
    
   
    
    
    /**
     * 再次整改
     */
    @ResponseBody
    @RequestMapping(value = "/jsonSaveZCZGItem.do", method=RequestMethod.POST)
    @RequiresPermissions("vision/efficiency/jsonSaveZCZGItem.do")
    public JsonResult<ItemProcess> jsonSaveZCZGItem(ItemProcess itemProcess, 
    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
    	
    	HttpSession session = request.getSession();
    	Integer sessionItemId =(Integer) session.getAttribute("efficiencyItemId");
    	
    	
    	//新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
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
			itemProcess.setItemId(sessionItemId);
			itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_8); //再次录入整改情况后进入监察室录入监察结论
			
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_XLJC, "用户："+loginUser.getName()+"，对效能监察项目进行整改操作", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
			itemProcessService.insert(itemProcess);	
			
			js.setCode(0);
			js.setMessage("上传资料成功!"); 
		}catch(Exception ex){
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
    }

    /*
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */
    
	/**
	 * 加载机构的树
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loadOrganTreeList.do")
	public List<Organ> getOrganList(
			@RequestParam(value = "pid", required = false) Integer pid,
			HttpServletRequest request, HttpServletResponse response) {
		
		Organ organ = new Organ();
		if (pid != null && pid>0) {
			organ.setPid(pid);
		} else {
			//获取当前登录用户
			User loginUser = this.getLoginUser();
			//获取当前用户对应的机构列表
			List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
			//获取当前用户对应的第一个机构
			Organ userOrg=userOrgList.get(0);
			if(userOrg.getOrgtype() == Constants.ORG_TYPE_4){
				organ.setPid(0);
			}else{
				organ.setPid(userOrg.getPid());
			}
		} 
		//获取用户所属的机构  
		List<Organ> list = organService.getOrganByPId(organ);	 
		return list;// json.toString();
	}  
    
}
