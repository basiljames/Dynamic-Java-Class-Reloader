///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.basil.reloaderjvmagent;
//
//
///**
// *
// * @author Basil
// */
//import java.lang.instrument.ClassFileTransformer;
//import java.lang.instrument.IllegalClassFormatException;
//import java.security.ProtectionDomain;
//
//import org.objectweb.asm.*;
//
//public class SimpleTransformer_1 implements ClassFileTransformer {
//
//	public SimpleTransformer_1() {
//		super();
//	}
//
//	public byte[] transform(ClassLoader loader, String className, Class redefiningClass, ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
//		byte[] result = bytes;
//		try {
//			// Create a reader for the existing bytes.
//			ClassReader reader = new ClassReader(bytes);
//			// Create a writer
//			ClassWriter writer = new ClassWriter(true);
//			// Create our class adapter, pointing to the class writer
//			// and then tell the reader to notify our visitor of all
//			// bytecode instructions
//			
//            reader.accept(new PrintStatementClassAdapter(writer, className), true);
//            
//			// get the result from the writer.
//			result = writer.toByteArray();
//		}
//		// Runtime exceptions thrown by the above code disappear
//		// This catch ensures that they are at least reported.
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//        return result;
//	}
//
//	/**
//	 * A simple class adapter that wraps the visitMethod result to return out
//	 * method visitor implementation.
//	 */
//	private class PrintStatementClassAdapter extends ClassAdapter {
//		PrintStatementClassAdapter(ClassVisitor visitor, String theClass) {
//			super(visitor);
//		}
//	}
//}
