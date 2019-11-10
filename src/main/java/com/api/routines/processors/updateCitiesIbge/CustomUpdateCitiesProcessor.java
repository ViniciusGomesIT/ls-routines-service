package com.api.routines.processors.updateCitiesIbge;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.api.routines.repository.CityRepository;
import com.api.routines.repository.StateRepository;
import com.vinicius.entity.commons.commons.CityEntity;
import com.vinicius.entity.commons.commons.StateEntity;
import com.vinicius.request.response.commons.routines.ibge.update.city.response.CityResponse;
import com.vinicius.util.commons.parse.ParseUtils;

@Component
public class CustomUpdateCitiesProcessor implements ItemProcessor<List<CityResponse>, List<CityEntity>> {
	
	private CityRepository cityRepository;
	private StateRepository stateRepository;
	
	private ParseUtils parseUtils;

	@Inject
	public CustomUpdateCitiesProcessor(CityRepository cityRepository, StateRepository stateRepository, ParseUtils parseUtils) {
		this.cityRepository = cityRepository;
		this.stateRepository = stateRepository;
		
		this.parseUtils = parseUtils;
	}

	@Override
	public List<CityEntity> process(List<CityResponse> listCityResponse) throws Exception {
		
		if ( !listCityResponse.isEmpty() ) {
			return listCityResponse.stream()
					.map(cityResponse -> parseUtils.objectParser(cityResponse, CityEntity.class))
					.filter(Objects::nonNull)
					.map(cityEntity -> checkUnsavedCities(cityEntity))
					.filter(cityEntityChecked -> cityEntityChecked.getId() == null)
					.map(cityEntityFilted -> checkCityState(cityEntityFilted))
					.collect(Collectors.toList());
		}
		
		return Collections.emptyList();
	}
	
	private CityEntity checkUnsavedCities(CityEntity cityEntity) {
		Optional<CityEntity> cityOpt = this.cityRepository.findByNameIgnoreCase(cityEntity.getName());
		
		if ( cityOpt.isPresent() ) {
			return cityOpt.get();
		} else {
			return cityEntity;
		}
	}
	
	private CityEntity checkCityState(CityEntity cityEntityFilted) {
		Optional<StateEntity> stateEntityOpt = this.stateRepository.findByNameIgnoreCase(cityEntityFilted.getState().getName());
		
		if ( stateEntityOpt.isPresent() ) {
			cityEntityFilted.setState(stateEntityOpt.get());
		} else {
			StateEntity state = this.stateRepository.save(cityEntityFilted.getState());
			cityEntityFilted.setState(state);
		}
		
		return cityEntityFilted;
	}
}
