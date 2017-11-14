package com.cetc.hubble.metagrid.controller.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cetc.hubble.metagrid.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.common.collect.Maps;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);


	@ExceptionHandler(value = { AppException.class })
	public final ResponseEntity<ErrorResult> handleAppException(AppException ex, HttpServletRequest request) {
		// 注入servletRequest，用于出错时打印请求URL与来源地址
		logError(ex, request);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
		ErrorResult result = new ErrorResult(ex.errorCode.code, ex.getMessage());
		result.data = ex.getErrorData();
		return new ResponseEntity<ErrorResult>(result, headers, HttpStatus.valueOf(ex.errorCode.httpStatus));
	}

	@ExceptionHandler(value = { Exception.class })
	public final ResponseEntity<ErrorResult> handleGeneralException(Exception ex, HttpServletRequest request) {
		logError(ex, request);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
		ErrorResult result = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		return new ResponseEntity<ErrorResult>(result, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 重载ResponseEntityExceptionHandler的方法，加入日志
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		logError(ex);

		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute("javax.servlet.error.exception", ex, WebRequest.SCOPE_REQUEST);
		}

		return new ResponseEntity<Object>(body, headers, status);
	}

	public void logError(Exception ex) {
		logger.error("log exception", ex);
	}

	public void logError(Exception ex, HttpServletRequest request) {
		Map<String, String> map = Maps.newHashMap();
		map.put("message", ex.getMessage());
		map.put("from", request.getRemoteAddr());
		String queryString = request.getQueryString();
		map.put("path", queryString != null ? (request.getRequestURI() + "?" + queryString) : request.getRequestURI());

		logger.error(map.toString(), ex);
	}
}
