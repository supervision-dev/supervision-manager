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
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.ItemProcess;
import com.rmbank.supervision.model.ItemProcessFile;
import com.rmbank.supervision.model.Meta;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.OrganVM;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ConfigService;
import com.rmbank.supervision.service.ItemProcessFileService;
import com.rmbank.supervision.service.ItemProcessService;
import com.rmbank.supervision.service.ItemService;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.SysLogService;
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
public class EfficiencyVisionAction extends SystemAction {
	
	@Resource
	private ItemService itemService;
	@Resource
	private UserService userService;
	@Resource
	private OrganService organService;
	@Resource
	private ItemProcessService itemProcessService;
	@Resource
	private ItemProcessFileService itemProcessFileService;
	@Resource
	private SysLogService logService;
	
	/**
     * 效能监察列表展示
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/efficiencyList.do")
    @RequiresPermissions("vision/efficiency/efficiencyList.do")
    public String efficiencyList(Item item, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
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
		
		// 分页集合
		List<Item> itemList = new ArrayList<Item>();
		try {
			//成都分行和超级管理员获取所有项目
			if(userOrg.getOrgtype()==Constants.ORG_TYPE_1 ||
					userOrg.getOrgtype()==Constants.ORG_TYPE_2 ||
							userOrg.getOrgtype()==Constants.ORG_TYPE_3 ||
					Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
				
				item.setSupervisionTypeId(2); //2代表效能监察
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListByType(item);			
				totalCount = itemService.getItemCountBySSJC(item); //实时监察分页
			}else {
				//当前登录用户只加载自己完成的项目如果是中支监察室还需要加载自己立项的
				item.setSupervisionTypeId(2); //2代表效能监察
				item.setSupervisionOrgId(userOrg.getId());
				item.setPreparerOrgId(userOrg.getId());
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListByTypeAndLogOrg(item);
				// 取满足要求的记录总数
				totalCount = itemService.getItemCountByLogOrgSSJC(item); //实时监察分页
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		for (Item it : itemList) {
			Date endTime = it.getEndTime();
			if(endTime!=null){
				String format = formatter.format(endTime);
				it.setShowDate(format);
			}
			
			List<ItemProcess> itemprocessList = itemProcessService.getItemProcessItemId(it.getId());
			if(itemprocessList.size()>0){
				ItemProcess lastItem = itemprocessList.get(itemprocessList.size()-1);
				it.setLasgTag(lastItem.getContentTypeId());
			}
		}
		// 通过request对象传值到前台
		item.setTotalCount(totalCount);
		request.setAttribute("Item", item);
		request.setAttribute("userOrg", userOrg);
		request.setAttribute("itemList", itemList);

    	
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了效能监察列表查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
    	return "web/vision/efficiency/efficiencyList";
    }
    
    /**
     * 跳转到添加工作事项
     * @param id
     * @param req
     * @param res
     * @return
     */
    @RequestMapping(value = "/efficiencyInfo.do")
	@RequiresPermissions("vision/efficiency/efficiencyInfo.do")
	public String efficiencyInfo(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {

    	//获取机构
		Organ organ=new Organ();
		List<Organ> organList = organService.getOrganList(organ);		
    	List<OrganVM> list=new ArrayList<OrganVM>();
		OrganVM frvm = null;
		for(Organ rc : organList){
			if(rc.getPid()==0 && rc.getSupervision()==0){
				frvm = new OrganVM();
				List<Organ> itemList = new ArrayList<Organ>();//用于当做OrganVM的itemList
				frvm.setId(rc.getId());
				frvm.setName(rc.getName());
				String path =rc.getId()+".";//当前登录机构的子机构的path都以此开头
				String substring=null;
				for(Organ rc1 : organList){
					if(rc1.getPath().length()>path.length()){
						substring=rc1.getPath().substring(0, path.length());
					}
					if(rc1.getPid() == rc.getId() && rc1.getSupervision()==0){ 
						itemList.add(rc1);
					}else if(rc1.getId()==43 && rc1.getPid() == rc.getId()){
						itemList.add(rc1);
					}else if(path.equals(substring) && rc1.getSupervision()==0){//添加孙子级节点	
						itemList.add(rc1);								
					}
				}
				frvm.setItemList(itemList);
				list.add(frvm);
			}
		}
		
		//获取当前登录用户所属机构下的所有用户
		User lgUser = this.getLoginUser();
		//获取当前用户对应的机构列表
		List<Organ> userOrgList=userService.getUserOrgByUserId(lgUser.getId());
		Organ organ2 = userOrgList.get(0);
		Integer orgtype = organ2.getOrgtype();
		List<User> userListByOrgId =null;
		//如果是中支监察室，加载中支领导
		if(orgtype == 43){
			userListByOrgId = userService.getUserListByOrgId(113);
		}else if (orgtype == 45 || orgtype == 46 || orgtype == 47) {
			userListByOrgId = userService.getUserListByOrgId(112);
		}
		//request.setAttribute("userOrg", organ2);
		request.setAttribute("byLgUser", userListByOrgId);
		request.setAttribute("OrgList", list);		
		return "web/vision/efficiency/efficiencyInfo";
	}
    /**
     * 跳转到立项页面
     * @param id
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/itemInfo.do")
	@RequiresPermissions("vision/efficiency/itemInfo.do")
	public String itemInfo(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
    	
    	//获取机构
		Organ organ=new Organ();
		List<Organ> organList = organService.getOrganList(organ);		
    	List<OrganVM> list=new ArrayList<OrganVM>();
		OrganVM frvm = null;
		for(Organ rc : organList){
			if(rc.getPid()==0 && rc.getSupervision()==0){
				frvm = new OrganVM();
				List<Organ> itemList = new ArrayList<Organ>();//用于当做OrganVM的itemList
				frvm.setId(rc.getId());
				frvm.setName(rc.getName());
				String path =rc.getId()+".";//当前登录机构的子机构的path都以此开头
				String substring=null;
				for(Organ rc1 : organList){
					if(rc1.getPath().length()>path.length()){
						substring=rc1.getPath().substring(0, path.length());
					}
					if(rc1.getPid() == rc.getId() && rc1.getSupervision()==0){ 
						itemList.add(rc1);
					}else if(rc1.getId()==43 && rc1.getPid() == rc.getId()){
						itemList.add(rc1);
					}else if(path.equals(substring) && rc1.getSupervision()==0){//添加孙子级节点	
						itemList.add(rc1);								
					}
				}
				frvm.setItemList(itemList);
				list.add(frvm);
			}
		}
		
		//获取当前登录用户所属机构下的所有用户
		User lgUser = this.getLoginUser();
		List<User> byLgUser = userService.getUserListByLgUser(lgUser);		
		request.setAttribute("byLgUser", byLgUser);
		request.setAttribute("OrgList", list);		
		request.setAttribute("itemId", id);
		return "web/vision/efficiency/itemInfo";
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
		
		// 获取当前登录用户
		User u = this.getLoginUser();
		//获取要立项的项目
		Item item2 = itemService.selectByPrimaryKey(item.getId());
		try {
			// 将前台传过来的String类型的时间转换为Date类型
			if (end_time != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(end_time);
				item2.setEndTime(date); // 完成时间			
				item2.setStatus(1);
				item2.setUuid(item.getUuid());			
			}

			//首先删除当前未立项的这条项目
			boolean states = itemService.deleteItemById(item2.getId());
			//获取当前项目的流程，有且只有一个
			List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item2.getId()); 
			ItemProcess getItemProcess = itemProcessList.get(0);
			//获取初始化流程的附件集合
			List<ItemProcessFile> fileList = itemProcessFileService.getFileListByItemId(itemProcessList.get(0).getId());
			List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());	
			//立项返回的项目ID
			List<Integer> itemIds= new ArrayList<Integer>();
			for (Integer orgId : OrgIds) {
				item2.setId(0);
				item2.setSupervisionOrgId(orgId);
				itemService.insertSelective(item2);//根据机构数对项目进行立项
				Integer itemId = item2.getId(); //立项返回的ID
				itemIds.add(itemId);
				
				getItemProcess.setId(0);
				getItemProcess.setItemId(itemId);	
				getItemProcess.setDefined(false);
				getItemProcess.setOrgId(userOrgIDs.get(0));
				getItemProcess.setContent(content);
				itemProcessService.insert(getItemProcess);//将项目的初始化流程赋给立项的项目				
				Integer itemProcessId = getItemProcess.getId(); //返回的id
				
				for (ItemProcessFile itemProcessFile : fileList) {
					itemProcessFile.setId(0);
					itemProcessFile.setItemProcessId(itemProcessId);
					itemProcessFileService.insertSelective(itemProcessFile);//将初始化的附件赋给立项项目
				}
								
				//新增立项流程
//				List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());					
//				ItemProcess itemProcess = new ItemProcess();
//				itemProcess.setUuid(item.getUuid());
//				itemProcess.setItemId(itemId);
//				itemProcess.setDefined(false);
//				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_0);// 监察室立项状态
//				itemProcess.setPreparerOrgId(orgId); // 制单部门的ID
//				itemProcess.setOrgId(userOrgIDs.get(0));
//				itemProcess.setContent(content);
//				itemProcess.setPreparerId(u.getId());
//				itemProcess.setPreparerTime(new Date());
//				itemProcessService.insert(itemProcess);
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
     * 跳转到不分节点监察上传资料
     * @param item
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/efficiencyNoFile.do")
	@RequiresPermissions("vision/efficiency/efficiencyNoFile.do")
	public String branchFHFile(@RequestParam(value="id",required=false) Integer id,
			HttpServletRequest request, HttpServletResponse response){
 
    	Item item = itemService.selectByPrimaryKey(id);
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
		return "web/vision/efficiency/efficiencyNoFile";
	}
    
    /**
     * 不分节点上传资料
     */
    @ResponseBody
    @RequestMapping(value = "/jsonSaveOrUpdateItemProcess.do", method=RequestMethod.POST)
    @RequiresPermissions("vision/efficiency/jsonSaveOrUpdateItemProcess.do")
    public JsonResult<ItemProcess> jsonSaveOrUpdateFileItem(ItemProcess itemProcess, 
    		@RequestParam(value="contentID",required=false) Integer contentID,
    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
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
     * 跳转到监察室给出监察意见
     */
    @RequestMapping(value = "/toOpinion.do")
    @RequiresPermissions("vision/efficiency/toOpinion.do")
    public String toOpinion(Item item, 
            HttpServletRequest request, HttpServletResponse response){
    	Item it = itemService.selectByPrimaryKey(item.getId());
    	if(it.getPreparerTime() != null){
    		it.setPreparerTimes(Constants.DATE_FORMAT.format(it.getPreparerTime()));
		}
    	request.setAttribute("Item", it);
    	return "web/vision/efficiency/opinion";
    }
    
    /**
     * 跳转到整改页面
     * @param item
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/resetItem.do")
    @RequiresPermissions("vision/efficiency/resetItem.do")
    public String resetItem(Item item, 
        HttpServletRequest request, HttpServletResponse response){
    	
     	request.setAttribute("Item", item);
     	return "web/vision/efficiency/resetView";
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
				Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
				item.setLasgTag(Constants.EFFICIENCY_VISION_4);
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
    	
    	//新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
    	//获取当前登录用户
    	User u = this.getLoginUser(); 
		js.setCode(new Integer(1));
		js.setMessage("保存信息失败!");
		try {   
			Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
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
						itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_3);//监察意见为不需要整改，进入监察室录入监察结论
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
    @RequestMapping(value = "/showItem.do")
    @RequiresPermissions("vision/efficiency/showItem.do")
    public String showItem(Item item, 
            HttpServletRequest request, HttpServletResponse response){
    	item = itemService.selectByPrimaryKey(item.getId());
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
				if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_0){
					request.setAttribute("ItemProcess", ip); //监察内容
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_2){
					request.setAttribute("ItemProcess2", ip); //已经上传资料
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_3){
					request.setAttribute("ItemProcess3", ip); //监察意见
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_4){
					request.setAttribute("ItemProcess4", ip); //监察室已经给出意见，需要整改，进入到被监察对象录入整改情况
				}else if(ip.getContentTypeId() ==666){
					request.setAttribute("ItemProcess5", ip);
				}else if(ip.getContentTypeId() ==688){
					request.setAttribute("ItemProcess10", ip); //不问责，进入监察室给出结论
				}else if(ip.getContentTypeId() ==777){
					request.setAttribute("ItemProcess6", ip); //监察室录入问责资料
				}else if(ip.getContentTypeId() ==778){
					request.setAttribute("ItemProcess7", ip); //再次上传整改情况
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_6){
					request.setAttribute("ItemProcess8", ip); //给出监察结论项目完结
				}else if(ip.getContentTypeId() ==779){ 
					request.setAttribute("ItemProcess9", ip); //再次整改后项目完结
				}else if(ip.getContentTypeId() ==999){ 				
					ipList.add(ip);					//分节点上传资料
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_11){					
					ipYJList.add(ip);			  //监察分节点上传资料的意见
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
		return "web/vision/efficiency/showItem";
    }
    
    /**
     * 效能监察流程
     * @param item
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/efficiencyFile.do")
	@RequiresPermissions("vision/efficiency/efficiencyFile.do")
	public String branchFHFile(Item item,
			HttpServletRequest request, HttpServletResponse response){
		int tag = item.getTag();
		item = itemService.selectByPrimaryKey(item.getId());
		if(item.getPreparerTime() != null){
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item.getPreparerTime()));
		}
		List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item.getId());  

		if(itemProcessList.size()>0){ 
			List<ItemProcess> ipList=new ArrayList<ItemProcess>(); //分节点上传的资料
			List<ItemProcess> ipYJList=new ArrayList<ItemProcess>(); //监察分节点上传的资料的意见
			for(ItemProcess ip : itemProcessList){  
				List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
				fileList = itemProcessFileService.getFileListByItemId(ip.getId());
				ip.setFileList(fileList);  
				if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_0){
					request.setAttribute("ItemProcess", ip); //新建状态
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_2){
					request.setAttribute("ItemProcess2", ip); //上传资料状态
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_3){
					request.setAttribute("ItemProcess3", ip); //给出监察意见
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_4){
					request.setAttribute("ItemProcess4", ip); //录入整改情况
				}else if(ip.getContentTypeId() ==666){
					request.setAttribute("ItemProcess5", ip); //录入整改意见
				}else if(ip.getContentTypeId() ==688){
					request.setAttribute("ItemProcess10", ip); //不问责，进入监察室给出结论
				}else if(ip.getContentTypeId() ==777){
					request.setAttribute("ItemProcess6", ip); //问责处理
				}else if(ip.getContentTypeId() ==778){
					request.setAttribute("ItemProcess7", ip); //再次整改
				}else if(ip.getContentTypeId() ==779){
					request.setAttribute("ItemProcess9", ip); //再次整改后，进入监察室给出结论
				}else if(ip.getContentTypeId() ==999){					
					ipList.add(ip);					//分节点上传资料
				}else if(ip.getContentTypeId() ==Constants.EFFICIENCY_VISION_11){					
					ipYJList.add(ip);			  //监察分节点上传资料的意见
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
		request.setAttribute("tag", tag);
		request.setAttribute("Item", item); 
		request.setAttribute("ContentTypeId", Constants.CONTENT_TYPE_ID_ZZZZ_OVER);
		
		if(tag == 67 ){
			ItemProcess itemProcess = itemProcessList.get(itemProcessList.size() - 1); // 获取最后一个元素
			Integer contentTypeId = itemProcess.getContentTypeId();
			if(contentTypeId==72){
				request.setAttribute("contentID", contentTypeId);
			}			
			return "web/vision/efficiency/efficiencyNoFile";
		}
		if(tag == 68 ){			
			return "web/vision/efficiency/opinion";
		}
		if(tag == 69 ){			
			return "web/vision/efficiency/resetView";
		}
		if(tag==688){
			return "web/vision/efficiency/jianChaJieLun";
		}
		if(tag==777){
			return "web/vision/efficiency/wenZeView";
		}
		if(tag==778){
			return "web/vision/efficiency/zhengGaiView";
		}
		return "";
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
    		@RequestParam(value="id" ,required=false) Integer itemId,
    		@RequestParam(value="isFollow" ,required=false) Integer isFollow,
    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
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
			itemProcess.setItemId(itemId);
			
			if(isFollow == 1){
				itemProcess.setContent("不问责");
				itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_10);//不问责，进入监察室给出结论
//				Item item = itemService.selectByPrimaryKey(itemId);
//				item.setStatus(Constants.ITEM_STATUS_OVER);
//				itemService.updateByPrimaryKeySelective(item);
				js.setCode(0);
				js.setMessage("请监察室给出监察结论!"); 
			}else if(isFollow == 0){
				itemProcess.setContent("问责");
				itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_7);//进入监察室录入问责资料
				js.setCode(0);
				js.setMessage("项目进入问责流程!"); 
			}	
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_XLJC, "用户："+loginUser.getName()+"，对效能监察项目进行问责操作", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
			itemProcessService.insert(itemProcess);			
			
		}catch(Exception ex){
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
    }
    
    /**
     * 监察室录入问责资料
     * @param itemProcess
     * @param status
     * @param request
     * @param response
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @RequestMapping(value = "/jsonSaveWenZeItem.do", method=RequestMethod.POST)
    @RequiresPermissions("vision/efficiency/jsonSaveWenZeItem.do")
    public JsonResult<ItemProcess> jsonSaveWenZeItem(ItemProcess itemProcess, 
    		@RequestParam(value="status",required = false) Integer status,
    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
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
			itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_9);//监察室录入问责相关资料，项目进入被监察再次上传整改情况
		
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_XLJC, "用户："+loginUser.getName()+"，录入效能监察项目的问责资料", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
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
     * 再次整改
     */
    @ResponseBody
    @RequestMapping(value = "/jsonSaveZCZGItem.do", method=RequestMethod.POST)
    @RequiresPermissions("vision/efficiency/jsonSaveZCZGItem.do")
    public JsonResult<ItemProcess> jsonSaveZCZGItem(ItemProcess itemProcess, 
    		HttpServletRequest request, HttpServletResponse response) throws ParseException{
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
			itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_8); //再次录入整改情况后进入监察室录入监察结论
			
//			Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
//			item.setStatus(Constants.ITEM_STATUS_OVER);
//			itemService.updateByPrimaryKeySelective(item);
//			if(status!=null){
//				if(status==null ||status!=4){//监察对象给出监察意见
//					itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_666);				
//					Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
//					item.setLasgTag(Constants.EFFICIENCY_VISION_4);
//				}else{
//					itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_6);//流程完结
//					Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
//					item.setEndTime(new Date());
//					item.setStatus(Constants.ITEM_STATUS_OVER);
//					itemService.updateByPrimaryKeySelective(item);
//				}
//			}
//			if(status==null){//被监察对象整改操作
//				itemProcess.setContentTypeId(Constants.EFFICIENCY_VISION_666);				
//				Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
//				item.setLasgTag(Constants.EFFICIENCY_VISION_4);
//			}
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
}
