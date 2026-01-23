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
package org.springframework.samples.petclinic.vet.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.petclinic.vet.model.Vet;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for VetRepository.
 *
 * @author PetClinic Team
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/schema.sql", "classpath:db/data.sql"})
class VetRepositoryTests {

	@Autowired
	private VetRepository vetRepository;

	@Test
	void shouldFindAllVets() {
		List<Vet> vets = vetRepository.findAll();
		assertThat(vets).isNotEmpty();
		assertThat(vets.size()).isGreaterThanOrEqualTo(6);
	}

	@Test
	void shouldFindVetById() {
		Vet vet = vetRepository.findById(1).orElse(null);
		assertThat(vet).isNotNull();
		assertThat(vet.getFirstName()).isEqualTo("James");
		assertThat(vet.getLastName()).isEqualTo("Carter");
	}

	@Test
	void shouldFindVetsPaginated() {
		Page<Vet> page = vetRepository.findAll(PageRequest.of(0, 3));
		assertThat(page.getContent()).hasSize(3);
		assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(6);
	}

	@Test
	void shouldSaveVet() {
		long initialCount = vetRepository.count();

		Vet newVet = new Vet();
		newVet.setFirstName("Test");
		newVet.setLastName("Vet");

		Vet savedVet = vetRepository.save(newVet);
		vetRepository.flush();

		assertThat(savedVet.getId()).isNotNull();
		assertThat(savedVet.getFirstName()).isEqualTo("Test");
		assertThat(vetRepository.count()).isEqualTo(initialCount + 1);
	}

	@Test
	void shouldDeleteVet() {
		Vet vet = vetRepository.findById(1).orElse(null);
		assertThat(vet).isNotNull();

		vetRepository.delete(vet);

		assertThat(vetRepository.findById(1)).isEmpty();
	}

}
