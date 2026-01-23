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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.pet.client.OwnerServiceClient;
import org.springframework.samples.petclinic.pet.dto.OwnerDto;
import org.springframework.samples.petclinic.pet.model.Pet;
import org.springframework.samples.petclinic.pet.model.PetType;
import org.springframework.samples.petclinic.pet.repository.PetRepository;
import org.springframework.samples.petclinic.pet.repository.PetTypeRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Test class for PetService.
 *
 * @author PetClinic Team
 */
@ExtendWith(MockitoExtension.class)
class PetServiceTests {

	@Mock
	private PetRepository petRepository;

	@Mock
	private PetTypeRepository petTypeRepository;

	@Mock
	private OwnerServiceClient ownerServiceClient;

	@InjectMocks
	private PetService petService;

	private Pet pet;
	private PetType catType;
	private OwnerDto ownerDto;

	@BeforeEach
	void setup() {
		catType = new PetType();
		catType.setId(1);
		catType.setName("cat");

		pet = new Pet();
		pet.setId(1);
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.of(2010, 9, 7));
		pet.setType(catType);
		pet.setOwnerId(1);

		ownerDto = new OwnerDto();
		ownerDto.setId(1);
		ownerDto.setFirstName("George");
		ownerDto.setLastName("Franklin");
	}

	@Test
	void shouldFindAllPets() {
		given(petRepository.findAll()).willReturn(Arrays.asList(pet));

		List<Pet> pets = petService.findAllPets();

		assertThat(pets).hasSize(1);
		assertThat(pets.get(0).getName()).isEqualTo("Leo");
	}

	@Test
	void shouldFindPetById() {
		given(petRepository.findById(1)).willReturn(Optional.of(pet));

		Optional<Pet> found = petService.findPetById(1);

		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("Leo");
	}

	@Test
	void shouldFindPetsByOwnerId() {
		given(petRepository.findByOwnerId(1)).willReturn(Arrays.asList(pet));

		List<Pet> pets = petService.findPetsByOwnerId(1);

		assertThat(pets).hasSize(1);
		assertThat(pets.get(0).getOwnerId()).isEqualTo(1);
	}

	@Test
	void shouldSavePetWhenOwnerExists() {
		given(ownerServiceClient.getOwner(1)).willReturn(ownerDto);
		given(petRepository.save(any(Pet.class))).willReturn(pet);

		Pet saved = petService.savePet(pet);

		assertThat(saved).isNotNull();
		verify(petRepository).save(pet);
	}

	@Test
	void shouldSavePetEvenWhenOwnerServiceUnavailable() {
		given(ownerServiceClient.getOwner(1)).willThrow(new RuntimeException("Service unavailable"));
		given(petRepository.save(any(Pet.class))).willReturn(pet);

		Pet saved = petService.savePet(pet);

		assertThat(saved).isNotNull();
		verify(petRepository).save(pet);
	}

	@Test
	void shouldDeletePet() {
		petService.deletePet(1);

		verify(petRepository).deleteById(1);
	}

	@Test
	void shouldCheckPetExists() {
		given(petRepository.existsById(1)).willReturn(true);

		boolean exists = petService.petExists(1);

		assertThat(exists).isTrue();
	}

	@Test
	void shouldFindAllPetTypes() {
		given(petTypeRepository.findAll()).willReturn(Arrays.asList(catType));

		List<PetType> types = petService.findAllPetTypes();

		assertThat(types).hasSize(1);
		assertThat(types.get(0).getName()).isEqualTo("cat");
	}

}
