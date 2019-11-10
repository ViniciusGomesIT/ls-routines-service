package com.api.routines.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinicius.entity.commons.routines.RoutinesExecutedInfoEntity;

public interface ExecutedRoutinesInfoRepository extends JpaRepository<RoutinesExecutedInfoEntity, Long> {

	Optional<RoutinesExecutedInfoEntity> findFirstByBatchNameOrderByStartedDesc(String batchName);
}
