package swallow.framework.service;



import java.util.List;
import java.util.function.Function;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;

import swallow.framework.exception.SwallowException;
import swallow.framework.jpaentity.IOnlyIdEntity;
import swallow.framework.jpaquery.repository.SwallowRepository;
import swallow.framework.web.BasePageQueryBean;
import swallow.framework.web.BaseQueryBean;
import swallow.framework.web.PageListData;

/**
 * 基础业务服务
 * @author aohanhe
 *
 * @param <T> SwallBaseRepository 的实现类
 * @param <K> 主键的实体类型 
 * @param <I> 主键的数据类型
 */
@SuppressWarnings("rawtypes")
public class BaseService <T extends SwallowRepository,K extends IOnlyIdEntity,I>{
	
	private static final Logger log = LoggerFactory.getLogger(BaseService.class);

	@Autowired
	private T repsitory;
	
	/**
	 * 通过ID取得实体对象
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public K getItemById(I id) {
		return (K) repsitory.getItemById(id);
	}
	
	/**
	 * 通过Id 删除实体对象
	 * @param id
	 */
	@SuppressWarnings("unchecked")
	public void deleteItemById(I id) {
		repsitory.deleteItemById(id);
		log.debug(String.format("Id=%d的实体对象被删除了", id));
	}	
	
	/**
	 * 列新实体对象
	 * @param enity
	 * @return
	 * @throws SwallowException
	 */
	@SuppressWarnings("unchecked")
	public K updateItem(K enity) throws SwallowException {
		var res= (K) repsitory.updateItem(enity);
		Assert.notNull(res,"更新实体对象失败，没有返回插入的结果");
		log.debug(String.format("id=%d实体对象被更新了",enity.getId()));
		return res;
	}
	
	/**
	 * 保存一个新的实体对象
	 * @param entity
	 * @return
	 * @throws SwallowException
	 */
	@SuppressWarnings("unchecked")
	public K insertItem(K entity)throws SwallowException{
		var res= (K) repsitory.insertItem(entity);		
		Assert.notNull(res,"插入实体对象失败，没有返回插入的结果");
		log.debug(String.format("id=%d实体对象被插入了",entity.getId()));
		return res;
	}
	
	/**
	 * 
	 * @param initQuery
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<K> getAllItems(Function<JPAQuery<Tuple>, JPAQuery<Tuple>> initQuery) {
		return repsitory.getAllItems(initQuery);
	}
	
	/**
	 * 取得满足条件的数据项
	 * @param initQuery
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public K getItem(Function<JPAQuery<Tuple>, JPAQuery<Tuple>> initQuery) {
		return (K) repsitory.getItem(initQuery);
	}
	
	/**
	 * 分页取得实体对象
	 * @param initQuery
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageListData<K> getAllItemsByPage(Function<JPAQuery<Tuple>, JPAQuery<Tuple>> initQuery, int page,
			int pageSize)
	{
		return repsitory.getAllItemsByPage(initQuery, page, pageSize);
	}
	
	/**
	 * 通过querybean返回所有数据
	 * @param queryBean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<K> getAllItemByQuerybean(BaseQueryBean queryBean){
		return repsitory.getAllItemByQuerybean(queryBean);
	}
	
	/**
	 * 通过queryben返回所有数据
	 * @param queryBean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageListData<T> getAllItemPageByQuerybean(BasePageQueryBean queryBean){
		return repsitory.getAllItemPageByQuerybean(queryBean);
	}

	public T getRepsitory() {
		return repsitory;
	}
}
