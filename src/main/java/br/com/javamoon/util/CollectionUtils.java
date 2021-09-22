package br.com.javamoon.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionUtils {

	@SuppressWarnings("unchecked")
	public static <T> List<T> listOf(T...objs){
		if (objs == null)
			return Collections.emptyList();
		
		List<T> list = new ArrayList<>(objs.length);
		for (T obj : objs)
			list.add(obj);
		
		return list;
	}
}
