package com.api.routines.controllers;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.api.routines.launchers.updateCitiesIbge.UpdateCitiesBatchLauncher;
import com.api.routines.launchers.updateStatesIbge.UpdateStatesBatchLauncher;

@Controller
public class BatchLaunchController {
	
	private UpdateStatesBatchLauncher updateStatesBatchLauncher;
	private UpdateCitiesBatchLauncher updateCitiesBatchLauncher;
	
	@Inject
	public BatchLaunchController(UpdateStatesBatchLauncher updateStatesBatchLauncher,
			UpdateCitiesBatchLauncher updateCitiesBatchLauncher) {
		this.updateStatesBatchLauncher = updateStatesBatchLauncher;
		this.updateCitiesBatchLauncher = updateCitiesBatchLauncher;
	}

	@RequestMapping("/batch/launch/{jobName}")
	public BodyBuilder runBatch(@PathVariable String jobName) {
		
		switch (jobName) {
		case "state":
			updateStatesBatchLauncher.lauchBatch();
			break;
		case "city":
			updateCitiesBatchLauncher.lauchBatch();
			break;
		default:
			break;
		}
		
		return ResponseEntity.ok();
	}
}
