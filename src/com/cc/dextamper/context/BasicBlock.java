package com.cc.dextamper.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.Label;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.iface.reference.StringReference;

public class BasicBlock {

	List<MethodLocation> list = new ArrayList<>();
	List<BasicBlock> target = new ArrayList<>();
	List<BasicBlock> pre = new ArrayList<>();
	Map<Integer, Object> inSet = new HashMap<>();
	Map<Integer, Object> outSet = new HashMap<>();
	
	public boolean search(){
		Map<Integer, Object> tmpSet = new HashMap<>();
		tmpSet.putAll(inSet);
		for (MethodLocation location: list){
			Instruction instruction = location.instruction;
			Opcode op = instruction.getOpcode();
			if (op.setsRegister()){
				Integer key = ((OneRegisterInstruction)instruction).getRegisterA();
				tmpSet.remove(key);
			}
			if (op.name.startsWith("const")) {// IN + GEN
				switch (op.format) {
				case Format11n:
				case Format21s:
				case Format31i:
					//const-int
					Integer reg1 = ((OneRegisterInstruction)instruction).getRegisterA();
					Integer number = ((NarrowLiteralInstruction)instruction).getNarrowLiteral();
					tmpSet.put(reg1, number);
					break;
				case Format21c:
					//const-string
					Integer reg2 = ((OneRegisterInstruction)instruction).getRegisterA();
					String value = ((StringReference)((ReferenceInstruction)instruction).getReference()).getString();
					tmpSet.put(reg2, value);
					break;
				default:
					break;
				}
			} else if (op.name.equals("invoke-static")) {
				BuilderInstruction35c invoke = (BuilderInstruction35c)instruction;
				int count = invoke.getRegisterCount();
				boolean find = true;
				int register = 0;
				List<Object> params = new ArrayList<>();
				switch (count) {
				case 5:
					register = invoke.getRegisterG();
					if (!tmpSet.containsKey(register)){
						find = false;
						break;
					}
					params.add(tmpSet.get(register));
				case 4:
					register = invoke.getRegisterF();
					if (!tmpSet.containsKey(register)){
						find = false;
						break;
					}
					params.add(tmpSet.get(register));
				case 3:
					register = invoke.getRegisterE();
					if (!tmpSet.containsKey(register)){
						find = false;
						break;
					}
					params.add(tmpSet.get(register));
				case 2:
					register = invoke.getRegisterD();
					if (!tmpSet.containsKey(register)){
						find = false;
						break;
					}
					params.add(tmpSet.get(register));
				case 1:
					register = invoke.getRegisterC();
					if (!tmpSet.containsKey(register)){
						find = false;
					}
					params.add(tmpSet.get(register));
					break;
				default:
					find = false;	// We assume that the decode method must have one parameter
					break;
				}
				if (find) {
					MethodReference ref = (MethodReference)(invoke.getReference());
					String Name = ref.getDefiningClass();
					if (!Name.startsWith("Ljava") &&
							!Name.startsWith("Landroid") &&
							!Name.startsWith("Ldalvik")){
						new MethodConfig(ref, params);
						return false;
					}
				}
			} else if (op.name.equals("invoke-static/range")) {
				//TODO
			}
		}
		return false;
	}
	
	public void addLocation(MethodLocation location){
		list.add(location);
	}
	
	public void addTarget(BasicBlock block){
		target.add(block);
		block.addPre(this);
	}
	
	public void addPre(BasicBlock block){
		pre.add(block);
	}
	
	public int size(){
		return list.size();
	}
	
	public MethodLocation getFirst(){
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public MethodLocation getLast(){
		if (list.size() > 0) {
			return list.get(size() - 1);
		}
		return null;
	}
	
	public MethodLocation getByindex(int index){
		if (index >= 0 && index <= size() -1) {
			return list.get(index);
		}
		return null;
	}
	
	public void addOutSet(Integer register, Object value){
		outSet.put(register, value);
	}
	
	public void removeOutSet(Integer key){
		outSet.remove(key);
	}
	
	public void printInfo(){
		for (MethodLocation location: list){
			System.out.println(location.instruction.getOpcode().name);
		}
		System.out.println("My Target:");
		for (BasicBlock block: target){
			System.out.print(block.getFirst().instruction.getOpcode().name + ";");
		}
		System.out.println();
		for (Entry<Integer, Object> map: outSet.entrySet()){
			System.out.println(map.getKey() + ":" + map.getValue());
		}
	}
}
