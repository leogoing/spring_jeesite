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

package org.springframework.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 占位符替换工具类<p>
 * Utility class for working with Strings that have placeholder values in them. A placeholder takes the form
 * {@code ${name}}. Using {@code PropertyPlaceholderHelper} these placeholders can be substituted for
 * user-supplied values. <p> Values for substitution can be supplied using a {@link Properties} instance or
 * using a {@link PlaceholderResolver}.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 3.0
 */
public class PropertyPlaceholderHelper {

	private static final Log logger = LogFactory.getLog(PropertyPlaceholderHelper.class);

	/**
	 * 存放通用的简单占位符号的前缀如：{、[、(
	 */
	private static final Map<String, String> wellKnownSimplePrefixes = new HashMap<String, String>(4);

	/**
	 * 初始化根据后缀存放对应前缀，因为后缀必定是占位符最后的字符串
	 */
	static {
		wellKnownSimplePrefixes.put("}", "{");
		wellKnownSimplePrefixes.put("]", "[");
		wellKnownSimplePrefixes.put(")", "(");
	}

	/**
	 * 占位符前缀
	 */
	private final String placeholderPrefix;

	/**
	 * 占位符后缀
	 */
	private final String placeholderSuffix;

	/**
	 * 占位符简单前缀如{、(
	 */
	private final String simplePrefix;

	/**
	 * 占位字符串正文分隔符
	 */
	private final String valueSeparator;

	/**
	 * 忽略无法解析的占位符
	 */
	private final boolean ignoreUnresolvablePlaceholders;


	/**
	 * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied prefix and suffix.
	 * Unresolvable placeholders are ignored.
	 * @param placeholderPrefix the prefix that denotes the start of a placeholder
	 * @param placeholderSuffix the suffix that denotes the end of a placeholder
	 */
	public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix) {
		this(placeholderPrefix, placeholderSuffix, null, true);
	}

	/**
	 * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied prefix and suffix.
	 * @param placeholderPrefix the prefix that denotes the start of a placeholder
	 * @param placeholderSuffix the suffix that denotes the end of a placeholder
	 * @param valueSeparator the separating character between the placeholder variable
	 * and the associated default value, if any
	 * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should
	 * be ignored ({@code true}) or cause an exception ({@code false})
	 */
	public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix,
			String valueSeparator, boolean ignoreUnresolvablePlaceholders) {

		Assert.notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
		Assert.notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
		this.placeholderPrefix = placeholderPrefix;
		this.placeholderSuffix = placeholderSuffix;
		String simplePrefixForSuffix = wellKnownSimplePrefixes.get(this.placeholderSuffix);//根据后缀查找占位符对应简单前缀如{
		if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {//如果占位符初始化前缀以简单前缀结束
			this.simplePrefix = simplePrefixForSuffix;//占位符简单前缀设为与后缀对应的前部分
		}
		else {
			this.simplePrefix = this.placeholderPrefix;//占位符简单前缀设为初始化前缀
		}
		this.valueSeparator = valueSeparator;
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}


	/**
	 * 替换所有的占位符根据传入的properties<p>
	 * Replaces all placeholders of format {@code ${name}} with the corresponding
	 * property from the supplied {@link Properties}.
	 * @param value 要被替换的包含占位符的字符串<br>the value containing the placeholders to be replaced
	 * @param properties 存放了占位符属性值<br>the {@code Properties} to use for replacement
	 * @return the supplied value with placeholders replaced inline
	 */
	public String replacePlaceholders(String value, final Properties properties) {
		Assert.notNull(properties, "'properties' must not be null");
		return replacePlaceholders(value, new PlaceholderResolver() {
			@Override
			public String resolvePlaceholder(String placeholderName) {
				return properties.getProperty(placeholderName);
			}
		});
	}

	/**
	 * 替换所有的占位符根据占位符解析对象<p>
	 * Replaces all placeholders of format {@code ${name}} with the value returned
	 * from the supplied {@link PlaceholderResolver}.
	 * @param value the value containing the placeholders to be replaced
	 * @param placeholderResolver the {@code PlaceholderResolver} to use for replacement
	 * @return the supplied value with placeholders replaced inline
	 */
	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		Assert.notNull(value, "'value' must not be null");
		return parseStringValue(value, placeholderResolver, new HashSet<String>());
	}

	/**
	 * 替换指定文本中的占位符主逻辑
	 * @param strVal  要替换的文本
	 * @param placeholderResolver  占位符解析对象
	 * @param visitedPlaceholders  存放占位符名的集合，适用于递归，用于判断嵌套占位符是否有同名占位符
	 * @return
	 */
	protected String parseStringValue(
			String strVal, PlaceholderResolver placeholderResolver, Set<String> visitedPlaceholders) {

		StringBuilder result = new StringBuilder(strVal);

		int startIndex = strVal.indexOf(this.placeholderPrefix);//初始占位符前缀在传入占位符中的位置
		while (startIndex != -1) {
			int endIndex = findPlaceholderEndIndex(result, startIndex);//查找占位符结束位置
			if (endIndex != -1) {
				String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);//占位符正文
				String originalPlaceholder = placeholder;
				if (!visitedPlaceholders.add(originalPlaceholder)) {//如果嵌套中已存在相同占位符则抛异常
					throw new IllegalArgumentException(
							"Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
				}
				// Recursive invocation, parsing placeholders contained in the placeholder key.
				placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);//递归解析嵌套占位符
				// Now obtain the value for the fully resolved key...
				String propVal = placeholderResolver.resolvePlaceholder(placeholder);//获取占位符对应属性值
				if (propVal == null && this.valueSeparator != null) {//如果对应属性值为空并有分隔符
					int separatorIndex = placeholder.indexOf(this.valueSeparator);//分隔符下标位置
					if (separatorIndex != -1) {
						String actualPlaceholder = placeholder.substring(0, separatorIndex);//占位符在第一个分隔符前的内容
						String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());//占位符在第一个分隔符后的内容
						propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);//用占位符分隔符前的部分查找对应的属性值
						if (propVal == null) {//查找不到则将占位符的分隔符后的部分作为属性值
							propVal = defaultValue;
						}
					}
				}
				if (propVal != null) {
					// Recursive invocation, parsing placeholders contained in the
					// previously resolved placeholder value.
					propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);//将对应的属性值中存在的占位符替换掉
					result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);//开始替换
					if (logger.isTraceEnabled()) {
						logger.trace("Resolved placeholder '" + placeholder + "'");
					}
					startIndex = result.indexOf(this.placeholderPrefix, startIndex + propVal.length());//定位下一个占位符前缀的下标位置
				}
				else if (this.ignoreUnresolvablePlaceholders) {//跳过无法解析的占位符
					// Proceed with unprocessed value.
					startIndex = result.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
				}
				else {
					throw new IllegalArgumentException("Could not resolve placeholder '" +
							placeholder + "'" + " in string value \"" + strVal + "\"");
				}
				visitedPlaceholders.remove(originalPlaceholder);//解析完删除记录
			}
			else {//没有占位符了
				startIndex = -1;
			}
		}

		return result.toString();
	}

	/**
	 * 查找占位符的结束下标,没有返回-1
	 * @param buf  传入的占位符
	 * @param startIndex  前缀有效起点位置
	 * @return 占位符结束下标位置
	 */
	private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
		int index = startIndex + this.placeholderPrefix.length();//占位符正文开始处=起点位置+初始占位符前缀长度
		int withinNestedPlaceholder = 0;//记录简单前后缀的嵌套个数如{{{}}}
		while (index < buf.length()) {
			if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {//从占位符正文开始之后是否包含初始占位符后缀
				if (withinNestedPlaceholder > 0) {//跳过嵌套的后缀
					withinNestedPlaceholder--;
					index = index + this.placeholderSuffix.length();
				}else {
					return index;
				}
			}
			else if (StringUtils.substringMatch(buf, index, this.simplePrefix)) {//从占位符正文开始之后是否包含简单前缀
				withinNestedPlaceholder++;
				index = index + this.simplePrefix.length();//下标加简单前缀长度
			}
			else {
				index++;
			}
		}
		return -1;
	}


	/**
	 * 占位符解析内部接口<p>
	 * Strategy interface used to resolve replacement values for placeholders contained in Strings.
	 */
	public static interface PlaceholderResolver {

		/**
		 * 根据占位符名获取替换值<p>
		 * Resolve the supplied placeholder name to the replacement value.
		 * @param placeholderName the name of the placeholder to resolve
		 * @return the replacement value, or {@code null} if no replacement is to be made
		 */
		String resolvePlaceholder(String placeholderName);
	}

}
