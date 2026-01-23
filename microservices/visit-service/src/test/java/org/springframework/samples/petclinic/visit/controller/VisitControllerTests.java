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
package org.springframework.samples.petclinic.visit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.samples.petclinic.visit.service.VisitService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for VisitController.
 *
 * @author PetClinic Team
 */
@WebMvcTest(VisitController.class)
class VisitControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VisitService visitService;

	private Visit visit1;
	private Visit visit2;

	@BeforeEach
	void setup() {
		visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.of(2023, 1, 8));
		visit1.setDescription("Rabies shot");
		visit1.setPetId(7);

		visit2 = new Visit();
		visit2.setId(2);
		visit2.setDate(LocalDate.of(2023, 3, 4));
		visit2.setDescription("Neutered");
		visit2.setPetId(8);
	}

	@Test
	void shouldGetAllVisits() throws Exception {
		given(visitService.findAllVisits()).willReturn(Arrays.asList(visit1, visit2));

		mockMvc.perform(get("/api/visits").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].description").value("Rabies shot"))
			.andExpect(jsonPath("$[1].description").value("Neutered"));
	}

	@Test
	void shouldGetVisitById() throws Exception {
		given(visitService.findVisitById(1)).willReturn(Optional.of(visit1));

		mockMvc.perform(get("/api/visits/1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.description").value("Rabies shot"))
			.andExpect(jsonPath("$.petId").value(7));
	}

	@Test
	void shouldReturn404WhenVisitNotFound() throws Exception {
		given(visitService.findVisitById(999)).willReturn(Optional.empty());

		mockMvc.perform(get("/api/visits/999").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldGetVisitsByPetId() throws Exception {
		given(visitService.findVisitsByPetId(7)).willReturn(Arrays.asList(visit1));

		mockMvc.perform(get("/api/visits/pet/7").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].description").value("Rabies shot"))
			.andExpect(jsonPath("$[0].petId").value(7));
	}

	@Test
	void shouldCreateVisit() throws Exception {
		Visit newVisit = new Visit();
		newVisit.setId(3);
		newVisit.setDescription("Annual checkup");
		newVisit.setPetId(1);
		newVisit.setDate(LocalDate.of(2024, 1, 1));

		given(visitService.saveVisit(any(Visit.class))).willReturn(newVisit);

		mockMvc.perform(post("/api/visits")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"description\":\"Annual checkup\",\"petId\":1,\"date\":\"2024-01-01\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.description").value("Annual checkup"));
	}

	@Test
	void shouldUpdateVisit() throws Exception {
		given(visitService.visitExists(1)).willReturn(true);
		given(visitService.saveVisit(any(Visit.class))).willReturn(visit1);

		mockMvc.perform(put("/api/visits/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"description\":\"Updated description\",\"petId\":7,\"date\":\"2023-01-08\"}"))
			.andExpect(status().isOk());
	}

	@Test
	void shouldReturn404WhenUpdatingNonExistentVisit() throws Exception {
		given(visitService.visitExists(999)).willReturn(false);

		mockMvc.perform(put("/api/visits/999")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"description\":\"Test\",\"petId\":1,\"date\":\"2023-01-01\"}"))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldDeleteVisit() throws Exception {
		given(visitService.visitExists(1)).willReturn(true);

		mockMvc.perform(delete("/api/visits/1"))
			.andExpect(status().isNoContent());
	}

	@Test
	void shouldReturn404WhenDeletingNonExistentVisit() throws Exception {
		given(visitService.visitExists(999)).willReturn(false);

		mockMvc.perform(delete("/api/visits/999"))
			.andExpect(status().isNotFound());
	}

}
