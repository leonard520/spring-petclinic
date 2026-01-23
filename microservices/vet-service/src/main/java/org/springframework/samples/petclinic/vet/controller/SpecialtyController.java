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
package org.springframework.samples.petclinic.vet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vet.model.Specialty;
import org.springframework.samples.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Specialty resources.
 *
 * @author PetClinic Team
 */
@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

	private final SpecialtyRepository specialtyRepository;

	public SpecialtyController(SpecialtyRepository specialtyRepository) {
		this.specialtyRepository = specialtyRepository;
	}

	/**
	 * Get all specialties.
	 */
	@GetMapping
	public ResponseEntity<List<Specialty>> getAllSpecialties() {
		List<Specialty> specialties = specialtyRepository.findAll();
		return ResponseEntity.ok(specialties);
	}

	/**
	 * Get a single specialty by ID.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Specialty> getSpecialty(@PathVariable Integer id) {
		Optional<Specialty> specialty = specialtyRepository.findById(id);
		return specialty.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

}
