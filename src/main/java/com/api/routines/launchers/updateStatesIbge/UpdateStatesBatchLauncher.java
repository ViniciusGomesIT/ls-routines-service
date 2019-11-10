package com.api.routines.launchers.updateStatesIbge;

import java.util.Arrays;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;

import com.api.routines.jobs.updateStatesIbge.UpdateStatesBatchJob;

@Component(value = "updateStatesBatchLauncher")
public class UpdateStatesBatchLauncher {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateStatesBatchLauncher.class);
	
	private JobLauncher launcher;
	private UpdateStatesBatchJob updateStateBatchJob;
	
	@Inject
	public UpdateStatesBatchLauncher(JobLauncher launcher, UpdateStatesBatchJob updateStateBatchJob) {
		this.launcher = launcher;
		this.updateStateBatchJob = updateStateBatchJob;
	}

	public void lauchBatch() {
			LOG.info("Starting Batch {}", this.getClass().getSimpleName());
	
			JobParameters jobParameter = new JobParametersBuilder()
					.addLong("time", System.currentTimeMillis())
					.addString("batchName", this.getClass().getSimpleName().toUpperCase())
					.toJobParameters();
			
			try {
				launcher.run(updateStateBatchJob.updateStatesJob(), jobParameter);
				
				LOG.info("Batch finalized");
				
			} catch (JobExecutionAlreadyRunningException
					| JobRestartException 
					| JobInstanceAlreadyCompleteException 
					| JobParametersInvalidException e) {
				
				LOG.error("Error While trying to execute batch. Cause: {} Message: {}", e.getCause(), e.getMessage());
				LOG.error(Arrays.toString(e.getStackTrace()));
			}
	}
}
