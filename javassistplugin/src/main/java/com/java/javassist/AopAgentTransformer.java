package com.java.javassist;

import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class AopAgentTransformer {

    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer)  {
        byte[] transformed = null;
        System.out.println("Transforming " + className);
        ClassPool pool = null;
        CtClass cl = null;
        try {
            pool = ClassPool.getDefault();

            cl = pool.makeClass(new java.io.ByteArrayInputStream(
                    classfileBuffer));

// CtMethod aop_method = pool.get("com.jdktest.instrument.AopMethods").
// getDeclaredMethod("aopMethod");
// System.out.println(aop_method.getLongName());

            CodeConverter convert = new CodeConverter();

            if (!cl.isInterface()) {
                CtMethod[] methods = cl.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (!methods[i].isEmpty()) {
                        AOPInsertMethod(methods[i]);
                    }
                }
                transformed = cl.toBytecode();
            }
        } catch (Exception e) {
            System.err.println("Could not instrument " + className
                    + ", exception : " + e.getMessage());
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
        return transformed;
    }

    public void AOPInsertMethod(CtMethod method) throws NotFoundException, CannotCompileException {
        //situation 1:添加监控时间
        method.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                m.replace("{ long stime = System.currentTimeMillis(); $_ = $proceed($$);System.out.println(\""
                        + m.getClassName() + "." + m.getMethodName()
                        + " cost:\" + (System.currentTimeMillis() - stime) + \" ms\");}");
            }
        });
        //situation 2:在方法体前后语句
// method.insertBefore("System.out.println(\"enter method\");");
// method.insertAfter("System.out.println(\"leave method\");");
    }

}