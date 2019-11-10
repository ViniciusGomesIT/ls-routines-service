package com.api.routines.jobs.updateStatesIbge;

import java.util.List;

import javax.inject.Inject;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.api.routines.listeners.ExecutionListener;
import com.api.routines.processors.updateStatesIbge.CustomUpdateStatesProcessor;
import com.api.routines.readers.updateStatesIbge.CustomUpdateStatesReader;
import com.api.routines.writers.updateStatesIbge.CustomUpdateStatesWriter;
import com.vinicius.entity.commons.commons.StateEntity;
import com.vinicius.request.response.commons.routines.ibge.update.state.response.StateResponse;

@Configuration
public class UpdateStatesBatchJob {
	
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    
    public CustomUpdateStatesReader customUpdateStatesReader;
    public CustomUpdateStatesProcessor customUpdateStatesProcessor;
    public CustomUpdateStatesWriter customUpdateStatesWriter;
    
    public ExecutionListener jobCompletionListener;
    
    @Inject
    public UpdateStatesBatchJob(JobBuilderFactory jobBuilderFactory,
			StepBuilderFactory stepBuilderFactory, CustomUpdateStatesReader customUpdateStatesReader,
			CustomUpdateStatesProcessor customUpdateStatesProcessor,
			CustomUpdateStatesWriter customUpdateStatesWriter,
			ExecutionListener jobCompletionListener) {
    	
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.customUpdateStatesReader = customUpdateStatesReader;
		this.customUpdateStatesProcessor = customUpdateStatesProcessor;
		this.customUpdateStatesWriter = customUpdateStatesWriter;
		this.jobCompletionListener = jobCompletionListener;
	}

    @Bean
    public Job updateStatesJob() {
        return jobBuilderFactory.get("updateStatesJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener)
                .flow(updateStatesStep1())
                .end()
                .build();
    }
    
	@Bean
    public Step updateStatesStep1() {
        return stepBuilderFactory
        		.get("updateStatesStep1")
                .<List<StateResponse>, List<StateEntity>>chunk(1)
                .reader(readerUpdateStates())
                .processor(processorUpdateStates())
                .writer(writerUpdateStates())
                .build();
    }
    
	@Bean
    public CustomUpdateStatesReader readerUpdateStates() {
		return customUpdateStatesReader;
	}
    
	@Bean
    public CustomUpdateStatesProcessor processorUpdateStates() {
        return customUpdateStatesProcessor;
    }
    
	@Bean
	public CustomUpdateStatesWriter writerUpdateStates() {
		return customUpdateStatesWriter;
	}
}
