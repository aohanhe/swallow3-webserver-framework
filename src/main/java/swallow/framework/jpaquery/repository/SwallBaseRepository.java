package swallow.framework.jpaquery.repository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import swallow.framework.exception.SwallowException;
import swallow.framework.jpaentity.IOnlyIdEntity;
import swallow.framework.jpaquery.repository.annotations.FieldPath;
import swallow.framework.jpaquery.repository.annotations.JoinEntity;
import swallow.framework.web.PageListData;

/**
 * 基础数据仓库类
 * 
 * @author aohanhe
 *
 * @param <T> 实体对象类型
 * @param <K> 实体对象的ID的类型
 */
public abstract class SwallBaseRepository<T extends IOnlyIdEntity, K> {

	private static final Logger logger = LoggerFactory.getLogger(SwallBaseRepository.class);

	@Autowired
	protected EntityManager em;

	private Class<T> entityClassInfo;

	private Class<K> idClassInfo;

	private EntityPath<T> mainTable;
	/**
	 * 表格路径管理器
	 */
	@Autowired
	protected ITablesPathMangerAware tablesPathManger;

	/**
	 * querydsl构建工具
	 */

	protected JPAQueryFactory factory;

	/**
	 * 实体查询的主体查询部分，包括了select 以及 from
	 */
	private static JPAQuery<Tuple> query;

	/**
	 * 提取数据使用的映射列表
	 */
	private static List<Tuple2<Field, Path<?>>> fecthDataList;

	/**
	 * id字段的路径
	 */
	private Path<K> idPath;

	/**
	 * 静态ID的path,以提高运行的效率
	 */
	private static Path<?> _idPath;

	/**
	 * 初始化基础变量
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	protected void init() {
		Assert.notNull(em, "EntityManager没有实例化");

		factory = new JPAQueryFactory(em);

		// 检查tablesPathManger已经实例化
		Assert.notNull(tablesPathManger, "请实现ITablesPathMangerAware实例化对象");

		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		var actualType = type.getActualTypeArguments();

		Assert.isTrue(actualType != null && actualType.length > 0, "没有取得实例化参数的class信息");

		entityClassInfo = (Class<T>) actualType[0];
		idClassInfo = (Class<K>) actualType[1];

		// 取得主表表达式
		mainTable = (EntityPath<T>) this.getMainTableExpression();
		Assert.notNull(mainTable, "getMainTableExpression返回的主表表达式为空");

		// 初始化查询
		if (query == null)
			initQueryByEnityClassInfo();

		idPath = (Path<K>) _idPath;
	}
	
	protected JPAQuery<Tuple> getQuery() {
		return this.query.clone();
	}

	/**
	 * 取得当前的主表表达式
	 * 
	 * @return
	 */
	protected abstract EntityPath<?> getMainTableExpression();

	/**
	 * 通过ID取得实体对象
	 * 
	 * @param id
	 * @return
	 */
	public T getItemById(K id) {
		var idValue = Expressions.constant(id);
		var pre = Expressions.predicate(Ops.EQ, this.idPath, idValue);
		var result = this.getQuery().where(pre).fetchOne();
		

		if (result == null)
			return null;
		return this.fectItemFromTuple(result);
	}

	/**
	 * 通过ID删除对应的记录
	 * 
	 * @param id
	 */
	@Transactional
	public long deleteItemById(K id) {
		var idValue = Expressions.constant(id);
		var pre = Expressions.predicate(Ops.EQ, this.idPath, idValue);

		return factory.delete(mainTable).where(pre).execute();
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public T updateItem(T entity) throws SwallowException {
		try {
			
			em.merge(entity);			

			// 重新取得记录，以更新对应的值
			return this.getItemById((K)(Long)entity.getId());

		} catch (Exception ex) {
			throw new SwallowException("更新实体对象时出错：" + ex.getMessage(), ex);
		}
	}

	@Transactional
	public T insertItem(T entity) throws SwallowException {
		try {
			em.persist(entity);
			K id = (K) (Long) entity.getId();
			// 重新取得记录，以更新对应的值
			return this.getItemById(id);
		} catch (Exception ex) {
			throw new SwallowException("插入实体对象时出错：" + ex.getMessage(), ex);
		}
	}

	/**
	 * 取得所有实体对象
	 * 
	 * @param initQuery 通过回调可以定制查询条件，或排序
	 * @return
	 */
	public List<T> getAllItems(Function<JPAQuery<Tuple>, JPAQuery<Tuple>> initQuery) {
		var fetchQuery = this.getQuery();
		if (initQuery != null)
			fetchQuery = initQuery.apply(this.getQuery());

		var list = fetchQuery.fetch();
		if (list == null)
			return null;

		return list.stream().map(this::fectItemFromTuple).collect(Collectors.toList());
	}

	/**
	 * 分页返回数据
	 * 
	 * @param initQuery 回调，可以修改条件以及排序
	 * @param page      页号
	 * @param pageSize  页面大小
	 * @return 分页结果集
	 */
	public PageListData<T> getAllItemsByPage(Function<JPAQuery<Tuple>, JPAQuery<Tuple>> initQuery, int page,
			int pageSize) {
		var fetchQuery = this.getQuery();
		if (initQuery != null)
			fetchQuery = initQuery.apply(this.getQuery());
		var res = fetchQuery.offset((page - 1) * pageSize).limit(pageSize).fetchResults();

		if (res == null)
			return null;

		var items = res.getResults().stream().map(this::fectItemFromTuple).collect(Collectors.toList());

		return new PageListData<T>(pageSize, res.getTotal(), items);
	}

	/**
	 * 通过classInfo来初始化查询
	 */
	private void initQueryByEnityClassInfo() {
		var selects = new ArrayList<Expression<?>>();
		var join = new ArrayList<Tuple3<EntityPath<?>, Path<?>, Path<?>>>();
		var fecthList = new ArrayList<Tuple2<Field, Path<?>>>();

		selects.add(this.mainTable); // 首先查询主表
		boolean isWantJoin=false;

		for(var field:getClassAllFields(entityClassInfo)) {
			
			field.setAccessible(true);

			if (field.isAnnotationPresent(Id.class)) {
				_idPath = Expressions.path(idClassInfo, this.mainTable, field.getName());
			}

			var tuple = this.getSelectExpressionFromField(field);
			if (tuple != null) {
				fecthList.add(tuple);
				selects.add(tuple.getT2());
				isWantJoin=true;
			}

			var joinTuple = this.getJoinExpressFromField(field);
			if (joinTuple != null)
				join.add(joinTuple);
		}
		
		//检查join设置是否正确
		Assert.isTrue(isWantJoin&&join.size()!=0,
				String.format("初始化仓库%s失败，选择语句有外表，但是没有字段@JoinEntity设置请检查是否设置了联表", this.getClass().getName()));
		

		// 添加select语句
		var queryTemp = factory.select(selects.toArray(new Expression[] {})).from(mainTable);

		// 添加join语句
		for (var joinTuple : join) {
			var eqField = Expressions.predicate(Ops.EQ, joinTuple.getT2(), joinTuple.getT3());
			queryTemp = queryTemp.leftJoin(joinTuple.getT1()).on(eqField);
		}

		if (fecthDataList == null)
			fecthDataList = fecthList;

		query = queryTemp;
	}

	/**
	 * 从字段取得路径表达式
	 * 
	 * @param field
	 * @return
	 */
	private Tuple2<Field, Path<?>> getSelectExpressionFromField(Field field) {
		// 没有带Transient标签则返回空
		if (!field.isAnnotationPresent(Transient.class)) {
			return null;
		}
		
		Assert.isTrue(field.isAnnotationPresent(FieldPath.class), "字段"+field.toString()+"添加了Transient标签的字段需要添加FieldPath标注");

		var fieldPath = field.getAnnotation(FieldPath.class);
		String name = fieldPath.name();
		int tableId = fieldPath.tableId();

		Assert.isTrue(!StringUtils.isEmpty(name), "字段"+field.toString()+"的FieldPath 注解的name一定要设置");
		Assert.isTrue(tableId >= 0, "FieldPath 注解的tableId设置要在于或等0");

		var table = this.tablesPathManger.getTablePathById(tableId);
		Assert.notNull(table, String.format("请配置TablesPathManger,确保tableId=%d有正确表达式返回值 ", tableId));
		var express = Expressions.path(field.getType(), (Path) table, name);

		return Tuples.of(field, express);
	}

	/**
	 * 从字段注解取得字段对应的联结语句元组
	 * 
	 * @param field
	 * @return
	 */
	private Tuple3<EntityPath<?>, Path<?>, Path<?>> getJoinExpressFromField(Field field) {
		// 没有注解直接返回
		if (!field.isAnnotationPresent(JoinEntity.class))
			return null;

		var join = field.getAnnotation(JoinEntity.class);
		String name = join.name();
		int tableId = join.tableId();

		Assert.isTrue(!StringUtils.isEmpty(name), "JoinEntity 注解的name一定要设置");
		Assert.isTrue(tableId >= 0, "JoinEntity 注解的tableId设置要在于或等0");

		var table = this.tablesPathManger.getTablePathById(tableId);
		Assert.notNull(table, String.format("请配置TablesPathManger,确保tableId=%d有正确表达式返回值 ", tableId));

		var mainTablePath = Expressions.path(field.getType(), (Path) this.mainTable, field.getName());
		var jointTablePath = Expressions.path(field.getType(), (Path) table, name);

		return Tuples.of(table, mainTablePath, jointTablePath);
	}

	// 从tuple取得item数据
	public T fectItemFromTuple(Tuple tuple) {
		T item = (T) tuple.get(this.mainTable);
		fecthDataList.forEach(fetch -> {
			try {
				fetch.getT1().set(item, tuple.get(fetch.getT2()));
			} catch (Exception e) {
				logger.error("从tuple提取数据失败:" + e.getMessage(), e);
			}
		});

		return item;
	}

	/**
	 * 取得类的所有字段信息，包括基础类
	 * 
	 * @param classInfo
	 * @return
	 */
	public List<Field> getClassAllFields(Class<?> classInfo) {
		var listFields = new ArrayList<Field>();
		while (classInfo != null) {
			listFields.addAll(Arrays.asList(classInfo.getDeclaredFields()));

			classInfo = classInfo.getSuperclass();
		}
		return listFields;
	}

	public EntityManager getEm() {
		return em;
	}

	public JPAQueryFactory getFactory() {
		return factory;
	}

	
}
