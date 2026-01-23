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
package org.springframework.samples.petclinic.pet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.pet.model.Pet;
import org.springframework.samples.petclinic.pet.model.PetType;
import org.springframework.samples.petclinic.pet.service.PetService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for PetController.
 *
 * @author PetClinic Team
 */
@WebMvcTest(PetController.class)
class PetControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PetService petService;

	private Pet pet1;
	private Pet pet2;
	private PetType catType;

	@BeforeEach
	void setup() {
		catType = new PetType();
		catType.setId(1);
		catType.setName("cat");

		pet1 = new Pet();
		pet1.setId(1);
		pet1.setName("Leo");
		pet1.setBirthDate(LocalDate.of(2010, 9, 7));
		pet1.setType(catType);
		pet1.setOwnerId(1);

		pet2 = new Pet();
		pet2.setId(2);
		pet2.setName("Basil");
		pet2.setBirthDate(LocalDate.of(2012, 8, 6));
		pet2.setType(catType);
		pet2.setOwnerId(2);
	}

	@Test
	void shouldGetAllPets() throws Exception {
		given(petService.findAllPets()).willReturn(Arrays.asList(pet1, pet2));

		mockMvc.perform(get("/api/pets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].name").value("Leo"))
			.andExpect(jsonPath("$[1].name").value("Basil"));
	}

	@Test
	void shouldGetPetById() throws Exception {
		given(petService.findPetById(1)).willReturn(Optional.of(pet1));

		mockMvc.perform(get("/api/pets/1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.name").value("Leo"))
			.andExpect(jsonPath("$.ownerId").value(1));
	}

	@Test
	void shouldReturn404WhenPetNotFound() throws Exception {
		given(petService.findPetById(999)).willReturn(Optional.empty());

		mockMvc.perform(get("/api/pets/999").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldGetPetsByOwner() throws Exception {
		given(petService.findPetsByOwnerId(1)).willReturn(Arrays.asList(pet1));

		mockMvc.perform(get("/api/pets/owner/1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].name").value("Leo"))
			.andExpect(jsonPath("$[0].ownerId").value(1));
	}

	@Test
	void shouldCreatePet() throws Exception {
		Pet newPet = new Pet();
		newPet.setId(3);
		newPet.setName("Max");
		newPet.setOwnerId(1);

		given(petService.savePet(any(Pet.class))).willReturn(newPet);

		mockMvc.perform(post("/api/pets")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Max\",\"ownerId\":1,\"birthDate\":\"2020-01-01\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("Max"));
	}

	@Test
	void shouldUpdatePet() throws Exception {
		given(petService.petExists(1)).willReturn(true);
		given(petService.savePet(any(Pet.class))).willReturn(pet1);

		mockMvc.perform(put("/api/pets/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Leo\",\"ownerId\":1,\"birthDate\":\"2010-09-07\"}"))
			.andExpect(status().isOk());
	}

	@Test
	void shouldReturn404WhenUpdatingNonExistentPet() throws Exception {
		given(petService.petExists(999)).willReturn(false);

		mockMvc.perform(put("/api/pets/999")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Test\",\"ownerId\":1,\"birthDate\":\"2020-01-01\"}"))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldDeletePet() throws Exception {
		given(petService.petExists(1)).willReturn(true);

		mockMvc.perform(delete("/api/pets/1"))
			.andExpect(status().isNoContent());
	}

	@Test
	void shouldReturn404WhenDeletingNonExistentPet() throws Exception {
		given(petService.petExists(999)).willReturn(false);

		mockMvc.perform(delete("/api/pets/999"))
			.andExpect(status().isNotFound());
	}

}
