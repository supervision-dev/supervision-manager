package com.rmbank.supervision.common.utils;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

public final class IpUtil {
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			// 澶氭鍙嶅悜浠ｇ悊鍚庝細鏈夊涓猧p鍊硷紝绗竴涓猧p鎵嶆槸鐪熷疄ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}
	
	/**
     * 浠嶳equest瀵硅薄涓幏寰楀鎴风IP锛屽鐞嗕簡HTTP浠ｇ悊鏈嶅姟鍣ㄥ拰Nginx鐨勫弽鍚戜唬鐞嗘埅鍙栦簡ip
     * @param request
     * @return ip
     */
   public static String getIp2(HttpServletRequest request) {
       String remoteAddr = request.getRemoteAddr();
       String forwarded = request.getHeader("X-Forwarded-For");
       String realIp = request.getHeader("X-Real-IP");

       String ip = null;
       if (realIp == null) {
           if (forwarded == null) {
               ip = remoteAddr;
           } else {
               ip = remoteAddr + "/" + forwarded.split(",")[0];
           }
       } else {
           if (realIp.equals(forwarded)) {
               ip = realIp;
           } else {
               if(forwarded != null){
                   forwarded = forwarded.split(",")[0];
               }
               ip = realIp + "/" + forwarded;
           }
       }
       return ip;
   }
   /**
	 * request获取ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if("0:0:0:0:0:0:0:1".equals(ip)){
			ip="127.0.0.1";
		}
		return ip;
	}
}
