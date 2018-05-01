/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      DomainMappingHandler.java
 * Date:    18-5-1 下午2:03
 * Author: krun
 */

package com.krun.spring.extend.mapping.handler;

import com.krun.spring.extend.mapping.DomainMapping;
import com.krun.spring.extend.mapping.RestDomainMapping;
import com.krun.spring.extend.mapping.resolver.MappingNameResolver;
import com.krun.spring.extend.mapping.utils.Utils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.krun.spring.extend.mapping.utils.Utils.findAnnotation;

/**
 * @author krun
 * @date 2018/05/01
 */
public class DomainMappingHandler extends RequestMappingHandlerMapping {

	private MappingNameResolver nameResolver;

	public DomainMappingHandler (MappingNameResolver nameResolver) {
		this.nameResolver = nameResolver;
	}

	private void log(String format, Object ...args) {
		if (this.logger.isInfoEnabled()) {
			this.logger.info(String.format(format, args));
		}
	}

	@Override
	protected boolean isHandler (Class<?> beanType) {
		/* 加入 DomainMapping 和 RestDomainMapping 的识别 */
		return AnnotatedElementUtils.hasAnnotation(beanType, DomainMapping.class)
		|| AnnotatedElementUtils.hasAnnotation(beanType, RestDomainMapping.class)
		|| super.isHandler(beanType);
	}

	@Override
	protected RequestMappingInfo getMappingForMethod (Method method, Class<?> handlerType) {
		RequestMappingInfo info = createRequestMappingInfo(method);
		if (info != null) {
			RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
			if (typeInfo != null) {
				info = typeInfo.combine(info);
			}
		}
		return info;
	}


	private RequestMappingInfo createRequestMappingInfo (AnnotatedElement element) {
		RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
		RequestCondition<?> condition = (element instanceof Class ? getCustomTypeCondition((Class<?>) element)
		                                                          : getCustomMethodCondition((Method) element));

		/* 如果使用了 RequestMapping ，那么使用该注解的属性值 */
		if (requestMapping != null) {
			/* 如果没有设置 value 或 path, 则使用 MappingNameResolver 获取合适的路径 */
			if (requestMapping.path().length == 0) {
				String[] path = generatePath(element);
				requestMapping = generateRequestMapping(requestMapping.name(), path, path, requestMapping.method(),
				                                        requestMapping.params(), requestMapping.headers(),
				                                        requestMapping.consumes(), requestMapping.produces());
				log("Generate path for RequestMapping on { %s %s }", element instanceof Class ? "class" : "method",
				    Utils.getElementDeclaration(element));
			}
		} else {
			/* 既没有 RequestMapping 也没有 DomainMapping 和 RestDomainMapping，则不处理*/
			if (!hasAnnotation(element instanceof Class ? element : ((Method) element).getDeclaringClass())) {
				return null;
			}
			if (element instanceof Method) {
				Method method = (Method) element;

				int modifiers = method.getModifiers();

				/* 检查方法是否合法: 公开、非静态、非抽象 */
				if (! Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isAbstract(modifiers)) {
					return null;
				}
			}
			requestMapping = generateRequestMapping(element);
			log("Generate RequestMapping on { %s %s }", element instanceof Class ? "class" : "method",
			    Utils.getElementDeclaration(element));
		}

		return createRequestMappingInfo(requestMapping, condition);
	}

	private String[] generatePath (AnnotatedElement element) {
		if (element instanceof Class) {
			return nameResolver.resolve((Class<?>) element, null, null);
		} else {
			return nameResolver.resolve(((Method) element).getDeclaringClass(), (Method) element, null);
		}
	}

	/**
	 * 伪造 RequestMapping
 	 */
	private RequestMapping generateRequestMapping (final String name, final String[] value, final String[] path,
	                                               final RequestMethod[] method, final String[] params,
	                                               final String[] headers, final String[] consumes,
	                                               final String[] produces) {
		return new RequestMapping() {

			@Override
			public Class<? extends Annotation> annotationType () {
				return RequestMapping.class;
			}
			@Override
			public String name () {
				return name;
			}
			@Override
			public String[] value () {
				return value;
			}
			@Override
			public String[] path () {
				return path;
			}
			@Override
			public RequestMethod[] method () {
				return method;
			}
			@Override
			public String[] params () {
				return params;
			}
			@Override
			public String[] headers () {
				return headers;
			}
			@Override
			public String[] consumes () {
				return consumes;
			}
			@Override
			public String[] produces () {
				return produces;
			}
		};
	}

	private RequestMapping generateRequestMapping (AnnotatedElement element) {
		Annotation annotation = findAnnotation(element);
		String[] path = generatePath(element);
		if (annotation instanceof DomainMapping) {
			return generateRequestMapping(((DomainMapping) annotation).name(), path, path,
			                              ((DomainMapping) annotation).method(), ((DomainMapping) annotation).params(),
			                              ((DomainMapping) annotation).headers(),
			                              ((DomainMapping) annotation).consumes(),
			                              ((DomainMapping) annotation).produces());
		} else {
				return generateRequestMapping(((RestDomainMapping) annotation).name(), path, path,
				                              ((RestDomainMapping) annotation).method(),
				                              ((RestDomainMapping) annotation).params(),
				                              ((RestDomainMapping) annotation).headers(),
				                              ((RestDomainMapping) annotation).consumes(),
				                              ((RestDomainMapping) annotation).produces());
			}
	}

	private boolean hasAnnotation(AnnotatedElement element) {
		return AnnotatedElementUtils.hasAnnotation(element, DomainMapping.class)
				|| AnnotatedElementUtils.hasAnnotation(element, RestDomainMapping.class);
	}
}