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
package org.springframework.samples.petclinic.pet;

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
import org.springframework.samples.petclinic.pet.client.OwnerServiceClient;
import org.springframework.samples.petclinic.pet.dto.OwnerDto;
import org.springframework.samples.petclinic.pet.model.Pet;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Integration tests for Pet Service.
 * Tests the complete flow from controller through service to repository.
 *
 * @author PetClinic Team
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PetServiceIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private OwnerServiceClient ownerServiceClient;

	@Test
	void shouldGetAllPets() {
		ResponseEntity<List<Pet>> response = restTemplate.exchange(
			"/api/pets",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<Pet>>() {}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody()).isNotEmpty();
	}

	@Test
	void shouldGetPetById() {
		ResponseEntity<Pet> response = restTemplate.getForEntity(
			"/api/pets/1",
			Pet.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getName()).isEqualTo("Leo");
	}

	@Test
	void shouldReturn404ForNonExistentPet() {
		ResponseEntity<Pet> response = restTemplate.getForEntity(
			"/api/pets/9999",
			Pet.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldGetPetsByOwner() {
		ResponseEntity<List<Pet>> response = restTemplate.exchange(
			"/api/pets/owner/1",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<Pet>>() {}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	void shouldCreatePetWithOwnerValidation() {
		// Mock the owner service to return a valid owner
		OwnerDto ownerDto = new OwnerDto();
		ownerDto.setId(1);
		ownerDto.setFirstName("George");
		ownerDto.setLastName("Franklin");
		given(ownerServiceClient.getOwner(1)).willReturn(ownerDto);

		Pet newPet = new Pet();
		newPet.setName("IntegrationTestPet");
		newPet.setBirthDate(LocalDate.of(2020, 1, 1));
		newPet.setOwnerId(1);

		ResponseEntity<Pet> response = restTemplate.postForEntity(
			"/api/pets",
			newPet,
			Pet.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getName()).isEqualTo("IntegrationTestPet");
		assertThat(response.getBody().getId()).isNotNull();
	}

	@Test
	void shouldCreatePetEvenWhenOwnerServiceUnavailable() {
		// Simulate owner service being unavailable (fallback)
		given(ownerServiceClient.getOwner(1)).willReturn(null);

		Pet newPet = new Pet();
		newPet.setName("FallbackTestPet");
		newPet.setBirthDate(LocalDate.of(2020, 1, 1));
		newPet.setOwnerId(1);

		ResponseEntity<Pet> response = restTemplate.postForEntity(
			"/api/pets",
			newPet,
			Pet.class
		);

		// Should still create the pet (graceful degradation)
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void shouldUpdatePet() {
		Pet updatedPet = new Pet();
		updatedPet.setName("UpdatedLeo");
		updatedPet.setBirthDate(LocalDate.of(2010, 9, 7));
		updatedPet.setOwnerId(1);

		ResponseEntity<Pet> response = restTemplate.exchange(
			"/api/pets/1",
			HttpMethod.PUT,
			new HttpEntity<>(updatedPet),
			Pet.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldDeletePet() {
		// First create a pet to delete
		Pet newPet = new Pet();
		newPet.setName("PetToDelete");
		newPet.setBirthDate(LocalDate.of(2020, 1, 1));
		newPet.setOwnerId(1);

		given(ownerServiceClient.getOwner(1)).willReturn(new OwnerDto());

		ResponseEntity<Pet> createResponse = restTemplate.postForEntity(
			"/api/pets",
			newPet,
			Pet.class
		);

		Integer petId = createResponse.getBody().getId();

		// Now delete it
		ResponseEntity<Void> deleteResponse = restTemplate.exchange(
			"/api/pets/" + petId,
			HttpMethod.DELETE,
			null,
			Void.class
		);

		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// Verify it's deleted
		ResponseEntity<Pet> getResponse = restTemplate.getForEntity(
			"/api/pets/" + petId,
			Pet.class
		);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}
