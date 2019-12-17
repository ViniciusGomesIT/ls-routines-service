package com.api.routines.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class HttpService {

    private static final Logger LOG = LoggerFactory.getLogger(HttpService.class);
	private HttpClient client;

	//TODO refatorar para um interceptor
	public <T> T doRequest(String uri) throws UnsupportedOperationException, ParseException, JSONException {
    	client = new DefaultHttpClient();
    	HttpGet get = new HttpGet(uri);
    	Object json = null;
    	
    	HttpResponse response;
		try {
			response = client.execute(get);
			
			if (response.getFirstHeader("Content-Encoding").getValue().equalsIgnoreCase("gzip")) {
				json = getGzipParsedToJson(response.getEntity().getContent());
			} else {
				json = getResponseParsedToJson(response.getEntity().getContent());
			}
			
		} catch (IOException e) {
			LOG.error("Ocorreu um erro ao tentar obter os dados");
			LOG.error(Arrays.toString(e.getStackTrace()));
		}
    	
        return (T) json;
    }

	private Object getGzipParsedToJson(InputStream content) throws IOException, ParseException {
		return new JSONParser(new GZIPInputStream(content)).parse();
	}

	private Object getResponseParsedToJson(InputStream content) throws IOException, ParseException {
		return new JSONParser(new GZIPInputStream(content)).parse();
	}
}
