package com.cetc.hubble.metagrid.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Created by yuson on 2016-07-01.
 */
@Configuration
@EnableSwagger2
@EnableAutoConfiguration
public class Swagger2Config {

    @Bean
    public Docket metaGridApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("metagrid-api")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/v2/api.*"))
                .build()
                .enableUrlTemplating(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("")
                .description("")
                .termsOfServiceUrl("http://www.chinacloud.com.cn")
                .contact("divingfish")
//                .license("Apache License Version 2.0")
//                .licenseUrl("http://www.chinacloud.com.cn/LICENSE")
                .version("2.0")
                .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/v2/api-docs").allowCredentials(true).allowedMethods("*").allowedOrigins("*").allowedHeaders("*");
            }
        };
    }
    /*
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/v2/api-docs", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }*/
}
