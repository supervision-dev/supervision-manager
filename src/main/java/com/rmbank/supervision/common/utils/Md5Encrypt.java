package com.rmbank.supervision.common.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Encrypt
{
  private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', 
    '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  public static String md5(String text)
  {
    return md5(text, Charset.forName("utf8"));
  }

  public static String md5(String text, Charset charset)
  {
    try
    {
      MessageDigest msgDigest = MessageDigest.getInstance("MD5");
      msgDigest.update(text.getBytes(charset));
      return new String(encodeHex(msgDigest.digest())); } catch (NoSuchAlgorithmException e) {
    }
    throw new IllegalStateException("褰撳墠绯荤粺涓嶆敮鎸丮d5鍔犲瘑");
  }

  public static char[] encodeHex(byte[] data)
  {
    int l = data.length;
    char[] out = new char[l << 1];

    int i = 0; for (int j = 0; i < l; i++) {
      out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
      out[(j++)] = DIGITS[(0xF & data[i])];
    }
    return out;
  }

  public static void main(String[] args) {
    System.out.println(md5("ty189@2013admin"));
  }

  public static byte[] md5Byte(byte[] input) {
    try {
      MessageDigest msgDigest = MessageDigest.getInstance("MD5");
      msgDigest.update(input);
      return msgDigest.digest(); } catch (NoSuchAlgorithmException e) {
    }
    throw new IllegalStateException("System doesn't support MD5 algorithm.");
  }
}