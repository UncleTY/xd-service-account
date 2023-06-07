package com.xindong.accounting;

import com.xindong.accounting.testa.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {

	@Resource
	private Test test;

	@RequestMapping("/testA")
	public String test() {
		return test.test();
	}
}
