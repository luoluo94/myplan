package com.guima.kits;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapKit
{
	public static Map<String, String[]> cloneMap(Map<String, String[]> map)
	{
		Map<String, String[]> clone = new LinkedHashMap<>();
        for (Entry<String, String[]> entry : map.entrySet())
        {
            clone.put(entry.getKey(), entry.getValue());
        }
		return clone;
	}

    @SuppressWarnings("unchecked")
	public static Map<String, String[]> copyMap(Map map)
	{
		Map<String, String[]> clone = new LinkedHashMap<>();
        for (Object o : map.entrySet())
        {
            Entry<String, String> entry = (Entry<String, String>) o;
            clone.put(entry.getKey(), new String[]{String.valueOf(entry.getValue())});
        }
		return clone;
	}

	public static Map<String, String[]> setValueToMap(Map<String, String[]> map, String key, String value)
	{
		map.put(key, new String[]
		{ value });
		return map;
	}

	public static String getValueFromMap(Map<String, String[]> map, String key)
	{
		String result = "";
		String[] resultArray = map.get(key);
		if (null != resultArray && resultArray.length > 0)
			result = resultArray[0];
		return result;
	}

	public static String removeValueFromMap(Map<String, String[]> map, String key)
	{
		String result = "";
		String[] resultArray = map.remove(key);
		if (null != resultArray && resultArray.length > 0)
			result = resultArray[0];
		return result;
	}

	public static Map<String, String[]> parse(Map<String, String> map)
	{
		Map<String, String[]> result = new LinkedHashMap<>();
		for (String key : map.keySet())
		{
			String val = String.valueOf(map.get(key));
			if (!val.equals("null"))
				result.put(key, new String[]
				{ val });
		}
		return result;
	}
}
