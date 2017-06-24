package com.rmbank.supervision.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @Description: 
 * @ClassName: com.ali.openim.util.JsonUtils
 * @author: Omar(OmarZhang)
 * @date: 2015骞�2鏈�鏃�涓婂崍1:17:50 
 *
 */
public final class JsonUtils {

	/** ObjectMapper */
	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * 涓嶅彲瀹炰緥鍖�
	 */
	private JsonUtils() {
	}

	/**
	 * 灏嗗璞¤浆鎹负JSON瀛楃涓�
	 * 
	 * @param value
	 *            瀵硅薄
	 * @return JSOn瀛楃涓�
	 */
	public static String toJson(Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 灏咼SON瀛楃涓茶浆鎹负瀵硅薄
	 * 
	 * @param json
	 *            JSON瀛楃涓�
	 * @param valueType
	 *            瀵硅薄绫诲瀷
	 * @return 瀵硅薄
	 */
	public static <T> T toObject(String json, Class<T> valueType) {
		try {
			return mapper.readValue(json, valueType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 灏咼SON瀛楃涓茶浆鎹负瀵硅薄
	 * 
	 * @param json
	 *            JSON瀛楃涓�
	 * @param typeReference
	 *            瀵硅薄绫诲瀷
	 * @return 瀵硅薄
	 */
	public static <T> T toObject(String json, TypeReference<?> typeReference) {
		try {
			return mapper.readValue(json, typeReference);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 灏咼SON瀛楃涓茶浆鎹负瀵硅薄
	 * 
	 * @param json
	 *            JSON瀛楃涓�
	 * @param javaType
	 *            瀵硅薄绫诲瀷
	 * @return 瀵硅薄
	 */
	public static <T> T toObject(String json, JavaType javaType) {
		try {
			return mapper.readValue(json, javaType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 灏嗗璞¤浆鎹负JSON娴�
	 * 
	 * @param writer
	 *            writer
	 * @param value
	 *            瀵硅薄
	 */
	public static void writeValue(Writer writer, Object value) {
		try {
			mapper.writeValue(writer, value);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	    * @Title: convertList2Json 
	    * @Description: 灏哃ist瀵硅薄杞崲涓篔son瀛楃涓�鏀寔娉涘瀷)
	    * @param objects
	    * @param clazz
	    * @throws IOException  
	    * @return String   
	    */ 
	    public static <T> String convertList2Json(List<T> objects, Class<?> clazz) throws IOException {
	    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES ,false);
	    	mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
	    	mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	        return mapper.writeValueAsString(objects);
	    }

	 /**
	  * 瀛楃涓茶浆 瀵硅薄闆嗗悎
	 * @param <T>
	  * @Title: toObjectList 
	  * @param storeGroupIds
	  * @param class1
	  * @return
	  * 
	  */
	public static <T> List<T> toObjectList(String json,Class<T> clazz) {
		List<T> lists = new ArrayList<T>();
		if(StringUtils.isBlank(json)) {
			return lists;
		}
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> tempList = toObject(json, new ArrayList<Map<String,Object>>().getClass());
		for(Map<String,Object> tempMap : tempList ) {
			try {
				lists.add(mapper.readValue(toJson(tempMap),clazz));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lists;
	}
	public static List<Map<String, String>> toListMap(String text) {
		JSONArray array = JSONArray.fromObject(text);
		List<Map<String, String>> pairs = new ArrayList<Map<String, String>>();
		for (int i = 0; i < array.size(); i++) {
			Map<String, String> pair = new HashMap<String, String>();
			JSONObject obj = (JSONObject) array.get(i);
			Set keys = obj.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				pair.put(key, obj.getString(key));
			}
			pairs.add(pair);
		}
		return pairs;
	}
}