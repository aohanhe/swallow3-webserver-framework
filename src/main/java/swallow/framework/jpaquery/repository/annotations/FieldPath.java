package swallow.framework.jpaquery.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定字段的表达式路径
 * @author aohanhe
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldPath {
	/**
	 * 对应的实体字段名
	 * @return
	 */
	String name() default "";
	/**
	 * 对应的表ID值
	 * @return
	 */
	int tableId() default -1;
}
