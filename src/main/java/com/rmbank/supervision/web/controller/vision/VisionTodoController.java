package com.rmbank.supervision.web.controller.vision;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.ItemProcess;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ItemService;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;

/**
 * 实时监察待办事项控制器
 * @author DELL
 *
 */ 

@Scope("prototype")
@Controller
@RequestMapping("/vision")
public class VisionTodoController  extends  SystemAction {

	@Resource
	private ItemService itemService;

	@Resource
	private UserService userService;
	
	@Resource
	private OrganService organService;

	/**
     * 实时监察待办事项列表展示
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
	@ResponseBody
    @RequestMapping(value = "/todoList.do")
    public List<Item> efficiencyList(Item item, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
		//获取当前登录用户
		User loginUser = this.getLoginUser();
		//获取当前用户对应的机构列表
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		//获取当前用户对应的第一个机构
		Organ userOrg=userOrgList.get(0);
		// 分页集合
		List<Item> itemList = new ArrayList<Item>();
		try {
			//成都分行监察室和分行领导加载各个模块的所有待办事项
			if(userOrg.getOrgtype()==Constants.ORG_TYPE_1 ||
				userOrg.getOrgtype()==Constants.ORG_TYPE_4 ||
					Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
				
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListXNJCToList(item);	
			}else {
				//当前登录用户只加载自己完成的项目				
				item.setSupervisionOrgId(userOrg.getId());
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListToListByLogOrg(item);	

				//如果是中支监察还要加载当前中支监察室和中支办公室录入的事项
				List<Item> JCSitemList = new ArrayList<Item>();
				List<Item> BGSitemList = new ArrayList<Item>();
				if(userOrg.getOrgtype()== Constants.ORG_TYPE_7 || 
						userOrg.getOrgtype()== Constants.ORG_TYPE_12){
					Organ BGS = organService.getOrganByPidAndName(userOrg.getPid(), "办公室");
					item.setPreparerOrgId(BGS.getId());
					item.setSupervisionOrgId(null); //查询条件不加完成机构id
					item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
					BGSitemList = itemService.getItemListToListByLogOrg(item);
					
					item.setPreparerOrgId(userOrg.getId());
					item.setSupervisionOrgId(null); //查询条件不加完成机构id
					item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
					JCSitemList = itemService.getItemListByTypeAndLogOrg(item);
				}
				itemList.addAll(JCSitemList);
				itemList.addAll(BGSitemList);
				
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
    	return itemList;
    }
	
	
	/**
	 * 廉政监察待办事项
	 * @param item
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
    @RequestMapping(value = "/LZJCtodoList.do")
    public List<Item> LZJC_DBSX(Item item, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
		//获取当前登录用户
		User loginUser = this.getLoginUser();
		//获取当前用户对应的机构列表
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		//获取当前用户对应的第一个机构
		Organ userOrg=userOrgList.get(0);
		// 分页集合
		List<Item> itemList = new ArrayList<Item>();
		try {
			//成都分行监察室和分行领导加载各个模块的所有待办事项
			if(userOrg.getOrgtype()==Constants.ORG_TYPE_1 ||
					userOrg.getOrgtype()==Constants.ORG_TYPE_4 ||			
						Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
				
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListXNJCToList(item);	
			}else {
				//当前登录用户只加载自己录入的项目
				item.setPreparerOrgId(userOrg.getId());
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListToListByLogOrg(item);	
				
				//如果是中支监察室需要加载当前中支下其他所有部门包括县支行的录入的工作事项
				List<Item> BMItem = new ArrayList<Item>();
				if(userOrg.getOrgtype()==Constants.ORG_TYPE_7){
					//获取和当前登录的中支监察室在同一个中支下的所有部门。
					List<Organ> organByPId = organService.getOrganByPId(userOrg.getPid());
					for (Organ organ : organByPId) {
						item.setPreparerOrgId(organ.getId());
						BMItem = itemService.getItemListByTypeAndLogOrg(item);
						itemList.addAll(BMItem);
					}
				}

			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
    	return itemList;
    }
	
	
	/**
	 * 执法监察待办事项
	 * @param item
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
    @RequestMapping(value = "/ZFJCtodoList.do")
    public List<Item> ZFJC_DBSX(Item item, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
		//获取当前登录用户
		User loginUser = this.getLoginUser();
		//获取当前用户对应的机构列表
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		//获取当前用户对应的第一个机构
		Organ userOrg=userOrgList.get(0);
		// 分页集合
		List<Item> itemList = new ArrayList<Item>();
		try {
			//成都分行监察室和分行领导加载各个模块的所有待办事项
			if(userOrg.getOrgtype()==Constants.ORG_TYPE_1 ||
					userOrg.getOrgtype()==Constants.ORG_TYPE_4 ||			
						Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
				
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListXNJCToList(item);	
			}else {
				
				
				//如果是中支监察室则或中支依法行政领导小组办公室则需要加载当前中支下的依法行政领导小组办公室录入的工作事项
				List<Item> BMItem = new ArrayList<Item>();
				if(userOrg.getOrgtype()==Constants.ORG_TYPE_7 || 
						userOrg.getOrgtype()==Constants.ORG_TYPE_11){
					//获取和当前登录的中支监察室在同一个中支下的所有部门。
					Organ YFLDXZBGS = organService.getOrganByPidAndName(userOrg.getPid(), "依法行政领导小组办公室");
					item.setPreparerOrgId(YFLDXZBGS.getId());
					item.setSupervisionOrgId(null); //查询条件不加完成机构id
					item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
					itemList = itemService.getItemListToListByLogOrg(item);
				}else{
					//当前登录用户只加载自己完成的项目
					item.setSupervisionOrgId(userOrg.getId());
					item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
					itemList = itemService.getItemListToListByLogOrg(item);	
				}
				
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
    	return itemList;
    }
}
