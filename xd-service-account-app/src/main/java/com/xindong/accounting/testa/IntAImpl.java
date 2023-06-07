package com.xindong.accounting.testa;

import com.xindong.accounting.api.IntA;
import org.springframework.stereotype.Component;

@Component(value = "compB")
public class IntAImpl implements IntA {
	@Override
	public String testA() {
		return "A";
	}
}
