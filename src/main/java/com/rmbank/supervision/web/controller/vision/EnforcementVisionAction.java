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
 * 执法监察控制器
 * 
 * @author DELL
 *
 */
@Scope("prototype")
@Controller
@RequestMapping("/vision/enforce")
public class EnforcementVisionAction extends SystemAction {

	@Resource
	private ItemService itemService;
	@Resource
	private UserService userService;
	@Resource
	private OrganService organService;
	@Resource
	private ConfigService configService;
	@Resource
	private ItemProcessService itemProcessService;
	@Resource
	private ItemProcessFileService itemProcessFileService;
	@Resource
	private SysLogService logService;
	
	/**
	 * 执法监察列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/enforceList.do")
	@RequiresPermissions("vision/enforce/enforceList.do")
	public String enforceList(Item item, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		// 判断搜索名是否为空，不为空则转为utf-8编码
		if (item.getSearchName() != null && item.getSearchName() != "") {
			String searchName = URLDecoder
					.decode(item.getSearchName(), "utf-8");
			item.setSearchName(searchName);
		}
		// 设置页面初始值及页面大小
		if (item.getPageNo() == null) {
			item.setPageNo(1);
		}
		item.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		int totalCount = 0;
		// 分页集合
		List<Item> itemList = new ArrayList<Item>();
		// 获取当前登录用户
		User loginUser = this.getLoginUser();
		// 获取当前用户对应的机构列表
		List<Organ> userOrgList = userService.getUserOrgByUserId(loginUser
				.getId());
		// 获取当前用户对应的第一个机构
		Organ userOrg = userOrgList.get(0);
		try {
			if (userOrg.getOrgtype()==Constants.ORG_TYPE_1 ||
					userOrg.getOrgtype()==Constants.ORG_TYPE_2 ||
					userOrg.getOrgtype()==Constants.ORG_TYPE_3 ||
					Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())) {
							
				// 取满足要求的参数数据
				item.setSupervisionTypeId(4);
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION);
				itemList = itemService.getItemListByType(item);
				// 取满足要求的记录总数
				totalCount = itemService.getItemCountBySSJC(item);
			} else {
				// 取满足要求的参数数据
				item.setSupervisionTypeId(4);
				item.setSupervisionOrgId(userOrg.getId());
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION);
				itemList = itemService.getItemListByTypeAndLogOrg(item);
				// 取满足要求的记录总数
				totalCount = itemService.getItemCountByLogOrgSSJC(item);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (Item it : itemList) {
			List<ItemProcess> itemprocessList = itemProcessService
					.getItemProcessItemId(it.getId());
			if (itemprocessList.size() > 0) {
				ItemProcess lastItem = itemprocessList.get(itemprocessList
						.size() - 1);
				it.setLasgTag(lastItem.getContentTypeId());
			}
		}
		// 通过request对象传值到前台
		item.setTotalCount(totalCount);
		request.setAttribute("Item", item);
		request.setAttribute("userOrg", userOrg);
		request.setAttribute("itemList", itemList);
		
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了执法监察项目列表的查看", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		return "web/vision/enforce/enforceList";
	}

	/**
	 * 跳转到添加工作事项
	 * 
	 * @param id
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "/itemInfo.do")
	@RequiresPermissions("vision/enforce/itemInfo.do")
	public String ItemInfo(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		// 获取机构
		Organ organ = new Organ();
		List<Organ> organList = organService.getOrganList(organ);
		// 页面加载的机构
		List<OrganVM> list = new ArrayList<OrganVM>();
		OrganVM frvm = null;
		for (Organ rc : organList) {
			if (rc.getId() == 21 || rc.getId() == 20) {
				frvm = new OrganVM();
				List<Organ> itemList = new ArrayList<Organ>();// 用于当做OrganVM的itemList
				frvm.setId(rc.getId());
				frvm.setName(rc.getName());
				for (Organ rc1 : organList) {
					if (rc1.getPid() == rc.getId() && rc1.getSupervision() == 0) {
						itemList.add(rc1);
					} else if (rc1.getId() == 43 && rc1.getPid() == rc.getId()) {
						itemList.add(rc1);
					}
				}
				frvm.setItemList(itemList);
				list.add(frvm);
			}
		}
		// 获取当前登录用户所属机构下的所有用户
		User lgUser = this.getLoginUser();
		List<User> byLgUser = userService.getUserListByLgUser(lgUser);
		// 获取执法类型
		List<Meta> meatListByKey = configService
				.getMeatListByKey(Constants.META_LAWTYPE_KEY);
		request.setAttribute("meatListByKey", meatListByKey);
		request.setAttribute("byLgUser", byLgUser);
		request.setAttribute("OrgList", list);
		return "web/vision/enforce/ItemInfo";
	}

	/**
	 * 依法行政领导小组添加工作事项
	 * 
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateItem.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/enforce/jsonSaveOrUpdateItem.do")
	public JsonResult<Item> jsonSaveOrUpdateItem(Item item,			
			@RequestParam(value = "end_time", required = false) String end_time,// 用于接收前台传过来的String类型的时间
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "OrgId", required = false) Integer[] OrgIds,
			HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
		// 将前台传过来的String类型的时间转换为Date类型
		if (end_time != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(end_time);
			item.setEndTime(date); // 完成时间
		}
		item.setPreparerTime(new Date()); // 创建时间
		item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); // 项目类型
		item.setSupervisionTypeId(4);
		item.setPid(0); // 主任务节点的ID
		item.setStageIndex(new Byte("0")); // 工作阶段排序
		// 获取当前登录用户
		User u = this.getLoginUser();
		item.setPreparerId(u.getId()); // 制单人的ID
		item.setSupervisionUserId(0); //
		// 获取当前用户所属的机构id，当做制单部门的ID
		List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
		item.setPreparerOrgId(userOrgIDs.get(0)); // 制单部门的ID
		// 新建一个json对象 并赋初值
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");

		boolean State = false;
		try {
			// 如为新增，则给id置0
			if (item.getId() == null || item.getId() == 0) {
				item.setId(0);
			}
			// 创建用于新增时根据项目名称去查询项目是否存在的对象
			Item im = new Item();
			im.setName(item.getName());
			// 根据name去数据库匹配，如编辑，则可以直接保存；如新增，则需匹配该项目是否重复
			List<Item> lc = itemService.getExistItem(im);
			if (lc.size() == 0) {
				State = itemService.saveOrUpdateItem(item, OrgIds, content);
				if (State) {
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_ZFJC, "用户："+loginUser.getName()+"，添加了执法监察的工作事项", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
					
					js.setCode(new Integer(0));
					js.setMessage("保存项目信息成功!");
					return js;
				} else {
					return js;
				}
			} else {
				js.setMessage("该项目已存在!");
				return js;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return js;
	}

	/**
	 * 跳转到立项监察
	 * 
	 * @param id
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "/enforceInfo.do")
	@RequiresPermissions("vision/enforce/enforceInfo.do")
	public String efficiencyInfo(
			@RequestParam(value = "id", required = false) Integer id,// 项目id
			HttpServletRequest request, HttpServletResponse response) {

		Item item = itemService.selectByPrimaryKey(id);
		// 获取机构
		Organ organ = new Organ();
		List<Organ> organList = organService.getOrganList(organ);
		// 页面加载的机构
		List<OrganVM> list = new ArrayList<OrganVM>();
		OrganVM frvm = null;
		for (Organ rc : organList) {
			if (rc.getId() == 21 || rc.getId() == 20) {
				frvm = new OrganVM();
				List<Organ> itemList = new ArrayList<Organ>();// 用于当做OrganVM的itemList
				frvm.setId(rc.getId());
				frvm.setName(rc.getName());
				String path =rc.getId()+".";//当前登录机构的子机构的path都以此开头
				String substring=null;
				for (Organ rc1 : organList) {
					if(rc1.getPath().length()>path.length()){
						substring=rc1.getPath().substring(0, path.length());
					}
					if (rc1.getPid() == rc.getId() && rc1.getSupervision() == 0) {
						itemList.add(rc1);
					}else if(path.equals(substring) && rc1.getSupervision() == 0){//添加孙子级节点	
						itemList.add(rc1);								
					}/*
					 * else if(rc1.getId()==43 && rc1.getPid() == rc.getId()){
					 * itemList.add(rc1); }
					 */
				}
				frvm.setItemList(itemList);
				list.add(frvm);
			}
		}
		// 获取当前登录用户所属机构下的所有用户
		User lgUser = this.getLoginUser();
		List<User> byLgUser = userService.getUserListByLgUser(lgUser);
		// 获取执法类型
		List<Meta> meatListByKey = configService
				.getMeatListByKey(Constants.META_LAWTYPE_KEY);
		request.setAttribute("meatListByKey", meatListByKey);
		request.setAttribute("byLgUser", byLgUser);
		request.setAttribute("OrgList", list);
		request.setAttribute("Item", item);
		return "web/vision/enforce/enforceInfo";
	}

	/**
	 * 监察室进行执法监察立项操作
	 * 
	 * @param item
	 * @param end_time
	 * @param content
	 * @param OrgIds
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonUpdateItem.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/enforce/jsonUpdateItem.do")
	public JsonResult<Item> jsonUpdateItem(Item item,			
			@RequestParam(value = "end_time", required = false) String end_time,// 用于接收前台传过来的String类型的时间
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "OrgId", required = false) Integer[] OrgIds,
			HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
		// 新建一个json对象 并赋初值
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");
		
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
			if (item.getSuperItemType() == 61) {
				// 如果为综合执法，直接修改该项目
				item2.setSupervisionOrgId(OrgIds[0]);
				itemService.updateByPrimaryKeySelective(item2);
				
				//新增项目操作流程
				List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());					
				ItemProcess itemProcess = new ItemProcess();
				itemProcess.setUuid(item.getUuid());
				itemProcess.setItemId(item.getId());
				itemProcess.setDefined(false);
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_1);// 监察室立项状态
				itemProcess.setPreparerOrgId(userOrgIDs.get(0)); // 制单部门的ID
				itemProcess.setOrgId(userOrgIDs.get(0));
				itemProcess.setContent(content);
				itemProcess.setPreparerId(u.getId());
				itemProcess.setPreparerTime(new Date());
				itemProcessService.insert(itemProcess);
				
			}else if(item.getSuperItemType() == 62){ 
				//如果为单项执法，则需要根据勾选的机构数目来立项
				List<Integer> itemIds= new ArrayList<Integer>();
				//首先删除当前未立项的这条项目
				itemService.deleteItemById(item2.getId());
				//获取当前项目的流程，有且只有一个
				List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item2.getId()); 
				ItemProcess getItemProcess = itemProcessList.get(0);
				//获取初始化流程的附件集合
				List<ItemProcessFile> fileList = itemProcessFileService.getFileListByItemId(itemProcessList.get(0).getId());
				
				for (Integer orgId : OrgIds) {
					item2.setId(0);
					item2.setSupervisionOrgId(orgId);
					itemService.insertSelective(item2);//根据机构数对项目进行立项
					Integer itemId = item2.getId(); //立项返回的ID
					itemIds.add(itemId);
					
					getItemProcess.setId(0);
					getItemProcess.setItemId(itemId);				
					itemProcessService.insert(getItemProcess);//将项目的初始化流程赋给立项的项目				
					Integer itemProcessId = getItemProcess.getId(); //返回的id
					
					for (ItemProcessFile itemProcessFile : fileList) {
						itemProcessFile.setId(0);
						itemProcessFile.setItemProcessId(itemProcessId);
						itemProcessFileService.insertSelective(itemProcessFile);//将初始化的附件赋给立项项目
					}
									
					//新增立项流程
					List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());					
					ItemProcess itemProcess = new ItemProcess();
					itemProcess.setUuid(item.getUuid());
					itemProcess.setItemId(itemId);
					itemProcess.setDefined(false);
					itemProcess.setContentTypeId(Constants.ENFORCE_VISION_1);// 监察室立项状态
					itemProcess.setPreparerOrgId(orgId); // 制单部门的ID
					itemProcess.setOrgId(userOrgIDs.get(0));
					itemProcess.setContent(content);
					itemProcess.setPreparerId(u.getId());
					itemProcess.setPreparerTime(new Date());
					itemProcessService.insert(itemProcess);
				}
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
	 * 执法监察所有流程
	 * 
	 * @param item
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/enforceFile.do")
	@RequiresPermissions("vision/enforce/enforceFile.do")
	public String branchFHFile(Item item, HttpServletRequest request,
			HttpServletResponse response) {
		int tag = item.getTag();
		item = itemService.selectByPrimaryKey(item.getId());
		if (item.getPreparerTime() != null) {
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item
					.getPreparerTime()));
		}
		List<ItemProcess> itemProcessList = itemProcessService
				.getItemProcessItemId(item.getId());

		if (itemProcessList.size() > 0) {
			for (ItemProcess ip : itemProcessList) {
				List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
				fileList = itemProcessFileService.getFileListByItemId(ip
						.getId());
				ip.setFileList(fileList);
				if (ip.getContentTypeId() == Constants.ENFORCE_VISION_0) {
					request.setAttribute("ItemProcess", ip); // 新建状态
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_1.intValue()) {
					request.setAttribute("ItemProcess1", ip); // 被监察对象录入项目方案
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_2.intValue()) {
					request.setAttribute("ItemProcess2", ip); // 监察室给出监察意见，并且项目合规
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_3.intValue()) {						
					request.setAttribute("ItemProcess3", ip); // 监察室给出监察意见,资料合规
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_1A.intValue()){						
					request.setAttribute("ItemProcess4", ip); // 监察室给出监察意见，资料不合规
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_4.intValue()) {
					request.setAttribute("ItemProcess5", ip); // 被监察对象已经录入录入意见书
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_5.intValue()) {
					request.setAttribute("ItemProcess6", ip); // 监察室对象已经检查意见书
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_6.intValue()) {
					request.setAttribute("ItemProcess8", ip); // 监察室已经录入监察执行情况，并且合规
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_7.intValue()) {						
					request.setAttribute("ItemProcess10", ip); // 被监察对象录入督促整改情况，但是要处罚
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_8.intValue()) {						
					request.setAttribute("ItemProcess11", ip); // 被监察对象录入督促整改情况，但是要处罚
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_9.intValue()) {	
					request.setAttribute("ItemProcess12", ip); // 监察室监察行政处罚意见告知，并且合规		
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_12.intValue()) {						
					request.setAttribute("ItemProcess15", ip); //听证，被监察对象录入听证相关资料
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_10.intValue()) {						
					request.setAttribute("ItemProcess14", ip); //不听证，被监察对象录入行政处罚决定书 
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_13.intValue()) {						
					request.setAttribute("ItemProcess16", ip); //监察室监察行政处罚决定书，并且合规
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_15.intValue()) {						
					request.setAttribute("ItemProcess18", ip); //监察室监察行政处罚决定书，并且合规
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_16.intValue()) {						
					request.setAttribute("ItemProcess19", ip); //需要复议，录入复议相关资料
				}
				
			}
		}
		// 获取当前用户
		User lgUser = this.getLoginUser();

		request.setAttribute("User", lgUser);
		request.setAttribute("tag", tag);
		request.setAttribute("Item", item);
		request.setAttribute("ContentTypeId",
				Constants.CONTENT_TYPE_ID_ZZZZ_OVER);

		if (tag == Constants.ENFORCE_VISION_1.intValue()) {
			return "web/vision/enforce/itemProcess130"; // 到被监察对象录入立项资料页面
		}
		if (tag == Constants.ENFORCE_VISION_2.intValue()) {
			return "web/vision/enforce/itemProcess131"; // 到监察室录入监察意见页面
		}
		if (tag == Constants.ENFORCE_VISION_3.intValue()) {
			return "web/vision/enforce/itemProcess132"; // 到被监察对象录入监察报告、意见书页面
		}
		if (tag == Constants.ENFORCE_VISION_4.intValue()) {
			return "web/vision/enforce/itemProcess133"; // 监察室给出监察意见书的意见页面
		}
		if (tag == Constants.ENFORCE_VISION_5.intValue()) {
			return "web/vision/enforce/itemProcess134"; // 到被监察对象录入督促整改情况页面
		}
		if (tag == Constants.ENFORCE_VISION_6.intValue()) {
			return "web/vision/enforce/itemProcess135"; // 到监察室给出监察结论页面
		}
		if (tag == Constants.ENFORCE_VISION_7.intValue()) {
			return "web/vision/enforce/itemProcess136"; // 到被监察对象录入行政处罚意见告知书页面
		}
		if (tag == Constants.ENFORCE_VISION_8.intValue()) {
			return "web/vision/enforce/itemProcess137"; //到被监察对象录入行政处罚意见告知书
		}
		if (tag == Constants.ENFORCE_VISION_9.intValue()) {
			return "web/vision/enforce/itemProcess138"; //不听证，到被监察对象录入行政处罚决定书页面
		}
		if (tag == 139) {
			return "web/vision/enforce/itemProcess139"; // 听证到录入听证相关资料页面
		}if (tag == Constants.ENFORCE_VISION_10.intValue()) {
			return "web/vision/enforce/itemProcess140"; // 监察室监察行政处罚决定书
		}if (tag == Constants.ENFORCE_VISION_13.intValue()) {
			return "web/vision/enforce/itemProcess141"; // 监察室监察行政处罚决定书
		}if (tag == Constants.ENFORCE_VISION_16.intValue()) {
			return "web/vision/enforce/itemProcess142"; // 需要复议，到录入复议相关资料页面
		}
		return "";
	}

	/**
	 * 流程
	 * @param itemProcess
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveItemProcess.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/enforce/jsonSaveItemProcess.do")
	public JsonResult<ItemProcess> jsonSaveOrUpdateFileItem(
			@RequestParam(value="tag" ,required=false) Integer tag,
			@RequestParam(value="status" ,required=false) Integer status,
			ItemProcess itemProcess, HttpServletRequest request,
			HttpServletResponse response) throws ParseException {
		// 新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		// 获取当前登录用户
		User u = this.getLoginUser();
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");
		try {
			// 获取当前用户所属的机构id，当做制单部门的ID
			List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u
					.getId());
			itemProcess.setPreparerOrgId(userOrgIDs.get(0)); // 制单部门的ID
			itemProcess.setOrgId(userOrgIDs.get(0)); // 制单部门的ID
			itemProcess.setPreparerId(u.getId());
			itemProcess.setPreparerTime(new Date());
			itemProcess.setDefined(false);
			if(tag==130){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_2);//被监察对象已经录入立项资料 
			}else if(tag==131 && status==0){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_3);//监察室给出监察意见，并且合规 
			}else if(tag==131 && status==1){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_1A);//监察室给出监察意见，但是不合规
			}else if(tag==132){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_4);//被监察对象录入现场监察报告、意见书
			}else if(tag==133 && status==0){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_5);//监察室给出监察意见书意见，并且合规
			}else if(tag==133 && status==1){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_3A);//监察室给出监察意见书意见，但是不合规
			}else if(tag==134 && status==0){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_6);//被监察对象录入督促整改情况，并且不处罚
			}else if(tag==134 && status==1){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_7);//被监察对象录入督促整改情况，但是要处罚
			}else if(tag==135){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_OVER);//监察室给出监察结论，项目完结
				Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
				if(item != null){
					if(item.getStatus() == 3){
						item.setStatus(5);
					}else{
						item.setStatus(4);
					}
					itemService.updateByPrimaryKeySelective(item);
				}
			}else if(tag==136){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_8);//被监察对象录入行政处罚意见告知书
			}else if(tag==137 && status==0){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_9);//监察室监察行政处罚意见告知书，并且合规
			}else if(tag==137 && status==1){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_2A);//监察室监察行政处罚意见告知书，但是不合规
			}else if(tag==138 && status==0){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_10);//被监察对象录入行政处罚决定书
			}else if(tag==138 && status==1){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_10);//被监察对象录入行政处罚决定书
			}else if(tag==139){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_12);//录入了听证相关资料，进入被监察对象录入行政处罚决定书
			}else if(tag==140 && status==0){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_13);//监察室监察行政处罚决定书，并且合规
			}else if(tag==140 && status==1){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_14);//监察室监察行政处罚决定书，但是不合规
			}else if(tag==141){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_15);//被监察对象录入行政处罚情况
			}else if(tag==142){
				itemProcess.setContentTypeId(Constants.ENFORCE_VISION_16);//复议，录入复议相关资料
			}			

			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_ZFJC, "用户："+loginUser.getName()+"，执行了对执法监察项目的流程操作", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
			
			itemProcessService.insert(itemProcess);

			js.setCode(0);
			js.setMessage("录入方案成功!");
		} catch (Exception ex) {
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
	}

	
	/**
	 * 修改立项状态
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonsetProjectById.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/enforce/jsonsetProjectById.do")
	public JsonResult<Item> jsonsetProjectById(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("修改立项状态失败!");
		try {
			Item item = new Item();
			item.setId(id);
			item.setStatus(1);
			int state = itemService.updateByPrimaryKeySelective(item);
			if (state == 1) {
				js.setCode(new Integer(0));
				js.setMessage("修改立项状态成功!");
				return js;
			} else {
				return js;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return js;
	}

	/**
	 * 删除项目
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsondeleteItemById.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/enforce/jsondeleteItemById.do")
	public JsonResult<Item> jsondeleteItemById(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("删除失败!");
		boolean state = false;
		try {
			Item item = itemService.selectByPrimaryKey(id);
			state = itemService.deleteItemById(id);
			if (state) {
				User loginUser = this.getLoginUser();
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_ZFJC, "用户："+loginUser.getName()+"，删除了项目："+item.getName(), 3, loginUser.getId(), loginUser.getUserOrgID(), ip);
				
				js.setCode(new Integer(0));
				js.setMessage("删除成功!");
				return js;
			} else {
				return js;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return js;
	}

	/**
	 * 查看项目
	 * 
	 * @param item
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/showItem.do")
	@RequiresPermissions("vision/enforce/showItem.do")
	public String showItem(Item item, HttpServletRequest request,
			HttpServletResponse response) {
		item = itemService.selectByPrimaryKey(item.getId()); // 项目基本信息
		if (item.getPreparerTime() != null) {
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item
					.getPreparerTime()));
		}
		// 获取流程集合
		List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item.getId());				
		if (itemProcessList.size() > 0) {
			for (ItemProcess ip : itemProcessList) {
				List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
				fileList = itemProcessFileService.getFileListByItemId(ip
						.getId()); // 获取当前流程拥有的附件
				ip.setFileList(fileList);
				if (ip.getContentTypeId() == Constants.ENFORCE_VISION_0) {
					request.setAttribute("ItemProcess", ip); // 添加工作事项后的信息
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_1.intValue()) {						
					request.setAttribute("ItemProcess1", ip); // 监察室立项后的信息
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_2.intValue()) {						
					request.setAttribute("ItemProcess2", ip); // 被监察对象录入立项后的信息
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_3.intValue()) {						
					List<ItemProcess> list = new ArrayList<ItemProcess>(); // 这里处理了循环不合规的情况
					for (ItemProcess iplt : itemProcessList) {
						if (iplt.getContentTypeId() == Constants.ENFORCE_VISION_2.intValue()) {
							list.add(iplt); //获取回到被监察对象录入资料的情况的流程
						}
					}
					ItemProcess itemProcess = itemProcessList.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (list.size() <= 1||itemProcess.getContentTypeId() == Constants.ENFORCE_VISION_3.intValue()){
						request.setAttribute("ItemProcess3", ip); // 监察室给出监察意见,资料合规
					}else if(list.size() > 1 && itemProcess.getContentTypeId()>Constants.ENFORCE_VISION_3.intValue()){
						request.setAttribute("ItemProcess3", ip); // 监察室给出监察意见,资料合规
					}else {
						request.setAttribute("ItemProcess3", null); // 已经重新录入资料，上一次的监察意见不显示
					}					
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_1A.intValue()) {						
					List<ItemProcess> list = new ArrayList<ItemProcess>(); // 这里处理了循环不合规的情况
					for (ItemProcess iplt : itemProcessList) {
						if (iplt.getContentTypeId() == Constants.ENFORCE_VISION_2.intValue()) {
							list.add(iplt);
						}
					}
					ItemProcess itemProcess = itemProcessList.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (list.size() <= 1 || itemProcess.getContentTypeId() == Constants.ENFORCE_VISION_1A.intValue()) {
						request.setAttribute("ItemProcess4", ip); // 第一次录入资料，监察室给出监察意见
					}else {
						request.setAttribute("ItemProcess4", null); // 已经重新上传方案，上一次的监察意见不显示
					}
					
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_4.intValue()) {						
					request.setAttribute("ItemProcess5", ip); // 被监察对象录入现场监察报告、意见书
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_5.intValue()) {						
					List<ItemProcess> list = new ArrayList<ItemProcess>(); // 这里处理了循环不合规的情况
					for (ItemProcess iplt : itemProcessList) {
						if (iplt.getContentTypeId() == Constants.ENFORCE_VISION_2.intValue()) {
							list.add(iplt); //获取回到被监察对象录入资料的情况的流程
						}
					}
					ItemProcess itemProcess = itemProcessList.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (list.size() <= 1||itemProcess.getContentTypeId() == Constants.ENFORCE_VISION_5.intValue()){
						request.setAttribute("ItemProcess6", ip); // 监察室给出监察意见,意见书合规
					}else if(list.size() > 1 && itemProcess.getContentTypeId()>Constants.ENFORCE_VISION_5.intValue()){
						request.setAttribute("ItemProcess6", ip); // 监察室给出监察意见,意见书合规
					}else {
						request.setAttribute("ItemProcess6", null); // 已经重新录入资料，上一次的监察意见不显示
					}					
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_3A.intValue()) {						
					List<ItemProcess> list = new ArrayList<ItemProcess>(); // 这里处理了循环不合规的情况
					for (ItemProcess iplt : itemProcessList) {
						if (iplt.getContentTypeId() == Constants.ENFORCE_VISION_4.intValue()) {
							list.add(iplt);
						}
					}
					ItemProcess itemProcess = itemProcessList.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (list.size() <= 1 || itemProcess.getContentTypeId() == Constants.ENFORCE_VISION_3A.intValue()) {
						request.setAttribute("ItemProcess7", ip); // 第一次录入资料，监察室给出监察意见
					}else {
						request.setAttribute("ItemProcess7", null); // 已经重新上传方案，上一次的监察意见不显示
					}
					
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_6.intValue()) {						
					request.setAttribute("ItemProcess8", ip); // 被监察对象录入督促整改情况，并且不处罚
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_7.intValue()) {						
					request.setAttribute("ItemProcess10", ip); // 被监察对象录入督促整改情况，但是要处罚
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_8.intValue()) {						
					request.setAttribute("ItemProcess11", ip); // 被监察对象录入行政处罚意见告知书
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_9.intValue()) {	
					List<ItemProcess> list = new ArrayList<ItemProcess>(); // 这里处理了循环不合规的情况
					for (ItemProcess iplt : itemProcessList) {
						if (iplt.getContentTypeId() == Constants.ENFORCE_VISION_8.intValue()) {
							list.add(iplt); //获取回到被监察对象录入资料的情况的流程
						}
					}
					ItemProcess itemProcess = itemProcessList.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (list.size() <= 1||itemProcess.getContentTypeId() == Constants.ENFORCE_VISION_5.intValue()){
						request.setAttribute("ItemProcess12", ip); // 监察室第一次监察行政处罚意见告知，并且合规
					}else if(list.size() > 1 && itemProcess.getContentTypeId()>Constants.ENFORCE_VISION_5.intValue()){
						request.setAttribute("ItemProcess12", ip); // 监察室监察行政处罚意见告知，并且合规
					}else {
						request.setAttribute("ItemProcess12", null); // 已经重新录入资料，上一次的监察意见不显示
					}					
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_2A.intValue()) {
					List<ItemProcess> list = new ArrayList<ItemProcess>(); // 这里处理了循环不合规的情况
					for (ItemProcess iplt : itemProcessList) {
						if (iplt.getContentTypeId() == Constants.ENFORCE_VISION_8.intValue()) {
							list.add(iplt);
						}
					}
					ItemProcess itemProcess = itemProcessList.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (list.size() <= 1 || itemProcess.getContentTypeId() == Constants.ENFORCE_VISION_2A.intValue()) {
						request.setAttribute("ItemProcess13", ip); // 监察室监察行政处罚意见告知，但是不合规
					}else {
						request.setAttribute("ItemProcess13", null); // 已经重新录入行政处罚意见书，上一次的监察意见不显示
					}					
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_10.intValue()) {						
					request.setAttribute("ItemProcess14", ip); //不听证，被监察对象录入行政处罚决定书 
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_12.intValue()) {						
					request.setAttribute("ItemProcess15", ip); //听证，被监察对象录入听证相关资料
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_13.intValue()) {						
					request.setAttribute("ItemProcess16", ip); //监察室监察行政处罚决定书，并且合规
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_14.intValue()) {	
					List<ItemProcess> list = new ArrayList<ItemProcess>(); // 这里处理了循环不合规的情况
					for (ItemProcess iplt : itemProcessList) {
						if (iplt.getContentTypeId() == Constants.ENFORCE_VISION_10.intValue()) {
							list.add(iplt);
						}
					}
					ItemProcess itemProcess = itemProcessList.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (list.size() <= 1 || itemProcess.getContentTypeId() == Constants.ENFORCE_VISION_14.intValue()) {
						request.setAttribute("ItemProcess17", ip); //监察室监察行政处罚决定书，但是不合规
					}else {
						request.setAttribute("ItemProcess17", null); // 已经重新录入行政处罚决定书，上一次的监察意见不显示
					}		
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_15.intValue()) {						
					request.setAttribute("ItemProcess18", ip); //被监察对象录入行政处罚情况
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_16.intValue()) {						
					request.setAttribute("ItemProcess19", ip); //需要复议，录入复议相关资料
				}else if (ip.getContentTypeId() == Constants.ENFORCE_VISION_OVER.intValue()) {						
					request.setAttribute("ItemProcess9", ip); // 监察给出监察结论，项目完结
				}
				
			}
		}

		// 获取当前用户
		User lgUser = this.getLoginUser();
		request.setAttribute("User", lgUser);
		request.setAttribute("Item", item);
		request.setAttribute("ContentTypeId",
				Constants.CONTENT_TYPE_ID_ZZZZ_OVER);
		
		User loginUser = this.getLoginUser();
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了对执法监察项目的查看", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		
		return "web/vision/enforce/showItem";
	}
}
