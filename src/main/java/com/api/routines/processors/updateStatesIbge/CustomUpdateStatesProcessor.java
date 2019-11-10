package com.api.routines.processors.updateStatesIbge;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.api.routines.repository.StateRepository;
import com.vinicius.entity.commons.commons.StateEntity;
import com.vinicius.request.response.commons.routines.ibge.update.state.response.StateResponse;
import com.vinicius.util.commons.parse.ParseUtils;

@Component
public class CustomUpdateStatesProcessor implements ItemProcessor<List<StateResponse>, List<StateEntity>> {
	
	private StateRepository repository;
	
	private ParseUtils parseUtils;

	@Inject
	public CustomUpdateStatesProcessor(StateRepository repository, ParseUtils parseUtils) {
		this.repository = repository;
		this.parseUtils = parseUtils;
	}

	@Override
	public List<StateEntity> process(List<StateResponse> listStateResponse) throws Exception {
		
		if ( !listStateResponse.isEmpty() ) {
			return listStateResponse.stream()
				.map(stateResponse -> parseUtils.objectParser(stateResponse, StateEntity.class))
				.filter(Objects::nonNull)
				.map(stateEntity -> checkUnsavedStates(stateEntity))
				.filter(stateEntityChecked -> stateEntityChecked.getId() == null)
				.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}
	
	private StateEntity checkUnsavedStates(StateEntity stateEntity) {
		Optional<StateEntity> state = this.repository.findByNameIgnoreCase(stateEntity.getName());
		
		if ( state.isPresent() ) {
			return state.get();
		} else {
			return stateEntity;
		}
	}
}
