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
package org.springframework.samples.petclinic.pet.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.pet.model.Pet;
import org.springframework.samples.petclinic.pet.model.PetType;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for PetRepository.
 *
 * @author PetClinic Team
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/schema.sql", "classpath:db/data.sql"})
class PetRepositoryTests {

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private PetTypeRepository petTypeRepository;

	@Test
	void shouldFindAllPets() {
		List<Pet> pets = petRepository.findAll();
		assertThat(pets).isNotEmpty();
		assertThat(pets.size()).isGreaterThanOrEqualTo(13);
	}

	@Test
	void shouldFindPetById() {
		Pet pet = petRepository.findById(1).orElse(null);
		assertThat(pet).isNotNull();
		assertThat(pet.getName()).isEqualTo("Leo");
	}

	@Test
	void shouldFindPetsByOwnerId() {
		List<Pet> pets = petRepository.findByOwnerId(3);
		assertThat(pets).hasSize(2);
	}

	@Test
	void shouldSavePet() {
		PetType type = petTypeRepository.findById(1).orElse(null);
		assertThat(type).isNotNull();

		Pet newPet = new Pet();
		newPet.setName("TestPet");
		newPet.setBirthDate(LocalDate.of(2020, 1, 1));
		newPet.setType(type);
		newPet.setOwnerId(1);

		Pet savedPet = petRepository.save(newPet);

		assertThat(savedPet.getId()).isNotNull();
		assertThat(savedPet.getName()).isEqualTo("TestPet");
	}

	@Test
	void shouldDeletePet() {
		Pet pet = petRepository.findById(1).orElse(null);
		assertThat(pet).isNotNull();

		petRepository.delete(pet);

		assertThat(petRepository.findById(1)).isEmpty();
	}

	@Test
	void shouldFindAllPetTypes() {
		List<PetType> types = petTypeRepository.findAll();
		assertThat(types).isNotEmpty();
		assertThat(types.size()).isGreaterThanOrEqualTo(6);
	}

}
