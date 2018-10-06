package swallow.framework.jpaquery.repository;

import com.querydsl.core.types.EntityPath;


/**
 * 表路径表达式管理器接口
 * @author aohanhe
 *
 */
public interface ITablesPathMangerAware {
	/**
	 * 通过表Id取得对应的路径表达式
	 * @param tableId
	 * @return
	 */
	EntityPath<?> getTablePathById(int tableId);
}
