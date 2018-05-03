/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      ConsumesConditionConverter.java
 * Date:    18-5-3 下午2:41
 * Author: krun
 */

package com.krun.spring.extend.route.convert;

import com.krun.spring.extend.route.Handler;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

/**
 * @author krun
 * @date 2018/05/03
 */
public class ConsumesConditionConverter extends AbstractRequestConditionConverter<ConsumesRequestCondition> {

	@Override
	public void setCondition (RequestMappingInfo info) {
		condition = info.getConsumesCondition();
	}
	@Override
	public void setValuesToHandler (Handler handler) {
		handler.setConsumes(getValues());
	}
	@Override
	protected String[] values () {
		Set<MediaType> consumableMediaTypes = condition.getConsumableMediaTypes();
		String[] values = new String[consumableMediaTypes.size()];
		MediaType[] types = new MediaType[consumableMediaTypes.size()];
		consumableMediaTypes.toArray(types);
		for (int i = 0; i < types.length; i++) {
			values[i] = types[i].toString();
		}
		return new String[0];
	}
}
