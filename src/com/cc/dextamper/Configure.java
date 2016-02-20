package com.cc.dextamper;

public class Configure {

	public int API_LEVEL = 16;
	public String inPath;
	public String outPath;
	public boolean hasModifed = false;	
	
	public Configure(String in, String out){
		this.inPath = in;
		this.outPath = out;
	}
	
	public Configure(String in, String out, int api){
		this(in, out);
		this.API_LEVEL = api;
	}
	
}
