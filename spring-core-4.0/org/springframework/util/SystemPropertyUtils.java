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

package org.springframework.util;

/**
 * Helper class for resolving placeholders in texts. Usually applied to file paths.
 *
 * <p>A text may contain {@code ${...}} placeholders, to be resolved as system properties:
 * e.g. {@code ${user.dir}}. Default values can be supplied using the ":" separator
 * between key and value.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Dave Syer
 * @since 1.2.5
 * @see #PLACEHOLDER_PREFIX
 * @see #PLACEHOLDER_SUFFIX
 * @see System#getProperty(String)
 */
public abstract class SystemPropertyUtils {

	/**占位符前缀 Prefix for system property placeholders: "${" */
	public static final String PLACEHOLDER_PREFIX = "${";

	/**占位符后缀 Suffix for system property placeholders: "}" */
	public static final String PLACEHOLDER_SUFFIX = "}";

	/**占位符分隔符 Value separator for system property placeholders: ":" */
	public static final String VALUE_SEPARATOR = ":";

	/**
	 * 严格的占位符工具类,不允许忽略无法解析的占位符
	 */
	private static final PropertyPlaceholderHelper strictHelper =
			new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, false);

	/**
	 * 不严格的占位符工具类,可以忽略无法解析的占位符
	 */
	private static final PropertyPlaceholderHelper nonStrictHelper =
			new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, true);


	/**
	 * 替换传入文本中的占位符<p>
	 * Resolve {@code ${...}} placeholders in the given text, replacing them with
	 * corresponding system property values.
	 * @param text the String to resolve
	 * @return the resolved String
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 * @throws IllegalArgumentException if there is an unresolvable placeholder
	 */
	public static String resolvePlaceholders(String text) {
		return resolvePlaceholders(text, false);
	}

	/**
	 * 替换传入文本中的占位符，根据指定布尔值执行是否忽略无法解析的占位符<p>
	 * Resolve {@code ${...}} placeholders in the given text, replacing them with
	 * corresponding system property values. Unresolvable placeholders with no default
	 * value are ignored and passed through unchanged if the flag is set to {@code true}.
	 * @param text 被解析的文本the String to resolve
	 * @param ignoreUnresolvablePlaceholders 是否忽略无法解析的占位符whether unresolved placeholders are to be ignored
	 * @return the resolved String
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 * @throws IllegalArgumentException if there is an unresolvable placeholder
	 * and the "ignoreUnresolvablePlaceholders" flag is {@code false}
	 */
	public static String resolvePlaceholders(String text, boolean ignoreUnresolvablePlaceholders) {
		PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper);
		return helper.replacePlaceholders(text, new SystemPropertyPlaceholderResolver(text));
	}


	/**
	 * 系统配置占位符属性获取内部类<p>
	 * PlaceholderResolver implementation that resolves against system properties
	 * and system environment variables.
	 */
	private static class SystemPropertyPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

		private final String text;

		public SystemPropertyPlaceholderResolver(String text) {
			this.text = text;
		}

		/**
		 * 从系统配置或系统环境中获取占位符对应属性值
		 */
		@Override
		public String resolvePlaceholder(String placeholderName) {
			try {
				String propVal = System.getProperty(placeholderName);//系统配置文件中获取
				if (propVal == null) {
					// Fall back to searching the system environment.
					propVal = System.getenv(placeholderName);//系统环境中获取
				}
				return propVal;
			}
			catch (Throwable ex) {
				System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" +
						this.text + "] as system property: " + ex);
				return null;
			}
		}
	}

}
