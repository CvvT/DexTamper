package com.cc.dextamper.builder;

import java.util.List;

import org.jf.dexlib2.base.reference.BaseMethodReference;
import org.jf.dexlib2.iface.Method;

public class PoolMethodReference extends BaseMethodReference{

	Method method;
	
	public PoolMethodReference(Method method) {
		// TODO Auto-generated constructor stub
		this.method = method;
	}
	
	@Override
	public String getDefiningClass() {
		// TODO Auto-generated method stub
		return method.getDefiningClass();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return method.getName();
	}

	@Override
	public List<? extends CharSequence> getParameterTypes() {
		// TODO Auto-generated method stub
		return method.getParameterTypes();
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return method.getReturnType();
	}

}
