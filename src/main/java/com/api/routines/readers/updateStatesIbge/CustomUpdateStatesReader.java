package com.api.routines.readers.updateStatesIbge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.api.routines.clients.IbgeClient;
import com.api.routines.repository.ExecutedRoutinesInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinicius.entity.commons.routines.RoutinesExecutedInfoEntity;
import com.vinicius.request.response.commons.routines.ibge.update.state.response.StateResponse;

import feign.Response;

@Component
public class CustomUpdateStatesReader implements ItemReader<List<StateResponse>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomUpdateStatesReader.class);
	
	private IbgeClient ibgeClient;
	private ExecutedRoutinesInfoRepository executionRepository;
	
	private List<StateResponse> listResponseParsed;
	
	private String batchName;
	
	@Inject
	public CustomUpdateStatesReader(IbgeClient ibgeClient, ExecutedRoutinesInfoRepository executionRepository) {
		this.ibgeClient = ibgeClient;
		this.executionRepository = executionRepository;
	}
	
	@BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobParameters parameters = stepExecution.getJobExecution().getJobParameters();
        this.batchName = parameters.getString("batchName");
	}

	@Override
	public List<StateResponse> read() {
		Optional<RoutinesExecutedInfoEntity> executionEntityOpt = this.executionRepository.findFirstByBatchNameOrderByStartedDesc(batchName);

		if (executionEntityOpt.isPresent() && null == executionEntityOpt.get().getLastExecution()) {
			ibgeStatesIntegration();
			
			updateExecutionEntity(executionEntityOpt.get());

			return this.listResponseParsed;
		}

		return null;
	}

	private void ibgeStatesIntegration() {
		feign.Response listResponse = ibgeClient.getStates();
		
		//TODO Replace it to an interceptor
		String responseString;
		JSONArray jsonArrayParsedFromIntegration;
		
		try {
			responseString = parseGzipToString(listResponse);
			jsonArrayParsedFromIntegration = getJsonArray(responseString);
			listResponseParsed = getResponseParsed(jsonArrayParsedFromIntegration);
			
		} catch (IOException | JSONException e) {
			LOG.info("There was an error while trying to parse response in batch {}. Cause {} Message {}", this.getClass().getSimpleName(), e.getCause(), e.getMessage());
			LOG.info(Arrays.toString(e.getStackTrace()));
		}
	}
	
	private String parseGzipToString(Response responseString) throws IOException  {
		InputStream responseBodyDecompressed = null;
		String responseBody = null;
		
		responseBodyDecompressed = new GZIPInputStream(responseString.body().asInputStream());
		responseBody = convertStreamToString(responseBodyDecompressed);
		
		return responseBody;
	}
    
    public String convertStreamToString(InputStream in) throws IOException {
    	//TODO melhorar lógica usando try with resource
        if (in != null) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int length = 0;
            
            while ((length = in.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            String response = new String(baos.toByteArray());

            baos.close();
            
            return response;

        } else {
            return null;
        }
    }
    
	private JSONArray getJsonArray(String responseString) throws JSONException {
		return new JSONArray(responseString);
	}
	
	private List<StateResponse> getResponseParsed(JSONArray array) throws IOException, JSONException {
		List<StateResponse> listResponse = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		StateResponse stateResponse;
		
		for (int i = 0; i < array.length(); i++) {
			stateResponse = mapper.readValue(array.getJSONObject(i).toString(), StateResponse.class);
			
			listResponse.add(stateResponse);
		}
			
		return listResponse;
	}

	private void updateExecutionEntity(RoutinesExecutedInfoEntity routinesExecutedInfoEntity) {
		routinesExecutedInfoEntity.setLastExecution(new Date());
		
		this.executionRepository.save(routinesExecutedInfoEntity);
	}
}
