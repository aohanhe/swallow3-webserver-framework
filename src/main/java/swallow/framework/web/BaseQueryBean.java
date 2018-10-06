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
	
	private String[] sorts;
	

	@ApiModelProperty(name="排序条件",value="排序条件 例+id,+表示升序 - 表示降序")
	@JsonIgnore
	private List<Order> orders;
	
	/**
	 * 对排序条件进行初始化
	 */	
	public void init() {		
		
		//从 sort中提取排序列表
		if(sorts!=null) {
			orders=Stream.of(sorts)
				.filter(v->!StringUtils.isEmpty(v))
				.filter(v->!((v.length()==1)&&(v=="+"||v=="-")))
				.map(v->{
					if(v.startsWith("+"))
						return Order.asc(v.substring(1));
					if(v.startsWith("-"))
						return Order.desc(v.substring(1));
					return Order.asc(v);
				}).collect(Collectors.toList());
		}else {
			orders=new ArrayList<>();
		}
	}

	

	public List<Order> getOrders() {
		// 如果order对象没有初始化，则初始化
		if(sorts!=null&&orders==null)
			this.init();
		return orders;
	}



	public String[] getSorts() {
		return sorts;
	}



	public void setSorts(String[] sorts) {
		this.sorts = sorts;
	}

	

}
