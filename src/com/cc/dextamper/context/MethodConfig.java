package com.cc.dextamper.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.reference.MethodReference;

import com.google.common.primitives.UnsignedBytes;

public class MethodConfig {
	private static HashMap<MethodConfig, List<Params>> collection = new HashMap<>();
	private String className;
	private String methodName;
	private String description;
	
	public MethodConfig(){}
	
	public MethodConfig(MethodReference ref, List<Object> params){
		className = ref.getDefiningClass();
		methodName = ref.getName();
		List<? extends CharSequence> types = ref.getParameterTypes();
		StringBuilder sBuilder = new StringBuilder(types.size()+3);
		sBuilder.append("(");
		for (CharSequence type: types){
			sBuilder.append(type);
		}
		sBuilder.append(")");
		sBuilder.append(ref.getReturnType());
		description = sBuilder.toString();
		if (collection.containsKey(this)) {
			List<Params> list = collection.get(this);
			list.add(new Params(params));
		}
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        return result;
    }

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MethodConfig other = (MethodConfig) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (methodName == null) {
            if (other.methodName != null)
                return false;
        } else if (!methodName.equals(other.methodName))
            return false;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        return true;
    }
	
	public static void printInfo(){
		for (Entry<MethodConfig, List<Params>> entry: collection.entrySet()){
			MethodConfig config = entry.getKey();
			List<Params> list = entry.getValue();
			if (list.size() == 0)
				continue;
			System.out.println(config.className + "->" + config.methodName + config.description);
			System.out.println(list.size() + " " + list.get(0).size());
			for (Params param: list){
				for (Object object: param.params)
					System.out.println(object);
			}
		}
	}
	
	public static void addtoCollection(MethodConfig config){
		List<Params> list = new ArrayList<>();
		collection.put(config, list);
	}
	
	class Params{
		List<Object> params;
		
		public Params(List<Object> params){
			this.params = params;
		}
		
		public String toString(){
			int count = params.size();
			StringBuilder sBuilder = new StringBuilder();
			for (int i = count-1; i >= 0; i--){
				sBuilder.append(params.get(i));
			}
			return sBuilder.toString();
		}
		
		public int size(){
			return params.size();
		}
	}
	
	public String getClassName(){
		return className;
	}
	
	public String getMethodName(){
		return methodName;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setClassName(String classname){
		this.className = classname;
	}
	
	public void setMethodName(String methodname){
		this.methodName = methodname;
	}
	
	public void setDescription(String descriptor){
		this.description = descriptor;
	}
}
