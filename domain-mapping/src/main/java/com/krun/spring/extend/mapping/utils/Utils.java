/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      Utils.java
 * Date:    18-5-1 下午3:26
 * Author: krun
 */

package com.krun.spring.extend.mapping.utils;

import com.krun.spring.extend.mapping.DomainMapping;
import com.krun.spring.extend.mapping.RestDomainMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 一些工具方法
 */
public class Utils {

	public static String getElementDeclaration(AnnotatedElement element) {
		if (element instanceof Class) {
			return getClassDeclaration((Class) element);
		} else if (element instanceof Method) {
			return getMethodDeclaration((Method) element);
		} else {
			return "UNKNOWN";
		}
	}

	public static String getClassDeclaration(Class clazz) {
		StringBuilder builder = new StringBuilder();
		String className = clazz.getName();
		String[] parts = className.split("\\.");
		for (int i = 0; i < parts.length; i++) {
			builder.append((i == parts.length - 1) ? parts[i] : parts[i].charAt(0)).append(".");
		}
		return builder.toString();
	}

	public static String getMethodDeclaration(Method method) {
		int modifiers = method.getModifiers();
		StringBuilder builder = new StringBuilder();
		if (Modifier.isPublic(modifiers)) {
			builder.append("public ");
		} else if (Modifier.isProtected(modifiers)) {
			builder.append("protected ");
		} else if (Modifier.isPrivate(modifiers)) {
			builder.append("private ");
		}

		if (Modifier.isStatic(modifiers)) {
			builder.append("static ");
		}

		if (Modifier.isFinal(modifiers)) {
			builder.append("final ");
		}

		builder.append(getClassDeclaration(method.getDeclaringClass()))
		       .append(method.getName())
		       .append("(");

		for (Class<?> paramType : method.getParameterTypes()) {
			builder.append(paramType.getSimpleName()).append(", ");
		}

		if (method.getParameterTypes().length > 0) {
			builder.setLength(builder.length() - 2);
		}

		builder.append(") :").append(method.getReturnType().getSimpleName());
		return builder.toString();
	}

	public static String translateName(String name) {
		StringBuilder builder = new StringBuilder();
		char[] array = name.toCharArray();
		for (int i = 0; i < array.length; i++) {
			char c = array[i];
			if ('a' <= c && c <= 'z') {
				builder.append(c);
			} else if ('A' <= c && c <= 'Z') {
				if (i != 0) {
					builder.append('-');
				}
				builder.append((char)(c + 32));
			} else if ('/' == c) {
				builder.append('.');
			} else  {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	public static Annotation findAnnotation (AnnotatedElement element) {
		Annotation annotation = element.getAnnotation(DomainMapping.class);
		if (annotation == null) {
			annotation = element.getAnnotation(RestDomainMapping.class);
			if (annotation == null) {
				return null;
			}
		}
		return annotation;
	}

}
