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
package org.springframework.samples.petclinic.owner.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.petclinic.owner.model.Owner;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for OwnerRepository.
 *
 * @author PetClinic Team
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/schema.sql", "classpath:db/data.sql"})
class OwnerRepositoryTests {

	@Autowired
	private OwnerRepository ownerRepository;

	@Test
	void shouldFindAllOwners() {
		List<Owner> owners = ownerRepository.findAll();
		assertThat(owners).isNotEmpty();
		assertThat(owners.size()).isGreaterThanOrEqualTo(10);
	}

	@Test
	void shouldFindOwnerById() {
		Owner owner = ownerRepository.findById(1).orElse(null);
		assertThat(owner).isNotNull();
		assertThat(owner.getFirstName()).isEqualTo("George");
		assertThat(owner.getLastName()).isEqualTo("Franklin");
	}

	@Test
	void shouldFindOwnersByLastName() {
		Page<Owner> page = ownerRepository.findByLastNameStartingWith("Davis", PageRequest.of(0, 5));
		assertThat(page.getContent()).isNotEmpty();
		assertThat(page.getContent().get(0).getLastName()).isEqualTo("Davis");
	}

	@Test
	void shouldFindOwnersPaginated() {
		Page<Owner> page = ownerRepository.findAll(PageRequest.of(0, 5));
		assertThat(page.getContent()).hasSize(5);
		assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(10);
	}

	@Test
	void shouldSaveOwner() {
		Owner newOwner = new Owner();
		newOwner.setFirstName("Test");
		newOwner.setLastName("Owner");
		newOwner.setAddress("123 Test St");
		newOwner.setCity("Test City");
		newOwner.setTelephone("1234567890");

		Owner savedOwner = ownerRepository.save(newOwner);

		assertThat(savedOwner.getId()).isNotNull();
		assertThat(savedOwner.getFirstName()).isEqualTo("Test");
	}

	@Test
	void shouldDeleteOwner() {
		Owner owner = ownerRepository.findById(1).orElse(null);
		assertThat(owner).isNotNull();

		ownerRepository.delete(owner);

		assertThat(ownerRepository.findById(1)).isEmpty();
	}

}
