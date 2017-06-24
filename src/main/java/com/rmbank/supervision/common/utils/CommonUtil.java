package com.rmbank.supervision.common.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Double;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.methods.MultipartPostMethod;
//import org.apache.commons.httpclient.methods.multipart.FilePart;
//import org.apache.commons.httpclient.methods.multipart.StringPart;

public class CommonUtil
{
  private static String web_service_url = "/AppServerService_war/services/ServerService?wsdl";
//  private static String web_service_url = "/services/ServerService?wsdl";

  public static Map<String, Object> classToMap(Object obj)
  {
    if (obj == null)
      return new HashMap();
    try
    {
      return PropertyUtils.describe(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }return new HashMap();
  }

  public static String getRandomPassword()
  {
    return RandomStringUtils.random(6, true, true);
  }

  public static String arrayToString(Object[] array)
  {
    StringBuffer str = new StringBuffer("{");
    if (array != null)
    {
      for (int i = 0; i < array.length; i++)
      {
        str.append(array[i]);

        if (i != array.length - 1) {
          str.append(",");
        }
      }
    }
    str.append("}");

    return str.toString();
  }

  public static String getGBStr(String str)
  {
    if ((str == null) || (str.trim().equalsIgnoreCase("")))
      return null;
    try {
      String temp_p = str;
      return new String(temp_p.getBytes("ISO8859_1"), "GBK");
    } catch (Exception e) {
    }
    return null;
  }

  public static String getUtf8Str(String str)
  {
    if ((str == null) || (str.trim().equalsIgnoreCase("")))
      return null;
    try {
      String temp_p = str;
      return new String(temp_p.getBytes("GBK"), "UTF-8");
    } catch (Exception e) {
    }
    return null;
  }

  public static String getRandom(int count)
  {
    String sRand = "";
    Random random = new Random();
    for (int i = 0; i < count; i++) {
      String rand = String.valueOf(random.nextInt(10));
      sRand = sRand + rand;
    }
    return sRand;
  }

  public static String getNotNullStr(String param) {
    if (param == null) {
      return "";
    }
    return param;
  }

  public static String getFileType(String fileFullName) {
    if (StringUtils.isNotBlank(fileFullName)) {
      int start = StringUtils.lastIndexOf(fileFullName, ".");
      return StringUtils.substring(fileFullName, start + 1,
        fileFullName.length());
    }
    return "";
  }

  public static String getFileName(String fileFullName)
  {
    if (StringUtils.isNotBlank(fileFullName)) {
      int start = StringUtils.lastIndexOf(fileFullName, ".");
      return StringUtils.substring(fileFullName, 0, start);
    }
    return "";
  }

  public static String getDateFormat(Date date)
  {
    if (date != null) {
      SimpleDateFormat dateformat = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
      String resultDateStr = dateformat.format(date);
      return resultDateStr;
    }
    return "";
  }

  public static String getDateFormat(Date date, String format)
  {
    if (date != null) {
      SimpleDateFormat dateformat = new SimpleDateFormat(format);
      String resultDateStr = dateformat.format(date);
      return resultDateStr;
    }
    return "";
  }

  public static Date parse(String date, SimpleDateFormat format)
  {
    if (StringUtils.isEmpty(date))
      return null;
    try
    {
      return format.parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }return null;
  }

  public static int isPicture(String fileName)
  {
    String fileType = getFileType(fileName);
    if ((StringUtils.equalsIgnoreCase(fileType, "BMP")) ||
      (StringUtils.equalsIgnoreCase(fileType, "GIF")) ||
      (StringUtils.equalsIgnoreCase(fileType, "JPEG")) ||
      (StringUtils.equalsIgnoreCase(fileType, "JPG")) ||
      (StringUtils.equalsIgnoreCase(fileType, "PNG")))
      return 0;
    if (StringUtils.equalsIgnoreCase(fileType, "AMR")) {
      return 2;
    }
    return 1;
  }

  public static int isWord(String fileName)
  {
    String fileType = getFileType(fileName);
    if ((StringUtils.equalsIgnoreCase(fileType, "DOC")) ||
      (StringUtils.equalsIgnoreCase(fileType, "DOCX"))) {
      return 0;
    }
    return 1;
  }

  public static int isExcel(String fileName)
  {
    String fileType = getFileType(fileName);
    if ((StringUtils.equalsIgnoreCase(fileType, "XLS")) ||
      (StringUtils.equalsIgnoreCase(fileType, "XLSX"))) {
      return 0;
    }
    return 1;
  }

  public static int isZip(String fileName)
  {
    String fileType = getFileType(fileName);
    if ((StringUtils.equalsIgnoreCase(fileType, "ZIP")) ||
      (StringUtils.equalsIgnoreCase(fileType, "RAR")) ||
      (StringUtils.equalsIgnoreCase(fileType, "7Z"))) {
      return 0;
    }
    return 1;
  }

  public static int isVideo(String fileName)
  {
    String fileType = getFileType(fileName);
    if ((StringUtils.equalsIgnoreCase(fileType, "3GP")) ||
      (StringUtils.equalsIgnoreCase(fileType, "MP4"))) {
      return 0;
    }
    return 1;
  }

  public static int isOther(String infileType, String fileName)
  {
    String fileType = getFileType(fileName);
    if ((StringUtils.equalsIgnoreCase(fileType, infileType.toUpperCase())) ||
      (StringUtils.equalsIgnoreCase(fileType,
      infileType.toUpperCase()))) {
      return 0;
    }
    return 1;
  }

  public static String getSystemPath(HttpServletRequest request)
  {
    System.out.println(request.getServerName() + ":::" + 
      request.getRemoteAddr());
    if (!request.getServerName().startsWith("www")) {
      return request.getScheme() + "://" + request.getServerName() + ":" + 
        request.getServerPort() + request.getContextPath();
    }
    return request.getScheme() + "://" + request.getServerName() + 
      request.getContextPath();
  }

  public static boolean isMobile(String str)
  {
    Pattern p = null;
    Matcher m = null;
    boolean b = false;
    p = Pattern.compile("^[1][3,5,7,8][0-9]{9}$");
    m = p.matcher(str);
    b = m.matches();
    return b;
  }

  public static String postFile(String actionUrl, Map<String, String> map, Map<String, File> fileMap) throws IOException
  {
   /* String resultStr = "";
    HttpClient client = new HttpClient();

    String BOUNDARY = UUID.randomUUID().toString();
    String MULTIPART_FROM_DATA = "multipart/form-data";
    MultipartPostMethod postMethod = new MultipartPostMethod(actionUrl);
    postMethod.setRequestHeader("Content-type", MULTIPART_FROM_DATA + 
      ";boundary=" + BOUNDARY);
    if ((map != null) && (!map.isEmpty())) {
      Iterator iter = map.entrySet()
        .iterator();
      while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry)iter.next();
        StringPart stringPart = new StringPart((String)entry.getKey(), 
          (String)entry.getValue(), "UTF-8");
        postMethod.addPart(stringPart);
      }
    }
    if ((fileMap != null) && (!fileMap.isEmpty())) {
      File targetFile = (File)fileMap.get("file");
      FilePart fp = new FilePart("file", targetFile, null, "UTF-8");
      File file = new File("d:\\Chrysanthemum.jpg");
      FilePart fp1 = new FilePart("file", file, null, "UTF-8");
      postMethod.addPart(fp);
      postMethod.addPart(fp1);
    }
    client.executeMethod(postMethod);
    resultStr = postMethod.getResponseBodyAsString();*/
//    return resultStr;
	  return "";
  }

  public static String Guid() {
    StringBuffer str = new StringBuffer();

    for (int i = 0; i < 1; i++)
    {
      UUID uuid = UUID.randomUUID();

      String a = uuid.toString();

      a = a.toUpperCase();

      a = a.replaceAll("-", "");

      str.append(a);
    }

    return str.toString();
  }

  public static boolean checkWithJdkGeneralPath(Double point, List<Double> polygon)
  {
    GeneralPath p = new GeneralPath();

    Double first = (Double)polygon.get(0);
    p.moveTo(first.x, first.y);

    for (Double d : polygon) {
      p.lineTo(d.x, d.y);
    }

    p.lineTo(first.x, first.y);

    p.closePath();

    return p.contains(point);
  }

  public static void transMap2Bean(Map<String, Object> map, Object obj)
  {
    try
    {
      BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

      for (PropertyDescriptor property : propertyDescriptors) {
        String key = property.getName();

        if (map.containsKey(key)) {
          Object value = map.get(key);

          Method setter = property.getWriteMethod();
          setter.invoke(obj, new Object[] { value });
        }
      }
    }
    catch (Exception e)
    {
      System.out.println("transMap2Bean Error " + e);
    }
  }

  public static Map<String, Object> transBean2Map(Object obj)
  {
    String sdfd = null;
    if (obj == null) {
      return null;
    }
    Map map = new HashMap();
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor property : propertyDescriptors) {
        String key = property.getName();
        sdfd = key;

        if (!key.equals("class"))
        {
          Method getter = property.getReadMethod();
          Object value = getter.invoke(obj, new Object[0]);

          map.put(key, value);
        }
      }
    }
    catch (Exception e) {
      System.out.println("transBean2Map Error " + e);
    }

    return map;
  }

  public static Date getStartTime(Date date)
  {
    Calendar todayStart = Calendar.getInstance();
    todayStart.setTime(date);
    todayStart.set(11, 0);
    todayStart.set(12, 0);
    todayStart.set(13, 0);
    todayStart.set(14, 0);
    return todayStart.getTime();
  }

  public static Date getEndTime(Date date) {
    Calendar todayEnd = Calendar.getInstance();
    todayEnd.setTime(date);
    todayEnd.set(11, 23);
    todayEnd.set(12, 59);
    todayEnd.set(13, 59);
    todayEnd.set(14, 999);
    return todayEnd.getTime();
  }

  public static Date getStartMonthDay(Date date) {
    Calendar todayStart = Calendar.getInstance();
    todayStart.setTime(date);
    todayStart.set(5, 1);
    todayStart.set(11, 0);
    todayStart.set(12, 0);
    todayStart.set(13, 0);
    todayStart.set(14, 0);
    return todayStart.getTime();
  }

  public static Date getEndMonthDay(Date date) {
    Calendar todayEnd = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
    todayEnd.setTime(date);

    todayEnd.set(5, todayEnd.getActualMaximum(5));

    todayEnd.set(11, 23);
    todayEnd.set(12, 59);
    todayEnd.set(13, 59);

    return todayEnd.getTime();
  }

  public static void main(String[] args)
  {
    List list = new ArrayList();
    Double point1 = new Double(116.295294D, 40.062139000000002D);
    Double point2 = new Double(116.291162D, 40.055208999999998D);
    Double point3 = new Double(116.301833D, 40.055788999999997D);
    list.add(point1);
    list.add(point2);
    list.add(point3);

    Double point = new Double(116.295869D, 40.061753000000003D);
    System.out.println(checkWithJdkGeneralPath(point, list));
  }

  public static boolean isOSWin(){//OS 鐗堟湰鍒ゆ柇
    String OS = System.getProperty("os.name").toLowerCase();
    if (OS.indexOf("win") >= 0) {
      return true;
    } else return false;
  }

  public static String getGrepOrFindStr(){
    if(isOSWin()){
      return "findstr";
    }
    return "grep";
  }

  public static List<String> getGPUInfo(String para){
    String cmd = "nvidia-smi " + para;
    if(isOSWin()){
        cmd = "cmd.exe /k c: && cd C:/Program Files/NVIDIA Corporation/NVSMI/ && nvidia-smi.exe " + para;
    }
    Runtime run = Runtime.getRuntime();
    List<String> result = new ArrayList<String>();
    try {
      Process process = run.exec(cmd);
      InputStream input = process.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      String szline;

      while (!StringUtil.isEmpty(szline = reader.readLine())) {
        result.add(szline);
        System.out.println(szline);
      }
      reader.close();
//            process.waitFor();
      process.destroy();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
  public static String getWebServiceUrl(){
      return web_service_url;
  }

  public static boolean isIPAddress(String ipaddr) {
    boolean flag = false;
    Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
    Matcher m = pattern.matcher(ipaddr);
    flag = m.matches();
    return flag;
  }
}