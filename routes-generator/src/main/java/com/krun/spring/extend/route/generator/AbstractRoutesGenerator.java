/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      AbstractRoutesGenerator.java
 * Date:    18-5-3 上午8:14
 * Author: krun
 */

package com.krun.spring.extend.route.generator;

import com.krun.spring.extend.route.Handler;
import com.krun.spring.extend.route.Route;
import com.krun.spring.extend.route.convert.AbstractRequestConditionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

/**
 * @author krun
 * @date 2018/05/03
 */
public abstract class AbstractRoutesGenerator {

	@Autowired
	private RequestMappingHandlerMapping handlerMapping;

	private Route root = null;

	/**
	 * 获取前缀以对类名进行裁剪
	 * @return
	 */
	protected abstract String getPrefix();

	/**
	 * 获取后缀以对类名进行裁剪
	 * @return
	 */
	protected abstract String getSuffix();

	/**
	 * 检查当前 beanType 是否为所需类型
	 * @param beanType
	 * @return
	 */
	protected abstract boolean isExcludeType(Class<?> beanType);

	public String getRoutes() {
		return getRoutes(new Class[0]);
	}

	/**
	 * 获取路由树（经 includeClasses 参数修建），如果参数数组长度为零，则获取所有路由
	 * @param includeClasses
	 * @return
	 */
	public String getRoutes(Class[] includeClasses) {
		if (root == null) {
			generateRoutes();
			root.clean();
		}

		Route route = Route.builder().name("@").routes(root.getRoutes(includeClasses)).build();
		route.clean();
		return route.toString();
	}

	/**
	 * 生成路由树
	 */
	private void generateRoutes() {

		Map<Class<?>, List<Map.Entry<RequestMappingInfo, HandlerMethod>>> classesMap = getClassesMap();
		List<Class<?>> classes = getClassesBySort(classesMap.keySet());
		List<Route> routes = new LinkedList<>();

		String path;
		Route route;
		Handler handler;
		List<Handler> handlers;

		List<Map.Entry<RequestMappingInfo, HandlerMethod>> entries;

		RequestMappingInfo info;
		HandlerMethod method;
		Set<String> patterns;

		AbstractRequestConditionConverter[] converters = AbstractRequestConditionConverter.converters();

		for (Class<?> clazz : classes) {
			path = translateNameToPath(clazz.getSimpleName());

			entries = classesMap.get(clazz);

			handlers = new LinkedList<>();

			for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : entries) {

				info = entry.getKey();
				method = entry.getValue();
				patterns = info.getPatternsCondition().getPatterns();

				for (String pattern : patterns) {
					handler = Handler.builder()
							.url(pattern)
							.name(method.getMethod().getName())
							.build();

					for (AbstractRequestConditionConverter converter : converters) {
						converter.setCondition(info);
						converter.setValuesToHandler(handler);
					}

					handlers.add(handler);
				}

			}

			List<Class<?>> list = new LinkedList<>();
			list.add(clazz);
			route = Route.builder().path(path).beanTypes(list).handlers(handlers).build();
			routes.add(route);

		}

		root = Route.builder().name("@").build();

		for (Route r : routes) {
			root.pushAsChild(r);
		}
	}


	private String translateNameToPath(String name) {
		if (name.startsWith(getPrefix())) {
			name = name.substring(getPrefix().length(), name.length());
		}
		if (name.endsWith(getSuffix())) {
			name = name.substring(0, name.lastIndexOf(getSuffix()));
		}
		char[] chars = name.toCharArray();
		StringBuilder builder = new StringBuilder();
		for (char c : chars) {
			if (isUp(c)) {
				builder.append('/').append((char) (c + 32));
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	private static boolean isLow(char c) {
		return 'a' <= c && c <= 'z';
	}

	private static boolean isUp(char c) {
		return 'A' <= c && c <= 'Z';
	}

	/**
	 * 根据已注册的路由信息表，构建与类对应的路由表
	 * @return
	 */
	private Map<Class<?>, List<Map.Entry<RequestMappingInfo, HandlerMethod>>> getClassesMap() {

		Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = handlerMapping.getHandlerMethods();
		Map<Class<?>, List<Map.Entry<RequestMappingInfo, HandlerMethod>>> classesMap = new HashMap<>();
		List<Map.Entry<RequestMappingInfo, HandlerMethod>> list;
		Class<?> beanType;

		for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
			beanType = getHandlerBeanType(entry);
			if (AbstractRoutesGenerator.class.isAssignableFrom(beanType) || isExcludeType(beanType)) {
				continue;
			}
			list = classesMap.get(beanType);
			if (list == null) {
				list = new LinkedList<>();
				classesMap.put(beanType, list);
			}
			list.add(entry);
		}

		return classesMap;
	}

	private static List<Class<?>> getClassesBySort(Set<Class<?>> classes) {
		List<Class<?>> list = new LinkedList<>(classes);
		list.sort(new Comparator<Class<?>>() {

			@Override
			public int compare (Class<?> o1, Class<?> o2) {
				return o1.getSimpleName()
				         .compareTo(o2.getSimpleName());
			}
		});
		return list;
	}

	private static Class<?> getHandlerBeanType(Map.Entry<RequestMappingInfo, HandlerMethod> entry) {
		return entry.getValue().getBeanType();
	}

}
