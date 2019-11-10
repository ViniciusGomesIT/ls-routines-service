package com.api.routines.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class IbgeConfig {

	@Value("${ibge.uri.base}")
	private String baseUri;

	@Value("${ibge.city.name}")
	private String cityServiceName;

	@Value("${ibge.city.uri}")
	private String cityUri;

	@Value("${ibge.state.name}")
	private String stateServiceName;

	@Value("${ibge.state.uri}")
	private String stateUri;

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getCityServiceName() {
		return cityServiceName;
	}

	public void setCityServiceName(String cityServiceName) {
		this.cityServiceName = cityServiceName;
	}

	public String getCityUri() {
		return cityUri;
	}

	public void setCityUri(String cityUri) {
		this.cityUri = cityUri;
	}

	public String getStateServiceName() {
		return stateServiceName;
	}

	public void setStateServiceName(String stateServiceName) {
		this.stateServiceName = stateServiceName;
	}

	public String getStateUri() {
		return stateUri;
	}

	public void setStateUri(String stateUri) {
		this.stateUri = stateUri;
	}
}
