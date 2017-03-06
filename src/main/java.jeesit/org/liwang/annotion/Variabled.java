package org.liwang.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.MapKey;


/**
 * 允许变量化
 * @author liwang
 *
 */
@Retention(RetentionPolicy.RUNTIME)//允许在虚拟机运行时被读取
@Target({ElementType.ANNOTATION_TYPE,ElementType.FIELD,ElementType.TYPE})//允许标注的地方
@Inherited//允许子类继承父类中的注解
public @interface Variabled {

	/**别称*/
	 String value() default "";
	 
	 /**字典名*/
	 String dict() default "";
}
