package swallow.framework.entitydescript;

/**
 * 实体属性名
 * 
 * @author aohanhe
 *
 */
public class EntityProperty {
	// 属性名
	private String name;
	// 类型名
	private String type;
	// js类型名
	private String jsType;

	public EntityProperty() {
		
	}
	
	public EntityProperty(String name,String type,String jsType) {
		this.name=name;
		this.type=type;
		this.jsType=jsType;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getJsType() {
		return jsType;
	}

	public void setJsType(String jsType) {
		this.jsType = jsType;
	}

}
