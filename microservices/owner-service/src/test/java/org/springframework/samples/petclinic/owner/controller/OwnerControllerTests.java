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
package org.springframework.samples.petclinic.owner.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.owner.model.Owner;
import org.springframework.samples.petclinic.owner.repository.OwnerRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for OwnerController.
 *
 * @author PetClinic Team
 */
@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository ownerRepository;

	private Owner owner1;
	private Owner owner2;

	@BeforeEach
	void setup() {
		owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");
		owner1.setAddress("110 W. Liberty St.");
		owner1.setCity("Madison");
		owner1.setTelephone("6085551023");

		owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");
		owner2.setAddress("638 Cardinal Ave.");
		owner2.setCity("Sun Prairie");
		owner2.setTelephone("6085551749");
	}

	@Test
	void shouldGetAllOwners() throws Exception {
		given(ownerRepository.findAll()).willReturn(Arrays.asList(owner1, owner2));

		mockMvc.perform(get("/api/owners").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].firstName").value("George"))
			.andExpect(jsonPath("$[1].firstName").value("Betty"));
	}

	@Test
	void shouldGetOwnersPaginated() throws Exception {
		List<Owner> owners = Arrays.asList(owner1, owner2);
		Page<Owner> page = new PageImpl<>(owners);
		given(ownerRepository.findAll(any(Pageable.class))).willReturn(page);

		mockMvc.perform(get("/api/owners/paginated?page=0&size=5").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content[0].firstName").value("George"));
	}

	@Test
	void shouldGetOwnerById() throws Exception {
		given(ownerRepository.findById(1)).willReturn(Optional.of(owner1));

		mockMvc.perform(get("/api/owners/1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.firstName").value("George"))
			.andExpect(jsonPath("$.lastName").value("Franklin"))
			.andExpect(jsonPath("$.city").value("Madison"));
	}

	@Test
	void shouldReturn404WhenOwnerNotFound() throws Exception {
		given(ownerRepository.findById(999)).willReturn(Optional.empty());

		mockMvc.perform(get("/api/owners/999").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldSearchOwnersByLastName() throws Exception {
		List<Owner> owners = Arrays.asList(owner1);
		Page<Owner> page = new PageImpl<>(owners);
		given(ownerRepository.findByLastNameStartingWith(anyString(), any(Pageable.class))).willReturn(page);

		mockMvc.perform(get("/api/owners/search?lastName=Frank").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].lastName").value("Franklin"));
	}

	@Test
	void shouldCreateOwner() throws Exception {
		Owner newOwner = new Owner();
		newOwner.setId(3);
		newOwner.setFirstName("John");
		newOwner.setLastName("Doe");
		newOwner.setAddress("123 Main St");
		newOwner.setCity("Springfield");
		newOwner.setTelephone("1234567890");

		given(ownerRepository.save(any(Owner.class))).willReturn(newOwner);

		mockMvc.perform(post("/api/owners")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"address\":\"123 Main St\",\"city\":\"Springfield\",\"telephone\":\"1234567890\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.firstName").value("John"))
			.andExpect(jsonPath("$.lastName").value("Doe"));
	}

	@Test
	void shouldUpdateOwner() throws Exception {
		given(ownerRepository.existsById(1)).willReturn(true);
		given(ownerRepository.save(any(Owner.class))).willReturn(owner1);

		mockMvc.perform(put("/api/owners/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"George\",\"lastName\":\"Franklin\",\"address\":\"110 W. Liberty St.\",\"city\":\"Madison\",\"telephone\":\"6085551023\"}"))
			.andExpect(status().isOk());
	}

	@Test
	void shouldReturn404WhenUpdatingNonExistentOwner() throws Exception {
		given(ownerRepository.existsById(999)).willReturn(false);

		mockMvc.perform(put("/api/owners/999")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Test\",\"lastName\":\"Owner\",\"address\":\"Test\",\"city\":\"Test\",\"telephone\":\"1234567890\"}"))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldDeleteOwner() throws Exception {
		given(ownerRepository.existsById(1)).willReturn(true);

		mockMvc.perform(delete("/api/owners/1"))
			.andExpect(status().isNoContent());
	}

	@Test
	void shouldReturn404WhenDeletingNonExistentOwner() throws Exception {
		given(ownerRepository.existsById(999)).willReturn(false);

		mockMvc.perform(delete("/api/owners/999"))
			.andExpect(status().isNotFound());
	}

}
