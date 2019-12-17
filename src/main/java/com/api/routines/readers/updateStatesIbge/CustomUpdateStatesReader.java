package com.api.routines.readers.updateStatesIbge;

import java.util.Date;
import java.util.List;
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
import com.vinicius.request.response.commons.routines.ibge.update.state.response.StateResponse;

@Component
public class CustomUpdateStatesReader implements ItemReader<List<StateResponse>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomUpdateStatesReader.class);
	
	private ExecutedRoutinesInfoRepository executionRepository;
	private HttpService httpService;
	private IbgeConfig config;
	
	private List<StateResponse> listResponseParsed;
	
	private String batchName;
	
	@Inject
	public CustomUpdateStatesReader(ExecutedRoutinesInfoRepository executionRepository,
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
	public List<StateResponse> read() throws ParseException, JSONException {
		Optional<RoutinesExecutedInfoEntity> executionEntityOpt = this.executionRepository.findFirstByBatchNameOrderByStartedDesc(batchName);

		if (executionEntityOpt.isPresent() && null == executionEntityOpt.get().getLastExecution()) {
			ibgeStatesIntegration();
			
			updateExecutionEntity(executionEntityOpt.get());

			return this.listResponseParsed;
		}

		return null;
	}

	private void ibgeStatesIntegration() throws ParseException, JSONException {
		listResponseParsed = httpService.doRequest(config.getBaseUri().concat(config.getStateUri()));
	}
	

	private void updateExecutionEntity(RoutinesExecutedInfoEntity routinesExecutedInfoEntity) {
		routinesExecutedInfoEntity.setLastExecution(new Date());
		
		this.executionRepository.save(routinesExecutedInfoEntity);
	}
}
