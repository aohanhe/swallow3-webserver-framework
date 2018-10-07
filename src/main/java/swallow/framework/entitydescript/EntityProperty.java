package swallow.framework.entitydescript;

/**
 * 实体的属性的定义
 * 
 * @author aohanhe
 *
 */
public class EntityProperty {
	// 名称
	private String name;
	// 类型
	private String type;
	// js对应的类型
	private String jsType;
	
	private String cnname;
	//是否只读字段
	private boolean readonly;

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

	public String getCnname() {
		return cnname;
	}

	public void setCnname(String cnname) {
		this.cnname = cnname;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	

}
