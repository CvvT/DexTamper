package com.cc.dextamper.builder;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x;
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;

public class InstructionBuilder {

	public static BuilderInstruction10x newNopIns(){
		return new BuilderInstruction10x(Opcode.NOP);
	}
	
	public static BuilderInstruction21c newConstStringIns(int register, String string){
		return new BuilderInstruction21c(Opcode.CONST_STRING, register, 
				BuilderReference.makeStringReference(string));
	}
	
	public static BuilderInstruction11n newConst4Ins(int register, int num){
		return new BuilderInstruction11n(Opcode.CONST_4, register, num);
	}
	
	public static BuilderInstruction35c newInvokeStaticIns(int registercount, int registerA,
			int registerB, int registerC, int registerD, int registerE, String className, 
			String methodName, DexBackedDexFile dexfile){
		return new BuilderInstruction35c(Opcode.INVOKE_STATIC, registercount, registerA, registerB, 
				registerC, registerD, registerE,
				BuilderReference.makeMethodReference(dexfile, className, methodName));
	}
}
