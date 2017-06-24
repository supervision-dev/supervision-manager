package com.rmbank.supervision.model;

import java.util.List;

import com.rmbank.supervision.common.utils.Page;

public class User extends Page{
    private Integer id;

    private String name;
    
    private String account;

    private String pwd;

    private String salt;

    private Integer used;      
    
    private String orgName;
    
    private String roleName;
    
    private boolean isSupervision; //是否监察部门
    
    private boolean isBranch; //是否分行机构
    
    private int userOrgID;//用户所属的机构ID    
    
    private boolean rememberMe;

    private int selectedMainMemu;

    private int selectedChildMenu;

    private List<FunctionMenu> childMenuList;

    private String oldPassword;

    private String newPassword;

    private String rePassword;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public int getSelectedMainMemu() {
		return selectedMainMemu;
	}

	public void setSelectedMainMemu(int selectedMainMemu) {
		this.selectedMainMemu = selectedMainMemu;
	}

	public int getSelectedChildMenu() {
		return selectedChildMenu;
	}

	public void setSelectedChildMenu(int selectedChildMenu) {
		this.selectedChildMenu = selectedChildMenu;
	}

	public List<FunctionMenu> getChildMenuList() {
		return childMenuList;
	}

	public void setChildMenuList(List<FunctionMenu> childMenuList) {
		this.childMenuList = childMenuList;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getRePassword() {
		return rePassword;
	}

	public void setRePassword(String rePassword) {
		this.rePassword = rePassword;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public boolean getIsSupervision() {
		return isSupervision;
	}

	public void setIsSupervision(boolean isSupervision) {
		this.isSupervision = isSupervision;
	}

	public boolean getIsBranch() {
		return isBranch;
	}

	public void setIsBranch(boolean isBranch) {
		this.isBranch = isBranch;
	}

	public int getUserOrgID() {
		return userOrgID;
	}

	public void setUserOrgID(int userOrgID) {
		this.userOrgID = userOrgID;
	}
}