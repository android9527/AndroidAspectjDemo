package io.github.android9527.mirror

import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod
import javassist.Modifier
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Created by michaelzhong on 2018/1/25.
 */

public class MirrorInject {

    public static void injectJar(String path, Project project) {
        if (path.endsWith(".jar")) {
            File jarFile = new File(path)
            String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace('.jar', '')
            File unJar = new File(jarZipDir)

            try {
                MirrorJarUtils.unJar(jarFile, unJar)
                injectDir(unJar.getAbsolutePath(), project)
                jarFile.delete()
                MirrorJarUtils.jar(jarFile, unJar)
            } catch (Exception e) {
                MirrorLogger.log("injectJar exception:" + e.toString())
            } finally {
                FileUtils.deleteDirectory(unJar)
            }
        }
    }

    public static void injectDir(String inputDir, Project project) {

        MirrorTransform.appendClassPath(inputDir)

        File dir = new File(inputDir)
        if (!dir.isDirectory()) {
            return
        }

        final int prefixIndex = inputDir.length() + 1

        dir.eachFileRecurse { File file ->

            String filePath = file.absolutePath

            //确保当前文件是class文件，并且不是系统自动生成的class文件
            if (filePath.endsWith(".class")
                    && !filePath.contains("R\$")
                    && !filePath.contains("R.class")
                    && !filePath.contains("BuildConfig.class")) {

                String className = MirrorUtils.getClassName(prefixIndex, filePath)

                if (className.startsWith(MirrorTransform.mirrorPackage)
                        && !className.startsWith("me.ele.mirror.lib")) {

                    CtClass ctClass = MirrorTransform.pool.getCtClass(className)

                    if (!Modifier.isInterface(ctClass.getModifiers())) {
                        if (ctClass.isFrozen()) {
                            ctClass.defrost()
                        }
                        if (ctClass.getDeclaredConstructors() != null) {
                            for (CtConstructor ctConstructor : ctClass.getDeclaredConstructors()) {

                                /**
                                 * 插入代码，统计主线程中是否有文件读写操作
                                 */
                                MirrorAnrDetect.detectFileOperate(ctConstructor)

                                if (filePath.contains("\$")) {
                                    continue
                                }

                                /**
                                 * 插入代码，统计方法耗时
                                 */
                                MirrorHelper.injectStatistics(ctConstructor)
                            }
                        }
                        //getDeclaredMethods获取自己申明的方法，ctClass.getMethods()会把所有父类的方法都加上
                        if (ctClass.getDeclaredMethods() != null) {
                            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {

                                /**
                                 * 插入代码，统计主线程中是否有文件读写操作
                                 */
                                MirrorAnrDetect.detectFileOperate(ctMethod)

                                if (filePath.contains("\$")) {
                                    continue
                                }

                                /**
                                 * 插入代码，统计方法耗时
                                 */
                                int modifiers = ctMethod.getModifiers()
                                // 对于抽象方法直接跳过
                                if (Modifier.isAbstract(modifiers) || Modifier.isNative(modifiers)) {
                                    continue
                                }

                                String methodName = MirrorUtils.getSimpleName(ctMethod)
                                if (methodName.contains("\$")) {
                                    continue
                                }

                                MirrorHelper.injectStatistics(ctMethod)
                            }
                        }
                        ctClass.writeFile(inputDir)
                    }
                    //用完一定记得要卸载，否则pool里的永远是旧的代码
                    ctClass.detach()
                }
            }
        }
    }
}
