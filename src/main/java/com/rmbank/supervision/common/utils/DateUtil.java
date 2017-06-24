package com.rmbank.supervision.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 
 * 涓昏鍔熻兘锛氬父鐢ㄦ棩鏈熷鐞嗙被锛屽寘鍚棩鏈熸牸寮忓寲銆佹棩鏈熸瘮杈冨拰鏃ユ湡鍔犲噺涓夌被鎿嶄綔锟�
 * <br>鏈被澶勭悊鐨勬棩鏈熷璞″潎涓簀ava.util.Date瀹炰緥瀵硅薄锛屽鏋滄槸鍏朵粬Date绫诲瀷瀵硅薄锛岃杞崲鍚庡啀浣跨敤锟�
 * <br>鏈被锟�锟斤拷鏂规硶鍧囦负闈欙拷?鏂规硶锛岋拷?杩囩被鍚嶇洿鎺ヨ皟鐢拷?
 * <br>甯搁噺浠嬬粛锟�
 *         <br>DateUtil.MONDAY    鏄熸湡锟�
 *         <br>DateUtil.TUESDAY   鏄熸湡锟�
 *         <br>DateUtil.WEDNESDAY 鏄熸湡锟�
 *         <br>DateUtil.THURSDAY  鏄熸湡锟�
 *         <br>DateUtil.FRIDAY    鏄熸湡锟�
 *         <br>DateUtil.SATURDAY  鏄熸湡锟�
 *         <br>DateUtil.SUNDAY    鏄熸湡锟�
 * 
 * @author wzy 2013/06/19
 * 
 */
public final class DateUtil {
	
	private static Log logger = LogFactory.getLog(DateUtil.class);
	
	/** 鏄熸湡锟�*/
	public static final int MONDAY = 1;
	/** 鏄熸湡锟�*/
	public static final int TUESDAY = 2;
	/** 鏄熸湡锟�*/
	public static final int WEDNESDAY = 3;
	/** 鏄熸湡锟�*/
	public static final int THURSDAY =4;
	/** 鏄熸湡锟�*/
	public static final int FRIDAY = 5;
	/** 鏄熸湡锟�*/
	public static final int SATURDAY = 6;
	/** 鏄熸湡锟�*/
	public static final int SUNDAY = 7;
	
	/**
	 * 绉佹湁鏋勶拷?銆備笉鍏佽鍒涘缓鏈被瀵硅薄锟�
	 */
	private DateUtil(){
		
	}
	
	/**
	 * 鑾峰彇浼犲叆鏃ユ湡鐨勫勾锟�
	 * @param date 鏃ユ湡
	 * @return 骞翠唤
	 */
	public static int getYear(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * 鑾峰彇浼犲叆鏃ユ湡鐨勬湀锟�
	 * @param date 鏃ユ湡
	 * @return 鏈堜唤
	 */
	public static int getMonth(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}
	
	/**
	 * 鑾峰彇浼犲叆鏃ユ湡鐨勫ぉ锟�
	 * @param date 鏃ユ湡
	 * @return 澶╂暟
	 */
	public static int getDay(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	/**
	 * 灏嗕紶鍏ョ殑鏃ユ湡瀵硅薄鏍煎紡鍖栬緭鍑猴拷?
	 * @param date 鏃ユ湡瀵硅薄
	 * @param pattern 甯屾湜鐢熸垚鐨勬棩鏈熸牸锟�
	 * @return 鏃ユ湡鏍煎紡瀛楃锟�
	 */
	public static String format(Date date, String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	/**
	 * 灏嗕紶鍏ョ殑鏃ユ湡瀵硅薄鏍煎紡鍖栬緭鍑猴拷?
	 * 榛樿杈撳嚭鏍煎紡涓簓yyy-MM-dd
	 * @param date 鏃ユ湡瀵硅薄
	 * @return 鏃ユ湡鏍煎紡瀛楃锟�
	 */
	public static String format(Date date){
		return format(date, "yyyy-MM-dd");
	}
	
	/**
	 * 灏嗕紶鍏ョ殑鏃ユ湡瀛楃涓叉牸寮忓寲杈撳嚭锟�
	 * @param date 鑳借浆鎹负鏃ユ湡鐨勫瓧绗︿覆
	 * @param patternSrc 鍘熸棩鏈熷瓧绗︿覆鏍煎紡
	 * @param patternOut 甯屾湜鐢熸垚鐨勬棩鏈熸牸锟�
	 * @return 鏃ユ湡鏍煎紡瀛楃锟�
	 * @throws ParseException 濡傛灉浼犲叆鐨刣ate瀛楃涓蹭笉鑳借浆鎹㈡垚java.util.Date瀵硅薄锛屽垯鎶涘嚭璇ュ紓锟�
	 */
	public static String format(String date, String patternSrc, String patternOut) throws ParseException{
		return format(parse(date, patternSrc), patternOut);
	}
	
	/**
	 * 灏嗗瓧绗︿覆杞崲鎴愭棩鏈熷锟�
	 * @param str 鏃ユ湡瀛楃锟�
	 * @param pattern 璇ュ瓧绗︿覆鐨勬牸锟�
	 * @return 鏃ユ湡鏍煎紡瀛楃锟�
	 * @throws ParseException 濡傛灉浼犲叆鐨剆tr瀛楃涓蹭笉鑳借浆鎹㈡垚java.util.Date瀵硅薄锛屽垯鎶涘嚭璇ュ紓锟�
	 */
	public static Date parse(String str, String pattern) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(str);
	}
	
	/**
	 * 鑾峰彇涓や釜鏃ユ湡涔嬮棿闂撮殧鐨勫ぉ鏁帮拷?
	 * 璇ラ棿闅斿ぉ鏁颁负鑷劧澶╋拷?
	 * 渚嬶細
	 *     2013-06-01 23:59:59锟�013-06-02 00:00:00鐨勯棿闅斿ぉ鏁颁负1澶╋拷?锛堝疄闄呴棿闅旀椂闂存槸1绉掞級
	 * @param min 杈冨皬鐨勬棩鏈熷锟�
	 * @param max 杈冨ぇ鐨勬棩鏈熷锟�
	 * @return 鏁存暟澶╂暟锛堝鏋滀负璐熸暟锛屽垯璇存槑鍙傛暟1澶т簬鍙傛暟2锟�
	 */
	public static int getBetweenDays(Date min, Date max){
		double temp = 0;
		//璁＄畻鐩稿樊澶╂暟锛屽拷鐣ユ椂鍒嗙
		try {
			Date mx  = parse(format(max), "yyyy-MM-dd");
			Date mi = parse(format(min), "yyyy-MM-dd");
			temp = mx.getTime() - mi.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int)Math.ceil(temp / (1000 * 60 * 60 * 24));
	}
	
	/**
	 * 鑾峰彇涓や釜鏃ユ湡涔嬮棿闂撮殧鐨勬湀鏁帮拷?
	 * 璇ラ棿闅旀湀鏁颁负鑷劧鏈堬拷?
	 * 渚嬶細
	 *     2013-06-30锟�013-07-01鐨勯棿闅旀湀鏁颁负1鏈堬拷?锛堝疄闄呴棿闅旀椂闂存槸1澶╋級
	 * @param min 杈冨皬鐨勬棩鏈熷锟�
	 * @param max 杈冨ぇ鐨勬棩鏈熷锟�
	 * @return 鏁存暟鏈堟暟锛堝鏋滀负璐熸暟锛屽垯璇存槑鍙傛暟1澶т簬鍙傛暟2锟�
	 */
	public static int getBetweenMonths(Date min, Date max){
		Calendar calMin = Calendar.getInstance();
		calMin.setTime(min);
		Calendar calMax = Calendar.getInstance();
		calMax.setTime(max);
		int month1 = calMin.get(Calendar.YEAR) * 12 + calMin.get(Calendar.MONTH) + 1;
		int month2 = calMax.get(Calendar.YEAR) * 12 + calMax.get(Calendar.MONTH) + 1;
		
		return month2 - month1;
	}
	
	/**
	 * 鑾峰彇涓や釜鏃ユ湡涔嬮棿闂撮殧鐨勫勾鏁帮拷?
	 * 璇ラ棿闅斿勾鏁颁负鑷劧骞达拷?
	 * 渚嬶細
	 *     2012-12-31锟�013-01-01鐨勯棿闅斿勾鏁颁负1骞达拷?锛堝疄闄呴棿闅旀椂闂存槸1澶╋級
	 * @param min 杈冨皬鐨勬棩鏈熷锟�
	 * @param max 杈冨ぇ鐨勬棩鏈熷锟�
	 * @return 鏁存暟骞存暟锛堝鏋滀负璐熸暟锛屽垯璇存槑鍙傛暟1澶т簬鍙傛暟2锟�
	 */
	public static int getBetweenYears(Date min, Date max){
		return getYear(max) - getYear(min);
	}
	
	/**
	 * 鑾峰彇涓や釜鏃ユ湡涔嬮棿闂撮殧鐨勫懆鏁帮拷?
	 * 璇ラ棿闅斿懆鏁颁负鑷劧鍛紝骞朵笖榛樿鏄互鍛ㄤ竴涓哄懆鐨勮捣濮嬫棩锟�
	 * 渚嬶細
	 *     涓婂懆鍛ㄦ棩鍒版湰鍛ㄥ懆锟�锟斤拷闅斾负1鍛拷?锛堝疄闄呴棿闅旀椂闂存槸1澶╋級
	 * @param min 杈冨皬鐨勬棩鏈熷锟�
	 * @param max 杈冨ぇ鐨勬棩鏈熷锟�
	 * @return 鏁存暟鍛ㄦ暟锛堝鏋滀负璐熸暟锛屽垯璇存槑鍙傛暟1澶т簬鍙傛暟2锟�
	 */
	public static int getBetweenWeeks(Date min, Date max){
		return getBetweenWeeks(min, max, MONDAY);
	}
	
	/**
	 * 鑾峰彇涓や釜鏃ユ湡涔嬮棿闂撮殧鐨勫懆鏁帮拷?
	 * 璇ラ棿闅斿懆鏁颁负鑷劧锟�
	 * 渚嬶細
	 *     涓婂懆鍛ㄦ棩鍒版湰鍛ㄥ懆锟�锟斤拷闅斾负1鍛拷?锛堝疄闄呴棿闅旀椂闂存槸1澶╋級
	 * @param min 杈冨皬鐨勬棩鏈熷锟�
	 * @param max 杈冨ぇ鐨勬棩鏈熷锟�
	 * @param value 琛ㄧず鏄熸湡鍑犱綔涓轰竴鍛ㄧ殑锟�锟斤拷
	 * @return 鏁存暟鍛ㄦ暟锛堝鏋滀负璐熸暟锛屽垯璇存槑鍙傛暟1澶т簬鍙傛暟2锟�
	 */
	public static int getBetweenWeeks(Date min, Date max, int value){
		Calendar calMin = Calendar.getInstance();
		Calendar calMax = Calendar.getInstance();
		boolean flag = false;
		if(min.getTime() > max.getTime()){
			calMax.setTime(min);
			calMin.setTime(max);
			flag = true;
		}else{
			calMax.setTime(max);
			calMin.setTime(min);
		}
		int minWeekDay = getDayOfWeek(calMin.getTime(), value);
		
		Date firstWeekEnd = addDays(calMin.getTime(), 7 - minWeekDay);
		
		if(getBetweenDays(firstWeekEnd, calMax.getTime()) > 0){
			Date tempDate = addDays(firstWeekEnd, 1);
			int count = 0;
			while(getBetweenDays(tempDate, calMax.getTime()) >= 0){
				count++;
				tempDate = addDays(tempDate, 7);
			}
			if(flag){
				count = count * -1;
			}
			return count;
		}else{
			return 0;
		}
	}
	
	/**
	 * 鑾峰彇浼犲叆鐨凞ate鏃ユ湡鍦ㄤ竴鍛ㄤ腑澶勪簬绗嚑锟�
	 * @param date 鏃ユ湡瀵硅薄
	 * @param value 琛ㄧず鏄熸湡鍑犱綔涓轰竴鍛ㄧ殑锟�锟斤拷
	 * @return 绗嚑锟�
	 */
	public static int getDayOfWeek(Date date,int value){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int temp = cal.get(Calendar.DAY_OF_WEEK) - value;
		if(temp < 1){
			temp += 7;
		}
		return temp;
	}
	
	/**
	 * 鍦ㄥ綋鍓嶆棩鏈熶笂淇敼澶╂暟锛屽苟灏嗕慨鏀瑰悗鐨勬棩鏈熷璞¤繑鍥烇拷?
	 * @param date 寰呬慨鏀圭殑鏃ユ湡瀵硅薄
	 * @param days 濡傛灉鍊间负璐燂紝鍒欏噺灏戠浉搴斿ぉ鏁帮拷?
	 * @return 淇敼鍚庣殑鏃ユ湡瀵硅薄
	 */
	public static Date addDays(Date date, int days){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}
	
	/**
	 * 鍦ㄥ綋鍓嶆棩鏈熶笂淇敼鏈堟暟锛屽苟灏嗕慨鏀瑰悗鐨勬棩鏈熷璞¤繑鍥烇拷?
	 * @param date 寰呬慨鏀圭殑鏃ユ湡瀵硅薄
	 * @param months 濡傛灉鍊间负璐燂紝鍒欏噺灏戠浉搴旀湀鏁帮拷?
	 * @return 淇敼鍚庣殑鏃ユ湡瀵硅薄
	 */
	public static Date addMonths(Date date, int months){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}
	
	/**
	 * 鍦ㄥ綋鍓嶆棩鏈熶笂淇敼灏忔椂鏁帮紝骞跺皢淇敼鍚庣殑鏃ユ湡瀵硅薄杩斿洖锟�
	 * @param date 寰呬慨鏀圭殑鏃ユ湡瀵硅薄
	 * @param hours 濡傛灉鍊间负璐燂紝鍒欏噺灏戠浉搴斿皬鏃舵暟锟�
	 * @return 淇敼鍚庣殑鏃ユ湡瀵硅薄
	 */
	public static Date addHours(Date date, int hours){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hours);
		return cal.getTime();
	}
	
	/**
	 * 鍦ㄥ綋鍓嶆棩鏈熶笂淇敼鍒嗛挓鏁帮紝骞跺皢淇敼鍚庣殑鏃ユ湡瀵硅薄杩斿洖锟�
	 * @param date 寰呬慨鏀圭殑鏃ユ湡瀵硅薄
	 * @param minutes 濡傛灉鍊间负璐燂紝鍒欏噺灏戠浉搴斿垎閽熸暟锟�
	 * @return 淇敼鍚庣殑鏃ユ湡瀵硅薄
	 */
	public static Date addMinutes(Date date, int minutes){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}
	
	
	/**
	 * 
	 * @param formatType 鏃ユ湡瀛楃鏍煎紡
	 * @return 杩斿洖鐩稿簲鐨勫瓧绗︽牸寮忎覆锛岄粯璁ゆ牸寮忎负yyyy-MM-dd HH:mm:ss
	 */
	private static String getFormatStr(int formatType) {
		String formatStr = null;
		switch (formatType) {
		case 1:
			formatStr = "yyyy-MM-dd";
			break;
		case 2:
			formatStr = "yyyy-MM-dd HH:mm:ss";
			break;
		case 3:
			formatStr = "yyyy-MM-dd HH:mm:ss SSS";
			break;
		case 4:
			formatStr = "yyyyMMdd";
			break;
		case 5:
			formatStr = "yyyyMMddHHmmss";
			break;
		case 6:
			formatStr = "yyyyMMddHHmmssSSS";
			break;
		case 7:
			formatStr = "yyyyMM";
			break;
		case 8:
			formatStr = "yyyy";
			break;
		case 9:
			formatStr = "MM";
			break;
		case 10:
			formatStr = "dd";
			break;
		case 11:
			formatStr = "HHmmssSSS";
			break;
		default:
			formatStr = "yyyy-MM-dd HH:mm:ss";
		}
		return formatStr;
	}

	public static long getLongFromStr(String timeStr, int formatType) {
		long time = 0L;
		String formatStr = getFormatStr(formatType);
		if ((timeStr != null) && (timeStr.length() > 0)) {
			DateFormat df = new SimpleDateFormat(formatStr);
			try {
				time = df.parse(timeStr).getTime();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return time;
	}

	public static long getLongFromLong(long timeLong, int formatType) {
		long time = 0L;
		if (timeLong > 0L) {
			String formatStr = getFormatStr(formatType);
			DateFormat df = new SimpleDateFormat(formatStr);
			time = Long.parseLong(df.format(Long.valueOf(timeLong)));
		}
		return time;
	}

	/**
	 * 灏嗘寚瀹氱殑鏃堕棿杞崲涓虹浉搴旂殑鏃堕棿瀛楃锟�
	 * 
	 * @param timeLong
	 * @param formatType
	 * @return
	 */
	public static String getStringTime(long timeLong, int formatType) {
		String time = "";
		if (timeLong > 0L) {
			String formatStr = getFormatStr(formatType);
			DateFormat df = new SimpleDateFormat(formatStr);
			time = df.format(Long.valueOf(timeLong));
		}
		return time;
	}

	public static long convertTime4Long(String stime, int formatType)
			throws Exception {
		Date date = null;
		DateFormat df = new SimpleDateFormat(getFormatStr(formatType));
		try {
			date = df.parse(stime);
		} catch (Exception e) {
			throw new Exception();
		}
		return date.getTime();
	}

	public static String convertTimeString(long time) {
		String s = String.valueOf(time);
		return s.substring(0, 4) + "-" + s.substring(4, 6) + "-"
				+ s.substring(6, 8) + " " + s.substring(8, 10) + ":"
				+ s.substring(10, 12) + ":" + s.substring(12, 14);
	}

	public static String getMonth(int month) {
		Calendar strDate = Calendar.getInstance();
		strDate.add(2, month);
		return getStringTime(strDate.getTime().getTime(), 7);
	}

	public static String getYearOrMonthOrDay(int formatType) {
		Calendar ca = Calendar.getInstance();
		String time = "";
		switch (formatType) {
		case 1:
			time = String.valueOf(ca.get(Calendar.YEAR));
			break;
		case 2:
			time = String.valueOf(ca.get(Calendar.MONTH));
			break;
		case 3:
			time = String.valueOf(ca.get(Calendar.DATE));
			break;
		default:
			break;
		}
		return time;
	}

	public static String getCurrentTimeToString(int styleType) {
		// 寤鸿鍐欐垚String褰㈠紡鍐欏叆
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(getFormatStr(styleType));
		String dateStr = format.format(date);

		return dateStr;
	}

	/**
	 * 鑾峰彇褰撳墠鏃堕棿鍑犳湀鍓嶆垨鍑犳湀鍚庣殑鏃堕棿
	 * 
	 * @param month
	 * @param formatStyle
	 * @return
	 */
	public static String getNextTime(int month, int formatStyle) {
		Calendar strDate = Calendar.getInstance();
		strDate.add(2, month);
		return getStringTime(strDate.getTime().getTime(), formatStyle);
	}

	public static String getNextMinute(int minute, int formatStyle) {
		Calendar strDate = Calendar.getInstance();
		strDate.add(Calendar.MINUTE, minute);
		return getStringTime(strDate.getTime().getTime(), formatStyle);
	}

	public static Date getNextMinute(int minute) {
		Calendar strDate = Calendar.getInstance();
		strDate.add(Calendar.MINUTE, minute);
		return strDate.getTime();
	}

	public static void main(String[] args) {
//		int month = Calendar.getInstance().get(2) + 1;
//		System.out.println(month);
//		month = 4;
//		DateUtil.getCurrentTimeToString(1);
//		try {
//			System.out.print(DateUtil.getNetTime());
//		}catch (Exception e){
//			System.out.print("broken");
//		}
		//System.out.print(DateUtil.getBetweenDays(DateUtil.getDateFromString("2016-12-06 "),DateUtil.getDateFromString("2016-12-05 ")));
		//logger.info("\n***********" + getNextTime(-month, 4));
	}
	/**
	   * 灏嗛暱鏃堕棿鏍煎紡鏃堕棿杞崲涓哄瓧绗︿覆 yyyy-MM-dd HH:mm:ss
	   * 
	   * @param dateDate
	   * @return
	   */
	public static String dateToStrLong(Date dateDate) {
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String dateString = formatter.format(dateDate);
	   return dateString;
	}

	public static String getNetTime()throws Exception{
		URL url = new URL("http://www.bjtime.cn");
		URLConnection uc = url.openConnection();
		uc.connect();
		long time = uc.getDate();
		Date date = new Date(time);
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public static Date getDateFromStartString(){
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat(getFormatStr(1));
			Date date = sdf.parse("2016-05-25");
			return date;
		}
		catch (ParseException e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	public static Date getDateFromEndString(){
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat(getFormatStr(1));
			Date date = sdf.parse("2016-08-27");
			return date;
		}
		catch (ParseException e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	public static Date getDateFromString(String dateStr){
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat(getFormatStr(1));
			Date date = sdf.parse(dateStr);
			return date;
		}
		catch (ParseException e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}

}
