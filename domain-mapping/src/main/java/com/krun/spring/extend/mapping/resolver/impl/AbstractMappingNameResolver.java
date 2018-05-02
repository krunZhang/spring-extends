/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      AbstractMappingNameResolver.java
 * Date:    18-5-1 下午8:15
 * Author: krun
 */

package com.krun.spring.extend.mapping.resolver.impl;

import com.krun.spring.extend.mapping.DomainMapping;
import com.krun.spring.extend.mapping.RestDomainMapping;
import com.krun.spring.extend.mapping.resolver.MappingNameResolver;
import com.krun.spring.extend.mapping.utils.Utils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * MappingNameResolver 抽象基类实现，要求实现 getSuffix() 和 getPrefix() 方法，
 * 以裁剪（可能）存在的前缀后缀，但是根元素的前缀不会被删除。
 *
 * @author krun
 * @date 2018/05/01
 */
public abstract class AbstractMappingNameResolver implements MappingNameResolver {

	/**
	 * 获取需要裁剪的后缀
	 * @return 需要裁剪的后缀
	 */
	protected abstract String getSuffix();
	/**
	 * 获取需要裁剪的前缀
	 * @return 需要裁剪的前缀
	 */
	protected abstract String getPrefix();

	@Override
	public String[] resolve (Class<?> clazz, Method method, String[] path) {
		return method == null ? generatePathFromClass(clazz)
		                      : generatePathFromMethod(method);
	}
	private String[] generatePathFromMethod (Method method) {
		return new String[] { Utils.translateName(method.getName())};
	}
	private String[] generatePathFromClass (Class<?> clazz) {
		List<String[]> allPaths = getPaths(clazz);

		String                   path;
		String[]                 paths;
		PatternsRequestCondition condition = null;

		final String prefix = getPrefix();
		final String suffix = getSuffix();

		/* 倒序遍历以拼装路径 */
		for (int i = allPaths.size() - 1; i > - 1; i--) {
			paths = allPaths.get(i);
			if ((i != allPaths.size() - 1)) {
				for (int j = 0; j < paths.length; j++) {
					path = paths[j];
					if (path.startsWith(prefix)) {
						paths[j] = path.substring(prefix.length(), path.length());
					}
				}
			}
			for (int j = 0; j < paths.length; j++) {
				path = paths[j];
				if (path.endsWith(suffix)) {
					paths[j] = path.substring(0, path.indexOf(suffix));
				}
				paths[j] = Utils.translateName(paths[j]);
			}
			if (condition == null) {
				condition = new PatternsRequestCondition(paths);
			} else {
				condition = condition.combine(new PatternsRequestCondition(paths));
			}

		}

		if (condition == null) {
			return new String[0];
		}

		paths = new String[condition.getPatterns().size()];
		condition.getPatterns().toArray(paths);
		return paths;
	}

	private String getRoot(Annotation annotation) {
		if (annotation instanceof DomainMapping) {
			return ((DomainMapping) annotation).root();
		} else {
			return ((RestDomainMapping) annotation).root();
		}
	}

	private String[] getValue(Annotation annotation) {
		if (annotation instanceof DomainMapping) {
			return ((DomainMapping) annotation).value();
		} else {
			return ((RestDomainMapping) annotation).value();
		}
	}

	private List<String[]> getPaths(Class<?> clazz) {
		List<String[]> allPaths = new ArrayList<>();
		String[]            value;
		String              root;
		/* 遍历并获取类信息直至根元素 */
		while (clazz != Object.class) {

			Annotation annotation = Utils.findAnnotation(clazz);

			if (annotation == null) {
				System.out.println(Utils.getClassDeclaration(clazz) + " 没有注解");
				clazz = Object.class;
				continue;
			}

			if (! getRoot(annotation).isEmpty()) {
				System.out.println(Utils.getClassDeclaration(clazz) + " root 属性非空");
				root = getRoot(annotation);
				root = root.startsWith("/") ? root.substring(1) : root;
				allPaths.add(new String[] { root });
				clazz = Object.class;
				continue;
			}

			value = getValue(annotation);

			if (value.length > 0) {
				allPaths.add(value);
			} else {
				allPaths.add(new String[] { clazz.getSimpleName() });
			}

			clazz = clazz.getSuperclass();
		}
		return allPaths;
	}
}
