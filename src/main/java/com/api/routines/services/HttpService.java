package com.api.routines.services;

import com.api.routines.interceptor.response.ResponsesInterceptor;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HttpService {

    private static final Logger LOG = LoggerFactory.getLogger(HttpService.class);

    public <T> T doRequest(String uri, HttpRequest request) {
        CloseableHttpResponse httpresponse = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();

        ResponseHandler<T> responseHandler = new ResponsesInterceptor<T>();

        HttpHost host = new HttpHost(uri);

        try {
            httpresponse = httpclient.execute(host, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T) httpresponse;
    }
}
