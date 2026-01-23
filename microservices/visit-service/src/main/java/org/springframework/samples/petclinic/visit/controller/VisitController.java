/*
 * Copyright 2012-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.visit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.samples.petclinic.visit.service.VisitService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Visit resources.
 *
 * @author PetClinic Team
 */
@RestController
@RequestMapping("/api/visits")
public class VisitController {

	private final VisitService visitService;

	public VisitController(VisitService visitService) {
		this.visitService = visitService;
	}

	/**
	 * Get all visits.
	 */
	@GetMapping
	public ResponseEntity<List<Visit>> getAllVisits() {
		List<Visit> visits = visitService.findAllVisits();
		return ResponseEntity.ok(visits);
	}

	/**
	 * Get a single visit by ID.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Visit> getVisit(@PathVariable Integer id) {
		Optional<Visit> visit = visitService.findVisitById(id);
		return visit.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Get visits by pet ID.
	 */
	@GetMapping("/pet/{petId}")
	public ResponseEntity<List<Visit>> getVisitsByPet(@PathVariable Integer petId) {
		List<Visit> visits = visitService.findVisitsByPetId(petId);
		return ResponseEntity.ok(visits);
	}

	/**
	 * Create a new visit.
	 */
	@PostMapping
	public ResponseEntity<Visit> createVisit(@Valid @RequestBody Visit visit) {
		try {
			Visit savedVisit = visitService.saveVisit(visit);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedVisit);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Update an existing visit.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Visit> updateVisit(@PathVariable Integer id, @Valid @RequestBody Visit visit) {
		if (!visitService.visitExists(id)) {
			return ResponseEntity.notFound().build();
		}
		visit.setId(id);
		try {
			Visit updatedVisit = visitService.saveVisit(visit);
			return ResponseEntity.ok(updatedVisit);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Delete a visit.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteVisit(@PathVariable Integer id) {
		if (!visitService.visitExists(id)) {
			return ResponseEntity.notFound().build();
		}
		visitService.deleteVisit(id);
		return ResponseEntity.noContent().build();
	}

}
