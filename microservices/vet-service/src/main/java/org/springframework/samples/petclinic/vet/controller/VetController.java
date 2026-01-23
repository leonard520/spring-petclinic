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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vet.model.Vet;
import org.springframework.samples.petclinic.vet.model.Vets;
import org.springframework.samples.petclinic.vet.repository.VetRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Vet resources.
 *
 * @author PetClinic Team
 */
@RestController
@RequestMapping("/api/vets")
public class VetController {

	private final VetRepository vetRepository;

	public VetController(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	/**
	 * Get all vets as JSON.
	 */
	@GetMapping
	public ResponseEntity<Vets> getAllVets() {
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vetRepository.findAll());
		return ResponseEntity.ok(vets);
	}

	/**
	 * Get vets with pagination.
	 */
	@GetMapping("/paginated")
	public ResponseEntity<Page<Vet>> getVetsPaginated(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Vet> vetsPage = vetRepository.findAll(pageable);
		return ResponseEntity.ok(vetsPage);
	}

	/**
	 * Get a single vet by ID.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Vet> getVet(@PathVariable Integer id) {
		Optional<Vet> vet = vetRepository.findById(id);
		return vet.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Create a new vet.
	 */
	@PostMapping
	public ResponseEntity<Vet> createVet(@Valid @RequestBody Vet vet) {
		Vet savedVet = vetRepository.save(vet);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedVet);
	}

	/**
	 * Update an existing vet.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Vet> updateVet(@PathVariable Integer id, @Valid @RequestBody Vet vet) {
		if (!vetRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		vet.setId(id);
		Vet updatedVet = vetRepository.save(vet);
		return ResponseEntity.ok(updatedVet);
	}

	/**
	 * Delete a vet.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteVet(@PathVariable Integer id) {
		if (!vetRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		vetRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
