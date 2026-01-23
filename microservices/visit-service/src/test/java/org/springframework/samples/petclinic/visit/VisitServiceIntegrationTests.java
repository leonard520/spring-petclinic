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
package org.springframework.samples.petclinic.visit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.visit.client.PetServiceClient;
import org.springframework.samples.petclinic.visit.dto.PetDto;
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Integration tests for Visit Service.
 * Tests the complete flow from controller through service to repository.
 *
 * @author PetClinic Team
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VisitServiceIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private PetServiceClient petServiceClient;

	@Test
	void shouldGetAllVisits() {
		ResponseEntity<List<Visit>> response = restTemplate.exchange(
			"/api/visits",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<Visit>>() {}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody()).isNotEmpty();
	}

	@Test
	void shouldGetVisitById() {
		ResponseEntity<Visit> response = restTemplate.getForEntity(
			"/api/visits/1",
			Visit.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	void shouldReturn404ForNonExistentVisit() {
		ResponseEntity<Visit> response = restTemplate.getForEntity(
			"/api/visits/9999",
			Visit.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldGetVisitsByPetId() {
		ResponseEntity<List<Visit>> response = restTemplate.exchange(
			"/api/visits/pet/7",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<Visit>>() {}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	void shouldCreateVisitWithPetValidation() {
		// Mock the pet service to return a valid pet
		PetDto petDto = new PetDto();
		petDto.setId(1);
		petDto.setName("Leo");
		petDto.setOwnerId(1);
		given(petServiceClient.getPet(1)).willReturn(petDto);

		Visit newVisit = new Visit();
		newVisit.setDate(LocalDate.of(2024, 6, 1));
		newVisit.setDescription("Integration test visit");
		newVisit.setPetId(1);

		ResponseEntity<Visit> response = restTemplate.postForEntity(
			"/api/visits",
			newVisit,
			Visit.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getDescription()).isEqualTo("Integration test visit");
		assertThat(response.getBody().getId()).isNotNull();
	}

	@Test
	void shouldCreateVisitEvenWhenPetServiceUnavailable() {
		// Simulate pet service being unavailable (fallback)
		given(petServiceClient.getPet(1)).willReturn(null);

		Visit newVisit = new Visit();
		newVisit.setDate(LocalDate.of(2024, 6, 1));
		newVisit.setDescription("Fallback test visit");
		newVisit.setPetId(1);

		ResponseEntity<Visit> response = restTemplate.postForEntity(
			"/api/visits",
			newVisit,
			Visit.class
		);

		// Should still create the visit (graceful degradation)
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void shouldUpdateVisit() {
		Visit updatedVisit = new Visit();
		updatedVisit.setDate(LocalDate.of(2023, 1, 8));
		updatedVisit.setDescription("Updated description");
		updatedVisit.setPetId(7);

		ResponseEntity<Visit> response = restTemplate.exchange(
			"/api/visits/1",
			HttpMethod.PUT,
			new HttpEntity<>(updatedVisit),
			Visit.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldDeleteVisit() {
		// First create a visit to delete
		Visit newVisit = new Visit();
		newVisit.setDate(LocalDate.of(2024, 6, 1));
		newVisit.setDescription("Visit to delete");
		newVisit.setPetId(1);

		given(petServiceClient.getPet(1)).willReturn(new PetDto());

		ResponseEntity<Visit> createResponse = restTemplate.postForEntity(
			"/api/visits",
			newVisit,
			Visit.class
		);

		Integer visitId = createResponse.getBody().getId();

		// Now delete it
		ResponseEntity<Void> deleteResponse = restTemplate.exchange(
			"/api/visits/" + visitId,
			HttpMethod.DELETE,
			null,
			Void.class
		);

		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// Verify it's deleted
		ResponseEntity<Visit> getResponse = restTemplate.getForEntity(
			"/api/visits/" + visitId,
			Visit.class
		);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}
