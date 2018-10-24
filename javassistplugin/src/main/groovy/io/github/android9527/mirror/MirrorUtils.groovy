package io.github.android9527.mirror

import javassist.ClassPool
import javassist.CtMethod


public class MirrorUtils {

    /**
     * 事先载入相关类
     * @param pool
     */
    static void importBaseClass(ClassPool pool) {
        pool.importPackage("android.os.SystemClock")
        pool.importPackage("java.lang.Throwable")
        pool.importPackage("android.util.Log")
    }

    static String getSimpleName(CtMethod ctMethod) {
        def methodName = ctMethod.getName();
        return methodName.substring(methodName.lastIndexOf('.') + 1, methodName.length());
    }

    static String getClassName(int index, String filePath) {
        // .class = 6
        int end = filePath.length() - 6
        return filePath.substring(index, end).replace('\\', '.').replace('/', '.')
    }

}
