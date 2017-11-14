package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.controller.support.Constant;
import com.chinacloud.oneaa.common.entity.UserInfo;
import com.chinacloud.oneaa.common.exception.AuthenticationFailureException;
import com.chinacloud.oneaa.common.service.OneAAService;
import com.chinacloud.oneaa.common.service.OneAAServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;



@Controller
public class BaseController {

	@Autowired
	protected HttpServletRequest request;

	protected OneAAService oneAAService = new OneAAServiceImpl();
	
	protected UserInfo getUserInfo() {

		UserInfo userInfo = null;

		String xAuthToken = request.getHeader(Constant.XAUTH_TOKEN);

		if (StringUtils.isNotBlank(xAuthToken)){
			try {
				userInfo =  oneAAService.isValidateToken(xAuthToken);
			} catch (AuthenticationFailureException e) {
				e.printStackTrace();
			}
		}
		return userInfo;

	}
	protected String getUserId() {
		UserInfo userInfo = getUserInfo();
		if (userInfo != null){
			return userInfo.getId();
		}
		return "";
	}
	protected String getUserName() {
		UserInfo userInfo = getUserInfo();
		if (userInfo != null){
			return userInfo.getUsername();
		}
		return "unknown";
	}
	protected String getUserPrefix() {
		String userName = getUserName();
		if (StringUtils.isNotBlank(userName)){
			String[] userStr = userName.split("@");
			return userStr[0];
		}
		return  "";
	}

}
