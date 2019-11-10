package com.api.routines.writers.updateCitiesIbge;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.api.routines.repository.CityRepository;
import com.vinicius.entity.commons.commons.CityEntity;

@Component
public class CustomUpdateCitiesWriter implements ItemWriter<List<CityEntity>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomUpdateCitiesWriter.class);
	
	private CityRepository repository;
	
	@Inject
	public CustomUpdateCitiesWriter(CityRepository repository) {
		this.repository = repository;
	}

	@Override
	public void write(List<? extends List<CityEntity>> cityEntitiesToSaveList) throws Exception {
		List<CityEntity> cityEntitySavedList;
		
		if ( !cityEntitiesToSaveList.isEmpty() ) {
			cityEntitySavedList = cityEntitiesToSaveList.get(0)
					.stream()
					.map(cityEntity -> repository.save(cityEntity))
					.collect(Collectors.toList());
			
			cityEntitySavedList.forEach(cityEntitySaved -> printInfos(cityEntitySaved));
			
		}
	}

	private void printInfos(CityEntity cityEntitySaved) {
		LOG.info("City {} was added", cityEntitySaved.getName());
	}
}
