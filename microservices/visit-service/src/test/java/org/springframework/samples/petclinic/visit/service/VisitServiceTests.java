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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.visit.client.PetServiceClient;
import org.springframework.samples.petclinic.visit.dto.PetDto;
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.samples.petclinic.visit.repository.VisitRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Test class for VisitService.
 *
 * @author PetClinic Team
 */
@ExtendWith(MockitoExtension.class)
class VisitServiceTests {

	@Mock
	private VisitRepository visitRepository;

	@Mock
	private PetServiceClient petServiceClient;

	@InjectMocks
	private VisitService visitService;

	private Visit visit;
	private PetDto petDto;

	@BeforeEach
	void setup() {
		visit = new Visit();
		visit.setId(1);
		visit.setDate(LocalDate.of(2023, 1, 8));
		visit.setDescription("Rabies shot");
		visit.setPetId(7);

		petDto = new PetDto();
		petDto.setId(7);
		petDto.setName("Samantha");
		petDto.setOwnerId(3);
	}

	@Test
	void shouldFindAllVisits() {
		given(visitRepository.findAll()).willReturn(Arrays.asList(visit));

		List<Visit> visits = visitService.findAllVisits();

		assertThat(visits).hasSize(1);
		assertThat(visits.get(0).getDescription()).isEqualTo("Rabies shot");
	}

	@Test
	void shouldFindVisitById() {
		given(visitRepository.findById(1)).willReturn(Optional.of(visit));

		Optional<Visit> found = visitService.findVisitById(1);

		assertThat(found).isPresent();
		assertThat(found.get().getDescription()).isEqualTo("Rabies shot");
	}

	@Test
	void shouldFindVisitsByPetId() {
		given(visitRepository.findByPetId(7)).willReturn(Arrays.asList(visit));

		List<Visit> visits = visitService.findVisitsByPetId(7);

		assertThat(visits).hasSize(1);
		assertThat(visits.get(0).getPetId()).isEqualTo(7);
	}

	@Test
	void shouldSaveVisitWhenPetExists() {
		given(petServiceClient.getPet(7)).willReturn(petDto);
		given(visitRepository.save(any(Visit.class))).willReturn(visit);

		Visit saved = visitService.saveVisit(visit);

		assertThat(saved).isNotNull();
		verify(visitRepository).save(visit);
	}

	@Test
	void shouldSaveVisitEvenWhenPetServiceUnavailable() {
		given(petServiceClient.getPet(7)).willThrow(new RuntimeException("Service unavailable"));
		given(visitRepository.save(any(Visit.class))).willReturn(visit);

		Visit saved = visitService.saveVisit(visit);

		assertThat(saved).isNotNull();
		verify(visitRepository).save(visit);
	}

	@Test
	void shouldDeleteVisit() {
		visitService.deleteVisit(1);

		verify(visitRepository).deleteById(1);
	}

	@Test
	void shouldCheckVisitExists() {
		given(visitRepository.existsById(1)).willReturn(true);

		boolean exists = visitService.visitExists(1);

		assertThat(exists).isTrue();
	}

	@Test
	void shouldReturnFalseWhenVisitDoesNotExist() {
		given(visitRepository.existsById(999)).willReturn(false);

		boolean exists = visitService.visitExists(999);

		assertThat(exists).isFalse();
	}

}
