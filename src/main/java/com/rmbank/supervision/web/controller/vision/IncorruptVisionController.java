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
 * 廉政监察控制器
 * 
 * @author DELL
 *
 */
@Scope("prototype")

@Controller
@RequestMapping("/vision/incorrupt")
public class IncorruptVisionController extends SystemAction {
	@Resource
	private ItemService itemService;
	@Resource
	private UserService userService;
	@Resource
	private OrganService organService;
	@Resource
	private ConfigService configService;
	@Resource
	private UserRoleService userRoleService;
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
	@ResponseBody
	@RequestMapping(value = "/incorruptList.do")
	@RequiresPermissions("vision/incorrupt/incorruptList.do")
	public DataListResult<Item> incorruptList(Item item, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		
		DataListResult<Item> dr = new DataListResult<Item>();
		
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
		List<Organ> userOrgList = userService.getUserOrgByUserId(loginUser.getId());
				
		// 获取当前用户对应的第一个机构
		Organ userOrg = userOrgList.get(0);
		
		//获取当前用户对应的角色
		List<Role> rolesByUserId = userRoleService.getRolesByUserId(loginUser.getId());
		Role userRole = rolesByUserId.get(0);
		
		item.setUserRole(userRole.getId());
		// 分页集合
		List<Item> itemList = new ArrayList<Item>();
		try {
			// 成都分行监察室和超级管理员加载所有的项目
			if (userOrg.getOrgtype()==Constants.ORG_TYPE_1 ||
					userOrg.getOrgtype()==Constants.ORG_TYPE_2 ||
						userOrg.getOrgtype()==Constants.ORG_TYPE_3 ||
							userOrg.getOrgtype()==Constants.ORG_TYPE_4 ||
								userOrg.getOrgtype()==Constants.ORG_TYPE_12||
					Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser
							.getAccount())) {
				// 取满足要求的参数数据
				item.setSupervisionTypeId(3);
				item.setPreparerOrgId(userOrg.getId());
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION);
				itemList = itemService.getItemListByType(item);
				// 取满足要求的记录总数
				totalCount = itemService.getItemCountBySSJC(item);
			} else {// 获取当前用户需要完成的项目
					// 取满足要求的参数数据
				/*List<Item> BGSitemList = new ArrayList<Item>();
				int BGStotalCount = 0;
				if(userOrg.getOrgtype()== Constants.ORG_TYPE_7){
					Organ BGS = organService.getOrganByPidAndName(userOrg.getPid(), "办公室");
					item.setSupervisionTypeId(2); //2代表效能监察
					item.setSupervisionOrgId(BGS.getId());
					item.setPreparerOrgId(BGS.getId());
					item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
					BGSitemList = itemService.getItemListByTypeAndLogOrg(item);
					BGStotalCount = itemService.getItemCountByLogOrgSSJC(item); //实时监察分页
				}*/
				item.setSupervisionTypeId(3);
				item.setPreparerOrgId(userOrg.getId());
				item.setSupervisionOrgId(userOrg.getId());
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION);
				itemList = itemService.getItemListByTypeAndLogOrg(item);
				// 取满足要求的记录总数
				totalCount = itemService.getItemCountByLogOrgSSJC(item);
			}
			
			for (Item it : itemList) {
				//设置流程节点
				List<ItemProcess> itemprocessList = itemProcessService.getItemProcessItemId(it.getId());		
				if (itemprocessList.size() > 0) {
					ItemProcess lastItem = itemprocessList.get(itemprocessList.size() - 1);							
					it.setLasgTag(lastItem.getContentTypeId());
				}
				//将登录机构的类型添加到项目中
				it.setOrgType(userOrg.getOrgtype());
				
				//将登陆用户的角色id添加到项目中
				it.setUserRole(userRole.getId());
				
				//将登陆用户的机构Id添加
				it.setLogOrgId(userOrg.getId());
			}

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
	 * 被监察对象录入工作事项
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
		
		OrgIds[0] =userOrgIDs.get(0);
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

		HttpSession session = request.getSession();
    	Integer sessionItemId = (Integer) session.getAttribute("incorruptItemId");
    	item.setId(sessionItemId);
		// 新建一个json对象 并赋初值
		JsonResult<Item> js = new JsonResult<Item>();
		js.setCode(new Integer(1));
		js.setMessage("立项失败!");
		try {
			if (item != null && item.getId() > 0) {
				Item temp = itemService.selectByPrimaryKey(sessionItemId);
				if (temp != null) {
					temp.setSuperItemType(item.getSuperItemType());
					temp.setStatus(1);
					temp.setEndTime(new Date());
					
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
		
		HttpSession session = request.getSession();
    	Integer sessionItemId = (Integer) session.getAttribute("incorruptItemId");
		
		// 新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		// 获取当前登录用户
		User u = this.getLoginUser();
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");
		try {
			// 获取当前用户所属的机构id，当做制单部门的ID
			List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
					
			itemProcess.setItemId(sessionItemId);
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
		
		HttpSession session = request.getSession();
    	Integer sessionItemId = (Integer) session.getAttribute("incorruptItemId");
		
		
		// 新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		// 获取当前登录用户
		User u = this.getLoginUser();
		js.setCode(new Integer(1));
		js.setMessage("保存信息失败!");
		try {
			// 获取当前用户所属的机构id，当做制单部门的ID
			List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
			itemProcess.setItemId(sessionItemId);		
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
		
		HttpSession session = request.getSession();
    	Integer sessionItemId = (Integer) session.getAttribute("incorruptItemId");
		
		// 新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		// 获取当前登录用户
		User u = this.getLoginUser();
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");
		try {
			// 获取当前用户所属的机构id，当做制单部门的ID
			List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
					
			itemProcess.setItemId(sessionItemId);
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
		
		HttpSession session = request.getSession();
    	Integer sessionItemId = (Integer) session.getAttribute("incorruptItemId");
		
		
		// 新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		// 获取当前登录用户
		User u = this.getLoginUser();
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");
		try {
			// 获取当前用户所属的机构id，当做制单部门的ID
			List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
					
			itemProcess.setItemId(sessionItemId);
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
		
		HttpSession session = request.getSession();
    	Integer sessionItemId = (Integer) session.getAttribute("incorruptItemId");
		
		
		// 新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		// 获取当前登录用户
		User u = this.getLoginUser();
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");
		try {
			// 获取当前用户所属的机构id，当做制单部门的ID
			List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
			itemProcess.setItemId(sessionItemId);		
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
		
		HttpSession session = request.getSession();
    	Integer sessionItemId = (Integer) session.getAttribute("incorruptItemId");
		
		
		// 新建一个json对象 并赋初值
		JsonResult<ItemProcess> js = new JsonResult<ItemProcess>();
		// 获取当前登录用户
		User u = this.getLoginUser();
		js.setCode(new Integer(1));
		js.setMessage("保存项目信息失败!");
		try {
			// 获取当前用户所属的机构id，当做制单部门的ID
			List<Integer> userOrgIDs = userService.getUserOrgIdsByUserId(u.getId());
					
			itemProcess.setItemId(sessionItemId);
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
	 * 查看项目
	 * 
	 * @param item
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/showItem.do")
	@RequiresPermissions("vision/incorrupt/showItem.do")
	public BaseItemResult showItem(
			HttpServletRequest request,HttpServletResponse response) {

		HttpSession session = request.getSession();
    	Integer itemId = (Integer) session.getAttribute("incShowItemId");
    	
    	if(itemId==null){
    		itemId = (Integer) session.getAttribute("incorruptItemId");
    	}
    	
    	BaseItemResult showResult = new BaseItemResult();
    	List<ItemProcess> drIPList=new ArrayList<ItemProcess>();
    	
    	Item item = itemService.selectByPrimaryKey(itemId);

    	
		if (item.getPreparerTime() != null) {
			item.setPreparerTimes(Constants.DATE_FORMAT.format(item.getPreparerTime()));		
		}
		List<ItemProcess> itemProcessList = itemProcessService.getItemProcessItemId(item.getId());
		if (itemProcessList.size() > 0) {
			for (ItemProcess ip : itemProcessList) {
				List<ItemProcessFile> fileList = new ArrayList<ItemProcessFile>();
				fileList = itemProcessFileService.getFileListByItemId(ip.getId());
				ip.setFileList(fileList);
				
				if(ip.getPreparerTime() != null){
					ip.setPreparerTimes(Constants.DATE_FORMAT.format(ip.getPreparerTime()));
				}
				
				if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_0) {
					drIPList.add(ip);//录入工作事项
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_1) {
					drIPList.add(ip); //已经录入方案
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_2) {
					drIPList.add(ip);  // 已经给出监察意见，并且方案合规
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_00) {
					drIPList.add(ip);  // 已经给出监察意见，但是方案不合规
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_33.intValue()) {
					drIPList.add(ip);  // 已经会议决策内容
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_4) {					
					drIPList.add(ip);  // 录入监察会议决策内容意见，并且无异议 
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_6) {
					drIPList.add(ip);  // 录入监察会议决策内容意见，但是有异议 
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_9) {
					drIPList.add(ip);  // 已经提请党委审议，党委意见为采纳意见，重新决策
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_10) {
					drIPList.add(ip);  // 已经提请党委审议，党委意见为维持原决议
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_8) {
					drIPList.add(ip);  // 已经录入监察结论，项目完结
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_5) {
					drIPList.add(ip);  // 已经录入执行情况
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_7) {
					drIPList.add(ip);  // 已经录入监察执行情况意见，并且合规
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_44.intValue()) {
					drIPList.add(ip);  // 已经录入监察执行情况意见，但是不合规，也不问责
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_55.intValue()) {
					drIPList.add(ip);  // 已经录入监察执行情况意见，但是不合规，且问责
				}else if (ip.getContentTypeId() == Constants.INCORRUPT_VISION_444.intValue()) {
					drIPList.add(ip);  // 已经录入问责相关资料
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
		
		showResult.setResultItem(item);
		showResult.setResultItemProcess(drIPList); 
    	return showResult;
	}

}
