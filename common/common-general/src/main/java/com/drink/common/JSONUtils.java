package com.drink.common;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang3.StringUtils;
//import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

public class JSONUtils {
//	private static Logger logger = Logger.getLogger(JSONUtils.class);
	private static ObjectMapper objectMapper = new ObjectMapper();;

	public static String readObject2String(Object obj) throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.writeValueAsString(obj);
	}

	public static <T> T readJson2POJO(String json, Class<T> entityClass) throws JsonParseException, JsonMappingException, IOException {
		if (!StringUtils.isEmpty(json)) {
			return objectMapper.readValue(json, entityClass);
		} else {
			return null;
		}
	}

	private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	public static <T> Collection<T> readJson2Collections(String json, Class<?> collectionClass, Class<T> entityClass) throws JsonParseException,
			JsonMappingException, IOException {
		if (!StringUtils.isEmpty(json)) {
			// config the objectMapper to support cancel timeStamp, so that it
			// can format the date type with [ISO-8601
			// ]-compliant notation, such as "1970-01-01T00:00:00.000+0000"
			// objectMapper.configure(
			// org.codehaus.jackson.map.DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
			// false);
			// objectMapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,
			// false);
			// objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
			JavaType javaType = getCollectionType(Collection.class, entityClass);
			return objectMapper.readValue(json, javaType);
		} else {
			return null;
		}
	}

	public static <T> List<T> readJson2List(String json, Class<T> entityClass) throws JsonParseException, JsonMappingException, IOException {
		if (!StringUtils.isEmpty(json)) {
			JavaType javaType = getCollectionType(List.class, entityClass);
			return objectMapper.readValue(json, javaType);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> readJson2List(String json) throws JsonParseException, JsonMappingException, IOException {
		if (!StringUtils.isEmpty(json)) {
			return objectMapper.readValue(json, List.class);
		} else {
			return null;
		}
	}

	public static String[] readJson2Array(String json) throws JsonParseException, JsonMappingException, IOException {
		if (!StringUtils.isEmpty(json)) {
			String[] rs = objectMapper.readValue(json, String[].class);
			return rs;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> readCollections2toList(Collection<?> collection) throws JsonGenerationException, JsonMappingException,
			IOException {
		if (null != collection) {
			String json = objectMapper.writeValueAsString(collection);
			return objectMapper.readValue(json, List.class);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> readObject2Map(Object object) throws JsonParseException, JsonMappingException, IOException {
		// config the objectMapper to support cancel timeStamp, so that it can
		// format the date type with [ISO-8601]-compliant notation, such as
		// "1970-01-01T00:00:00.000+0000"
		// objectMapper.configure(
		// org.codehaus.jackson.map.DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
		// false);
		// objectMapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,
		// false);
		// objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		String json = null;
		if (object instanceof String) {
			json = object.toString();
		} else {
			json = objectMapper.writeValueAsString(object);
		}

		Map<String, Object> maps = objectMapper.readValue(json, Map.class);
		return maps;
	}

}
