package swallow.framework.entitydescript;

import java.util.List;

/**
 * 实体描述
 * @author aohanhe
 *
 */
public class EntityDescript {
	/**
	 * 实体的名称
	 */
	private String name;
	/**
	 * 实体的加上了包名的全名
	 */
	private String fullName;
	
	private List<EntityProperty> props;
	
	public EntityDescript() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public List<EntityProperty> getProps() {
		return props;
	}

	public void setProps(List<EntityProperty> props) {
		this.props = props;
	}

}
