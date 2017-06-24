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
 * 廉政监察控制器
 * 
 * @author DELL
 *
 */
@Scope("prototype")

@Controller
@RequestMapping("/vision/incorrupt")
public class IncorruptVisionAction extends SystemAction {
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
	 * 廉政监察列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/incorruptList.do")
	@RequiresPermissions("vision/incorrupt/incorruptList.do")
	public String incorruptList(Item item, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		// 判断搜索名是否为空，不为空则转为utf-8编码
		if (item.getSearchName() != null && item.getSearchName() != "") {
			String searchName = URLDecoder
					.decode(item.getSearchName(), "utf-8");
			item.setSearchName(searchName);
		}
		// 设置页面初始值及页面大小
		if (item.getPageNo() == null)
			item.setPageNo(1);
		item.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		int totalCount = 0;
		// 获取当前登录用户
		User loginUser = this.getLoginUser();
		// 获取当前用户对应的机构列表
		List<Organ> userOrgList = userService.getUserOrgByUserId(loginUser
				.getId());
		// 获取当前用户对应的第一个机构
		Organ userOrg = userOrgList.get(0);
		// 分页集合
		List<Item> itemList = new ArrayList<Item>();
		try {
			// 成都分行监察室和超级管理员加载所有的项目
			if (userOrg.getOrgtype()==Constants.ORG_TYPE_1 ||
					userOrg.getOrgtype()==Constants.ORG_TYPE_2 ||
					userOrg.getOrgtype()==Constants.ORG_TYPE_3 ||
					Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser
							.getAccount())) {
				// 取满足要求的参数数据
				item.setSupervisionTypeId(3);
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION);
				itemList = itemService.getItemListByType(item);
				// 取满足要求的记录总数
				totalCount = itemService.getItemCountBySSJC(item);
			} else {// 获取当前用户需要完成的项目
					// 取满足要求的参数数据
				item.setSupervisionTypeId(3);
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
			List<ItemProcess> itemprocessList = itemProcessService.getItemProcessItemId(it.getId());
					
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
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了对廉政监察项目列表的查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		
		return "web/vision/incorrupt/incorruptList";
	}

	/**
	 * 跳转到添加工作事项
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/itemInfo.do")
	@RequiresPermissions("vision/incorrupt/itemInfo.do")
	public String itemInfo(HttpServletRequest request,
			HttpServletResponse response) {
		// 获取当前登录用户
		User loginUser = this.getLoginUser();
		// 获取当前用户对应的机构列表
		List<Organ> userOrgList = userService.getUserOrgByUserId(loginUser
				.getId());
		// 获取当前用户对应的第一个机构
		Organ userOrg = userOrgList.get(0);
		request.setAttribute("userOrg", userOrg);
		return "web/vision/incorrupt/ItemInfo";
	}

	/**
	 * 跳转到录入项目
	 * 
	 * @param id
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "/incorruptInfo.do")
	@RequiresPermissions("vision/incorrupt/incorruptInfo.do")
	public String efficiencyInfo(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		// 获取当前登录用户所属机构下的所有用户
		User lgUser = this.getLoginUser();
		List<User> byLgUser = userService.getUserListByLgUser(lgUser);

		// 获取当前用户对应的机构列表
		List<Organ> userOrgList = userService
				.getUserOrgByUserId(lgUser.getId());
		// 获取当前用户对应的第一个机构
		Organ userOrg = userOrgList.get(0);
		request.setAttribute("userOrg", userOrg);
		// 获取项目类别
		List<Meta> meatListByKey = configService
				.getMeatListByKey(Constants.META_ITEMCATEGORY_KEY);
		request.setAttribute("meatListByKey", meatListByKey);
		request.setAttribute("byLgUser", byLgUser);
		request.setAttribute("itemId", id);
		return "web/vision/incorrupt/incorruptInfo";
	}

	/**
	 * 被监察对象添加工作事项
	 * 
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateItem.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/incorrupt/jsonSaveOrUpdateItem.do")
	public JsonResult<Item> jsonSaveOrUpdateItem(
			Item item,
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

		item.setPreparerTime(new Date()); // 立项时间
		item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); // 项目类型
		item.setSupervisionTypeId(3);
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
					logService.writeLog(Constants.LOG_TYPE_LZJC, "用户："+loginUser.getName()+"，添加了廉政监察的工作事项", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
					
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
	 * 修改立项状态
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonsetProjectById.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/incorrupt/jsonsetProjectById.do")
	public JsonResult<Item> jsonsetProjectById(Item item,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("立项失败!");
		try {
			if (item != null && item.getId() > 0) {
				Item temp = itemService.selectByPrimaryKey(item.getId());
				if (temp != null) {
					temp.setSuperItemType(item.getSuperItemType());
					temp.setStatus(1);
					if (item.getEndTimes() != null) { 
						temp.setEndTime(Constants.DATE_FORMAT.parse(item.getEndTimes()+" 00:00:00"));
					}
					itemService.updateByPrimaryKeySelective(temp);
					List<ItemProcess> itemList = itemProcessService.getItemProcessItemId(item.getId());
							
					if (itemList.size() > 0) {
						ItemProcess itemProcess = itemList.get(0);
						itemProcess.setContent(item.getName());
						itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_0);								
						itemProcessService.updateByPrimaryKeySelective(itemProcess);
								
						User loginUser = this.getLoginUser();
						String ip = IpUtil.getIpAddress(request);		
						logService.writeLog(Constants.LOG_TYPE_LZJC, "用户："+loginUser.getName()+"，对廉政监察的项目进行了立项", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
						
						js.setCode(0);
						js.setMessage("立项成功，待被监察对象上传项目方案");
					}
				}
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
	@RequiresPermissions("vision/incorrupt/jsondeleteItemById.do")
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
				logService.writeLog(Constants.LOG_TYPE_LZJC, "用户："+loginUser.getName()+"，删除了廉政监察项目："+item.getName(), 3, loginUser.getId(), loginUser.getUserOrgID(), ip);
				
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
	 * 跳转到被监察对象录入方案
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/toSaveScheme.do")
	@RequiresPermissions("vision/incorrupt/toSaveScheme.do")
	public String toSaveScheme(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {

		Item item = itemService.selectByPrimaryKey(id);
		if (item.getPreparerTime() != null) {
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item
					.getPreparerTime()));
		}

		List<ItemProcess> itemProcessList = itemProcessService
				.getItemProcessItemId(item.getId());
		ItemProcess itemProcess = new ItemProcess();
		if (itemProcessList.size() > 0) {
			itemProcess = itemProcessList.get(0);
		}

		List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
		if (itemProcess.getId() != null) {
			fileList = itemProcessFileService.getFileListByItemId(itemProcess
					.getId());
		}
		// 获取当前用户
		User lgUser = this.getLoginUser();

		request.setAttribute("User", lgUser);
		request.setAttribute("ItemProcess", itemProcess);
		request.setAttribute("Item", item);
		request.setAttribute("FileList", fileList);
		return "web/vision/incorrupt/itemScheme";
	}

	/**
	 * 跳转到监察室录入监察意见
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/toOpinion.do")
	@RequiresPermissions("vision/incorrupt/toOpinion.do")
	public String toOpinion(
			@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "lasgTag", required = false) Integer lasgTag,
			HttpServletRequest request, HttpServletResponse response) {

		Item item = itemService.selectByPrimaryKey(id);
		if (item.getPreparerTime() != null) {
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item
					.getPreparerTime()));
			item.setLasgTag(lasgTag);
		}

		List<ItemProcess> itemProcessList = itemProcessService
				.getItemProcessItemId(item.getId());
		ItemProcess itemProcess = new ItemProcess();
		if (itemProcessList.size() > 0) {
			itemProcess = itemProcessList.get(0);
		}

		List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
		if (itemProcess.getId() != null) {
			fileList = itemProcessFileService.getFileListByItemId(itemProcess
					.getId());
		}
		// 获取当前用户
		User lgUser = this.getLoginUser();

		request.setAttribute("User", lgUser);
		request.setAttribute("ItemProcess", itemProcess);
		request.setAttribute("Item", item);
		request.setAttribute("FileList", fileList);
		return "web/vision/incorrupt/opinion";
	}

	/**
	 * 跳转到被监察对象录入会议决策
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/toSaveDecision.do")
	@RequiresPermissions("vision/incorrupt/toSaveDecision.do")
	public String toSaveDecision(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {

		Item item = itemService.selectByPrimaryKey(id);
		if (item.getPreparerTime() != null) {
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item
					.getPreparerTime()));
		}

		List<ItemProcess> itemProcessList = itemProcessService
				.getItemProcessItemId(item.getId());
		ItemProcess itemProcess = new ItemProcess();
		if (itemProcessList.size() > 0) {
			itemProcess = itemProcessList.get(0);
		}

		List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
		if (itemProcess.getId() != null) {
			fileList = itemProcessFileService.getFileListByItemId(itemProcess
					.getId());
		}
		// 获取当前用户
		User lgUser = this.getLoginUser();

		request.setAttribute("User", lgUser);
		request.setAttribute("ItemProcess", itemProcess);
		request.setAttribute("Item", item);
		request.setAttribute("FileList", fileList);
		return "web/vision/incorrupt/decision";
	}

	/**
	 * 跳转到被监察对象录入执行情况
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/toExecution.do")
	@RequiresPermissions("vision/incorrupt/toExecution.do")
	public String toExecution(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {

		Item item = itemService.selectByPrimaryKey(id);
		if (item.getPreparerTime() != null) {
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item
					.getPreparerTime()));
		}

		List<ItemProcess> itemProcessList = itemProcessService
				.getItemProcessItemId(item.getId());
		ItemProcess itemProcess = new ItemProcess();
		if (itemProcessList.size() > 0) {
			itemProcess = itemProcessList.get(0);
		}

		List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
		if (itemProcess.getId() != null) {
			fileList = itemProcessFileService.getFileListByItemId(itemProcess
					.getId());
		}
		// 获取当前用户
		User lgUser = this.getLoginUser();

		request.setAttribute("User", lgUser);
		request.setAttribute("ItemProcess", itemProcess);
		request.setAttribute("Item", item);
		request.setAttribute("FileList", fileList);
		return "web/vision/incorrupt/execution";
	}

	/**
	 * 跳转到监察室监察执行情况
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/toJCExecution.do")
	@RequiresPermissions("vision/incorrupt/toJCExecution.do")
	public String toJCExecution(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {

		Item item = itemService.selectByPrimaryKey(id);
		if (item.getPreparerTime() != null) {
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item
					.getPreparerTime()));
		}

		List<ItemProcess> itemProcessList = itemProcessService
				.getItemProcessItemId(item.getId());
		ItemProcess itemProcess = new ItemProcess();
		if (itemProcessList.size() > 0) {
			itemProcess = itemProcessList.get(0);
		}

		List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
		if (itemProcess.getId() != null) {
			fileList = itemProcessFileService.getFileListByItemId(itemProcess
					.getId());
		}
		// 获取当前用户
		User lgUser = this.getLoginUser();

		request.setAttribute("User", lgUser);
		request.setAttribute("ItemProcess", itemProcess);
		request.setAttribute("Item", item);
		request.setAttribute("FileList", fileList);
		return "web/vision/incorrupt/JCexecution";
	}

	/**
	 * 被监察对象录入方案
	 * 
	 * @param itemProcess
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveItemScheme.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/incorrupt/jsonSaveItemScheme.do")
	public JsonResult<ItemProcess> jsonSaveOrUpdateFileItem(
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
			itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_1);// 被监察对象录入项目方案
			itemProcessService.insert(itemProcess);

			Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
			item.setLasgTag(Constants.INCORRUPT_VISION_1);
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_LZJC, "用户："+loginUser.getName()+"，录入了廉政监察项目方案", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
			
			js.setCode(0);
			js.setMessage("录入方案成功!");
		} catch (Exception ex) {
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
	}

	/**
	 * 提出监察意见，并选择合规或不合规
	 * 
	 * @param itemProcess
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonsaveOpinion.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/incorrupt/jsonsaveOpinion.do")
	public JsonResult<ItemProcess> jsonsaveOpinion(ItemProcess itemProcess,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "yijian", required = false) Integer yijian,
			HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
		// 新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		// 获取当前登录用户
		User u = this.getLoginUser();
		js.setCode(new Integer(1));
		js.setMessage("保存信息失败!");
		try {
			// 获取当前用户所属的机构id，当做制单部门的ID
			List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u
					.getId());
			itemProcess.setPreparerOrgId(userOrgIDs.get(0)); // 制单部门的ID
			itemProcess.setOrgId(userOrgIDs.get(0)); // 制单部门的ID
			itemProcess.setPreparerId(u.getId());
			itemProcess.setPreparerTime(new Date());
			itemProcess.setDefined(false);
			if (status == null && yijian == null) {
				itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_444); // 监察室录入问责资料
			}else if(status != null){
				if (status == 4) {
					itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_2);// 项目合规，进入被监察对象录入会议决策内容
				} else if (status == 0) {
					itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_00); // 监察室给出监察项目方案意见，但是方案不合规，进入重新录入方案流程
				} else if (status == 5) {
					itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_6); // 监察室给出监察意见，但是有异议，进入被监察对象提请党委参考监察意见
				} else if (status == 6) {
					itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_4); // 监察室给出监察意见，并且无异议，进入被监察对象录入执行情况
				}
			}else if(yijian!=null){
				if (yijian == 1) {
					itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_9); // 党委采纳意见重新决策，回到被监察对象重新录入会议决策内容
					itemProcess.setContent("党委采纳意见，重新决策");
				}else if (yijian == 0) {
					itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_10); // 党委维持原决议
					itemProcess.setContent("党委维持原决议");
				}
			}
			
			
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_LZJC, "用户："+loginUser.getName()+"，执行了对廉政监察项目的流程操作", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
			
			itemProcessService.insert(itemProcess);

			// Item item =
			// itemService.selectByPrimaryKey(itemProcess.getItemId());
			// item.setLasgTag(Constants.INCORRUPT_VISION_3);

			js.setCode(0);
			js.setMessage("保存信息成功!");
		} catch (Exception ex) {
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
	}

	/**
	 * 被监察对象录入会议监察内容
	 * 
	 * @param itemProcess
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonsaveDecision.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/incorrupt/jsonsaveDecision.do")
	public JsonResult<ItemProcess> jsonsaveDecision(ItemProcess itemProcess,
			HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
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
			itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_33);// 被监察对象录入会议决策后监察室提出监察意见
			
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_LZJC, "用户："+loginUser.getName()+"，执行了对廉政监察项目的流程操作", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
			
			itemProcessService.insert(itemProcess);

			// Item item =
			// itemService.selectByPrimaryKey(itemProcess.getItemId());
			// item.setLasgTag(Constants.INCORRUPT_VISION_33);

			js.setCode(0);
			js.setMessage("保存信息成功!");
		} catch (Exception ex) {
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
	}

	/**
	 * 被监察对象录入执行情况
	 * 
	 * @param itemProcess
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveExecution.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/incorrupt/jsonSaveExecution.do")
	public JsonResult<ItemProcess> jsonSaveExecution(ItemProcess itemProcess,
			HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
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
			itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_5);// 监察室监察执行情况
			
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_LZJC, "用户："+loginUser.getName()+"，执行了对廉政监察项目的流程操作", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);	
			
			itemProcessService.insert(itemProcess);

			js.setCode(0);
			js.setMessage("保存信息成功!");
		} catch (Exception ex) {
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
	}

	/**
	 * 监察室监察执行情况
	 * 
	 * @param itemProcess
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveJCExecution.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/incorrupt/jsonSaveJCExecution.do")
	public JsonResult<ItemProcess> jsonSaveJCExecution(ItemProcess itemProcess,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "wenze", required = false) Integer wenze,
			HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
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
			if (status == 4) {
				itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_7); // 监察执行情况合规，进入监察室给出监察结论
			} else if (status == 0) {
				// 项目不合规，判断是否问责
				if (wenze == 0) {// 不问责
					itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_44); // 监察室提出执行情况不合规，但是不问责，回到被监察对象录入执行情况
				} else {
					// 问责
					itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_55); // 监察室提出执行情况不合规，并且进行问责，进入监察室录入问责资料流程
				}
			}
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_LZJC, "用户："+loginUser.getName()+"，执行了对廉政监察项目的流程操作", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);	
			
			itemProcessService.insert(itemProcess);
			js.setCode(0);
			js.setMessage("保存信息成功!");
		} catch (Exception ex) {
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
	}

	/**
	 * 监察室给出监察结论，完结项目
	 * 
	 * @param itemProcess
	 * @param status
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveJianChaJieLun.do", method = RequestMethod.POST)
	@RequiresPermissions("vision/incorrupt/jsonSaveJianChaJieLun.do")
	public JsonResult<ItemProcess> jsonSaveJianChaJieLun(
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

			itemProcess.setContentTypeId(Constants.INCORRUPT_VISION_8); // 监察室给出监察结论,项目完结
			Item item = itemService.selectByPrimaryKey(itemProcess.getItemId());
			if(item != null){
				if(item.getStatus() == 3){
					item.setStatus(5);
				}else{
					item.setStatus(4);
				}
				itemService.updateByPrimaryKeySelective(item);
			}
			User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_LZJC, "用户："+loginUser.getName()+"，对廉政监察项目给出监察结论", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);	
			
			itemProcessService.insert(itemProcess);
			js.setCode(0);
			js.setMessage("保存信息成功!");
		} catch (Exception ex) {
			js.setMessage("保存数据出错!");
			ex.printStackTrace();
		}
		return js;
	}

	/**
	 * 廉政监察所有流程
	 * 
	 * @param item
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/incorruptFile.do")
	@RequiresPermissions("vision/incorrupt/incorruptFile.do")
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
				if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_0) {
					request.setAttribute("ItemProcess", ip); // 新建状态
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_1) {
					request.setAttribute("ItemProcess2", ip); // 被监察对象录入项目方案
				}
				if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_00) {
					request.setAttribute("ItemProcess0", ip); // 已经给出监察意见，方案不合规，回到被监察对象录入方案
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_2) {
					request.setAttribute("ItemProcess3", ip); // 监察室给出监察意见，并且项目合规
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_4) {
					request.setAttribute("ItemProcess4", ip); // 监察室给出监察意见，并且无异议
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_5) {
					request.setAttribute("ItemProcess5", ip); // 被监察对象已经录入执行情况
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_6) {
					request.setAttribute("ItemProcess8", ip); // 监察室给出监察意见，但是无异议
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_7) {
					request.setAttribute("ItemProcess7", ip); // 监察室已经录入监察执行情况，并且合规
				} else if (ip.getContentTypeId() == 666) {
					request.setAttribute("ItemProcess12", ip); // 监察室录入问责资料
				} else if (ip.getContentTypeId() == 777) {
					request.setAttribute("ItemProcess6", ip); // 被监察对象录入会议决策内容
				} else if (ip.getContentTypeId() == 778) {
					request.setAttribute("ItemProcess10", ip); // 需要问责，问责前一节点的监察意见
				} else if (ip.getContentTypeId() == 779) {
					request.setAttribute("ItemProcess11", ip); // 已经给出监察意见，执行情况不合规，但是不问责，回到被监察对象录入执行情况
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_9) {
					request.setAttribute("ItemProcess13", ip); // 党委意见，重新决策
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_10) {
					request.setAttribute("ItemProcess14", ip); // 给出监察结论，项目完结
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

		if (tag == 72) {
			return "web/vision/incorrupt/itemScheme"; // 到被监察对象录入方案页面
		}
		if (tag == 73) {
			return "web/vision/incorrupt/decision"; // 到被监察对象录入会议决策
		}
		if (tag == 74) {
			return "web/vision/incorrupt/opinion"; // 到监察室提出监察意见
		}
		if (tag == 75) {
			return "web/vision/incorrupt/execution"; // 到被监察对象录入执行情况
		}
		if (tag == 76) {
			return "web/vision/incorrupt/JCexecution"; // 到监察室监察执行情况
		}
		if (tag == 77) {
			return "web/vision/incorrupt/shenYi"; // 到监察室监察执行情况
		}
		if (tag == 78) {
			return "web/vision/incorrupt/jianChaJieLun"; // 到监察室给出监察结论
		}
		if (tag == 86) {
			return "web/vision/incorrupt/jianChaJieLunA"; // 到监察室给出监察结论
		}
		if (tag == 777) {
			return "web/vision/incorrupt/advice"; // 到监察室对会议决策内容给出意见
		}
		if (tag == 778) {
			return "web/vision/incorrupt/wenZe"; // 到监察室录入问责资料
		}
		return "";
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
	@RequiresPermissions("vision/incorrupt/showItem.do")
	public String showItem(Item item, HttpServletRequest request,
			HttpServletResponse response) {
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
				if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_0) {
					request.setAttribute("ItemProcess", ip); // 监察内容
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_1) {
					request.setAttribute("ItemProcess2", ip); // 已经录入内容
				}
				if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_00) {
					List<ItemProcess> list = new ArrayList<ItemProcess>(); // 这里处理了循环不合规的情况
					for (ItemProcess iplt : itemProcessList) {
						if (iplt.getContentTypeId() == Constants.INCORRUPT_VISION_1) {
							list.add(iplt);
						}
					}
					ItemProcess itemProcess = itemProcessList
							.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (list.size() <= 1
							|| itemProcess.getContentTypeId() == Constants.INCORRUPT_VISION_00) {
						request.setAttribute("ItemProcess0", ip); // 已经给出监察意见，方案不合规，回到被监察对象录入方案
					} else {
						request.setAttribute("ItemProcess0", null); // 已经重新上传方案，上一次的监察意见不显示
					}
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_2) {
					request.setAttribute("ItemProcess3", ip); // 已经给出监察意见，并且方案合规
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_4) {
					request.setAttribute("ItemProcess4", ip); // 已经给出监察意见，并且无异议
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_9) {					
					ItemProcess itemProcess = itemProcessList.get(itemProcessList.size() - 1); // 获取最后一个元素		
					if (itemProcess.getContentTypeId() == Constants.INCORRUPT_VISION_9) {
						request.setAttribute("ItemProcess13", ip); // 党委意见，重新决策
					}
					
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_5) {
					request.setAttribute("ItemProcess5", ip); // 被监察对象已经录入执行情况
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_6) {// 有异议，提请党委重新决策
				 ItemProcess itemProcess = itemProcessList.get(itemProcessList.size()-1);
				 
				 if(itemProcess.getContentTypeId()==Constants.INCORRUPT_VISION_6){
					 request.setAttribute("ItemProcess8", ip); //已经给出监察意见，方案不合规，回到被监察对象录入方案
				 }else {
				 request.setAttribute("ItemProcess8", null);
				 //已经重新上传方案，上一次的监察意见不显示
				 }
					//request.setAttribute("ItemProcess8", ip);
				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_7) {
					request.setAttribute("ItemProcess7", ip); // 监察室监察已经录入的执行情况，并且合规
				} else if (ip.getContentTypeId() == 666) {
					ItemProcess itemProcess = itemProcessList
							.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (itemProcess.getContentTypeId() == 666) {
						request.setAttribute("ItemProcess12", ip); // 监察室录入问责资料
					}
				} else if (ip.getContentTypeId() == 777) {
					request.setAttribute("ItemProcess6", ip); // 被监察对象已经录入会议决策
				} else if (ip.getContentTypeId() == 778) {
					ItemProcess itemProcess = itemProcessList
							.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (itemProcess.getContentTypeId() == 778) {
						request.setAttribute("ItemProcess10", ip); // 需要问责，问责前一节点的监察意见
					}

				} else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_8) {
					request.setAttribute("ItemProcess9", ip); // 给出监察结论，项目完结
				} else if (ip.getContentTypeId() == 779) {
					// 这里处理了循环不合规，并且不问责的情况
					List<ItemProcess> list = new ArrayList<ItemProcess>();
					for (ItemProcess iplt : itemProcessList) {
						if (iplt.getContentTypeId() == Constants.INCORRUPT_VISION_5) {
							list.add(iplt); // 获取状态为被监察对象录入执行情况的记录
						}
					}
					ItemProcess itemProcess = itemProcessList
							.get(itemProcessList.size() - 1); // 获取最后一个元素
					if (list.size() <= 1
							|| itemProcess.getContentTypeId() == 779) {
						request.setAttribute("ItemProcess11", ip); // 已经给出监察意见，执行情况不合规，但是不问责，回到被监察对象录入执行情况
					} else {
						request.setAttribute("ItemProcess11", null); // 被监察对象已经重新录入执行情况，上一次的监察意见不显示
					}
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_10) {
					request.setAttribute("ItemProcess14", ip); // 给出监察结论，项目完结
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
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了对廉政监察项目的查看", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);	
		
		return "web/vision/incorrupt/showItem";
	}

}
