package com.rmbank.supervision.common.utils;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.net.PortUnreachableException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

public class Constants
{
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static final SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat("yyyy-MM-dd");
  public static final String USER_ADMIN_ACCOUNT = "admin";
  public static final String USER_SUPER_ADMIN_ACCOUNT = "administrator";
  public static final String USER_SESSION_RESOURCE = "userResources";
  public static final String USER_INFO = "userInfo";
  public static final String THE_REALM_NAME = "userRealm";



  public static final String DEFAULT_USER_PASSWORD = "123456";
  public static final int DEFAULT_PAGE_SIZE = 10;



  public static final Integer RESULT_SUCCESS = 200;
  public static final String RESULT_SUCCESS_MESSAGE = "load data success";
  public static final Integer RESULT_FAILED = 201;
  public static final String RESULT_FAILED_MESSAGE = "load data failed";
  public static final Integer RESULT_ERROR = 202;
  public static final String RESULT_ERROR_MESSAGE = "load data error";
  public static final Integer RESULT_EXCEPTION = 203;
  public static final String RESULT_EXCEPTION_MESSAGE = "load data exception";
  public static final Integer RESULT_PARAMS = 204;
  public static final String RESULT_PARAMS_MESSAGE = "param is not invalid";

  public static final Integer USER_TYPE_CUSTOMER = 0;
  public static final Integer USER_TYPE_ADMIN = 1;
  public static final Integer USER_TYPE_SUPERADMIN = 1;

  public static final Integer USER_STATUS_LOCKED = 0;
  public static final Integer USER_STATUS_EFFICTIVE = 1;  


  public static final Integer FLAG_LOCKED = 0;
  public static final Integer FLAG_UNLOCK = 1;
  public static final Integer STATUS_LOCKED = 0;
  public static final Integer STATUS_EFFICTIVE = 1;

  public static final Integer USER_NOT_DELETE = 0;
  public static final Integer USER_DELETED = 1;

  public static final Integer ROLE_TYPE_CUSTOMER = 0;
  public static final Integer ROLE_TYPE_ADMIN = 1; 
  
  public static final String ITEM_TYPE_FH_NAME = "分行"; 
  public static final String ITEM_TYPE_ZZ_NAME = "中支";
  
  public static final Integer LOG_TYPE_XLJC=1;
  public static final Integer LOG_TYPE_LZJC=2;
  public static final Integer LOG_TYPE_ZFJC=3;
  public static final Integer LOG_TYPE_LXGL=4;
  public static final Integer LOG_TYPE_BASE_DATA=5;
  public static final Integer LOG_TYPE_SYS=6;
  
  
  public static final String META_POSITION_KEY = "POSITION";
  public static final String META_PROJECT_KEY = "PROJECT";
  public static final String META_ORGTYPE_KEY = "ORGTYPE";
  public static final String META_ITEMCATEGORY_KEY = "ITEMCATEGORY";
  public static final String META_LAWTYPE_KEY = "LAWTYPE";


  public static final Integer STATIC_MODULE_ID = 99999;

  //项目类型，综合管理or实时监察
  public static final Integer STATIC_ITEM_TYPE_MANAGE = 1; 
  public static final Integer STATIC_ITEM_TYPE_SVISION = 0;
  
  //机构类型
  public static final Integer ORG_TYPE_1= 45; //行领导
  public static final Integer ORG_TYPE_2= 46; //依法行政领导办公室
  public static final Integer ORG_TYPE_3= 47; //监察室
  public static final Integer ORG_TYPE_4= 48; //分行机关
  public static final Integer ORG_TYPE_5= 49; //中支机构
  public static final Integer ORG_TYPE_6= 43; //中支监察室
  
  //分行立项，分行完成项目进行流程
  public static final Integer CONTENT_TYPE_ID_1= 31; 
  public static final Integer CONTENT_TYPE_ID_2= 32; 
  public static final Integer CONTENT_TYPE_ID_3= 33; 
  public static final Integer CONTENT_TYPE_ID_4= 34; 
  public static final Integer CONTENT_TYPE_ID_5= 35;  
  public static final Integer CONTENT_TYPE_ID_6= 91;   
  public static final Integer CONTENT_TYPE_ID_7= 93;  

  public static final Integer SUPERVISION_ORGAN_ID_CDFH = 19;
  
  public static final Integer SUPERVISION_TYPE_ID_XL = 2;
  public static final Integer SUPERVISION_TYPE_ID_LZ = 3;  
  public static final Integer SUPERVISION_TYPE_ID_ZF = 4;
  

  //分行立项，中支完成项目进行流程
  public static final Integer CONTENT_TYPE_ID_FHZZ= 54;  

  //中支立项，中支完成项目进行流程
  public static final Integer CONTENT_TYPE_ID_ZZZZ= 51; 
  public static final Integer CONTENT_TYPE_ID_ZZZZ_OVER= 52;  
  public static final Integer CONTENT_TYPE_ID_ZZZZ_VALUE= 999;  
  public static final Integer CONTENT_TYPE_ID_ZZZZ_RE_FILE= 998;  
  
  public static final Integer IS_VALUE = 1;
  public static final Integer NOT_VALUE = 0;

  public static final Integer IS_OVER = 1;
  public static final Integer NOT_OVER = 0;
  
  
  public static final Integer ITEM_STATUS_NEW =0; 
  public static final Integer ITEM_STATUS_NORMAL =1; 
  public static final Integer ITEM_STATUS_REBACK =2; 
  public static final Integer ITEM_STATUS_OVERLINE =3;
  public static final Integer ITEM_STATUS_OVER =4;
  public static final Integer ITEM_STATUS_OVER_COMPLETE =5;
  
  
  //效能监察流程
  public static final Integer EFFICIENCY_VISION_0=-1; //新增项目时的初始状态
  public static final Integer EFFICIENCY_VISION_1=66; //签收
  public static final Integer EFFICIENCY_VISION_2=67; //上传资料
  public static final Integer EFFICIENCY_VISION_3=68; //整改建议和整改建议
  public static final Integer EFFICIENCY_VISION_4=69; //录入整改情况
  public static final Integer EFFICIENCY_VISION_666=666; //整改建议
  public static final Integer EFFICIENCY_VISION_5=70; //跟踪监察
  public static final Integer EFFICIENCY_VISION_6=71; //效能监察流程完成
  public static final Integer EFFICIENCY_VISION_7=777; //需要问责，进入监察室录入问责资料
  public static final Integer EFFICIENCY_VISION_8=778; //再次上传整改情况
  public static final Integer EFFICIENCY_VISION_9=779; //监察室录入问责相关资料，项目进入被监察再次上传整改情况
  public static final Integer EFFICIENCY_VISION_10=688; //监察结论
  public static final Integer EFFICIENCY_VISION_11=72; //项目未完结，继续录入资料
  public static final Integer EFFICIENCY_VISION_12=999; //未完结时，上传的资料
  
  
  
  //廉政监察流程
  public static final Integer INCORRUPT_VISION_0=-1; //新增项目时的初始状态
  public static final Integer INCORRUPT_VISION_00=0; //新增项目时的初始状态
  public static final Integer INCORRUPT_VISION_1=72; //被监察对象录入项目方案
  public static final Integer INCORRUPT_VISION_2=73; //被监察对象录入决策内容
  public static final Integer INCORRUPT_VISION_3=74; //监察室提出监察意见  
  public static final Integer INCORRUPT_VISION_33=777; //录入会议决策后给出监察意见
  public static final Integer INCORRUPT_VISION_4=75; //被监察对象录入执行情况
  public static final Integer INCORRUPT_VISION_44=779; //监察室对执行情况提出不合规的意见，但是不问责，项目又回到被监察对象录入执行情况 等同于75
  public static final Integer INCORRUPT_VISION_444=666; //监察室对执行情况提出不合规的意见，但是问责，项目又回到被监察对象录入执行情况 等同于75
  public static final Integer INCORRUPT_VISION_5=76; //监察室监察执行情况
  public static final Integer INCORRUPT_VISION_55=778; //监察室对执行情况的意见是，不合规并且进行问责
  public static final Integer INCORRUPT_VISION_6=77; //被监察对象提请重新决策
  public static final Integer INCORRUPT_VISION_7=78; //监察室给出监察结论 													
  public static final Integer INCORRUPT_VISION_8=84; //廉政监察完成
  public static final Integer INCORRUPT_VISION_9=85; //党委采纳意见重新决策
  public static final Integer INCORRUPT_VISION_10=86; //党委维持原决议
  
  
  //执法监察流程
  public static final Integer ENFORCE_VISION_0= -1; //添加工作事项的初始状态
  public static final Integer ENFORCE_VISION_1= 130; //监察室立项状态
  public static final Integer ENFORCE_VISION_2= 131; //被监察对象已经录入立项资料
  public static final Integer ENFORCE_VISION_3= 132; //监察室给出监察立项资料意见，并且合规
  public static final Integer ENFORCE_VISION_4= 133; //被监察对象录入现场监察报告、意见书
  public static final Integer ENFORCE_VISION_5= 134; //监察室给出监察意见书意见，并且合规
  public static final Integer ENFORCE_VISION_6= 135; //被监察对象录入督促整改情况，并且不处罚
  public static final Integer ENFORCE_VISION_7= 136; //被监察对象录入督促整改情况，但是要处罚
  public static final Integer ENFORCE_VISION_8= 137; //被监察对象录入行政处罚意见告知书
  public static final Integer ENFORCE_VISION_9= 138; //监察室监察行政处罚意见告知书，并且合规
  public static final Integer ENFORCE_VISION_10= 140; //被监察对象录入行政处罚决定书
  public static final Integer ENFORCE_VISION_11= 141; //听证，录入听证相关资料
  public static final Integer ENFORCE_VISION_12= 142; //听证，被监察对象录入行政处罚决定书
  public static final Integer ENFORCE_VISION_13= 143; //监察室监察行政处罚决定书，并且合规
  public static final Integer ENFORCE_VISION_14= 144; //监察室监察行政处罚决定书，但是不合规
  public static final Integer ENFORCE_VISION_15= 145; //不复议，进入被监察对象录入行政处罚情况
  public static final Integer ENFORCE_VISION_OVER= 146;
  public static final Integer ENFORCE_VISION_16= 147; //复议，进入被监察对象录入复议相关资料
  
  public static final Integer ENFORCE_VISION_1A= 230; //监察室给出监察意见，但是不合规，回到被监察对象上传立项资料前一状态与130相等
  public static final Integer ENFORCE_VISION_3A= 232; //监察室给出监察意见，但是不合规，回到被监察对象上传意见书前一状态与132相等
  public static final Integer ENFORCE_VISION_2A= 234; //监察室给出监察意见，但是不合规，回到被监察对象录入行政处罚意见告知书前一状态与136相等
  
  
  
  
  
  
  
  
  
  
  
  
  
}
