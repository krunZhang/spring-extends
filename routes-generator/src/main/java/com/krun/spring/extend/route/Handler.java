/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      Handler.java
 * Date:    18-5-3 上午8:07
 * Author: krun
 */

package com.krun.spring.extend.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author krun
 * @date 2018/05/03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Handler {

	String name;
	String url;
	String[] methods;
	String[] params;
	String[] headers;
	String[] consumes;
	String[] produces;
}
