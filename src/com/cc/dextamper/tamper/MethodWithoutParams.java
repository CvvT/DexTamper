package com.cc.dextamper.tamper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.rewriter.ClassDefRewriter;
import org.jf.dexlib2.rewriter.DexRewriter;
import org.jf.dexlib2.rewriter.MethodImplementationRewriter;
import org.jf.dexlib2.rewriter.MethodRewriter;
import org.jf.dexlib2.rewriter.Rewriter;
import org.jf.dexlib2.rewriter.RewriterModule;
import org.jf.dexlib2.rewriter.RewriterUtils;
import org.jf.dexlib2.rewriter.Rewriters;
import org.jf.dexlib2.writer.pool.DexPool;

import com.cc.dextamper.Configure;
import com.cc.dextamper.builder.BuilderReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

public class MethodWithoutParams {

	DexBackedDexFile dexFile;
	DexRewriter rewriter;
	ModifyMethodWitoutParams mListener;
	Configure config;
	boolean HAS_THIS_METHOD = true;
	DexRewriter extra = new DexRewriter(new RewriterModule(){
		public Rewriter<MethodImplementation> getMethodImplementationRewriter(@Nonnull Rewriters rewriters) {
	        return new MethodImplementationRewriter(rewriters){
	        	public MethodImplementation rewrite(@Nonnull MethodImplementation methodImplementation) {
	        		MutableMethodImplementation builder = new MutableMethodImplementation(methodImplementation);
	        		mListener.modify(builder, dexFile);
	        		return builder;
	            }
	        };
	    }
	});
	
	public MethodWithoutParams(Configure config) throws IOException{
		this.config = config;
		if (config.hasModifed) {
			this.dexFile = DexFileFactory.loadDexFile(config.outPath, config.API_LEVEL);
		}else{
			this.dexFile = DexFileFactory.loadDexFile(config.inPath, config.API_LEVEL);
		}
	}
	
	public void initMethod(String className, String methodName, ModifyMethodWitoutParams listener){
		this.mListener = listener;
		Method method = BuilderReference.getMethod(dexFile, className, methodName);
		if (method == null){
			//don't have this method, add a new method to the class
			HAS_THIS_METHOD = false;
		}
		rewriter = new DexRewriter(new RewriterModule(){
			public Rewriter<Method> getMethodRewriter(@Nonnull Rewriters rewriters) {
		        return new MethodRewriter(rewriters){
		        	public Method rewrite(@Nonnull Method value) {
		        		if (className.equals(value.getDefiningClass())
		        				&& methodName.equals(value.getName())){
		        			return new RewrittenMethod(value){
		        				public MethodImplementation getImplementation() {
		        		            return RewriterUtils.rewriteNullable(extra.getMethodImplementationRewriter(),
		        		                    method.getImplementation());
		        		        }
		        			};
		        		}
		                return new RewrittenMethod(value);
		        	}
		        };
			}
			@Override
			public Rewriter<ClassDef> getClassDefRewriter(@Nonnull Rewriters rewriters){
				return new ClassDefRewriter(rewriters){
					@Override
					public ClassDef rewrite(@Nonnull ClassDef classDef) {
				        return new RewrittenClassDef(classDef){
				        	@Override
				        	public Iterable<? extends Method> getDirectMethods(){
				        		if (HAS_THIS_METHOD || !classDef.equals(className)) {
									return RewriterUtils.rewriteIterable(rewriters.getMethodRewriter(), classDef.getDirectMethods());
								}
					        	Iterable<Method> tmp = new Iterable<Method>() {
					                @Nonnull
					                @Override
					                public Iterator<Method> iterator() {
					                	Iterable<? extends Method> iterators =  classDef.getDirectMethods();
					                	List<Method> list = new ArrayList<>();
					                	list.add(BuilderReference.makeMethod(className, 
					                			methodName, ImmutableList.of(), "V", 
					                			AccessFlags.PUBLIC.getValue() | AccessFlags.STATIC.getValue(), 
					                			new MutableMethodImplementation(1)));
					                	return Iterators.concat(iterators.iterator(), list.iterator());
					                }
					            };
					            return RewriterUtils.rewriteIterable(rewriters.getMethodRewriter(), tmp);
				        	}
				        };
				    }
				};
			}
		});
	}
	
	public void modify() throws IOException{
		DexFile rewrittenDexFile = rewriter.rewriteDexFile(this.dexFile);
		DexPool.writeTo(config.outPath, rewrittenDexFile);
		config.hasModifed = true;
	}
	
	public interface ModifyMethodWitoutParams{
		void modify(MutableMethodImplementation impl, DexBackedDexFile dexFile);
	}
}
