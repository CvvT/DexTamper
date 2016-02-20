package com.cc.dextamper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Method;

import com.alibaba.fastjson.JSON;
import com.cc.dextamper.context.Grapher;
import com.cc.dextamper.context.MethodConfig;
import com.cc.dextamper.option.Options;
import com.cc.dextamper.option.Packer;

public class Main {

	private static String search_path = "/Users/CwT/Documents/workspace/DexTamper/test/demo.dex";

	public static void main(String argv[]){
		int count = argv.length;
		try {
			FileInputStream in = new FileInputStream(new File("assets/decode.json"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			StringBuilder sBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
				sBuilder.append(line);
			reader.close();
			Options option = JSON.parseObject(sBuilder.toString(), Options.class);
			search(option);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void search(Options options){
		try{
			for (Packer packer: options.getPacker()){
				for (MethodConfig config: packer.getMethod()){
					MethodConfig.addtoCollection(config);
				}
			}
			DexBackedDexFile dexFile = DexFileFactory.loadDexFile(search_path, 16);
			for (ClassDef classDef: dexFile.getClasses()){
				if (classDef.getType().startsWith("Landroid") ||
						classDef.getType().startsWith("Ljava") ||
						classDef.getType().startsWith("Ldalvik")) {
					continue;
				}
				for (Method method: classDef.getMethods()){
					if (method.getImplementation() == null)
						continue;
					Grapher grapher = new Grapher(method.getImplementation());
					grapher.visitConstant();
					grapher.search();
				}
			}
			MethodConfig.printInfo();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
