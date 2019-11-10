package com.api.routines.jobs.updateCitiesIbge;

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
import com.api.routines.processors.updateCitiesIbge.CustomUpdateCitiesProcessor;
import com.api.routines.readers.updateCitiesIbge.CustomUpdateCitiesReader;
import com.api.routines.writers.updateCitiesIbge.CustomUpdateCitiesWriter;
import com.vinicius.entity.commons.commons.CityEntity;
import com.vinicius.request.response.commons.routines.ibge.update.city.response.CityResponse;

@Configuration
public class UpdateCitiesBatchJob {
	
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    
    public CustomUpdateCitiesReader customUpdateCitiesReader;
    public CustomUpdateCitiesProcessor customUpdateCitiesProcessor;
    public CustomUpdateCitiesWriter customUpdateCitiesWriter;
    
    public ExecutionListener jobCompletionListener;
    
    @Inject
    public UpdateCitiesBatchJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
			CustomUpdateCitiesReader customUpdateCitiesReader, CustomUpdateCitiesProcessor customUpdateCitiesProcessor,
			CustomUpdateCitiesWriter customUpdateCitiesWriter,
			ExecutionListener jobCompletionListener) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.customUpdateCitiesReader = customUpdateCitiesReader;
		this.customUpdateCitiesProcessor = customUpdateCitiesProcessor;
		this.customUpdateCitiesWriter = customUpdateCitiesWriter;
		this.jobCompletionListener = jobCompletionListener;
	}

	@Bean()
    public Job updateCitiesJob() {
        return jobBuilderFactory.get("updateCitiesJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener)
                .flow(updateCitiesStep1())
                .end()
                .build();
    }
    
	@Bean
    public Step updateCitiesStep1() {
        return stepBuilderFactory
        		.get("updateCitiesStep1")
                .<List<CityResponse>, List<CityEntity>>chunk(1)
                .reader(readerUpdateCities())
                .processor(processorUpdateCities())
                .writer(writerUpdateCities())
                .build();
    }
    
	@Bean
    public CustomUpdateCitiesReader readerUpdateCities() {
		return customUpdateCitiesReader;
	}
    
	@Bean
    public CustomUpdateCitiesProcessor processorUpdateCities() {
        return customUpdateCitiesProcessor;
    }
    
	@Bean
	public CustomUpdateCitiesWriter writerUpdateCities() {
		return customUpdateCitiesWriter;
	}
}
