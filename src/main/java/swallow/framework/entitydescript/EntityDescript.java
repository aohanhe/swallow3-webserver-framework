package swallow.framework.entitydescript;

import java.util.List;

/**
 * ʵ������
 * @author aohanhe
 *
 */
public class EntityDescript {
	/**
	 * ʵ�������
	 */
	private String name;
	/**
	 * ʵ��ļ����˰�����ȫ��
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
