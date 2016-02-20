package com.cc.dextamper.tamper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.rewriter.DexRewriter;
import org.jf.dexlib2.rewriter.RewriterModule;
import org.jf.dexlib2.writer.pool.DexPool;

import com.cc.dextamper.Configure;
import com.cc.dextamper.iface.FilterClassDef;

public class MergeDex {

	private String add_path;
	private Configure config;
	DexBackedDexFile dexfile;
	private Map<String, Integer> internedItem = new HashMap<>();
	private boolean ADD_ALL = false;

	public MergeDex(Configure configure, String addpath) throws IOException{
		this.config = configure;
		this.add_path = addpath;
		if (configure.hasModifed) {
			dexfile = DexFileFactory.loadDexFile(configure.outPath, configure.API_LEVEL);
		}else{
			dexfile = DexFileFactory.loadDexFile(configure.inPath, configure.API_LEVEL);
		}
	}
	
	/*
	 * If you add a class name here, the class will be added to the new dex file
	 */
	public void addclassName(String className){
		className = className.replaceAll("\\.", "/");
		className = "L" + className + ";";
		if (!internedItem.containsKey(className)) {
			System.out.println(className);
			internedItem.put(className, 0);
		}
	}
	
	/*
	 * If you want to add all class except the class which has name starting with "Landroid"
	 * or "Ljava" or "Ldalvik", I will simply drop these kind of class and add the others.
	 * Just call this function
	 */
	public void addAllclass(){
		this.ADD_ALL = true;
	}
	
	public void merge(){
		try {
			DexBackedDexFile addfile = DexFileFactory.loadDexFile(add_path, config.API_LEVEL);
			dexfile.setaddDexFile(addfile, new FilterClassDef() {
				
				@Override
				public DexBackedClassDef rewrite(DexBackedClassDef classDef) {
					// TODO Auto-generated method stub
					String className = classDef.getType();
					if (ADD_ALL) {
						if (!className.startsWith("Landroid") && 
								!className.startsWith("Ljava") &&
								!className.startsWith("Ldalvik")) {
								return classDef;
						}else{
							return null;
						}
					}
					
					if (internedItem.containsKey(className)) {
						System.out.println("contain " + className);
						return classDef;
					}
					return null;
				}
			});

			DexRewriter rewriter = new DexRewriter(new RewriterModule());
			DexFile rewrittenDexFile = rewriter.rewriteDexFile(dexfile);
			DexPool.writeTo(config.outPath, rewrittenDexFile);
			config.hasModifed = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
