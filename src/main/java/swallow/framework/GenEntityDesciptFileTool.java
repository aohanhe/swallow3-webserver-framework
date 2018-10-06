package swallow.framework;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import swallow.framework.entitydescript.EntityDescript;
import swallow.framework.entitydescript.EntityProperty;

@SpringBootApplication
public class GenEntityDesciptFileTool {
	private static ObjectMapper objectMapper; 
	
	static {
		objectMapper=new ObjectMapper();
		
	}
	
	public static void main(String []avg) throws IOException {
		
	}
	
	/**
	 * 取得包下所有的类的名称
	 * @param packageName
	 * @throws IOException 
	 */
	public static List<Class<?>> getEntityClassListFromPackage(String packageName) throws IOException {
		ClassScanTool tool=new ClassScanTool();
		return tool.scanClassFromPackage(packageName, Entity.class);	
	}
	
	/**
	 * 生成实体的描述文件到指定的目录
	 * @param packageName
	 * @param dirPath
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void generateEntityDescriptFromPackage(String packageName,String dirPath) throws URISyntaxException, IOException {
		var classLoader=GenEntityDesciptFileTool.class.getClassLoader();
		
		String projectDir=System.getProperty("user.dir").replace('\\', '/');
		
		//如果是使用了相对路径
		if(dirPath.startsWith("/"))
			dirPath=dirPath.replaceFirst("/", projectDir+"/");	
		
		final String outPath=dirPath;
		
		File dir=new File(dirPath);
		Assert.isTrue(dir.exists()&&dir.isDirectory(),"指定的输出路径"+dirPath+"不存在");
		
		
		var list=getEntityClassListFromPackage(packageName);
		
		// 对取得满足条件的类进行处理
		list.stream().forEach((classInfo)->{
			try {
				generateEntityDescriptFromClassInfo(classInfo,outPath);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		});
		
	}
	/**
	 * 生成描述文件到指定的目录
	 * @param classInfo
	 * @param outDirUrl
	 * @throws JsonProcessingException 
	 */
	private static void generateEntityDescriptFromClassInfo(Class<?> classInfo,String outDirPath) throws JsonProcessingException {
		System.out.println("扫描到类:"+classInfo.getName());
		
		var path=Paths.get(outDirPath, classInfo.getSimpleName()+".des").toString();
						
		EntityDescript descript=new EntityDescript();
		descript.setName(classInfo.getSimpleName());
		descript.setFullName(classInfo.getName());
		
		var props = Flux.fromArray(classInfo.getDeclaredFields())
			.map(GenEntityDesciptFileTool::generateEntityPropertyFromField)
			.collect(Collectors.toList()).block();
		
		descript.setProps(props);
		
		try(PrintWriter writer=new PrintWriter(path, "UTF8"))
		{
			//写成文件到指定的目录
			objectMapper.writeValue(writer,descript);
			
			System.out.println(classInfo.getName()+"描述文件生成完成");
		}catch(Exception ex) {
			ex.printStackTrace();
			System.out.println(String.format("创建类%s描述文件时出错:%s", classInfo.getName(),ex.getMessage()));
		}
		
	}
	
	/**
	 * 生成字段的描述
	 * @param field
	 * @return
	 */
	private static EntityProperty generateEntityPropertyFromField(Field field) {
		var prop=new EntityProperty();
		prop.setName(field.getName());
		
		
		var filedType=field.getType();
		prop.setType(field.getType().getSimpleName());
		
		String jsType="string";
		if(Integer.class.isAssignableFrom(filedType))
			jsType="number";
		if(Long.class.isAssignableFrom(filedType))
			jsType="number";
		if(Float.class.isAssignableFrom(filedType))
			jsType="number";
		if(Double.class.isAssignableFrom(filedType))
			jsType="number";
		
		if(Date.class.isAssignableFrom(filedType))
			jsType="string";
		
		prop.setJsType(jsType);
		
		return prop;
	}
	

}
