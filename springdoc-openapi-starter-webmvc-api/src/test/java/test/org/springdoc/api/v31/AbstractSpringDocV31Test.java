/*
 *
 *  *
 *  *  *
 *  *  *  *
 *  *  *  *  * Copyright 2019-2024 the original author or authors.
 *  *  *  *  *
 *  *  *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *  *  * you may not use this file except in compliance with the License.
 *  *  *  *  * You may obtain a copy of the License at
 *  *  *  *  *
 *  *  *  *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *  *  *  *
 *  *  *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *  *  * See the License for the specific language governing permissions and
 *  *  *  *  * limitations under the License.
 *  *  *  *
 *  *  *
 *  *
 *
 */

package test.org.springdoc.api.v31;

import org.junit.jupiter.api.Test;
import org.springdoc.core.utils.Constants;
import test.org.springdoc.api.AbstractCommonTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springdoc.core.utils.Constants.SPRINGDOC_CACHE_DISABLED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A common base for OpenAPI 3.1 tests which provides the necessary foundation for OpenAPI 3.1 tests, 
 * making the test setup cleaner and more consistent.
 *
 * @author zdary
 */
@SpringBootTest
@TestPropertySource(properties = { SPRINGDOC_CACHE_DISABLED + "=true", "springdoc.api-docs.version=OPENAPI_3_1" })
public abstract class AbstractSpringDocV31Test extends AbstractCommonTest {

	@Test
	protected void testApp() throws Exception {
		String className = getClass().getSimpleName();
		String testNumber = className.replaceAll("[^0-9]", "");
		MvcResult mockMvcResult = mockMvc.perform(get(Constants.DEFAULT_API_DOCS_URL)).andExpect(status().isOk())
				.andExpect(jsonPath("$.openapi", is("3.1.0"))).andReturn();
		String result = mockMvcResult.getResponse().getContentAsString();
		String expected = getContent("results/3.1.0/app" + testNumber + ".json");
		assertEquals(expected, result, true);
	}
}
