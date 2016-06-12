/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.io;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 继承属性编辑支持类,融合了资源与类加载类用来加载指定路径资源和属性解析器类来解析占位符(主要set和getAsText两个方法的扩展)<p>
 * {@link java.beans.PropertyEditor Editor} for {@link Resource}
 * descriptors, to automatically convert {@code String} locations
 * e.g. {@code file:C:/myfile.txt} or {@code classpath:myfile.txt} to
 * {@code Resource} properties instead of using a {@code String} location property.
 *
 * <p>The path may contain {@code ${...}} placeholders, to be
 * resolved as {@link org.springframework.core.env.Environment} properties:
 * e.g. {@code ${user.dir}}. Unresolvable placeholders are ignored by default.
 *
 * <p>Delegates to a {@link ResourceLoader} to do the heavy lifting,
 * by default using a {@link DefaultResourceLoader}.
 *
 * @author Juergen Hoeller
 * @author Dave Syer
 * @author Chris Beams
 * @since 28.12.2003
 * @see Resource
 * @see ResourceLoader
 * @see DefaultResourceLoader
 * @see PropertyResolver#resolvePlaceholders
 */
public class ResourceEditor extends PropertyEditorSupport {

	private final ResourceLoader resourceLoader;

	private PropertyResolver propertyResolver;

	private final boolean ignoreUnresolvablePlaceholders;


	/**
	 * Create a new instance of the {@link ResourceEditor} class
	 * using a {@link DefaultResourceLoader} and {@link StandardEnvironment}.
	 */
	public ResourceEditor() {
		this(new DefaultResourceLoader(), null);
	}

	/**
	 * Create a new instance of the {@link ResourceEditor} class
	 * using the given {@link ResourceLoader} and a {@link StandardEnvironment}.
	 * @param resourceLoader the {@code ResourceLoader} to use
	 * @deprecated as of Spring 3.1 in favor of
	 * {@link #ResourceEditor(ResourceLoader, PropertyResolver)}
	 */
	@Deprecated
	public ResourceEditor(ResourceLoader resourceLoader) {
		this(resourceLoader, null, true);
	}

	/**
	 * Create a new instance of the {@link ResourceEditor} class
	 * using the given {@link ResourceLoader}.
	 * @param resourceLoader the {@code ResourceLoader} to use
	 * @param ignoreUnresolvablePlaceholders whether to ignore unresolvable placeholders
	 * if no corresponding property could be found
	 * @deprecated as of Spring 3.1 in favor of
	 * {@link #ResourceEditor(ResourceLoader, PropertyResolver, boolean)}
	 */
	@Deprecated
	public ResourceEditor(ResourceLoader resourceLoader, boolean ignoreUnresolvablePlaceholders) {
		this(resourceLoader, null, ignoreUnresolvablePlaceholders);
	}

	/**
	 * Create a new instance of the {@link ResourceEditor} class
	 * using the given {@link ResourceLoader} and {@link PropertyResolver}.
	 * @param resourceLoader the {@code ResourceLoader} to use
	 * @param propertyResolver the {@code PropertyResolver} to use
	 */
	public ResourceEditor(ResourceLoader resourceLoader, PropertyResolver propertyResolver) {
		this(resourceLoader, propertyResolver, true);
	}

	/**
	 * Create a new instance of the {@link ResourceEditor} class
	 * using the given {@link ResourceLoader}.
	 * @param resourceLoader the {@code ResourceLoader} to use
	 * @param propertyResolver the {@code PropertyResolver} to use
	 * @param ignoreUnresolvablePlaceholders whether to ignore unresolvable placeholders
	 * if no corresponding property could be found in the given {@code propertyResolver}
	 */
	public ResourceEditor(ResourceLoader resourceLoader, PropertyResolver propertyResolver, boolean ignoreUnresolvablePlaceholders) {
		Assert.notNull(resourceLoader, "ResourceLoader must not be null");
		this.resourceLoader = resourceLoader;
		this.propertyResolver = propertyResolver;
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}


	/**
	 * 若传入字符串有字符则替换占位符后根据字符路径信息获取资源对象否则为null然后设置为value属性同时通知该属性所有监听者内容发生了改变(监听者模式)
	 */
	@Override
	public void setAsText(String text) {
		if (StringUtils.hasText(text)) {
			String locationToUse = resolvePath(text).trim();
			setValue(this.resourceLoader.getResource(locationToUse));
		}
		else {
			setValue(null);
		}
	}

	/**
	 * 替换传入路径的占位符<p>
	 * Resolve the given path, replacing placeholders with corresponding
	 * property values from the {@code environment} if necessary.
	 * @param path the original file path
	 * @return the resolved file path
	 * @see PropertyResolver#resolvePlaceholders
	 * @see PropertyResolver#resolveRequiredPlaceholders
	 */
	protected String resolvePath(String path) {
		if (this.propertyResolver == null) {
			this.propertyResolver = new StandardEnvironment();
		}
		return (this.ignoreUnresolvablePlaceholders ? this.propertyResolver.resolvePlaceholders(path) :
				this.propertyResolver.resolveRequiredPlaceholders(path));
	}


	/**
	 * 获取value属性值转为资源对象后获取其URL对象然后构造此 URL的字符串表示形式并返回
	 */
	@Override
	public String getAsText() {
		Resource value = (Resource) getValue();
		try {
			// Try to determine URL for resource.
			return (value != null ? value.getURL().toExternalForm() : "");
		}
		catch (IOException ex) {
			// Couldn't determine resource URL - return null to indicate
			// that there is no appropriate text representation.
			return null;
		}
	}

}
