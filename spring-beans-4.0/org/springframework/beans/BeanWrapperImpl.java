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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.CollectionFactory;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Default {@link BeanWrapper} implementation that should be sufficient
 * for all typical use cases. Caches introspection results for efficiency.
 *
 * <p>Note: Auto-registers default property editors from the
 * {@code org.springframework.beans.propertyeditors} package, which apply
 * in addition to the JDK's standard PropertyEditors. Applications can call
 * the {@link #registerCustomEditor(Class, java.beans.PropertyEditor)} method
 * to register an editor for a particular instance (i.e. they are not shared
 * across the application). See the base class
 * {@link PropertyEditorRegistrySupport} for details.
 *
 * <p>{@code BeanWrapperImpl} will convert collection and array values
 * to the corresponding target collections or arrays, if necessary. Custom
 * property editors that deal with collections or arrays can either be
 * written via PropertyEditor's {@code setValue}, or against a
 * comma-delimited String via {@code setAsText}, as String arrays are
 * converted in such a format if the array itself is not assignable.
 *
 * <p><b>NOTE: As of Spring 2.5, this is - for almost all purposes - an
 * internal class.</b> It is just public in order to allow for access from
 * other framework packages. For standard application access purposes, use the
 * {@link PropertyAccessorFactory#forBeanPropertyAccess} factory method instead.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 15 April 2001
 * @see #registerCustomEditor
 * @see #setPropertyValues
 * @see #setPropertyValue
 * @see #getPropertyValue
 * @see #getPropertyType
 * @see BeanWrapper
 * @see PropertyEditorRegistrySupport
 */
public class BeanWrapperImpl extends AbstractPropertyAccessor implements BeanWrapper {

	/**
	 * We'll create a lot of these objects, so we don't want a new logger every time.
	 */
	private static final Log logger = LogFactory.getLog(BeanWrapperImpl.class);


	/**被包装的对象  The wrapped object */
	private Object object;

	private String nestedPath = "";

	private Object rootObject;

	/**
	 * 可能是封装了一个安全的上下文用来调用反射到的方法的<br>
	 * The security context used for invoking the property methods
	 */
	private AccessControlContext acc;

	/**
	 * Cached introspections results for this object, to prevent encountering
	 * the cost of JavaBeans introspection every time.
	 */
	private CachedIntrospectionResults cachedIntrospectionResults;

	/**
	 * 存放嵌套路径后的子bean包装类的缓存<p>
	 * Map with cached nested BeanWrappers: nested path -> BeanWrapper instance.
	 */
	private Map<String, BeanWrapperImpl> nestedBeanWrappers;

	/**
	 * 是否自动扩展嵌套路径
	 */
	private boolean autoGrowNestedPaths = false;

	private int autoGrowCollectionLimit = Integer.MAX_VALUE;


	/**
	 * Create new empty BeanWrapperImpl. Wrapped instance needs to be set afterwards.
	 * Registers default editors.
	 * @see #setWrappedInstance
	 */
	public BeanWrapperImpl() {
		this(true);
	}

	/**
	 * Create new empty BeanWrapperImpl. Wrapped instance needs to be set afterwards.
	 * @param registerDefaultEditors whether to register default editors
	 * (can be suppressed if the BeanWrapper won't need any type conversion)
	 * @see #setWrappedInstance
	 */
	public BeanWrapperImpl(boolean registerDefaultEditors) {
		if (registerDefaultEditors) {
			registerDefaultEditors();
		}
		this.typeConverterDelegate = new TypeConverterDelegate(this);
	}

	/**
	 * 初始化被包装的对象其他为默认属性<p>
	 * Create new BeanWrapperImpl for the given object.
	 * @param object object wrapped by this BeanWrapper
	 */
	public BeanWrapperImpl(Object object) {
		registerDefaultEditors();
		setWrappedInstance(object);
	}

	/**
	 * Create new BeanWrapperImpl, wrapping a new instance of the specified class.
	 * @param clazz class to instantiate and wrap
	 */
	public BeanWrapperImpl(Class<?> clazz) {
		registerDefaultEditors();
		setWrappedInstance(BeanUtils.instantiateClass(clazz));
	}

	/**
	 * Create new BeanWrapperImpl for the given object,
	 * registering a nested path that the object is in.
	 * @param object object wrapped by this BeanWrapper
	 * @param nestedPath the nested path of the object
	 * @param rootObject the root object at the top of the path
	 */
	public BeanWrapperImpl(Object object, String nestedPath, Object rootObject) {
		registerDefaultEditors();
		setWrappedInstance(object, nestedPath, rootObject);
	}

	/**
	 * Create new BeanWrapperImpl for the given object,
	 * registering a nested path that the object is in.
	 * @param object object wrapped by this BeanWrapper
	 * @param nestedPath the nested path of the object
	 * @param superBw the containing BeanWrapper (must not be {@code null})
	 */
	private BeanWrapperImpl(Object object, String nestedPath, BeanWrapperImpl superBw) {
		setWrappedInstance(object, nestedPath, superBw.getWrappedInstance());
		setExtractOldValueForEditor(superBw.isExtractOldValueForEditor());
		setAutoGrowNestedPaths(superBw.isAutoGrowNestedPaths());
		setAutoGrowCollectionLimit(superBw.getAutoGrowCollectionLimit());
		setConversionService(superBw.getConversionService());
		setSecurityContext(superBw.acc);
	}


	//---------------------------------------------------------------------
	// Implementation of BeanWrapper interface
	//---------------------------------------------------------------------

	/**
	 * 初始化对象属性,根据传入参数设置被包装bean对象,嵌套路径(默认设""),bean根对象(默认为bean),类型转换委托类(新建)<p>
	 * Switch the target object, replacing the cached introspection results only
	 * if the class of the new object is different to that of the replaced object.
	 * @param object the new target object
	 */
	public void setWrappedInstance(Object object) {
		setWrappedInstance(object, "", null);
	}

	/**
	 * 初始化对象属性,根据传入参数设置被包装bean对象,嵌套路径(为空默认设""),bean根对象(嵌套路径为空则默认为bean),类型转换委托类(新建)<br>
	 * 并设置内省缓存结果集为null如果结果集的beanClass与传入要包装的Class相同<p>
	 * Switch the target object, replacing the cached introspection results only
	 * if the class of the new object is different to that of the replaced object.
	 * @param object 被包装的对象   the new target object
	 * @param nestedPath the nested path of the object
	 * @param rootObject the root object at the top of the path
	 */
	public void setWrappedInstance(Object object, String nestedPath, Object rootObject) {
		Assert.notNull(object, "Bean object must not be null");
		this.object = object;
		this.nestedPath = (nestedPath != null ? nestedPath : "");
		this.rootObject = (!"".equals(this.nestedPath) ? rootObject : object);//当嵌套路径为空时默认根对象为包装对象
		this.nestedBeanWrappers = null;
		this.typeConverterDelegate = new TypeConverterDelegate(this, object);//创建类型转换委派类
		setIntrospectionClass(object.getClass());
	}

	@Override
	public final Object getWrappedInstance() {
		return this.object;
	}

	/**
	 * 获取当前类的Class对象
	 */
	@Override
	public final Class<?> getWrappedClass() {
		return (this.object != null ? this.object.getClass() : null);
	}

	/**
	 * Return the nested path of the object wrapped by this BeanWrapper.
	 */
	public final String getNestedPath() {
		return this.nestedPath;
	}

	/**
	 * Return the root object at the top of the path of this BeanWrapper.
	 * @see #getNestedPath
	 */
	public final Object getRootInstance() {
		return this.rootObject;
	}

	/**
	 * Return the class of the root object at the top of the path of this BeanWrapper.
	 * @see #getNestedPath
	 */
	public final Class<?> getRootClass() {
		return (this.rootObject != null ? this.rootObject.getClass() : null);
	}

	/**
	 * Set whether this BeanWrapper should attempt to "auto-grow" a nested path that contains a null value.
	 * <p>If "true", a null path location will be populated with a default object value and traversed
	 * instead of resulting in a {@link NullValueInNestedPathException}. Turning this flag on also
	 * enables auto-growth of collection elements when accessing an out-of-bounds index.
	 * <p>Default is "false" on a plain BeanWrapper.
	 */
	@Override
	public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
		this.autoGrowNestedPaths = autoGrowNestedPaths;
	}

	/**
	 * Return whether "auto-growing" of nested paths has been activated.
	 */
	@Override
	public boolean isAutoGrowNestedPaths() {
		return this.autoGrowNestedPaths;
	}

	/**
	 * Specify a limit for array and collection auto-growing.
	 * <p>Default is unlimited on a plain BeanWrapper.
	 */
	@Override
	public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
		this.autoGrowCollectionLimit = autoGrowCollectionLimit;
	}

	/**
	 * Return the limit for array and collection auto-growing.
	 */
	@Override
	public int getAutoGrowCollectionLimit() {
		return this.autoGrowCollectionLimit;
	}

	/**
	 * Set the security context used during the invocation of the wrapped instance methods.
	 * Can be null.
	 */
	public void setSecurityContext(AccessControlContext acc) {
		this.acc = acc;
	}

	/**
	 * Return the security context used during the invocation of the wrapped instance methods.
	 * Can be null.
	 */
	public AccessControlContext getSecurityContext() {
		return this.acc;
	}

	/**
	 * 设置内省Class(如果传入的Class与内省结果集缓存的bean的Class相同则设置内省缓存结果集为null否则不做其他处理)<p>
	 * Set the class to introspect.
	 * Needs to be called when the target object changes.
	 * @param clazz the class to introspect
	 */
	protected void setIntrospectionClass(Class<?> clazz) {
		if (this.cachedIntrospectionResults != null &&
				!clazz.equals(this.cachedIntrospectionResults.getBeanClass())) {
			this.cachedIntrospectionResults = null;
		}
	}

	/**
	 * 获取CachedIntrospectionResults属性对象,为空则传入当前对象的Class创建<p>
	 * Obtain a lazily initializted CachedIntrospectionResults instance
	 * for the wrapped object.
	 */
	private CachedIntrospectionResults getCachedIntrospectionResults() {
		Assert.state(this.object != null, "BeanWrapper does not hold a bean instance");
		if (this.cachedIntrospectionResults == null) {
			this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(getWrappedClass());
		}
		return this.cachedIntrospectionResults;
	}


	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		return getCachedIntrospectionResults().getPropertyDescriptors();
	}

	@Override
	public PropertyDescriptor getPropertyDescriptor(String propertyName) throws BeansException {
		PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
		if (pd == null) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"No property '" + propertyName + "' found");
		}
		return pd;
	}

	/**
	 * Internal version of {@link #getPropertyDescriptor}:
	 * Returns {@code null} if not found rather than throwing an exception.
	 * @param propertyName the property to obtain the descriptor for
	 * @return the property descriptor for the specified property,
	 * or {@code null} if not found
	 * @throws BeansException in case of introspection failure
	 */
	protected PropertyDescriptor getPropertyDescriptorInternal(String propertyName) throws BeansException {
		Assert.notNull(propertyName, "Property name must not be null");
		BeanWrapperImpl nestedBw = getBeanWrapperForPropertyPath(propertyName);
		return nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(getFinalPath(nestedBw, propertyName));
	}

	@Override
	public Class<?> getPropertyType(String propertyName) throws BeansException {
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				return pd.getPropertyType();
			}
			else {
				// Maybe an indexed/mapped property...
				Object value = getPropertyValue(propertyName);
				if (value != null) {
					return value.getClass();
				}
				// Check to see if there is a custom editor,
				// which might give an indication on the desired target type.
				Class<?> editorType = guessPropertyTypeFromEditors(propertyName);
				if (editorType != null) {
					return editorType;
				}
			}
		}
		catch (InvalidPropertyException ex) {
			// Consider as not determinable.
		}
		return null;
	}

	@Override
	public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException {
		try {
			BeanWrapperImpl nestedBw = getBeanWrapperForPropertyPath(propertyName);
			String finalPath = getFinalPath(nestedBw, propertyName);
			PropertyTokenHolder tokens = getPropertyNameTokens(finalPath);
			PropertyDescriptor pd = nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(tokens.actualName);
			if (pd != null) {
				if (tokens.keys != null) {
					if (pd.getReadMethod() != null || pd.getWriteMethod() != null) {
						return TypeDescriptor.nested(property(pd), tokens.keys.length);
					}
				}
				else {
					if (pd.getReadMethod() != null || pd.getWriteMethod() != null) {
						return new TypeDescriptor(property(pd));
					}
				}
			}
		}
		catch (InvalidPropertyException ex) {
			// Consider as not determinable.
		}
		return null;
	}

	@Override
	public boolean isReadableProperty(String propertyName) {
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				if (pd.getReadMethod() != null) {
					return true;
				}
			}
			else {
				// Maybe an indexed/mapped property...
				getPropertyValue(propertyName);
				return true;
			}
		}
		catch (InvalidPropertyException ex) {
			// Cannot be evaluated, so can't be readable.
		}
		return false;
	}

	@Override
	public boolean isWritableProperty(String propertyName) {
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				if (pd.getWriteMethod() != null) {
					return true;
				}
			}
			else {
				// Maybe an indexed/mapped property...
				getPropertyValue(propertyName);
				return true;
			}
		}
		catch (InvalidPropertyException ex) {
			// Cannot be evaluated, so can't be writable.
		}
		return false;
	}

	private Object convertIfNecessary(String propertyName, Object oldValue, Object newValue, Class<?> requiredType,
			TypeDescriptor td) throws TypeMismatchException {
		try {
			return this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue, requiredType, td);
		}
		catch (ConverterNotFoundException ex) {
			PropertyChangeEvent pce =
					new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
			throw new ConversionNotSupportedException(pce, td.getType(), ex);
		}
		catch (ConversionException ex) {
			PropertyChangeEvent pce =
					new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
			throw new TypeMismatchException(pce, requiredType, ex);
		}
		catch (IllegalStateException ex) {
			PropertyChangeEvent pce =
					new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
			throw new ConversionNotSupportedException(pce, requiredType, ex);
		}
		catch (IllegalArgumentException ex) {
			PropertyChangeEvent pce =
					new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
			throw new TypeMismatchException(pce, requiredType, ex);
		}
	}

	/**
	 * Convert the given value for the specified property to the latter's type.
	 * <p>This method is only intended for optimizations in a BeanFactory.
	 * Use the {@code convertIfNecessary} methods for programmatic conversion.
	 * @param value the value to convert
	 * @param propertyName the target property
	 * (note that nested or indexed properties are not supported here)
	 * @return the new value, possibly the result of type conversion
	 * @throws TypeMismatchException if type conversion failed
	 */
	public Object convertForProperty(Object value, String propertyName) throws TypeMismatchException {
		CachedIntrospectionResults cachedIntrospectionResults = getCachedIntrospectionResults();
		PropertyDescriptor pd = cachedIntrospectionResults.getPropertyDescriptor(propertyName);
		if (pd == null) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"No property '" + propertyName + "' found");
		}
		TypeDescriptor td = cachedIntrospectionResults.getTypeDescriptor(pd);
		if (td == null) {
			td = new TypeDescriptor(property(pd));
			cachedIntrospectionResults.addTypeDescriptor(pd, td);
		}
		return convertForProperty(propertyName, null, value, td);
	}

	private Object convertForProperty(String propertyName, Object oldValue, Object newValue, TypeDescriptor td)
			throws TypeMismatchException {

		return convertIfNecessary(propertyName, oldValue, newValue, td.getType(), td);
	}

	private Property property(PropertyDescriptor pd) {
		GenericTypeAwarePropertyDescriptor typeAware = (GenericTypeAwarePropertyDescriptor) pd;
		return new Property(typeAware.getBeanClass(), typeAware.getReadMethod(), typeAware.getWriteMethod(), typeAware.getName());
	}


	//---------------------------------------------------------------------
	// Implementation methods
	//---------------------------------------------------------------------

	/**
	 * 获取嵌套路径链的末尾属性名<br>
	 * (如果传入BeanWrapper为当前对象则直接返回传入的嵌套路径否则切割最后一个'.'之后的部分返回)<p>
	 * Get the last component of the path. Also works if not nested.
	 * @param bw BeanWrapper to work on
	 * @param nestedPath property path we know is nested
	 * @return last component of the path (the property on the target bean)
	 */
	private String getFinalPath(BeanWrapper bw, String nestedPath) {
		if (bw == this) {
			return nestedPath;
		}
		return nestedPath.substring(PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(nestedPath) + 1);
	}

	/**
	 * 创建指定属性路径下的所有嵌套属性bean包装对象(递归)<br>
	 * (通过子bean包装类缓存进行手尾链接起来)并返回最后一个子bean包装类<p>
	 * Recursively navigate to return a BeanWrapper for the nested property path.
	 * @param propertyPath property property path, which may be nested
	 * @return a BeanWrapper for the target bean
	 */
	protected BeanWrapperImpl getBeanWrapperForPropertyPath(String propertyPath) {
		int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
		// Handle nested properties recursively.
		if (pos > -1) {
			String nestedProperty = propertyPath.substring(0, pos);//切割分隔符前的
			String nestedPath = propertyPath.substring(pos + 1);//切割分隔符后的
			BeanWrapperImpl nestedBw = getNestedBeanWrapper(nestedProperty);
			return nestedBw.getBeanWrapperForPropertyPath(nestedPath);//递归获取嵌套子bean包装类
		}
		else {
			return this;
		}
	}

	/**
	 * 获取指定嵌套属性的嵌套bean包装对象<br>
	 * (先创建属性令牌类然后获取属性值对象不存在则创建默认属性值对象然后从缓存中获取嵌套属性子bean包装类不存在则创建子包装类并复制当前编辑器相关属性到子包装类)<p>
	 * Retrieve a BeanWrapper for the given nested property.
	 * Create a new one if not found in the cache.
	 * <p>Note: Caching nested BeanWrappers is necessary now,
	 * to keep registered custom editors for nested properties.
	 * @param nestedProperty property to create the BeanWrapper for
	 * @return the BeanWrapper instance, either cached or newly created
	 */
	private BeanWrapperImpl getNestedBeanWrapper(String nestedProperty) {
		if (this.nestedBeanWrappers == null) {
			this.nestedBeanWrappers = new HashMap<String, BeanWrapperImpl>();
		}
		// Get value of bean property.
		PropertyTokenHolder tokens = getPropertyNameTokens(nestedProperty);
		String canonicalName = tokens.canonicalName;
		Object propertyValue = getPropertyValue(tokens);
		if (propertyValue == null) {
			if (this.autoGrowNestedPaths) {
				propertyValue = setDefaultValue(tokens);
			}
			else {
				throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + canonicalName);
			}
		}

		// Lookup cached sub-BeanWrapper, create new one if not found.
		BeanWrapperImpl nestedBw = this.nestedBeanWrappers.get(canonicalName);//获取当前包装对象缓存的嵌套路径的子bean包装类
		if (nestedBw == null || nestedBw.getWrappedInstance() != propertyValue) {//如果不存在或所包装的bean不同
			if (logger.isTraceEnabled()) {
				logger.trace("Creating new nested BeanWrapper for property '" + canonicalName + "'");
			}
			nestedBw = newNestedBeanWrapper(propertyValue, this.nestedPath + canonicalName + NESTED_PROPERTY_SEPARATOR);
			// Inherit all type-specific PropertyEditors.
			copyDefaultEditorsTo(nestedBw);
			copyCustomEditorsTo(nestedBw, canonicalName);
			this.nestedBeanWrappers.put(canonicalName, nestedBw);
		}
		else {
			if (logger.isTraceEnabled()) {
				logger.trace("Using cached nested BeanWrapper for property '" + canonicalName + "'");
			}
		}
		return nestedBw;
	}

	/**
	 * 根据属性名创建默认属性令牌类再根据令牌类创建默认属性值对象最后设置属性值后返回
	 * @param propertyName
	 * @return
	 */
	private Object setDefaultValue(String propertyName) {
		PropertyTokenHolder tokens = new PropertyTokenHolder();
		tokens.actualName = propertyName;
		tokens.canonicalName = propertyName;
		return setDefaultValue(tokens);
	}

	/**
	 * 根据传入属性令牌类创建属性对象并封装为属性值对象再设置属性值后获取属性值
	 * @param tokens
	 * @return
	 */
	private Object setDefaultValue(PropertyTokenHolder tokens) {
		PropertyValue pv = createDefaultPropertyValue(tokens);
		setPropertyValue(tokens, pv);
		return getPropertyValue(tokens);
	}

	/**
	 * 根据传入属性令牌类的完整属性名获取属性类型再根据类型创建默认对象再将对象与属性名包装为属性值对象返回
	 * @param tokens
	 * @return
	 */
	private PropertyValue createDefaultPropertyValue(PropertyTokenHolder tokens) {
		Class<?> type = getPropertyTypeDescriptor(tokens.canonicalName).getType();
		if (type == null) {
			throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + tokens.canonicalName,
					"Could not determine property type for auto-growing a default value");
		}
		Object defaultValue = newValue(type, tokens.canonicalName);
		return new PropertyValue(tokens.canonicalName, defaultValue);
	}

	/**
	 * 根据传入Class对象创建该类型对象(支持数组嵌套数组集合Map)(如果为接口返回常用实现类,默认容量16)<p>
	 * @param type
	 * @param name 用于日志输出
	 * @return
	 */
	private Object newValue(Class<?> type, String name) {
		try {
			if (type.isArray()) {//如果属于数组
				Class<?> componentType = type.getComponentType();//得到数组里的数据类型
				// TODO - only handles 2-dimensional arrays
				if (componentType.isArray()) {//如果也为数组
					Object array = Array.newInstance(componentType, 1);
					//新创建一个的array数组内的元素类型对象设置为array第一个元素
					Array.set(array, 0, Array.newInstance(componentType.getComponentType(), 0));
					return array;
				}
				else {
					return Array.newInstance(componentType, 0);
				}
			}
			else if (Collection.class.isAssignableFrom(type)) {//如果属于集合类型
				return CollectionFactory.createCollection(type, 16);
			}
			else if (Map.class.isAssignableFrom(type)) {//如果属于Map类型
				return CollectionFactory.createMap(type, 16);
			}
			else {
				return type.newInstance();
			}
		}
		catch (Exception ex) {
			// TODO Root cause exception context is lost here... should we throw another exception type that preserves context instead?
			throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + name,
					"Could not instantiate property type [" + type.getName() + "] to auto-grow nested property path: " + ex);
		}
	}

	/**
	 * 创建一个新的BeanWrapperImpl对象返回<p>
	 * Create a new nested BeanWrapper instance.
	 * <p>Default implementation creates a BeanWrapperImpl instance.
	 * Can be overridden in subclasses to create a BeanWrapperImpl subclass.
	 * @param object object wrapped by this BeanWrapper
	 * @param nestedPath the nested path of the object
	 * @return the nested BeanWrapper instance
	 */
	protected BeanWrapperImpl newNestedBeanWrapper(Object object, String nestedPath) {
		return new BeanWrapperImpl(object, nestedPath, this);
	}

	/**
	 * 获取属性名令牌对象(包含属性名[]前的名和里面的各个内容及完整名)<p>
	 * Parse the given property name into the corresponding property name tokens.
	 * @param propertyName the property name to parse
	 * @return representation of the parsed property tokens
	 */
	private PropertyTokenHolder getPropertyNameTokens(String propertyName) {
		PropertyTokenHolder tokens = new PropertyTokenHolder();
		String actualName = null;
		List<String> keys = new ArrayList<String>(2);
		int searchIndex = 0;
		while (searchIndex != -1) {
			int keyStart = propertyName.indexOf(PROPERTY_KEY_PREFIX, searchIndex);//'['的下标
			searchIndex = -1;
			if (keyStart != -1) {
				int keyEnd = propertyName.indexOf(PROPERTY_KEY_SUFFIX, keyStart + PROPERTY_KEY_PREFIX.length());//']'的下标
				if (keyEnd != -1) {
					if (actualName == null) {
						actualName = propertyName.substring(0, keyStart);//'['前的名字
					}
					String key = propertyName.substring(keyStart + PROPERTY_KEY_PREFIX.length(), keyEnd);//'[]'内的字符名
					if ((key.startsWith("'") && key.endsWith("'")) || (key.startsWith("\"") && key.endsWith("\""))) {//去掉两边的引号
						key = key.substring(1, key.length() - 1);
					}
					keys.add(key);
					searchIndex = keyEnd + PROPERTY_KEY_SUFFIX.length();
				}
			}
		}
		tokens.actualName = (actualName != null ? actualName : propertyName);
		tokens.canonicalName = tokens.actualName;
		if (!keys.isEmpty()) {
			tokens.canonicalName +=
					PROPERTY_KEY_PREFIX +
					StringUtils.collectionToDelimitedString(keys, PROPERTY_KEY_SUFFIX + PROPERTY_KEY_PREFIX) +
					PROPERTY_KEY_SUFFIX;
			tokens.keys = StringUtils.toStringArray(keys);
		}
		return tokens;
	}


	//---------------------------------------------------------------------
	// Implementation of PropertyAccessor interface
	//---------------------------------------------------------------------

	@Override
	public Object getPropertyValue(String propertyName) throws BeansException {
		BeanWrapperImpl nestedBw = getBeanWrapperForPropertyPath(propertyName);
		PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedBw, propertyName));
		return nestedBw.getPropertyValue(tokens);
	}

	/**
	 * 根据传入令牌类的属性名获取缓存中的属性描述类再据此获取属性get方法并调用<br>
	 * 若属性为集合Map等类型则扩展大小为属性原大小(支持多维扩展)返回集合或Map最后一个元素<br>
	 * 普通属性直接返回
	 * @param tokens
	 * @return
	 * @throws BeansException
	 */
    @SuppressWarnings("unchecked")
	private Object getPropertyValue(PropertyTokenHolder tokens) throws BeansException {
		String propertyName = tokens.canonicalName;
		String actualName = tokens.actualName;
		PropertyDescriptor pd = getCachedIntrospectionResults().getPropertyDescriptor(actualName);//根据属性名称获取缓存中的属性描述类
		if (pd == null || pd.getReadMethod() == null) {
			throw new NotReadablePropertyException(getRootClass(), this.nestedPath + propertyName);
		}
		final Method readMethod = pd.getReadMethod();
		try {
			//如果属性的可读方法对象所在的实际类的访问修饰符不是公开的并该可读方法不可访问
			if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers()) && !readMethod.isAccessible()) {
				if (System.getSecurityManager() != null) {//如果启动了系统安全权限
					AccessController.doPrivileged(new PrivilegedAction<Object>() {//让系统在没有权限检查的情况下执行设置方法为可访问的
						@Override
						public Object run() {
							readMethod.setAccessible(true);
							return null;
						}
					});
				}
				else {
					readMethod.setAccessible(true);//否则直接设置为可访问状态
				}
			}

			Object value;
			if (System.getSecurityManager() != null) {//如果启动了权限管理则让系统在没有权限情况下调用属性的可读方法
				try {
					value = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
						@Override
						public Object run() throws Exception {
							return readMethod.invoke(object, (Object[]) null);
						}
					}, acc);
				}
				catch (PrivilegedActionException pae) {
					throw pae.getException();
				}
			}
			else {
				value = readMethod.invoke(object, (Object[]) null);
			}

			if (tokens.keys != null) {//存在嵌套路径
				if (value == null) {
					if (this.autoGrowNestedPaths) {//去掉嵌套路径后创建属性值
						value = setDefaultValue(tokens.actualName);
					}
					else {
						throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName,
								"Cannot access indexed value of property referenced in indexed " +
								"property path '" + propertyName + "': returned null");
					}
				}
				String indexedPropertyName = tokens.actualName;
				// apply indexes and map keys
				for (int i = 0; i < tokens.keys.length; i++) {//为集合Map等情况则扩展为属性的大小(包括转换)(支持嵌套路径扩展)
					String key = tokens.keys[i];
					if (value == null) {
						throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName,
								"Cannot access indexed value of property referenced in indexed " +
								"property path '" + propertyName + "': returned null");
					}
					else if (value.getClass().isArray()) {
						int index = Integer.parseInt(key);
						value = growArrayIfNecessary(value, index, indexedPropertyName);
						value = Array.get(value, index);
					}
					else if (value instanceof List) {
						int index = Integer.parseInt(key);
						List<Object> list = (List<Object>) value;
						growCollectionIfNecessary(list, index, indexedPropertyName, pd, i + 1);
						value = list.get(index);
					}
					else if (value instanceof Set) {
						// Apply index to Iterator in case of a Set.
						Set<Object> set = (Set<Object>) value;
						int index = Integer.parseInt(key);
						if (index < 0 || index >= set.size()) {
							throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
									"Cannot get element with index " + index + " from Set of size " +
									set.size() + ", accessed using property path '" + propertyName + "'");
						}
						Iterator<Object> it = set.iterator();
						for (int j = 0; it.hasNext(); j++) {
							Object elem = it.next();
							if (j == index) {
								value = elem;
								break;
							}
						}
					}
					else if (value instanceof Map) {
						Map<Object, Object> map = (Map<Object, Object>) value;
						Class<?> mapKeyType = GenericCollectionTypeResolver.getMapKeyReturnType(pd.getReadMethod(), i + 1);
						// IMPORTANT: Do not pass full property name in here - property editors
						// must not kick in for map keys but rather only for map values.
						TypeDescriptor typeDescriptor = (mapKeyType != null ?
								TypeDescriptor.valueOf(mapKeyType) : TypeDescriptor.valueOf(Object.class));
						Object convertedMapKey = convertIfNecessary(null, null, key, mapKeyType, typeDescriptor);
						value = map.get(convertedMapKey);
					}
					else {
						throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
								"Property referenced in indexed property path '" + propertyName +
								"' is neither an array nor a List nor a Set nor a Map; returned value was [" + value + "]");
					}
					indexedPropertyName += PROPERTY_KEY_PREFIX + key + PROPERTY_KEY_SUFFIX;
				}
			}
			return value;
		}
		catch (IndexOutOfBoundsException ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Index of out of bounds in property path '" + propertyName + "'", ex);
		}
		catch (NumberFormatException ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Invalid index in property path '" + propertyName + "'", ex);
		}
		catch (TypeMismatchException ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Invalid index in property path '" + propertyName + "'", ex);
		}
		catch (InvocationTargetException ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Getter for property '" + actualName + "' threw exception", ex);
		}
		catch (Exception ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Illegal attempt to get property '" + actualName + "' threw exception", ex);
		}
	}

    /**
     * 若当前对象为自动扩展嵌套路径并数组对象长度小于指定数(指定数值要小于自动扩展集合大小的最大限制)<br>
     * 创建一个新数组对象复制旧数组对象并补全到指定数值大小最后设置属性(包含转换)后再获取属性值对象
     * @param array
     * @param index
     * @param name
     * @return
     */
	private Object growArrayIfNecessary(Object array, int index, String name) {
		if (!this.autoGrowNestedPaths) {
			return array;
		}
		int length = Array.getLength(array);
		if (index >= length && index < this.autoGrowCollectionLimit) {
			Class<?> componentType = array.getClass().getComponentType();//获取数组类元素类型
			Object newArray = Array.newInstance(componentType, index + 1);
			System.arraycopy(array, 0, newArray, 0, length);
			for (int i = length; i < Array.getLength(newArray); i++) {
				Array.set(newArray, i, newValue(componentType, name));
			}
			// TODO this is not efficient because conversion may create a copy ... set directly because we know it is assignable.
			setPropertyValue(name, newArray);
			return getPropertyValue(name);
		}
		else {
			return array;
		}
	}

	private void growCollectionIfNecessary(Collection<Object> collection, int index, String name,
			PropertyDescriptor pd, int nestingLevel) {

		if (!this.autoGrowNestedPaths) {
			return;
		}
		int size = collection.size();
		if (index >= size && index < this.autoGrowCollectionLimit) {
			Class<?> elementType = GenericCollectionTypeResolver.getCollectionReturnType(pd.getReadMethod(), nestingLevel);
			if (elementType != null) {
				for (int i = collection.size(); i < index + 1; i++) {
					collection.add(newValue(elementType, name));
				}
			}
		}
	}

	/**
	 * 实现抽象方法,根据属性名设置属性value为传入的value对象<br>
	 * 如果存在嵌套路径则一层层剖析到最后的子嵌套属性并将传入value对象设置到该属性值
	 */
	@Override
	public void setPropertyValue(String propertyName, Object value) throws BeansException {
		BeanWrapperImpl nestedBw;
		try {
			nestedBw = getBeanWrapperForPropertyPath(propertyName);//返回最后一个子BeanWrapperImpl
		}
		catch (NotReadablePropertyException ex) {
			throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName,
					"Nested property in path '" + propertyName + "' does not exist", ex);
		}
		//将除传入的属性名切为最后一个嵌套的子属性并包装为令牌类
		PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedBw, propertyName));
		nestedBw.setPropertyValue(tokens, new PropertyValue(propertyName, value));//将传入的属性名及值设置到子BeanWrapperImpl中
	}

	@Override
	public void setPropertyValue(PropertyValue pv) throws BeansException {
		PropertyTokenHolder tokens = (PropertyTokenHolder) pv.resolvedTokens;
		if (tokens == null) {
			String propertyName = pv.getName();
			BeanWrapperImpl nestedBw;
			try {
				nestedBw = getBeanWrapperForPropertyPath(propertyName);
			}
			catch (NotReadablePropertyException ex) {
				throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName,
						"Nested property in path '" + propertyName + "' does not exist", ex);
			}
			tokens = getPropertyNameTokens(getFinalPath(nestedBw, propertyName));
			if (nestedBw == this) {
				pv.getOriginalPropertyValue().resolvedTokens = tokens;
			}
			nestedBw.setPropertyValue(tokens, pv);
		}
		else {
			setPropertyValue(tokens, pv);
		}
	}

	/**
	 * 根据指定的属性令牌类获取属性值对象(不存在则创建一个默认的)并设置为指定的属性值对象<br>
	 * (先将值按旧值转换,如果属于集合Map等则按照内部元素类型转换然后设置其最后一个元素为转换后的值;<br>
	 * 如果为普通对象则先获取get方法执行然后准备设置的属性值对象根据得到的旧值转换后再获取set方法将转换后的值设置进去)
	 * @param tokens 被设置的属性值令牌类
	 * @param pv 用来设置的属性值对象
	 * @throws BeansException
	 */
	@SuppressWarnings("unchecked")
	private void setPropertyValue(PropertyTokenHolder tokens, PropertyValue pv) throws BeansException {
		String propertyName = tokens.canonicalName;
		String actualName = tokens.actualName;

		if (tokens.keys != null) {//如果'[]'里面有内容
			// Apply indexes and map keys: fetch value for all keys but the last one.
			PropertyTokenHolder getterTokens = new PropertyTokenHolder();//将传入属性令牌类各个属性复制进到一个新属性令牌类
			getterTokens.canonicalName = tokens.canonicalName;
			getterTokens.actualName = tokens.actualName;
			getterTokens.keys = new String[tokens.keys.length - 1];
			System.arraycopy(tokens.keys, 0, getterTokens.keys, 0, tokens.keys.length - 1);
			Object propValue;
			try {
				propValue = getPropertyValue(getterTokens);
			}
			catch (NotReadablePropertyException ex) {
				throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName,
						"Cannot access indexed value in property referenced " +
						"in indexed property path '" + propertyName + "'", ex);
			}
			// Set value for last key.
			String key = tokens.keys[tokens.keys.length - 1];//最后一个'[]'里的内容
			if (propValue == null) {//如果属性value为空若可自动扩展嵌套路径去掉最后嵌套部分后再创建默认属性值对象后返回属性value内容
				// null map value case
				if (this.autoGrowNestedPaths) {
					// TODO: cleanup, this is pretty hacky
					int lastKeyIndex = tokens.canonicalName.lastIndexOf('[');//从属性名最后数'['的下标位置
					getterTokens.canonicalName = tokens.canonicalName.substring(0, lastKeyIndex);//去掉最后的嵌套内容
					propValue = setDefaultValue(getterTokens);
				}
				else {
					throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName,
							"Cannot access indexed value in property referenced " +
							"in indexed property path '" + propertyName + "': returned null");
				}
			}
			if (propValue.getClass().isArray()) {//如果属性value属于数组对象
				PropertyDescriptor pd = getCachedIntrospectionResults().getPropertyDescriptor(actualName);//获取属性描述类对象
				Class<?> requiredType = propValue.getClass().getComponentType();//获取属性value数组里的元素类型
				int arrayIndex = Integer.parseInt(key);//将嵌套路径的最后一个'[]'里的内容转为数值
				Object oldValue = null;
				try {
					if (isExtractOldValueForEditor() && arrayIndex < Array.getLength(propValue)) {//如果要提取旧值并提取的到
						oldValue = Array.get(propValue, arrayIndex);//提取数组的最后一个值
					}
					Object convertedValue = convertIfNecessary(propertyName, oldValue, pv.getValue(),
							requiredType, TypeDescriptor.nested(property(pd), tokens.keys.length));
					Array.set(propValue, arrayIndex, convertedValue);//设置最后一个值为转换后的值
				}
				catch (IndexOutOfBoundsException ex) {
					throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
							"Invalid array index in property path '" + propertyName + "'", ex);
				}
			}
			else if (propValue instanceof List) {//如果属性value属于集合
				PropertyDescriptor pd = getCachedIntrospectionResults().getPropertyDescriptor(actualName);//获取属性描述类对象
				Class<?> requiredType = GenericCollectionTypeResolver.getCollectionReturnType(
						pd.getReadMethod(), tokens.keys.length);//获取泛型类型
				List<Object> list = (List<Object>) propValue;
				int index = Integer.parseInt(key);//将嵌套路径的最后一个'[]'里的内容转为数值
				Object oldValue = null;
				if (isExtractOldValueForEditor() && index < list.size()) {//如果要提取旧值并提取的到
					oldValue = list.get(index);//提取集合的最后一个值
				}
				Object convertedValue = convertIfNecessary(propertyName, oldValue, pv.getValue(),
						requiredType, TypeDescriptor.nested(property(pd), tokens.keys.length));
				int size = list.size();
				//如果集合size小于属性'[]'里的数值,进行添加null补全最后一个添加转换后的value
				if (index >= size && index < this.autoGrowCollectionLimit) {
					for (int i = size; i < index; i++) {
						try {
							list.add(null);
						}
						catch (NullPointerException ex) {
							throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
									"Cannot set element with index " + index + " in List of size " +
									size + ", accessed using property path '" + propertyName +
									"': List does not support filling up gaps with null elements");
						}
					}
					list.add(convertedValue);
				}
				else {//如果集合size大于等于属性'[]'里的数值设置该索引的值为转换后的value
					try {
						list.set(index, convertedValue);
					}
					catch (IndexOutOfBoundsException ex) {
						throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
								"Invalid list index in property path '" + propertyName + "'", ex);
					}
				}
			}
			else if (propValue instanceof Map) {//如果属性value属于Map
				PropertyDescriptor pd = getCachedIntrospectionResults().getPropertyDescriptor(actualName);
				Class<?> mapKeyType = GenericCollectionTypeResolver.getMapKeyReturnType(
						pd.getReadMethod(), tokens.keys.length);//获取key泛型类型
				Class<?> mapValueType = GenericCollectionTypeResolver.getMapValueReturnType(
						pd.getReadMethod(), tokens.keys.length);//获取value泛型类型
				Map<Object, Object> map = (Map<Object, Object>) propValue;
				// IMPORTANT: Do not pass full property name in here - property editors
				// must not kick in for map keys but rather only for map values.
				TypeDescriptor typeDescriptor = (mapKeyType != null ?
						TypeDescriptor.valueOf(mapKeyType) : TypeDescriptor.valueOf(Object.class));
				Object convertedMapKey = convertIfNecessary(null, null, key, mapKeyType, typeDescriptor);//转换Map的key
				Object oldValue = null;
				if (isExtractOldValueForEditor()) {//如果要提取旧值
					oldValue = map.get(convertedMapKey);
				}
				// Pass full property name and old value in here, since we want full
				// conversion ability for map values.
				Object convertedMapValue = convertIfNecessary(propertyName, oldValue, pv.getValue(),
						mapValueType, TypeDescriptor.nested(property(pd), tokens.keys.length));//转换Map的value
				map.put(convertedMapKey, convertedMapValue);//将转换后的key和value存进Map
			}
			else {
				throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
						"Property referenced in indexed property path '" + propertyName +
						"' is neither an array nor a List nor a Map; returned value was [" + pv.getValue() + "]");
			}
		}

		else {//如果属性名没有'[]'
			PropertyDescriptor pd = pv.resolvedDescriptor;
			//若属性值对象的属性描述类为空或当前对象的object属性类型不等于或不为该对象所在的类的类型的子类
			if (pd == null || !pd.getWriteMethod().getDeclaringClass().isInstance(this.object)) {
				pd = getCachedIntrospectionResults().getPropertyDescriptor(actualName);//获取缓存中的属性的描述对象
				if (pd == null || pd.getWriteMethod() == null) {//属性描述类不存在或没有set方法
					if (pv.isOptional()) {//若属性为可选属性则返回否则抛异常
						logger.debug("Ignoring optional value for property '" + actualName +
								"' - property not found on bean class [" + getRootClass().getName() + "]");
						return;
					}
					else {
						PropertyMatches matches = PropertyMatches.forProperty(propertyName, getRootClass());
						throw new NotWritablePropertyException(
								getRootClass(), this.nestedPath + propertyName,
								matches.buildErrorMessage(), matches.getPossibleMatches());
					}
				}
				pv.getOriginalPropertyValue().resolvedDescriptor = pd;
			}

			Object oldValue = null;
			try {
				Object originalValue = pv.getValue();
				Object valueToApply = originalValue;
				if (!Boolean.FALSE.equals(pv.conversionNecessary)) {
					if (pv.isConverted()) {//如果属性值对象有转换的值
						valueToApply = pv.getConvertedValue();//获取转换的值
					}
					else {
						if (isExtractOldValueForEditor() && pd.getReadMethod() != null) {//如果属性编辑时要提取旧值并存在get方法
							final Method readMethod = pd.getReadMethod();//获取get方法
							//如果属性的可读方法对象所在的实际类的访问修饰符不是公开的并该可读方法不可访问
							if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers()) &&
									!readMethod.isAccessible()) {
								if (System.getSecurityManager()!= null) {//如果启用了系统安全权限
									AccessController.doPrivileged(new PrivilegedAction<Object>() {//在没有权限检查下设置访问权限为公开
										@Override
										public Object run() {
											readMethod.setAccessible(true);
											return null;
										}
									});
								}
								else {
									readMethod.setAccessible(true);//没有系统安全检查则直接设访问权限为公开
								}
							}
							try {//绕过系统权限(如果有)调用get方法
								if (System.getSecurityManager() != null) {
									oldValue = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
										@Override
										public Object run() throws Exception {
											return readMethod.invoke(object);
										}
									}, acc);
								}
								else {
									oldValue = readMethod.invoke(object);
								}
							}
							catch (Exception ex) {
								if (ex instanceof PrivilegedActionException) {
									ex = ((PrivilegedActionException) ex).getException();
								}
								if (logger.isDebugEnabled()) {
									logger.debug("Could not read previous value of property '" +
											this.nestedPath + propertyName + "'", ex);
								}
							}
						}
						valueToApply = convertForProperty(//转换
								propertyName, oldValue, originalValue, new TypeDescriptor(property(pd)));
					}
					//若转换后的值与转换钱的值相同则设置该属性值对象是不需要转换的否则为true
					pv.getOriginalPropertyValue().conversionNecessary = (valueToApply != originalValue);
				}
				final Method writeMethod = (pd instanceof GenericTypeAwarePropertyDescriptor ?//从属性描述对象获取set方法
						((GenericTypeAwarePropertyDescriptor) pd).getWriteMethodForActualAccess() :
						pd.getWriteMethod());
				//绕过系统权限(如果有)将set方法设为公开
				if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers()) && !writeMethod.isAccessible()) {
					if (System.getSecurityManager()!= null) {
						AccessController.doPrivileged(new PrivilegedAction<Object>() {
							@Override
							public Object run() {
								writeMethod.setAccessible(true);
								return null;
							}
						});
					}
					else {
						writeMethod.setAccessible(true);
					}
				}
				final Object value = valueToApply;
				if (System.getSecurityManager() != null) {//绕过系统权限(如果有)调用set方法
					try {
						AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
							@Override
							public Object run() throws Exception {
								writeMethod.invoke(object, value);
								return null;
							}
						}, acc);
					}
					catch (PrivilegedActionException ex) {
						throw ex.getException();
					}
				}
				else {
					writeMethod.invoke(this.object, value);
				}
			}
			catch (TypeMismatchException ex) {
				throw ex;
			}
			catch (InvocationTargetException ex) {
				PropertyChangeEvent propertyChangeEvent =
						new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, pv.getValue());
				if (ex.getTargetException() instanceof ClassCastException) {
					throw new TypeMismatchException(propertyChangeEvent, pd.getPropertyType(), ex.getTargetException());
				}
				else {
					throw new MethodInvocationException(propertyChangeEvent, ex.getTargetException());
				}
			}
			catch (Exception ex) {
				PropertyChangeEvent pce =
						new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, pv.getValue());
				throw new MethodInvocationException(pce, ex);
			}
		}
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getName());
		if (this.object != null) {
			sb.append(": wrapping object [").append(ObjectUtils.identityToString(this.object)).append("]");
		}
		else {
			sb.append(": no wrapped object set");
		}
		return sb.toString();
	}


	//---------------------------------------------------------------------
	// Inner class for internal use
	//---------------------------------------------------------------------

	/**
	 * 属性令牌信息类,只存放了属性的标准名(完整)实际名('[]'前的名)和'[]'里的内容的数组(size一般是两个)三个属性
	 */
	private static class PropertyTokenHolder {

		/**标准名(完整)*/
		public String canonicalName;

		/**实际名('[]'前的名)*/
		public String actualName;

		/**'[]'里的内容的数组(size一般是两个,如actualName[2][2]二维数组)*/
		public String[] keys;
	}

}
