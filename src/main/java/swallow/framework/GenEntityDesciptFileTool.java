package swallow.framework;



import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.lang.reflect.Field;

import java.net.URISyntaxException;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import swallow.framework.entitydescript.EntityDescript;
import swallow.framework.entitydescript.EntityProperty;
import swallow.framework.jpaquery.repository.annotations.CnName;

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
	public static void generateEntityDescriptFromPackage(String packageName,Class<?> baseCommonEnityClass,String dirPath) throws URISyntaxException, IOException {
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
				generateEntityDescriptFromClassInfo(classInfo,outPath,baseCommonEnityClass);
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
	private static void generateEntityDescriptFromClassInfo(Class<?> classInfo,String outDirPath,Class<?> checkOnlyIdType) throws JsonProcessingException {
		System.out.println("扫描到类:"+classInfo.getName());
		
		var path=Paths.get(outDirPath, classInfo.getSimpleName()+".des").toString();
						
		EntityDescript descript=new EntityDescript();
		descript.setName(classInfo.getSimpleName());
		descript.setFullName(classInfo.getName());
		
		// 如果不是指定类的派生类，则认为是只有id的实体
		descript.setOnlyId(!checkOnlyIdType.isAssignableFrom(classInfo));
				
		
		if(classInfo.isAnnotationPresent(CnName.class)){
			var cnName=classInfo.getAnnotation(CnName.class);
			Assert.isTrue(!StringUtils.isEmpty(cnName),"CnName注解的值不能为空");
			descript.setCnname(cnName.value());
		}
		
		
		
		var props = Flux.fromIterable(getClassAllFields(classInfo))
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
	 * 取得类的所有字段信息，包括基础类
	 * 
	 * @param classInfo
	 * @return
	 */
	private static List<Field> getClassAllFields(Class<?> classInfo) {
		var listFields = new ArrayList<Field>();
		while (classInfo != null) {
			listFields.addAll(Arrays.asList(classInfo.getDeclaredFields()));

			classInfo = classInfo.getSuperclass();
		}
		return listFields;
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
		var javaType=field.getType().getSimpleName();
		
		
		String jsType="string";
		if(Integer.class.isAssignableFrom(filedType)||int.class.isAssignableFrom(filedType)) {
			jsType="number";
			javaType="Integer";
		}
		if(Long.class.isAssignableFrom(filedType)||long.class.isAssignableFrom(filedType)) {
			jsType="number";
			javaType="Long";
		}
		if(Float.class.isAssignableFrom(filedType)||float.class.isAssignableFrom(filedType)) {
			jsType="number";
			javaType="Float";
		}
		if(Double.class.isAssignableFrom(filedType)||double.class.isAssignableFrom(filedType)) {
			jsType="number";
			javaType="Double";
		}
		if(Byte.class.isAssignableFrom(filedType)||byte.class.isAssignableFrom(filedType)) {
			jsType="number";
			javaType="Byte";
		}
		if(Boolean.class.isAssignableFrom(filedType)||boolean.class.isAssignableFrom(filedType)) {
			jsType="number";
			javaType="Boolean";
		}
		if(Date.class.isAssignableFrom(filedType))
			jsType="string";
		
		prop.setJsType(jsType);
		prop.setType(javaType);
		
		
		//取得中文名称
		if(field.isAnnotationPresent(CnName.class)) {
			CnName cnName=field.getAnnotation(CnName.class);
			Assert.isTrue(!StringUtils.isEmpty(cnName.value()),"注解CnName的值没有设置");
			
			prop.setCnname(cnName.value());
		}else {
			prop.setCnname(field.getName());
		}
		
		prop.setReadonly(field.isAnnotationPresent(Transient.class)||
				field.isAnnotationPresent(Id.class)
				||field.isAnnotationPresent(Version.class)
				||field.isAnnotationPresent(CreatedDate.class)
				||field.isAnnotationPresent(CreatedBy.class)
				||field.isAnnotationPresent(LastModifiedBy.class)
				||field.isAnnotationPresent(LastModifiedDate.class));
		
		return prop;
	}
	

}
