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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.pet.model.Pet;
import org.springframework.samples.petclinic.pet.service.PetService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Pet resources.
 *
 * @author PetClinic Team
 */
@RestController
@RequestMapping("/api/pets")
public class PetController {

	private final PetService petService;

	public PetController(PetService petService) {
		this.petService = petService;
	}

	/**
	 * Get all pets.
	 */
	@GetMapping
	public ResponseEntity<List<Pet>> getAllPets() {
		List<Pet> pets = petService.findAllPets();
		return ResponseEntity.ok(pets);
	}

	/**
	 * Get a single pet by ID.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Pet> getPet(@PathVariable Integer id) {
		Optional<Pet> pet = petService.findPetById(id);
		return pet.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Get pets by owner ID.
	 */
	@GetMapping("/owner/{ownerId}")
	public ResponseEntity<List<Pet>> getPetsByOwner(@PathVariable Integer ownerId) {
		List<Pet> pets = petService.findPetsByOwnerId(ownerId);
		return ResponseEntity.ok(pets);
	}

	/**
	 * Create a new pet.
	 */
	@PostMapping
	public ResponseEntity<Pet> createPet(@Valid @RequestBody Pet pet) {
		try {
			Pet savedPet = petService.savePet(pet);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedPet);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Update an existing pet.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Pet> updatePet(@PathVariable Integer id, @Valid @RequestBody Pet pet) {
		if (!petService.petExists(id)) {
			return ResponseEntity.notFound().build();
		}
		pet.setId(id);
		try {
			Pet updatedPet = petService.savePet(pet);
			return ResponseEntity.ok(updatedPet);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Delete a pet.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePet(@PathVariable Integer id) {
		if (!petService.petExists(id)) {
			return ResponseEntity.notFound().build();
		}
		petService.deletePet(id);
		return ResponseEntity.noContent().build();
	}

}
