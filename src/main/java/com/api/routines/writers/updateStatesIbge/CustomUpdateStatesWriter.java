package com.api.routines.writers.updateStatesIbge;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.api.routines.repository.StateRepository;
import com.vinicius.entity.commons.commons.StateEntity;

@Component
public class CustomUpdateStatesWriter implements ItemWriter<List<StateEntity>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomUpdateStatesWriter.class);
	
	private StateRepository repository;
	
	@Inject
	public CustomUpdateStatesWriter(StateRepository repository) {
		this.repository = repository;
	}

	@Override
	public void write(List<? extends List<StateEntity>> stateEntitiesToSaveList) throws Exception {
		List<StateEntity> stateEntitySavedList;
		
		if ( !stateEntitiesToSaveList.get(0).isEmpty() ) {
			stateEntitySavedList = stateEntitiesToSaveList.get(0)
					.stream()
					.map(stateEntity -> repository.save(stateEntity))
					.collect(Collectors.toList());
			
			stateEntitySavedList.forEach(stateEntitySaved -> printInfos(stateEntitySaved));
		}
	}

	private void printInfos(StateEntity stateEntitySaved) {
		LOG.info("State {} was added", stateEntitySaved.getName().concat(" - ").concat(stateEntitySaved.getInitials()));
	}
}
