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

package org.springframework.beans;

import java.beans.PropertyDescriptor;

/**
 * Bean包装接口,设置和获取Bean的属性<p>
 * The central interface of Spring's low-level JavaBeans infrastructure.
 *
 * <p>Typically not used directly but rather implicitly via a
 * {@link org.springframework.beans.factory.BeanFactory} or a
 * {@link org.springframework.validation.DataBinder}.
 *
 * <p>Provides operations to analyze and manipulate standard JavaBeans:
 * the ability to get and set property values (individually or in bulk),
 * get property descriptors, and query the readability/writability of properties.
 *
 * <p>This interface supports <b>nested properties</b> enabling the setting
 * of properties on subproperties to an unlimited depth.
 *
 * <p>A BeanWrapper's default for the "extractOldValueForEditor" setting
 * is "false", to avoid side effects caused by getter method invocations.
 * Turn this to "true" to expose present property values to custom editors.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13 April 2001
 * @see PropertyAccessor
 * @see PropertyEditorRegistry
 * @see PropertyAccessorFactory#forBeanPropertyAccess
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.validation.BeanPropertyBindingResult
 * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
 */
public interface BeanWrapper extends ConfigurablePropertyAccessor {

	/**
	 * 获取被包装的bean对象<p>
	 * Return the bean instance wrapped by this object, if any.
	 * @return the bean instance, or {@code null} if none set
	 */
	Object getWrappedInstance();

	/**
	 * 获取这个对象的bean包装类的Class类型<p>
	 * Return the type of the wrapped JavaBean object.
	 * @return the type of the wrapped bean instance,
	 * or {@code null} if no wrapped object has been set
	 */
	Class<?> getWrappedClass();

	/**
	 * 获取属性的描述类数组<p>
	 * Obtain the PropertyDescriptors for the wrapped object
	 * (as determined by standard JavaBeans introspection).
	 * @return the PropertyDescriptors for the wrapped object
	 */
	PropertyDescriptor[] getPropertyDescriptors();

	/**
	 * 获取指定属性名的属性描述类<p>
	 * Obtain the property descriptor for a specific property
	 * of the wrapped object.
	 * @param propertyName the property to obtain the descriptor for
	 * (may be a nested path, but no indexed/mapped property)
	 * @return the property descriptor for the specified property
	 * @throws InvalidPropertyException if there is no such property
	 */
	PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException;

	/**
	 * 设置自动扩展嵌套访问路径<p>
	 * Set whether this BeanWrapper should attempt to "auto-grow" a
	 * nested path that contains a {@code null} value.
	 * <p>If {@code true}, a {@code null} path location will be populated
	 * with a default object value and traversed instead of resulting in a
	 * {@link NullValueInNestedPathException}. Turning this flag on also enables
	 * auto-growth of collection elements when accessing an out-of-bounds index.
	 * <p>Default is {@code false} on a plain BeanWrapper.
	 */
	void setAutoGrowNestedPaths(boolean autoGrowNestedPaths);

	/**
	 * 判断是否自动扩展嵌套访问路径<p>
	 * Return whether "auto-growing" of nested paths has been activated.
	 */
	boolean isAutoGrowNestedPaths();

	/**
	 * 设置自动扩展集合界限<p>
	 * Specify a limit for array and collection auto-growing.
	 * <p>Default is unlimited on a plain BeanWrapper.
	 */
	void setAutoGrowCollectionLimit(int autoGrowCollectionLimit);

	/**
	 * 获取自动扩展集合的界限<p>
	 * Return the limit for array and collection auto-growing.
	 */
	int getAutoGrowCollectionLimit();

}
