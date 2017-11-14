package com.cetc.hubble.metagrid.controller.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.chinacloud.oneaa.common.entity.UserInfo;
import com.chinacloud.oneaa.common.exception.AuthenticationFailureException;
import com.chinacloud.oneaa.common.service.OneAAService;
import com.chinacloud.oneaa.common.service.OneAAServiceImpl;
import com.chinacloud.oneaa.common.service.UserPrivService;
import com.chinacloud.oneaa.common.service.UserPrivServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OneAAFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${sdk.oneaa.auth.bypass}")
	public Boolean authBypass;
	@Value("${sdk.oneaa.prefix}")
	public String prefix;

	protected FilterConfig config;

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		HttpServletRequest hservletRequest = (HttpServletRequest)servletRequest;

		OneAAService oneAAService = new OneAAServiceImpl();
		UserPrivService userPrivService = new UserPrivServiceImpl();

		try {
			String e = hservletRequest.getRequestURI();
			String contextPath = hservletRequest.getContextPath();
			System.out.println("requestURI==" + e);
			logger.info("requestURI==" + e);
			logger.debug("contextPath==" + contextPath);
			String xAuthToken = hservletRequest.getHeader(Constant.XAUTH_TOKEN);
			String authorization = hservletRequest.getHeader(Constant.AUTHORIZATION);
			String method = hservletRequest.getMethod();
			String url = e.replace(contextPath, "").replace(prefix, "");
			logger.debug("xAuthToken==" + xAuthToken);
			logger.debug("authorization==" + authorization);
			logger.debug("request--url==" + url);
			if(this.authBypass) {
				filterChain.doFilter(servletRequest, servletResponse);
			} else if("OPTIONS".equalsIgnoreCase(method)) {
				filterChain.doFilter(servletRequest, servletResponse);
			} else if(userPrivService.isPublicAPI(url, method)) {
				filterChain.doFilter(servletRequest, servletResponse);
			} else if(authorization != null && !"".equals(authorization) && authorization.contains("Bearer")) {
				String userInfo1 = authorization.replace("Bearer", "");
				String[] tenantIds1 = userInfo1.split("&");
				if(tenantIds1 != null && tenantIds1.length > 1) {
					String key = tenantIds1[0].split("=")[1];
					String token = tenantIds1[1].split("=")[1];
					final String keyCloakKey = com.chinacloud.oneaa.common.constant.Constant.initDataMap.get("oneaa.clientId");
					final String keyCloakToken = com.chinacloud.oneaa.common.constant.Constant.initDataMap.get("oneaa.secret");
					if(keyCloakKey.equals(key) && keyCloakToken.equals(token)) {
						String userName = hservletRequest.getHeader(Constant.REQUEST_ONE_AA_USER_NAME);
						hservletRequest.setAttribute(Constant.REQUEST_CONTEXT_USER_NAME, userName);
						filterChain.doFilter(hservletRequest, servletResponse);
					} else {
						this.responseAuthFailure(response, 407, "Secret-Key or ID don\'t match");
					}
				} else {
					this.responseAuthFailure(response, 401, Constant.AUTHORIZATION + " expired.");
				}
			} else if(xAuthToken != null && !"".equals(xAuthToken)) {
				UserInfo userInfo = oneAAService.isValidateToken(xAuthToken);
				if(userInfo != null) {
					if(userPrivService.isProtectedAPI(url, method)) {
						if(userPrivService.hasPrivilege(url, method, userInfo.getId())) {
							filterChain.doFilter(servletRequest, servletResponse);
						} else {
							this.responseAuthFailure(response, 403, Constant.FORBIDDEN);
						}
					} else {
						filterChain.doFilter(servletRequest, servletResponse);
					}
				} else {
					this.responseAuthFailure(response, 401, Constant.XAUTH_TOKEN + " expired.");
				}
			} else {
				this.responseAuthFailure(response, 401, Constant.XAUTH_TOKEN + " expired.");
			}
		} catch (AuthenticationFailureException var17) {
			this.responseAuthFailure(response, 401, Constant.XAUTH_TOKEN + " expired.");
		} catch (Exception e) {
			this.responseAuthFailure(response, 500, "Internal Server Error");
		}

	}

	private void responseAuthFailure(HttpServletResponse response, int status, String message)
			throws IOException {
		response.setStatus(status);
		response.setContentType("application/json;charset=utf-8");
		Writer writer = response.getWriter();
		Map<String, Object> respMap = new HashMap<>();
		respMap.put("code", status);
		respMap.put("message", message);
		ObjectMapper om = new ObjectMapper();
		writer.write(om.writeValueAsString(respMap));
		writer.flush();
	}
}