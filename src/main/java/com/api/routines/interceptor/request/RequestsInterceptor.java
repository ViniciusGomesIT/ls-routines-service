package com.api.routines.interceptor.request;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;


@Configuration
public class RequestsInterceptor {
	
	@Bean
	RequestInterceptor gzipInterceptor() {
		return template -> template.header("Accept-Encoding", "gzip");
	}
}
