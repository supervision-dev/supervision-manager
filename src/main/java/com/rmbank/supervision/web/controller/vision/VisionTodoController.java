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
 * 效能监察待办事项控制器
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
     * 效能监察待办事项列表展示
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
			//成都分行和超级管理员获取所有项目
			if(userOrg.getOrgtype()==Constants.ORG_TYPE_1 ||
//					userOrg.getOrgtype()==Constants.ORG_TYPE_2 ||
//							userOrg.getOrgtype()==Constants.ORG_TYPE_3 ||
									userOrg.getOrgtype()==Constants.ORG_TYPE_4 ||
//											userOrg.getOrgtype()==Constants.ORG_TYPE_12 ||
					Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
				
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListXNJCToList(item);	
			}else {
				//当前登录用户只加载自己完成的项目				
				item.setSupervisionOrgId(userOrg.getId());
				item.setPreparerOrgId(userOrg.getId());
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemList = itemService.getItemListToListByLogOrg(item);	
				
				//如果是中支监察还要加载当前中支监察录入的事项和办公室录入事项
				List<Item> JCSitemList = new ArrayList<Item>();
				List<Item> BGSitemList = new ArrayList<Item>();
				if(userOrg.getOrgtype()== Constants.ORG_TYPE_7 || 
						userOrg.getOrgtype()== Constants.ORG_TYPE_12){
					Organ BGS = organService.getOrganByPidAndName(userOrg.getPid(), "办公室");
					item.setPreparerOrgId(BGS.getId());
					item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
					BGSitemList = itemService.getItemListByTypeAndLogOrg(item);
					
					item.setPreparerOrgId(userOrg.getId());
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
}
