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
package org.springframework.samples.petclinic.owner.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.model.Owner;
import org.springframework.samples.petclinic.owner.repository.OwnerRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Owner resources.
 *
 * @author PetClinic Team
 */
@RestController
@RequestMapping("/api/owners")
public class OwnerController {

	private final OwnerRepository ownerRepository;

	public OwnerController(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	/**
	 * Get all owners.
	 */
	@GetMapping
	public ResponseEntity<List<Owner>> getAllOwners() {
		List<Owner> owners = ownerRepository.findAll();
		return ResponseEntity.ok(owners);
	}

	/**
	 * Get owners with pagination.
	 */
	@GetMapping("/paginated")
	public ResponseEntity<Page<Owner>> getOwnersPaginated(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Owner> ownersPage = ownerRepository.findAll(pageable);
		return ResponseEntity.ok(ownersPage);
	}

	/**
	 * Get a single owner by ID.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Owner> getOwner(@PathVariable Integer id) {
		Optional<Owner> owner = ownerRepository.findById(id);
		return owner.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Search owners by last name.
	 */
	@GetMapping("/search")
	public ResponseEntity<Page<Owner>> searchOwners(
			@RequestParam(defaultValue = "") String lastName,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Owner> owners = ownerRepository.findByLastNameStartingWith(lastName, pageable);
		return ResponseEntity.ok(owners);
	}

	/**
	 * Create a new owner.
	 */
	@PostMapping
	public ResponseEntity<Owner> createOwner(@Valid @RequestBody Owner owner) {
		Owner savedOwner = ownerRepository.save(owner);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedOwner);
	}

	/**
	 * Update an existing owner.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Owner> updateOwner(@PathVariable Integer id, @Valid @RequestBody Owner owner) {
		if (!ownerRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		owner.setId(id);
		Owner updatedOwner = ownerRepository.save(owner);
		return ResponseEntity.ok(updatedOwner);
	}

	/**
	 * Delete an owner.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOwner(@PathVariable Integer id) {
		if (!ownerRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		ownerRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
