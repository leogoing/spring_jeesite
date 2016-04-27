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

package org.springframework.core.env;

/**
 * 属性解析器，用于解析相应key的value(各种getProperty方法及占位符解析)<p>
 * Interface for resolving properties against any underlying source.
 *
 * @author Chris Beams
 * @since 3.1
 * @see Environment
 * @see PropertySourcesPropertyResolver
 */
public interface PropertyResolver {

	/**
	 * 判断是否包含指定key<p>
	 * Return whether the given property key is available for resolution, i.e.,
	 * the value for the given key is not {@code null}.
	 */
	boolean containsProperty(String key);

	/**
	 * 返回指定key的属性值<p>
	 * Return the property value associated with the given key, or {@code null}
	 * if the key cannot be resolved.
	 * @param key the property name to resolve
	 * @see #getProperty(String, String)
	 * @see #getProperty(String, Class)
	 * @see #getRequiredProperty(String)
	 */
	String getProperty(String key);

	/**
	 * 返回指定key的属性值,没有则返回默认<p>
	 * Return the property value associated with the given key, or
	 * {@code defaultValue} if the key cannot be resolved.
	 * @param key the property name to resolve
	 * @param defaultValue the default value to return if no value is found
	 * @see #getRequiredProperty(String)
	 * @see #getProperty(String, Class)
	 */
	String getProperty(String key, String defaultValue);

	/**
	 * 返回指定key对应得属性值并转换为指定类型,无法转换则为null<p>
	 * Return the property value associated with the given key, or {@code null}
	 * if the key cannot be resolved.
	 * @param key the property name to resolve
	 * @param targetType the expected type of the property value
	 * @see #getRequiredProperty(String, Class)
	 */
	<T> T getProperty(String key, Class<T> targetType);

	/**
	 * 返回指定key对应得属性值并转换为指定类型,无法转换则为指定类型<p>
	 * Return the property value associated with the given key, or
	 * {@code defaultValue} if the key cannot be resolved.
	 * @param key the property name to resolve
	 * @param targetType the expected type of the property value
	 * @param defaultValue the default value to return if no value is found
	 * @see #getRequiredProperty(String, Class)
	 */
	<T> T getProperty(String key, Class<T> targetType, T defaultValue);

	/**
	 * 获取指定key的属性值转换为指定class对象返回<p>
	 * Convert the property value associated with the given key to a {@code Class}
	 * of type {@code T} or {@code null} if the key cannot be resolved.
	 * @throws org.springframework.core.convert.ConversionException if class specified
	 * by property value cannot be found  or loaded or if targetType is not assignable
	 * from class specified by property value
	 * @see #getProperty(String, Class)
	 */
	<T> Class<T> getPropertyAsClass(String key, Class<T> targetType);

	/**
	 * 返回指定key的属性值,没有则抛异常<p>
	 * Return the property value associated with the given key, converted to the given
	 * targetType (never {@code null}).
	 * @throws IllegalStateException if the key cannot be resolved
	 * @see #getRequiredProperty(String, Class)
	 */
	String getRequiredProperty(String key) throws IllegalStateException;

	/**
	 * 返回指定key对应得属性值并转换为指定类型,无法转换则为抛异常<p>
	 * Return the property value associated with the given key, converted to the given
	 * targetType (never {@code null}).
	 * @throws IllegalStateException if the given key cannot be resolved
	 */
	<T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

	/**
	 * 替换文本中的占位符（${key}）到属性值,未传入对应的属性值是为了实现类的扩展性<p>
	 * Resolve ${...} placeholders in the given text, replacing them with corresponding
	 * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
	 * no default value are ignored and passed through unchanged.
	 * @param text the String to resolve
	 * @return the resolved String (never {@code null})
	 * @throws IllegalArgumentException if given text is {@code null}
	 * @see #resolveRequiredPlaceholders
	 * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String)
	 */
	String resolvePlaceholders(String text);

	/**
	 * 替换文本中的占位符（${key}）到属性值,找不到抛异常<p>
	 * Resolve ${...} placeholders in the given text, replacing them with corresponding
	 * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
	 * no default value will cause an IllegalArgumentException to be thrown.
	 * @return the resolved String (never {@code null})
	 * @throws IllegalArgumentException if given text is {@code null}
	 * or if any placeholders are unresolvable
	 * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String, boolean)
	 */
	String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

}
