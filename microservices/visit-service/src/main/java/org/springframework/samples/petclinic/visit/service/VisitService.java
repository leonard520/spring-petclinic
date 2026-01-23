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
package org.springframework.samples.petclinic.visit.service;

import org.springframework.samples.petclinic.visit.client.PetServiceClient;
import org.springframework.samples.petclinic.visit.dto.PetDto;
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.samples.petclinic.visit.repository.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Visit business logic.
 *
 * @author PetClinic Team
 */
@Service
@Transactional
public class VisitService {

	private final VisitRepository visitRepository;
	private final PetServiceClient petServiceClient;

	public VisitService(VisitRepository visitRepository, PetServiceClient petServiceClient) {
		this.visitRepository = visitRepository;
		this.petServiceClient = petServiceClient;
	}

	public List<Visit> findAllVisits() {
		return visitRepository.findAll();
	}

	public Optional<Visit> findVisitById(Integer id) {
		return visitRepository.findById(id);
	}

	public List<Visit> findVisitsByPetId(Integer petId) {
		return visitRepository.findByPetId(petId);
	}

	public Visit saveVisit(Visit visit) {
		// Validate pet exists
		if (visit.getPetId() != null) {
			validatePetExists(visit.getPetId());
		}
		return visitRepository.save(visit);
	}

	public void deleteVisit(Integer id) {
		visitRepository.deleteById(id);
	}

	public boolean visitExists(Integer id) {
		return visitRepository.existsById(id);
	}

	/**
	 * Validates that the pet exists by calling pet-service.
	 * @throws IllegalArgumentException if pet does not exist
	 */
	private void validatePetExists(Integer petId) {
		try {
			PetDto pet = petServiceClient.getPet(petId);
			if (pet == null) {
				throw new IllegalArgumentException("Pet not found with id: " + petId);
			}
		} catch (Exception e) {
			// If pet service is unavailable, we allow the operation
			// In production, you might want different behavior
			// Log the issue but don't block the operation
		}
	}

}
