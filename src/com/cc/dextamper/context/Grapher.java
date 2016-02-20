package com.cc.dextamper.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderOffsetInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.reference.StringReference;

public class Grapher {

	private MutableMethodImplementation impl;
	private List<MethodLocation> instructions;
	private Map<MethodLocation, BasicBlock> blocks = new HashMap<>();	// easy to find later
	public List<BasicBlock> listBlocks = new ArrayList<>();	// we must have an order
	
	public Grapher(MethodImplementation implementation){
		this.impl = new MutableMethodImplementation(implementation);
		this.instructions = impl.instructionList;
		BasicBlock block = null;
		for (int i = 0; i < instructions.size() - 1; i++){
			MethodLocation location = instructions.get(i);
			if (location.getLabels().size() > 0 || i == 0) {
				if (block != null && block.size() > 0) {
					blocks.put(block.getFirst(), block);
					listBlocks.add(block);
				}
				block = new BasicBlock();
			}
			block.addLocation(location);
			switch (location.instruction.getOpcode().format){
			case Format10t:
			case Format20t:
			case Format30t:
			case Format21t:
			case Format22t:
				//GOTO && CONDITION
				if (block != null && block.size() > 0) {
					blocks.put(block.getFirst(), block);
					listBlocks.add(block);
				}
				block = new BasicBlock();
				break;
			default:
				break;
			}
		}
		if (block != null && block.size() > 0) {
			blocks.put(block.getFirst(), block);
			listBlocks.add(block);
		}
		for (int i = 0; i < listBlocks.size(); i++){
			BasicBlock basicBlock = listBlocks.get(i);
			MethodLocation lastOne = basicBlock.getLast();
			switch (lastOne.instruction.getOpcode().format) {
			case Format10t:
			case Format20t:
			case Format30t:
				//GOTO
				MethodLocation location = ((BuilderOffsetInstruction)lastOne.instruction).getTarget().getLocation();
				BasicBlock target = blocks.get(location);
				basicBlock.addTarget(target);
				break;
			case Format21t:
			case Format22t:
				MethodLocation location2 = ((BuilderOffsetInstruction)lastOne.instruction).getTarget().getLocation();
				BasicBlock target2 = blocks.get(location2);
				basicBlock.addTarget(target2);
			default:
				if (lastOne.instruction.getOpcode().name.startsWith("return")) {
					break;
				}
				if (i != listBlocks.size() - 1){
					BasicBlock nextOne = listBlocks.get(i + 1);
					basicBlock.addTarget(nextOne);
				}
				break;
			}
		}
	}
	
	public void visitConstant(){
		if (listBlocks.size() > 0) {
			calConstant(listBlocks.get(0));
		}
	}
	
	public void calConstant(BasicBlock block){
		int start = block.outSet.size();
		for (BasicBlock pre: block.pre){	// IN = U OUT
			block.inSet.putAll(pre.outSet);
		}
		block.outSet.putAll(block.inSet);
		for (MethodLocation location: block.list){
			Instruction instruction = location.getInstruction();
			Opcode op = instruction.getOpcode();
			if (op.setsRegister()){
				Integer key = ((OneRegisterInstruction)instruction).getRegisterA();
				block.removeOutSet(key);	//IN - kill
			}
			if (op.name.startsWith("const")) {// IN + GEN
				switch (op.format) {
				case Format11n:
				case Format21s:
				case Format31i:
					//const-int
					Integer reg1 = ((OneRegisterInstruction)instruction).getRegisterA();
					Integer number = ((NarrowLiteralInstruction)instruction).getNarrowLiteral();
					block.addOutSet(reg1, number);
					break;
				case Format21c:
					//const-string
					Integer reg2 = ((OneRegisterInstruction)instruction).getRegisterA();
					String value = ((StringReference)((ReferenceInstruction)instruction).getReference()).getString();
					block.addOutSet(reg2, value);
					break;
				default:
					break;
				}
			}
		}
		int end = block.outSet.size();
		if (start != end) {		//if we don not add a new <key, value>, it means we can stop now
			for (BasicBlock target: block.target){
				calConstant(target);
			}
		}
	}
	
	public void search(){
		//TODO We assume that each method has at most one decode method
		for (BasicBlock block: listBlocks){
			if (block.search())
				break;
		}
	}
	
	public void printInfo(){
		for (BasicBlock block: listBlocks){
			System.out.println("-------------BLOCK START-----------------");
			block.printInfo();
			System.out.println("-------------BLOCK END-------------------");
		}
	}
	
}
