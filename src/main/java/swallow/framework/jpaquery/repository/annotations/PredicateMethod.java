package swallow.framework.jpaquery.repository.annotations;

/**
 * 表达式函数,指定querybean在生成条件时候，使用bean本的函数的返回表达式
 * @author aohanhe
 *
 */
public @interface PredicateMethod {
	/**
	 * 生成表达式时取得对应使用的函数名
	 * @return
	 */
	String value();
	
}
