package swallow.framework.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Sort.Order;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

/**
 * 查询bean对象的基础类
 * @author aohanhe
 *
 */
public class BaseQueryBean {
	@ApiModelProperty(name="排序条件",value="排序条件 例+id,+表示升序 - 表示降序")
	private String sort;
	

	/**
	 * 处理后的排序条件列表
	 */
	@JsonIgnore
	private List<Order> orders;
	
	/**
	 * 对排序条件进行初始化
	 */
	@PostConstruct
	public void init() {		
		
		//从 sort中提取排序列表
		if(!StringUtils.isEmpty(this.sort)) {
			orders=Stream.of(sort.split(","))
				.map(v->{
					if(v.startsWith("+"))
						return Order.asc(v.substring(0,v.length()-1));
					if(v.startsWith("-"))
						return Order.desc(v.substring(0, v.length()-1));
					return Order.asc(v);
				}).collect(Collectors.toList());
		}else {
			orders=new ArrayList<>();
		}
	}

	

	public List<Order> getOrders() {
		return orders;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

}
