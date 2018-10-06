package swallow.framework.jpaquery.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义字段联结到指定表实体的指定字段
 * @author aohanhe
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinEntity {
	/**
	 * 要联结实体的字段名
	 * @return
	 */
	String name();
	/**
	 * 要联结实体的表ID
	 * @return
	 */
	int tableId();
}
