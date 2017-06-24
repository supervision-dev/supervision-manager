package com.rmbank.supervision.common.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.rmbank.supervision.model.User;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;

public final class EndecryptUtils
{
  public static User md5Password(String username, String password)
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(username), "username涓嶈兘涓虹┖");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "password涓嶈兘涓虹┖");
    SecureRandomNumberGenerator secureRandomNumberGenerator = new SecureRandomNumberGenerator();
    String salt = secureRandomNumberGenerator.nextBytes().toHex();

    String password_cipherText = new Md5Hash(password, username + salt, 2).toHex();
    User user = new User();
    user.setPwd(password_cipherText);
    user.setSalt(salt);
    user.setName(username);
    return user;
  }

  public static boolean checkMd5Password(String username, String password, String salt, String md5cipherText)
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(username), "username涓嶈兘涓虹┖");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "password涓嶈兘涓虹┖");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(md5cipherText), "md5cipherText涓嶈兘涓虹┖");

    String password_cipherText = new Md5Hash(password, username + salt, 2).toHex();
    return md5cipherText.equals(password_cipherText);
  }
}