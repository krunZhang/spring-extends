/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      MethodsConditionConverter.java
 * Date:    18-5-3 下午2:28
 * Author: krun
 */

package com.krun.spring.extend.route.convert;

import com.krun.spring.extend.route.Handler;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

/**
 * @author krun
 * @Date 2018/05/03
 */
public class MethodsConditionConverter
		extends
		AbstractRequestConditionConverter<RequestMethodsRequestCondition> {

	@Override
	public void setCondition (RequestMappingInfo info) {
		condition = info.getMethodsCondition();
	}
	@Override
	public void setValuesToHandler (Handler handler) {
		handler.setMethods(getValues());
	}
	@Override
	protected String[] values () {
		Set<RequestMethod> methodSet = condition.getMethods();
		String[] values = new String[methodSet.size()];
		RequestMethod[] methods = new RequestMethod[methodSet.size()];
		methodSet.toArray(methods);
		for (int i = 0; i < methods.length; i++) {
			values[i] = methods[i].name();
		}
		return values;
	}
}
