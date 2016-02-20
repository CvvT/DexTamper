package com.cc.dextamper.sample;

import java.io.IOException;

import com.cc.dextamper.Configure;
import com.cc.dextamper.tamper.MergeDex;

public class AddClass {

	private static String path = "/Users/CwT/Documents/workspace/DexTamper/test/test.apk";
	private static String add_path = "/Users/CwT/Documents/workspace/DexTamper/test/add.apk";

	public static void main(String argv[]){
		Configure configure = new Configure(path, "out.dex");
		MergeDex merge;
		try {
			merge = new MergeDex(configure, add_path);
			/*
			 * add one class like this or addAllclass
			 */
			merge.addclassName("com.cc.test.MainActivity");
			merge.addAllclass();
			merge.merge();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
