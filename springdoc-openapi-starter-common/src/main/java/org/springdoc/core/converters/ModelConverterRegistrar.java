/*
 *
 *  *
 *  *  *
 *  *  *  *
 *  *  *  *  *
 *  *  *  *  *  * Copyright 2019-2025 the original author or authors.
 *  *  *  *  *  *
 *  *  *  *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *  *  *  * you may not use this file except in compliance with the License.
 *  *  *  *  *  * You may obtain a copy of the License at
 *  *  *  *  *  *
 *  *  *  *  *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *  *  *  *  *
 *  *  *  *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  *  *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *  *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *  *  *  * See the License for the specific language governing permissions and
 *  *  *  *  *  * limitations under the License.
 *  *  *  *  *
 *  *  *  *
 *  *  *
 *  *
 *
 */

package org.springdoc.core.converters;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json31;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.properties.SpringDocConfigProperties;

/**
 * Wrapper for model converters to only register converters once
 *
 * @author bnasslahsen
 */
public class ModelConverterRegistrar {

	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelConverterRegistrar.class);

	/**
	 * The constant modelConvertersInstance.
	 */
	private final ModelConverters modelConvertersInstance;


	/**
	 * The singleton fallback instance.
	 */
	private static volatile ModelConverters modelConvertersFallbackInstance;
	
	/**
	 * Instantiates a new Model converter registrar.
	 *
	 * @param modelConverters           spring registered model converter beans which have to be registered in {@link ModelConverters} instance
	 * @param springDocConfigProperties the spring doc config properties
	 */
	public ModelConverterRegistrar(List<ModelConverter> modelConverters, SpringDocConfigProperties springDocConfigProperties) {
		modelConvertersInstance = ModelConverters.getInstance(springDocConfigProperties.isOpenapi31());
		for (ModelConverter modelConverter : modelConverters) {
			Optional<ModelConverter> registeredConverterOptional = getRegisteredConverterSameAs(modelConverter);
			registeredConverterOptional.ifPresent(modelConvertersInstance::removeConverter);
			modelConvertersInstance.addConverter(modelConverter);
		}
		if (springDocConfigProperties.isOpenapi31()) {
			initializeFallbackInstance();
		}
	}

	/**
	 * Initialize fallback instance.
	 */
	private static synchronized void initializeFallbackInstance() {
		if (modelConvertersFallbackInstance == null) {
			modelConvertersFallbackInstance = new ModelConverters(true);
			modelConvertersFallbackInstance.addConverter(new ModelResolver(Json31.mapper()));
		}
	}

	/**
	 * Gets model converters fallback instance.
	 *
	 * @return the model converters fallback instance
	 */
	public static ModelConverters getModelConvertersFallbackInstance() {
		return modelConvertersFallbackInstance;
	}
	
	/**
	 * Gets registered converter same as.
	 *
	 * @param modelConverter the model converter
	 * @return the registered converter same as
	 */
	@SuppressWarnings("unchecked")
	private Optional<ModelConverter> getRegisteredConverterSameAs(ModelConverter modelConverter) {
		try {
			List<ModelConverter> modelConverters = (List<ModelConverter>) FieldUtils.readDeclaredField(modelConvertersInstance, "converters", true);
			return modelConverters.stream()
					.filter(registeredModelConverter -> isSameConverter(registeredModelConverter, modelConverter))
					.findFirst();
		}
		catch (IllegalAccessException exception) {
			LOGGER.warn(exception.getMessage());
		}
		return Optional.empty();
	}

	/**
	 * Is same converter boolean.
	 *
	 * @param modelConverter1 the model converter 1
	 * @param modelConverter2 the model converter 2
	 * @return the boolean
	 */
	private boolean isSameConverter(ModelConverter modelConverter1, ModelConverter modelConverter2) {
		// comparing by the converter type
		Class<? extends ModelConverter> modelConverter1Class = modelConverter1.getClass();
		Class<? extends ModelConverter> modelConverter2Class = modelConverter2.getClass();
		return modelConverter1Class.getName().equals(modelConverter2Class.getName());
	}
}
