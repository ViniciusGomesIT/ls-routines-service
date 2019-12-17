package com.api.routines.readers.updateCitiesIbge;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.tomcat.util.json.ParseException;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.api.routines.model.IbgeConfig;
import com.api.routines.repository.ExecutedRoutinesInfoRepository;
import com.api.routines.services.HttpService;
import com.vinicius.entity.commons.routines.RoutinesExecutedInfoEntity;
import com.vinicius.request.response.commons.routines.ibge.update.city.response.CityResponse;

@Component
public class CustomUpdateCitiesReader implements ItemReader<List<CityResponse>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomUpdateCitiesReader.class);
	
	private ExecutedRoutinesInfoRepository executionRepository;
	private HttpService httpService;
	private IbgeConfig config;
	
	private List<CityResponse> listResponseParsed;
	
	private String batchName;
	
	@Inject
	public CustomUpdateCitiesReader(ExecutedRoutinesInfoRepository executionRepository,
									HttpService httpService,
									IbgeConfig config) {
		this.executionRepository = executionRepository;
		this.httpService = httpService;
		this.config = config;
	}
	
	@BeforeStep
    public void beforeStep(StepExecution stepExecution) {
		JobParameters parameters = stepExecution.getJobExecution().getJobParameters();
        this.batchName = parameters.getString("batchName");
	}

	@Override
	public List<CityResponse> read() throws ParseException, JSONException {
		Optional<RoutinesExecutedInfoEntity> executionEntityOpt = this.executionRepository.findFirstByBatchNameOrderByStartedDesc(batchName);
		
		if ( executionEntityOpt.isPresent() && Objects.isNull(executionEntityOpt.get().getLastExecution()) ) {
			ibgeCitiesIntegration();
			
			updateExecutionEntity(executionEntityOpt.get());
			
			return this.listResponseParsed;
		}
		
		return null;
	}

	private void ibgeCitiesIntegration() throws ParseException, JSONException {
		listResponseParsed = httpService.doRequest(config.getBaseUri().concat(config.getCityUri()));
	}
	
	private void updateExecutionEntity(RoutinesExecutedInfoEntity routinesExecutedInfoEntity) {
		routinesExecutedInfoEntity.setLastExecution(new Date());
		
		this.executionRepository.save(routinesExecutedInfoEntity);
	}
}
