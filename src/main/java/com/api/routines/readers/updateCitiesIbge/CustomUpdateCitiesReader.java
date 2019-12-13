package com.api.routines.readers.updateCitiesIbge;

import com.api.routines.clients.IbgeClient;
import com.api.routines.model.IbgeConfig;
import com.api.routines.repository.ExecutedRoutinesInfoRepository;
import com.api.routines.services.HttpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinicius.entity.commons.routines.RoutinesExecutedInfoEntity;
import com.vinicius.request.response.commons.routines.ibge.update.city.response.CityResponse;
import feign.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

@Component
public class CustomUpdateCitiesReader implements ItemReader<List<CityResponse>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomUpdateCitiesReader.class);
	
	private IbgeClient ibgeClient;
	private ExecutedRoutinesInfoRepository executionRepository;
	private HttpService httpService;
	private IbgeConfig config;
	
	private List<CityResponse> listResponseParsed;
	
	private String batchName;
	
	@Inject
	public CustomUpdateCitiesReader(IbgeClient ibgeClient,
									ExecutedRoutinesInfoRepository executionRepository,
									HttpService httpService,
									IbgeConfig config) {
		this.ibgeClient = ibgeClient;
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
	public List<CityResponse> read() {
		Optional<RoutinesExecutedInfoEntity> executionEntityOpt = this.executionRepository.findFirstByBatchNameOrderByStartedDesc(batchName);
		
		if ( executionEntityOpt.isPresent() && null == executionEntityOpt.get().getLastExecution() ) {
			ibgeCitiesIntegration();
			
			updateExecutionEntity(executionEntityOpt.get());
			
			return this.listResponseParsed;
		}
		
		return null;
	}
	/*
	private void ibgeCitiesIntegration() {
		feign.Response listResponse = ibgeClient.getCities();
		
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
	*/
	private void ibgeCitiesIntegration() {
		List<CityResponse> listCities = httpService.doRequest(config.getBaseUri().concat(config.getCityUri()), null);
	}
	
	private String parseGzipToString(Response responseString) throws IOException {
		InputStream responseBodyDecompressed = null;
		String responseBody = null;
		
		responseBodyDecompressed = new GZIPInputStream(responseString.body().asInputStream());
		responseBody = convertStreamToString(responseBodyDecompressed);
		
		return responseBody;
	}
    
    public String convertStreamToString(InputStream in) throws IOException {
    	//TODO melhorar lógica usando try with resource
        if ( in != null ) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int length = 0;
            
            while ( (length = in.read(buffer) ) != -1) {
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
	
	private List<CityResponse> getResponseParsed(JSONArray array) throws IOException, JSONException {
		List<CityResponse> listResponse = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		CityResponse cityResponse;
		
		for (int i = 0; i < array.length(); i++) {
			cityResponse = mapper.readValue(array.getJSONObject(i).toString(), CityResponse.class);
			
			listResponse.add(cityResponse);
		}
			
		return listResponse;
	}
	
	private void updateExecutionEntity(RoutinesExecutedInfoEntity routinesExecutedInfoEntity) {
		routinesExecutedInfoEntity.setLastExecution(new Date());
		
		this.executionRepository.save(routinesExecutedInfoEntity);
	}
}
