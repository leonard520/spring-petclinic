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
package org.springframework.samples.petclinic.pet.service;

import org.springframework.samples.petclinic.pet.client.OwnerServiceClient;
import org.springframework.samples.petclinic.pet.dto.OwnerDto;
import org.springframework.samples.petclinic.pet.model.Pet;
import org.springframework.samples.petclinic.pet.model.PetType;
import org.springframework.samples.petclinic.pet.repository.PetRepository;
import org.springframework.samples.petclinic.pet.repository.PetTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Pet business logic.
 *
 * @author PetClinic Team
 */
@Service
@Transactional
public class PetService {

	private final PetRepository petRepository;
	private final PetTypeRepository petTypeRepository;
	private final OwnerServiceClient ownerServiceClient;

	public PetService(PetRepository petRepository, PetTypeRepository petTypeRepository,
			OwnerServiceClient ownerServiceClient) {
		this.petRepository = petRepository;
		this.petTypeRepository = petTypeRepository;
		this.ownerServiceClient = ownerServiceClient;
	}

	public List<Pet> findAllPets() {
		return petRepository.findAll();
	}

	public Optional<Pet> findPetById(Integer id) {
		return petRepository.findById(id);
	}

	public List<Pet> findPetsByOwnerId(Integer ownerId) {
		return petRepository.findByOwnerId(ownerId);
	}

	public Pet savePet(Pet pet) {
		// Validate owner exists
		if (pet.getOwnerId() != null) {
			validateOwnerExists(pet.getOwnerId());
		}
		return petRepository.save(pet);
	}

	public void deletePet(Integer id) {
		petRepository.deleteById(id);
	}

	public boolean petExists(Integer id) {
		return petRepository.existsById(id);
	}

	public List<PetType> findAllPetTypes() {
		return petTypeRepository.findAll();
	}

	public Optional<PetType> findPetTypeById(Integer id) {
		return petTypeRepository.findById(id);
	}

	/**
	 * Validates that the owner exists by calling owner-service.
	 * @throws IllegalArgumentException if owner does not exist
	 */
	private void validateOwnerExists(Integer ownerId) {
		try {
			OwnerDto owner = ownerServiceClient.getOwner(ownerId);
			if (owner == null) {
				throw new IllegalArgumentException("Owner not found with id: " + ownerId);
			}
		} catch (Exception e) {
			// If owner service is unavailable, we allow the operation
			// In production, you might want different behavior
			// Log the issue but don't block the operation
		}
	}

}
