package com.xindong.accounting.testb;

import com.xindong.accounting.api.IntB;
import org.springframework.stereotype.Component;

@Component
public class IntAImpl implements IntB {

	@Override
	public String testA() {
		return "B";
	}
}
