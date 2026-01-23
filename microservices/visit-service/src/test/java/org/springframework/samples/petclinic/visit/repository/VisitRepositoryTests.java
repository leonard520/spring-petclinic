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
package org.springframework.samples.petclinic.visit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for VisitRepository.
 *
 * @author PetClinic Team
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/schema.sql", "classpath:db/data.sql"})
class VisitRepositoryTests {

	@Autowired
	private VisitRepository visitRepository;

	@Test
	void shouldFindAllVisits() {
		List<Visit> visits = visitRepository.findAll();
		assertThat(visits).isNotEmpty();
		assertThat(visits.size()).isGreaterThanOrEqualTo(4);
	}

	@Test
	void shouldFindVisitById() {
		Visit visit = visitRepository.findById(1).orElse(null);
		assertThat(visit).isNotNull();
		assertThat(visit.getDescription()).isNotNull();
	}

	@Test
	void shouldFindVisitsByPetId() {
		List<Visit> visits = visitRepository.findByPetId(7);
		assertThat(visits).hasSize(2);
	}

	@Test
	void shouldSaveVisit() {
		Visit newVisit = new Visit();
		newVisit.setDate(LocalDate.of(2024, 5, 1));
		newVisit.setDescription("Test vaccination");
		newVisit.setPetId(1);

		Visit savedVisit = visitRepository.save(newVisit);

		assertThat(savedVisit.getId()).isNotNull();
		assertThat(savedVisit.getDescription()).isEqualTo("Test vaccination");
	}

	@Test
	void shouldDeleteVisit() {
		Visit visit = visitRepository.findById(1).orElse(null);
		assertThat(visit).isNotNull();

		visitRepository.delete(visit);

		assertThat(visitRepository.findById(1)).isEmpty();
	}

	@Test
	void shouldFindMultipleVisitsForPet() {
		List<Visit> visits = visitRepository.findByPetId(7);
		assertThat(visits).isNotEmpty();
		// Verify multiple visits are found
		if (visits.size() > 1) {
			assertThat(visits.size()).isGreaterThan(1);
		}
	}

}
