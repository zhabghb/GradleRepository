package com.cetc.hubble.metagrid.config;

import com.cetc.hubble.metagrid.controller.support.OneAAFilter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;


@Configuration
public class OneAAAuthFilterConfiguration implements ServletContextInitializer {


    public FilterRegistrationBean delegateFilter(ServletContext servletContext)
            throws ServletException {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new OneAAFilter());
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
/*

    @Bean
    public FilterRegistrationBean someFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AuthFilter());
        registration.addUrlPatterns("*/
/*");
        registration.addInitParameter("targetBeanName", "authFilterSeriver");
        registration.addInitParameter("targetFilterLifecycle", "true");
        registration.setName("authFilter");
        return registration;
    }
*/

    /*@Bean(name = "authFilter")
    public Filter authFilter() {
        return new DelegatingFilterProxy();
    }*/

    public void onStartup(ServletContext servletContext)
            throws ServletException {
        // Auto-generated method stub
        delegateFilter(servletContext);
    }
}