package com.cc.dextamper.iface;

import org.jf.dexlib2.dexbacked.DexBackedClassDef;

public interface FilterClassDef {
	public DexBackedClassDef rewrite(DexBackedClassDef classDef);
}
