package com.cc.dextamper.option;

import java.util.List;

public class Options {
	private String version;
	private List<Packer> packer;
	
	public String getVersion(){
		return version;
	}
	
	public List<Packer> getPacker(){
		return packer;
	}
	
	public void setVersion(String version){
		this.version = version;
	}
	
	public void setPacker(List<Packer> packers){
		this.packer = packers;
	}
}
