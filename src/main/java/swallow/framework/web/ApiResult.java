package swallow.framework.web;



import org.springframework.util.Assert;

import io.swagger.annotations.ApiModelProperty;

/**
 * Api返回结果
 * @author aohanhe
 *
 */
public class ApiResult <T> extends BaseApiResult{
	@ApiModelProperty(name="负载数据")
	private T data = null;
	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	/**
	 * 构建一个返回结果
	 * @param code
	 * @param message
	 */
	public ApiResult(int code, String message) {
		super(code, message);		
	}
	
	/**
	 * 构造一个失败结果
	 * @param code
	 * @param message
	 * @return
	 * @throws ScanElectricityException 
	 */
	@SuppressWarnings("rawtypes")
	public static<T> ApiResult fail(int code,String message) {
		
		Assert.isTrue(code>0,"错误结果请不要设置code值为0");
		
		return new ApiResult(code,message);
	}
	
	/**
	 * 构造一个标准错误
	 * @param message
	 * @return
	 * @throws ScanElectricityException
	 */
	@SuppressWarnings("rawtypes")
	public static<T> ApiResult fail(String message) {
		return new ApiResult(500,message);		
	}
	
	/**
	 * 构造一个成功结果
	 * @param data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static<T> ApiResult success(T data) {
		ApiResult<T> re=new ApiResult<>(0, "成功");
		re.setData(data);
		return re;
	}

}
