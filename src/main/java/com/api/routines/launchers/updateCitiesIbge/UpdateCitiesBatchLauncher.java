package com.api.routines.launchers.updateCitiesIbge;

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

import com.api.routines.jobs.updateCitiesIbge.UpdateCitiesBatchJob;

@Component(value = "updateCitiesBatchLauncher")
public class UpdateCitiesBatchLauncher {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateCitiesBatchLauncher.class);
	
	private JobLauncher launcher;
	private UpdateCitiesBatchJob updateCitiesBatchJob;
	
	@Inject
	public UpdateCitiesBatchLauncher(JobLauncher launcher, UpdateCitiesBatchJob updateCitiesBatchJob) {
		this.launcher = launcher;
		this.updateCitiesBatchJob = updateCitiesBatchJob;
	}

	public void lauchBatch() {
		LOG.info("Starting Batch {}", this.getClass().getSimpleName());

		JobParameters jobParameter = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.addString("batchName", this.getClass().getSimpleName().toUpperCase())
				.toJobParameters();
		
		try {
			launcher.run(updateCitiesBatchJob.updateCitiesJob(), jobParameter);
			
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
