package com.cc.dextamper.option;

import java.util.List;

import com.cc.dextamper.context.MethodConfig;

public class Packer {
	private String name;
	private List<MethodConfig> method;
	
	public String getName(){
		return name;
	}
	
	public List<MethodConfig> getMethod(){
		return method;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setMethod(List<MethodConfig> method){
		this.method = method;
	}
}
