package com.api.routines.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinicius.entity.commons.commons.StateEntity;

public interface StateRepository extends JpaRepository<StateEntity, Long> {
	
	Optional<StateEntity> findByNameIgnoreCase(String name);
}
