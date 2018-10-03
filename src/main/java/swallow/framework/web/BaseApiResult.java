package swallow.framework.web;



import org.springframework.util.Assert;
import io.swagger.annotations.ApiModelProperty;


/**
 * Api基础返回结果集
 * @author aohanhe
 *
 */
public class BaseApiResult {
	@ApiModelProperty(name="返回状态码",value="返回状态码,0 表示成功")
	private int code= 0;
	@ApiModelProperty(name="错误消息",value="返回状态消息")
	private String  message;
	
	
	public BaseApiResult(int code,String message) {
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * 构造一个失败结果
	 * @param code
	 * @param message
	 * @return
	 * @throws ScanElectricityException 
	 */
	public static BaseApiResult failResult(int code,String message){		
		Assert.isTrue(code>0,"失败结果不能设置code为0");
		
		return new BaseApiResult(code,message);
	}
	
	/**
	 * 构造一个成功结果
	 * @param data
	 * @return
	 */
	public static BaseApiResult successResult() {
		BaseApiResult re=new BaseApiResult(0, "成功");
		
		return re;
	}

}
