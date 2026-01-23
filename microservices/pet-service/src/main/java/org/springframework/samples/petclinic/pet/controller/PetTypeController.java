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
package org.springframework.samples.petclinic.pet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.pet.model.PetType;
import org.springframework.samples.petclinic.pet.service.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for PetType resources.
 *
 * @author PetClinic Team
 */
@RestController
@RequestMapping("/api/pet-types")
public class PetTypeController {

	private final PetService petService;

	public PetTypeController(PetService petService) {
		this.petService = petService;
	}

	/**
	 * Get all pet types.
	 */
	@GetMapping
	public ResponseEntity<List<PetType>> getAllPetTypes() {
		List<PetType> petTypes = petService.findAllPetTypes();
		return ResponseEntity.ok(petTypes);
	}

	/**
	 * Get a single pet type by ID.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<PetType> getPetType(@PathVariable Integer id) {
		Optional<PetType> petType = petService.findPetTypeById(id);
		return petType.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

}
