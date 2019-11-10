package com.api.routines.listeners;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import com.api.routines.repository.ExecutedRoutinesInfoRepository;
import com.vinicius.entity.commons.routines.RoutinesExecutedInfoEntity;

@Component
public class ExecutionListener extends JobExecutionListenerSupport {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionListener.class);
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    @Inject
    private ExecutedRoutinesInfoRepository routinesInfoRepository;
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
    	String dateFormated = dateFormat.format(jobExecution.getStartTime());
    	
    	createJobExecutionInfoRegister(jobExecution);
    	
    	LOG.info("Batch {} started at {}", jobExecution.getJobInstance().getJobName(), dateFormated);
    }

	@Override
    public void afterJob(JobExecution jobExecution) {
    	String dateFormated = dateFormat.format(jobExecution.getEndTime());
    	String batchName = jobExecution.getJobParameters().getString("batchName");
    	
    	populateJobExecutionInfoRegister(jobExecution);
    	
    	LOG.info("Batch {} finished at {} with status {}", batchName, dateFormated, jobExecution.getStatus());
    }
	
    private void createJobExecutionInfoRegister(JobExecution jobExecution) {
    	String batchName = jobExecution.getJobParameters().getString("batchName");
    	RoutinesExecutedInfoEntity infoEntity = new RoutinesExecutedInfoEntity();
    	
    	infoEntity.setBatchName(batchName);
    	infoEntity.setStarted(jobExecution.getStartTime());
    	
    	this.routinesInfoRepository.save(infoEntity);
	}

	private void populateJobExecutionInfoRegister(JobExecution jobExecution) {
		String batchName = jobExecution.getJobParameters().getString("batchName");
		Time duration = new Time(jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime());
		RoutinesExecutedInfoEntity infoEntity;
		
		Optional<RoutinesExecutedInfoEntity> infoOptional = this.routinesInfoRepository.findFirstByBatchNameOrderByStartedDesc(batchName);
		
		if ( infoOptional.isPresent() ) {
			infoEntity = infoOptional.get();
			
			infoEntity.setLastExecutionStatus(jobExecution.getStatus().toString());
			infoEntity.setFinished(jobExecution.getEndTime());
			infoEntity.setDuration(duration);
			infoEntity.setLastExecution(new Date());
			
			this.routinesInfoRepository.save(infoEntity);
		}
	}
}
