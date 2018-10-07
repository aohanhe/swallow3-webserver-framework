package swallow.framework.entitydescript;

import java.util.List;

/**
 * 实体对象描述
 * @author aohanhe
 *
 */
public class EntityDescript {
	/**
	 * 中文名称
	 */
	private String cnname;
	/**
	 * 短名称
	 */
	private String name;
	/**
	 * 长名，包括了为命名空间
	 */
	private String fullName;
	
	/**
	 * 是否只是只拥有ID的基础实体
	 */
	private boolean onlyId;
	
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

	public String getCnname() {
		return cnname;
	}

	public void setCnname(String cnname) {
		this.cnname = cnname;
	}

	public boolean isOnlyId() {
		return onlyId;
	}

	public void setOnlyId(boolean onlyId) {
		this.onlyId = onlyId;
	}

	

}
