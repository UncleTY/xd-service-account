package com.xindong.accounting.exception;

import com.xindong.accounting.api.IResponseEnum;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

	private IResponseEnum responseEnum;

	private Object[] args;

	private String message;

	public BaseException(IResponseEnum responseEnum, Object[] args, String message) {
		System.out.println(message);
	}

	public BaseException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
		System.out.println(message);
	}
}
