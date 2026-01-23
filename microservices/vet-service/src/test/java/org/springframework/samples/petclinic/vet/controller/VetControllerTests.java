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
package org.springframework.samples.petclinic.vet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vet.model.Specialty;
import org.springframework.samples.petclinic.vet.model.Vet;
import org.springframework.samples.petclinic.vet.repository.VetRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for VetController.
 *
 * @author PetClinic Team
 */
@WebMvcTest(VetController.class)
class VetControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VetRepository vetRepository;

	private Vet vet1;
	private Vet vet2;

	@BeforeEach
	void setup() {
		vet1 = new Vet();
		vet1.setId(1);
		vet1.setFirstName("James");
		vet1.setLastName("Carter");

		vet2 = new Vet();
		vet2.setId(2);
		vet2.setFirstName("Helen");
		vet2.setLastName("Leary");

		Specialty radiology = new Specialty();
		radiology.setId(1);
		radiology.setName("radiology");
		vet2.addSpecialty(radiology);
	}

	@Test
	void shouldGetAllVets() throws Exception {
		given(vetRepository.findAll()).willReturn(Arrays.asList(vet1, vet2));

		mockMvc.perform(get("/api/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList[0].firstName").value("James"))
			.andExpect(jsonPath("$.vetList[1].firstName").value("Helen"));
	}

	@Test
	void shouldGetVetsPaginated() throws Exception {
		List<Vet> vets = Arrays.asList(vet1, vet2);
		Page<Vet> page = new PageImpl<>(vets);
		given(vetRepository.findAll(any(Pageable.class))).willReturn(page);

		mockMvc.perform(get("/api/vets/paginated?page=0&size=5").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content[0].firstName").value("James"));
	}

	@Test
	void shouldGetVetById() throws Exception {
		given(vetRepository.findById(1)).willReturn(Optional.of(vet1));

		mockMvc.perform(get("/api/vets/1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.firstName").value("James"))
			.andExpect(jsonPath("$.lastName").value("Carter"));
	}

	@Test
	void shouldReturn404WhenVetNotFound() throws Exception {
		given(vetRepository.findById(999)).willReturn(Optional.empty());

		mockMvc.perform(get("/api/vets/999").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldCreateVet() throws Exception {
		Vet newVet = new Vet();
		newVet.setId(3);
		newVet.setFirstName("Linda");
		newVet.setLastName("Douglas");

		given(vetRepository.save(any(Vet.class))).willReturn(newVet);

		mockMvc.perform(post("/api/vets")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Linda\",\"lastName\":\"Douglas\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.firstName").value("Linda"))
			.andExpect(jsonPath("$.lastName").value("Douglas"));
	}

	@Test
	void shouldUpdateVet() throws Exception {
		given(vetRepository.existsById(1)).willReturn(true);
		given(vetRepository.save(any(Vet.class))).willReturn(vet1);

		mockMvc.perform(put("/api/vets/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"James\",\"lastName\":\"Carter\"}"))
			.andExpect(status().isOk());
	}

	@Test
	void shouldReturn404WhenUpdatingNonExistentVet() throws Exception {
		given(vetRepository.existsById(999)).willReturn(false);

		mockMvc.perform(put("/api/vets/999")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Test\",\"lastName\":\"Vet\"}"))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldDeleteVet() throws Exception {
		given(vetRepository.existsById(1)).willReturn(true);

		mockMvc.perform(delete("/api/vets/1"))
			.andExpect(status().isNoContent());
	}

	@Test
	void shouldReturn404WhenDeletingNonExistentVet() throws Exception {
		given(vetRepository.existsById(999)).willReturn(false);

		mockMvc.perform(delete("/api/vets/999"))
			.andExpect(status().isNotFound());
	}

}
