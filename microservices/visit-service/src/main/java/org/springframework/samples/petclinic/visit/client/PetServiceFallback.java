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
package org.springframework.samples.petclinic.visit.client;

import org.springframework.samples.petclinic.visit.dto.PetDto;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for PetServiceClient.
 * Used when pet-service is unavailable.
 *
 * @author PetClinic Team
 */
@Component
public class PetServiceFallback implements PetServiceClient {

	@Override
	public PetDto getPet(Integer petId) {
		// Return null to indicate pet service is unavailable
		// The calling code should handle this appropriately
		return null;
	}

}
