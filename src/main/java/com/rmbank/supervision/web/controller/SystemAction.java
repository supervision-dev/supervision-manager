package com.rmbank.supervision.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.model.User;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

public abstract class SystemAction
{
  protected Logger log = Logger.getLogger(getClass());
  private String resultCode;
  private String resultMessage;
  private HttpServletRequest req;
  private HttpServletResponse res;

  public HttpServletRequest getReq()
  {
    return this.req;
  }

  public void setReq(HttpServletRequest req) {
    this.req = req;
  }

  public HttpServletResponse getRes() {
    return this.res;
  }

  public void setRes(HttpServletResponse res) {
    this.res = res;
  }

  public Object getSession(String key) {
    return SecurityUtils.getSubject().getSession().getAttribute(key);
  }

  public Session getSession() {
    return SecurityUtils.getSubject().getSession();
  }

  public String getResultCode() {
    return this.resultCode;
  }

  public void setResultCode(String resultCode) {
    this.resultCode = resultCode;
  }

  public String getResultMessage() {
    return this.resultMessage;
  }

  public void setResultMessage(String resultMessage) {
    this.resultMessage = resultMessage;
  }

  protected User getLoginUser()
  {
    User loginUser = (User)SecurityUtils.getSubject().getSession().getAttribute(Constants.USER_INFO);
    return loginUser;
  }

  protected void setLoginUser(User loginUser)
  {
    SecurityUtils.getSubject().getSession().setAttribute(Constants.USER_INFO, loginUser);
  }
}