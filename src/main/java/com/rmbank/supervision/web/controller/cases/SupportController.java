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
//		User loginUser = this.getLoginUser();
		User loginUser =new User();
		loginUser.setId(1);
		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Integer logUserOrg = userOrgByUserId.get(0).getId(); //当前登录用户所属的机构ID
		Organ organ = userOrgByUserId.get(0);
		//获取项目列表,根据不同的机构类型加载不同的项目
		List<Item> itemList =null;
		if(organ.getOrgtype()==Constants.ORG_TYPE_1 ||
				organ.getOrgtype()==Constants.ORG_TYPE_2 ||
				organ.getOrgtype()==Constants.ORG_TYPE_3 ||
				Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
			//成都分行监察室和超级管理员加载所有的中支立项项目
			//itemList=itemService.getItemList(item);
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setOrgTypeA(Constants.ORG_TYPE_6);			
			itemList=itemService.getItemListByOrgType(item);
			totalCount=itemService.getItemCountZZLXALL(item);
		}else{
			//当前登录机构只加载当前登录中支立的项目和子机构完成的项目
			item.setItemType(Constants.STATIC_ITEM_TYPE_MANAGE);
			item.setOrgTypeA(Constants.ORG_TYPE_6);	
			item.setOrgTypeB(Constants.ORG_TYPE_5);
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
	 * 加载所有项目类型
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
	
} 
