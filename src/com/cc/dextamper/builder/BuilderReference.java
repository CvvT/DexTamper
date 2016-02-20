package com.cc.dextamper.builder;

import java.util.List;

import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.writer.builder.BuilderAnnotationSet;
import org.jf.dexlib2.writer.builder.BuilderFieldReference;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderMethodParameter;
import org.jf.dexlib2.writer.builder.BuilderMethodReference;
import org.jf.dexlib2.writer.builder.BuilderProtoReference;
import org.jf.dexlib2.writer.builder.BuilderStringReference;
import org.jf.dexlib2.writer.builder.BuilderTypeList;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.jf.util.ExceptionWithContext;

import com.google.common.collect.ImmutableList;

public class BuilderReference {

	public static Reference makeStringReference(String str){
		if (str == null)
			return null;
		return new BuilderStringReference(str);
	}
	
	public static Reference makeTypeReference(String str){
		return new BuilderTypeReference((BuilderStringReference)makeStringReference(str));
	}
	
	public static Reference makeFieldReference(String defineClass, String name, String type){
		return new BuilderFieldReference((BuilderTypeReference)makeTypeReference(defineClass),
				new BuilderStringReference(name),
				(BuilderTypeReference)makeTypeReference(type));
	}
	
	public static BuilderTypeList makeTypeList(List<String> params){
		List<BuilderTypeReference> list = ImmutableList.of();
		for (String type: params){
			list.add((BuilderTypeReference)makeTypeReference(type));
		}
		return new BuilderTypeList(list);
	}
	
	public static BuilderProtoReference makeProtoReference(List<String> params, String returnType){
		StringBuilder sBuilder = new StringBuilder(makeshorty(returnType));
		for (String str: params){
			sBuilder.append(makeshorty(str));
		}
		return new BuilderProtoReference((BuilderStringReference)makeStringReference(sBuilder.toString()), 
				makeTypeList(params), 
				(BuilderTypeReference)makeTypeReference(returnType));
	}
	
	public static BuilderMethodParameter makeMethodParameter(String type, String name){
		return new BuilderMethodParameter((BuilderTypeReference)makeTypeReference(type), 
				(BuilderStringReference)makeStringReference(name), 
				BuilderAnnotationSet.EMPTY);
	}
	
	public static Reference makeMethodReference(String className, String methodName, List<String> params, String returntype){
		return new BuilderMethodReference((BuilderTypeReference)makeTypeReference(className), 
				(BuilderStringReference)makeStringReference(methodName), 
				makeProtoReference(params, returntype));
	}
	
	public static Reference makeMethodReference(DexBackedDexFile dexfile, String className, String methodName){
		//todo
		Method method = getMethod(dexfile, className, methodName);
		if (method == null) {
			throw new ExceptionWithContext("Can not find this method: " + className + "->" + methodName);
		}
		return new PoolMethodReference(method);
	}
	
	public static Method getMethod(DexBackedDexFile dexfile, String className, String methodName){
		for (ClassDef classDef: dexfile.getClasses()){
			if (classDef.getType().equals(className)) {
				System.out.println("Find the class");
				for (Method method: classDef.getMethods()){
					if (method.getName().equals(methodName)) {
						return method;
					}
				}
			}
		}
		return null;
	}
	
	public static BuilderMethod makeMethod(String className, String methodName, List<String> params, 
			String returntype, int accessFlags, MethodImplementation impl){
		List<BuilderMethodParameter> list = ImmutableList.of();
		for (String type: params){
			list.add(makeMethodParameter(type, null));
		}
		return new BuilderMethod((BuilderMethodReference)makeMethodReference(className, methodName, params, returntype), 
				list, accessFlags, BuilderAnnotationSet.EMPTY, impl);
	}
	
	public static String makeshorty(String type){
		if (type.charAt(0) == 'L') {
			return "L";
		}
		return type;
	}
}
