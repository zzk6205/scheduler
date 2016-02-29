package net.web.base.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map工具类
 */
public class MapUtils extends org.apache.commons.collections.MapUtils {

	/**
	 * List<Map>转化为List<Object>
	 * 
	 * @param clazz
	 * @param list
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static <T, V> List<T> toObjectList(Class<T> clazz, List<Map<String, V>> list) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		List<T> retList = new ArrayList<T>();
		if (list != null && !list.isEmpty()) {
			for (Map<String, V> map : list) {
				T object = clazz.newInstance();
				retList.add(toObject(object, map, false));
			}
		}
		return retList;
	}

	/**
	 * List<Map>转化为List<Object> <br>
	 * 使用骆驼拼写法<br>
	 * 
	 * @param clazz
	 * @param list
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static <T, V> List<T> toCamelCaseObjectList(Class<T> clazz, List<Map<String, V>> list) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		List<T> retList = new ArrayList<T>();
		if (list != null && !list.isEmpty()) {
			for (Map<String, V> map : list) {
				T object = clazz.newInstance();
				retList.add(toObject(object, map, true));
			}
		}
		return retList;
	}

	/**
	 * Map转化为Object
	 * 
	 * @param clazz
	 * @param list
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static <T, V> T toObject(Class<T> clazz, Map<String, V> map) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		T object = clazz.newInstance();
		return toObject(object, map, false);
	}

	/**
	 * Map转化为Object <br>
	 * 使用骆驼拼写法<br>
	 * 
	 * @param clazz
	 * @param list
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static <T, V> T toCamelCaseObject(Class<T> clazz, Map<String, V> map) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		T object = clazz.newInstance();
		return toObject(object, map, true);
	}

	private static <T, V> T toObject(T object, Map<String, V> map, boolean toCamelCase) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		if (toCamelCase) {
			map = toCamelCaseMap(map);
		}
		BeanUtils.populate(object, map);
		return object;
	}

	/**
	 * Object对象转Map
	 * 
	 * @param object
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Map<String, String> toMap(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return BeanUtils.describe(object);
	}

	/**
	 * 转换成Map并提供字段命名驼峰转平行
	 * 
	 * @param object
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Map<String, String> toFlatMap(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, String> map = toMap(object);
		return toUnderlineStringMap(map);
	}

	/**
	 * 转换为Collection<Map<K, V>>
	 * 
	 * @param collection
	 *            待转换对象集合
	 * @return 转换后的Collection<Map<K, V>>
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static <T> List<Map<String, String>> toMapList(List<T> list) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				Map<String, String> map = toMap(object);
				retList.add(map);
			}
		}
		return retList;
	}

	/**
	 * 转换为Collection,同时为字段做驼峰转换<Map<K, V>>
	 * 
	 * @param collection
	 *            待转换对象集合
	 * @return 转换后的Collection<Map<K, V>>
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static <T> List<Map<String, String>> toFlatMapList(List<T> list) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				Map<String, String> map = toFlatMap(object);
				retList.add(map);
			}
		}
		return retList;
	}

	/**
	 * 将Map的Keys去下划线 (例:branch_no -> branchNo )
	 * 
	 * @param map
	 *            待转换Map
	 * @return
	 */
	public static <V> Map<String, V> toCamelCaseMap(Map<String, V> map) {
		Map<String, V> newMap = new HashMap<String, V>();
		for (String key : map.keySet()) {
			safeAddToMap(newMap, JavaBeanUtils.toCamelCaseString(key), map.get(key));
		}
		return newMap;
	}

	/**
	 * 将Map的Keys转译成下划线格式的 (例:branchNo -> branch_no)
	 * 
	 * @param map
	 *            待转换Map
	 * @return
	 */
	public static <V> Map<String, V> toUnderlineStringMap(Map<String, V> map) {
		Map<String, V> newMap = new HashMap<String, V>();
		for (String key : map.keySet()) {
			newMap.put(JavaBeanUtils.toUnderlineString(key), map.get(key));
		}
		return newMap;
	}

}
