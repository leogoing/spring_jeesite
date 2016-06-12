/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.core.env;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 配置源集的默认实现,支持对配置源集的操作<p>
 * Default implementation of the {@link PropertySources} interface.
 * Allows manipulation of contained property sources and provides a constructor
 * for copying an existing {@code PropertySources} instance.
 *
 * <p>Where <em>precedence</em> is mentioned in methods such as {@link #addFirst}
 * and {@link #addLast}, this is with regard to the order in which property sources
 * will be searched when resolving a given property with a {@link PropertyResolver}.
 *
 * @author Chris Beams
 * @since 3.1
 * @see PropertySourcesPropertyResolver
 */
public class MutablePropertySources implements PropertySources {
	
	/**配置源不存在Msg*/
	static final String NON_EXISTENT_PROPERTY_SOURCE_MESSAGE = "PropertySource named [%s] does not exist";
	static final String ILLEGAL_RELATIVE_ADDITION_MESSAGE = "PropertySource named [%s] cannot be added relative to itself";

	private final Log logger;

	/**
	 * 存放配置源集合
	 */
	private final LinkedList<PropertySource<?>> propertySourceList = new LinkedList<PropertySource<?>>();


	/**
	 * 无参构造,加载logger<p>
	 * Create a new {@link MutablePropertySources} object.
	 */
	public MutablePropertySources() {
		this.logger = LogFactory.getLog(this.getClass());
	}

	/**
	 * 创建新对象,复制传入的配置源集所有的配置源<p>
	 * Create a new {@code MutablePropertySources} from the given propertySources
	 * object, preserving the original order of contained {@code PropertySource} objects.
	 */
	public MutablePropertySources(PropertySources propertySources) {
		this();
		for (PropertySource<?> propertySource : propertySources) {
			this.addLast(propertySource);
		}
	}

	/**
	 * Create a new {@link MutablePropertySources} object and inherit the given logger,
	 * usually from an enclosing {@link Environment}.
	 */
	MutablePropertySources(Log logger) {
		this.logger = logger;
	}


	@Override
	public boolean contains(String name) {
		return this.propertySourceList.contains(PropertySource.named(name));
	}

	@Override
	public PropertySource<?> get(String name) {
		int index = this.propertySourceList.indexOf(PropertySource.named(name));
		return index == -1 ? null : this.propertySourceList.get(index);
	}

	@Override
	public Iterator<PropertySource<?>> iterator() {
		return this.propertySourceList.iterator();
	}

	/**
	 * 添加配置源到首位<p>
	 * Add the given property source object with highest precedence.
	 */
	public void addFirst(PropertySource<?> propertySource) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Adding [%s] PropertySource with highest search precedence",
					propertySource.getName()));
		}
		removeIfPresent(propertySource);
		this.propertySourceList.addFirst(propertySource);
	}

	/**
	 * 添加配置源到末位<p>
	 * Add the given property source object with lowest precedence.
	 */
	public void addLast(PropertySource<?> propertySource) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Adding [%s] PropertySource with lowest search precedence",
					propertySource.getName()));
		}
		removeIfPresent(propertySource);
		this.propertySourceList.addLast(propertySource);
	}

	/**
	 * 添加配置源到指定配置源前<p>
	 * Add the given property source object with precedence immediately higher
	 * than the named relative property source.
	 */
	public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Adding [%s] PropertySource with search precedence immediately higher than [%s]",
					propertySource.getName(), relativePropertySourceName));
		}
		assertLegalRelativeAddition(relativePropertySourceName, propertySource);
		removeIfPresent(propertySource);
		int index = assertPresentAndGetIndex(relativePropertySourceName);
		addAtIndex(index, propertySource);
	}

	/**
	 * 添加配置源到指定配置源后<p>
	 * Add the given property source object with precedence immediately lower
	 * than the named relative property source.
	 */
	public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Adding [%s] PropertySource with search precedence immediately lower than [%s]",
					propertySource.getName(), relativePropertySourceName));
		}
		assertLegalRelativeAddition(relativePropertySourceName, propertySource);
		removeIfPresent(propertySource);
		int index = assertPresentAndGetIndex(relativePropertySourceName);
		addAtIndex(index + 1, propertySource);
	}

	/**
	 * 获取指定配置源在配置源集合中下标位置<p>
	 * Return the precedence of the given property source, {@code -1} if not found.
	 */
	public int precedenceOf(PropertySource<?> propertySource) {
		return this.propertySourceList.indexOf(propertySource);
	}

	/**
	 * 删除指定配置源名的配置源并返回<p>
	 * Remove and return the property source with the given name, {@code null} if not found.
	 * @param name the name of the property source to find and remove
	 */
	public PropertySource<?> remove(String name) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Removing [%s] PropertySource", name));
		}
		int index = this.propertySourceList.indexOf(PropertySource.named(name));
		return index == -1 ? null : this.propertySourceList.remove(index);
	}

	/**
	 * 将指定的name属性的配置源替换为给定的配置源对象<p>
	 * Replace the property source with the given name with the given property source object.
	 * @param name the name of the property source to find and replace
	 * @param propertySource the replacement property source
	 * @throws IllegalArgumentException if no property source with the given name is present
	 * @see #contains
	 */
	public void replace(String name, PropertySource<?> propertySource) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Replacing [%s] PropertySource with [%s]",
					name, propertySource.getName()));
		}
		int index = assertPresentAndGetIndex(name);
		this.propertySourceList.set(index, propertySource);
	}

	/**
	 * 返回配置源集的大小<p>
	 * Return the number of {@link PropertySource} objects contained.
	 */
	public int size() {
		return this.propertySourceList.size();
	}

	@Override
	public String toString() {
		String[] names = new String[this.size()];
		for (int i=0; i < size(); i++) {
			names[i] = this.propertySourceList.get(i).getName();
		}
		return String.format("[%s]", StringUtils.arrayToCommaDelimitedString(names));
	}

	/**
	 * 确保指定的配置源name属性为指定字符串,否则抛异常<p>
	 * Ensure that the given property source is not being added relative to itself.
	 */
	protected void assertLegalRelativeAddition(String relativePropertySourceName, PropertySource<?> propertySource) {
		String newPropertySourceName = propertySource.getName();
		Assert.isTrue(!relativePropertySourceName.equals(newPropertySourceName),
				String.format(ILLEGAL_RELATIVE_ADDITION_MESSAGE, newPropertySourceName));
	}

	/**
	 * 删除指定配置源<p>
	 * Remove the given property source if it is present.
	 */
	protected void removeIfPresent(PropertySource<?> propertySource) {
		if (this.propertySourceList.contains(propertySource)) {
			this.propertySourceList.remove(propertySource);
		}
	}

	/**
	 * 添加配置源到指定位置<p>
	 * Add the given property source at a particular index in the list.
	 */
	private void addAtIndex(int index, PropertySource<?> propertySource) {
		removeIfPresent(propertySource);
		this.propertySourceList.add(index, propertySource);
	}

	/**
	 * 判断是否存在指定name属性的配置源<p>
	 * Assert that the named property source is present and return its index.
	 * @param name the {@linkplain PropertySource#getName() name of the property source}
	 * to find
	 * @throws IllegalArgumentException if the named property source is not present
	 */
	private int assertPresentAndGetIndex(String name) {
		int index = this.propertySourceList.indexOf(PropertySource.named(name));
		Assert.isTrue(index >= 0, String.format(NON_EXISTENT_PROPERTY_SOURCE_MESSAGE, name));
		return index;
	}

}
