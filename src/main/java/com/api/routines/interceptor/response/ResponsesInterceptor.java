package com.api.routines.interceptor.response;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResponsesInterceptor<T> implements ResponseHandler<T> {

	 private static final Logger LOG = LoggerFactory.getLogger(ResponsesInterceptor.class);

	@Override
	public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		if (response.getStatusLine().getStatusCode() == 200
				|| response.getStatusLine().getStatusCode() == 201 ) {

			if (response.getEntity().getContentEncoding().getName().equalsIgnoreCase("gzip")) {
				LOG.info("passou no if");
			}
		}
		return (T) response;
	}
}
