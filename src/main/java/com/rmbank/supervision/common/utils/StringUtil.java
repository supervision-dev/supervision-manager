package com.rmbank.supervision.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil
{
  public static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', 
    '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  public static boolean isEmpty(String sourceString)
  {
    if (sourceString == null)
      return true;
    if (sourceString.trim().length() == 0) {
      return true;
    }
    return false;
  }

  public static String trim(String sourceString)
  {
    if (sourceString == null) {
      return "";
    }
    return sourceString.trim();
  }

  public static String leftTrim(String sourceString)
  {
    if (sourceString == null) {
      return "";
    }
    String regEx = "^\\s{1,}";
    Pattern pat = Pattern.compile(regEx);
    Matcher mat = pat.matcher(sourceString);
    String result = mat.replaceAll("");
    return result;
  }

  public static String rightTrim(String sourceString)
  {
    if (sourceString == null) {
      return "";
    }
    String regEx = "\\s{1,}$";
    Pattern pat = Pattern.compile(regEx);
    Matcher mat = pat.matcher(sourceString);
    String result = mat.replaceAll("");
    return result;
  }

  public static boolean isEmail(String emailString)
  {
    if (isEmpty(emailString)) {
      return false;
    }
    String regEx = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    Pattern pat = Pattern.compile(regEx);
    Matcher mat = pat.matcher(emailString);
    return mat.matches();
  }

  public static String toMD5(String sourceString)
  {
    MessageDigest md5 = null;
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
    if (isEmpty(sourceString)) {
      return null;
    }
    byte[] md = md5.digest(sourceString.getBytes());
    char[] c = new char[md.length * 2];
    int k = 0;

    for (int i = 0; i < md.length; i++) {
      byte b = md[i];
      c[(k++)] = HEX_DIGITS[(b >> 4 & 0xF)];
      c[(k++)] = HEX_DIGITS[(b & 0xF)];
    }
    return new String(c);
  }

  public static String dateToString(Date date, String pattern)
  {
    if ((date == null) || (isEmpty(pattern))) {
      return "1900-01-01";
    }
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    return sdf.format(date);
  }

  public static String dateToString(Date date)
  {
    if (date == null) {
      return "1900-01-01";
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    return sdf.format(date);
  }

  public static boolean matche(String sourceString, String targetString)
  {
    if ((sourceString == null) || (targetString == null)) {
      return false;
    }
    if (sourceString.length() != targetString.length()) {
      return false;
    }
    if (sourceString.equals(targetString)) {
      return true;
    }
    return false;
  }

  public static String transformEncode(String sourceString, String targerCharset)
    throws UnsupportedEncodingException
  {
    if ((isEmpty(sourceString)) || (isEmpty(sourceString))) {
      return null;
    }

    String result = new String(sourceString.getBytes(targerCharset), targerCharset);
    return result;
  }

  public static boolean isMobileNumber(String number)
  {
    if (isEmpty(number)) {
      return false;
    }
    String regEx = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    Pattern pat = Pattern.compile(regEx);
    Matcher mat = pat.matcher(number);
    return mat.matches();
  }

  public static boolean isIp(String ip)
  {
    if (isEmpty(ip)) {
      return false;
    }
    String regEx = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
    Pattern pat = Pattern.compile(regEx);
    Matcher mat = pat.matcher(ip);
    return mat.matches();
  }
}