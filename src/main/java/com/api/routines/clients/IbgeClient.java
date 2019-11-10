package com.api.routines.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${ibge.name}", url = "${ibge.uri.base}")
public interface IbgeClient {

	@GetMapping(value = "${ibge.state.uri}")
	feign.Response getStates();
	
	@GetMapping(value = "${ibge.city.uri}")
	feign.Response getCities();
}
