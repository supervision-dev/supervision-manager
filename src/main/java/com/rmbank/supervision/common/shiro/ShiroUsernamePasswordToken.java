package com.rmbank.supervision.common.shiro;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import org.apache.shiro.authc.UsernamePasswordToken;

public class ShiroUsernamePasswordToken extends UsernamePasswordToken
{
  private static final long serialVersionUID = 811630853593090492L;
  private static final Log logger = LogFactory.getLog(ShiroUsernamePasswordToken.class);
  private String salt;
  private String base64Password;

  public ShiroUsernamePasswordToken()
  {
  }

  public ShiroUsernamePasswordToken(String username, String password, String basepassword, String salt, String host)
  {
    super(username, password, false, host);
    this.salt = salt;
    this.base64Password = basepassword;
  }
  public String getSalt() {
    return this.salt;
  }
  public void setSalt(String salt) {
    this.salt = salt;
  }
  public String getBase64Password() {
    return this.base64Password;
  }
  public void setBase64Password(String base64Password) {
    this.base64Password = base64Password;
  }
}