package com.api.routines.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinicius.entity.commons.commons.CityEntity;

public interface CityRepository extends JpaRepository<CityEntity, Long> {

	Optional<CityEntity> findByNameIgnoreCase(String name);
	
	Optional<CityEntity> findByCode(Long code);
}
