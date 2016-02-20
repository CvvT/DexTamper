package com.cc.dextamper.sample;

import java.io.IOException;

import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;

import com.cc.dextamper.Configure;
import com.cc.dextamper.builder.InstructionBuilder;
import com.cc.dextamper.tamper.MethodWithoutParams;
import com.cc.dextamper.tamper.MethodWithoutParams.ModifyMethodWitoutParams;

public class AddInstructions {

	private static String add_path = "/Users/CwT/Documents/workspace/DexTamper/test/add.apk";
	
	public static void main(String argv[]){
		Configure configure = new Configure(add_path, "test.dex");
		try {
			MethodWithoutParams method = new MethodWithoutParams(configure);
			method.initMethod("Lcom/cc/test/Hello;", 
					"<clinit>", new ModifyMethodWitoutParams() {
						
						@Override
						public void modify(MutableMethodImplementation impl, DexBackedDexFile dexfile) {
							// TODO Auto-generated method stub
							impl.addInstruction(0, InstructionBuilder.newConstStringIns(0, 
			        				"com.cc.test"));
							impl.addInstruction(1, InstructionBuilder.newConst4Ins(1, 0));
							impl.addInstruction(2, InstructionBuilder.newInvokeStaticIns(2, 
			                		0, 1, 0, 0, 0, "Lcom/cc/test/ProxyShell;", "startshell", dexfile));
							if (impl.getRegisterCount() < 2)
								impl.setRegisterCount(2);
						}
					});
			method.modify();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
