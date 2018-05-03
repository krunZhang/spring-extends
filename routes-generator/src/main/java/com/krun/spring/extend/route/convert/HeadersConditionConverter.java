/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      HeadersConditionConverter.java
 * Date:    18-5-3 下午2:35
 * Author: krun
 */

package com.krun.spring.extend.route.convert;

import com.krun.spring.extend.route.Handler;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

/**
 * @author krun
 * @date 2018/05/03
 */
public class HeadersConditionConverter extends AbstractRequestConditionConverter<HeadersRequestCondition> {

	@Override
	public void setCondition (RequestMappingInfo info) {
		condition = info.getHeadersCondition();
	}
	@Override
	public void setValuesToHandler (Handler handler) {
		handler.setHeaders(getValues());
	}
	@Override
	protected String[] values () {
		return getStrings(condition.getExpressions());
	}
}
