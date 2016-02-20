package com.cc.dextamper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Format;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.ReferenceType;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.reference.MethodReference;

import com.cc.dextamper.context.Grapher;
import com.cc.dextamper.context.MethodConfig;

public class Test {

	private static int api = 16;
//	private static String path = "/Users/CwT/Documents/workspace/DexTamper/test/test.apk";
//	private static String add_path = "/Users/CwT/Documents/workspace/DexTamper/test/add.apk";
	private static String search_path = "/Users/CwT/Documents/workspace/DexTamper/test/demo.dex";
	static List<String> paramsList = new ArrayList<>();
	
	static{
		paramsList.add("Ljava/lang/String;");
	}
	
	public static void main(String argv[]){
		test1(argv);
	}
	
	public static void test1(String argv[]){
		try{
			DexBackedDexFile dexFile = DexFileFactory.loadDexFile(search_path, 16);
			for (ClassDef classDef: dexFile.getClasses()){
				if (classDef.getType().startsWith("Landroid") ||
						classDef.getType().startsWith("Ljava") ||
						classDef.getType().startsWith("Ldalvik")) {
					continue;
				}
				for (Method method: classDef.getMethods()){
//					if (method.getDefiningClass().equals("Lcom/cc/test/MainActivity;")
//							&& method.getName().equals("onCreate")){
					if (method.getImplementation() == null)
						continue;
					Grapher grapher = new Grapher(method.getImplementation());
					grapher.visitConstant();
					grapher.search();
//						grapher.printInfo();
//					}
				}
			}
			MethodConfig.printInfo();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void test(String argv[]){
		try {
			DexBackedDexFile dexFile = DexFileFactory.loadDexFile(search_path, 16);
			for (ClassDef classdef: dexFile.getClasses()){
				for (Method method: classdef.getMethods()){
					MethodImplementation impl = method.getImplementation();
					if (impl == null) {
						continue;
					}
//					System.out.println(method.getDefiningClass() + "->" + method.getName());
					for (Instruction instruction: impl.getInstructions()){
						Opcode opcode = instruction.getOpcode();
						if (opcode.referenceType == ReferenceType.METHOD){
							ReferenceInstruction ref = (ReferenceInstruction)instruction;
							MethodReference methodRef = (MethodReference)ref.getReference();
							if (methodRef.getName().equals("d") && 
									methodRef.getDefiningClass().equals("Lcom/baidu/protect/A;")) {
								List<? extends CharSequence> params = methodRef.getParameterTypes();
								
								if (params.size() == paramsList.size()) {
									boolean flag = true;
									int index = 0;
									for(CharSequence param: params){
										if (!param.equals(paramsList.get(index))) {
											flag = false;
											break;
										}
										index++;
									}
									if (flag && "V".equals(methodRef.getReturnType())) {
										
									}
								}
							}
//							System.out.println(methodRef.getName() + " " + methodRef.getDefiningClass());
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}